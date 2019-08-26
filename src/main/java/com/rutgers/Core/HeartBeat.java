/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rutgers.Core;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.tomp2p.connection.ChannelCreator;
import net.tomp2p.connection.DefaultConnectionConfiguration;
import net.tomp2p.futures.FutureChannelCreator;
import net.tomp2p.futures.FutureResponse;
import net.tomp2p.p2p.Peer;
import net.tomp2p.peers.PeerAddress;

/**
* This class implements the heart beat message that is send 
* to all of the RP's to make sure that they are all alive.
*
* @author  Eduard Giber Renart
* @version 1.0
*/
public class HeartBeat implements Runnable {
    
    private FutureChannelCreator futurecc = null;
    private Peer peer = null;
    private ChannelCreator channelCreator = null;
    private Boolean Running = false;
    private FutureResponse futureResponse = null;
    private long startTime = 0;
    private long estimatedTime = 0;
    private int counter;
    private long total, average;
    private Manager manager = null;

    public HeartBeat(Boolean first) throws IOException {
        manager = Manager.getInstance();
        
        if(first)
            peer = manager.getRpOne().getPeer();
        else 
            peer = manager.getRpTwo().getPeer();
        
        futurecc = peer.connectionBean().reservation().create(1, 1);
        futurecc.awaitUninterruptibly();
        channelCreator = futurecc.channelCreator();
        Running = true;
        counter = 0;
        total = 0;
        average = 0;
    }
	/**
	 * Check if the heart beat is running or not
	 * @return if the heart beat is running
	 */
    Boolean getRunning() {
        return Running;
    }
    /**
     * Used to init the the heart beat.
     * @param running Set it to true to start or false to stop
     */
    void setRunning(Boolean running) {
        Running = running;
    }
    
    /**
     * Get the latency of all available peers.
     * @return Returns the average latency amongst all the peers
     */
    long getAverage() {
        return average;
    }

    @Override
	   /**
	   * This runs in a separate thread and sends pings to all of the peers in the list
	   * @return Nothing.
	   */
    public void run() {
        while(Running) {
            try {
            	/**
            	 * Iterate through the peer list
            	 */
                for (PeerAddress peerAddr : peer.peerBean().peerMap().all()) {
                	/**
                	 * Send a ping and get the latency between the two nodes.
                	 */
                    startTime = System.currentTimeMillis();
                    futureResponse = peer.pingRPC().pingUDPProbe(peerAddr, channelCreator, new DefaultConnectionConfiguration());
                    futureResponse.awaitUninterruptibly();
                    estimatedTime = System.currentTimeMillis() - startTime;
                    
                    if (futureResponse.isSuccess()) {
                        counter++;
                        total += estimatedTime;
                        average = total / counter;
                    }
                }
                /**
                 * Calculate the average latency amongst all the peers in the list.
                 */
                manager.setLatency(average);
                Thread.sleep(1500);
            } catch (InterruptedException ex) {
                Logger.getLogger(HeartBeat.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
