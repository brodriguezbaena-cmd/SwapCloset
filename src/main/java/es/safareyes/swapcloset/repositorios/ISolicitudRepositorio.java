package es.safareyes.swapcloset.repositorios;

import es.safareyes.swapcloset.dtos.SolicitudesPorEstado;
import es.safareyes.swapcloset.Modelos.Solicitudes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface ISolicitudRepositorio extends JpaRepository<Solicitudes, Integer> {

}
