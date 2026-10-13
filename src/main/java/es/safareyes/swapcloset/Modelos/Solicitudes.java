package es.safareyes.swapcloset.Modelos;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "solicitudes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Solicitudes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_solicitud")
    private Integer id;

    // 'Intercambio' | 'Prestamo'
    @Column(name = "tipo_solicitud", length = 50)
    private String tipoSolicitud;

    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    // NULL mientras nadie ha respondido
    @Column(name = "fecha_respuesta")
    private LocalDate fechaRespuesta;

    // 'Pendiente' | 'Aceptada' | 'Rechazada'
    @Column(name = "estado", length = 50)
    private String estado;

    @Column(name = "fecha_creacion")
    private LocalDate fechaCreacion;

    // Perfil que hace la solicitud
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_perfil")
    private Perfil perfil;

    // Tabla puente solicitud_recibe (id_solicitud, id_perfil): perfiles que reciben la solicitud
    @ManyToMany
    @JoinTable(name = "solicitud_recibe",
            joinColumns = @JoinColumn(name = "id_solicitud"),
            inverseJoinColumns = @JoinColumn(name = "id_perfil"))
    private Set<Perfil> destinatarios;
}
