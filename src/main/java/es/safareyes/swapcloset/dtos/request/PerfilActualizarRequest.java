package es.safareyes.swapcloset.dtos.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

/** Todos los campos son opcionales: solo se modifican los que llegan informados. */
public record PerfilActualizarRequest(
        @Size(min = 1, max = 100) String nombre,
        @Size(min = 1, max = 100) String apellido1,
        @Size(max = 100) String apellido2,
        @Size(min = 1, max = 100) String ciudad,
        @Size(max = 500) String biografia,

        @Pattern(regexp = "XS|S|M|L|XL|XXL", message = "Talla de camiseta válida: XS, S, M, L, XL o XXL")
        String tallaCamiseta,

        @Pattern(regexp = "3[4-9]|4[0-9]|50", message = "Talla de pantalón válida: 34 a 50")
        String tallaPantalon,

        @Pattern(regexp = "3[5-9]|4[0-6]", message = "Talla de calzado válida: 35 a 46")
        String tallaZapatos,

        @Size(min = 1, max = 5, message = "Entre 1 y 5 estilos")
        List<@NotNull Integer> estilosIds
) {
}
