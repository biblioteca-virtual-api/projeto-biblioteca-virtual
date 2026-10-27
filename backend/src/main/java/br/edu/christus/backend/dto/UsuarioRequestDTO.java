package br.edu.christus.backend.dto;

import br.edu.christus.backend.domain.TipoUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO de ENTRADA do POST /api/v1/users (cadastro de usuário).
 *
 * Não tem id de propósito: quando o controller recebia a entidade Usuario, quem mandasse o "id"
 * de outra conta fazia o save() virar UPDATE e sobrescrever essa conta. Com o DTO, o usuário é sempre novo.
 * tipoUsuario é opcional: sem ele, o cadastro cria ALUNO. Só um ADMINISTRADOR logado pode pedir
 * PROFESSOR ou ADMINISTRADOR (RN01/RF03); essa regra fica no UsuarioService.
 */
public record UsuarioRequestDTO(

        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 255, message = "O nome deve ter no máximo 255 caracteres")
        String nome,

        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "Informe um e-mail válido")
        @Size(max = 255, message = "O e-mail deve ter no máximo 255 caracteres")
        String email,

        // 72 é o limite do BCrypt; o UsuarioService ainda confere o limite em bytes (acentos ocupam mais de 1).
        @NotBlank(message = "A senha é obrigatória")
        @Size(min = 8, max = 72, message = "A senha deve ter entre 8 e 72 caracteres")
        String senha,

        TipoUsuario tipoUsuario
) {
}
