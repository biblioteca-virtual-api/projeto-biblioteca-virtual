-- V6: cria a tabela de anotações privadas do leitor (RF18 a RF25).
-- Próxima migration livre do projeto: V7 (não crie V5: o Flyway recusa versões menores depois de aplicar uma maior).
-- Não edite este arquivo depois de aplicado (nem comentários): o Flyway compara o checksum e trava a subida.
CREATE TABLE tb_anotacao (
    id SERIAL PRIMARY KEY,

    -- RN18: limite de 400 caracteres também garantido no banco.
    texto VARCHAR(400) NOT NULL,

    -- Página do livro à qual a anotação pertence.
    pagina INTEGER NOT NULL,

    -- RN17/RN19: toda anotação pertence a um único usuário (o dono) e a um livro.
    usuario_id INTEGER NOT NULL,
    livro_id INTEGER NOT NULL,

    -- CASCADE: anotação é dado dependente; se o usuário ou o livro forem removidos, ela some junto.
    CONSTRAINT fk_anotacao_usuario FOREIGN KEY (usuario_id)
        REFERENCES tb_usuario (id) ON DELETE CASCADE,
    CONSTRAINT fk_anotacao_livro FOREIGN KEY (livro_id)
        REFERENCES tb_livro (id) ON DELETE CASCADE
);

-- Índice para a consulta mais comum: "minhas anotações deste livro".
CREATE INDEX idx_anotacao_usuario_livro ON tb_anotacao (usuario_id, livro_id);
