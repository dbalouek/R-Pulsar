/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rutgers.Encryption;

import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.tomp2p.dht.FutureGet;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.p2p.Peer;
import net.tomp2p.peers.Number160;

/**
 * This class is responsible for storing all the public keys.
 * It is used to encrypt communication between peers.
 * @param keys
 * @return
 */
public class PublicKeyManager {
    
    private String userId = null;
    private KeyPair usersKeyPair = null;
    private Map<String, PublicKey> publicKeyCache = null;
    private PeerDHT peerDHT = null;
    private Peer peer = null;
    private RSAEncryption rsa = null;
    
    public PublicKeyManager(String userId, KeyPair usersKeyPair, PeerDHT dht, Peer peer) {
        this.userId = userId;
        this.usersKeyPair = usersKeyPair;
        this.publicKeyCache = new ConcurrentHashMap<String, PublicKey>();
        this.peerDHT = dht;
        this.rsa = new RSAEncryption();
        this.peer = peer;
    }
    
    /**
     * Return all the public kyes stored in the system.
     * @return
     */
    public Map<String, PublicKey> getCachedPublicKeys() {
        return Collections.unmodifiableMap(publicKeyCache);
    }

    /**
     * Store a new public key into the system.
     * @param userId
     * @param publicKey
     */
    public void putPublicKey(String userId, PublicKey publicKey) {
        publicKeyCache.put(userId, publicKey);
    }

    /**
     * Check if the given public key already existing in the system.
     * @param userId String of the public key to check.
     * @return
     */
    public boolean containsPublicKey(String userId) {
        return publicKeyCache.containsKey(userId);
    }

    /**
     * Get a specific public key given a user id.
     * @param userId
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public PublicKey getPublicKey(String userId) throws NoSuchAlgorithmException, InvalidKeySpecException, ClassNotFoundException, IOException {
        if (this.userId.equals(userId)) {
            // get the own public key
            return usersKeyPair.getPublic();
        }
        if (publicKeyCache.containsKey(userId)) {
            // check the cache
            return publicKeyCache.get(userId);
        }
        
        Number160 id = new Number160(userId);
        PublicKey key = null;
        
        if(peerDHT != null) {
            FutureGet futureGet = peerDHT.get(id).start();
            futureGet.awaitUninterruptibly();
            String k = futureGet.data().object().toString();
            System.out.println(k);
            key = rsa.getPublicKey(k);
            publicKeyCache.put(userId, key);
        }else {
            //Fix
            //SEND MSG TO SOMEONE AND ASK FOR KEY
        }

        
        return key;
    }
}
