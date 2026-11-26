package es.safareyes.swapcloset.repositorios;

import es.safareyes.swapcloset.Modelos.Mensaje;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IMensajeRepositorio extends JpaRepository<Mensaje, Integer> {

    @EntityGraph(attributePaths = {"autor"})
    Page<Mensaje> findBySolicitudIdOrderByFechaEnvioAscHoraEnvioAscIdAsc(Integer solicitudId, Pageable pageable);
}