FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/target/uno-final-project-1.0-SNAPSHOT.jar app.jar

CMD ["java", "-jar", "app.jar", "--humans", "0", "--bots", "3", "--games", "1", "--quiet", "--seed", "1"]

