package es.safareyes.swapcloset.Modelos;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "mensajes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Mensaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mensaje")
    private Integer id;

    @Column(name = "fecha_envio")
    private LocalDate fechaEnvio;

    @Column(name = "hora_envio")
    private LocalTime horaEnvio;

    @Column(name = "contenido", length = 500)
    private String contenido;


    @ManyToMany
    @JoinTable(name = "mensaje_envia",
            joinColumns = @JoinColumn(name = "id_mensaje"),
            inverseJoinColumns = @JoinColumn(name = "id_perfil"))
    private Set<Perfil> remitentes = new HashSet<>();

    // Tabla puente mensaje_recibe (id_mensaje, id_perfil)
    @ManyToMany
    @JoinTable(name = "mensaje_recibe",
            joinColumns = @JoinColumn(name = "id_mensaje"),
            inverseJoinColumns = @JoinColumn(name = "id_perfil"))
    private Set<Perfil> destinatarios = new HashSet<>();
}
