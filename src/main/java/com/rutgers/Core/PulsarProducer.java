/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rutgers.Core;

import com.google.protobuf.ProtocolStringList;
import static com.rutgers.Core.Globals._MAX_QUERY_DIM_;
import com.rutgers.Hilbert.Base39;
import com.rutgers.RuleEngine.Rule;
import java.io.IOException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import net.tomp2p.connection.PeerConnection;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;

/**
 *
 * @author eduard
 */
public class PulsarProducer extends Pulsar{
    
    private static final String alphabet = "abcdefghijklmnopqrstuvwxyz0123456789";
        
    public PulsarProducer(Properties args) throws IOException, UnknownHostException, InterruptedException, NoSuchAlgorithmException {
        super(args);
    }
    
    public void post(Message.ARMessage msg, Message.ARMessage.Header.Profile profile) throws NoSuchAlgorithmException, InvalidKeySpecException, InterruptedException {

        Map<Integer, ArrayList> array = new HashMap<>();
        
        ProtocolStringList payloadList = msg.getPayloadList();
        List<String> singleList = profile.getSingleList();
        
        int j = 0;
        int total = 0;
        while (singleList.size() > j) {
            String single = singleList.get(j);
            if(single.contains("*")) {
                ArrayList value = new ArrayList();
                
                for(int i = 0; i < alphabet.length(); i++) {
                    String replace = single.replace('*', alphabet.charAt(i));
                    long decodeBase39 = Base39.decodeBase39(replace);
                    value.add(decodeBase39);
                }

                if(total == 0){
                    total = 36;
                }else {
                    total = total * 36;
                }
                
                array.put(j, value);
            }else {
                ArrayList value = new ArrayList();
                long decodeBase39 = Base39.decodeBase39(single);
                value.add(decodeBase39);
                array.put(j, value);
            }
            j++;
        }

        long [] points = new long[array.size()];
        Number160 [] indexs = new Number160[total];
        
        for(int i = 0; i < total; i++) {
            for(int x = 0; x < array.size(); x++) {
                if(array.get(x).size() > i)
                    points[x] = (long) array.get(x).get(i);
                else
                    points[x] = (long) array.get(x).get(0);
            }
            
            indexs[i] = hc.index(points);
        }
        
        //ArrayList<PeerAddress> nearestKey = lkManager.nearestKey(indexs);
        //for(PeerAddress peer : nearestKey) {
        //    rp.sendDirectMessageNonBlocking(peer, msg); 
        //}

        Number160 index = hc.index(singleList, payloadList.toArray(new String[payloadList.size()]));
        PeerAddress peer = lkManager.nearestKey(index);
        rp.sendDirectMessageNonBlocking(peer, msg); 
    }
    
    public void stream(Message.ARMessage msg, String peerId) throws NoSuchAlgorithmException, InvalidKeySpecException, UnknownHostException, InterruptedException {
        PeerAddress exactKey = lkManager.exactKey(new Number160(peerId));
        if(exactKey != null)
            rp.sendDirectMessageNonBlocking(exactKey, msg);
        else 
            System.out.println("Failed to send the message.");
    }
    
    public void store(Message.ARMessage msg, Message.ARMessage.Header.Profile profile) throws NoSuchAlgorithmException, InvalidKeySpecException, InterruptedException {
        ProtocolStringList payloadList = msg.getPayloadList();
        
        List<String> singleList = profile.getSingleList();
        
        Number160 index = hc.index(singleList, payloadList.toArray(new String[payloadList.size()]));
        PeerAddress peer = lkManager.nearestKey(index);
        rp.sendDirectMessageNonBlocking(peer, msg);
    }
    
    public PeerAddress create(String peerId) {
        return lkManager.exactKey(new Number160(peerId));
    }
    
    public void send(PeerConnection conn, Message.ARMessage msg) throws NoSuchAlgorithmException, InvalidKeySpecException {
        rp.sendDirectMessageNonBlocking(conn, msg);
    }
    
    public void post(Message.ARMessage msg, Message.ARMessage.Header.Profile profile, int i) throws NoSuchAlgorithmException, InvalidKeySpecException, InterruptedException {
        PeerAddress peer =  (PeerAddress) lkManager.getCache().values().toArray()[i];
        rp.sendDirectMessageNonBlocking(peer, msg); 
    }
    
    public void addRule(Rule rule) {
        rules.addRule(rule);
    }
    
    public void evaluateRules(Map<String, String> bindings) {
        rules.eval(bindings);
    }
}
