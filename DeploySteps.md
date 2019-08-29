# How to deploy and R-Pulsar 

To deploy R-Pulsar there are two different options for deployment: Option 1 is by compiling the source code and deploying, and option 2 is by using the already build docker images or R-Pulsar.
To start and RP we always first need to start a Master RP and then if more RPs are needed it they all need to be started as Slave RP with the IP address of the master RP. 

# Deploy using source code

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

# Deploy using Docker images

1- Deploy a Master RP:

```
docker run erenart/rpulsar:RPMaster-ARCH --expose=5000
```

2- Deploy a slave that will communicate with the other RP:

```
docker run erenart/rpulsar:RPSlave-ARCH --expose=5001 -e "ip_port=ip:port" 
```

Where the arch is the architecture of the node that R-Pulsar will be deployed. If you select the wrong architecture the docker image will not start. Currently, the only architectures supported are AMD64. 
