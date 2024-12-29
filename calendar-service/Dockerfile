FROM openjdk:17-jdk-slim

WORKDIR /app

COPY ./build/libs/calendar-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 13002

CMD ["java", "-jar", "app.jar"]
