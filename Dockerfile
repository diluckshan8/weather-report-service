# Use an official Eclipse Temurin Java 21 runtime as the base image
FROM eclipse-temurin:21-jdk-alpine

# Set the working directory in the container
WORKDIR /app

# Copy Gradle build output (JAR file) into the container
COPY build/libs/weather-report-service-0.0.1-SNAPSHOT.jar weather-service.jar

# Expose the default Spring Boot application port
EXPOSE 8080

# Command to run the Spring Boot application
CMD ["java", "-jar", "weather-service.jar"]