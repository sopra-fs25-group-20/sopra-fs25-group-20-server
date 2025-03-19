FROM gradle:7.6-jdk17 AS build
WORKDIR /app
COPY gradlew gradlew.bat /app/
COPY gradle /app/gradle
RUN chmod +x ./gradlew
COPY build.gradle settings.gradle /app/
COPY src /app/src
ENV GRADLE_USER_HOME="/app/.gradle"
RUN ./gradlew clean build --no-daemon

FROM openjdk:17-alpine
ENV SPRING_PROFILES_ACTIVE=production
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser
WORKDIR /app
COPY --from=build /app/build/libs/*.jar /app/app.jar
EXPOSE 8080
CMD ["java", "-jar", "/app/app.jar"]