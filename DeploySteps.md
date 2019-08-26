# How to deploy and R-Pulsar node using source code

1- Compiling the project using Maven

```
mvn clean package
```

2- Deploy the first RP as a master RP.

```
java -jar P2P-1.0-SNAPSHOT-jar-with-dependencies.jar -l x -gps 40:-74 -p 5000
```

3- Deploy other RP's as slaves of the master RP.

```
java -jar P2P-1.0-SNAPSHOT-jar-with-dependencies.jar -l x -gps 40:-74 -p 5000 -b ip:port
```

For the -b flag you need to pass the Ip address and the port of the master RP.

# How to deploy and R-Pulsar node using Docker images

1- Deploy a Master RP:

```
docker run erenart/rpulsar:RPMaster-ARCH 
```

2- Deploy a slave that will communicate with the other RP:

```
docker run erenart/rpulsar:RPSlave-ARCH 
```
