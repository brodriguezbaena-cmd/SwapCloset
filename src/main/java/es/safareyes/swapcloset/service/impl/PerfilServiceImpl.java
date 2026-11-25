package es.safareyes.swapcloset.service.impl;

import es.safareyes.swapcloset.Modelos.EstadoPrenda;
import es.safareyes.swapcloset.Modelos.Estilo;
import es.safareyes.swapcloset.Modelos.Perfil;
import es.safareyes.swapcloset.Modelos.Prenda;
import es.safareyes.swapcloset.dtos.request.PerfilActualizarRequest;
import es.safareyes.swapcloset.dtos.response.PerfilAfinResponse;
import es.safareyes.swapcloset.dtos.response.PerfilPublicoResponse;
import es.safareyes.swapcloset.exception.RecursoNoEncontradoException;
import es.safareyes.swapcloset.mapper.PerfilMapper;
import es.safareyes.swapcloset.repositorios.IPerfilRepositorio;
import es.safareyes.swapcloset.repositorios.IPrendaRepositorio;
import es.safareyes.swapcloset.security.UsuarioAutenticado;
import es.safareyes.swapcloset.service.PerfilService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PerfilServiceImpl implements PerfilService {

    private final IPerfilRepositorio perfilRepositorio;
    private final IPrendaRepositorio prendaRepositorio;
    private final PerfilMapper perfilMapper;
    private final ResolvedorEstilos resolvedorEstilos;
    private final Reputaciones reputaciones;

    // ------------------------------------------------------------------ endpoint 7

    @Override
    @Transactional(readOnly = true)
    public PerfilPublicoResponse obtener(Integer perfilId) {
        return construir(cargar(perfilId));
    }

    // ------------------------------------------------------------------ endpoint 8

    @Override
    @Transactional
    public PerfilPublicoResponse actualizarMiPerfil(PerfilActualizarRequest request, UsuarioAutenticado actual) {
        Perfil perfil = cargar(actual.getPerfilId());
        perfilMapper.actualizar(request, perfil);
        if (request.estilosIds() != null) {
            perfil.getEstilos().clear();
            perfil.getEstilos().addAll(resolvedorEstilos.resolver(request.estilosIds()));
        }
        log.info("Perfil {} actualizado", perfil.getId());
        return construir(perfil);
    }

    // ------------------------------------------------------------------ endpoint 9

    @Override
    @Transactional(readOnly = true)
    public List<PerfilAfinResponse> afines(UsuarioAutenticado actual, int limite) {
        Perfil perfil = cargar(actual.getPerfilId());
        List<Integer> misEstilos = perfil.getEstilos().stream().map(Estilo::getId).toList();
        if (misEstilos.isEmpty()) {
            return List.of();
        }
        return perfilRepositorio.buscarAfines(perfil.getId(), misEstilos, PageRequest.of(0, limite)).stream()
                .map(v -> new PerfilAfinResponse(v.getId(), v.getNombreUsuario(), v.getCiudad(),
                        v.getEstilosComunes(),
                        Reputaciones.redondear(v.getReputacionMedia(), v.getTotalValoraciones())))
                .toList();
    }

    // ------------------------------------------------------------------ auxiliares

    private Perfil cargar(Integer perfilId) {
        return perfilRepositorio.findConEstilosById(perfilId)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el perfil con id " + perfilId));
    }

    private PerfilPublicoResponse construir(Perfil perfil) {
        List<Prenda> disponibles = prendaRepositorio
                .findByPerfilIdAndSituacionOrderByFechaPublicacionDescIdDesc(perfil.getId(), EstadoPrenda.DISPONIBLE);
        return perfilMapper.toPublico(perfil)
                .conReputacionYPrendas(reputaciones.de(perfil.getId()), perfilMapper.toTarjetas(disponibles));
    }
}
