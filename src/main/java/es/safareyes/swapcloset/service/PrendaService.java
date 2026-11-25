package es.safareyes.swapcloset.service;

import es.safareyes.swapcloset.Modelos.TipoSolicitud;
import es.safareyes.swapcloset.dtos.request.PrendaActualizarRequest;
import es.safareyes.swapcloset.dtos.request.PrendaCrearRequest;
import es.safareyes.swapcloset.dtos.response.PrendaDetalleResponse;
import es.safareyes.swapcloset.dtos.response.PrendaTarjetaResponse;
import es.safareyes.swapcloset.security.UsuarioAutenticado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PrendaService {

    Page<PrendaTarjetaResponse> buscar(Integer categoriaId, String talla, String estado, Integer estiloId,
                                       String ciudad, TipoSolicitud oferta, Pageable pageable);

    /** 'actual' es null si la petición es anónima. */
    PrendaDetalleResponse detalle(Integer id, UsuarioAutenticado actual);

    PrendaDetalleResponse crear(PrendaCrearRequest request, UsuarioAutenticado actual);

    PrendaDetalleResponse actualizar(Integer id, PrendaActualizarRequest request, UsuarioAutenticado actual);

    void retirar(Integer id, UsuarioAutenticado actual);
}
