### STAGE 1: Build ###
FROM maven:3.6.3-jdk-11-slim AS build
RUN mkdir -p /workspace
WORKDIR /workspace
COPY pom.xml /workspace
COPY src /workspace/src
COPY mock_profile_pictures /workspace/mock_profile_pictures
RUN mvn -f pom.xml clean package

### STAGE 2: Run ###
FROM adoptopenjdk/openjdk11:latest
COPY --from=build /workspace/target/*.jar app.jar
COPY --from=build /workspace/mock_profile_pictures /mock_profile_pictures
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]