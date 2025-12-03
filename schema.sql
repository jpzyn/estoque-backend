-- Script SQL para criação das tabelas do Sistema de Estoque
-- Execute este script após criar o banco de dados

CREATE DATABASE IF NOT EXISTS estoque CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE estoque;

-- Tabela de Categorias
CREATE TABLE IF NOT EXISTS categorias (
    nome VARCHAR(100) PRIMARY KEY,
    tamanho ENUM('PEQUENO', 'MEDIO', 'GRANDE') NOT NULL,
    embalagem ENUM('LATA', 'VIDRO', 'PLASTICO') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabela de Produtos
CREATE TABLE IF NOT EXISTS produtos (
    nome VARCHAR(100) PRIMARY KEY,
    preco DECIMAL(10, 2) NOT NULL,
    unidade VARCHAR(20) NOT NULL,
    estoque_atual INT NOT NULL DEFAULT 0,
    estoque_minimo INT NOT NULL DEFAULT 0,
    estoque_maximo INT NOT NULL DEFAULT 0,
    categoria_nome VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (categoria_nome) REFERENCES categorias(nome) ON DELETE RESTRICT ON UPDATE CASCADE,
    INDEX idx_categoria (categoria_nome)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabela de Movimentações
CREATE TABLE IF NOT EXISTS movimentacoes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    produto_nome VARCHAR(100) NOT NULL,
    tipo ENUM('ENTRADA', 'SAIDA') NOT NULL,
    quantidade INT NOT NULL,
    observacao TEXT,
    data_movimentacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (produto_nome) REFERENCES produtos(nome) ON DELETE RESTRICT ON UPDATE CASCADE,
    INDEX idx_produto (produto_nome),
    INDEX idx_tipo (tipo),
    INDEX idx_data (data_movimentacao)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

