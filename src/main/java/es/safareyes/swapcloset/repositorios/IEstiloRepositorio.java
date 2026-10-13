package es.safareyes.swapcloset.repositorios;

import es.safareyes.swapcloset.Modelos.Estilo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface IEstiloRepositorio extends JpaRepository<Estilo, Integer> {

    Optional<Estilo> findByNombre(String nombre);
}

