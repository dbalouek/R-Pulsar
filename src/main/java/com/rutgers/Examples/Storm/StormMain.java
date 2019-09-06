/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rutgers.Examples.Storm;

import com.google.common.io.Resources;
import com.rutgers.Core.Listener;
import com.rutgers.Core.Message;
import com.rutgers.Core.MessageListener;
import com.rutgers.Core.PulsarConsumer;
import com.rutgers.Examples.Publisher;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.thrift.TException;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;

/**
 * This is and example of the use of the R-Pulsar API.
 * This example shows how to build a R-Pulsar consumer.
 * @param keys
 * @return
 */
public class StormMain {
    static PulsarConsumer consumer = null;
    
    /**
     * @param args the command line arguments
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {
        try {
            // Load the consumer properties into an InputStream  
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
            Message.ARMessage.Header.Profile profile = Message.ARMessage.Header.Profile.newBuilder().addSingle("temperature").addSingle("fahrenheit").build();
            //Create a header and set our physical location
            Message.ARMessage.Header header = Message.ARMessage.Header.newBuilder().setLatitude(0.00).setLongitude(0.00).setType(Message.ARMessage.RPType.AR_CONSUMER).setProfile(profile).setPeerId(consumer.getPeerID()).build();
            //Create an AR Message and tell the system to notify us if there is a profile that matches this criteria
            Message.ARMessage msg = Message.ARMessage.newBuilder().setHeader(header).setAction(Message.ARMessage.Action.NOTIFY_DATA).build();
            //Use the consumer object to send the message
            
            TopologyBuilder builder = new TopologyBuilder();
            builder.setSpout("spout", new RandomSentenceSpout(msg, consumer));
            builder.setBolt("split", new SplitSentenceBolt(), 8).shuffleGrouping("spout");
            builder.setBolt("count", new WordCountBolt(), 12).fieldsGrouping("split", new Fields("word"));
            
            Config conf = new Config();
            conf.setDebug(true);
            conf.setMaxTaskParallelism(3);
            LocalCluster cluster = new LocalCluster();
            
            //Create a listener for incoming AR messages
            consumer.replayListener(new Listener(){
                @Override
                public void replay(MessageListener ml, Message.ARMessage o) {
                    switch(o.getAction()) {
                        //When we receive a notify_start we need to start processing the data that we will receive
                        case NOTIFY_START:
                            // Start a new thread with the Consum class
							try {
								cluster.submitTopology("word-count", conf, builder.createTopology());
							} catch (TException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
                            break;
                        //We will not receive any more data from the sensors
                        case NOTIFY_STOP:
							cluster.shutdown();
                            break;
                        case REQUEST_RESPONSE:
                            break;
                    }
                }
            });
            
            consumer.post(msg);
            
        } catch (IOException | InterruptedException | NoSuchAlgorithmException | InvalidKeySpecException ex) {
            Logger.getLogger(Publisher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
