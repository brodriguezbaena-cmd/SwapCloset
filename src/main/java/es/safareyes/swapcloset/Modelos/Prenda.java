package es.safareyes.swapcloset.Modelos;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "prenda")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Prenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_prenda")
    private Integer id;

    @Column(name = "titulo", length = 100)
    private String titulo;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "marca", length = 100)
    private String marca;

    // 'Nueva con etiqueta' | 'Como nueva' | 'Buen estado' | 'Usado'
    @Column(name = "estado", length = 50)
    private String estado;

    @Column(name = "permite_prestamo")
    private Boolean permitePrestamo;

    @Column(name = "permite_intercambio")
    private Boolean permiteIntercambio;

    @Column(name = "disponibilidad")
    private Boolean disponibilidad;

    @Column(name = "talla", length = 20)
    private String talla;

    @Column(name = "fecha_publicacion")
    private LocalDate fechaPublicacion;

    // Perfil que publica la prenda
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_perfil")
    private Perfil perfil;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria")
    private Categoria categoria;

    @OneToMany(mappedBy = "prenda", fetch = FetchType.LAZY)
    @OrderBy("orden ASC")
    private List<Foto> fotos = new ArrayList<>();
}
