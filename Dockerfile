# Build stage
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY . .
RUN mvn -pl banking-app -am clean verify

# Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app
# Create unprivileged user and group
RUN addgroup --system app && adduser --system --ingroup app app
COPY --from=build /workspace/banking-app/target/banking-app-1.0.0.jar /app/app.jar
USER app
ENV SERVER_PORT=8080
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
