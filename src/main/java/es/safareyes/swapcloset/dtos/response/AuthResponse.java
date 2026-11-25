package es.safareyes.swapcloset.dtos.response;

import es.safareyes.swapcloset.Modelos.Rol;

public record AuthResponse(String token, String tipo, long expiraEnSegundos, Integer perfilId, Rol rol) {
}
