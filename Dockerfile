FROM maven:3.9.15-eclipse-temurin-25 AS builder

WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

LABEL authors="olegpona"

FROM eclipse-temurin:25
WORKDIR /app
COPY --from=builder /build/target/funFarm-1.0.jar app.jar

EXPOSE 8080/tcp

ENTRYPOINT ["java", "-jar", "app.jar"]
