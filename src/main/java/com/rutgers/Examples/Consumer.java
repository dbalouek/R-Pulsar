/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rutgers.Examples;

import com.google.common.io.Resources;
import com.rutgers.Core.Listener;
import com.rutgers.Core.Message;
import com.rutgers.Core.MessageListener;
import com.rutgers.Core.PulsarConsumer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is and example of the use of the R-Pulsar API.
 * This example shows how to build a R-Pulsar consumer.
 * @param keys
 * @return
 */
public class Consumer {
    static boolean running = false;
    static Thread thread = null;
    static PulsarConsumer consumer = null;
    
    public static class Consum implements Runnable {
        Message.ARMessage msg;
        Message.ARMessage.Header.Profile profile;
        Message.ARMessage.Header header;
        
        Consum(Message.ARMessage msg) {
            this.msg = msg;
            profile = Message.ARMessage.Header.Profile.newBuilder().addSingle("temperature").addSingle("fahrenheit").build();
            header = Message.ARMessage.Header.newBuilder().setLatitude(0.00).setLongitude(0.00).setType(Message.ARMessage.RPType.AR_CONSUMER).setProfile(profile).setPeerId(consumer.getPeerID()).build();
        }
        
        @Override
        public void run() {
            while(running) {
                try {
                    Message.ARMessage consum_msg = Message.ARMessage.newBuilder().setHeader(header).setAction(Message.ARMessage.Action.REQUEST).setTopic(msg.getTopic()).build();
                    //Get the message that was send by the sensor
                    Message.ARMessage poll = consumer.poll(consum_msg, msg.getHeader().getPeerId());
                    
                    String payload = poll.getPayload(0).split("\\r?\\n")[1].split(":")[1].trim().replace("\"", "");
                    System.out.println("Recived: " + payload);
                	
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException ex) {
                    Logger.getLogger(HelloWorldPublisher.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NoSuchAlgorithmException | InvalidKeySpecException | UnknownHostException ex) {
                    Logger.getLogger(Consumer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws UnknownHostException, ClassNotFoundException {
        try {
        	
        	if(args.length == 0) {
    	        System.out.println("Need to specify the consumer property file!!");
    	        System.exit(0);
        	}
        	
            // Load the consumer properties into an InputStream  
            InputStream props = new FileInputStream(args[0]);//Resources.getResource("consumer.prop").openStream();
            //Create a java util properties object 
            Properties properties = new Properties();
            //Load the consumer properties into memory
            properties.load(props);
            
            //Create a new R-Pulsar consumer object
            consumer = new PulsarConsumer(properties);
            //Init the R-Pulsar consumer
            consumer.init();
            
            //Create a listener for incoming AR messages
            consumer.replayListener(new Listener(){
                @Override
                public void replay(MessageListener ml, Message.ARMessage o) {
                    switch(o.getAction()) {
                        //When we receive a notify_start we need to start processing the data that we will receive
                        case NOTIFY_START:
                            running = true;
                            // Start a new thread with the Consum class
                            thread = new Thread(new Consum(o));
                            thread.start();
                            break;
                        //We will not receive any more data from the sensors
                        case NOTIFY_STOP:
                            try {
                                running = false;
                                thread.join();
                            } catch (InterruptedException ex) {
                                Logger.getLogger(HelloWorldPublisher.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            break;
                        case REQUEST_RESPONSE:
                           
                            break;
                    }
                }
            });
            
            //Create a profile with the data that we are interested in
            Message.ARMessage.Header.Profile profile = Message.ARMessage.Header.Profile.newBuilder().addSingle("temperature").addSingle("fahrenheit").build();
            //Create a header and set our physical location
            Message.ARMessage.Header header = Message.ARMessage.Header.newBuilder().setLatitude(0.00).setLongitude(0.00).setType(Message.ARMessage.RPType.AR_CONSUMER).setProfile(profile).setPeerId(consumer.getPeerID()).build();
            //Create an AR Message and tell the system to notify us if there is a profile that matches this criteria
            Message.ARMessage msg = Message.ARMessage.newBuilder().setHeader(header).setAction(Message.ARMessage.Action.NOTIFY_DATA).build();
            //Use the consumer object to send the message
            consumer.post(msg);
            
        } catch (IOException | InterruptedException | NoSuchAlgorithmException | InvalidKeySpecException ex) {
            Logger.getLogger(HelloWorldPublisher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
