package es.safareyes.swapcloset.security;

import es.safareyes.swapcloset.Modelos.Rol;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.util.Date;
import java.util.Optional;

@Service
public class JwtService {

    private final SecretKey clave;
    private final long expiracionMinutos;
    private final Clock clock;

    public JwtService(@Value("${app.jwt.secret}") String secreto,
                      @Value("${app.jwt.expiracion-minutos:60}") long expiracionMinutos,
                      Clock clock) {
        this.clave = Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
        this.expiracionMinutos = expiracionMinutos;
        this.clock = clock;
    }

    public String generar(UsuarioAutenticado u) {
        Date ahora = Date.from(clock.instant());
        Date caduca = new Date(ahora.getTime() + expiracionMinutos * 60_000);
        return Jwts.builder()
                .subject(String.valueOf(u.getUsuarioId()))
                .claim("perfilId", u.getPerfilId())
                .claim("email", u.getEmail())
                .claim("rol", u.getRol().name())
                .issuedAt(ahora)
                .expiration(caduca)
                .signWith(clave)
                .compact();
    }

    public long getExpiracionSegundos() {
        return expiracionMinutos * 60;
    }

    /** Devuelve la persona del token, o vacío si el token no es válido o ha caducado. */
    public Optional<UsuarioAutenticado> leer(String token) {
        try {
            Claims c = Jwts.parser().verifyWith(clave).build().parseSignedClaims(token).getPayload();
            return Optional.of(new UsuarioAutenticado(
                    Long.valueOf(c.getSubject()),
                    c.get("perfilId", Integer.class),
                    c.get("email", String.class),
                    null,
                    Rol.valueOf(c.get("rol", String.class))));
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
