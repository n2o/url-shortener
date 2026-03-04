FROM gradle:8-jdk25-corretto AS BUILD
WORKDIR /home/gradle/src
COPY . .
RUN gradle bootJar

FROM amazoncorretto:25-alpine
WORKDIR /code
COPY --from=BUILD /home/gradle/src/build/libs/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]
