package br.edu.christus.backend.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;



@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@Embeddable //permite que eu use essa classe em uma PK composta

public class FavoritoId {
    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(name = "livro_id")
    private Long livroId;
}

