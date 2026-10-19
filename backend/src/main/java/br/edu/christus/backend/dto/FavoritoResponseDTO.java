package br.edu.christus.backend.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FavoritoResponseDTO {
    private Long livroId;
    private String livroTitulo;
    private String livroIsbn;
}
