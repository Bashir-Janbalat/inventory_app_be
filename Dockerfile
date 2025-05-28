FROM openjdk:17-jdk-slim
WORKDIR /app

ARG APP_VERSION
ENV APP_VERSION=${APP_VERSION}

COPY target/app.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]