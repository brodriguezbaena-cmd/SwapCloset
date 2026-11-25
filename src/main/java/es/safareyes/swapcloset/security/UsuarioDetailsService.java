package es.safareyes.swapcloset.security;

import es.safareyes.swapcloset.Modelos.Usuario;
import es.safareyes.swapcloset.repositorios.IUsuarioRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioDetailsService implements UserDetailsService {

    private final IUsuarioRepositorio usuarioRepositorio;

    /** El 'username' del login es el email del perfil. */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario u = usuarioRepositorio.buscarConPerfilPorEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Credenciales incorrectas"));
        return new UsuarioAutenticado(u.getId(), u.getPerfil().getId(), u.getPerfil().getEmail(),
                u.getContrasena(), u.getRol());
    }
}
