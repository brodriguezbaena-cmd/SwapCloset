package es.safareyes.swapcloset.service;

import es.safareyes.swapcloset.Modelos.EstadoSolicitud;
import es.safareyes.swapcloset.dtos.request.Bandeja;
import es.safareyes.swapcloset.dtos.request.SolicitudCrearRequest;
import es.safareyes.swapcloset.dtos.response.SolicitudResponse;
import es.safareyes.swapcloset.security.UsuarioAutenticado;

import java.util.List;

public interface SolicitudService {

    SolicitudResponse crear(SolicitudCrearRequest request, UsuarioAutenticado actual);

    /** 'estado' puede ser null (todas). */
    List<SolicitudResponse> listar(Bandeja bandeja, EstadoSolicitud estado, UsuarioAutenticado actual);

    SolicitudResponse cambiarEstado(Integer solicitudId, EstadoSolicitud nuevoEstado, UsuarioAutenticado actual);
}
