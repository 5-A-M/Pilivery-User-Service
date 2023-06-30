FROM openjdk:11
WORKDIR /app
COPY /build/libs/user-service-1.0.jar userSVC.jar
CMD ["java", "-jar", "/app/userSVC.jar"]
