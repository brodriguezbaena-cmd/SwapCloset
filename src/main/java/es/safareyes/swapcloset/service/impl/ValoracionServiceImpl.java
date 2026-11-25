package es.safareyes.swapcloset.service.impl;

import es.safareyes.swapcloset.Modelos.EstadoSolicitud;
import es.safareyes.swapcloset.Modelos.Perfil;
import es.safareyes.swapcloset.Modelos.Solicitudes;
import es.safareyes.swapcloset.Modelos.Valoracion;
import es.safareyes.swapcloset.dtos.request.ValoracionRequest;
import es.safareyes.swapcloset.dtos.response.ValoracionResponse;
import es.safareyes.swapcloset.exception.ConflictoException;
import es.safareyes.swapcloset.exception.ReglaNegocioException;
import es.safareyes.swapcloset.mapper.ValoracionMapper;
import es.safareyes.swapcloset.repositorios.IPerfilRepositorio;
import es.safareyes.swapcloset.repositorios.IValoracionRepositorio;
import es.safareyes.swapcloset.security.UsuarioAutenticado;
import es.safareyes.swapcloset.service.ValoracionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class ValoracionServiceImpl implements ValoracionService {

    private final IValoracionRepositorio valoracionRepositorio;
    private final IPerfilRepositorio perfilRepositorio;
    private final AccesoSolicitudes accesoSolicitudes;
    private final ValoracionMapper valoracionMapper;
    private final Clock clock;

    // ------------------------------------------------------------------ endpoint 15

    @Override
    @Transactional
    public ValoracionResponse crear(Integer solicitudId, ValoracionRequest request, UsuarioAutenticado actual) {
        Integer yo = actual.getPerfilId();
        Solicitudes solicitud = accesoSolicitudes.cargarComoParticipante(solicitudId, yo);

        // RN-09: solo en solicitudes FINALIZADAS, una por participante
        if (solicitud.getEstado() != EstadoSolicitud.FINALIZADA) {
            throw new ReglaNegocioException("Solo se puede valorar una solicitud FINALIZADA");
        }
        if (valoracionRepositorio.existsBySolicitudIdAndAutorId(solicitudId, yo)) {
            throw new ConflictoException("Ya has valorado esta solicitud");
        }

        Perfil otra = solicitud.esSolicitante(yo) ? solicitud.getDestinatario() : solicitud.getSolicitante();

        Valoracion valoracion = new Valoracion();
        valoracion.setSolicitud(solicitud);
        valoracion.setAutor(perfilRepositorio.getReferenceById(yo));
        valoracion.setValorado(otra);
        valoracion.setPuntuacion(request.puntuacion());
        valoracion.setComentario(request.comentario() == null || request.comentario().isBlank()
                ? null : request.comentario().trim());
        valoracion.setFecha(LocalDate.now(clock));
        valoracionRepositorio.save(valoracion);

        log.info("Valoración {} del perfil {} al perfil {} (solicitud {})",
                valoracion.getId(), yo, otra.getId(), solicitudId);
        return valoracionMapper.toResponse(valoracion);
    }
}
