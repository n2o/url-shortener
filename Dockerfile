FROM amazoncorretto:25-alpine AS build
WORKDIR /app
COPY . .
RUN ./gradlew bootJar

FROM amazoncorretto:25-alpine
WORKDIR /code
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]
