# Use an official OpenJDK runtime as a parent image
FROM openjdk:8-jre-slim

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file into the container at /app
COPY target/CRM-0.0.1-SNAPSHOT.jar /app/CRM-0.0.1-SNAPSHOT.jar

# Make port 8080 available to the world outside this container (adjust as needed)
EXPOSE 8087

# Command to run the application
CMD ["java", "-jar", "CRM-0.0.1-SNAPSHOT.jar"]
