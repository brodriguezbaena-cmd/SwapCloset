package es.safareyes.swapcloset.Modelos;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "Perfil")
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Perfil {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_perfil")
    private Integer id;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "apellido1")
    private String apellido1;

    @Column(name = "apellido2")
    private String apellido2;

    @Column(name = "biografia")
    private String biografia;

    @Column(name = "ciudad")
    private String ciudad;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(name = "email")
    private String email;

    @Column(name = "talla_zapatos")
    private String talla_zapatos;

    @Column(name = "talla_camiseta")
    private String tallaCamiseta;

    @Column(name = "talla_pantalon")
    private String talla_pantalon;

    // Tabla puente perfil_estilo (id_perfil, id_estilo)
    @ManyToMany
    @JoinTable(name = "perfil_estilo",
            joinColumns = @JoinColumn(name = "id_perfil"),
            inverseJoinColumns = @JoinColumn(name = "id_estilo"))
    private Set<Estilo> estilos;

    @OneToMany(mappedBy = "perfil")
    private Set<Prenda> prendas;
 }
