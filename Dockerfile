# Use the official Eclipse Temurin JDK 17 image with Alpine Linux as the base image
FROM eclipse-temurin:17-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file from the local "target" directory to the container
COPY target/*.jar /target/demo.jar

# Specify the command to run your application
ENTRYPOINT ["java", "-jar", "/target/demo.jar"]

# Expose the port your application will run on (assuming it's 8080)
EXPOSE 8080
