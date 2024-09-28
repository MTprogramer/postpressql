FROM openjdk:11-jdk-slim

# Set the working directory
WORKDIR /src

# Copy the project files into the container
COPY . .

# Update package lists and install dos2unix in one layer
RUN apt-get update && \
    apt-get install -y dos2unix && \
    rm -rf /var/lib/apt/lists/* && \
    dos2unix gradlew

# Build the application using shadowJar instead of fatJar
RUN bash gradlew shadowJar

# Set the working directory for the run command
WORKDIR /run

# Copy the jar file to the run directory
RUN cp /src/build/libs/*-all.jar /run/server.jar

# Expose the application port
EXPOSE 8081

# Specify the command to run the application
ENTRYPOINT ["java", "-jar", "/run/server.jar"]
