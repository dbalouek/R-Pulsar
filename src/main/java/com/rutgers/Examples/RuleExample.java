/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rutgers.Examples;

import com.google.common.io.Resources;
import com.rutgers.Core.Message;
import com.rutgers.Core.PulsarProducer;
import com.rutgers.RuleEngine.Rule;
import com.rutgers.RuleEngine.Rules;
import com.rutgers.RuleEngine.TriggerProfileReaction;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is and example of the use of the R-Pulsar Rule engine.
 * @author eduard
 *
 */
public class RuleExample {
    
    static PulsarProducer producer = null;
    
    public static void main(String[] args) throws UnknownHostException, ClassNotFoundException {
        try {
            // TODO code application logic here
            InputStream props = Resources.getResource("producer.prop").openStream();
            Properties properties = new Properties();
            properties.load(props);
            
            //Create an R-Pulsar consumer
            producer = new PulsarProducer(properties);
            //Init the consumer
            producer.init();
            
            //Create a header with the profile
            Message.ARMessage.Header.Profile profile = Message.ARMessage.Header.Profile.newBuilder().addSingle("temperature").addSingle("fahrenheit").build();
            Message.ARMessage.Header header = Message.ARMessage.Header.newBuilder().setLatitude(0.00).setLongitude(0.00).setType(Message.ARMessage.RPType.AR_PRODUCER).setProfile(profile).setPeerId(producer.getPeerID()).build();
            //Telling the RP to store the message in the DHT
            Message.ARMessage msg = Message.ARMessage.newBuilder().setHeader(header).setAction(Message.ARMessage.Action.STORE_DATA).build();

            Rules boltRules = new Rules();
            //Add a rule with to check if X > 5 if the rule is fired the message with the AR Profile will be send
            boltRules.addRule(new Rule.Builder().withCondition("X > 5").withConsequence(new TriggerProfileReaction(msg)).withPriority(1).build());
            boltRules.addRule(new Rule.Builder().withCondition("X < 5").withConsequence(new TriggerProfileReaction(msg)).withPriority(1).build());


            
            Map<String, String> bindings = new HashMap<>();
            //The bindings is the data that is coming in from the sensors.
            bindings.put("X", "5");
            bindings.put("Y", "5");
            //Evalues the rules
            boltRules.eval(bindings);
            
            
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException | InterruptedException ex) {
            Logger.getLogger(RuleExample.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
