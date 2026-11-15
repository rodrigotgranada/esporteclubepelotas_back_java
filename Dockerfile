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

# Alocação segura para container de 512MB (evita OOM Kill do cgroup) e startup rápido em low-CPU
ENV JAVA_TOOL_OPTIONS="-XX:+UseSerialGC -XX:MaxRAMPercentage=55.0 -XX:MaxMetaspaceSize=160m -XX:ReservedCodeCacheSize=48m -Xss512k -XX:TieredStopAtLevel=1 -XX:+ExitOnOutOfMemoryError"

# Verificação de integridade via Spring Actuator
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
    CMD curl -fsS http://localhost:${PORT:-8080}/actuator/health || exit 1

# Inicia a aplicação Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]
