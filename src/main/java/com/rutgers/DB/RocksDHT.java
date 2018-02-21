/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rutgers.DB;

import static com.rutgers.Core.Globals.DEFAULT_MAX_VERSIONS;
import static com.rutgers.Core.Globals.DEFAULT_STORAGE_CHECK_INTERVAL;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.tomp2p.dht.Storage;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.Number320;
import net.tomp2p.peers.Number480;
import net.tomp2p.peers.Number640;
import net.tomp2p.storage.Data;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.rocksdb.WriteBatch;
import org.rocksdb.WriteOptions;

/**
 *
 * @author eduard
 */
public class RocksDHT implements Storage{
    
    private Options options = null;
    private RocksDB dataMap = null;
    private WriteBatch writeBatch = null;
    private WriteOptions writeOpts = null;
    private Data d = null;
    
    // Maintenance
    final private Map<Number640, Long> timeoutMap = new ConcurrentHashMap<Number640, Long>();
    final private ConcurrentSkipListMap<Long, Set<Number640>> timeoutMapRev = new ConcurrentSkipListMap<Long, Set<Number640>>();

    // Protection
    final private Map<Number320, PublicKey> protectedMap = new ConcurrentHashMap<Number320, PublicKey>();
    final private Map<Number480, PublicKey> entryMap = new ConcurrentHashMap<Number480, PublicKey>();

    // Responsibility
    final private Map<Number160, Number160> responsibilityMap = new ConcurrentHashMap<Number160, Number160>();
    final private Map<Number160, Set<Number160>> responsibilityMapRev = new ConcurrentHashMap<Number160, Set<Number160>>();

    final int storageCheckIntervalMillis = DEFAULT_STORAGE_CHECK_INTERVAL;
    final int maxVersions = DEFAULT_MAX_VERSIONS;

    public RocksDHT(Number160 peerId) {
        try {
            RocksDB.loadLibrary();
            options = new Options().setCreateIfMissing(true);
            dataMap = RocksDB.open(options, "dataMap_" + peerId.toString());
            writeBatch = new WriteBatch();
            writeOpts = new WriteOptions();
            byte[] result = "".getBytes();
            d = new Data(result);
        } catch (RocksDBException ex) {
            Logger.getLogger(RocksDHT.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Data put(Number640 key, Data value) {
        byte[] k = new byte[key.byteValue()];
        byte[] v = value.toBytes();
        writeBatch.put(k, v);
        return d;
    }

    @Override
    public void writeBatch() {
        try {
            dataMap.write(writeOpts, writeBatch);
            //System.out.println("DHT Insert stop: " + System.currentTimeMillis());
        } catch (RocksDBException ex) {
            Logger.getLogger(RocksDHT.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Data get(Number640 key) {
        byte[] value = null;
        byte k[] = new byte[key.byteValue()];
        try {
            value = dataMap.get(k);
        } catch (RocksDBException ex) {
            Logger.getLogger(RocksDHT.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new Data(value);
    }

    @Override
    public boolean contains(Number640 key) {
        byte[] value = null;
        try {
            byte k[] = new byte[key.byteValue()];
            value = dataMap.get(k);
        } catch (RocksDBException ex) {
            Logger.getLogger(RocksDHT.class.getName()).log(Level.SEVERE, null, ex);
        }
        return (value != null);
    }

    @Override
    public int contains(Number640 nmbr, Number640 nmbr1) {
        Map<byte[], byte[]> multiGet = null;
        
        try {
            List<byte[]> list = new ArrayList<byte[]>();
       
            int i = nmbr.contentKey().intValue();
            while(i < nmbr1.contentKey().intValue()) {
                i++;
                Number640 tmp = new Number640(nmbr.locationKey(),nmbr.domainKey(),new Number160(i), nmbr.versionKey());
                list.add(new byte[tmp.byteValue()]);
            }
            
            multiGet = dataMap.multiGet(list);
        } catch (RocksDBException ex) {
            Logger.getLogger(RocksDHT.class.getName()).log(Level.SEVERE, null, ex);
        }
        return multiGet.size();
    }

    @Override
    public Data remove(Number640 key, boolean returnData) {
        byte[] value = null;
        try {
            byte k[] = new byte[key.byteValue()];
            value = dataMap.get(k);
            dataMap.singleDelete(k);
        } catch (RocksDBException ex) {
            Logger.getLogger(RocksDHT.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new Data(value);
    }

    @Override
    public NavigableMap<Number640, Data> remove(Number640 nmbr, Number640 nmbr1) {
        try {
            byte k[] = new byte[nmbr.byteValue()];
            byte k1[] = new byte[nmbr1.byteValue()];
            dataMap.deleteRange(k, k1);
        } catch (RocksDBException ex) {
            Logger.getLogger(RocksDHT.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public NavigableMap<Number640, Data> subMap(Number640 nmbr, Number640 nmbr1, int i, boolean bln) {
//        System.out.println("From: " + nmbr + " To: " + nmbr1);
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public NavigableMap<Number640, Data> map() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void close() {
        dataMap.close();
        protectedMap.clear();
        timeoutMap.clear();
        timeoutMapRev.clear();
    }

    @Override
    public void addTimeout(Number640 key, long expiration) {
        Long oldExpiration = timeoutMap.put(key, expiration);
        Set<Number640> tmp = putIfAbsent2(expiration,
                Collections.newSetFromMap(new ConcurrentHashMap<Number640, Boolean>()));
        tmp.add(key);
        if (oldExpiration == null) {
            return;
        }
        removeRevTimeout(key, oldExpiration);
    }
    
    private Set<Number640> putIfAbsent2(long expiration, Set<Number640> hashSet) {
        Set<Number640> timeouts = timeoutMapRev.putIfAbsent(expiration, hashSet);
        return timeouts == null ? hashSet : timeouts;
    }

    @Override
    public void removeTimeout(Number640 key) {
       Long expiration = timeoutMap.remove(key);
        if (expiration == null) {
            return;
        }
        removeRevTimeout(key, expiration);
    }
    
    private void removeRevTimeout(Number640 key, Long expiration) {
        Set<Number640> tmp = timeoutMapRev.get(expiration);
        if (tmp != null) {
            tmp.remove(key);
            if (tmp.isEmpty()) {
                timeoutMapRev.remove(expiration);
            }
        }
    }

    @Override
    public Collection<Number640> subMapTimeout(long to) {
        SortedMap<Long, Set<Number640>> tmp = timeoutMapRev.subMap(0L, to);
        Collection<Number640> toRemove = new ArrayList<Number640>();
        for (Set<Number640> set : tmp.values()) {
            toRemove.addAll(set);
        }
        return toRemove;
    }

    @Override
    public int storageCheckIntervalMillis() {
        return storageCheckIntervalMillis;
    }

    @Override
    public boolean protectDomain(Number320 key, PublicKey publicKey) {
        protectedMap.put(key, publicKey);
        return true;
    }

    @Override
    public boolean isDomainProtectedByOthers(Number320 key, PublicKey publicKey) {
        PublicKey other = protectedMap.get(key);
        if (other == null) {
            return false;
        }
        final boolean retVal = !other.equals(publicKey);
        return retVal;
    }

    @Override
    public boolean protectEntry(Number480 key, PublicKey publicKey) {
        entryMap.put(key, publicKey);
        return true;
    }

    @Override
    public boolean isEntryProtectedByOthers(Number480 key, PublicKey publicKey) {
        PublicKey other = entryMap.get(key);
        if (other == null) {
            return false;
        }
        return !other.equals(publicKey);
    }

    @Override
    public Number160 findPeerIDsForResponsibleContent(Number160 locationKey) {
        return responsibilityMap.get(locationKey);
    }

    @Override
    public Collection<Number160> findContentForResponsiblePeerID(Number160 peerID) {
        return responsibilityMapRev.get(peerID);
    }

    @Override
    public boolean updateResponsibilities(Number160 locationKey, Number160 peerId) {
        final Number160 oldPeerID =  responsibilityMap.put(locationKey, peerId);
        final boolean hasChanged;
        if(oldPeerID != null) {
                if(oldPeerID.equals(peerId)) {
                        hasChanged = false;
                } else {
                        removeRevResponsibility(oldPeerID, locationKey);
                        hasChanged = true;
                }
        } else {
                hasChanged = true;
        }
        Set<Number160> contentIDs = responsibilityMapRev.get(peerId);
        if(contentIDs == null) {
                contentIDs = Collections.newSetFromMap(new ConcurrentHashMap<Number160, Boolean>()); 
                responsibilityMapRev.put(peerId, contentIDs);
        }
        contentIDs.add(locationKey);
        return hasChanged;
    }

    @Override
    public void removeResponsibility(Number160 locationKey) {
        Number160 peerId = responsibilityMap.remove(locationKey);
        if(peerId != null) {
            removeRevResponsibility(peerId, locationKey);
        }
    }
    
    private void removeRevResponsibility(Number160 peerId, Number160 locationKey) {
        Set<Number160> contentIDs = responsibilityMapRev.get(peerId);
        if (contentIDs != null) {
            contentIDs.remove(locationKey);
            if (contentIDs.isEmpty()) {
                responsibilityMapRev.remove(peerId);
            }
        }
    }
}