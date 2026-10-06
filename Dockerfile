FROM openjdk:17
COPY ".target/app-0.0.1-SNAPSHOT.jar" "app.jar"
EXPOSE 8113
ENTRYPOINT [ "java", "-jar","app.jar"]