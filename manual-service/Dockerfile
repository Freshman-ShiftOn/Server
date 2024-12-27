FROM openjdk:17-jdk-slim

WORKDIR /app

COPY ./build/libs/manual-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 13001

CMD ["java", "-jar", "app.jar"]
