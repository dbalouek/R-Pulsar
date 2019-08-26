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
* This class is singelton class that will store values 
* and parameters that will be need it at runtime.
* This class is only instantated once.
*
* @author  Eduard Giber Renart
* @version 1.0
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
    /**
     * Creates the singelton class
     * @return
     * @throws IOException
     */
    public static Manager getInstance() throws IOException {
        if(instance == null) {
            instance = new Manager();
            Files.createDirectories(Paths.get(stormDir));
        }
        return instance;
    }
    /**
     * Set Rp one
     * @param rp
     */
    public void setRpOne(RP rp) {
        rpOne = rp;
    }
    
    /**
     * Return the value of RP one
     * @return
     */
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

    /**
     * Get the current latency amongst all the RP's.
     * @return
     */
    public synchronized long getLatency() {
        return Latency;
    }
    
    public void setQueueManager(QueueManager mqueue){
        this.mqueue = mqueue;
    }
    
    public QueueManager getQueueManager() {
        return mqueue;
    }
    
    /**
     * Set the instance of the Rock DBMS
     * @param dbms
     */
    public void setRocksDBMS(RocksDBMS dbms) {
        this.dbms = dbms;
    }
    /**
     * Get the instance of the Rock DBMS
     * @return
     */
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
    
    /**
     * Set the current latitude of the RP.
     * @param latitude
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * Set the current longitude of the RP
     * @param longitude
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * Get the current latitude of the RP.
     * @return latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /** 
     * Get the current longitude of the RP.
     * @return longitude
     */
    public double getLongitude() {
        return longitude;
    }
    
    public void setPush(AndroidPush push) {
        this.push = push;
    }

    public AndroidPush getPush() {
        return push;
    }

	/**
	 * Check if we are the master or not
	 * @return
	 */
    public boolean isMaster() {
        return master;
    }

    /** 
     * Define who will be the master and save it.
     * @param master
     */
    public void setMaster(boolean master) {
        this.master = master;
    }

    /** 
     * Get the quadtree object.
     * @return
     */
    public PointQuadTree<PeerAddress> getqTree() {
        return qTree;
    }

    /**
     * Set the quadtree object.
     * @param qTree
     */
    public void setqTree(PointQuadTree<PeerAddress> qTree) {
        this.qTree = qTree;
    }
}
