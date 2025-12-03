#!/bin/bash

# Script para iniciar o servidor com MySQL

echo "=== Iniciando Sistema de Estoque com MySQL ==="

# Cores
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Diretório do projeto
cd "$(dirname "$0")"
PROJECT_DIR=$(pwd)

# Verificar MySQL
echo -e "${YELLOW}Verificando MySQL...${NC}"
if ! lsof -i :3306 > /dev/null 2>&1; then
    echo -e "${RED}❌ MySQL não está rodando na porta 3306!${NC}"
    echo "Inicie o MySQL primeiro:"
    echo "  macOS: brew services start mysql"
    echo "  Linux: sudo systemctl start mysql"
    exit 1
fi
echo -e "${GREEN}✅ MySQL está rodando${NC}"

# Verificar driver MySQL
MYSQL_JAR=""
if [ -f "lib/mysql-connector-j-8.0.33.jar" ]; then
    MYSQL_JAR="lib/mysql-connector-j-8.0.33.jar"
    echo -e "${GREEN}✅ Driver MySQL encontrado${NC}"
else
    echo -e "${YELLOW}⚠️  Driver MySQL não encontrado. Tentando baixar...${NC}"
    mkdir -p lib
    curl -L "https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.0.33/mysql-connector-j-8.0.33.jar" \
         -o lib/mysql-connector-j-8.0.33.jar 2>/dev/null
    
    if [ -f "lib/mysql-connector-j-8.0.33.jar" ]; then
        MYSQL_JAR="lib/mysql-connector-j-8.0.33.jar"
        echo -e "${GREEN}✅ Driver MySQL baixado com sucesso${NC}"
    else
        echo -e "${RED}❌ Erro ao baixar driver MySQL${NC}"
        echo "Por favor, baixe manualmente de:"
        echo "https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.0.33/"
        exit 1
    fi
fi

# Compilar classes se necessário
echo -e "${YELLOW}Compilando classes...${NC}"
javac -d target/classes -cp "target/classes:$MYSQL_JAR" \
    src/config/*.java \
    src/util/*.java \
    src/dao/*.java 2>&1 | grep -v "Note:" || true

# Construir classpath
CLASSPATH="target/classes:$MYSQL_JAR"

# Adicionar todas as classes compiladas
if [ -d "target/classes" ]; then
    CLASSPATH="$CLASSPATH:target/classes"
fi

# Verificar arquivo de configuração
if [ ! -f "database.properties" ]; then
    echo -e "${YELLOW}Copiando database.properties...${NC}"
    cp src/resources/database.properties database.properties 2>/dev/null || true
fi

# Inicializar banco de dados
echo -e "${YELLOW}Inicializando banco de dados...${NC}"
java -cp "$CLASSPATH" util.DatabaseInitializer 2>&1 | head -5

# Iniciar servidor
echo -e "${GREEN}=== Iniciando Servidor na porta 12345 ===${NC}"
echo "Pressione Ctrl+C para parar"
echo ""

java -cp "$CLASSPATH" server.Servidor

