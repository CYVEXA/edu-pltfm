# Use Java 21
FROM eclipse-temurin:21

# App folder
WORKDIR /app

# Copy jar file
COPY target/*.jar app.jar

# Run app
ENTRYPOINT ["java", "-jar", "app.jar"]