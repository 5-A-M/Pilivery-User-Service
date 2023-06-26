FROM openjdk:11
WORKDIR /app
COPY . .
CMD ["echo", "docker file test"]