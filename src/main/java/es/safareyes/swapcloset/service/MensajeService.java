package es.safareyes.swapcloset.service;

import es.safareyes.swapcloset.dtos.request.MensajeRequest;
import es.safareyes.swapcloset.dtos.response.MensajeResponse;
import es.safareyes.swapcloset.security.UsuarioAutenticado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MensajeService {

    MensajeResponse enviar(Integer solicitudId, MensajeRequest request, UsuarioAutenticado actual);

    Page<MensajeResponse> listar(Integer solicitudId, Pageable pageable, UsuarioAutenticado actual);
}
