# Decisões técnicas — Hypeadvice

## Exercício 1 – Configuração

A propriedade `hibernate.dialect` no `application.properties` estava errada — o prefixo correto é `spring.jpa.properties.hibernate.dialect`. Sem isso o Hibernate rodava com dialect genérico e gerava SQL incompatível com o PostgreSQL.

Escolhi `PostgreSQL10Dialect` porque é o dialect mais atual disponível no Hibernate 5.6.x (que é o que o Spring Boot 2.7.3 traz). Ele funciona para qualquer versão do PostgreSQL a partir da 10, então não tem problema rodar com versões mais novas.

As credenciais ficaram com fallback para variáveis de ambiente (`${DATABASE_URL}`, etc.) pra não deixar senha hardcoded no código — boa prática independente do ambiente.

---

## Exercício 2 – Tipo de Conselho

Criei o enum `AdviceTypeEnum` com `GRATUITO` e `PAGO` e anotei com `@Enumerated(EnumType.STRING)` na entidade. O motivo de usar STRING em vez do padrão ORDINAL é que o ORDINAL persiste o índice (0, 1) — se eu reordenar os valores do enum depois, quebra tudo no banco. Com STRING fica salvo o nome literal, que é muito mais seguro.

A validação do campo ficou em duas camadas: `required="true"` no selectOneMenu do JSF e `nullable = false` na coluna do banco. Assim se alguém chamar a API diretamente sem passar pelo formulário, o banco também rejeita.

---

## Exercício 3 – Consumo da API

Criei a `RecursoNaoEncontradoException` para quando a API retornar que não encontrou o conselho. Preferi uma exception customizada a lançar `RuntimeException` genérica porque fica mais fácil tratar de forma centralizada depois e a mensagem chega legível pro usuário.

A busca por ID tem um problema na API externa: o endpoint `/advice/{id}` não retorna o campo `date`, só a busca aleatória retorna. Resolvi injetando a data atual no JSON antes de desserializar — funciona porque o `JsonDateDeserializer` já estava configurado no projeto pra esse formato.

Movi as URLs da API pra constantes estáticas na classe de serviço. Pareça pequeno mas evita duplicação e facilita se o endpoint mudar algum dia.

Também substituí o `org.apache.http.HttpStatus` pelo `org.springframework.http.HttpStatus` — a dependência Apache estava sendo importada desnecessariamente, o do Spring já serve.

---

## Exercício 4 – Modelagem

**customer**: tabela do cliente com os dados básicos. Coloquei índice no `nome` por ser campo de busca frequente e `UNIQUE` no `email`.

**advice**: os conselhos do sistema. FK para `customer` é opcional — pode existir conselho sem cliente associado. O `CHECK` em `tipo` repete a validação do enum na camada de banco, o que é bom porque garante integridade mesmo que alguém acesse o banco diretamente.

**sales**: registra as vendas. Usei `NUMERIC(10,2)` pro valor pra não ter problema com arredondamento de ponto flutuante em valores monetários. Índices nas FKs e na data de compra pensando em relatórios.

**schedule**: para agendamentos. Usei UUID como identificador público — assim a API pode expor o ID do agendamento sem revelar a sequência interna do banco. O campo `status` tem CHECK com os valores possíveis e `usuario_criacao` pra auditoria básica.

---

## Melhorias gerais

- `@Autowired` público virou `private` + constructor injection nos services/controller
- `findAll` e leituras em geral ficaram com `@Transactional(readOnly = true)`
- `findById` do `AdviceRepository` foi removido — já existe no `JpaRepository`, não precisa redeclarar
- `@Configuration`, `@ComponentScan` e `@EntityScan` removidas da classe principal — o `@SpringBootApplication` já cobre tudo isso
- `javax.faces.bean.ViewScoped` trocado pelo `javax.faces.view.ViewScoped` correto (CDI)
- `e.printStackTrace()` trocado por SLF4J na classe `Bean`
- `total_results` virou `totalResults` com `@JsonProperty` pra seguir camelCase do Java sem quebrar o contrato com a API
- Removidas dependências `jakarta.persistence-api:3.0.0` e `org.eclipse.persistence:javax.persistence:2.2.1` que conflitavam com o `javax.persistence` do Spring Boot 2.x
