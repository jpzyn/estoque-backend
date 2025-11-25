# 🧪 Instruções de Teste - Backend

Como testar o backend separadamente do frontend.

## 📋 Pré-requisitos

- Java 17 instalado
- Maven configurado (opcional, mas recomendado)
- NetBeans IDE (ou qualquer IDE Java)

## 🚀 Como Testar

### Opção 1: Usando o Cliente de Teste (Recomendado)

Este é o método mais fácil para testar todas as funcionalidades do backend.

#### Passo 1: Iniciar o Servidor

1. No NetBeans, abra o projeto
2. Encontre o arquivo `src/server/Servidor.java`
3. Clique com botão direito → **Run File** (ou pressione Shift + F6)
4. Você verá no console:
   ```
   Servidor iniciado na porta 12345
   Aguardando conexões...
   ```

#### Passo 2: Executar o Cliente de Teste

1. Com o servidor rodando, encontre o arquivo `src/ClienteTeste.java`
2. Clique com botão direito → **Run File** (ou pressione Shift + F6)
3. O cliente vai:
   - Conectar ao servidor
   - Executar testes de CRUD de Categorias
   - Executar testes de CRUD de Produtos
   - Testar Movimentações (Entrada/Saída)
   - Gerar todos os Relatórios
   - Testar operações de Delete

### Opção 2: Usando o Maven

#### Terminal 1 - Iniciar Servidor:
```bash
mvn exec:java -Dexec.mainClass="Servidor"
```

#### Terminal 2 - Executar Cliente de Teste:
```bash
mvn exec:java -Dexec.mainClass="ClienteTeste"
```

### Opção 3: Usando TestadorEstoque (Teste Local sem Socket)

Se quiser testar apenas as classes model sem o servidor:

1. Execute `src/TestadorEstoque.java`
2. Este testador funciona localmente, sem comunicação socket

## 📡 Protocolo de Comunicação

O servidor aceita comandos no formato:

```
COMANDO|param1|param2|param3...
```

### Comandos Disponíveis:

#### Categorias
- `CATEGORIA_CRIAR|nome|tamanho|embalagem`
  - Exemplo: `CATEGORIA_CRIAR|Limpeza|GRANDE|PLASTICO`
- `CATEGORIA_LISTAR`
- `CATEGORIA_BUSCAR|nome`
- `CATEGORIA_ATUALIZAR|nome|tamanho|embalagem`
- `CATEGORIA_DELETAR|nome`

#### Produtos
- `PRODUTO_CRIAR|nome|preco|unidade|qtdEstoque|qtdMin|qtdMax|categoria`
  - Exemplo: `PRODUTO_CRIAR|Detergente|5.50|Litro|100|20|200|Limpeza`
- `PRODUTO_LISTAR`
- `PRODUTO_BUSCAR|nome`
- `PRODUTO_ATUALIZAR|nome|preco|unidade|qtdEstoque|qtdMin|qtdMax|categoria`
- `PRODUTO_DELETAR|nome`

#### Movimentações
- `MOVIMENTACAO_CRIAR|produto|tipo|quantidade`
  - Exemplo: `MOVIMENTACAO_CRIAR|Detergente|ENTRADA|50`
  - Tipos: `ENTRADA` ou `SAIDA`
- `MOVIMENTACAO_LISTAR`

#### Relatórios
- `RELATORIO_LISTA_PRECOS`
- `RELATORIO_BALANCO`
- `RELATORIO_ABAIXO_MINIMO`
- `RELATORIO_QUANTIDADE_CATEGORIA`
- `RELATORIO_MAIS_MOVIMENTACOES`

### Formato de Resposta:

**Sucesso:**
```
SUCCESS|dados...
```

**Erro:**
```
ERROR|mensagem de erro
```

## 🔍 Testando Manualmente com Telnet (Opcional)

Você pode testar o servidor manualmente usando telnet ou netcat:

```bash
# Conectar ao servidor
telnet localhost 12345

# Ou usando nc (netcat)
nc localhost 12345

# Depois, digite os comandos:
CATEGORIA_LISTAR
PRODUTO_LISTAR
RELATORIO_LISTA_PRECOS
```

## ⚠️ Troubleshooting

### Erro: "Erro ao conectar ao servidor"
- **Solução:** Certifique-se de que o servidor está rodando antes de executar o cliente de teste

### Erro: "Porta já em uso"
- **Solução:** Feche outras instâncias do servidor ou altere a porta no arquivo `Servidor.java`

### Erro de compilação
- **Solução:** Certifique-se de que está usando Java 17 e que todos os arquivos foram compilados

## 📝 Exemplo de Saída Esperada

Quando executar o `ClienteTeste`, você verá:

```
[TESTE 1] Testando CRUD de Categorias
1.1 Criando categorias...
   Resposta: SUCCESS|Categoria criada com sucesso: Limpeza
...

[TESTE 2] Testando CRUD de Produtos
2.1 Criando produtos...
   Resposta: SUCCESS|Produto criado com sucesso: Detergente Neutro
...

[TESTE 3] Testando Movimentações
3.1 Criando entrada de 50 unidades de Detergente...
   Resposta: SUCCESS|Movimentação criada com sucesso. Novo estoque: 150
...

[TESTE 4] Testando Relatórios
4.1 Relatório: Lista de Preços
=== LISTA DE PREÇOS ===
...
```

## ✅ Checklist de Testes

- [ ] Servidor inicia corretamente
- [ ] Cliente conecta ao servidor
- [ ] CRUD de Categorias funciona
- [ ] CRUD de Produtos funciona
- [ ] Movimentações de entrada funcionam
- [ ] Movimentações de saída funcionam
- [ ] Validações de estoque funcionam
- [ ] Todos os relatórios funcionam
- [ ] Delete funciona corretamente

