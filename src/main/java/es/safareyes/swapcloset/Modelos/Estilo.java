package es.safareyes.swapcloset.Modelos;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "estilos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Estilo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estilo")
    private Integer id;

    @Column(name = "nombre")
    private String nombre;
}
