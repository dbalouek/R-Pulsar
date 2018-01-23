/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rutgers.Core;

import com.google.protobuf.ProtocolStringList;
import java.io.IOException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Properties;
import net.tomp2p.connection.PeerConnection;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;

/**
 *
 * @author eduard
 */
public class PulsarProducer extends Pulsar{
    
    public PulsarProducer(Properties args) throws IOException, UnknownHostException, InterruptedException, NoSuchAlgorithmException {
        super(args);
    }
    
    public void post(Message.ARMessage msg, Message.ARMessage.Header.Profile profile) throws NoSuchAlgorithmException, InvalidKeySpecException, InterruptedException {
        ProtocolStringList payloadList = msg.getPayloadList();
        Number160 index = hc.index(profile, payloadList.toArray(new String[payloadList.size()]));
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
        Number160 index = hc.index(profile, payloadList.toArray(new String[payloadList.size()]));
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
}
