FROM amazoncorretto:25-alpine AS build
WORKDIR /app

# Copy only the build configuration first so the dependency download is cached
# as its own layer; it is reused on every build where these files are unchanged.
COPY gradlew settings.gradle build.gradle ./
COPY gradle ./gradle
RUN ./gradlew --no-daemon dependencies

# Now copy the sources and build the executable jar.
COPY src ./src
RUN ./gradlew --no-daemon bootJar

FROM amazoncorretto:25-alpine
WORKDIR /code
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]
