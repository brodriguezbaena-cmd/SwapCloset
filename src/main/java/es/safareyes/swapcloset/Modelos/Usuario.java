package es.safareyes.swapcloset.Modelos;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Usuario")
@Getter @Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long id;

    @Column(name = "contrasena")
    private String contraseña;

    @Column(name = "rol")
    private String rol;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_perfil", unique = true)
    private Perfil perfil;
}

