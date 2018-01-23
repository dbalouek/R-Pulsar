/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rutgers.Core;

import static com.rutgers.Core.Globals.*;
import com.rutgers.Core.Message.ARMessage;
import com.rutgers.Encryption.RSAEncryption;
import com.rutgers.Hilbert.HilbertCurve;
import java.io.IOException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.tomp2p.p2p.Peer;
import net.tomp2p.peers.PeerAddress;

/**
 *
 * @author eduard
 */
public class Pulsar {
    
    BlockingQueue<com.rutgers.Core.Pair<PeerAddress, ARMessage>> messageQueue = null;
    BlockingQueue<ARMessage> pollMessageQueue = null;
    ExecutorService executorService = null;
    List<ConsumerReplyHandler> executorClass = null;
    Thread pingThread = null;
    Manager manager = null;
    HeartBeat ping = null;
    RP rp = null;
    HilbertCurve hcurce = null; 
    RSAEncryption rsa = null;
    Cli c = null;
    HilbertCurve hc = null;
    Properties prop = null;
    LocationKeyManager lkManager = null;
    BlockingQueue<ARMessage> userQueue;
    BlockingQueue<ARMessage> pollQueue;

    public Pulsar(Properties args) throws IOException, UnknownHostException, InterruptedException, NoSuchAlgorithmException {
        rp = new RP(args.getProperty("port"), args.getProperty("keys.dir"));
        executorService = Executors.newFixedThreadPool(_THREAD_POOL_); 
        manager = Manager.getInstance();
        manager.setRpOne(rp);
        hcurce = HilbertCurve.bits(5).dimensions(_MAX_QUERY_DIM_);
        rsa = new RSAEncryption();
        hc = HilbertCurve.bits(_QUERY_BITS_).dimensions(_MAX_QUERY_DIM_);
        prop = args;
        lkManager = new LocationKeyManager();
        executorClass = new ArrayList();
    }
        
    public Pulsar init() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, UnknownHostException, ClassNotFoundException {
//        if(prop.getProperty("start.gui").compareToIgnoreCase("true") == 1) {
//            System.out.println("Starting GUI.");
//            QuadTreeGUI GUI = new QuadTreeGUI();
//            GUI.setupGui();
//        }
        
        manager.setLocationKeyManager(lkManager);
        String server = prop.getProperty("bootstrap.server");
        String[] ipANDport = server.split(",");
        //rp.startBootstrap(server.split(":")[0], server.split(":")[1]);
        rp.startBootstrap(ipANDport);
        Collection<PeerAddress> addressList = rp.getAllKnownPeers();
        Iterator<PeerAddress> iterator = addressList.iterator();
        
        messageQueue = new ArrayBlockingQueue<>(_MAX_QUEUE_SIZE_);
        pollMessageQueue = new ArrayBlockingQueue<>(_MAX_QUEUE_SIZE_);
        
        rp.setupReplyHandler(messageQueue, 1);
        
        messageQueue = new ArrayBlockingQueue<>(_MAX_QUEUE_SIZE_);
        userQueue = new ArrayBlockingQueue<>(_MAX_QUEUE_SIZE_);
        pollQueue = new ArrayBlockingQueue<>(_MAX_QUEUE_SIZE_);
        rp.setupReplyHandler(messageQueue, 1);
        
        for(int i = 0; i < _LOWER_THREAD_POOL_; i++) {
            executorClass.add(new ConsumerReplyHandler(messageQueue, userQueue, pollQueue));
            executorService.execute(executorClass.get(i));
        }

        while(iterator.hasNext()) { 
            PeerAddress peer = iterator.next();
            System.out.println("Sending AR_Hello to " + peer.peerId().toString() + ".");
            Message.ARMessage.Header h = Message.ARMessage.Header.newBuilder().setLatitude(0.00).setLongitude(0.0).setType(Message.ARMessage.RPType.AR_USER).build();
            Message.ARMessage msg = Message.ARMessage.newBuilder().setHeader(h).setAction(Message.ARMessage.Action.HELLO).build();
            int type = (int) rp.sendDirectMessageBlocking(peer, msg);
            if(type == 0)
                lkManager.insertKey(peer.peerId(), peer);
        }
            
        return this;
    }
    
    public void replayListener(Listener o) {
        rp.addMessageListener(o);
    }
    
    public String getPeerID() {
        return rp.getId().toString();
    }
    
    public void shutdown() {
        executorClass.stream().forEach((temp) -> {
            temp.setRunning(false);
        });
        rp.stop();
        executorService.shutdown();
    }
    
    public Peer getPeer() {
        return rp.getPeer();
    }
}
