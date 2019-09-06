package com.rutgers.Examples.Storm;

import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;
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

import com.rutgers.Core.Message;
import com.rutgers.Core.PulsarConsumer;
import com.rutgers.Examples.Publisher;

/**
 * This is and example of the use of the R-Pulsar API.
 * This class consumes sentences from the R-Pulsar consumer and passes the contents the SplitSentenceBolt.
 * @author eduard
 */

public class RandomSentenceSpout extends BaseRichSpout {
	
    PulsarConsumer consumer = null;
    Message.ARMessage msg;
    Message.ARMessage.Header.Profile profile;
    Message.ARMessage.Header header;
    SpoutOutputCollector _collector;
        
    RandomSentenceSpout(Message.ARMessage msg, PulsarConsumer consumer) {
        this.msg = msg;
        this.consumer = consumer;
        profile = Message.ARMessage.Header.Profile.newBuilder().addSingle("temperature").addSingle("fahrenheit").build();
        header = Message.ARMessage.Header.newBuilder().setLatitude(0.00).setLongitude(0.00).setType(Message.ARMessage.RPType.AR_CONSUMER).setProfile(profile).setPeerId(consumer.getPeerID()).build();
    }
    
    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
      _collector = collector;
    }

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("word"));
	}

	@Override
	public void nextTuple() {
        try {
		// TODO Auto-generated method stub
            Message.ARMessage consum_msg = Message.ARMessage.newBuilder().setHeader(header).setAction(Message.ARMessage.Action.REQUEST).setTopic(msg.getTopic()).build();
            //Get the message that was send by the sensor
            Message.ARMessage poll = consumer.poll(consum_msg, msg.getHeader().getPeerId());
            //Emit the sentence from the R-Pulsar producer to the next storm step
            _collector.emit(new Values(poll.getPayload(0)));
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException | NoSuchAlgorithmException | InvalidKeySpecException | UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
