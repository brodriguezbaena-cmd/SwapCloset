package es.safareyes.swapcloset.dtos.response;

import java.time.LocalDate;

public record ValoracionResponse(
        Integer id,
        Integer solicitudId,
        Integer autorId,
        Integer valoradoId,
        Integer puntuacion,
        String comentario,
        LocalDate fecha
) {
}
