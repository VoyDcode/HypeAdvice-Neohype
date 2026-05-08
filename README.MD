# Hype Advice

Aplicação Java desenvolvida como parte de um teste técnico. O projeto consome a API pública [Advice Slip](https://api.adviceslip.com/) para geração e busca de conselhos, com persistência local em PostgreSQL e interface web em JSF/BootsFaces.

## Stack

- Java 17 (compilação alvo: 1.8)
- Spring Boot 2.7.3 (WAR)
- JoinFaces 4.7 + JSF + BootsFaces 1.5
- Hibernate 5.6 / JPA
- PostgreSQL
- Maven Wrapper
- Lombok
- Unirest (cliente HTTP para API externa)

## Estrutura

```
src/main/java/com/example/hypeadvice/
├── domain/
│   ├── bean/          → Managed Beans JSF (AdviceBean, AdviceListBean)
│   ├── controller/v1/ → REST controllers
│   ├── entity/        → Entidades JPA
│   ├── enums/         → AdviceTypeEnum (GRATUITO, PAGO)
│   ├── exception/     → Exceções customizadas
│   ├── repository/    → Spring Data repositories
│   ├── service/       → Regras de negócio e integração com API externa
│   ├── utils/         → Helpers (Gson, etc.)
│   └── vo/            → Value Objects para resposta da API externa
└── HypeadviceApplication.java

src/main/webapp/
├── advice-crud.xhtml  → Cadastro de conselhos
└── advice-list.xhtml  → Pesquisa de conselhos

exemplos_modelagem/
└── scripts.txt        → Script DDL do exercício 4
```

## Pré-requisitos

- JDK 17
- PostgreSQL 13+ rodando localmente
- pgAdmin (opcional, para administração via interface gráfica)

## Setup do banco

Crie a role e o database. Pelo pgAdmin (Query Tool) ou via psql:

```sql
CREATE ROLE hypeadvice WITH LOGIN PASSWORD 'hypeadvice';
CREATE DATABASE hypeadvice OWNER hypeadvice;
GRANT ALL PRIVILEGES ON DATABASE hypeadvice TO hypeadvice;
```

> **Atenção:** o `application.properties` está apontando para `jdbc:postgresql://localhost:5433/hypeadvice`. Ajuste a porta se o seu PostgreSQL estiver na 5432.

O Hibernate está configurado com `ddl-auto=update`, então a tabela `advice` é criada automaticamente na primeira execução.

## Executando

```bash
./mvnw spring-boot:run
```

No Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

Após o startup, acesse:

- Cadastro de conselhos: http://localhost:8080/advice-crud.xhtml
- Pesquisa de conselhos: http://localhost:8080/advice-list.xhtml
- Swagger UI (documentação dos endpoints REST): http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

## Endpoints REST

| Método | Caminho                | Descrição                          |
|--------|------------------------|------------------------------------|
| GET    | `/advice/v1/listar`    | Lista todos os conselhos cadastrados |
| POST   | `/advice/v1/salvar`    | Cadastra um novo conselho          |

Exemplo de payload para `POST /advice/v1/salvar`:

```json
{
  "nome": "Conselho do dia",
  "descricao": "Beba mais água.",
  "tipo": "GRATUITO"
}
```

## Documentação no código

Todas as classes principais (services, controller, beans, entity, enum, exception) possuem JavaDoc descrevendo responsabilidades e decisões importantes. Pontos não-óbvios estão comentados inline (ex.: tratamento da resposta HTTP 200 com payload de erro da Advice Slip API).

## Resolução dos exercícios

### Exercício 1 — Configuração

Ajustado o dialect do Hibernate para o PostgreSQL e revisada a configuração do `application.properties` (URL, driver, dialect, `ddl-auto`).

### Exercício 2 — Tipo de Conselho

- Criado o enum `AdviceTypeEnum` com os valores `GRATUITO` e `PAGO`.
- Adicionado o campo `tipo` na entidade `Advice` mapeado com `@Enumerated(EnumType.STRING)` e `nullable = false`.
- Campo exibido nas tabelas e formulário de cadastro com validação obrigatória.
- Implementado o endpoint `POST /v1/advice/salvar` para persistir o conselho.

### Exercício 3 — Consumo de API REST

- Implementado `buscarById(Long id)` em `AdvicesLIPService` consumindo `https://api.adviceslip.com/advice/{id}`.
- Tratamento das duas formas de resposta da API: payload de sucesso e payload de erro (`type: notice`).
- Quando o ID não é encontrado, lança `RecursoNaoEncontradoException` com mensagem amigável apresentada na view via `b:messages`.
- A API externa não retorna `date` na busca por ID, então a data atual é injetada antes do parse para manter a coluna preenchida na tabela. Uma alternativa seria criar um `SlipVO` separado sem o campo, mas optei por manter um único VO.

### Exercício 4 — Modelagem de banco

Script disponível em `exemplos_modelagem/scripts.txt` com quatro tabelas:

- `customer` — cadastro de clientes (PK, nome, email único, telefone, timestamps).
- `advice` — conselhos vinculados opcionalmente a um cliente, com check constraint no campo `tipo`.
- `sales` — vendas relacionando cliente e conselho, com valor e data de compra.
- `schedule` — agendamentos por cliente, com UUID, status validado por check e auditoria mínima.

Decisões de modelagem:

- Uso de `BIGSERIAL` para IDs sequenciais.
- Relacionamento opcional entre `advice` e `customer` (`ON DELETE SET NULL`), permitindo conselhos sem dono associado.
- Em `sales`, usei `ON DELETE RESTRICT` para evitar perda de histórico transacional.
- Índices nos campos mais usados em filtro (nome, tipo, FKs, data).
- Campos de auditoria (`created_at`, `updated_at`) em todas as tabelas.

## Arquivos principais alterados/criados

| Arquivo | Descrição |
|---|---|
| `Advice.java` | Entidade com novo campo `tipo` (enum) |
| `AdviceTypeEnum.java` | Enum com GRATUITO e PAGO |
| `RecursoNaoEncontradoException.java` | Exceção customizada para 404 da API externa |
| `AdviceService.java` | CRUD local + busca por ID retornando VO |
| `AdvicesLIPService.java` | Integração com a Advice Slip API |
| `AdviceV1.java` | Endpoint REST `POST /v1/advice/salvar` |
| `AdviceBean.java` | Bean de cadastro com validação do tipo |
| `AdviceListBean.java` | Bean de pesquisa por descrição e por ID |
| `advice-crud.xhtml` | View de cadastro |
| `advice-list.xhtml` | View de pesquisa |
| `application.properties` | Configuração de datasource e Hibernate |
| `exemplos_modelagem/scripts.txt` | Script DDL do exercício 4 |

## Observações

- O projeto roda em Java 17, mas mantém o `source/target = 1.8` definido no `pom.xml` original. Para evitar problemas de CGLIB com o sistema de módulos do Java 17, foram adicionados `--add-opens` em `<jvmArguments>` do `spring-boot-maven-plugin`.
- A escolha por `javax.faces.view.ViewScoped` (CDI) foi proposital — o `javax.faces.bean.ViewScoped` está depreciado.
