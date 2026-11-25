package es.safareyes.swapcloset.dtos.response;

import java.time.LocalDateTime;

public record MensajeResponse(
        Integer id,
        Integer autorId,
        String autorNombreUsuario,
        String contenido,
        LocalDateTime enviadoEn
) {
}
