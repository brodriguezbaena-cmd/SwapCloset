package es.safareyes.swapcloset.repositorios;
import es.safareyes.swapcloset.Modelos.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface ICategoriaRepositorio extends JpaRepository<Categoria, Integer> {

    Optional<Categoria> findByNombre(String nombre);
}
