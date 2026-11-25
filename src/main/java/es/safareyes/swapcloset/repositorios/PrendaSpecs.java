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
    @SuppressWarnings("unchecked")
    public static Specification<Prenda> filtrar(Integer categoriaId, String talla, String estado,
                                                Integer estiloId, String ciudad, TipoSolicitud oferta) {
        return (root, query, cb) -> {
            boolean esConteo = Long.class.equals(query.getResultType()) || long.class.equals(query.getResultType());

            // En la consulta de datos se cargan categoría y propietaria con un JOIN FETCH (evita N+1);
            // en la de conteo basta un JOIN normal.
            Join<Prenda, Perfil> perfil;
            Join<Prenda, Categoria> categoria;
            if (esConteo) {
                perfil = root.join("perfil", JoinType.INNER);
                categoria = root.join("categoria", JoinType.INNER);
            } else {
                perfil = (Join<Prenda, Perfil>) root.fetch("perfil", JoinType.INNER);
                categoria = (Join<Prenda, Categoria>) root.fetch("categoria", JoinType.INNER);
            }

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
                // estilo de la propietaria
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
