package es.safareyes.swapcloset.dtos.response;

public record PerfilAfinResponse(
        Integer id,
        String nombreUsuario,
        String ciudad,
        Long estilosComunes,
        ReputacionResponse reputacion
) {
}
