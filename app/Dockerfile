FROM maven:3.9.15-eclipse-temurin-25 AS builder

WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

LABEL authors="olegpona"

FROM eclipse-temurin:25
WORKDIR /app
COPY --from=builder /build/target/funFarm-1.0.jar app.jar
COPY src/main/resources/db/migration /app/db/migration
EXPOSE 8080/tcp

ENV SPRINGDOC_API-DOCS_ENABLED="false"
ENV SPRINGDOC_SWAGGER-UI_ENABLED="false"

ENTRYPOINT ["java", "-jar", "app.jar"]
