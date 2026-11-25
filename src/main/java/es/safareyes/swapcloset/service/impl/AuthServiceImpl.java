package es.safareyes.swapcloset.service.impl;

import es.safareyes.swapcloset.Modelos.Perfil;
import es.safareyes.swapcloset.Modelos.Rol;
import es.safareyes.swapcloset.Modelos.Usuario;
import es.safareyes.swapcloset.dtos.request.LoginRequest;
import es.safareyes.swapcloset.dtos.request.RegistroRequest;
import es.safareyes.swapcloset.dtos.response.AuthResponse;
import es.safareyes.swapcloset.exception.ConflictoException;
import es.safareyes.swapcloset.mapper.PerfilMapper;
import es.safareyes.swapcloset.repositorios.IPerfilRepositorio;
import es.safareyes.swapcloset.repositorios.IUsuarioRepositorio;
import es.safareyes.swapcloset.security.JwtService;
import es.safareyes.swapcloset.security.UsuarioAutenticado;
import es.safareyes.swapcloset.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final IPerfilRepositorio perfilRepositorio;
    private final IUsuarioRepositorio usuarioRepositorio;
    private final ResolvedorEstilos resolvedorEstilos;
    private final PerfilMapper perfilMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    @Transactional
    public AuthResponse registrar(RegistroRequest request) {
        String email = request.email().trim().toLowerCase();
        // RN-01: email y nombre de usuario únicos (409)
        if (perfilRepositorio.existsByEmailIgnoreCase(email)) {
            throw new ConflictoException("Ya existe una cuenta con ese email");
        }
        if (perfilRepositorio.existsByNombreUsuarioIgnoreCase(request.nombreUsuario())) {
            throw new ConflictoException("El nombre de usuario ya está en uso");
        }

        Perfil perfil = perfilMapper.toEntity(request);
        perfil.setEmail(email);
        perfil.setEstilos(resolvedorEstilos.resolver(request.estilosIds()));
        perfilRepositorio.save(perfil);

        Usuario usuario = new Usuario();
        usuario.setContrasena(passwordEncoder.encode(request.password()));
        usuario.setRol(Rol.USUARIO);
        usuario.setPerfil(perfil);
        usuarioRepositorio.save(usuario);

        log.info("Nuevo registro: perfil {} ({})", perfil.getId(), perfil.getNombreUsuario());
        return respuesta(new UsuarioAutenticado(usuario.getId(), perfil.getId(), email, null, Rol.USUARIO));
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        // Credenciales incorrectas -> AuthenticationException -> 401 en el @RestControllerAdvice
        Authentication autenticacion = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email().trim(), request.password()));
        UsuarioAutenticado usuario = (UsuarioAutenticado) autenticacion.getPrincipal();
        log.info("Login correcto: perfil {}", usuario.getPerfilId());
        return respuesta(usuario);
    }

    private AuthResponse respuesta(UsuarioAutenticado usuario) {
        return new AuthResponse(jwtService.generar(usuario), "Bearer", jwtService.getExpiracionSegundos(),
                usuario.getPerfilId(), usuario.getRol());
    }
}
