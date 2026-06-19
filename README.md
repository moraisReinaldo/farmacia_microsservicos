# 💊 Sistema Corporativo de Farmácia - Microsserviços

Sistema corporativo para rede de farmácias desenvolvido em arquitetura de microsserviços com **Spring Boot 3.2** e **MySQL 8.0**.

## 📋 Visão Geral

O sistema gerencia operações de uma rede de farmácias, incluindo:
- **Cadastro** de medicamentos (controlados e não controlados) e produtos de higiene/cosméticos
- **Vendas** no balcão, online e via iFood com comissão de vendedores
- **Emissão de NF-e** com CPF opcional (integração SEFAZ simulada)
- **Controle de receitas** de medicamentos controlados (integração ANS simulada)
- **Descontos progressivos** e bonificações para clientes cadastrados
- **Descontos especiais** para idosos com convênio médico
- **Relatórios gerenciais** completos

## 🏗️ Arquitetura

### Microsserviços

| Serviço | Porta | Descrição |
|---------|-------|-----------|
| `discovery-server` | 8761 | Eureka Server - Service Discovery |
| `api-gateway` | 8080 | Spring Cloud Gateway - Roteamento |
| `produto-service` | 8081 | CRUD de medicamentos e produtos |
| `cliente-service` | 8082 | Cadastro de clientes e convênios |
| `venda-service` | 8083 | Processamento de vendas e comissões |
| `estoque-service` | 8084 | Controle de estoque |
| `nota-fiscal-service` | 8085 | Emissão de NF-e / SEFAZ |
| `receita-service` | 8086 | Receitas controladas / ANS |
| `desconto-service` | 8087 | Motor de regras de desconto |
| `relatorio-service` | 8088 | Relatórios gerenciais |

### Biblioteca Compartilhada
- `farmacia-common` — Componentes reutilizáveis (ApiResponse, validadores, exception handler)

### Duas Versões

- **V1**: Comunicação síncrona via REST (RestTemplate)
- **V2**: Comunicação assíncrona via RabbitMQ (ativar com `--spring.profiles.active=v2`)

## 🚀 Como Executar

### Pré-requisitos
- Java 17+
- Maven 3.8+
- Docker e Docker Compose (para MySQL e RabbitMQ)

### 1. Subir infraestrutura
```bash
docker-compose up -d
```

### 2. Compilar todos os módulos
```bash
mvn clean install -DskipTests
```

### 3. Iniciar os serviços (na ordem)
```bash
# 1º - Discovery Server
cd discovery-server && mvn spring-boot:run

# 2º - API Gateway
cd api-gateway && mvn spring-boot:run

# 3º - Microsserviços (em terminais separados)
cd produto-service && mvn spring-boot:run
cd cliente-service && mvn spring-boot:run
cd estoque-service && mvn spring-boot:run
cd nota-fiscal-service && mvn spring-boot:run
cd receita-service && mvn spring-boot:run
cd desconto-service && mvn spring-boot:run
cd venda-service && mvn spring-boot:run
cd relatorio-service && mvn spring-boot:run
```

### Para rodar a V2 (com RabbitMQ)
```bash
cd venda-service && mvn spring-boot:run -Dspring-boot.run.profiles=v2
cd estoque-service && mvn spring-boot:run -Dspring-boot.run.profiles=v2
cd nota-fiscal-service && mvn spring-boot:run -Dspring-boot.run.profiles=v2
cd receita-service && mvn spring-boot:run -Dspring-boot.run.profiles=v2
```

## 📡 Endpoints Principais (via API Gateway :8080)

### Produtos
```
GET    /api/produtos              - Listar produtos
POST   /api/produtos              - Cadastrar produto
GET    /api/produtos/{id}         - Buscar por ID
GET    /api/produtos/controlados  - Listar controlados
GET    /api/produtos/categoria/{cat} - Filtrar por categoria
```

### Clientes
```
GET    /api/clientes              - Listar clientes
POST   /api/clientes              - Cadastrar cliente
GET    /api/clientes/cpf/{cpf}    - Buscar por CPF
```

### Vendas
```
POST   /api/vendas                - Registrar venda (balcão/online)
POST   /api/vendas/ifood          - Receber pedido iFood
GET    /api/vendas                - Listar vendas
GET    /api/vendas/periodo        - Vendas por período
```

### Nota Fiscal
```
POST   /api/notas-fiscais         - Emitir NF-e
GET    /api/notas-fiscais/venda/{id} - NF-e por venda
```

### Receitas
```
POST   /api/receitas              - Registrar receita
POST   /api/receitas/enviar-ans   - Enviar lote para ANS
```

### Descontos
```
POST   /api/descontos/calcular    - Calcular desconto
GET    /api/descontos/regras      - Listar regras
POST   /api/descontos/regras      - Criar regra
```

### Relatórios
```
GET    /api/relatorios/vendas?inicio=&fim=           - Vendas por período
GET    /api/relatorios/produtos-mais-vendidos?inicio=&fim= - Ranking de produtos
GET    /api/relatorios/estoque                        - Situação do estoque
GET    /api/relatorios/comissoes?inicio=&fim=         - Comissões dos vendedores
```

## 🔧 Tecnologias

- **Spring Boot 3.2.5** — Framework principal
- **Spring Cloud** — Gateway, Eureka
- **Spring Data JPA** — Persistência
- **MySQL 8.0** — Banco de dados
- **RabbitMQ 3.12** — Mensageria (V2)
- **Lombok** — Redução de boilerplate
- **Docker Compose** — Infraestrutura

## 👥 Equipe

Projeto acadêmico desenvolvido para a disciplina de Engenharia de Software.
