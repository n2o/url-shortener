FROM gradle:7-jdk17 AS BUILD
WORKDIR /home/gradle/src
COPY . .
RUN gradle bootJar

# ------------------------------------------------------------------------------

FROM openjdk:17-jre-slim
WORKDIR /code
COPY --from=BUILD /home/gradle/src/build/libs/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]
