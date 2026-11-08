# 📚 Biblioteca Virtual — API REST

API REST desenvolvida em **Java 17** com **Spring Boot** para gerenciamento de acervo bibliográfico digital, favoritos, anotações privadas de leitura e fluxo de upload, análise e download de livros em PDF.

---

## 👥 Equipe do Projeto e Contribuições

- **José Rodrigues** ([@joserodrigues1](https://github.com/joserodrigues1))
  - Configuração inicial da arquitetura da aplicação, Docker Compose (`PostgreSQL 15`), variáveis de ambiente (`.env`) e migrações de banco de dados com Flyway (`V1` a `V8`).
  - Implementação do tratamento global de erros/exceções HTTP com `@RestControllerAdvice` (`ApiExceptionHandler` e `StandardError`).
  - Desenvolvimento da camada de segurança e autenticação Stateless via Spring Security e Tokens JWT (`Auth0 Java-JWT` com `HMAC256`), controle de acesso por perfil (`ADMINISTRADOR`, `PROFESSOR`, `ALUNO`) e hash de senhas com `BCrypt`.
  - Implementação dos CRUDs de **Usuário**, **Livro** (busca, paginação e integração com Autor/Gênero), **Anotações** privadas de leitura e estrutura de análise de **Uploads de PDF**.
  - Especificação e documentação completa de todas as rotas, esquemas e contratos de erro no **Swagger / OpenAPI 3**.

- **Gabriella Colares Crisóstomo Lima** ([@GabriellaCLima](https://github.com/GabriellaCLima))
  - Implementação do CRUD da entidade **Gênero** literário e regra de exclusão lógica para gêneros vinculados a livros.
  - Implementação da funcionalidade de **Favoritos** do usuário (`FavoritoController` e `FavoritoService`).
  - Implementação do envio de uploads de PDF pelo professor e configuração das permissões de acesso correspondentes.

- **Maurício Mazza** ([@MauricioMazza](https://github.com/MauricioMazza))
  - Implementação do CRUD completo da entidade **Autor** (`Autor`, `AutorRepository`, `AutorService`, `AutorController` e DTOs).
  - Modificação na entidade e no CRUD de **Livro** com a adição do atributo de caminho para o arquivo PDF (`pdfPath`).
  - Implementação da funcionalidade de **download do PDF** do livro (`GET /api/v1/livros/{id}/download`).

---

## 🤖 Registro do Uso de Inteligência Artificial (IA)

Em conformidade com as diretrizes de transparência acadêmica da disciplina, declaramos de forma detalhada como as ferramentas de Inteligência Artificial generativa (ChatGPT, Claude e GitHub Copilot / Antigravity) foram utilizadas ao longo do desenvolvimento:

- **José Rodrigues:** Utilizou IA de forma intensiva como par de programação (*pair programming*) e tutor técnico em todas as etapas sob sua responsabilidade, incluindo:
  - **Infraestrutura e Banco de Dados:** Auxílio na montagem do `docker-compose.yml`, configuração do `application.yaml` com variáveis de ambiente (`.env`) e escrita dos scripts SQL das migrações do Flyway (`V1` a `V8`).
  - **Segurança e Autenticação (JWT):** Apoio na implementação completa da cadeia de filtros do Spring Security (`SecurityConfig`, `SecurityFilter`), geração e validação de tokens JWT com `Auth0 Java-JWT` (`TokenService`) e regras de autorização por perfil (`ADMINISTRADOR`, `PROFESSOR`, `ALUNO`).
  - **Desenvolvimento de Funcionalidades e Tratamento de Erros:** Auxílio na estruturação da arquitetura em camadas (Entities, Repositories, Services, Controllers e `record` DTOs) para os módulos de Usuário, Livro, Anotações e Fluxo de Uploads de PDF, além da criação do handler global de exceções (`@RestControllerAdvice`).
  - **Documentação Swagger:** Geração das anotações do **Swagger / OpenAPI 3** (`@Operation`, `@ApiResponse`, `@Schema`) e estruturação do `README.md`.
  - **Aprendizado e Estudo:** Uso contínuo da IA para explicar linha a linha o funcionamento do código Java/Spring Boot, tirar dúvidas conceituais e validar os fluxos antes de cada commit.
- **Gabriella Colares:** Utilizou a IA como apoio para revisar códigos, corrigir erros, tirar dúvidas sobre regras de negócio (como exclusão lógica de gêneros e relacionamentos de favoritos) e melhorar a organização do trabalho.
- **Maurício Mazza:** Utilizou a IA para auxiliar na implementação da funcionalidade de download de arquivos PDF em `LivroController`/`LivroService` e esclarecer dúvidas na adaptação da entidade `Livro`.

Todas as implementações geradas com auxílio de IA foram testadas, integradas e revisadas pela equipe para garantir o cumprimento dos requisitos funcionais e não funcionais do projeto.

---

## 🚀 Tecnologias Utilizadas

- **Java 17 (LTS)**
- **Spring Boot** (Spring Web MVC, Spring Data JPA, Spring Security, Bean Validation)
- **PostgreSQL 15** (via Docker Compose)
- **Flyway Migration** (controle e versionamento do esquema do banco de dados)
- **Auth0 Java-JWT** (autenticação Stateless e tokens assinados com `HMAC256`)
- **SpringDoc OpenAPI 3 / Swagger UI** (documentação interativa da API)
- **Lombok** e **ModelMapper**
- **Maven** (gerenciador de dependências e build)

---

## ⚙️ Pré-requisitos para Execução

Para executar o projeto na máquina local:
1. **Java JDK 17** (ou superior) instalado e configurado (`JAVA_HOME`).
2. **Docker** e **Docker Compose** instalados (para subir o banco PostgreSQL).
3. **Git**.

---

## 🛠️ Passo a Passo para Rodar o Projeto

### 1. Clonar o repositório
```bash
git clone https://github.com/biblioteca-virtual-api/projeto-biblioteca-virtual.git
cd projeto-biblioteca-virtual/backend
```

### 2. Configurar as Variáveis de Ambiente (`.env`)
Na pasta `backend/`, crie um arquivo chamado `.env` (você pode copiar o `.env.example` como base) com o seguinte conteúdo:

```properties
DB_URL=jdbc:postgresql://localhost:5432/biblioteca_db
DB_USER=admin
DB_PASS=admin
JWT_SECRET=8f4b2e9a1c7d6f3e5a0b9c8d7e6f5a4b3c2d1e0f9a8b7c6d5e4f3a2b1c0d9e8f
```
> **Importante:** O `JWT_SECRET` é obrigatório para a aplicação subir e deve possuir pelo menos 32 caracteres.

### 3. Subir o Banco de Dados PostgreSQL com Docker
Dentro da pasta `backend/` (onde está localizado o `docker-compose.yml`), execute:
```bash
docker compose up -d
```
> O container `biblioteca_postgres` iniciará na porta `5432` com o banco `biblioteca_db`, usuário `admin` e senha `admin`.

### 4. Executar a Aplicação Spring Boot
Ainda dentro da pasta `backend/`, execute via Maven Wrapper:

**No Windows (PowerShell / CMD):**
```powershell
.\mvnw.cmd spring-boot:run
```

**No Linux / macOS:**
```bash
./mvnw spring-boot:run
```

O Flyway executará automaticamente as migrações (`V1` a `V8`) e a API estará disponível em `http://localhost:8080`.

---

## 📖 Documentação Interativa (Swagger UI)

Com a aplicação em execução, acesse no navegador:

👉 **[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)**

Especificação JSON OpenAPI:
👉 **[http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)**

### Como Autenticar e Testar Rotas no Swagger:
1. Acesse `POST /api/v1/auth/login` (ou crie um primeiro usuário caso o banco esteja vazio) e clique em **Try it out**.
2. Informe `email` e `senha` e clique em **Execute**.
3. Copie o valor do campo `token` retornado no JSON de resposta (sem as aspas).
4. No topo da página do Swagger, clique no botão verde **"Authorize"** 🔓.
5. Cole o token no campo **Value**, clique em **Authorize** e depois em **Close**.
6. Pronto! O Swagger enviará o cabeçalho `Authorization: Bearer <token>` automaticamente em todas as requisições.

---

## 🏛️ Arquitetura e Estrutura de Pacotes

```text
backend/src/main/java/br/edu/christus/backend/
├── config/             # Configurações do Swagger OpenAPI e ModelMapper
│   └── doc/            # Anotações reutilizáveis de respostas de erro para o Swagger
├── controller/         # Controladores REST (Auth, Usuario, Livro, Autor, Genero, Favorito, Anotacao, Upload)
├── domain/             # Entidades JPA e Enums (Usuario, Livro, Autor, Genero, Favorito, Anotacao, Upload, TipoUsuario, StatusUpload)
├── dto/                # Records DTO de entrada (Request) e saída (Response) com validações
├── exception/          # Tratamento global de erros (@RestControllerAdvice, ApiExceptionHandler e StandardError)
├── repository/         # Interfaces Spring Data JPA (JpaRepository)
├── security/           # Configuração Spring Security, SecurityFilter, TokenService e CustomUserDetailsService
├── service/            # Camada de regras de negócio e armazenamento de arquivos
└── util/               # Utilitários de validação
```

---

## 🗄️ Migrações de Banco de Dados (Flyway)

Localizadas em `backend/src/main/resources/db/migration/`:
- `V1__create_table_usuario.sql`: Criação da tabela `tb_usuario`.
- `V2__create_table_autor.sql`: Criação da tabela `tb_autor`.
- `V3__create_table_genero.sql`: Criação da tabela `tb_genero`.
- `V4__create_table_livro.sql`: Criação da tabela `tb_livro` com chaves estrangeiras para autor e gênero.
- `V6__create_table_anotacao.sql`: Criação da tabela `tb_anotacao` para notas privadas do leitor.
- `V7__add_edicao_pdf_to_livro.sql`: Adição das colunas `edicao` e `pdf_path` em `tb_livro`.
- `V8__create_table_upload.sql`: Criação da tabela `tb_upload` para o fluxo de submissão e moderação de PDFs.

---

## 🔒 Segurança e Perfis de Acesso (`TipoUsuario`)

A autenticação é **Stateless com Token JWT** e o controle de acesso é dividido em três perfis:
- **`ADMINISTRADOR`**: Acesso total ao gerenciamento de usuários, autores, gêneros, livros e aprovação/rejeição de uploads de PDF.
- **`PROFESSOR`**: Consulta ao acervo, leitura/download de PDFs, favoritos, anotações e submissão de novos livros em PDF (`POST /api/v1/uploads`) para análise.
- **`ALUNO`**: Consulta ao acervo, leitura/download de PDFs, gerenciamento de sua lista de favoritos e de suas anotações privadas por livro.
