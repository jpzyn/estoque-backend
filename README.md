# 🏪 Sistema de Controle de Estoque - Backend Parcial

Este módulo representa a evolução do backend até o estágio em que **Produtos** e **Categorias** já estão disponíveis, enquanto as demais funcionalidades permanecem em desenvolvimento.

## ⚙️ Tecnologias
- Java 17
- Sockets (ServerSocket/Socket)
- Coleções em memória (simples DAO)

## 📊 Status das Funcionalidades
| Módulo         | Situação             | Observação                              |
| -------------- | -------------------- | --------------------------------------- |
| Produtos       | ✅ Implementado       | Cadastro e listagem completos           |
| Categorias     | ✅ Implementado       | Cadastro com Tamanho e Embalagem        |
| Movimentações  | ⏳ Em desenvolvimento | Protocolos respondem com placeholder    |
| Relatórios     | ⏳ Em desenvolvimento | Protocolos respondem com placeholder    |

## 🚀 Como Executar

Compile:
```bash
javac -d target/classes src/model/*.java src/dao/*.java src/server/Servidor.java
```

Execute:
```bash
java -cp target/classes server.Servidor
```

O servidor escuta na porta `12345` e compreende os seguintes comandos:

### Formato `chave=valor`
```
acao=cadastrarProduto;nome=Arroz;categoria=Grãos;estoqueInicial=100;estoqueMinimo=10;estoqueMaximo=200;preco=9.90;unidade=kg
acao=listarProdutos
acao=cadastrarCategoria;nome=Grãos;tamanho=MEDIO;embalagem=LATA
acao=listarCategorias
```

### Formato com `|`
```
PRODUTO_CRIAR|Arroz|Graos|100|10|200|9.90
PRODUTO_LISTAR
CATEGORIA_CRIAR|Graos|MEDIO|LATA
CATEGORIA_LISTAR
```

Demais comandos retornam `FUNCIONALIDADE_EM_DESENVOLVIMENTO`.
