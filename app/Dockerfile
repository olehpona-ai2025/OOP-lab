FROM ghcr.io/graalvm/native-image-community:latest AS builder

RUN microdnf install maven

WORKDIR /build

COPY pom.xml .
COPY src ./src

RUN mvn -Pnative native:compile -DskipTests

FROM debian:bookworm-slim

WORKDIR /app

COPY --from=builder /build/target/funFarm /app/funFarm
COPY db/migration /app/db/migration
EXPOSE 8080/tcp

ENV SPRINGDOC_API-DOCS_ENABLED="false"
ENV SPRINGDOC_SWAGGER-UI_ENABLED="false"

ENTRYPOINT ["/app/funFarm"]