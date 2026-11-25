package es.safareyes.swapcloset.service.impl;

import es.safareyes.swapcloset.Modelos.Categoria;
import es.safareyes.swapcloset.Modelos.EstadoPrenda;
import es.safareyes.swapcloset.Modelos.Perfil;
import es.safareyes.swapcloset.Modelos.Prenda;
import es.safareyes.swapcloset.Modelos.TipoSolicitud;
import es.safareyes.swapcloset.dtos.request.PrendaActualizarRequest;
import es.safareyes.swapcloset.dtos.request.PrendaCrearRequest;
import es.safareyes.swapcloset.dtos.response.PrendaDetalleResponse;
import es.safareyes.swapcloset.dtos.response.PrendaTarjetaResponse;
import es.safareyes.swapcloset.dtos.response.PropietariaResponse;
import es.safareyes.swapcloset.exception.PermisoDenegadoException;
import es.safareyes.swapcloset.exception.RecursoNoEncontradoException;
import es.safareyes.swapcloset.exception.ReglaNegocioException;
import es.safareyes.swapcloset.mapper.PrendaMapper;
import es.safareyes.swapcloset.repositorios.ICategoriaRepositorio;
import es.safareyes.swapcloset.repositorios.IPerfilRepositorio;
import es.safareyes.swapcloset.repositorios.IPrendaRepositorio;
import es.safareyes.swapcloset.repositorios.ISolicitudRepositorio;
import es.safareyes.swapcloset.repositorios.PrendaSpecs;
import es.safareyes.swapcloset.security.UsuarioAutenticado;
import es.safareyes.swapcloset.service.PrendaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrendaServiceImpl implements PrendaService {

    private final IPrendaRepositorio prendaRepositorio;
    private final ICategoriaRepositorio categoriaRepositorio;
    private final IPerfilRepositorio perfilRepositorio;
    private final ISolicitudRepositorio solicitudRepositorio;
    private final PrendaMapper prendaMapper;
    private final Reputaciones reputaciones;
    private final Clock clock;

    // ------------------------------------------------------------------ endpoint 1

    @Override
    @Transactional(readOnly = true)
    public Page<PrendaTarjetaResponse> buscar(Integer categoriaId, String talla, String estado, Integer estiloId,
                                              String ciudad, TipoSolicitud oferta, Pageable pageable) {
        return prendaRepositorio
                .findAll(PrendaSpecs.filtrar(categoriaId, talla, estado, estiloId, ciudad, oferta), pageable)
                .map(prendaMapper::toTarjeta);
    }

    // ------------------------------------------------------------------ endpoint 2

    @Override
    @Transactional(readOnly = true)
    public PrendaDetalleResponse detalle(Integer id, UsuarioAutenticado actual) {
        Prenda prenda = prendaRepositorio.findDetalleById(id).orElseThrow(() -> noExiste(id));

        boolean publica = prenda.getSituacion() != EstadoPrenda.BORRADOR
                && prenda.getSituacion() != EstadoPrenda.RETIRADA;
        if (!publica) {
            // Borradores y retiradas: solo su propietaria o el equipo de gestión; para el resto no existen
            boolean autorizado = actual != null
                    && (actual.esPersonalDeGestion() || prenda.getPerfil().getId().equals(actual.getPerfilId()));
            if (!autorizado) {
                throw noExiste(id);
            }
        }
        return conPropietaria(prenda);
    }

    // ------------------------------------------------------------------ endpoint 3

    @Override
    @Transactional
    public PrendaDetalleResponse crear(PrendaCrearRequest request, UsuarioAutenticado actual) {
        Categoria categoria = categoriaRepositorio.findById(request.categoriaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la categoría con id " + request.categoriaId()));
        Perfil propietaria = perfilRepositorio.findById(actual.getPerfilId())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el perfil con id " + actual.getPerfilId()));

        Prenda prenda = prendaMapper.toEntity(request);
        prenda.setCategoria(categoria);
        prenda.setPerfil(propietaria);
        prenda.setFechaPublicacion(LocalDate.now(clock));
        prenda.setSituacion(EstadoPrenda.BORRADOR); // RN-03: pasa a DISPONIBLE al subir la primera foto
        prendaRepositorio.save(prenda);

        log.info("Prenda {} creada en BORRADOR por el perfil {}", prenda.getId(), propietaria.getId());
        return conPropietaria(prenda);
    }

    // ------------------------------------------------------------------ endpoint 4

    @Override
    @Transactional
    public PrendaDetalleResponse actualizar(Integer id, PrendaActualizarRequest request, UsuarioAutenticado actual) {
        Prenda prenda = prendaRepositorio.findById(id).orElseThrow(() -> noExiste(id));
        if (!prenda.getPerfil().getId().equals(actual.getPerfilId())) {
            throw new PermisoDenegadoException("Solo la propietaria puede modificar la prenda");
        }
        if (prenda.getSituacion() == EstadoPrenda.RETIRADA) {
            throw new ReglaNegocioException("No se puede modificar una prenda retirada");
        }

        prendaMapper.actualizar(request, prenda);
        if (request.categoriaId() != null) {
            prenda.setCategoria(categoriaRepositorio.findById(request.categoriaId()).orElseThrow(
                    () -> new RecursoNoEncontradoException("No existe la categoría con id " + request.categoriaId())));
        }
        if (!Boolean.TRUE.equals(prenda.getPermitePrestamo()) && !Boolean.TRUE.equals(prenda.getPermiteIntercambio())) {
            throw new ReglaNegocioException("La prenda debe admitir préstamo, intercambio o ambos");
        }

        log.info("Prenda {} modificada por el perfil {}", id, actual.getPerfilId());
        return conPropietaria(prenda);
    }

    // ------------------------------------------------------------------ endpoint 5

    @Override
    @Transactional
    public void retirar(Integer id, UsuarioAutenticado actual) {
        Prenda prenda = prendaRepositorio.findById(id).orElseThrow(() -> noExiste(id));
        boolean propia = prenda.getPerfil().getId().equals(actual.getPerfilId());
        if (!propia && !actual.esPersonalDeGestion()) {
            throw new PermisoDenegadoException("Solo la propietaria o el equipo de moderación pueden retirar la prenda");
        }
        if (prenda.getSituacion() == EstadoPrenda.RETIRADA) {
            return; // ya estaba retirada: el borrado es idempotente
        }

        prenda.setSituacion(EstadoPrenda.RETIRADA); // baja lógica
        prendaRepositorio.saveAndFlush(prenda);

        // RN-10: sus solicitudes pendientes se cancelan (UPDATE masivo; limpia el contexto de persistencia)
        int canceladas = solicitudRepositorio.cancelarPendientesDePrenda(id, LocalDate.now(clock));
        log.info("Prenda {} retirada por el perfil {} ({} solicitudes pendientes canceladas)",
                id, actual.getPerfilId(), canceladas);
    }

    // ------------------------------------------------------------------ auxiliares

    private PrendaDetalleResponse conPropietaria(Prenda prenda) {
        Perfil duena = prenda.getPerfil();
        PropietariaResponse propietaria = new PropietariaResponse(duena.getId(), duena.getNombreUsuario(),
                duena.getCiudad(), reputaciones.de(duena.getId()));
        return prendaMapper.toDetalle(prenda).conPropietaria(propietaria);
    }

    private RecursoNoEncontradoException noExiste(Integer id) {
        return new RecursoNoEncontradoException("No existe la prenda con id " + id);
    }
}
