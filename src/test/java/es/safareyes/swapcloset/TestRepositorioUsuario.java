package es.safareyes.swapcloset;

import es.safareyes.swapcloset.Modelos.Usuario;
import es.safareyes.swapcloset.repositorios.IUsuarioRepositorio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:db_test;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.sql.init.mode=never" // 👈 Desactiva la ejecución de schema.sql en los tests
})
class TestRepositorioUsuario {

    @Autowired
    private IUsuarioRepositorio usuarioRepositorio;

    @Test
    @DisplayName("Guardar y recuperar Usuario")
    void testUsuarioPersistence() {
        Usuario u = new Usuario();
        Usuario guardado = usuarioRepositorio.save(u);

        assertThat(guardado).isNotNull();
        assertThat(usuarioRepositorio.findAll()).isNotEmpty();
    }
}