package es.safareyes.swapcloset.repositorios;

import es.safareyes.swapcloset.Modelos.EstadoSolicitud;
import es.safareyes.swapcloset.Modelos.Solicitudes;
import es.safareyes.swapcloset.Modelos.TipoSolicitud;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ISolicitudRepositorio extends JpaRepository<Solicitudes, Integer> {

    @EntityGraph(attributePaths = {"solicitante", "destinatarios", "prenda", "prendaOfrecida"})
    Optional<Solicitudes> findDetalleById(Integer id);

    boolean existsBySolicitanteIdAndPrendaIdAndEstado(Integer solicitanteId, Integer prendaId, EstadoSolicitud estado);

    long countBySolicitanteIdAndTipoSolicitudAndEstadoIn(Integer solicitanteId,
                                                         TipoSolicitud tipoSolicitud,
                                                         Collection<EstadoSolicitud> estados);

    @EntityGraph(attributePaths = {"solicitante", "destinatarios", "prenda", "prendaOfrecida"})
    List<Solicitudes> findBySolicitanteIdOrderByFechaCreacionDescIdDesc(Integer solicitanteId);

    @EntityGraph(attributePaths = {"solicitante", "destinatarios", "prenda", "prendaOfrecida"})
    List<Solicitudes> findBySolicitanteIdAndEstadoOrderByFechaCreacionDescIdDesc(Integer solicitanteId,
                                                                                 EstadoSolicitud estado);

    @EntityGraph(attributePaths = {"solicitante", "destinatarios", "prenda", "prendaOfrecida"})
    @Query("select distinct s from Solicitudes s join s.destinatarios d where d.id = :perfilId order by s.fechaCreacion desc, s.id desc")
    List<Solicitudes> buscarRecibidas(@Param("perfilId") Integer perfilId);

    @EntityGraph(attributePaths = {"solicitante", "destinatarios", "prenda", "prendaOfrecida"})
    @Query("select distinct s from Solicitudes s join s.destinatarios d where d.id = :perfilId and s.estado = :estado order by s.fechaCreacion desc, s.id desc")
    List<Solicitudes> buscarRecibidasPorEstado(@Param("perfilId") Integer perfilId,
                                               @Param("estado") EstadoSolicitud estado);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Solicitudes s set s.estado = 'CANCELADA', s.fechaRespuesta = :hoy " +
            "where (s.prenda.id = :prendaId or s.prendaOfrecida.id = :prendaId) and s.estado = 'PENDIENTE'")
    int cancelarPendientesDePrenda(@Param("prendaId") Integer prendaId, @Param("hoy") LocalDate hoy);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Solicitudes s set s.estado = 'RECHAZADA', s.fechaRespuesta = :hoy " +
            "where s.id <> :aceptadaId and s.estado = 'PENDIENTE' " +
            "and (s.prenda.id in :prendasIds or s.prendaOfrecida.id in :prendasIds)")
    int rechazarPendientesDePrendas(@Param("aceptadaId") Integer aceptadaId,
                                    @Param("prendasIds") List<Integer> prendasIds,
                                    @Param("hoy") LocalDate hoy);
}