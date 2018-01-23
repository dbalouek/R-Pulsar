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
 *
 * @author eduard
 */
public class Publisher { 
    
    static int numRecords = 10000;
    static int recordSize = 200;
    static int iterations = 10;
    static boolean running = false;
    static Thread thread = null;
    static PulsarProducer producer = null;
    
    public static class Push implements Runnable {
        Message.ARMessage msg = null;
        ARMessage.Header.Profile profile = null;
        ARMessage.Header header = null;
        String str;
        
        Push(Message.ARMessage msg) {
            this.msg = msg;
            byte[] data = new byte[200000];
            str = new String(data);
        }
        
        @Override
        public void run() {
            Message.ARMessage push_msg = Message.ARMessage.newBuilder().setAction(Message.ARMessage.Action.STORE_QUEUE).setTopic(msg.getTopic()).addPayload(str).build();

            long start = System.currentTimeMillis();
            for(int i = 0; i < iterations; i ++) {
                try {
                    producer.stream(push_msg, msg.getHeader().getPeerId());
                } catch (NoSuchAlgorithmException | InvalidKeySpecException | UnknownHostException | InterruptedException ex) {
                    Logger.getLogger(Publisher.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
            long ellapsed = System.currentTimeMillis() - start;
            double msgsSec = 1000.0 * numRecords / (double) ellapsed;
            double mbSec = msgsSec * recordSize / (1024.0 * 1024.0);
            System.out.println(start);
            System.out.printf("%d records sent in %d ms ms. %.2f records per second (%.2f mb/sec).", numRecords, ellapsed, msgsSec, mbSec);
        }
    }
    
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
            
            producer.replayListener(new Listener(){
                @Override
                public void replay(MessageListener ml, Message.ARMessage o) {
                    switch(o.getAction()) {
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
                                Logger.getLogger(Publisher.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            break;
                    }
                }
            });
            
            ARMessage.Header.Profile profile = ARMessage.Header.Profile.newBuilder().addSingle("temperature").addSingle("fahrenheit").build();
            ARMessage.Header header = ARMessage.Header.newBuilder().setLatitude(0.00).setLongitude(0.00).setType(ARMessage.RPType.AR_PRODUCER).setProfile(profile).setPeerId(producer.getPeerID()).build();
            ARMessage msg = ARMessage.newBuilder().setHeader(header).setAction(ARMessage.Action.NOTIFY_INTEREST).build();
            producer.post(msg, profile);
            
        } catch (IOException | InterruptedException | NoSuchAlgorithmException | InvalidKeySpecException ex) {
            Logger.getLogger(Publisher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
