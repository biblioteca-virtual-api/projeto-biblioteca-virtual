package br.edu.christus.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Livro enviado por um professor para análise do administrador (RF30 a RF44).
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tb_upload")
public class Upload {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    // RF43: texto livre, o autor pode ainda não estar cadastrado
    @Column(nullable = false)
    private String autor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "genero_id", nullable = false)
    private Genero genero;

    @Column(nullable = false, length = 50)
    private String edicao;

    @Column(name = "ano_publicacao", nullable = false)
    private Integer anoPublicacao;

    // Referência (caminho/URL) do PDF enviado
    @Column(nullable = false, length = 500)
    private String pdf;

    // Professor que enviou o upload
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "professor_id", nullable = false)
    private Usuario professor;

    // RN04: todo upload nasce PENDENTE
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusUpload status = StatusUpload.PENDENTE;

    // RN06: obrigatório quando o upload é REJEITADO
    @Column(name = "motivo_rejeicao", length = 500)
    private String motivoRejeicao;

    // RF41: livro criado na aprovação; fica nulo se o livro for excluído (RN23)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "livro_id", unique = true)
    private Livro livro;
}
