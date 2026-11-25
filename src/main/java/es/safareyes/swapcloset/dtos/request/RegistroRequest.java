package es.safareyes.swapcloset.dtos.request;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.List;

public record RegistroRequest(
        @NotBlank @Size(min = 3, max = 30)
        @Pattern(regexp = "[A-Za-z0-9_.]+", message = "Solo letras, números, punto y guion bajo")
        String nombreUsuario,

        @NotBlank @Email @Size(max = 100)
        String email,

        @NotBlank @Size(min = 8, max = 72)
        String password,

        @NotBlank @Size(max = 100) String nombre,
        @NotBlank @Size(max = 100) String apellido1,
        @Size(max = 100) String apellido2,

        @NotNull @Past @MayorDeEdad(minimo = 16)
        LocalDate fechaNacimiento,

        @NotBlank @Size(max = 100) String ciudad,
        @Size(max = 500) String biografia,

        @NotBlank @Pattern(regexp = "XS|S|M|L|XL|XXL", message = "Talla de camiseta válida: XS, S, M, L, XL o XXL")
        String tallaCamiseta,

        @NotBlank @Pattern(regexp = "3[4-9]|4[0-9]|50", message = "Talla de pantalón válida: 34 a 50")
        String tallaPantalon,

        @NotBlank @Pattern(regexp = "3[5-9]|4[0-6]", message = "Talla de calzado válida: 35 a 46")
        String tallaZapatos,

        @NotEmpty @Size(max = 5, message = "Como máximo 5 estilos")
        List<@NotNull Integer> estilosIds
) {
}
