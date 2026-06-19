-- ============================================================
-- Sistema de Farmácia - Microsserviços
-- Script de Inicialização do Banco de Dados MySQL
-- ============================================================

CREATE DATABASE IF NOT EXISTS farmacia_db;
USE farmacia_db;

-- ============ TABELA DE PRODUTOS/MEDICAMENTOS ============
CREATE TABLE IF NOT EXISTS produtos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    descricao TEXT,
    categoria ENUM('MEDICAMENTO', 'HIGIENE', 'COSMETICO', 'OUTRO') NOT NULL DEFAULT 'MEDICAMENTO',
    preco DECIMAL(10,2) NOT NULL,
    estoque INT NOT NULL DEFAULT 0,
    estoque_minimo INT NOT NULL DEFAULT 10,
    controlado BOOLEAN NOT NULL DEFAULT FALSE,
    registro_anvisa VARCHAR(50),
    fabricante VARCHAR(255),
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============ TABELA DE CLIENTES ============
CREATE TABLE IF NOT EXISTS clientes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cpf VARCHAR(14) UNIQUE,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    telefone VARCHAR(20),
    endereco VARCHAR(500),
    cidade VARCHAR(100),
    estado VARCHAR(2),
    cep VARCHAR(10),
    data_nascimento DATE,
    convenio_medico BOOLEAN DEFAULT FALSE,
    nome_convenio VARCHAR(255),
    total_compras INT DEFAULT 0,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============ TABELA DE VENDEDORES ============
CREATE TABLE IF NOT EXISTS vendedores (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    telefone VARCHAR(20),
    percentual_comissao DECIMAL(5,2) NOT NULL DEFAULT 5.00,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============ TABELA DE VENDAS ============
CREATE TABLE IF NOT EXISTS vendas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cliente_id BIGINT,
    vendedor_id BIGINT,
    cpf_nota VARCHAR(14),
    tipo_venda ENUM('BALCAO', 'ONLINE', 'IFOOD') NOT NULL DEFAULT 'BALCAO',
    subtotal DECIMAL(10,2) NOT NULL,
    desconto DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    total DECIMAL(10,2) NOT NULL,
    comissao DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    status ENUM('PENDENTE', 'CONCLUIDA', 'CANCELADA') NOT NULL DEFAULT 'PENDENTE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============ TABELA DE ITENS DA VENDA ============
CREATE TABLE IF NOT EXISTS itens_venda (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    venda_id BIGINT NOT NULL,
    produto_id BIGINT NOT NULL,
    quantidade INT NOT NULL,
    preco_unitario DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (venda_id) REFERENCES vendas(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============ TABELA DE NOTAS FISCAIS ============
CREATE TABLE IF NOT EXISTS notas_fiscais (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    venda_id BIGINT NOT NULL,
    numero_nf VARCHAR(50) NOT NULL,
    chave_acesso VARCHAR(44),
    cpf_destinatario VARCHAR(14),
    valor_total DECIMAL(10,2) NOT NULL,
    status ENUM('EMITIDA', 'AUTORIZADA', 'CANCELADA') NOT NULL DEFAULT 'EMITIDA',
    xml_nfe TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (venda_id) REFERENCES vendas(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============ TABELA DE RECEITAS (Medicamentos Controlados) ============
CREATE TABLE IF NOT EXISTS receitas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    venda_id BIGINT NOT NULL,
    cliente_cpf VARCHAR(14) NOT NULL,
    produto_id BIGINT NOT NULL,
    numero_receita VARCHAR(50),
    crm_medico VARCHAR(20),
    nome_medico VARCHAR(255),
    protocolo_ans VARCHAR(50),
    enviada_ans BOOLEAN DEFAULT FALSE,
    data_envio_ans TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (venda_id) REFERENCES vendas(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============ TABELA DE REGRAS DE DESCONTO ============
CREATE TABLE IF NOT EXISTS regras_desconto (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    descricao TEXT,
    tipo ENUM('PROGRESSIVO', 'CONVENIO', 'FABRICANTE', 'IDOSO') NOT NULL,
    valor_percentual DECIMAL(5,2) NOT NULL,
    min_compras INT DEFAULT 0,
    max_compras INT,
    farmacia_id BIGINT,
    apenas_idosos BOOLEAN DEFAULT FALSE,
    requer_convenio BOOLEAN DEFAULT FALSE,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============ TABELA DE MOVIMENTAÇÕES DE ESTOQUE ============
CREATE TABLE IF NOT EXISTS movimentacoes_estoque (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    produto_id BIGINT NOT NULL,
    tipo ENUM('ENTRADA', 'SAIDA', 'AJUSTE') NOT NULL,
    quantidade INT NOT NULL,
    motivo VARCHAR(255),
    venda_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============ DADOS INICIAIS ============

-- Categorias de produtos já estão no ENUM da tabela

-- Produtos iniciais
INSERT INTO produtos (nome, descricao, categoria, preco, estoque, estoque_minimo, controlado, registro_anvisa, fabricante) VALUES
('Dipirona Sódica 500mg', 'Analgésico e antitérmico', 'MEDICAMENTO', 8.50, 200, 50, FALSE, 'MS-1.0573.0266', 'Medley'),
('Amoxicilina 500mg', 'Antibiótico de amplo espectro', 'MEDICAMENTO', 25.90, 150, 30, FALSE, 'MS-1.0573.0341', 'EMS'),
('Rivotril 2mg', 'Clonazepam - Ansiolítico', 'MEDICAMENTO', 15.80, 80, 20, TRUE, 'MS-1.0100.0025', 'Roche'),
('Ritalina 10mg', 'Metilfenidato - TDAH', 'MEDICAMENTO', 32.50, 50, 15, TRUE, 'MS-1.0100.0218', 'Novartis'),
('Paracetamol 750mg', 'Analgésico e antitérmico', 'MEDICAMENTO', 6.90, 300, 50, FALSE, 'MS-1.0573.0147', 'Genérico'),
('Losartana 50mg', 'Anti-hipertensivo', 'MEDICAMENTO', 12.40, 180, 40, FALSE, 'MS-1.0573.0389', 'Medley'),
('Omeprazol 20mg', 'Protetor gástrico', 'MEDICAMENTO', 9.80, 250, 40, FALSE, 'MS-1.0573.0156', 'EMS'),
('Shampoo Clear Men', 'Shampoo anticaspa masculino 400ml', 'HIGIENE', 28.90, 60, 15, FALSE, NULL, 'Unilever'),
('Creme Nivea Soft', 'Creme hidratante facial 100g', 'COSMETICO', 19.90, 45, 10, FALSE, NULL, 'Nivea'),
('Protetor Solar FPS 50', 'Protetor solar facial 60ml', 'COSMETICO', 45.90, 35, 10, FALSE, NULL, 'La Roche-Posay'),
('Desodorante Rexona', 'Desodorante aerosol 150ml', 'HIGIENE', 15.90, 80, 20, FALSE, NULL, 'Unilever'),
('Escova Dental Colgate', 'Escova dental macia', 'HIGIENE', 8.90, 100, 25, FALSE, NULL, 'Colgate');

-- Clientes iniciais
INSERT INTO clientes (cpf, nome, email, telefone, endereco, cidade, estado, cep, data_nascimento, convenio_medico, nome_convenio, total_compras) VALUES
('123.456.789-09', 'Maria Silva', 'maria@email.com', '(11) 99999-1111', 'Rua das Flores, 123', 'São Paulo', 'SP', '01234-567', '1960-03-15', TRUE, 'Unimed', 25),
('987.654.321-00', 'João Santos', 'joao@email.com', '(11) 98888-2222', 'Av. Paulista, 456', 'São Paulo', 'SP', '01310-100', '1985-07-22', FALSE, NULL, 8),
('456.789.123-45', 'Ana Oliveira', 'ana@email.com', '(21) 97777-3333', 'Rua Copacabana, 789', 'Rio de Janeiro', 'RJ', '22041-080', '1955-11-30', TRUE, 'Amil', 42);

-- Vendedores iniciais
INSERT INTO vendedores (nome, email, telefone, percentual_comissao) VALUES
('Carlos Vendedor', 'carlos@farmacia.com', '(11) 91111-0001', 5.00),
('Patricia Atendente', 'patricia@farmacia.com', '(11) 91111-0002', 4.50),
('Roberto Farmacêutico', 'roberto@farmacia.com', '(11) 91111-0003', 6.00);

-- Regras de desconto iniciais
INSERT INTO regras_desconto (nome, descricao, tipo, valor_percentual, min_compras, max_compras, apenas_idosos, requer_convenio) VALUES
('Bronze', 'Desconto para clientes com 5+ compras', 'PROGRESSIVO', 3.00, 5, 14, FALSE, FALSE),
('Prata', 'Desconto para clientes com 15+ compras', 'PROGRESSIVO', 5.00, 15, 29, FALSE, FALSE),
('Ouro', 'Desconto para clientes com 30+ compras', 'PROGRESSIVO', 8.00, 30, 49, FALSE, FALSE),
('Diamante', 'Desconto para clientes com 50+ compras', 'PROGRESSIVO', 12.00, 50, NULL, FALSE, FALSE),
('Convênio Médico', 'Desconto para idosos com convênio', 'CONVENIO', 10.00, 0, NULL, TRUE, TRUE),
('Desconto Fabricante', 'Desconto do fabricante para idosos', 'FABRICANTE', 8.00, 0, NULL, TRUE, FALSE);
