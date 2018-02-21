/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rutgers.Core;

import com.rutgers.Core.Message.ARMessage;
import com.rutgers.DB.RocksDHT;
import com.rutgers.Encryption.PublicKeyManager;
import com.rutgers.Encryption.UserProfile;
import com.rutgers.Tree.ThreadedBinarySearchTree;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Collection;
import java.util.List;
import java.util.NavigableMap;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.tomp2p.connection.Bindings;
import net.tomp2p.connection.DiscoverNetworks;
import net.tomp2p.connection.PeerConnection;
import net.tomp2p.dht.FutureGet;
import net.tomp2p.dht.FuturePut;
import net.tomp2p.dht.PeerBuilderDHT;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.dht.RemoveBuilder;
import net.tomp2p.dht.StorageLayer;
import net.tomp2p.futures.BaseFuture;
import net.tomp2p.futures.BaseFutureListener;
import net.tomp2p.futures.FutureBootstrap;
import net.tomp2p.futures.FutureDirect;
import net.tomp2p.futures.FutureDiscover;
import net.tomp2p.p2p.Peer;
import net.tomp2p.p2p.PeerBuilder;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.Number640;
import net.tomp2p.peers.PeerAddress;
import net.tomp2p.replication.IndirectReplication;
import net.tomp2p.storage.Data;

/**
 *
 * @author eduard
 */
public class RP extends MessageListener {

    private Bindings bindings = null;
    private Peer peer = null;
    private PeerDHT peerDHT = null;
    private FutureBootstrap futureBootstrap = null;
    private FutureDiscover futureDiscover = null;
//    private PeerAddress peerAddress = null;
    private InetAddress bootstrap_Ip; 
    private FutureDirect futureDirect = null;
//    private FutureResponse futureResponse = null;
    private Number160 id = null;
    private ProducerReplyHandler producerHandler = null;
    private FuturePut futurePut = null;
    private FutureGet futureGet = null;
//    private FutureChannelCreator futurecc = null;
//    private ChannelCreator channelCreator = null;
    private UserProfile uProfile = null;
    private PublicKeyManager keyManager = null;
    private final PeerAddress selfPeerAddress = null;
    public String sDir;
    public int port, bootstrap_Port;
    public int replicationFactor;
    
    public RP(String port, String dir) throws UnknownHostException, IOException, InterruptedException, NoSuchAlgorithmException {
        bindings = new Bindings().listenAny();
        this.port = Integer.valueOf(port);
        uProfile = new UserProfile("", dir);
        sDir = dir;
    } 
    
    public void startDHTMaster(int replicationFactor) throws IOException {
        peer = new PeerBuilder(uProfile.getEncryptionKeys()).bindings(bindings).ports(port).start();    
        peerDHT = new PeerBuilderDHT(peer).storageLayer(new StorageLayer(new RocksDHT(peer.peerID()), new ThreadedBinarySearchTree(Number640.ZERO))).start();    
        this.replicationFactor = replicationFactor;
        
        id = peer.peerID();
        System.out.println("DHT Master started Listening to: " + DiscoverNetworks.discoverInterfaces(bindings));
        System.out.println("Address visible outside is " + peer.peerAddress());
        
//        futurecc = peer.connectionBean().reservation().create(1,1);
//        futurecc.awaitUninterruptibly();
//        channelCreator = futurecc.channelCreator();
//        
//        selfPeerAddress = peer.peerAddress();
        
        keyManager = new PublicKeyManager(id.toString(),uProfile.getEncryptionKeys(), peerDHT, peer);
        new IndirectReplication(peerDHT).replicationFactor(replicationFactor).start();
    }
                
    public void startDHTBootstrap(String bootstrap_ip, String bootstrap_port, int replicationFactor) throws UnknownHostException, IOException {
        bootstrap_Ip = InetAddress.getByName(bootstrap_ip);
        bootstrap_Port = Integer.parseInt(bootstrap_port);
        this.replicationFactor = replicationFactor; 
        
        peer = new PeerBuilder(uProfile.getEncryptionKeys()).bindings(bindings).ports(port).start();    
        peerDHT = new PeerBuilderDHT(peer).storageLayer(new StorageLayer(new RocksDHT(peer.peerID()), new ThreadedBinarySearchTree(Number640.ZERO))).start();
        id = peer.peerID();
        System.out.println("DHT Bootstrap started Listening to: " + DiscoverNetworks.discoverInterfaces(bindings));
        System.out.println("Address visible outside is " + peer.peerAddress());
        
        futureDiscover = peer.discover().expectManualForwarding().inetAddress(bootstrap_Ip).ports(bootstrap_Port).start();
        futureDiscover.awaitUninterruptibly();
        
        futureBootstrap = peer.bootstrap().inetAddress(bootstrap_Ip).ports(bootstrap_Port).start();
        futureBootstrap.awaitUninterruptibly();
        
        if (futureDiscover.isSuccess()) {
            System.out.println("*** FOUND THAT MY OUTSIDE ADDRESS IS " + futureDiscover.peerAddress() + " ***");
        } else {
            System.out.println("*** FAILED " + futureDiscover.failedReason() + " ***");
        }
        
//        futurecc = peer.connectionBean().reservation().create(1,1);
//        futurecc.awaitUninterruptibly();
//        channelCreator = futurecc.channelCreator();
//        
//        selfPeerAddress = peer.peerAddress();
        
        keyManager = new PublicKeyManager(id.toString(),uProfile.getEncryptionKeys(), peerDHT, peer);
        new IndirectReplication(peerDHT).replicationFactor(replicationFactor).start();
    }
    
    public void startBootstrap(String[] bootstrap_ip_port) throws IOException {

        peer = new PeerBuilder(uProfile.getEncryptionKeys()).bindings(bindings).ports(port).start(); 
        id = peer.peerID();
        System.out.println("Peer Bootstrap started Listening to: " + DiscoverNetworks.discoverInterfaces(bindings));
        System.out.println("Address visible outside is " + peer.peerAddress());
        
        for(int i = 0; i < bootstrap_ip_port.length; i ++) {
            
            String bootstrap_ip = bootstrap_ip_port[i].split(":")[0];
            String bootstrap_port = bootstrap_ip_port[i].split(":")[1];
            
            bootstrap_Ip = InetAddress.getByName(bootstrap_ip);
            bootstrap_Port = Integer.parseInt(bootstrap_port);

            futureDiscover = peer.discover().expectManualForwarding().inetAddress(bootstrap_Ip).ports(bootstrap_Port).start();
            futureDiscover.awaitUninterruptibly();

            futureBootstrap = peer.bootstrap().inetAddress(bootstrap_Ip).ports(bootstrap_Port).start(); 
            futureBootstrap.awaitUninterruptibly();
        }
        
        
        if (futureDiscover.isSuccess()) {
            System.out.println("*** FOUND THAT MY OUTSIDE ADDRESS IS " + futureDiscover.peerAddress() + " ***");
        } else {
            System.out.println("*** FAILED " + futureDiscover.failedReason() + " ***");
        }
        
//        futurecc = peer.connectionBean().reservation().create(1,1);
//        futurecc.awaitUninterruptibly();
//        channelCreator = futurecc.channelCreator();
//        
//        selfPeerAddress = peer.peerAddress();
        
        keyManager = new PublicKeyManager(id.toString(),uProfile.getEncryptionKeys(), null, peer);
    }
    
    public List<PeerAddress> getAllKnownPeers() {
        return peer.peerBean().peerMap().all();
    }
    
    public Object sendDirectMessageBlocking(PeerAddress addr, Message.ARMessage msg) throws UnknownHostException, ClassNotFoundException, IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        futureDirect = peer.sendDirect(addr).object(msg).start();
        futureDirect.awaitUninterruptibly();
        return futureDirect.object();
    }
    
    public void sendDirectMessageNonBlocking(PeerAddress addr, Message.ARMessage msg) throws NoSuchAlgorithmException, InvalidKeySpecException {        
        futureDirect = peer.sendDirect(addr).object(msg).start();
        futureDirect.addListener(new BaseFutureListener() {
            @Override
            public void operationComplete(BaseFuture f) throws Exception {
//                System.out.println(System.currentTimeMillis());
            }

            @Override
            public void exceptionCaught(Throwable thrwbl) throws Exception {
            }
        });
    }
    
    public void sendDirectMessageNonBlocking(PeerConnection conn, Message.ARMessage msg) throws NoSuchAlgorithmException, InvalidKeySpecException {        
        futureDirect = peer.sendDirect(conn).object(msg).start();
        futureDirect.addListener(new BaseFutureListener() {
            @Override
            public void operationComplete(BaseFuture f) throws Exception {
//                System.out.println(System.currentTimeMillis());
            }

            @Override
            public void exceptionCaught(Throwable thrwbl) throws Exception {
            }
        });
    }
    
    public void sendDirectMessageNonBlocking(PeerAddress addr, byte[] msg) throws NoSuchAlgorithmException, InvalidKeySpecException {
        futureDirect = peer.sendDirect(addr).object(msg).start();
        futureDirect.addListener(new BaseFutureListener() {
            @Override
            public void operationComplete(BaseFuture f) throws Exception {
//                System.out.println(System.currentTimeMillis());
            }

            @Override
            public void exceptionCaught(Throwable thrwbl) throws Exception {
            }
        });
    }
    
//    public void sendDirectMessageRPC(PeerAddress addr, SendDirectBuilder builder) {
//        SendDirectBuilder s = new SendDirectBuilder(peer, addr);
//        RequestHandler<FutureResponse> sendInternal = peer.directDataRPC().sendInternal(addr, s);
//        sendInternal.sendTCP(channelCreator);
//
//        futureResponse = peer.directDataRPC().send(addr, builder, channelCreator);
//        futureResponse.addListener(new BaseFutureListener() {
//            @Override
//            public void operationComplete(BaseFuture f) throws Exception {
//            }
//
//            @Override
//            public void exceptionCaught(Throwable thrwbl) throws Exception {
//            }
//        });
//    }
    
    public void setupReplyHandler(BlockingQueue<com.rutgers.Core.Pair<PeerAddress, ARMessage>> queue, int type) {
        try {
            producerHandler = new ProducerReplyHandler(queue, type);
            peer.objectDataReply(producerHandler);
        } catch (IOException ex) {
            Logger.getLogger(RP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void putDHT(Number160 locationKey, Number160 domainKey, NavigableMap<Number640, Data> content) {
        futurePut = peerDHT.put(locationKey).dataMap(content).domainKey(domainKey).start();
        futurePut.awaitUninterruptibly();
    }
    
    public void putDHT(Number160 locationKey, Number160 domainKey, Object content) {
        try {
            futurePut = peerDHT.put(locationKey).object(content).domainKey(domainKey).start();
            futurePut.awaitUninterruptibly();
        } catch (IOException ex) {
            Logger.getLogger(RP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public FutureGet getDHT(int id, Number160 domainKey) {
        Number160 contentHash = Number160.createHash(id);
        futureGet = peerDHT.get(contentHash).all().start();
        futureGet.awaitUninterruptibly();
        return futureGet;
    }
    
    public FutureGet getDHT(Number160 id, Number160 domainKey) {
        futureGet = peerDHT.get(id).all().start();
        futureGet.awaitUninterruptibly();
        return futureGet;
    }
    
    public Data getDHT(Number160 locationKey, Number160 domainKey, Collection<Number160>  hids) { 
        futureGet = peerDHT.get(locationKey).contentKeys(hids).start();
        //futureGet = peerDHT.get(locationKey).from(Number640.ZERO).to(Number640.ZERO).start();
        futureGet.awaitUninterruptibly();
        return futureGet.data();
    }
    
    public FutureGet getDHT(Number160 locationKey, Number640 contentFrom, Number640 contentTo) {
        futureGet = peerDHT.get(locationKey).from(contentFrom).to(contentTo).start();
        futureGet.awaitUninterruptibly();
        return futureGet;
    }
    
    public void removeDHT(Number160 locationKey) {
        RemoveBuilder remove = peerDHT.remove(locationKey);
    }

    public Number160 getId() {
        return id;
    }
    
    public Peer getPeer() {
        return peer;
    }
    
    public PeerDHT getPeerDHT() {
        return peerDHT;
    }
    
    public void stop() {
        peer.shutdown();
    }
    
    public PublicKeyManager getPublicKeyManager() {
        return keyManager;
    }
    
    public UserProfile getUserProfile() {
        return uProfile;
    }
    
    public PeerAddress getPeerAddress() {
        return selfPeerAddress;
    }
}