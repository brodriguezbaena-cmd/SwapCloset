package es.safareyes.swapcloset.service.impl;

import es.safareyes.swapcloset.Modelos.Estilo;
import es.safareyes.swapcloset.exception.ReglaNegocioException;
import es.safareyes.swapcloset.repositorios.IEstiloRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** RN-02: un perfil tiene entre 1 y 5 estilos, y todos deben existir. */
@Component
@RequiredArgsConstructor
class ResolvedorEstilos {

    private static final int MAXIMO = 5;

    private final IEstiloRepositorio estiloRepositorio;

    Set<Estilo> resolver(Collection<Integer> ids) {
        Set<Integer> unicos = new LinkedHashSet<>(ids);
        if (unicos.isEmpty() || unicos.size() > MAXIMO) {
            throw new ReglaNegocioException("Un perfil debe tener entre 1 y " + MAXIMO + " estilos");
        }
        List<Estilo> encontrados = estiloRepositorio.findAllById(unicos);
        if (encontrados.size() != unicos.size()) {
            throw new ReglaNegocioException("Alguno de los estilos indicados no existe");
        }
        return new HashSet<>(encontrados);
    }
}
