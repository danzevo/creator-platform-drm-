# Stage 1: Build the JAR with Maven
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app

# Copy Maven wrapper & POM first to leverage Docker layer caching
COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN chmod +x ./mvnw && ./mvnw dependency:go-offline -B

# Copy source code and package
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Stage 2: Minimal Production JRE Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Create shared storage directory for DRM files
RUN mkdir -p /app/storage/watermarked /app/storage/masters

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]