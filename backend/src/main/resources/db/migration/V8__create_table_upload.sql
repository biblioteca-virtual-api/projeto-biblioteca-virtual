-- V8: cria a tabela de uploads de livro enviados por professores (RF30 a RF44).
-- Não crie migrations com versão menor que a última aplicada: o Flyway recusa depois de aplicar uma maior.
-- Não edite este arquivo depois de aplicado (nem comentários): o Flyway compara o checksum e trava a subida.
CREATE TABLE tb_upload (
    id SERIAL PRIMARY KEY,

    titulo VARCHAR(255) NOT NULL,

    -- RF43: o autor vem como texto, porque pode ainda não estar cadastrado.
    -- Na aprovação, o administrador escolhe um Autor existente ou cadastra um novo (RF44).
    autor VARCHAR(255) NOT NULL,

    -- RN12: o gênero precisa estar ativo (validado no service).
    genero_id INTEGER NOT NULL,

    edicao VARCHAR(50) NOT NULL,
    ano_publicacao INTEGER NOT NULL,

    -- Referência (caminho/URL) do PDF enviado. O envio do arquivo e a validação ficam na A11.
    pdf VARCHAR(500) NOT NULL,

    -- Professor que enviou o upload.
    professor_id INTEGER NOT NULL,

    -- RN04: todo upload nasce PENDENTE.
    status VARCHAR(20) NOT NULL DEFAULT 'PENDENTE',

    -- RN06: obrigatório quando o upload é REJEITADO (o CHECK abaixo garante também no banco).
    motivo_rejeicao VARCHAR(500),

    -- RF41: livro criado na aprovação. UNIQUE: cada livro vem de no máximo um upload (RN10).
    livro_id INTEGER UNIQUE,

    CONSTRAINT ck_upload_status CHECK (status IN ('PENDENTE', 'REJEITADO', 'APROVADO')),

    -- RN06: upload REJEITADO precisa de um motivo com texto (vazio ou só espaços não vale).
    CONSTRAINT ck_upload_motivo_rejeicao CHECK (
        status <> 'REJEITADO' OR (motivo_rejeicao IS NOT NULL AND btrim(motivo_rejeicao) <> '')),

    -- RF41: só upload APROVADO aponta para um livro. O livro_id pode ficar nulo depois (RN23).
    CONSTRAINT ck_upload_livro_aprovado CHECK (livro_id IS NULL OR status = 'APROVADO'),

    -- RESTRICT: não deixa apagar um gênero ou um professor que tenha uploads (o upload é histórico, RN09).
    CONSTRAINT fk_upload_genero FOREIGN KEY (genero_id)
        REFERENCES tb_genero (id_genero) ON DELETE RESTRICT,
    CONSTRAINT fk_upload_professor FOREIGN KEY (professor_id)
        REFERENCES tb_usuario (id) ON DELETE RESTRICT,

    -- RN23/RF42: SET NULL mantém o upload aprovado como histórico se o livro for excluído.
    CONSTRAINT fk_upload_livro FOREIGN KEY (livro_id)
        REFERENCES tb_livro (id) ON DELETE SET NULL
);

-- Índices: a fila do administrador filtra por status (RF36), o professor consulta os seus (RF31)
-- e a exclusão de gênero verifica se ele tem uploads (RN25).
CREATE INDEX idx_upload_status ON tb_upload (status);
CREATE INDEX idx_upload_professor ON tb_upload (professor_id);
CREATE INDEX idx_upload_genero ON tb_upload (genero_id);
