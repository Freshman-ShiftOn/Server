FROM openjdk:17-jdk-slim

WORKDIR /app

COPY ./build/libs/discoveryservice-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 18761

CMD ["java", "-jar", "app.jar"]