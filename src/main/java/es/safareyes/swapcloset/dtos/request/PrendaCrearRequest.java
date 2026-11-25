package es.safareyes.swapcloset.dtos.request;

import jakarta.validation.constraints.*;

public record PrendaCrearRequest(
        @NotBlank @Size(max = 100) String titulo,
        @Size(max = 1000) String descripcion,
        @Size(max = 100) String marca,
        @NotNull @Positive Integer categoriaId,
        @NotBlank @Size(max = 20) String talla,

        @NotBlank
        @Pattern(regexp = "Nueva con etiqueta|Como nueva|Buen estado|Usado",
                message = "Estado válido: Nueva con etiqueta, Como nueva, Buen estado o Usado")
        String estado,

        @NotNull Boolean permitePrestamo,
        @NotNull Boolean permiteIntercambio
) {

    @AssertTrue(message = "La prenda debe admitir préstamo, intercambio o ambos")
    public boolean isModalidadValida() {
        return Boolean.TRUE.equals(permitePrestamo) || Boolean.TRUE.equals(permiteIntercambio);
    }
}
