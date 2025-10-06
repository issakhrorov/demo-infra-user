FROM gradle:8-jdk21 AS builder
WORKDIR /home/gradle/project
COPY --chown=gradle:gradle . .
RUN gradle --no-daemon clean assemble

FROM eclipse-temurin:21-jre-jammy

ENV SPRING_PROFILES_ACTIVE=prod

WORKDIR /app
COPY --from=builder /home/gradle/project/build/libs/*.jar app.jar
EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=30s CMD curl -f http://localhost:8080/infra/api/v1/user/actuator/health || exit 1
ENTRYPOINT ["java", "-jar", "/app/app.jar"]