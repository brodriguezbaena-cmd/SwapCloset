package es.safareyes.swapcloset.repositorios;

import es.safareyes.swapcloset.dtos.PerfilesPorCiudad;
import es.safareyes.swapcloset.Modelos.Perfil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface IPerfilRepositorio extends JpaRepository<Perfil, Integer> {


    List<Perfil> findAllByCiudad(String ciudad);

    List<Perfil> findAllByCiudadAndTallaCamiseta(String ciudad, String tallaCamiseta);


    @Query("select p from Perfil p join p.estilos e where e.nombre = :nombre")
    List<Perfil> buscarPorNombreEstilo(@Param("nombre") String nombreEstilo);


    @Query(value = "select * from perfil where ciudad = :ciudad " +
            "and biografia like concat('%', :palabra, '%')",
            nativeQuery = true)
    List<Perfil> buscarPorCiudadYPalabraBiografia(@Param("ciudad") String ciudad,
                                                  @Param("palabra") String palabra);


    @Query("select new es.safareyes.swapcloset.dtos.PerfilesPorCiudad(p.ciudad, count(p)) " +
            "from Perfil p group by p.ciudad order by p.ciudad")
    List<PerfilesPorCiudad> contarPerfilesPorCiudad();
}
