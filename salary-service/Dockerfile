FROM openjdk:17-jdk-slim

WORKDIR /app

COPY ./build/libs/salary-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 13006

CMD ["java", "-jar", "app.jar"]
