FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn -q package

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/target/uno-cli-1.0-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]

CMD ["--bots", "3", "--games", "1", "--quiet"]
