package es.safareyes.swapcloset.repositorios;

import es.safareyes.swapcloset.Modelos.Foto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface IFotoRepositorio extends JpaRepository<Foto, Integer> {


    List<Foto> findAllByPrendaIdOrderByOrdenAsc(Integer idPrenda);
}
