package br.edu.christus.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tb_favorito")
public class Favorito {

    @EmbeddedId // faz que usuarioId e livroId sejam chave primária composta,
    private FavoritoId id;

    @MapsId("usuarioId") //associa com atributo que foi criado em FavoritoId
    // para não precisar criar o atributo usuarioId aqui tb
    @ManyToOne(fetch = FetchType.LAZY, optional = false)  //o fav precisa estar associado a um usuario
    @JoinColumn(name = "usuario_id", nullable = false) //usa o join para relacionamentos/FK
    private Usuario usuario;

    @MapsId("livroId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "livro_id", nullable = false)
    private Livro livro;
}
