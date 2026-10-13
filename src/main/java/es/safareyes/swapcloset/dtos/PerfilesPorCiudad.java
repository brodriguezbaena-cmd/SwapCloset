package es.safareyes.swapcloset.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PerfilesPorCiudad {
    private String ciudad;
    private Long perfiles;
}
