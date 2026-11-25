package es.safareyes.swapcloset.dtos.response;

/** Nunca expone la ruta del fichero en disco: solo la URL pública de descarga. */
public record FotoResponse(Integer id, Integer orden, String url) {
}
