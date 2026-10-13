package es.safareyes.swapcloset.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SolicitudesPorEstado {
    private String estado;
    private Long solicitudes;
}
