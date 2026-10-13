package es.safareyes.swapcloset.repositorios;

import es.safareyes.swapcloset.Modelos.Valoracion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface IValoracionRepositorio extends JpaRepository<Valoracion, Integer> {

    List<Valoracion> findAllByPuntuacionGreaterThanEqual(Integer puntuacion);

    Optional<Valoracion> findBysolicitudes(Integer id_solicitud);


    @Query("select avg(v.puntuacion) from Valoracion v")
    Double puntuacionMedia();
}
