-- V4: cria a tabela de livros (RF04 a RF08).
-- O Flyway executa este arquivo uma única vez; depois de aplicado, NÃO edite: crie uma V5.
CREATE TABLE tb_livro (
    -- Chave primária autoincremento (mesmo padrão de tb_autor e tb_genero).
    id SERIAL PRIMARY KEY,

    -- Título obrigatório.
    titulo VARCHAR(255) NOT NULL,

    -- ISBN obrigatório e único: dois livros não podem ter o mesmo ISBN.
    isbn VARCHAR(20) NOT NULL UNIQUE,

    -- Ano de publicação (opcional).
    ano_publicacao INTEGER,

    -- Sinopse em texto longo (opcional).
    sinopse TEXT,

    -- RN14: NOT NULL garante que todo livro tenha exatamente um autor e um gênero.
    -- INTEGER porque tb_autor.id e tb_genero.id_genero são SERIAL (inteiro).
    autor_id INTEGER NOT NULL,
    genero_id INTEGER NOT NULL,

    -- RN22: ON DELETE RESTRICT impede excluir um autor que tenha livros.
    -- O banco lança erro de integridade, que a API vai traduzir para 409 Conflict.
    CONSTRAINT fk_livro_autor FOREIGN KEY (autor_id)
        REFERENCES tb_autor (id) ON DELETE RESTRICT,

    -- RN22: o mesmo vale para o gênero (a coluna referenciada é id_genero).
    CONSTRAINT fk_livro_genero FOREIGN KEY (genero_id)
        REFERENCES tb_genero (id_genero) ON DELETE RESTRICT
);

-- Índices nas FKs: aceleram buscas e a checagem "este autor/gênero tem livros?".
CREATE INDEX idx_livro_autor ON tb_livro (autor_id);
CREATE INDEX idx_livro_genero ON tb_livro (genero_id);
