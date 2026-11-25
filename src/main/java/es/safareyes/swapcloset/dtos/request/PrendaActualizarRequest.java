package es.safareyes.swapcloset.dtos.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/** Actualización parcial: los campos a null no se modifican. */
public record PrendaActualizarRequest(
        @Size(min = 1, max = 100) String titulo,
        @Size(max = 1000) String descripcion,
        @Size(max = 100) String marca,
        @Positive Integer categoriaId,
        @Size(min = 1, max = 20) String talla,

        @Pattern(regexp = "Nueva con etiqueta|Como nueva|Buen estado|Usado",
                message = "Estado válido: Nueva con etiqueta, Como nueva, Buen estado o Usado")
        String estado,

        Boolean permitePrestamo,
        Boolean permiteIntercambio
) {
}
