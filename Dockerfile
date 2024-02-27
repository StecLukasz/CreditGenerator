FROM openjdk:11-jdk
COPY creditgenerator-1.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]