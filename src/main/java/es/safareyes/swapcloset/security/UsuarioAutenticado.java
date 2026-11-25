package es.safareyes.swapcloset.security;

import es.safareyes.swapcloset.Modelos.Rol;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/** Persona autenticada: es el 'principal' que reciben los controladores con @AuthenticationPrincipal. */
@Getter
public class UsuarioAutenticado implements UserDetails {

    private final Long usuarioId;
    private final Integer perfilId;
    private final String email;
    private final String password;
    private final Rol rol;

    public UsuarioAutenticado(Long usuarioId, Integer perfilId, String email, String password, Rol rol) {
        this.usuarioId = usuarioId;
        this.perfilId = perfilId;
        this.email = email;
        this.password = password;
        this.rol = rol;
    }

    /** ADMIN o MODERADOR. */
    public boolean esPersonalDeGestion() {
        return rol == Rol.ADMIN || rol == Rol.MODERADOR;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + rol.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
