/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rutgers.Core;

import static com.rutgers.Core.Globals._MAX_QUERY_DIM_;
import static com.rutgers.Core.Globals._QUERY_BITS_;
import com.rutgers.Core.Message.ARMessage;
import com.rutgers.Core.Message.ARMessage.RPType;
import com.rutgers.DB.RocksDBMS;
import com.rutgers.Hilbert.HilbertCurve;
import com.rutgers.QuadTree.PointQuadTree;
import com.rutgers.Storm.Storm;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.Number640;
import net.tomp2p.peers.PeerAddress;
import net.tomp2p.storage.Data;

/**
* This class reacts to the messages received by other peers. 
*
* @author  Eduard Giber Renart
* @author  Daniel Balouek-Thomert
* @version 1.0
*/
public class ConsumerReplyHandler implements Runnable{
    private final BlockingQueue<com.rutgers.Core.Pair<PeerAddress, ARMessage>> queue;
    private Boolean running;
    private QueueManager queueManager = null;
    private final RP peer, peerTwo;
    private Number160 Hid;
    private Manager manager = null;
    private RocksDBMS dbms = null;
    private HilbertCurve hc = null;
    private LocationKeyManager lk = null;
    private BlockingQueue<ARMessage> userQueue = null;
    private BlockingQueue<ARMessage> pollQueue = null;
    private PointQuadTree<PeerAddress> qTree = null;
 
	   /**
	   * This is the main method which instantiates all the necessary components 
	   * @param args The arguments need to follow the CLI implementation.
	   * @return Nothing.
	   */
    public ConsumerReplyHandler(BlockingQueue<com.rutgers.Core.Pair<PeerAddress, ARMessage>> queue, BlockingQueue<ARMessage> userQueue, BlockingQueue<ARMessage> pollQueue) throws IOException {
        this.queue = queue;
        this.running = true;
        this.userQueue = userQueue;
        this.pollQueue = pollQueue;
        queueManager = Manager.getInstance().getQueueManager();
        peer = Manager.getInstance().getRpOne();
        peerTwo = Manager.getInstance().getRpTwo();
        manager = Manager.getInstance();
        dbms = manager.getRocksDBMS();
        hc = HilbertCurve.bits(_QUERY_BITS_).dimensions(_MAX_QUERY_DIM_);
        lk = manager.getLocationKeyManager();
        qTree = manager.getqTree();
    }
    
    /**
     * This method return if the class is currently running.
     * @return If the thread is currently running.
     */
    public Boolean getRunning() {
        return running;
    }

    /**
     * This method is to start or stop the thread.
     * @param running set to true or false to start or stop the thread
     */
    public void setRunning(Boolean running) {
        this.running = running;
    }
 
    /**
     * This is where to thread loops everytime we receive a message.
     * The first set of message types are for the RP's and then the others are used for the peers.
     */
    @Override
    public void run() {
        while (running) {
            try {
                com.rutgers.Core.Pair<PeerAddress, ARMessage> msg = queue.poll();
                if(msg != null) {
                ARMessage ARMsg = msg.getElement1(); 
                switch(ARMsg.getAction()) {
	                /**
	                 * Push message to the local queue.
	                 */
                    case STORE_QUEUE: 
                        if(!"".equals(ARMsg.getTopic())) {
                            queueManager.addToQueue(ARMsg.getTopic(), ARMsg.toByteArray());
                            System.out.println(System.currentTimeMillis());
                        }else 
                            System.out.println("Message not inserted missing queue name.");
                        break;
                    case NOTIFY_DATA:
                        System.out.println("Notify Data");
                        Hid = hc.index(ARMsg.getHeader().getProfile());
                        ArrayList<byte[]> rd = dbms.getInterest(Hid);
                        /**
                         * Check in the local database if a similar profile exists if not just store it.
                         */
                        if(rd.isEmpty()) { 
                            dbms.putData(ARMsg.getHeader().toByteArray(),Hid);
                            PeerAddress addr = PeerAddress.class.cast(msg.getElement0());
                            manager.getLocationKeyManager().insertKey(addr.peerId(),addr);
                        }else {
                            /**
                             * A match exists then notify both peers to start streaming.
                             */
                            System.out.println("Match");
                            QueueManager q = manager.getQueueManager();
                            String topic = q.randomTopic();
                            q.createQueue(topic, topic);

                            String id = "";
                            if(manager.isMaster() && manager.getRpTwo() != null)
                                id = manager.getRpTwo().getId().toString();
                            else 
                                id = manager.getRpOne().getId().toString();

                            ARMessage.Header h = ARMessage.Header.newBuilder().setLatitude(manager.getLatitude()).setLongitude(manager.getLongitude()).setType(ARMessage.RPType.AR_RP).setPeerId(id).build();
                            ARMessage pr = ARMessage.newBuilder().setHeader(h).setAction(ARMessage.Action.NOTIFY_START).setTopic(topic).build();
                            peer.sendDirectMessageNonBlocking(PeerAddress.class.cast(msg.getElement0()), pr);
                            //Send to second one
                            for (int i = 0; i < rd.size(); i++) {
                                ARMessage.Header m = ARMessage.Header.parseFrom(rd.get(i));
                                PeerAddress exactKey = lk.exactKey(new Number160(m.getPeerId()));
                                if(exactKey != null)
                                    peer.sendDirectMessageNonBlocking(exactKey, pr);
                            }
                        }
                        break;
                    case NOTIFY_INTEREST:
                        System.out.println("Notify Interest");
                        Hid = hc.index(ARMsg.getHeader().getProfile());
                        ArrayList<byte[]> ri = dbms.getData(Hid);
                        if(ri.isEmpty()) {
                            /**
                             * Check in the local database if a similar profile exists if not just store it.
                             */
                            dbms.putInterest(ARMsg.getHeader().toByteArray(), Hid);
                            PeerAddress addr = PeerAddress.class.cast(msg.getElement0());
                            manager.getLocationKeyManager().insertKey(addr.peerId(),addr);
                        }else {
                        	/**
                             * A match exists then notify both peers to start streaming.
                             */
                            System.out.println("Match");
                            QueueManager q = manager.getQueueManager();
                            String topic = q.randomTopic();
                            q.createQueue(topic, topic);

                            String id = "";
                            if(manager.isMaster())
                                id = manager.getRpTwo().getId().toString();
                            else 
                                id = manager.getRpOne().getId().toString();

                            ARMessage.Header h = ARMessage.Header.newBuilder().setLatitude(manager.getLatitude()).setLongitude(manager.getLongitude()).setType(ARMessage.RPType.AR_RP).setPeerId(id).build();
                            ARMessage pr = ARMessage.newBuilder().setHeader(h).setAction(ARMessage.Action.NOTIFY_START).setTopic(topic).build();
                            peer.sendDirectMessageNonBlocking(PeerAddress.class.cast(msg.getElement0()), pr);
                            //Send to second one
                            for (int i = 0; i < ri.size(); i++) {
                                ARMessage.Header m = ARMessage.Header.parseFrom(ri.get(i));
                                PeerAddress exactKey = lk.exactKey(new Number160(m.getPeerId()));
                                if(exactKey != null)
                                    peer.sendDirectMessageNonBlocking(exactKey, pr);
                            }
                        }
                        break;
                    case NOTIFY_DATA_ANDROID:
                        break;
                    /**
                     * Delete profile that the peer previously send.
                     */
                    case DELETE_INTEREST:
                        Hid = hc.index(ARMsg.getHeader().getProfile());
                        dbms.deleteInterest(Hid);
                        break;
                    /**
                     * Delete data that peer send.
                     */
                    case DELETE_DATA:
                        //Fix
                        peer.removeDHT(Number160.ONE);
                        break;
                    /**
                     * Save the storm topology for later execusion.
                     */
                    case STORE_STORM_TOPOLOGY:
                        String dir = manager.getStormDir();
                        List<String> payloadJAR = ARMsg.getPayloadList();
                        String str = payloadJAR.get(1);
                        String full = dir + str ;
                        System.out.println(full);
                        Utils.writeBytesToFile(payloadJAR.get(0).getBytes(), full);
                        break;
                    /**
                     * Start the storm topology.
                     */
                    case START_STORM_TOPOLOGY:
                        List<String> payloadName = ARMsg.getPayloadList();
                        String name = payloadName.get(0);
                        String mainclass = payloadName.get(1);
                        String arg = payloadName.get(2);
                        Path path = Paths.get(manager.getStormDir() + name);
                        if (Files.exists(path)) {
                            Storm.StartTopology(manager.getStormDir() + name, mainclass, arg);
                        }else {
                            System.out.println("Jar does not exist");
                        }
                        break;
                    /**
                     * Stop the storm topology.
                     */
                    case STOP_STORM_TOPOLOGY:
                        List<String> stop = ARMsg.getPayloadList();
                        Storm.KillTopology(manager.getStormDir() + stop.get(0));
                        break;
                    case STORE_EDGENT_TOPOLOGY:
                        dir = manager.getStormDir();
                        payloadJAR = ARMsg.getPayloadList();
                        str = payloadJAR.get(1);
                        full = dir + str ;
                        System.out.println(full);
                        Utils.writeBytesToFile(payloadJAR.get(0).getBytes(), full);
                        break;
                    case START_EDGENT_TOPOLOGY:
                        payloadName = ARMsg.getPayloadList();
                        name = payloadName.get(0);
                        mainclass = payloadName.get(1);
                        arg = payloadName.get(2);
                        path = Paths.get(manager.getStormDir() + name);
                        if (Files.exists(path)) {
                            Utils.executeCommand("java -cp " + path + " " + arg);
                        }else {
                            System.out.println("Jar does not exist");
                        }
                        break;
                    case STOP_EDGENT_TOPOLOGY:
                        stop = ARMsg.getPayloadList();
                        Utils.executeCommand("kill -9 -f" + stop.get(0));
                        break;    
                    case REQUEST:
                        if(!"".equals(ARMsg.getTopic())) {
                            byte[] payload = queueManager.removeFromQueue(ARMsg.getTopic());

                            while(payload == null) {
                                payload = queueManager.removeFromQueue(ARMsg.getTopic());
                            }

                            String id = "";
                            if(manager.isMaster() && manager.getRpTwo() != null)
                                id = manager.getRpTwo().getId().toString();
                            else 
                                id = manager.getRpOne().getId().toString();

                            
                            ARMessage parseFrom = ARMessage.parseFrom(payload);
                            ARMessage.Header h = ARMessage.Header.newBuilder().setLatitude(manager.getLatitude()).setLongitude(manager.getLongitude()).setType(ARMessage.RPType.AR_RP).setPeerId(id).build();
                            ARMessage pr = ARMessage.newBuilder().setHeader(h).setAction(ARMessage.Action.REQUEST_RESPONSE).addPayload(parseFrom.toString()).build();
                            peer.sendDirectMessageNonBlocking(PeerAddress.class.cast(msg.getElement0()), pr);
                        } else {
                            System.out.println("Message not inserted missing queue name.");
                        }
                        break;
                    /**
                     * All the following messages are used by the peers.
                     */    
                    /** 
                     * New peer added to the network, we need to add it to the quadtree
                     */
                    case HELLO:
                        ARMessage.Header header = ARMsg.getHeader();
                        if(manager.isMaster() && header.getType().equals(RPType.AR_RP)) {
                            PeerAddress addr = PeerAddress.class.cast(msg.getElement0());
                            qTree.insert(header.getLatitude(), header.getLongitude(), addr, "https://png.icons8.com/raspberry-pi/color/48");
                        } 
                      
                        if(header.getType().equals(RPType.AR_RP)) {
                            PeerAddress addr = PeerAddress.class.cast(msg.getElement0());
                            lk.insertKey(addr.peerId(), addr);
                        }
                        break;
                    /**
                     * Send back all the profiles that are currently stored in the RP.
                     */
                    case PROFILE_REQUEST:
                        Hid = hc.index(ARMsg.getHeader().getProfile());
                        ArrayList<byte[]> result = dbms.getData(Hid);

                        ArrayList<String> bstr = new ArrayList<String>();
                        Iterator<byte[]> itr = result.iterator();
                        while (itr.hasNext()) {
                            bstr.add(new String(itr.next()));
                        }

                        ARMessage.Header h = ARMessage.Header.newBuilder().setLatitude(manager.getLatitude()).setLongitude(manager.getLongitude()).setType(ARMessage.RPType.AR_RP).build();
                        ARMessage pr = ARMessage.newBuilder().setHeader(h).setAction(ARMessage.Action.PROFILE_RESPONSE).addAllPayload(bstr).build();
                        peer.sendDirectMessageNonBlocking(PeerAddress.class.cast(msg.getElement0()), pr);
                        break;
                    case PROFILE_RESPONSE:
                        userQueue.put(ARMsg);
                        break;
                    case NOTIFY_START:
                        peer.setChanged();
                        peer.notifyUsers(ARMsg);
                        break;
                    case NOTIFY_STOP:
                        peer.setChanged();
                        peer.notifyUsers(ARMsg);
                        break;
                    case REQUEST_RESPONSE:
                        pollQueue.put(ARMsg);
                        break;
                    /** 
                     * Store data in the local DHT.
                     */
                    case STORE_DATA:
                        NavigableMap<Number640, Data> map = new TreeMap();
                        Number160 locationKey = null;
                        List<String> dataPayload = ARMsg.getPayloadList();
                        for (int i = 0; i < dataPayload.size(); i++) {      
                            byte[] data = dataPayload.get(i).getBytes();
                            int dim = ARMsg.getHeader().getProfile().getSingleCount();//+ 1;
                            HilbertCurve curve = HilbertCurve.bits(_QUERY_BITS_).dimensions(dim);
                            List<String> singleList =  ARMsg.getHeader().getProfile().getSingleList();
                            locationKey = curve.index(singleList, dataPayload.get(i));
                            map.put(new Number640(locationKey, Number160.ZERO, Number160.ZERO, Number160.ZERO), new Data(data));
                        }
                        
                        Number160 domainKey;
                        //String domain = ARMsg.getHeader().getDomain();
                        //if(!domain.isEmpty())
                        //    domainKey = new Number160(domain.getBytes());
                        //else
                            domainKey = Number160.ZERO;
                        
                        if(peerTwo != null) {
                            peerTwo.putDHT(locationKey, domainKey, map);
                        }else {
                            peer.putDHT(locationKey, domainKey, map);
                        }
                        break;
                    /**
                     * Stop the peer.
                     */
                    case STOP:
                        peer.stop();
                        break;
                    /**
                     * Get data from the DHT.
                     */
                    case QUERY:
                        List<Number160> qmap = new ArrayList();
                        List<String> qdataPayload = ARMsg.getHeader().getHIDList();
                        Number160 lK = null;
                        for (int i = 0; i < qdataPayload.size(); i++) {      
                            lK = new Number160(qdataPayload.get(i).getBytes());
                            qmap.add(lK);
                        }
                        
                        //domain = ARMsg.getHeader().getDomain();
                        //if(!domain.isEmpty())
                        //    domainKey = new Number160(domain.getBytes());
                        //else
                            domainKey = Number160.ZERO;
                        
                        if(peerTwo != null) {
                           peerTwo.getDHT(lK, domainKey, qmap);
                        }else {
                           peer.getDHT(lK, domainKey, qmap);
                        }
                        break;
                    /**
                     * A new region has been created so we need to updated the peers in our network.
                     */
                    case UPDATE:
                        int port = peer.port + 2;
                        int replication = peer.replicationFactor;
                        String stormDir = peer.sDir;

                        if(manager.getRpOne() != null) {
                            manager.getRpOne().stop();
                        }

                        if(manager.getRpTwo() != null) {
                            manager.getRpTwo().stop();
                        }

                        String bootstrap = ARMsg.getPayload(0);
                        String tree = ARMsg.getPayload(1);

                        if(ARMsg.getPayloadCount() >= 3) {
                            String[] split = tree.split(",");
                            PointQuadTree<PeerAddress> qTree = new PointQuadTree<PeerAddress>(Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]), 4, 4);
                            manager.setqTree(qTree);
                            RP rp = new RP(Integer.toString(port), stormDir);
                            rp.startDHTMaster(replication);
                            manager.setRpTwo(rp);
                        }else {
                            RP rp = new RP(Integer.toString(port), stormDir);
                            String[] split = bootstrap.split(":");
                            rp.startDHTBootstrap(split[0], split[1], replication);
                            manager.setRpOne(rp);

                            ARMessage.Header.Profile.Builder p = ARMessage.Header.Profile.newBuilder();
                            ARMessage.Header he = ARMessage.Header.newBuilder().setLatitude(manager.getLatitude()).setLongitude(manager.getLongitude()).setType(ARMessage.RPType.AR_RP).setProfile(p).build();
                            ARMessage armsg = ARMessage.newBuilder().setHeader(he).setAction(ARMessage.Action.HELLO).build();
                            rp.sendDirectMessageNonBlocking(rp.getAllKnownPeers().get(0), armsg);
                        }
                        break;
                        /**
                         * A new region has been created so we need to updated the peers in our network.
                         */
                    /*case SCHEDULING_PATH: // First attemp at establish a path
                        int port = peer.port + 2;
                        int replication = peer.replicationFactor;
                        String stormDir = peer.sDir;

                        if(manager.getRpOne() != null) {
                            manager.getRpOne().stop();
                        }

                        if(manager.getRpTwo() != null) {
                            manager.getRpTwo().stop();
                        }

                        String bootstrap = ARMsg.getPayload(0);
                        String tree = ARMsg.getPayload(1);

                        if(ARMsg.getPayloadCount() >= 3) {
                            String[] split = tree.split(",");
                            PointQuadTree<PeerAddress> qTree = new PointQuadTree<PeerAddress>(Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]), 4, 4);
                            manager.setqTree(qTree);
                            RP rp = new RP(Integer.toString(port), stormDir);
                            rp.startDHTMaster(replication);
                            manager.setRpTwo(rp);
                        }else {
                            RP rp = new RP(Integer.toString(port), stormDir);
                            String[] split = bootstrap.split(":");
                            rp.startDHTBootstrap(split[0], split[1], replication);
                            manager.setRpOne(rp);

                            ARMessage.Header.Profile.Builder p = ARMessage.Header.Profile.newBuilder();
                            ARMessage.Header he = ARMessage.Header.newBuilder().setLatitude(manager.getLatitude()).setLongitude(manager.getLongitude()).setType(ARMessage.RPType.AR_RP).setProfile(p).build();
                            ARMessage armsg = ARMessage.newBuilder().setHeader(he).setAction(ARMessage.Action.HELLO).build();
                            rp.sendDirectMessageNonBlocking(rp.getAllKnownPeers().get(0), armsg);
                        }
                        break;*/
                }
                }
            } catch (InterruptedException | IOException | NoSuchAlgorithmException | InvalidKeySpecException ex) {
                Logger.getLogger(ConsumerReplyHandler.class.getName()).log(Level.SEVERE, null, ex);
            } 
            
        }
    }
}
