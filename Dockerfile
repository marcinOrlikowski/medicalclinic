FROM eclipse-temurin:21-jdk
MAINTAINER marcinOrlikowski
COPY target/medicalclinic-0.0.1-SNAPSHOT.jar medicalclinic-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar","medicalclinic-0.0.1-SNAPSHOT.jar"]