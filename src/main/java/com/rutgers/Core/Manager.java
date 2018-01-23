/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rutgers.Core;

import com.rutgers.Android.AndroidPush;
import com.rutgers.DB.RocksDBMS;
import com.rutgers.QuadTree.PointQuadTree;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import net.tomp2p.peers.PeerAddress;

/**
 *
 * @author eduard
 */
public class Manager {
    
    private static Manager instance = null;
    private RP rpOne = null;
    private RP rpTwo = null;
    private long Latency = 0;
    private QueueManager mqueue = null;
    private RocksDBMS dbms = null;
    private LocationKeyManager lkManager = null;
    private static final String stormDir = "stormJAR/";
    private double latitude, longitude = 0.0;
    private AndroidPush push = null;
    private boolean master = false;
    private PointQuadTree<PeerAddress> qTree = null;
    
    
    Manager() {}
    
    public static Manager getInstance() throws IOException {
        if(instance == null) {
            instance = new Manager();
            Files.createDirectories(Paths.get(stormDir));
        }
        return instance;
    }
    
    public void setRpOne(RP rp) {
        rpOne = rp;
    }
    
    public RP getRpOne() {
        return rpOne;
    }
    
    public void setRpTwo(RP rp) {
        rpTwo = rp;
    }
    
    public RP getRpTwo() {
        return rpTwo;
    }
    
    public synchronized void setLatency(long Latency) {
        this.Latency = Latency;
    }

    public synchronized long getLatency() {
        return Latency;
    }
    
    public void setQueueManager(QueueManager mqueue){
        this.mqueue = mqueue;
    }
    
    public QueueManager getQueueManager() {
        return mqueue;
    }
    
    public void setRocksDBMS(RocksDBMS dbms) {
        this.dbms = dbms;
    }
    
    public RocksDBMS getRocksDBMS() {
        return dbms;
    }
    
    public String getStormDir() {
        return stormDir;
    }
    
    public void setLocationKeyManager(LocationKeyManager lkManager){
        this.lkManager = lkManager;
    }
    
    public LocationKeyManager getLocationKeyManager() {
        return lkManager;
    }
    
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
    
    public void setPush(AndroidPush push) {
        this.push = push;
    }

    public AndroidPush getPush() {
        return push;
    }

    public boolean isMaster() {
        return master;
    }

    public void setMaster(boolean master) {
        this.master = master;
    }

    public PointQuadTree<PeerAddress> getqTree() {
        return qTree;
    }

    public void setqTree(PointQuadTree<PeerAddress> qTree) {
        this.qTree = qTree;
    }
}
