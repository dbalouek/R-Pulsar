FROM maven:3.6.1-jdk-8

ARG GPS=40:-74
ARG PORT=5000

ENV GPS=${GPS} \
    PORT=${PORT} 

COPY . R-Pulsar

RUN cd R-Pulsar && cp target/P2P-1.0-SNAPSHOT-jar-with-dependencies.jar ../Rpulsar.jar

ENTRYPOINT [ "java", "-jar", "Rpulsar.jar"]
CMD [ -l, x, -gps, $GPS  , -p, $PORT ]