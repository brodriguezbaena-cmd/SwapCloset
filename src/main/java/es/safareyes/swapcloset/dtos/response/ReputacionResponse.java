package es.safareyes.swapcloset.dtos.response;

/** Media (un decimal, null si no hay valoraciones) y número de valoraciones recibidas. */
public record ReputacionResponse(Double media, Long total) {
}
