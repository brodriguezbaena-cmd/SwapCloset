package es.safareyes.swapcloset.controller;

import es.safareyes.swapcloset.Modelos.TipoSolicitud;
import es.safareyes.swapcloset.dtos.request.PrendaActualizarRequest;
import es.safareyes.swapcloset.dtos.request.PrendaCrearRequest;
import es.safareyes.swapcloset.dtos.response.PrendaDetalleResponse;
import es.safareyes.swapcloset.dtos.response.PrendaTarjetaResponse;
import es.safareyes.swapcloset.security.UsuarioAutenticado;
import es.safareyes.swapcloset.service.PrendaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedModel;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/prendas")
@RequiredArgsConstructor
public class PrendaController {

    private final PrendaService prendaService;

    // 1. Búsqueda paginada con filtros (público). ?page=&size=&sort= ; por defecto, las más recientes primero
    @GetMapping
    public PagedModel<PrendaTarjetaResponse> buscar(
            @RequestParam(required = false) Integer categoriaId,
            @RequestParam(required = false) String talla,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Integer estiloId,
            @RequestParam(required = false) String ciudad,
            @RequestParam(required = false) TipoSolicitud oferta,
            @PageableDefault(size = 20, sort = {"fechaPublicacion", "id"}, direction = Sort.Direction.DESC)
            Pageable pageable) {
        return new PagedModel<>(prendaService.buscar(categoriaId, talla, estado, estiloId, ciudad, oferta, pageable));
    }

    // 2. Detalle con fotos y propietaria con su reputación (público)
    @GetMapping("/{id}")
    public PrendaDetalleResponse detalle(@PathVariable Integer id,
                                         @AuthenticationPrincipal UsuarioAutenticado actual) {
        return prendaService.detalle(id, actual);
    }

    // 3. Publicar prenda (queda en BORRADOR)
    @PostMapping
    @PreAuthorize("hasRole('USUARIO')")
    public ResponseEntity<PrendaDetalleResponse> crear(@Valid @RequestBody PrendaCrearRequest request,
                                                       @AuthenticationPrincipal UsuarioAutenticado actual) {
        PrendaDetalleResponse creada = prendaService.crear(request, actual);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creada.id()).toUri();
        return ResponseEntity.created(ubicacion).body(creada);
    }

    // 4. Modificar prenda (solo la propietaria)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USUARIO')")
    public PrendaDetalleResponse actualizar(@PathVariable Integer id,
                                            @Valid @RequestBody PrendaActualizarRequest request,
                                            @AuthenticationPrincipal UsuarioAutenticado actual) {
        return prendaService.actualizar(id, request, actual);
    }

    // 5. Retirar prenda (baja lógica): la propietaria o el equipo de moderación
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USUARIO', 'MODERADOR', 'ADMIN')")
    public ResponseEntity<Void> retirar(@PathVariable Integer id,
                                        @AuthenticationPrincipal UsuarioAutenticado actual) {
        prendaService.retirar(id, actual);
        return ResponseEntity.noContent().build();
    }
}
