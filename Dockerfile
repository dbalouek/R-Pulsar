FROM maven:3.8.6-openjdk-8

ARG GPS=40:-74
ARG PORT=5000

ENV GPS=${GPS} \
    PORT=${PORT} 

WORKDIR /R-Pulsar
COPY src src
COPY pom.xml pom.xml
RUN mvn clean package
COPY target/P2P-1.0-SNAPSHOT-jar-with-dependencies.jar Rpulsar.jar

ENTRYPOINT [ "java", "-jar", "Rpulsar.jar"]
CMD [ -l, x, -gps, $GPS  , -p, $PORT ]