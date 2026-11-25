package es.safareyes.swapcloset.service;

import es.safareyes.swapcloset.dtos.request.PerfilActualizarRequest;
import es.safareyes.swapcloset.dtos.response.PerfilAfinResponse;
import es.safareyes.swapcloset.dtos.response.PerfilPublicoResponse;
import es.safareyes.swapcloset.security.UsuarioAutenticado;

import java.util.List;

public interface PerfilService {

    PerfilPublicoResponse obtener(Integer perfilId);

    PerfilPublicoResponse actualizarMiPerfil(PerfilActualizarRequest request, UsuarioAutenticado actual);

    List<PerfilAfinResponse> afines(UsuarioAutenticado actual, int limite);
}
