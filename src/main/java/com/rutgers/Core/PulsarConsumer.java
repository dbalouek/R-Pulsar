/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rutgers.Core;

import com.google.protobuf.ProtocolStringList;
import static com.rutgers.Core.Message.ARMessage.Action.*;
import com.rutgers.RuleEngine.Rule;
import java.io.IOException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;

/**
 *
 * @author eduard
 */
public class PulsarConsumer extends Pulsar{
    
    public PulsarConsumer(Properties args) throws IOException, UnknownHostException, InterruptedException, NoSuchAlgorithmException {
        super(args);
    }
    
    public void post(Message.ARMessage msg) throws NoSuchAlgorithmException, InvalidKeySpecException, InterruptedException {
        if(msg.getAction() == HELLO || msg.getAction() == PROFILE_RESPONSE) {
            System.out.println("Invalid AR Action.");
        } else {
            Message.ARMessage.Header.Profile profile = msg.getHeader().getProfile();
            ProtocolStringList payloadList = msg.getPayloadList();
            List<String> singleList = profile.getSingleList();
            Number160 index = hc.index(singleList, payloadList.toArray(new String[payloadList.size()]));
 //           System.out.println(index);
            PeerAddress peer = lkManager.nearestKey(index);
            rp.sendDirectMessageNonBlocking(peer, msg); 
        }
    }
    
    public void post(Message.ARMessage msg, String peerId) throws NoSuchAlgorithmException, InvalidKeySpecException, UnknownHostException, InterruptedException {
        if(msg.getAction() == HELLO || msg.getAction() == PROFILE_RESPONSE) {
            System.out.println("Invalid AR Action.");
        } else {
        
        PeerAddress exactKey = lkManager.exactKey(new Number160(peerId));
        if(exactKey != null)
            rp.sendDirectMessageNonBlocking(exactKey, msg);
        else 
            System.out.println("Failed to send the message.");
        }
    }
    
    public Message.ARMessage poll(Message.ARMessage msg, String peerId) throws NoSuchAlgorithmException, InvalidKeySpecException, UnknownHostException, InterruptedException {
        if(!pollQueue.isEmpty()) {
            PeerAddress exactKey = lkManager.exactKey(new Number160(peerId));
            if(exactKey != null)
                rp.sendDirectMessageNonBlocking(exactKey, msg);
            else 
                System.out.println("Failed to send the message.");
            
            return pollQueue.take();
        } else  {
            PeerAddress exactKey = lkManager.exactKey(new Number160(peerId));
            if(exactKey != null)
                rp.sendDirectMessageNonBlocking(exactKey, msg);
            else 
                System.out.println("Failed to send the message.");

            rp.sendDirectMessageNonBlocking(exactKey, msg);
            return pollQueue.take();
        }
    }
    
    public void addRule(Rule rule) {
        rules.addRule(rule);
    }
    
    public void evaluateRules(Map<String, String> bindings) {
        rules.eval(bindings);
    }
}
