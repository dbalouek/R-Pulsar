/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rutgers.Encryption;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.NoSuchPaddingException;

/**
 *
 * @author eduard
 */
public class UserProfile {

    private final String userId;
    private final KeyPair encryptionKeys;
    private RSAEncryption rsa;
    private SymmetricKeyEncryption ske;

    public UserProfile(String userId, String dir) throws NoSuchAlgorithmException, IOException {
        assert userId != null;
        this.userId = userId;
        rsa = new RSAEncryption();
        this.encryptionKeys = rsa.generateKey(dir);
    }

    public String getUserId() {
        return userId;
    }

    public KeyPair getEncryptionKeys() {
        return encryptionKeys;
    }
    
    public PrivateKey getPrivateKey() {
        return encryptionKeys.getPrivate();
    }
    
    public PublicKey getPublicKey() {
        return encryptionKeys.getPublic();
    }
    
    public byte[] encrypt(String text, PublicKey key) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return rsa.encrypt(text, key);
    }
    
    public byte[] encrypt(byte[] text, PublicKey key) {
        return rsa.encrypt(text, key);
    }
    
    public String decrypt(String text) throws NoSuchAlgorithmException, InvalidKeySpecException {
        System.out.println(text);
        System.out.println(encryptionKeys.getPrivate().toString());
        return rsa.decrypt(text, encryptionKeys.getPrivate());
    }
    
    public String decrypt(byte[] text) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return rsa.decrypt(text, encryptionKeys.getPrivate());
    }
    
    public void createGroup(String password) throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException {
        ske = new SymmetricKeyEncryption(password, 16, "AES");
    }
}