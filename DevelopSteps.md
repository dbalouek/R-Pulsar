# How to create custom R-Pulsar apps

1- Download and import the R-Pulsar_x.x.jar located in the release section of the github repo.

2- Import the following packages in to your main Java class:

```java
import com.google.common.io.Resources;
import com.rutgers.Core.Listener;
import com.rutgers.Core.Message;
import com.rutgers.Core.Message.ARMessage;
import com.rutgers.Core.MessageListener;
import com.rutgers.Core.PulsarProducer;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
```

3 - Define the properties of the R-Pulsar consumer or produer and load it into the java Properties class:

``` 
bootstrap.server=127.0.0.1:5000 
port=5002
keys.dir=/tmp/public2.key
location.longitude=0.00
location.latitude=0.00
```
```java
InputStream props = Resources.getResource("consumer.prop").openStream();
Properties properties = new Properties();
properties.load(props);
```

4.1- Create an R-Pulsar producer use the following code:

```java
PulsarProducer producer = new PulsarProducer(properties);
producer.init();
```
Note that the PulsarProducer object is the object that will be used to intereact with R-Pulsar.

4.2- Create an R-Pulsar consumer user the following code:

```java
PulsarConsumer consumer = new PulsarConsumer(properties);
consumer.init();
```
Note that the PulsarConsumer object is the object that will be used to intereact with R-Pulsar.

5- Create and AR Message:

```java
Message.ARMessage.Header.Profile profile = Message.ARMessage.Header.Profile.newBuilder().addSingle("temperature").addSingle("fahrenheit").build();

Message.ARMessage.Header header = Message.ARMessage.Header.newBuilder().setLatitude(0.00).setLongitude(0.00).setType(Message.ARMessage.RPType.AR_CONSUMER).setProfile(profile).setPeerId(consumer.getPeerID()).build();

Message.ARMessage msg = Message.ARMessage.newBuilder().setHeader(header).setAction(Message.ARMessage.Action.NOTIFY_DATA).build();
```

6- Send the message:

```java
consumer.post(msg);
producer.port(msg);
```

7- Integrate Apache Storm, Apache Edgent or Apache Flink to perform data analytics.

8- For further code syntax take a look at the examples provided or at the documentation. The relevant classes for interacting with R-Pulsar are: PulsarConsumer, PulsarProducer, Rules, and Rule



