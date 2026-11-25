package es.safareyes.swapcloset.service.impl;

import es.safareyes.swapcloset.dtos.response.CategoriaResponse;
import es.safareyes.swapcloset.repositorios.ICategoriaRepositorio;
import es.safareyes.swapcloset.service.CategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaServiceImpl implements CategoriaService {

    private final ICategoriaRepositorio categoriaRepositorio;

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaResponse> listar() {
        return categoriaRepositorio.contarPrendasDisponibles().stream()
                .map(v -> new CategoriaResponse(v.getId(), v.getNombre(), v.getPrendasDisponibles()))
                .toList();
    }
}
