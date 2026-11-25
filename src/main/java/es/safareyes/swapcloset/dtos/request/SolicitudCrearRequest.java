package es.safareyes.swapcloset.dtos.request;

import es.safareyes.swapcloset.Modelos.TipoSolicitud;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record SolicitudCrearRequest(
        @NotNull @Positive Integer prendaId,
        @NotNull TipoSolicitud tipo,
        // Solo en intercambios
        @Positive Integer prendaOfrecidaId,
        // Solo en préstamos
        LocalDate fechaInicio,
        LocalDate fechaFin
) {
}
