FROM openjdk:17-jdk-slim

WORKDIR /app

COPY ./build/libs/approval-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 13005

CMD ["java", "-jar", "app.jar"]
