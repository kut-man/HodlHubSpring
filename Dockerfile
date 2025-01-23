# Use a Maven image to build the application
FROM maven:3.9.4-eclipse-temurin-22 AS build
WORKDIR /app

# Copy the Maven configuration files
COPY pom.xml .
COPY src ./src

# Build the project
RUN mvn clean package -DskipTests

# Use a lightweight JDK runtime image for the final build
FROM eclipse-temurin:22-jre
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/HodlHub-0.0.1-SNAPSHOT.jar app.jar

# Expose the port your app runs on (default for Spring Boot is 8080)
EXPOSE 8080

# Set environment variables (change if necessary)
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://dpg-cu98bsl6l47c73d7b0v0-a/postgres_9av6 \
    SPRING_DATASOURCE_USERNAME=postgres_9av6_user \
    SPRING_DATASOURCE_PASSWORD=VgoLIzJux8CxkBDFyyrAgrBnYN1rDPst

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
