FROM openjdk:24-ea-17-jdk
VOLUME /tmp
EXPOSE 8080
ADD ./target/GTICS_LAB8_20182048-0.0.1-SNAPSHOT.jar lab8.jar
ENTRYPOINT ["java", "-jar", "lab8.jar"]