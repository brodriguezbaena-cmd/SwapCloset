package es.safareyes.swapcloset.repositorios;

import es.safareyes.swapcloset.Modelos.Perfil;
import es.safareyes.swapcloset.dtos.PerfilAfinView;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IPerfilRepositorio extends JpaRepository<Perfil, Integer> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByNombreUsuarioIgnoreCase(String nombreUsuario);

    @EntityGraph(attributePaths = "estilos")
    Optional<Perfil> findConEstilosById(Integer id);

    @Query("""
            select p.id as id,
                   p.nombreUsuario as nombreUsuario,
                   p.ciudad as ciudad,
                   count(distinct e.id) as estilosComunes,
                   (select avg(v.puntuacion) from Valoracion v where v.valorado.id = p.id) as reputacionMedia,
                   (select count(v) from Valoracion v where v.valorado.id = p.id) as totalValoraciones
            from Perfil p
            join p.estilos e
            where p.id <> :perfilId
              and e.id in :estilosIds
            group by p.id, p.nombreUsuario, p.ciudad
            order by count(distinct e.id) desc, p.id asc
            """)
    List<PerfilAfinView> buscarAfines(@Param("perfilId") Integer perfilId,
                                      @Param("estilosIds") List<Integer> estilosIds,
                                      Pageable pageable);
}