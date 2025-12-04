# Stage 1: Build the application
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy the pom.xml and source code
COPY pom.xml .
COPY cinedaltonsproj ./cinedaltonsproj

# Build the application (skipping tests to speed up the build)
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

# Copy the jar file from the build stage
# Note: We use a wildcard (*) to match the versioned jar name automatically
COPY --from=build /app/target/*.jar app.jar

# Expose the port your app runs on (default Spring Boot port is 8080)
EXPOSE 8082

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]