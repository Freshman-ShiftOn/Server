FROM openjdk:17-jdk-slim

WORKDIR /app

COPY ./build/libs/user-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 13004

CMD ["java", "-jar", "app.jar"]
