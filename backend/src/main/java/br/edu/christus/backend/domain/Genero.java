package main.java.br.edu.christus.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@Entity  //diz ao JPA que genero é uma entidade no banco
@Table(name = "tb_genero")
public class Genero {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_genero;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private Boolean ativo=true;



}
