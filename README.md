# Portal Esporte Clube Pelotas - Backend Java (Spring Boot 3 + PostgreSQL)

API RESTful oficial do **Esporte Clube Pelotas** (Áureo-Cerúleo) desenvolvida em **Java 17 / 21** e **Spring Boot 3**, conectada ao banco de dados relacional **PostgreSQL (Neon Serverless)**.

---

## 🛠️ Stack Tecnológica

- **Linguagem:** Java 17 LTS (Compatível com Java 21)
- **Framework:** Spring Boot 3.4.1
- **Segurança:** Spring Security 6 + JJWT (0.12.6) com RBAC nativo e BCrypt
- **Documentação:** SpringDoc OpenAPI 3 + Swagger UI (v2.8.3)
- **Persistência:** Spring Data JPA + Hibernate 6 com suporte a JSONB
- **Banco de Dados:** PostgreSQL 17 (Neon Serverless)
- **Uploads:** Firebase Admin SDK / AWS S3
- **Container & Nuvem:** Docker multi-stage build no Render

---

## 🌐 Endpoints Locais e Swagger UI

| Recurso | URL Local (Porta 7997) | URL Local Padrão (Porta 7998) | Descrição |
| :--- | :--- | :--- | :--- |
| **Swagger UI Interativo** | [`http://localhost:7997/swagger-ui.html`](http://localhost:7997/swagger-ui.html) | [`http://localhost:7998/swagger-ui.html`](http://localhost:7998/swagger-ui.html) | Interface gráfica interativa para explorar e testar todas as rotas da API |
| **OpenAPI Docs (JSON)** | [`http://localhost:7997/v3/api-docs`](http://localhost:7997/v3/api-docs) | [`http://localhost:7998/v3/api-docs`](http://localhost:7998/v3/api-docs) | Especificação OpenAPI 3 em formato JSON |
| **Health Check** | [`http://localhost:7997/health`](http://localhost:7997/health) | [`http://localhost:7998/health`](http://localhost:7998/health) | Status de saúde da aplicação (`{"status":"UP"}`) |
| **Tema Ativo** | [`http://localhost:7997/themes/active`](http://localhost:7997/themes/active) | [`http://localhost:7998/themes/active`](http://localhost:7998/themes/active) | Paleta de cores oficial (Áureo-Cerúleo / Campanhas sazonais) |
| **Configurações Globais** | [`http://localhost:7997/settings`](http://localhost:7997/settings) | [`http://localhost:7998/settings`](http://localhost:7998/settings) | Dados institucionais, redes sociais, telefones e Estádio Boca do Lobo |

---

## 👥 Usuários de Teste no Banco de Dados (Database Seeder)

O sistema popula automaticamente uma conta de teste para cada perfil (Role) do clube na primeira inicialização:

> 🔑 **Senha padrão para todas as contas:** `admin123`

| Role | Perfil | E-mail | CPF | Permissões / Escopo |
| :--- | :--- | :--- | :--- | :--- |
| **OWNER** | Proprietário Master | `admin@esporteclubepelotas.com.br` | `00000000001` | Controle irrestrito e total (`*`) |
| **ADMIN** | Gestor Administrativo | `gestor@esporteclubepelotas.com.br` | `00000000002` | Conteúdo, configurações, temas e torcedores |
| **EDITOR** | Editor de Comunicação | `editor@esporteclubepelotas.com.br` | `00000000003` | Matérias, notícias e galeria de ídolos |
| **SOCIO** | Sócio Áureo-Cerúleo | `socio@esporteclubepelotas.com.br` | `00000000004` | Benefícios exclusivos do quadro social |
| **USER** | Torcedor do Lobo | `torcedor@esporteclubepelotas.com.br` | `00000000005` | Perfil público, comentários e reações |

---

## 🚀 Como Executar Localmente

### Opção 1: Via Docker Compose (Recomendado)

Suba o PostgreSQL 17 local junto com a aplicação compilada:

**Caso a porta 7998 já esteja ocupada pelo NestJS antigo (usando porta 7997):**
```powershell
$env:APP_PORT=7997; docker compose up -d --build
```

**Caso a porta 7998 esteja livre:**
```powershell
docker compose up -d --build
```

Para verificar os logs do backend:
```powershell
docker logs -f ecpelotas_backend
```

Para parar os containers:
```powershell
docker compose down
```

---

### Opção 2: Standalone via Maven Wrapper

Para rodar diretamente na sua máquina conectando em memória (H2) ou apontando para o PostgreSQL:

**No Windows (PowerShell):**
```powershell
.\mvnw.cmd spring-boot:run
```

**Executar os Testes Automatizados:**
```powershell
.\mvnw.cmd test
```

---

## ☁️ Ambientes em Nuvem (Render + Neon)

| Ambiente | Branch | URL da API | Status |
| :--- | :--- | :--- | :--- |
| **Produção** | `main` | [`https://esporteclubepelotas-back-java.onrender.com`](https://esporteclubepelotas-back-java.onrender.com) | 🟢 Live |
| **Desenvolvimento** | `develop` | [`https://esporteclubepelotas-back-java-dev.onrender.com`](https://esporteclubepelotas-back-java-dev.onrender.com) | 🟢 Live |

---

## 🔒 Regra de Identidade e Rivalidade (Regra 4)
O Esporte Clube Pelotas tem como cores tradicionais o **Azul e Amarelo (Áureo-Cerúleo)** e rivalidade histórica com o GE Brasil (Xavante). É **terminantemente proibido** o uso da cor vermelha em qualquer resposta visual ou tema. Para estados de erro, perigo e alerta crítico, o sistema utiliza o token **Terracota Alerta** (`#ea580c`).
