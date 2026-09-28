# Build stage
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy pom.xml and source code
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy the built jar from builder stage
COPY --from=builder /app/target/money-transfer-system-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 9890

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:9890/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
