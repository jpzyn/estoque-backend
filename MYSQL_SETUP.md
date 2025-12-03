# 🗄️ Configuração do MySQL

Este documento explica como configurar o MySQL para o Sistema de Controle de Estoque.

## 📋 **Pré-requisitos**

1. MySQL Server instalado (versão 8.0 ou superior)
2. Java 17 ou superior
3. Maven (opcional, mas recomendado)

## 🔧 **Instalação do MySQL**

### **macOS (Homebrew)**
```bash
brew install mysql
brew services start mysql
```

### **Linux (Ubuntu/Debian)**
```bash
sudo apt update
sudo apt install mysql-server
sudo systemctl start mysql
```

### **Windows**
1. Baixe o MySQL Installer: https://dev.mysql.com/downloads/installer/
2. Execute o instalador e siga as instruções
3. Configure senha do usuário `root`

## ⚙️ **Configuração**

### **1. Criar o banco de dados (opcional - será criado automaticamente)**

O sistema criará o banco automaticamente, mas você pode criar manualmente:

```sql
CREATE DATABASE estoque CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### **2. Configurar arquivo de propriedades**

Edite o arquivo `src/resources/database.properties` ou crie um arquivo `database.properties` na raiz do projeto:

```properties
# Configurações do Banco de Dados MySQL
db.host=localhost
db.port=3306
db.database=estoque
db.username=root
db.password=sua_senha_aqui
```

**Importante:** Se não tiver senha, deixe `db.password=` vazio.

### **3. Verificar conexão**

Execute o MySQL:
```bash
mysql -u root -p
```

Digite sua senha quando solicitado.

## 🚀 **Inicialização**

### **Opção 1: Automática (Recomendado)**

O sistema criará automaticamente as tabelas na primeira execução. Basta iniciar o servidor:

```bash
cd estoque-backend
java -cp target/classes server.Servidor
```

### **Opção 2: Manual**

Se preferir criar as tabelas manualmente, execute o script SQL:

```bash
mysql -u root -p estoque < src/resources/schema.sql
```

Ou abra o MySQL e execute:
```sql
USE estoque;
SOURCE src/resources/schema.sql;
```

## 🔍 **Verificação**

Para verificar se tudo está funcionando:

```sql
USE estoque;
SHOW TABLES;
```

Você deve ver:
- `categorias`
- `produtos`
- `movimentacoes`

## 🐛 **Solução de Problemas**

### **Erro: "Access denied for user"**
- Verifique se o usuário e senha estão corretos no `database.properties`
- Certifique-se de que o MySQL está rodando: `mysql -u root -p`

### **Erro: "Unknown database 'estoque'"**
- O sistema criará automaticamente. Se persistir, crie manualmente:
```sql
CREATE DATABASE estoque CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### **Erro: "Table doesn't exist"**
- Execute o script de inicialização ou deixe o sistema criar automaticamente
- Verifique se o arquivo `schema.sql` existe em `src/resources/`

### **Erro: "Connection refused"**
- Verifique se o MySQL está rodando:
  - macOS: `brew services list`
  - Linux: `sudo systemctl status mysql`
  - Windows: Verifique nos Serviços do Windows

### **Porta 3306 ocupada**
- Verifique qual processo está usando: `lsof -i :3306` (macOS/Linux)
- Altere a porta no `database.properties` se necessário

## 📝 **Estrutura do Banco**

### **Tabela: categorias**
- `nome` (VARCHAR, PK)
- `tamanho` (ENUM: PEQUENO, MEDIO, GRANDE)
- `embalagem` (ENUM: LATA, VIDRO, PLASTICO)
- `created_at`, `updated_at` (TIMESTAMP)

### **Tabela: produtos**
- `nome` (VARCHAR, PK)
- `preco` (DECIMAL)
- `unidade` (VARCHAR)
- `estoque_atual`, `estoque_minimo`, `estoque_maximo` (INT)
- `categoria_nome` (FK para categorias)
- `created_at`, `updated_at` (TIMESTAMP)

### **Tabela: movimentacoes**
- `id` (INT, PK, AUTO_INCREMENT)
- `produto_nome` (FK para produtos)
- `tipo` (ENUM: ENTRADA, SAIDA)
- `quantidade` (INT)
- `observacao` (TEXT)
- `data_movimentacao` (TIMESTAMP)

## 🔐 **Segurança**

Para produção, recomenda-se:
1. Criar um usuário específico (não usar `root`)
2. Dar apenas permissões necessárias
3. Usar senha forte
4. Habilitar SSL nas conexões

Exemplo:
```sql
CREATE USER 'estoque_user'@'localhost' IDENTIFIED BY 'senha_segura';
GRANT SELECT, INSERT, UPDATE, DELETE ON estoque.* TO 'estoque_user'@'localhost';
FLUSH PRIVILEGES;
```

Depois atualize o `database.properties`:
```properties
db.username=estoque_user
db.password=senha_segura
```

## 📚 **Referências**

- [Documentação MySQL](https://dev.mysql.com/doc/)
- [MySQL Connector/J](https://dev.mysql.com/doc/connector-j/8.0/en/)

