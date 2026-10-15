package br.edu.christus.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LivroResponseDTO {
    private Long id;
    private String titulo;
    private String isbn;
    private Integer anoPublicacao;
    private String sinopse;
    private Long autorId;
    private String autorNome;
    private Long generoId;
    private String generoNome;
}
