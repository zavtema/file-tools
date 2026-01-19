# ---------- build stage ----------
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# сначала зависимости (кешируется лучше)
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

# потом исходники
COPY src ./src

# соберем jar
RUN mvn -q -DskipTests package

# ---------- runtime stage ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

# Render прокидывает PORT, но Spring Boot по умолчанию 8080.
# Мы скажем Spring слушать PORT.
ENV JAVA_OPTS=""
ENV SERVER_PORT=8080

# jar из build stage
COPY --from=build /app/target/*.jar app.jar

# Render будет направлять трафик на этот порт
EXPOSE 8080

# Запуск
CMD ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT:-8080} -jar app.jar"]
