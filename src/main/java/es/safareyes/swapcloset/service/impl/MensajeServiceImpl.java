package es.safareyes.swapcloset.service.impl;

import es.safareyes.swapcloset.Modelos.EstadoSolicitud;
import es.safareyes.swapcloset.Modelos.Mensaje;
import es.safareyes.swapcloset.Modelos.Solicitudes;
import es.safareyes.swapcloset.dtos.request.MensajeRequest;
import es.safareyes.swapcloset.dtos.response.MensajeResponse;
import es.safareyes.swapcloset.exception.ReglaNegocioException;
import es.safareyes.swapcloset.mapper.MensajeMapper;
import es.safareyes.swapcloset.repositorios.IMensajeRepositorio;
import es.safareyes.swapcloset.repositorios.IPerfilRepositorio;
import es.safareyes.swapcloset.security.UsuarioAutenticado;
import es.safareyes.swapcloset.service.MensajeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class MensajeServiceImpl implements MensajeService {

    // RN-08: no se escribe en solicitudes cerradas sin acuerdo
    private static final Set<EstadoSolicitud> SIN_CHAT = Set.of(
            EstadoSolicitud.RECHAZADA, EstadoSolicitud.CANCELADA, EstadoSolicitud.CADUCADA);

    private final IMensajeRepositorio mensajeRepositorio;
    private final IPerfilRepositorio perfilRepositorio;
    private final AccesoSolicitudes accesoSolicitudes;
    private final MensajeMapper mensajeMapper;
    private final Clock clock;

    // ------------------------------------------------------------------ endpoint 13

    @Override
    @Transactional
    public MensajeResponse enviar(Integer solicitudId, MensajeRequest request, UsuarioAutenticado actual) {
        Solicitudes solicitud = accesoSolicitudes.cargarComoParticipante(solicitudId, actual.getPerfilId());
        if (SIN_CHAT.contains(solicitud.getEstado())) {
            throw new ReglaNegocioException("No se puede escribir en una solicitud " + solicitud.getEstado());
        }

        Mensaje mensaje = new Mensaje();
        mensaje.setContenido(request.contenido().trim());
        mensaje.setFechaEnvio(LocalDate.now(clock));
        mensaje.setHoraEnvio(LocalTime.now(clock).truncatedTo(ChronoUnit.SECONDS));
        mensaje.setSolicitud(solicitud);
        mensaje.setAutor(perfilRepositorio.getReferenceById(actual.getPerfilId()));
        mensajeRepositorio.save(mensaje);

        log.info("Mensaje {} en la solicitud {} del perfil {}", mensaje.getId(), solicitudId, actual.getPerfilId());
        return mensajeMapper.toResponse(mensaje);
    }

    // ------------------------------------------------------------------ endpoint 14

    @Override
    @Transactional(readOnly = true)
    public Page<MensajeResponse> listar(Integer solicitudId, Pageable pageable, UsuarioAutenticado actual) {
        accesoSolicitudes.cargarComoParticipante(solicitudId, actual.getPerfilId());
        return mensajeRepositorio
                .findBySolicitudIdOrderByFechaEnvioAscHoraEnvioAscIdAsc(solicitudId, pageable)
                .map(mensajeMapper::toResponse);
    }
}
