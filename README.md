# Rutgers-Pulsar
R-Pulsar is an IoT Edge Framework, that extends cloud capabilities to local devices and provides a programming model for deciding what, when, and where data get collected and processed. It has been deployed and tested on embedded devices (Raspberry Pi and Android phone) and presents an experimental evaluation that demonstrates that R-Pulsar can enable timely stream analytics by effectively leveraging edge and core resources.

## Features
* Memory mapped pub/sub queue.
* Memory mapped DHT.
* A data-driven programming framework.
* Support for Raspberry Pi and Android Phones.

## Quick Start

Compiling the project using Maven

```
mvn clean package
```

Executing an RP Master

```
java -jar P2P-1.0-SNAPSHOT-jar-with-dependencies.jar  -l x -gps 40:-74 -p 5000
```

Executing a Publisher

```
java -cp P2P-1.0-SNAPSHOT-jar-with-dependencies.jar com.rutgers.Examples.Publisher path_to_the_property_file
```

Executing a Consumer

```
java -cp P2P-1.0-SNAPSHOT-jar-with-dependencies.jar com.rutgers.Examples.Consumer path_to_the_property_file

```

If we need to add more RP's into the system we need to tell them where the RP Master is located by running the following command:
```
java -jar P2P-1.0-SNAPSHOT-jar-with-dependencies.jar  -l x -gps 40:-74 -p 5000 -b ip:port
```
Where the IP is the ip address of the master RP and the port is the port where the master RP is listening to.

## Code Examples
```java
//create a R-Pulsar producer
PulsarProducer producer = new PulsarProducer(properties);
producer.init();

//producer temperature profile
Message.ARMessage.Header.Profile profile = Message.ARMessage.Header.Profile.newBuilder().addSingle("temperature").addSingle("fahrenheit").build();

//message to store object in the DHT
Message.ARMessage msg = Message.ARMessage.newBuilder().setHeader(header).setAction(Message.ARMessage.Action.STORE_DATA).addAllPayload(payloadList).build();
```

## Publications
E. Gibert Renart, D. Balouek-Thomert and M. Parashar. An Edge-Based Framework for Enabling Data-Driven Pipelines for IoT Systems (In preparation) <br />
E. Gibert Renart, A. Silva Veith and M. Dias de Assuncao and L. Lefèvre and D. Balouek-Thomert and M. Parashar. Cost and Latency Aware Framework for Data Stream Analytics. (CCGrid2019) <br />
E. Gibert Renart, D. Balouek-Thomert and M. Parashar. Edge Based Data-Driven Pipelines (Technical Report - Aug 2018) <br />
E. Gibert Renart, J. Diaz-Montes, and M. Parashar. Datadriven stream processing at the edge. In IEEE International Conference on Fog and Edge Computing, 2017.<br />
E. Gibert Renart, D. Balouek-Thomert and M. Parashar. Pulsar: Enabling Dynamic Data-Driven IoT Applications. In IEEE Foundations and Applications of Self* Systems, 2017.<br />
E. Gibert Renart, D. Balouek-Thomert, X. Hu, J. Gong and M. Parashar. Online Decision-Making Using Edge Resources for Content-Driven Stream Processing. In IEEE International Conference on eScience, 2017.<br />


## License
This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details




