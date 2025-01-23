# Use Ubuntu as the base image for the build stage
FROM ubuntu:22.04 AS build

# Install dependencies (Java, Maven, and Git)
RUN apt-get update && apt-get install -y \
    openjdk-17-jdk \
    maven \
    git \
    && rm -rf /var/lib/apt/lists/*

# Set the working directory
WORKDIR /app

# Copy the project files
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Use Ubuntu as the base image for the runtime stage
FROM ubuntu:22.04

# Install only Java for running the application
RUN apt-get update && apt-get install -y \
    openjdk-17-jdk \
    && rm -rf /var/lib/apt/lists/*

# Set the working directory
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/HodlHub-0.0.1-SNAPSHOT.jar app.jar

# Expose the port your application runs on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
