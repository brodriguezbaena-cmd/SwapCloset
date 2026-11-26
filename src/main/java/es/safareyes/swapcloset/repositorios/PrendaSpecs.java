package es.safareyes.swapcloset.repositorios;

import es.safareyes.swapcloset.Modelos.Categoria;
import es.safareyes.swapcloset.Modelos.EstadoPrenda;
import es.safareyes.swapcloset.Modelos.Perfil;
import es.safareyes.swapcloset.Modelos.Prenda;
import es.safareyes.swapcloset.Modelos.TipoSolicitud;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/** Filtros opcionales y combinables para la búsqueda de prendas (RF-03). */
public final class PrendaSpecs {

    private PrendaSpecs() {
    }

    /**
     * Solo prendas DISPONIBLES (los borradores y las retiradas no aparecen en búsquedas, RN-03 y RN-10).
     * Cada filtro a null se ignora.
     */
    public static Specification<Prenda> filtrar(Integer categoriaId, String talla, String estado,
                                                Integer estiloId, String ciudad, TipoSolicitud oferta) {
        return (root, query, cb) -> {
            boolean esConteo = query != null
                    && (Long.class.equals(query.getResultType()) || long.class.equals(query.getResultType()));

            if (!esConteo) {
                root.fetch("perfil", JoinType.INNER);
                root.fetch("categoria", JoinType.INNER);
            }

            Join<Prenda, Perfil> perfil = root.join("perfil", JoinType.INNER);
            Join<Prenda, Categoria> categoria = root.join("categoria", JoinType.INNER);

            List<Predicate> filtros = new ArrayList<>();
            filtros.add(cb.equal(root.get("situacion"), EstadoPrenda.DISPONIBLE));

            if (categoriaId != null) {
                filtros.add(cb.equal(categoria.get("id"), categoriaId));
            }
            if (hayTexto(talla)) {
                filtros.add(cb.equal(cb.lower(root.get("talla")), talla.trim().toLowerCase()));
            }
            if (hayTexto(estado)) {
                filtros.add(cb.equal(cb.lower(root.get("estado")), estado.trim().toLowerCase()));
            }
            if (hayTexto(ciudad)) {
                filtros.add(cb.equal(cb.lower(perfil.get("ciudad")), ciudad.trim().toLowerCase()));
            }
            if (estiloId != null) {
                Join<Perfil, ?> estilos = perfil.join("estilos", JoinType.INNER);
                filtros.add(cb.equal(estilos.get("id"), estiloId));
            }
            if (oferta == TipoSolicitud.INTERCAMBIO) {
                filtros.add(cb.isTrue(root.get("permiteIntercambio")));
            } else if (oferta == TipoSolicitud.PRESTAMO) {
                filtros.add(cb.isTrue(root.get("permitePrestamo")));
            }
            return cb.and(filtros.toArray(new Predicate[0]));
        };
    }

    private static boolean hayTexto(String s) {
        return s != null && !s.isBlank();
    }
}