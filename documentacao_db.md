# Documentação da Modelagem de Banco de Dados

Para permitir a venda de conselhos, as seguintes alterações e novas entidades foram projetadas no banco de dados.

## Entidades e Seus Papéis

### 1. `advice` (Conselho)
**Descrição:** Armazena os conselhos cadastrados no sistema. Foi alterada para incluir informações financeiras e a classificação do conselho.
*   `id` (PK): Identificador único do conselho.
*   `nome`: Título ou nome resumido do conselho.
*   `descricao`: O texto completo do conselho.
*   `tipo`: Indica se o conselho é "Pago" ou "Gratuito" (Nova coluna do Exercício 2).
*   `preco`: O valor cobrado pelo conselho (se for do tipo "Pago"). Valor padrão é 0.00.

### 2. `cliente` (Cliente)
**Descrição:** Armazena os dados dos clientes que estão comprando os conselhos pagos.
*   `id` (PK): Identificador único do cliente.
*   `nome`: Nome completo do cliente.
*   `email`: Email para contato e envio de comprovantes (campo único).
*   `cpf`: CPF do cliente para emissão de nota fiscal (campo único).
*   `data_cadastro`: Data em que o cliente foi registrado no sistema.

### 3. `venda` (Venda)
**Descrição:** Registra a transação de compra de um ou mais conselhos por um cliente.
*   `id` (PK): Identificador único da transação.
*   `cliente_id` (FK): Referência ao cliente que realizou a compra.
*   `data_venda`: Data e hora em que a compra foi realizada.
*   `valor_total`: Valor total da compra.
*   `status`: Status do pagamento (ex: "Aprovado", "Pendente", "Cancelado").

### 4. `venda_item` (Itens da Venda)
**Descrição:** Tabela associativa (N:N) que detalha quais conselhos foram vendidos em uma transação específica, pois uma venda pode conter múltiplos conselhos.
*   `venda_id` (PK, FK): Referência à venda.
*   `advice_id` (PK, FK): Referência ao conselho vendido.
*   `preco_unitario`: O preço do conselho no momento da venda (garante histórico de preços caso o valor mude depois).

---

## Relacionamentos
*   Um **Cliente** pode fazer várias **Vendas** (1:N).
*   Uma **Venda** pertence a um único **Cliente** (N:1).
*   Uma **Venda** possui vários **Itens de Venda** (1:N).
*   Um **Conselho** pode estar presente em vários **Itens de Venda** (1:N).

## Estratégia de Otimização (Índices)
Foram criados índices para colunas muito utilizadas em buscas:
*   `idx_cliente_email` e `idx_cliente_cpf`: Para buscar clientes rapidamente durante o checkout.
*   `idx_venda_cliente`: Para buscar o histórico de compras de um cliente rapidamente.
*   `idx_advice_tipo`: Para filtrar facilmente entre conselhos gratuitos e pagos na tela de compra.
