# 🏪 Sistema de Controle de Estoque - Frontend

Frontend do sistema de controle de estoque desenvolvido em Java utilizando Swing.

## ⚙️ Tecnologias
- Java 17
- Java Swing (Interface Gráfica)
- Socket (para comunicação com o servidor)

## 📦 Funcionalidades
- CRUD de Produtos
- Gerenciamento de Categorias (via backend)
- Unidades de Medida
- Interface com abas para navegação

## 🚀 Como Executar

Compile o projeto:
```bash
javac -d target/classes -cp "target/classes" src/view/MenuPrincipal.java src/controller/EstoqueController.java src/client/ClienteSocket.java src/view/GraficoPanel.java
```

Execute a aplicação:
```bash
java -cp "target/classes" view.MenuPrincipal
```

## 👥 Autores
- João Pedro Nobile dos Santos (RA: 1072411014)
