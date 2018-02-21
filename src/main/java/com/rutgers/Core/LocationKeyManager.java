/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rutgers.Core;

import com.google.common.collect.Iterators;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;

/**
 *
 * @author eduard
 */
public class LocationKeyManager {
    TreeMap<Number160, PeerAddress> peerIdCache = null;   
    static final int MAX = 10001;
    
    public LocationKeyManager() {
        this.peerIdCache = new TreeMap<Number160, PeerAddress>();
    }
    
    public synchronized void insertKey(Number160 locationKey, PeerAddress peer) {
        if(!peerIdCache.containsKey(locationKey)) {
            peerIdCache.put(locationKey, peer);
        }
    }
    
    public synchronized void removeKey(Number160 locationKey) {
        if(peerIdCache.containsKey(locationKey)) {
            peerIdCache.remove(locationKey);
        }
    }
    
    public synchronized PeerAddress nearestKey(Number160 locationKey) {
        Map.Entry<Number160, PeerAddress> floorEntry = peerIdCache.ceilingEntry(locationKey);

        if(floorEntry.getValue() != null) {
            return floorEntry.getValue();
        } else {
            Map.Entry<Number160, PeerAddress> entry = peerIdCache.firstEntry();
            return entry.getValue();
        }
    }
    
    public synchronized PeerAddress exactKey(Number160 locationKey) {
        PeerAddress entry = peerIdCache.get(locationKey);
        return entry;
    }
     
    
    public synchronized ArrayList<PeerAddress> nearestKey(Number160... locationKey) {        
        Wildcard w = new Wildcard();
        int blk_sz = (int) Math.sqrt(locationKey.length);
        int bit[][] = new int[blk_sz+1][MAX];
        int global = 0;
        
        ArrayList<PeerAddress> result = new ArrayList<PeerAddress>();
        Map.Entry<Number160, PeerAddress> floorEntryFrom = peerIdCache.ceilingEntry(locationKey[0]);
        Map.Entry<Number160, PeerAddress> floorEntryTo = peerIdCache.ceilingEntry(locationKey[locationKey.length-1]);
        
        if(floorEntryFrom.getKey().compareTo(floorEntryTo.getKey()) == 0) {
            result.add(floorEntryFrom.getValue());
        }else {
            Set set = peerIdCache.entrySet();
            Iterator iterator = set.iterator();
            
            w.preprocess(locationKey, blk_sz, locationKey.length, bit);
            Map.Entry mentry = (Map.Entry)iterator.next();
            int index = w.query(0, locationKey.length-1, (Number160) mentry.getKey(), locationKey, blk_sz, bit);       
            global += index;
            
            while(iterator.hasNext()) {
                int bit2[][] = new int[blk_sz+1][MAX];
                w.preprocess(locationKey, blk_sz, locationKey.length, bit2);
                mentry = (Map.Entry)iterator.next();
                index = w.query(global, locationKey.length-1, (Number160) mentry.getKey(), locationKey, blk_sz, bit2);

                if(index != 0)
                    result.add((PeerAddress) mentry.getValue());

                global += index;

                if(index == Iterators.size(iterator))
                    break;
            }
        }
        
        return result;
    }
    
    public synchronized TreeMap<Number160, PeerAddress> getCache() {
        return peerIdCache;
    }
}
