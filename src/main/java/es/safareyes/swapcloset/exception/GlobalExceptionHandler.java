package es.safareyes.swapcloset.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.core.PropertyReferenceException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

/**
 * Único punto de salida de errores: todos con el formato ProblemDetail (RFC 9457).
 * Extiende ResponseEntityExceptionHandler para que los errores propios de Spring MVC
 * (405, 415, parámetros que faltan, JSON ilegible...) salgan también como ProblemDetail.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final ZoneId MADRID = ZoneId.of("Europe/Madrid");

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ProblemDetail noEncontrado(RecursoNoEncontradoException ex, HttpServletRequest req) {
        return problema(HttpStatus.NOT_FOUND, "Recurso no encontrado", ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public ProblemDetail reglaNegocio(ReglaNegocioException ex, HttpServletRequest req) {
        return problema(HttpStatus.UNPROCESSABLE_ENTITY, "Regla de negocio incumplida", ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(ConflictoException.class)
    public ProblemDetail conflicto(ConflictoException ex, HttpServletRequest req) {
        return problema(HttpStatus.CONFLICT, "Conflicto", ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(PermisoDenegadoException.class)
    public ProblemDetail permisoDenegado(PermisoDenegadoException ex, HttpServletRequest req) {
        return problema(HttpStatus.FORBIDDEN, "Acceso denegado", ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(PeticionNoValidaException.class)
    public ProblemDetail peticionNoValida(PeticionNoValidaException ex, HttpServletRequest req) {
        return problema(HttpStatus.BAD_REQUEST, "Petición no válida", ex.getMessage(), req.getRequestURI());
    }

    // @PreAuthorize y reglas de ruta que se evalúan dentro del controlador
    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail accesoDenegado(AccessDeniedException ex, HttpServletRequest req) {
        return problema(HttpStatus.FORBIDDEN, "Acceso denegado", "No tienes permisos para esta operación", req.getRequestURI());
    }

    // Credenciales incorrectas en el login
    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail noAutenticado(AuthenticationException ex, HttpServletRequest req) {
        return problema(HttpStatus.UNAUTHORIZED, "No autenticado", "Credenciales incorrectas", req.getRequestURI());
    }

    // ?sort= con una propiedad que no existe
    @ExceptionHandler(PropertyReferenceException.class)
    public ProblemDetail propiedadInvalida(PropertyReferenceException ex, HttpServletRequest req) {
        return problema(HttpStatus.BAD_REQUEST, "Petición no válida", "Criterio de ordenación no válido: " + ex.getPropertyName(), req.getRequestURI());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail integridad(DataIntegrityViolationException ex, HttpServletRequest req) {
        log.warn("Violación de integridad en {}: {}", req.getRequestURI(), ex.getMostSpecificCause().getMessage());
        return problema(HttpStatus.CONFLICT, "Conflicto", "La operación entra en conflicto con datos existentes", req.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail inesperado(Exception ex, HttpServletRequest req) {
        log.error("Error no controlado en {}", req.getRequestURI(), ex);
        return problema(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno", "Se ha producido un error inesperado", req.getRequestURI());
    }

    // 400 con la lista de campos erróneos
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        List<Map<String, String>> errores = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> Map.of("campo", e.getField(),
                        "mensaje", e.getDefaultMessage() == null ? "no válido" : e.getDefaultMessage()))
                .toList();

        String uri = request instanceof ServletWebRequest swr ? swr.getRequest().getRequestURI() : "";
        ProblemDetail pd = problema(HttpStatus.BAD_REQUEST, "Validación fallida",
                "Hay campos con valores no válidos", uri);
        pd.setProperty("errores", errores);
        return ResponseEntity.badRequest().body(pd);
    }

    public static ProblemDetail problema(HttpStatus status, String titulo, String detalle, String instancia) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detalle);
        pd.setTitle(titulo);
        if (instancia != null && !instancia.isEmpty()) {
            pd.setInstance(URI.create(instancia));
        }
        pd.setProperty("timestamp", LocalDateTime.now(MADRID).truncatedTo(ChronoUnit.SECONDS).toString());
        return pd;
    }
}
