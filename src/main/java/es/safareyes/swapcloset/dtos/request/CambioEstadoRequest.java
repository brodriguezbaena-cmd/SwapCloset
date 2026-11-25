package es.safareyes.swapcloset.dtos.request;

import es.safareyes.swapcloset.Modelos.EstadoSolicitud;
import jakarta.validation.constraints.NotNull;

public record CambioEstadoRequest(@NotNull EstadoSolicitud estado) {
}
