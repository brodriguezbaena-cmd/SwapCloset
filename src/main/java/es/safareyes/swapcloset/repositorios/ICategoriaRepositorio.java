package es.safareyes.swapcloset.repositorios;

import es.safareyes.swapcloset.Modelos.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ICategoriaRepositorio extends JpaRepository<Categoria, Integer> {

    interface CategoriaConteoView {
        Integer getId();
        String getNombre();
        Long getPrendasDisponibles();
    }

    @Query("""
            select c.id as id,
                   c.nombre as nombre,
                   count(p) as prendasDisponibles
            from Categoria c
            left join Prenda p on p.categoria = c and p.situacion = es.safareyes.swapcloset.Modelos.EstadoPrenda.DISPONIBLE
            group by c.id, c.nombre
            order by c.nombre asc
            """)
    List<CategoriaConteoView> contarPrendasDisponibles();
}