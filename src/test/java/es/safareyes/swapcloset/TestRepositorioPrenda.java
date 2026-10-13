package es.safareyes.swapcloset;

import es.safareyes.swapcloset.Modelos.Prenda;
import es.safareyes.swapcloset.repositorios.IPrendaRepositorio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:db_test;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class TestRepositorioPrenda {

    @Autowired
    private IPrendaRepositorio prendaRepositorio;

    @Test
    @DisplayName("Debe guardar y listar prendas existentes")
    void testGuardarYListarPrendas() {
        // Given
        Prenda prenda = new Prenda();
        prenda.setTitulo("Chaqueta Vaquera");
        prendaRepositorio.save(prenda);

        // When
        List<Prenda> lista = prendaRepositorio.findAll();

        // Then
        assertThat(lista).isNotEmpty();
        assertThat(lista.get(0).getTitulo()).isEqualTo("Chaqueta Vaquera");
    }
}