FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/inventory_app-0.0.1-SNAPSHOT.jar inventory_app-0.0.1.jar
ENTRYPOINT ["java", "-jar", "inventory_app-0.0.1.jar"]