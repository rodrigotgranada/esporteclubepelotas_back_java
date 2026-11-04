# =======================================================
# Estágio 1: Build da Aplicação com Maven e Java 17
# =======================================================
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

# Copia arquivos do wrapper e dependências primeiro (cache Docker eficiente)
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x ./mvnw

# Baixa as dependências offline para acelerar builds subsequentes
RUN ./mvnw dependency:go-offline -B || true

# Copia o código fonte e compila o pacote JAR
COPY src ./src
RUN ./mvnw clean package -DskipTests

# =======================================================
# Estágio 2: Imagem Final de Execução (Leve e Segura)
# =======================================================
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Cria usuário não-root para segurança
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Copia o artefato compilado do estágio anterior
COPY --from=builder /app/target/*.jar app.jar

# Expõe a porta padrão (o Render injetará a variável $PORT)
EXPOSE 8080

# Otimização de memória para limites de 512MB do Render Free
ENV JAVA_TOOL_OPTIONS="-XX:+UseSerialGC -Xmx350m -Xms128m"

# Inicia a aplicação Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]

