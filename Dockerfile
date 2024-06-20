# Use a base image with Java 8 already installed
FROM amazoncorretto:8

# Set the working directory inside the container
WORKDIR /app

# Copy the executable JAR file from your host into the container at /app
COPY target/CRM-0.0.1-SNAPSHOT.jar .

# Expose the port that your application listens to
EXPOSE 8081

# Command to run your application
CMD ["java", "-jar", "CRM-0.0.1-SNAPSHOT.jar"]

