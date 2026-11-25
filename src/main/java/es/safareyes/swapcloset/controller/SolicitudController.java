package es.safareyes.swapcloset.controller;

import es.safareyes.swapcloset.Modelos.EstadoSolicitud;
import es.safareyes.swapcloset.dtos.request.Bandeja;
import es.safareyes.swapcloset.dtos.request.CambioEstadoRequest;
import es.safareyes.swapcloset.dtos.request.MensajeRequest;
import es.safareyes.swapcloset.dtos.request.SolicitudCrearRequest;
import es.safareyes.swapcloset.dtos.request.ValoracionRequest;
import es.safareyes.swapcloset.dtos.response.MensajeResponse;
import es.safareyes.swapcloset.dtos.response.SolicitudResponse;
import es.safareyes.swapcloset.dtos.response.ValoracionResponse;
import es.safareyes.swapcloset.security.UsuarioAutenticado;
import es.safareyes.swapcloset.service.MensajeService;
import es.safareyes.swapcloset.service.SolicitudService;
import es.safareyes.swapcloset.service.ValoracionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/solicitudes")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USUARIO')")
public class SolicitudController {

    private final SolicitudService solicitudService;
    private final MensajeService mensajeService;
    private final ValoracionService valoracionService;

    // 10. Crear solicitud de intercambio o préstamo
    @PostMapping
    public ResponseEntity<SolicitudResponse> crear(@Valid @RequestBody SolicitudCrearRequest request,
                                                   @AuthenticationPrincipal UsuarioAutenticado actual) {
        SolicitudResponse creada = solicitudService.crear(request, actual);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creada.id()).toUri();
        return ResponseEntity.created(ubicacion).body(creada);
    }

    // 11. Mis solicitudes enviadas o recibidas, con filtro opcional por estado
    @GetMapping
    public List<SolicitudResponse> listar(@RequestParam(defaultValue = "ENVIADAS") Bandeja bandeja,
                                          @RequestParam(required = false) EstadoSolicitud estado,
                                          @AuthenticationPrincipal UsuarioAutenticado actual) {
        return solicitudService.listar(bandeja, estado, actual);
    }

    // 12. Aceptar, rechazar, cancelar o finalizar
    @PatchMapping("/{id}/estado")
    public SolicitudResponse cambiarEstado(@PathVariable Integer id,
                                           @Valid @RequestBody CambioEstadoRequest request,
                                           @AuthenticationPrincipal UsuarioAutenticado actual) {
        return solicitudService.cambiarEstado(id, request.estado(), actual);
    }

    // 13. Enviar mensaje
    @PostMapping("/{id}/mensajes")
    public ResponseEntity<MensajeResponse> enviarMensaje(@PathVariable Integer id,
                                                         @Valid @RequestBody MensajeRequest request,
                                                         @AuthenticationPrincipal UsuarioAutenticado actual) {
        MensajeResponse creado = mensajeService.enviar(id, request, actual);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
        return ResponseEntity.created(ubicacion).body(creado);
    }

    // 14. Conversación paginada (orden cronológico)
    @GetMapping("/{id}/mensajes")
    public PagedModel<MensajeResponse> mensajes(@PathVariable Integer id,
                                                @PageableDefault(size = 50) Pageable pageable,
                                                @AuthenticationPrincipal UsuarioAutenticado actual) {
        return new PagedModel<>(mensajeService.listar(id, pageable, actual));
    }

    // 15. Valorar a la otra persona
    @PostMapping("/{id}/valoraciones")
    public ResponseEntity<ValoracionResponse> valorar(@PathVariable Integer id,
                                                      @Valid @RequestBody ValoracionRequest request,
                                                      @AuthenticationPrincipal UsuarioAutenticado actual) {
        ValoracionResponse creada = valoracionService.crear(id, request, actual);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
        return ResponseEntity.created(ubicacion).body(creada);
    }
}
