# Use an official OpenJDK 21 runtime as a parent image
FROM openjdk:21-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the packaged jar file into the container at /app
COPY target/demo-0.0.1-SNAPSHOT.jar /app/app.jar

# Expose the port that Spring Boot uses (default 8080)
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "app.jar"]
