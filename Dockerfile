# BUILD STAGE
FROM maven:3.9.6-eclipse-temurin-21-alpine  AS builder

WORKDIR /app

COPY pom.xml .

COPY . .

RUN mvn package -DskipTests

# RUNTIME STAGE
FROM eclipse-temurin:21-jre-alpine

COPY --from=builder /app/app/target/app-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]
