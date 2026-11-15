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

# Instala curl para HEALTHCHECK nativo do container
RUN apk add --no-cache curl

# Cria usuário não-root para segurança
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Copia o artefato compilado do estágio anterior
COPY --from=builder /app/target/*.jar app.jar

# Expõe a porta padrão da aplicação em container/produção
EXPOSE 8080

# Alocação proporcional de memória para containers com pouca RAM e falha rápida em OOM
ENV JAVA_TOOL_OPTIONS="-XX:+UseSerialGC -XX:MaxRAMPercentage=70.0 -XX:+ExitOnOutOfMemoryError -XX:TieredStopAtLevel=1 -Xss512k"

# Verificação de integridade via Spring Actuator
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
    CMD curl -fsS http://localhost:8080/actuator/health || exit 1

# Inicia a aplicação Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]
