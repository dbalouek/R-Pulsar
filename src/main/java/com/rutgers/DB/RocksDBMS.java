/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rutgers.DB;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.tomp2p.peers.Number160;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.rocksdb.WriteBatch;
import org.rocksdb.WriteOptions;

/**
* This class implements some basic functionality of RocksDBMS.
* This class is used to store all the profiles in the system.
* This class in the paper is decribed as the matching engein.
* 
* @author  Eduard Gibert Renart
* @version 1.0
*/
public class RocksDBMS {
    
    private Options options = null;
    private RocksDB data_dbs = null;
    private RocksDB interest_dbs = null;
    private WriteBatch batch = null;
    private WriteOptions writeOpt = null;
    
    public RocksDBMS() {
        try {
            RocksDB.loadLibrary();            
            options = new Options().setCreateIfMissing(true);
            data_dbs = RocksDB.open(options, "notify_data");
            interest_dbs = RocksDB.open(options, "notify_interest");
            batch = new WriteBatch();
            writeOpt = new WriteOptions();
        } catch (RocksDBException ex) {
            Logger.getLogger(RocksDBMS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Store a single element into the database.
     * @param id
     * @param keys
     */
    public void putData(String[] id, Number160[] keys) {
        synchronized (data_dbs) {
            batch.clear();
            try {
                for(int i = 0; i < id.length; i++) {
                    batch.put(keys[i].toByteArray(), id[i].getBytes());
                }
                data_dbs.write(writeOpt, batch);
            } catch (RocksDBException ex) {
                Logger.getLogger(RocksDBMS.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * Store multiple elements into the database.
     * @param id
     * @param keys
     */
    public void putData(byte[] id, Number160... keys) {
        synchronized (data_dbs) {
            batch.clear();
            try {
                for(Number160 key : keys) {
                    batch.put(key.toByteArray(), id);
                }
                data_dbs.write(writeOpt, batch);
            } catch (RocksDBException ex) {
                Logger.getLogger(RocksDBMS.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * Store multiple interest profiles into the DB.
     * @param id
     * @param keys
     */
    public void putInterest(byte[] id, Number160... keys) {
        synchronized (interest_dbs) {
            batch.clear();
            try {
                for(Number160 key : keys) {
                    batch.put(key.toByteArray(), id);
                }
                interest_dbs.write(writeOpt, batch);
            } catch (RocksDBException ex) {
                Logger.getLogger(RocksDBMS.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    

    public ArrayList<byte[]> getData(Number160... keys) {
        ArrayList<byte[]>  result = new ArrayList<byte[]>();
        synchronized (data_dbs) {
            try {
                for(Number160 key : keys) {                    
                    byte[] array = data_dbs.get(key.toByteArray());
                    if(array != null) {
                        result.add(array);
                    }
                }
            } catch (RocksDBException ex) {
                Logger.getLogger(RocksDBMS.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return result;  
    }

    /**
     * Check if a set of interest profiles are present in the DB.
     * @param keys
     * @return
     */
    public ArrayList<byte[]> getInterest(Number160... keys) {
        ArrayList<byte[]>  result = new ArrayList<byte[]>();
        synchronized (interest_dbs) {
            try {
                for(Number160 key : keys) {                    
                    byte[] array = interest_dbs.get(key.toByteArray());
                    if(array != null) {
                        result.add(array);
                    }
                }
            } catch (RocksDBException ex) {
                Logger.getLogger(RocksDBMS.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return result;  
    }
    
    /**
     * Delete multiple interest profiles from the DB.
     * @param keys
     */
    public void deleteInterest(Number160... keys) {
        
        for(Number160 key : keys) {                    
            try {
                interest_dbs.delete(key.toByteArray());
            } catch (RocksDBException ex) {
                Logger.getLogger(RocksDBMS.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * Close the DB.
     */
    public void close() {
        interest_dbs.close();
        data_dbs.close();
    }
}
