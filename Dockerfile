
FROM maven:3.8.6 AS maven
LABEL MAINTAINER="emmanuel.coulombe.1@ens.etsmtl.ca"
EXPOSE 8080
ENV PORT=8080
WORKDIR /usr/src/app
COPY . /usr/src/app
# Compile and package the application to an executable JAR
RUN mvn package

FROM openjdk:11
ARG JAR_FILE=horaire-ets.jar

WORKDIR /opt/app

# Copy the spring-boot-api-tutorial.jar from the maven stage to the /opt/app directory of the current stage.
COPY --from=maven /usr/src/app/target/${JAR_FILE} /opt/app/

ENTRYPOINT ["java","-jar", "-Dserver.port=$PORT","horaire-ets.jar"]