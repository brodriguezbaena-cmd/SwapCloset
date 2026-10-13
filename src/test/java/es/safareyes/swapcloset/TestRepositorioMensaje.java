package es.safareyes.swapcloset;

import es.safareyes.swapcloset.Modelos.Mensaje;
import es.safareyes.swapcloset.repositorios.IMensajeRepositorio;
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
        "spring.datasource.url=jdbc:h2:mem:db_test;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class TestRepositorioMensaje {

    @Autowired
    private IMensajeRepositorio mensajeRepositorio;

    @Test
    @DisplayName("Guardar y recuperar Mensaje")
    void testMensajePersistence() {
        Mensaje m = new Mensaje();
        Mensaje guardado = mensajeRepositorio.save(m);

        assertThat(guardado).isNotNull();
        assertThat(mensajeRepositorio.findAll()).isNotEmpty();
    }
}