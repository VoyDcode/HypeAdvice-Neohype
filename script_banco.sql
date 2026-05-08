-- 1. ALTERAÇÃO NA TABELA ADVICE (Exercício 2 e 4)
-- Adicionando colunas de tipo e preço à tabela de conselhos
ALTER TABLE advice ADD COLUMN tipo VARCHAR(50);
ALTER TABLE advice ADD COLUMN preco DECIMAL(10, 2) DEFAULT 0.00;

-- Criando índice para otimizar busca por tipo
CREATE INDEX idx_advice_tipo ON advice(tipo);

-- ==========================================
-- 2. CRIAÇÃO DA TABELA CLIENTE
-- ==========================================
CREATE TABLE cliente (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    cpf VARCHAR(14) UNIQUE NOT NULL,
    data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Índices para buscas rápidas no checkout
CREATE INDEX idx_cliente_email ON cliente(email);
CREATE INDEX idx_cliente_cpf ON cliente(cpf);

-- ==========================================
-- 3. CRIAÇÃO DA TABELA VENDA
-- ==========================================
CREATE TABLE venda (
    id SERIAL PRIMARY KEY,
    cliente_id INT NOT NULL,
    data_venda TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    valor_total DECIMAL(10, 2) NOT NULL,
    status VARCHAR(50) DEFAULT 'Pendente',
    CONSTRAINT fk_venda_cliente FOREIGN KEY (cliente_id) REFERENCES cliente(id) ON DELETE RESTRICT
);

-- Índice para histórico de compras
CREATE INDEX idx_venda_cliente ON venda(cliente_id);

-- ==========================================
-- 4. CRIAÇÃO DA TABELA VENDA_ITEM (Associação Conselho <-> Venda)
-- ==========================================
CREATE TABLE venda_item (
    venda_id INT NOT NULL,
    advice_id INT NOT NULL,
    preco_unitario DECIMAL(10, 2) NOT NULL,
    PRIMARY KEY (venda_id, advice_id),
    CONSTRAINT fk_venda_item_venda FOREIGN KEY (venda_id) REFERENCES venda(id) ON DELETE CASCADE,
    CONSTRAINT fk_venda_item_advice FOREIGN KEY (advice_id) REFERENCES advice(id) ON DELETE RESTRICT
);

-- ==========================================
-- 5. CRIAÇÃO DA TABELA AGENDAMENTO (Opcional, conforme Exercício 4)
-- ==========================================
CREATE TABLE agendamento (
    id SERIAL PRIMARY KEY,
    cliente_id INT NOT NULL,
    venda_id INT NOT NULL,
    data_agendada TIMESTAMP NOT NULL,
    status VARCHAR(50) DEFAULT 'Agendado',
    CONSTRAINT fk_agendamento_cliente FOREIGN KEY (cliente_id) REFERENCES cliente(id) ON DELETE RESTRICT,
    CONSTRAINT fk_agendamento_venda FOREIGN KEY (venda_id) REFERENCES venda(id) ON DELETE CASCADE
);
