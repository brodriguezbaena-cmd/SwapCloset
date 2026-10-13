package es.safareyes.swapcloset.repositorios;

import es.safareyes.swapcloset.Modelos.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.*;

@Repository
public interface IMensajeRepositorio extends JpaRepository<Mensaje, Integer> {

    List<Mensaje> findAllByFechaEnvioBetween(LocalDate desde, LocalDate hasta);


    @Query("select m from Mensaje m join m.destinatarios d where d.id = :idPerfil " +
            "order by m.fechaEnvio, m.horaEnvio")
    List<Mensaje> buscarRecibidosPorPerfil(@Param("idPerfil") Integer idPerfil);
}
