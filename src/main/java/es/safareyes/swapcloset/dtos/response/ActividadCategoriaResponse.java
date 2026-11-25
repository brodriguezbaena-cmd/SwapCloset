package es.safareyes.swapcloset.dtos.response;

public record ActividadCategoriaResponse(
        String mes,
        String categoria,
        Long publicaciones,
        Long intercambios,
        Long prestamos
) {
}
