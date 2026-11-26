package es.safareyes.swapcloset.repositorios;

import es.safareyes.swapcloset.Modelos.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUsuarioRepositorio extends JpaRepository<Usuario, Long> {

    @Query("select u from Usuario u join fetch u.perfil p where lower(p.email) = lower(:email)")
    Optional<Usuario> buscarConPerfilPorEmail(@Param("email") String email);
}