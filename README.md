# Portal Esporte Clube Pelotas - Backend Java (Spring Boot 3 + PostgreSQL)

API RESTful oficial do **Esporte Clube Pelotas** (Áureo-Cerúleo) desenvolvida em **Java 17 / 21** e **Spring Boot 3**, conectada ao banco de dados relacional **PostgreSQL (Neon)**.

---

## 🛠️ Stack Tecnológica

- **Linguagem:** Java 17 LTS (Compatível com Java 21)
- **Framework:** Spring Boot 3.4.x
- **Persistência:** Spring Data JPA + Hibernate
- **Banco de Dados:** PostgreSQL (Neon Serverless)
- **Validação:** Jakarta Bean Validation (`@Valid`, `@NotNull`, etc.)
- **Produtividade:** Project Lombok
- **Deploy & Container:** Docker (Multi-stage build) no Render

---

## 🚀 Como Executar Localmente

### 1. Pré-requisitos
- **Java 17** ou superior instalado (`java -version`).
- Não é necessário ter o Maven instalado globalmente (o projeto utiliza o Maven Wrapper incluído).

### 2. Rodar a Aplicação
No terminal dentro da pasta `backend_java`:

**No Windows (PowerShell/CMD):**
```bash
.\mvnw.cmd spring-boot:run
```

**No Linux / macOS:**
```bash
./mvnw spring-boot:run
```

A API iniciará por padrão em: `http://localhost:8080`

### 3. Endpoints Iniciais de Verificação
- **Boas-vindas / Status:** `GET http://localhost:8080/`
- **Health Check:** `GET http://localhost:8080/health`
- **Actuator:** `GET http://localhost:8080/actuator/health`

---

## 🐘 Conexão com o PostgreSQL (Neon)

No painel do **Neon (neon.tech)**, copie a connection string fornecida e defina as variáveis de ambiente:

```properties
SPRING_DATASOURCE_URL=jdbc:postgresql://<host-neon>.neon.tech/<database>?sslmode=require
SPRING_DATASOURCE_USERNAME=<seu-usuario>
SPRING_DATASOURCE_PASSWORD=<sua-senha>
```

---

## 🚢 Deploy no Render (Free Web Service)

1. Crie um **New Web Service** apontando para este repositório: `https://github.com/rodrigotgranada/esporteclubepelotas_back_java`.
2. Em **Language**, selecione **`Docker`**.
3. Em **Branch**, selecione `main` (ou `develop`).
4. Em **Environment Variables**, adicione suas credenciais do Neon (`SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`).
5. Clique em **Deploy web service**. O Render usará o `Dockerfile` automaticamente!
