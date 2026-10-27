-- V7: adiciona edição e PDF ao livro (levantamento, seção 8).
-- São necessários para copiar os dados do upload aprovado para o novo livro (RF40).
-- Colunas opcionais no banco: os livros já cadastrados não têm esses dados.
-- Não edite este arquivo depois de aplicado (nem comentários): o Flyway compara o checksum e trava a subida.
ALTER TABLE tb_livro ADD COLUMN edicao VARCHAR(50);

-- Referência (caminho/URL) do PDF usado na leitura online.
ALTER TABLE tb_livro ADD COLUMN pdf VARCHAR(500);
