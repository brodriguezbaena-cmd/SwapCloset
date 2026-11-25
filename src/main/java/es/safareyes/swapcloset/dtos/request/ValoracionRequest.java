package es.safareyes.swapcloset.dtos.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ValoracionRequest(
        @NotNull @Min(1) @Max(5) Integer puntuacion,
        @Size(max = 500) String comentario
) {
}
