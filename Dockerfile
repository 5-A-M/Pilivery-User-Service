FROM openjdk:11
WORKDIR /app
CMD ["./gradlew", "clean", "build"]
COPY build/libs/user-service-1.0.jar userSVC.jar
CMD ["java", "-jar", "userSVC.jar"]
