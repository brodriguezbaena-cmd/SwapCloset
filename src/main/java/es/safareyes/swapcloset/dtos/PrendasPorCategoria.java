package es.safareyes.swapcloset.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PrendasPorCategoria {
    private String categoria;
    private Long prendas;
}
