package es.safareyes.swapcloset.dtos.response;

import java.util.List;

/** Perfil público: sin email, apellidos ni fecha de nacimiento. */
public record PerfilPublicoResponse(
        Integer id,
        String nombreUsuario,
        String nombre,
        String ciudad,
        String biografia,
        String tallaCamiseta,
        String tallaPantalon,
        String tallaZapatos,
        List<String> estilos,
        ReputacionResponse reputacion,
        List<PrendaTarjetaResponse> prendas
) {

    public PerfilPublicoResponse conReputacionYPrendas(ReputacionResponse nuevaReputacion,
                                                       List<PrendaTarjetaResponse> nuevasPrendas) {
        return new PerfilPublicoResponse(id, nombreUsuario, nombre, ciudad, biografia, tallaCamiseta,
                tallaPantalon, tallaZapatos, estilos, nuevaReputacion, nuevasPrendas);
    }
}
