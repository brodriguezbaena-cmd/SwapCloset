package es.safareyes.swapcloset.service;

import es.safareyes.swapcloset.dtos.request.ValoracionRequest;
import es.safareyes.swapcloset.dtos.response.ValoracionResponse;
import es.safareyes.swapcloset.security.UsuarioAutenticado;

public interface ValoracionService {

    ValoracionResponse crear(Integer solicitudId, ValoracionRequest request, UsuarioAutenticado actual);
}
