package es.safareyes.swapcloset.service.impl;

import es.safareyes.swapcloset.dtos.response.ActividadCategoriaResponse;
import es.safareyes.swapcloset.exception.PeticionNoValidaException;
import es.safareyes.swapcloset.repositorios.IPrendaRepositorio;
import es.safareyes.swapcloset.service.InformeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InformeServiceImpl implements InformeService {

    private final IPrendaRepositorio prendaRepositorio;

    // ------------------------------------------------------------------ endpoint 16

    @Override
    @Transactional(readOnly = true)
    public List<ActividadCategoriaResponse> actividad(LocalDate desde, LocalDate hasta) {
        if (desde.isAfter(hasta)) {
            throw new PeticionNoValidaException("'desde' no puede ser posterior a 'hasta'");
        }
        return prendaRepositorio.actividadPorCategoriaYMes(desde, hasta).stream()
                .map(v -> new ActividadCategoriaResponse(v.getMes(), v.getCategoria(),
                        v.getPublicaciones(), v.getIntercambios(), v.getPrestamos()))
                .toList();
    }
}
