package es.safareyes.swapcloset.repositorios;

import es.safareyes.swapcloset.Modelos.EstadoPrenda;
import es.safareyes.swapcloset.Modelos.Prenda;
import es.safareyes.swapcloset.dtos.ActividadView;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface IPrendaRepositorio extends JpaRepository<Prenda, Integer>, JpaSpecificationExecutor<Prenda> {

    @EntityGraph(attributePaths = {"perfil", "categoria", "fotos"})
    Optional<Prenda> findDetalleById(Integer id);

    @EntityGraph(attributePaths = {"perfil", "categoria", "fotos"})
    List<Prenda> findByPerfilIdAndSituacionOrderByFechaPublicacionDescIdDesc(Integer perfilId, EstadoPrenda situacion);

    @Query(value = """
            select c.nombre as categoria,
                   to_char(pr.fecha_publicacion, 'YYYY-MM') as mes,
                   count(distinct pr.id_prenda) as publicaciones,
                   count(distinct case when s.tipo_solicitud = 'INTERCAMBIO' then s.id_solicitud end) as intercambios,
                   count(distinct case when s.tipo_solicitud = 'PRESTAMO' then s.id_solicitud end) as prestamos
            from prenda pr
            join categoria c on c.id_categoria = pr.id_categoria
            left join solicitudes s on s.id_prenda = pr.id_prenda
            where pr.fecha_publicacion between :desde and :hasta
            group by c.nombre, to_char(pr.fecha_publicacion, 'YYYY-MM')
            order by mes desc, categoria asc
            """, nativeQuery = true)
    List<ActividadView> actividadPorCategoriaYMes(@Param("desde") LocalDate desde, @Param("hasta") LocalDate hasta);
}