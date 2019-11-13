package com.rutgers.Examples.Storm;

import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import com.google.common.io.Resources;
import com.rutgers.Core.Listener;
import com.rutgers.Core.Message;
import com.rutgers.Core.Message.ARMessage;
import com.rutgers.Core.MessageListener;
import com.rutgers.Core.PulsarConsumer;
import com.rutgers.Examples.Publisher;
import com.rutgers.Examples.Consumer.Consum;

/**
 * This is and example of the use of the R-Pulsar API.
 * This class consumes sentences from the R-Pulsar consumer and passes the contents the SplitSentenceBolt.
 * @author eduard
 */

public class RandomSentenceSpout extends BaseRichSpout {
	
    PulsarConsumer consumer;
    Message.ARMessage msg;
    Message.ARMessage msg2 = null;
    Message.ARMessage.Header.Profile profile;
    Message.ARMessage.Header header;
    SpoutOutputCollector _collector;
        
    RandomSentenceSpout() {
    }
    
    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
      _collector = collector;
      
		try {
			  InputStream props = Resources.getResource("consumer.prop").openStream();
		      //Create a java util properties object 
		      Properties properties = new Properties();
		      //Load the consumer properties into memory
		      properties.load(props);
		      
		      //Create a new R-Pulsar consumer object
		      consumer = new PulsarConsumer(properties);
		      //Init the R-Pulsar consumer
		      consumer.init();
		      
		      //Create a profile with the data that we are interested in
		      profile = Message.ARMessage.Header.Profile.newBuilder().addSingle("temperature").addSingle("fahrenheit").build();
		      //Create a header and set our physical location
		      header = Message.ARMessage.Header.newBuilder().setLatitude(0.00).setLongitude(0.00).setType(Message.ARMessage.RPType.AR_CONSUMER).setProfile(profile).setPeerId(consumer.getPeerID()).build();
		      //Create an AR Message and tell the system to notify us if there is a profile that matches this criteria
		      msg = Message.ARMessage.newBuilder().setHeader(header).setAction(Message.ARMessage.Action.NOTIFY_DATA).build();
		      //Use the consumer object to send the message
		      consumer.post(msg);
		      
	          consumer.replayListener(new Listener(){
	                @Override
	                public void replay(MessageListener ml, Message.ARMessage o) {
	                    switch(o.getAction()) {
	                        //When we receive a notify_start we need to start processing the data that we will receive
	                        case NOTIFY_START:
	                        	msg2 = o;
	                            break;
	                        //We will not receive any more data from the sensors
	                        case NOTIFY_STOP:
	                            break;
	                        case REQUEST_RESPONSE:    
	                            break;
	                    }
	                }
	          });
		      
		} catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException | InterruptedException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
	    declarer.declare(new Fields("sentence"));
	}

	@Override
	public void nextTuple() {
        try {
		// TODO Auto-generated method stub        	

            //Get the message that was send by the sensor
           //TimeUnit.SECONDS.sleep(1);
           //System.out.println(msg.getHeader().getPeerId());
           if(msg2 != null) {
               Message.ARMessage consum_msg = Message.ARMessage.newBuilder().setHeader(header).setAction(Message.ARMessage.Action.REQUEST).setTopic(msg2.getTopic()).build();
        	   Message.ARMessage poll = consumer.poll(consum_msg, msg2.getHeader().getPeerId());   
        	   String payload = poll.getPayload(0).split("\\r?\\n")[1].split(":")[1].trim().replace("\"", "");
        	   //Emit the sentence from the R-Pulsar producer to the next storm step
        	   _collector.emit(new Values(payload));
           }
		} catch (InterruptedException | NoSuchAlgorithmException | InvalidKeySpecException | UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
