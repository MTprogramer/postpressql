FROM gradle:7.5-jdk17 AS build
COPY . /home/app
WORKDIR /home/app
RUN gradle build --no-daemon

FROM openjdk:17-jdk-slim
COPY --from=build /home/app/build/libs/myapp.jar /app/myapp.jar
ENTRYPOINT ["java", "-jar", "/app/myapp.jar"]
