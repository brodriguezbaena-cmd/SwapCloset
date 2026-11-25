package es.safareyes.swapcloset.dtos.response;

import es.safareyes.swapcloset.Modelos.EstadoSolicitud;
import es.safareyes.swapcloset.Modelos.TipoSolicitud;

import java.time.LocalDate;

public record SolicitudResponse(
        Integer id,
        TipoSolicitud tipo,
        EstadoSolicitud estado,
        Integer prendaId,
        String prendaTitulo,
        Integer prendaOfrecidaId,
        String prendaOfrecidaTitulo,
        Integer solicitanteId,
        String solicitanteNombreUsuario,
        Integer propietariaId,
        String propietariaNombreUsuario,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        LocalDate fechaCreacion,
        LocalDate fechaRespuesta
) {
}
