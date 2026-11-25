package es.safareyes.swapcloset.service.impl;

import es.safareyes.swapcloset.Modelos.Solicitudes;
import es.safareyes.swapcloset.exception.PermisoDenegadoException;
import es.safareyes.swapcloset.exception.RecursoNoEncontradoException;
import es.safareyes.swapcloset.repositorios.ISolicitudRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** Regla de propiedad: una solicitud solo la ven y usan las dos personas implicadas. */
@Component
@RequiredArgsConstructor
class AccesoSolicitudes {

    private final ISolicitudRepositorio solicitudRepositorio;

    /** 404 si no existe, 403 si quien pregunta no es solicitante ni destinataria. */
    Solicitudes cargarComoParticipante(Integer solicitudId, Integer perfilId) {
        Solicitudes solicitud = solicitudRepositorio.findDetalleById(solicitudId)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la solicitud con id " + solicitudId));
        if (!solicitud.esParticipante(perfilId)) {
            throw new PermisoDenegadoException("No participas en esta solicitud");
        }
        return solicitud;
    }
}
