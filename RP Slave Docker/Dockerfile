FROM maven:3.6.1-jdk-8

ARG gps=40:-74
ARG port=5001
ARG ip_port

RUN git clone https://github.com/egibert/Rutgers-Pulsar.git

RUN cd Rutgers-Pulsar && mvn clean package && cp target/P2P-1.0-SNAPSHOT-jar-with-dependencies.jar ../Rpulsar.jar && rm -Rf Rutgers-Pulsar

RUN java -jar Rpulsar.jar -l x -gps $gps -b $ip_port -p $port; exit 0
