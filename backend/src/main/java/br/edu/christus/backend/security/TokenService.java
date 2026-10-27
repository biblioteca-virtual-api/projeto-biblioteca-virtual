package br.edu.christus.backend.security;

import br.edu.christus.backend.domain.Usuario;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    // A especificação do HS256 (RFC 7518) exige chave de pelo menos 256 bits = 32 bytes.
    private static final int TAMANHO_MINIMO_SEGREDO = 32;

    // Segredo que já foi publicado no .env.example e no código (continua no histórico do Git).
    // Quem lê o repositório conseguiria assinar tokens falsos com ele, por isso ele é recusado.
    private static final String SEGREDO_PUBLICO_ANTIGO = "segredo-super-secreto-biblioteca-virtual-np2";

    private final String secret;

    // ${JWT_SECRET} sem valor padrão de propósito: se a variável não estiver no .env (nem no ambiente),
    // o Spring não consegue criar o TokenService e a aplicação não sobe. O .env.example explica como gerar.
    public TokenService(@Value("${JWT_SECRET}") String secret) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("JWT_SECRET está vazio. Defina um segredo no .env (veja o .env.example).");
        }
        if (secret.equals(SEGREDO_PUBLICO_ANTIGO)) {
            throw new IllegalStateException("JWT_SECRET está com o segredo público antigo do .env.example. "
                    + "Gere um novo (veja o .env.example).");
        }
        if (secret.getBytes(StandardCharsets.UTF_8).length < TAMANHO_MINIMO_SEGREDO) {
            throw new IllegalStateException("JWT_SECRET é curto demais: use pelo menos "
                    + TAMANHO_MINIMO_SEGREDO + " caracteres (veja o .env.example).");
        }
        this.secret = secret;
    }

    public String gerarToken(Usuario usuario) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("biblioteca-virtual-api")
                    .withSubject(usuario.getEmail())
                    .withClaim("id", usuario.getId())
                    .withClaim("tipoUsuario", usuario.getTipoUsuario().name())
                    .withExpiresAt(gerarDataExpiracao())
                    .sign(algorithm);
        } catch (JWTCreationException e) {
            throw new RuntimeException("Erro ao gerar token JWT", e);
        }
    }

    public String validarToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("biblioteca-virtual-api")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException e) {
            return null;
        }
    }

    private Instant gerarDataExpiracao() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}