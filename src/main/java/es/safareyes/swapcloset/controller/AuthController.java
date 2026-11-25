package es.safareyes.swapcloset.controller;

import es.safareyes.swapcloset.dtos.request.LoginRequest;
import es.safareyes.swapcloset.dtos.request.RegistroRequest;
import es.safareyes.swapcloset.dtos.response.AuthResponse;
import es.safareyes.swapcloset.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/registro")
    public ResponseEntity<AuthResponse> registro(@Valid @RequestBody RegistroRequest request) {
        AuthResponse respuesta = authService.registrar(request);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/usuarios/{id}").buildAndExpand(respuesta.perfilId()).toUri();
        return ResponseEntity.created(ubicacion).body(respuesta);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
