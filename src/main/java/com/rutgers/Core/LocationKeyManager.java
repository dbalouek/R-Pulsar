/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rutgers.Core;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;

/**
 *
 * @author eduard
 */
public class LocationKeyManager {
    private TreeMap<Number160, PeerAddress> peerIdCache = null;
    
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
    
    public synchronized ArrayList<PeerAddress> nearestKey(Number160[] locationKey) {
        int high = 0;
        int low = locationKey.length - 1;
        
        ArrayList<PeerAddress> result = new ArrayList<PeerAddress>();
        Map.Entry<Number160, PeerAddress> floorEntryFrom = peerIdCache.ceilingEntry(locationKey[0]);
        Map.Entry<Number160, PeerAddress> floorEntryTo = peerIdCache.ceilingEntry(locationKey[locationKey.length-1]);
        
        if(floorEntryFrom.getKey().compareTo(floorEntryTo.getKey()) == 0) {
            result.add(floorEntryFrom.getValue());
        }else {
//            while(floorEntryFrom.getKey().compareTo(floorEntryTo.getKey()) == -1 || floorEntryFrom.getKey().compareTo(floorEntryTo.getKey()) == 1) {
//                int mid = (high + low) / 2;
//                
//                Map.Entry<BigInteger, PeerAddress> mmid = peerIdCache.floorEntry(locationKey[mid]);
//                
//                switch (floorEntryFrom.getKey().compareTo(mmid.getKey())) {
//                    case -1:
//                        break;
//                    case 1:
//                        break;
//                    default:
//                        break;                
//                }
//            }
        }
        
        return result;
    }
    
    public synchronized TreeMap<Number160, PeerAddress> getCache() {
        return peerIdCache;
    }
}
