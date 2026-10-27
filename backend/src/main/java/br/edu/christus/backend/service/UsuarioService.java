package br.edu.christus.backend.service;

import br.edu.christus.backend.domain.TipoUsuario;
import br.edu.christus.backend.domain.Usuario;
import br.edu.christus.backend.dto.UsuarioRequestDTO;
import br.edu.christus.backend.dto.UsuarioResponseDTO;
import br.edu.christus.backend.exception.BadRequestException;
import br.edu.christus.backend.exception.ConflictException;
import br.edu.christus.backend.exception.ResourceNotFoundException;
import br.edu.christus.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class UsuarioService {

    // Limite do BCrypt: acima disso o PasswordEncoder lança exceção (viraria erro 500).
    private static final int MAXIMO_BYTES_SENHA = 72;

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Cadastro. O usuário é montado do zero a partir do DTO (que não tem id),
    // então o save() sempre faz INSERT e nunca sobrescreve uma conta existente.
    @Transactional
    public UsuarioResponseDTO create(UsuarioRequestDTO dto) {
        // RN01/RF03: sem perfil informado, o usuário é ALUNO. PROFESSOR e ADMINISTRADOR só podem ser
        // criados por um ADMINISTRADOR logado: no cadastro público (sem token), pedir esses perfis dá 403.
        TipoUsuario tipo = (dto.tipoUsuario() == null) ? TipoUsuario.ALUNO : dto.tipoUsuario();
        if (tipo != TipoUsuario.ALUNO && !usuarioLogadoEhAdministrador()) {
            throw new AccessDeniedException("Só um administrador pode cadastrar professores e administradores.");
        }

        // O @Size do DTO conta caracteres, mas o BCrypt conta bytes: letras acentuadas ocupam 2.
        if (dto.senha().getBytes(StandardCharsets.UTF_8).length > MAXIMO_BYTES_SENHA) {
            throw new BadRequestException("senha: A senha é longa demais. Use até 72 caracteres, "
                    + "lembrando que letras acentuadas contam como 2.");
        }

        // E-mail é único no banco: devolve 409 com uma mensagem clara em vez do erro genérico de integridade
        if (repository.existsByEmail(dto.email())) {
            throw new ConflictException("Já existe um usuário cadastrado com este e-mail.");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        // Criptografa a senha antes de salvar no banco
        usuario.setSenha(passwordEncoder.encode(dto.senha()));
        usuario.setTipoUsuario(tipo);
        return toResponse(repository.save(usuario));
    }

    // Só o ADMINISTRADOR chega aqui (SecurityConfig). A resposta é DTO: a senha não sai da API.
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> findUsers() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    public Usuario findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado. ID: " + id));
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Usuário não encontrado para exclusão. ID: " + id);
        }
        repository.deleteById(id);
    }

    // O SecurityFilter coloca o usuário do token no contexto, mesmo na rota pública de cadastro.
    // Sem token (ou com token inválido), quem chama é anônimo e não tem o perfil ADMINISTRADOR.
    private boolean usuarioLogadoEhAdministrador() {
        Authentication autenticacao = SecurityContextHolder.getContext().getAuthentication();
        return autenticacao != null && autenticacao.getAuthorities().stream()
                .anyMatch(perfil -> "ROLE_ADMINISTRADOR".equals(perfil.getAuthority()));
    }

    // Conversão ENTIDADE -> DTO de saída: só os campos que podem ser expostos (sem a senha).
    private UsuarioResponseDTO toResponse(Usuario u) {
        return new UsuarioResponseDTO(u.getId(), u.getNome(), u.getEmail(), u.getTipoUsuario());
    }
}
