package es.safareyes.swapcloset.dtos.response;

import es.safareyes.swapcloset.Modelos.EstadoPrenda;

import java.time.LocalDate;
import java.util.List;

public record PrendaDetalleResponse(
        Integer id,
        String titulo,
        String descripcion,
        String marca,
        String estado,
        String talla,
        EstadoPrenda situacion,
        Boolean permitePrestamo,
        Boolean permiteIntercambio,
        LocalDate fechaPublicacion,
        Integer categoriaId,
        String categoria,
        List<FotoResponse> fotos,
        PropietariaResponse propietaria
) {

    /** La propietaria y su reputación las calcula el servicio, no el mapper. */
    public PrendaDetalleResponse conPropietaria(PropietariaResponse nueva) {
        return new PrendaDetalleResponse(id, titulo, descripcion, marca, estado, talla, situacion,
                permitePrestamo, permiteIntercambio, fechaPublicacion, categoriaId, categoria, fotos, nueva);
    }
}
