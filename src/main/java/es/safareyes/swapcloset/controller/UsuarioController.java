package es.safareyes.swapcloset.controller;

import es.safareyes.swapcloset.dtos.request.PerfilActualizarRequest;
import es.safareyes.swapcloset.dtos.response.PerfilAfinResponse;
import es.safareyes.swapcloset.dtos.response.PerfilPublicoResponse;
import es.safareyes.swapcloset.security.UsuarioAutenticado;
import es.safareyes.swapcloset.service.PerfilService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** {id} es el id del perfil (el mismo que aparece como propietariaId, solicitanteId...). */
@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private static final int LIMITE_MAXIMO = 50;

    private final PerfilService perfilService;

    // 7. Perfil público (cualquier persona autenticada)
    @GetMapping("/{id}")
    public PerfilPublicoResponse perfil(@PathVariable Integer id) {
        return perfilService.obtener(id);
    }

    // 8. Editar mi perfil, tallas y estilos
    @PutMapping("/me")
    @PreAuthorize("hasRole('USUARIO')")
    public PerfilPublicoResponse actualizarMiPerfil(@Valid @RequestBody PerfilActualizarRequest request,
                                                    @AuthenticationPrincipal UsuarioAutenticado actual) {
        return perfilService.actualizarMiPerfil(request, actual);
    }

    // 9. Perfiles con más estilos en común y mejor reputación
    @GetMapping("/afines")
    @PreAuthorize("hasRole('USUARIO')")
    public List<PerfilAfinResponse> afines(@RequestParam(defaultValue = "10") int limite,
                                           @AuthenticationPrincipal UsuarioAutenticado actual) {
        return perfilService.afines(actual, Math.max(1, Math.min(limite, LIMITE_MAXIMO)));
    }
}
