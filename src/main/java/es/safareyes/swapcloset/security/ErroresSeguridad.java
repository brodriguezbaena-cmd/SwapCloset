package es.safareyes.swapcloset.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

/**
 * 401 y 403 que se producen en los filtros de Security (antes de llegar a un controlador),
 * con el mismo formato ProblemDetail que el resto de errores.
 */
@Component
public class ErroresSeguridad implements AuthenticationEntryPoint, AccessDeniedHandler {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         org.springframework.security.core.AuthenticationException ex) throws IOException {
        escribir(request, response, HttpStatus.UNAUTHORIZED, "No autenticado",
                "Falta el token o no es válido, o ha caducado");
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       org.springframework.security.access.AccessDeniedException ex) throws IOException {
        escribir(request, response, HttpStatus.FORBIDDEN, "Acceso denegado",
                "No tienes permisos para esta operación");
    }

    private void escribir(HttpServletRequest request, HttpServletResponse response, HttpStatus status,
                          String titulo, String detalle) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        String json = "{\"type\":\"about:blank\",\"title\":\"" + escapar(titulo) + "\",\"status\":" + status.value()
                + ",\"detail\":\"" + escapar(detalle) + "\",\"instance\":\"" + escapar(request.getRequestURI())
                + "\",\"timestamp\":\""
                + LocalDateTime.now(ZoneId.of("Europe/Madrid")).truncatedTo(ChronoUnit.SECONDS) + "\"}";
        response.getWriter().write(json);
    }

    private String escapar(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
