package es.safareyes.swapcloset.repositorios;

import es.safareyes.swapcloset.Modelos.Valoracion;
import es.safareyes.swapcloset.dtos.response.ReputacionResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IValoracionRepositorio extends JpaRepository<Valoracion, Integer> {

    boolean existsBySolicitudIdAndAutorId(Integer solicitudId, Integer autorId);

    @Query("select new es.safareyes.swapcloset.dtos.response.ReputacionResponse(avg(v.puntuacion), count(v)) " +
            "from Valoracion v where v.valorado.id = :perfilId")
    ReputacionResponse reputacionDe(@Param("perfilId") Integer perfilId);
}