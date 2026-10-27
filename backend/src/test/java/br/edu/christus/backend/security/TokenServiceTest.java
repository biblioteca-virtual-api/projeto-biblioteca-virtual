package br.edu.christus.backend.security;

import br.edu.christus.backend.domain.TipoUsuario;
import br.edu.christus.backend.domain.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.core.env.StandardEnvironment;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * O JWT_SECRET não tem mais valor padrão no código: a aplicação só sobe com um segredo próprio.
 * Testes sem banco: o TokenService é criado direto ou num contexto Spring mínimo (só com ele).
 */
class TokenServiceTest {

    private static final String SEGREDO_VALIDO = "0123456789abcdef0123456789abcdef"; // 32 caracteres

    @Test
    void aplicacaoNaoSobeSemJwtSecret() {
        new ApplicationContextRunner()
                // Tira as variáveis de ambiente da máquina: o teste não pode depender de quem roda ter JWT_SECRET
                .withInitializer(c -> c.getEnvironment().getPropertySources()
                        .remove(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME))
                // Mesmo tratamento de ${...} da aplicação real: variável inexistente é erro, e não texto literal
                .withConfiguration(AutoConfigurations.of(PropertyPlaceholderAutoConfiguration.class))
                .withUserConfiguration(TokenService.class)
                .run(contexto -> {
                    assertThat(contexto).hasFailed();
                    assertThat(contexto.getStartupFailure()).rootCause()
                            .hasMessageContaining("Could not resolve placeholder 'JWT_SECRET'");
                });
    }

    @Test
    void recusaSegredoVazioCurtoOuOPublicoAntigo() {
        for (String segredo : List.of("", "   ", "curto-demais", "segredo-super-secreto-biblioteca-virtual-np2")) {
            assertThrows(IllegalStateException.class, () -> new TokenService(segredo));
        }
    }

    @Test
    void tokenSoValeComOMesmoSegredo() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("ana@teste.local");
        usuario.setTipoUsuario(TipoUsuario.ALUNO);

        String token = new TokenService(SEGREDO_VALIDO).gerarToken(usuario);

        assertEquals("ana@teste.local", new TokenService(SEGREDO_VALIDO).validarToken(token));
        // Com outro segredo a assinatura não confere: sem conhecer o JWT_SECRET não dá para forjar um token
        assertNull(new TokenService("fedcba9876543210fedcba9876543210").validarToken(token));
    }
}
