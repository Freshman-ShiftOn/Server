FROM openjdk:17-jdk-slim

WORKDIR /app

COPY ./build/libs/branch-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 13003

CMD ["java", "-jar", "app.jar"]
