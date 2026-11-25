package es.safareyes.swapcloset.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MensajeRequest(
        @NotBlank @Size(max = 1000) String contenido
) {
}
