FROM eclipse-temurin:21-jre
VOLUME /tmp
COPY target/poc-1.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]