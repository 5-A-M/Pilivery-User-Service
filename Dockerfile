FROM openjdk:11
WORKDIR /app
CMD ["./gradlew", "clean", "build"]
COPY build/libs/service-discovery-0.0.1-SNAPSHOT.jar userSVC.jar
CMD ["java", "-jar", "userSVC.jar"]
