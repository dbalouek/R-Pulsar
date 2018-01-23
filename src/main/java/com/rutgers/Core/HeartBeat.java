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
 *
 * @author eduard
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
    
    Boolean getRunning() {
        return Running;
    }
    
    void setRunning(Boolean running) {
        Running = running;
    }
    
    long getAverage() {
        return average;
    }

    @Override
    public void run() {
        while(Running) {
            try {
                for (PeerAddress peerAddr : peer.peerBean().peerMap().all()) {
                    
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
                manager.setLatency(average);
                Thread.sleep(1500);
            } catch (InterruptedException ex) {
                Logger.getLogger(HeartBeat.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
