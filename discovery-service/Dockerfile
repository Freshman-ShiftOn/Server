FROM openjdk:17-jdk-slim

WORKDIR /app

COPY ./target/discovery-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 18761

CMD ["java", "-jar", "app.jar"]