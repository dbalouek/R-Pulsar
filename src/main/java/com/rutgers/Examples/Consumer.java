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
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
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
                    Message.ARMessage poll = consumer.poll(consum_msg, msg.getHeader().getPeerId());
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Publisher.class.getName()).log(Level.SEVERE, null, ex);
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
            // TODO code application logic here          
            InputStream props = Resources.getResource("consumer.prop").openStream();
            Properties properties = new Properties();
            properties.load(props);
            
            consumer = new PulsarConsumer(properties);
            consumer.init();
            
            consumer.replayListener(new Listener(){
                @Override
                public void replay(MessageListener ml, Message.ARMessage o) {
                    switch(o.getAction()) {
                        case NOTIFY_START:
                            running = true;
                            thread = new Thread(new Consum(o));
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
                        case REQUEST_RESPONSE:
                           
                            break;
                    }
                }
            });
            
            Message.ARMessage.Header.Profile profile = Message.ARMessage.Header.Profile.newBuilder().addSingle("temperature").addSingle("fahrenheit").build();
            Message.ARMessage.Header header = Message.ARMessage.Header.newBuilder().setLatitude(0.00).setLongitude(0.00).setType(Message.ARMessage.RPType.AR_CONSUMER).setProfile(profile).setPeerId(consumer.getPeerID()).build();
            Message.ARMessage msg = Message.ARMessage.newBuilder().setHeader(header).setAction(Message.ARMessage.Action.NOTIFY_DATA).build();
            consumer.post(msg);
            
        } catch (IOException | InterruptedException | NoSuchAlgorithmException | InvalidKeySpecException ex) {
            Logger.getLogger(Publisher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
