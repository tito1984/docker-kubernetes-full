FROM openjdk:17-jdk-alpine as builder

WORKDIR /app/msvc-courses

COPY ./pom.xml /app
COPY ./msvc-courses/.mvn ./.mvn
COPY ./msvc-courses/mvnw .
COPY ./msvc-courses/pom.xml .

#RUN ./mvnw clean package -Dmaven.test.skip -Dmaven.main.skip -Dspring-boot.repackage.skip && rm -r ./target/
RUN ./mvnw dependency:go-offline
COPY ./msvc-courses/src ./src

RUN ./mvnw clean package -DskipTests

FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY --from=builder /app/msvc-courses/target/msvc-courses-0.0.1-SNAPSHOT.jar .
EXPOSE 8002

ENTRYPOINT ["java", "-jar", "msvc-courses-0.0.1-SNAPSHOT.jar"]