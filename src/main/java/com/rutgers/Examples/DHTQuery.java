/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rutgers.Examples;

import com.google.common.io.Resources;
import com.rutgers.Core.Message;
import com.rutgers.Core.PulsarConsumer;
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
public class DHTQuery {
    
    static PulsarConsumer consumer = null;
    
    public static void main(String[] args) throws UnknownHostException, ClassNotFoundException {
        try {
            // TODO code application logic here
            InputStream props = Resources.getResource("consumer.prop").openStream();
            Properties properties = new Properties();
            properties.load(props);
            
            consumer = new PulsarConsumer(properties);
            consumer.init();

            Message.ARMessage.Header.Profile profile = Message.ARMessage.Header.Profile.newBuilder().addSingle("temperature").addSingle("fahrenheit").build();
            Message.ARMessage.Header header = Message.ARMessage.Header.newBuilder().setLatitude(0.00).setLongitude(0.00).setType(Message.ARMessage.RPType.AR_PRODUCER).setHDimension(2).setProfile(profile).setPeerId(consumer.getPeerID()).addHID("0x87ffb703f8a94d8").build();
            Message.ARMessage msg = Message.ARMessage.newBuilder().setHeader(header).setAction(Message.ARMessage.Action.STORE_DATA).addPayload("5").build();
            
            System.out.println("DHT Query start: " + System.currentTimeMillis());
            consumer.post(msg);

        } catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException | InterruptedException ex) {
            Logger.getLogger(DHTPublisher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
