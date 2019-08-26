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
* This is also another class that developers will use to communicate with R-Pulsar.
* This class will be used if we want to create an R-Pulsar cosummer.
* 
* @author  Eduard Giber Renart
* @version 1.0
*/
public class PulsarConsumer extends Pulsar{
    
	/**
	 * Init the R-Pulsar instance as an R-Pulsar Consumer.
	 * @param args
	 * @throws IOException
	 * @throws UnknownHostException
	 * @throws InterruptedException
	 * @throws NoSuchAlgorithmException
	 */
    public PulsarConsumer(Properties args) throws IOException, UnknownHostException, InterruptedException, NoSuchAlgorithmException {
        super(args);
    }
    
    /**
     * Method used to send a message without knowing where the reciver is located at.
     * The space filling curve is used to route the message.
     * @param msg The AR message that we want to send.
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws InterruptedException
     */
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
    
    /**
     * Send an AR message to a specific peer. 
     * In this case the space filling curve is not used.
     * @param msg The message that we want to deliver.
     * @param peerId The Peer Id of the recipient of the message.
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws UnknownHostException
     * @throws InterruptedException
     */
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
    
    /**
     * Method used to get messages from a specific peer.
     * @param msg This will contain the response message.
     * @param peerId This is the peer that will be sending the message.
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws UnknownHostException
     * @throws InterruptedException
     */
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
    
    /**
     * Method used to add rules to make decisions.
     * @param rule Need to pass a Rule object.
     */
    public void addRule(Rule rule) {
        rules.addRule(rule);
    }
    
    /**
     * Method used to evaluate the rules against the upcomming data.
     * @param bindings
     */
    public void evaluateRules(Map<String, String> bindings) {
        rules.eval(bindings);
    }
}
