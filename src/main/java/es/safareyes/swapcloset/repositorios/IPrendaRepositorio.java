package es.safareyes.swapcloset.repositorios;

import es.safareyes.swapcloset.dtos.PrendasPorCategoria;
import es.safareyes.swapcloset.Modelos.Prenda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface IPrendaRepositorio extends JpaRepository<Prenda, Integer> {


    List<Prenda> findAllByDisponibilidadTrue();

    List<Prenda> findAllByMarca(String marca);

    List<Prenda> findAllByPermitePrestamoTrueAndPermiteIntercambioTrue();


    @Query("select p from Prenda p where p.categoria.nombre = :nombre")
    List<Prenda> buscarPorNombreCategoria(@Param("nombre") String nombreCategoria);


    @Query(value = "select pr.* from prenda pr " +
            "join perfil pf on pf.id_perfil = pr.id_perfil " +
            "where pf.ciudad = :ciudad",
            nativeQuery = true)
    List<Prenda> buscarPorCiudadDelPerfil(@Param("ciudad") String ciudad);


    @Query("select new es.safareyes.swapcloset.dtos.PrendasPorCategoria(c.nombre, count(p)) " +
            "from Prenda p join p.categoria c group by c.nombre order by c.nombre")
    List<PrendasPorCategoria> contarPrendasPorCategoria();
}
