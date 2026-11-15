# Raízes do Nordeste - API RESTful Multicanal (nordeste-api)

API Back-end desenvolvida em **Java com Spring Boot** e **PostgreSQL** para gestão integrada de pedidos multicanal (`APP`, `TOTEM`, `BALCAO`, `PICKUP`, `WEB`), controle de estoque por unidade, processamento de pagamentos (mock), programa de fidelidade e conformidade com a LGPD para a rede de restaurantes **Raízes do Nordeste**.

---

## Tecnologias Utilizadas

* **Linguagem:** Java 17+
* **Framework:** Spring Boot (Spring Web, Spring Data JPA, Spring Validation, Spring Security)
* **Banco de Dados:** PostgreSQL 14+ (Hibernate ORM)
* **Documentação Interativa:** Springdoc OpenAPI 3.1 (Swagger UI v2.8.5)
* **Gerenciador de Dependências:** Apache Maven
* **Testes de Integração:** Postman

---

## Arquitetura do Projeto

O sistema foi estruturado seguindo a separação de responsabilidades em camadas dentro do pacote base `com.raizes.nordeste_api`:

* **`config`**: Configuração de segurança HTTP (`SecurityConfig`) e carga inicial automática do usuário administrador (`DataInitializer`).
* **`controller`**: Camada RESTful responsável pela exposição dos endpoints da API (`UsuarioController`, `UnidadeController`, `ProdutoController`, `EstoqueController`, `PedidoController`, `PagamentoController`).
* **`domain`**: Entidades de persistência JPA (`Usuario`, `Unidade`, `Produto`, `Estoque`, `Pedido`, `ItemPedido`, `Pagamento`) e tipos enumerados (`PerfilUsuario`, `CanalPedido`, `StatusPedido`, `StatusPagamento`).
* **`dto`**: Objetos de transferência de dados para entrada e validação das requisições (`PedidoRequestDTO`, `PagamentoRequestDTO`, `EstoqueMovimentacaoDTO`).
* **`repository`**: Interfaces de acesso a dados estendendo `JpaRepository` com queries derivadas para filtros multicanal e validação de estoque.
* **`exception`**: Interceptador global (`GlobalExceptionHandler` com `@RestControllerAdvice`) para formatação padronizada de erros HTTP (`400`, `404`, `409`).

---

## Requisitos de Segurança, Auditoria e LGPD

1. **Proteção de Dados Sensíveis (LGPD):** O atributo `senha` na entidade `Usuario` utiliza `@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)`, permitindo o envio no cadastro (`POST`), mas impedindo sua exposição em qualquer resposta JSON da API.
2. **Consentimento para Fidelização:** O acúmulo de pontos no programa de fidelidade (`pontosFidelidade`) só é processado após a aprovação do pagamento se o cliente tiver registrado consentimento explícito (`aceiteLgpd = true`).
3. **Logs de Auditoria:** Operações críticas (criação de pedidos multicanal e mudanças de status na cozinha) geram registros estruturados no console através do logger `AUDITORIA_RAIZES`.
4. **Integridade de Estoque:** A tabela `ESTOQUES` aplica restrição única composta (`unidade_id`, `produto_id`), e a criação de pedidos valida e deduz o saldo da unidade em tempo real, bloqueando vendas sem saldo com status `409 Conflict`.

---

## Como Executar o Projeto Localmente

### 1. Pré-requisitos
* Java JDK 17 ou superior instalado.
* PostgreSQL ativo na porta `5432`.

### 2. Configuração do Banco de Dados
No terminal do PostgreSQL (`psql`), crie o banco de dados:

    CREATE DATABASE raizes_nordeste;

Verifique as credenciais configuradas em `src/main/resources/application.properties`:

    spring.datasource.url=jdbc:postgresql://localhost:5432/raizes_nordeste
    spring.datasource.username=postgres
    spring.datasource.password=sua_senha
    spring.jpa.hibernate.ddl-auto=update
    spring.jpa.show-sql=true

### 3. Inicialização da Aplicação
Na raiz do projeto, execute o Maven Wrapper via terminal:

    ./mvnw spring-boot:run

Ao iniciar pela primeira vez, a classe `DataInitializer` criará automaticamente o usuário `GERENTE` padrão (`gerente@raizesnordeste.com`).

---

## Documentação Interativa (Swagger / OpenAPI)

Com a aplicação em execução, acesse no navegador:

* **Swagger UI:** http://localhost:8080/swagger-ui/index.html
* **Especificação OpenAPI (JSON):** http://localhost:8080/v3/api-docs

---

## Guia de Endpoints e Exemplos de Payloads

### 1. Usuários (`/api/usuarios`)
* **`GET /api/usuarios`** — Lista todos os usuários cadastrados.
* **`POST /api/usuarios`** — Cadastra um novo usuário (`GERENTE`, `BALCONISTA`, `COZINHEIRO`, `CLIENTE`).

    {
      "nomeCompleto": "Maria Silva",
      "email": "maria.silva@email.com",
      "senha": "senhaSegura123",
      "aceiteLgpd": true,
      "perfil": "CLIENTE"
    }

### 2. Unidades (`/api/unidades`)
* **`GET /api/unidades`** — Lista as unidades da rede.
* **`POST /api/unidades`** — Cadastra uma nova loja/unidade.

    {
      "nome": "Unidade Matriz Recife",
      "cidade": "Recife - PE",
      "cozinhaCompleta": true
    }

### 3. Produtos (`/api/produtos`)
* **`GET /api/produtos`** — Lista todos os itens do cardápio.
* **`POST /api/produtos`** — Cadastra um novo item no cardápio.

    {
      "nome": "Tapioca Recheada de Carne Seca",
      "descricao": "Tapioca tradicional com carne seca desfiada e queijo coalho",
      "preco": 24.90,
      "disponivel": true
    }

### 4. Estoque (`/api/estoque`)
* **`GET /api/estoque?unidadeId=1`** — Consulta o estoque disponível (geral ou filtrado por unidade).
* **`POST /api/estoque/movimentar`** — Adiciona ou remove saldo de estoque (valores positivos para entrada, negativos para saída).

    {
      "unidadeId": 1,
      "produtoId": 1,
      "quantidade": 50
    }

### 5. Pedidos Multicanal (`/api/pedidos`)
* **`POST /api/pedidos`** — Registra um novo pedido, valida disponibilidade, desconta do estoque da unidade informada e calcula o valor total.

    {
      "canalPedido": "APP",
      "clienteId": 1,
      "unidadeId": 1,
      "formaPagamento": "PIX",
      "itens": [
        {
          "produtoId": 1,
          "quantidade": 2
        }
      ]
    }

* **`GET /api/pedidos?canalPedido=APP&status=PRONTO`** — Consulta pedidos com filtros opcionais de canal e status.
* **`PATCH /api/pedidos/{id}/status`** — Atualiza o status do pedido (`AGUARDANDO_PAGAMENTO`, `EM_PREPARO`, `PRONTO`, `ENTREGUE`, `CANCELADO`).

    {
      "status": "PRONTO"
    }

### 6. Pagamentos Mock (`/api/pagamentos`)
* **`GET /api/pagamentos`** — Lista todos os pagamentos processados.
* **`POST /api/pagamentos/mock`** — Simula a aprovação (`true`) ou recusa (`false`) do pagamento de um pedido. Quando aprovado, altera o status do pedido para `EM_PREPARO` e credita os pontos de fidelidade ao cliente.

    {
      "pedidoId": 2,
      "simularAprovacao": true
    }

---

## Padrão de Tratamento de Erros

Todas as falhas de validação (`400 Bad Request`), recursos inexistentes (`404 Not Found`) e conflitos de regra de negócio (`409 Conflict`) retornam um payload padronizado:

### Exemplo de Conflito de Estoque (`409 Conflict`)

    {
      "error": "409 CONFLICT",
      "message": "Estoque insuficiente para o produto 'Tapioca Recheada de Carne Seca'. Disponível: 48",
      "details": [],
      "timestamp": "2026-11-16T23:44:38.573351013Z",
      "path": "/api/pedidos"
    }

### Exemplo de Erro de Validação (`400 Bad Request`)

    {
      "error": "ERRO_VALIDACAO",
      "message": "Um ou mais campos estão inválidos.",
      "details": [
        {
          "field": "unidadeId",
          "issue": "O unidadeId é obrigatório"
        },
        {
          "field": "itens",
          "issue": "O pedido deve conter pelo menos um item"
        }
      ],
      "timestamp": "2026-11-16T23:45:10.351679363Z",
      "path": "/api/pedidos"
    }

---

## Autor

* **Matheus Henrique Bonin** — Projeto Acadêmico de Arquitetura e Desenvolvimento Back-end (UNINTER).