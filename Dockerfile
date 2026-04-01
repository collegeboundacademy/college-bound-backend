# Build stage
FROM maven:3.8.1-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

# Runtime stage
FROM openjdk:17-slim
WORKDIR /app
COPY --from=build /app/target/demo-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
