package es.safareyes.swapcloset;

import es.safareyes.swapcloset.Modelos.Perfil; // Ajusta según la ruta de tu entidad
import es.safareyes.swapcloset.repositorios.IPerfilRepositorio; // Ajusta según tu repositorio
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
class TestRepositorioPerfil {

    @Autowired
    private IPerfilRepositorio perfilRepositorio;

    @Test
    @DisplayName("Debe buscar perfiles por ciudad correctamente")
    void buscarPorCiudadTest() {
        // 1. Guardar datos ficticios
        Perfil perfil = new Perfil();
        perfil.setCiudad("Sevilla");
        perfil.setTallaCamiseta("M");
        perfilRepositorio.save(perfil);

        // 2. Ejecutar la consulta del repositorio
        List<Perfil> resultado = perfilRepositorio.findAllByCiudad("Sevilla");

        // 3. Comprobar que devuelve lo esperado
        assertThat(resultado).isNotEmpty();
        assertThat(resultado.get(0).getCiudad()).isEqualTo("Sevilla");
    }
}