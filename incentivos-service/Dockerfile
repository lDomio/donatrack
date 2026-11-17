FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /workspace

COPY pom.xml .
COPY common-lib/pom.xml common-lib/pom.xml
COPY autenticacion-service/pom.xml autenticacion-service/pom.xml
COPY incentivos-service/pom.xml incentivos-service/pom.xml
COPY donaciones-service/pom.xml donaciones-service/pom.xml
COPY logistica-service/pom.xml logistica-service/pom.xml
COPY notificacion-service/pom.xml notificacion-service/pom.xml
COPY front-end-app/pom.xml front-end-app/pom.xml
COPY common-lib/src common-lib/src
COPY incentivos-service/src incentivos-service/src

RUN mvn -pl incentivos-service -am -DskipTests package

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /workspace/incentivos-service/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
