package es.safareyes.swapcloset.Modelos;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "perfil")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
public class Perfil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_perfil")
    @EqualsAndHashCode.Include
    private Integer id;

    @Column(name = "nombre_usuario", unique = true, length = 50)
    private String nombreUsuario;

    @Column(name = "nombre", length = 50)
    private String nombre;

    @Column(name = "apellido1", length = 50)
    private String apellido1;

    @Column(name = "apellido2", length = 50)
    private String apellido2;

    @Column(name = "biografia", length = 255)
    private String biografia;

    @Column(name = "ciudad", length = 100)
    private String ciudad;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(name = "email", unique = true, length = 100)
    private String email;

    @Column(name = "talla_zapatos", length = 20)
    private String tallaZapatos;

    @Column(name = "talla_camiseta", length = 20)
    private String tallaCamiseta;

    @Column(name = "talla_pantalon", length = 20)
    private String tallaPantalon;

    @ManyToMany
    @JoinTable(name = "perfil_estilo",
            joinColumns = @JoinColumn(name = "id_perfil"),
            inverseJoinColumns = @JoinColumn(name = "id_estilo"))
    private Set<Estilo> estilos = new HashSet<>();

    @OneToMany(mappedBy = "perfil")
    private Set<Prenda> prendas = new HashSet<>();
}