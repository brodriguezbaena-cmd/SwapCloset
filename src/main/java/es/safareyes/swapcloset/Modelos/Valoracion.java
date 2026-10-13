package es.safareyes.swapcloset.Modelos;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "valoraciones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Valoracion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_valoracion")
    private Integer id;

    @Column(name = "comentario", length = 500)
    private String comentario;

    @Column(name = "puntuacion")
    private Integer puntuacion;

    @Column(name = "fecha")
    private LocalDate fecha;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_solicitud", unique = true)
    private Solicitudes solicitudes;

    // Perfil que escribe la valoración
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_perfil")
    private Perfil perfil;
}
