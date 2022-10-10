/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rutgers.Examples;

import com.rutgers.Core.Listener;
import com.rutgers.Core.Message;
import com.rutgers.Core.Message.ARMessage;

import com.rutgers.Core.MessageListener;
import com.rutgers.Core.PulsarProducer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileReader;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * This is and example of the use of the R-Pulsar API.
 */
public class SmokePublisher { 
    
    static int numRecords = 10000;
    static int recordSize = 200;
    static int iterations = 20;
    static boolean running = false;
    static Thread thread = null;
    static PulsarProducer producer = null;
    public static String payload = "payload";
    
    public static class Push implements Runnable {
        Message.ARMessage msg = null;
        ARMessage.Header.Profile profile = null;
        ARMessage.Header header = null;
        
        Push(Message.ARMessage msg) {
            this.msg = msg;
        }
        
        @Override
        public void run() {
            //Using some compression techniques to reduce network overhead
            for(int i = 0; i < iterations; i ++) {
                try {
                	// Pushing a record to the RP
                    Message.ARMessage push_msg = Message.ARMessage.newBuilder().setAction(Message.ARMessage.Action.STORE_QUEUE).setTopic(msg.getTopic()).addPayload(payload).build();
                    System.out.println("Sending: " + payload);
                    producer.stream(push_msg, msg.getHeader().getPeerId());
                } catch (NoSuchAlgorithmException | InvalidKeySpecException | UnknownHostException | InterruptedException ex) {
                    Logger.getLogger(StormPublisher.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    /**
     * @param args the command line arguments
     * @throws ParseException
     */
    public static void main(String[] args) throws UnknownHostException, ClassNotFoundException, ParseException {
        try {
            Thread.sleep(10000);
        	if(args.length == 0) {
    	        System.out.println("Need to specify input files.");
    	        System.exit(0);
        	}
            JSONParser parser = new JSONParser();
            String pathToFile = args[0];
            BufferedReader bufferReader = new BufferedReader( new FileReader( pathToFile ));
            String someData;
            while (( someData = bufferReader.readLine() ) != null )
            {
                JSONObject jsonObj = (JSONObject) parser.parse(someData);
                payload = jsonObj.toString();
                System.out.println(payload);
            }
            bufferReader.close();
            InputStream props = new FileInputStream(args[1]);
            Properties properties = new Properties();
            properties.load(props);
            
            producer = new PulsarProducer(properties);
            producer.init();
            
            producer.replayListener(new Listener(){
                @Override
                public void replay(MessageListener ml, Message.ARMessage o) {
                    switch(o.getAction()) {
                    	//Start streaming the sensor data
                        case NOTIFY_START:
                            running = true;
                            thread = new Thread(new Push(o));
                            thread.start();
                            break;
                        case NOTIFY_STOP:
                            try {
                                running = false;
                                thread.join();
                            } catch (InterruptedException ex) {
                                Logger.getLogger(SmokePublisher.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            break;
                    }
                }
            });
            // String profile_value = "SmokeDetector";
            // System.out.println("Adding to profile: " + profile_value);
            // ARMessage.Header.Profile profile = ARMessage.Header.Profile.newBuilder().addSingle(profile_value).build();
            //Create sensor profile
            ARMessage.Header.Profile profile = ARMessage.Header.Profile.newBuilder().addSingle("temperature").addSingle("fahrenheit").build();
            ARMessage.Header header = ARMessage.Header.newBuilder().setLatitude(0.00).setLongitude(0.00).setType(ARMessage.RPType.AR_PRODUCER).setProfile(profile).setPeerId(producer.getPeerID()).build();
            ARMessage msg = ARMessage.newBuilder().setHeader(header).setAction(ARMessage.Action.NOTIFY_INTEREST).build();
            //Send the message to the RP
            producer.post(msg, profile);
        } catch (IOException | InterruptedException | NoSuchAlgorithmException | InvalidKeySpecException ex) {
            Logger.getLogger(SmokePublisher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
