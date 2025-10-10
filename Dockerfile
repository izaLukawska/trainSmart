# BUILD STAGE
FROM maven:3.9.6-eclipse-temurin-21-alpine  AS builder

WORKDIR /app

COPY pom.xml .

COPY src ./src

RUN mvn package -DskipTests

# RUNTIME STAGE
FROM eclipse-temurin:21-jre-alpine

COPY --from=builder /app/target/trainSmart-*.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]
