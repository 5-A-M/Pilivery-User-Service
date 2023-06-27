FROM openjdk:11
WORKDIR /app
CMD ["./gradlew", "clean", "build"]
CMD ["ls" ,"build/libs/"]
