FROM openjdk:17-jdk-slim

WORKDIR /app

COPY ./target/apigateway-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 11000

CMD ["java", "-jar", "app.jar"]