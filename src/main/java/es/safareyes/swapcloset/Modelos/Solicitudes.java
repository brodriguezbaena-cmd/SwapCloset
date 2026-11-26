package es.safareyes.swapcloset.Modelos;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_solicitud", length = 50)
    private TipoSolicitud tipoSolicitud;

    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @Column(name = "fecha_respuesta")
    private LocalDate fechaRespuesta;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", length = 50)
    private EstadoSolicitud estado;

    @Column(name = "fecha_creacion")
    private LocalDate fechaCreacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_perfil")
    private Perfil solicitante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_prenda")
    private Prenda prenda;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_prenda_ofrecida")
    private Prenda prendaOfrecida;

    @ManyToMany
    @JoinTable(name = "solicitud_recibe",
            joinColumns = @JoinColumn(name = "id_solicitud"),
            inverseJoinColumns = @JoinColumn(name = "id_perfil"))
    private Set<Perfil> destinatarios = new HashSet<>();

    public Perfil getDestinatario() {
        return (destinatarios == null || destinatarios.isEmpty()) ? null : destinatarios.iterator().next();
    }

    public boolean esSolicitante(Integer perfilId) {
        return solicitante != null && solicitante.getId().equals(perfilId);
    }

    public boolean esDestinatario(Integer perfilId) {
        return destinatarios != null && destinatarios.stream().anyMatch(d -> d.getId().equals(perfilId));
    }

    public boolean esParticipante(Integer perfilId) {
        return esSolicitante(perfilId) || esDestinatario(perfilId);
    }
}