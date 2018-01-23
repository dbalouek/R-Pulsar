/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rutgers.Examples;

import com.google.common.io.Resources;
import com.rutgers.Core.Message;
import com.rutgers.Core.PulsarProducer;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author eduard
 */
public class DHTPublisher {
    
    static PulsarProducer producer = null;
    
    public static void main(String[] args) throws UnknownHostException, ClassNotFoundException {
        try {
            // TODO code application logic here
            InputStream props = Resources.getResource("producer.prop").openStream();
            Properties properties = new Properties();
            properties.load(props);
            
            producer = new PulsarProducer(properties);
            producer.init();

            int size = Integer.parseInt(args[0]);
            int nodesOne = Integer.parseInt(args[1]);
            int nodesTwo = Integer.parseInt(args[2]);
            int nodesTree = Integer.parseInt(args[3]);
            int nodesFour = Integer.parseInt(args[4]);
            int nodesFive = Integer.parseInt(args[5]);
            
            List<String> payloadList = new ArrayList<>();
            for(int i = 0; i < size; i++) {
                String num = String.valueOf(new Random().nextInt(15000));
                while (payloadList.contains(num)) {
                    num = String.valueOf(new Random().nextInt(15000));
                }
                payloadList.add(num);
            }

            Message.ARMessage.Header.Profile profile = Message.ARMessage.Header.Profile.newBuilder().addSingle("temperature").addSingle("fahrenheit").build();
            Message.ARMessage.Header header = Message.ARMessage.Header.newBuilder().setLatitude(0.00).setLongitude(0.00).setType(Message.ARMessage.RPType.AR_PRODUCER).setProfile(profile).setPeerId(producer.getPeerID()).build();
            Message.ARMessage msg = Message.ARMessage.newBuilder().setHeader(header).setAction(Message.ARMessage.Action.STORE_DATA).addAllPayload(payloadList).build();
           
            System.out.println("DHT Insert start: " + System.currentTimeMillis());
            
            for(int i = 0; i < nodesOne; i++)
                producer.post(msg, profile, 0);

            for(int i = 0; i < nodesTwo; i++)
                producer.post(msg, profile, 1);
            
            for(int i = 0; i < nodesTree; i++)
                producer.post(msg, profile, 2);
                        
            for(int i = 0; i < nodesFour; i++)
                producer.post(msg, profile, 3);
            
            for(int i = 0; i < nodesFive; i++)
                producer.post(msg, profile, 4);
            
            
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException | InterruptedException ex) {
            Logger.getLogger(DHTPublisher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
