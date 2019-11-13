/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rutgers.Examples;

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

/**
 * This is and example of the use of the R-Pulsar API.
 * This example shows how to build a R-Pulsar Publisher.
 * @param keys
 * @return
 */
public class Publisher2 { 
    
    static int numRecords = 10000;
    static int recordSize = 200;
    static int iterations = 10;
    static boolean running = false;
    static Thread thread = null;
    static PulsarProducer producer = null;
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws UnknownHostException, ClassNotFoundException {
        try {
            // TODO code application logic here           
            InputStream props = Resources.getResource("producer.prop").openStream();
            Properties properties = new Properties();
            properties.load(props);
            
            producer = new PulsarProducer(properties);
            producer.init();
            
            
            //Create sensor profile
            ARMessage.Header.Profile profile = ARMessage.Header.Profile.newBuilder().addSingle("temperature").addSingle("fahrenheit").addSingle("fahrenheit").build();
            ARMessage.Header header = ARMessage.Header.newBuilder().setLatitude(0.00).setLongitude(0.00).setType(ARMessage.RPType.AR_PRODUCER).setProfile(profile).setPeerId(producer.getPeerID()).build();
            ARMessage msg = ARMessage.newBuilder().setHeader(header).setAction(ARMessage.Action.NOTIFY_INTEREST).build();
            
            
            //Send the message to the RP
            long start = System.nanoTime();
            for(int i =0; i < 10; i++) {
            	producer.post(msg, profile);
            }
            long ellapsed = System.nanoTime() - start;
            System.out.println(ellapsed);
            
        } catch (IOException | InterruptedException | NoSuchAlgorithmException | InvalidKeySpecException ex) {
            Logger.getLogger(Publisher2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
