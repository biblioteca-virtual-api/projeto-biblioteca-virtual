package br.edu.christus.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tb_livro")
public class Livro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false, unique = true, length = 20)
    private String isbn;

    @Column(name = "ano_publicacao")
    private Integer anoPublicacao;

    @Column(columnDefinition = "TEXT")
    private String sinopse;

    // Edição e PDF (V7): opcionais no banco porque os livros antigos não têm;
    // o livro criado pela aprovação de um upload recebe os dois (RF40).
    @Column(length = 50)
    private String edicao;

    // Referência (caminho/URL) do PDF usado na leitura online
    @Column(length = 500)
    private String pdf;

    // RN14: exatamente um autor (optional = false => obrigatório)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "autor_id", nullable = false)
    private Autor autor;

    // RN14: exatamente um gênero
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "genero_id", nullable = false)
    private Genero genero;
}
