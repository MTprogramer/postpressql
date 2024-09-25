# Stage 1: Build the application
FROM gradle:7.3.3-jdk11 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src

# Add this line to print the directory contents
RUN ls -al /home/gradle/src

# Update the Gradle wrapper
RUN ./gradlew wrapper --gradle-version 7.3.3

# Run the build command
RUN ./gradlew build --no-daemon

# Stage 2: Run the application
FROM openjdk:11-jre-slim
COPY --from=build /home/gradle/src/build/libs/*.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
