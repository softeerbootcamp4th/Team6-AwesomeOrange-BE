# Use OpenJDK 17 as the base image
FROM openjdk:17

# Specify the build argument for the JAR file
ARG JAR_FILE=build/libs/*.jar

# Copy the JAR file into the container
COPY ${JAR_FILE} app.jar

# Set the entry point to run the JAR file
ENTRYPOINT ["java", "-jar", "/app.jar"]
