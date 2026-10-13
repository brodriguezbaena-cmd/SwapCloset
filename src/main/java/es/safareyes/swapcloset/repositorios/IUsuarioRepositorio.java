package es.safareyes.swapcloset.repositorios;

import es.safareyes.swapcloset.Modelos.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.*;

// La clave primaria de Usuario es un String (id_usuario VARCHAR)
@Repository
public interface IUsuarioRepositorio extends JpaRepository<Usuario, String> {

    List<Usuario> findAllByRol(String rol);

    Optional<Usuario> findByPerfilEmail(String email);
}
