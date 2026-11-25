package es.safareyes.swapcloset.dtos.response;

import java.time.LocalDate;

/** Versión resumida de una prenda para listados. */
public record PrendaTarjetaResponse(
        Integer id,
        String titulo,
        String marca,
        String talla,
        String estado,
        String categoria,
        String ciudad,
        Boolean permitePrestamo,
        Boolean permiteIntercambio,
        LocalDate fechaPublicacion,
        Integer fotoPortadaId,
        Integer propietariaId,
        String propietariaNombreUsuario
) {
}
