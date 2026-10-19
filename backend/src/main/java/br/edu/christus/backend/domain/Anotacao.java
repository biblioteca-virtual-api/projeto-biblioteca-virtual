package br.edu.christus.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter

@Entity
@Table(name = "tb_anotacao")
public class Anotacao {
    public Anotacao() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // RN18: mÃ¡ximo de 400 caracteres
    @Column(nullable = false, length = 400)
    private String texto;

    @Column(nullable = false)
    private Integer pagina;

    // RN17/RN19: o dono da anotaÃ§Ã£o
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "livro_id", nullable = false)
    private Livro livro;
}