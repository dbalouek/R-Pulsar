/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rutgers.Encryption;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * This class is used for performing RSA encryption.
 * @param keys
 * @return
 */
public final class RSAEncryption {
    /**
     * String to hold name of the encryption algorithm.
     */
    public String ALGORITHM = "RSA";

    /**
     * Generate key which contains a pair of private and public key using 1024
     * bytes.
     * 
       * @return 
       * @throws java.security.NoSuchAlgorithmException 
     */
    public KeyPair generateKey(String dir) throws NoSuchAlgorithmException, IOException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
        keyGen.initialize(1024);
        KeyPair key = keyGen.generateKeyPair();
        
        File publicKeyFile = new File(dir);
        
        if (publicKeyFile.getParentFile() != null) {
            publicKeyFile.getParentFile().mkdirs();
        }
        publicKeyFile.createNewFile();
        
        ObjectOutputStream publicKeyOS = new ObjectOutputStream(new FileOutputStream(publicKeyFile));
        publicKeyOS.writeObject(key.getPublic());
        publicKeyOS.close();
        
        return key;
    }

    /**
     * The method checks if the pair of public and private key has been generated.
     * 
       * @param dprivate
       * @param dpublic
     * @return flag indicating if the pair of keys were generated.
     */
    public boolean areKeysPresent(String dprivate, String dpublic) {
        File privateKey = new File(dprivate);
        File publicKey = new File(dpublic);

        return privateKey.exists() && publicKey.exists();
    }

    /**
     * Encrypt the plain text using public key.
     * 
     * @param text
     *          : original plain text
     * @param key
     *          :The public key
     * @return Encrypted text
     */
    public byte[] encrypt(String text, PublicKey key) {
        byte[] cipherText = null;
        try {
            // get an RSA cipher object and print the provider
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            // encrypt the plain text using the public key
            cipher.init(Cipher.ENCRYPT_MODE, key);
            cipherText = cipher.doFinal(text.getBytes());
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
        }
        return cipherText;
    }
    
    public byte[] encrypt(byte[] text, PublicKey key) {
        byte[] cipherText = null;
        try {
            // get an RSA cipher object and print the provider
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            // encrypt the plain text using the public key
            cipher.init(Cipher.ENCRYPT_MODE, key);
            cipherText = cipher.doFinal(text);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
        }
        return cipherText;
    }

    /**
     * Decrypt text using private key.
     * 
     * @param text
     *          :encrypted text
     * @param key
     *          :The private key
     * @return plain text
     */
    public String decrypt(String text, PrivateKey key) {
        byte[] dectyptedText = null;
        try {
            // get an RSA cipher object and print the provider
            Cipher cipher = Cipher.getInstance(ALGORITHM);

            // decrypt the text using the private key
            cipher.init(Cipher.DECRYPT_MODE, key);
            dectyptedText = cipher.doFinal(text.getBytes());
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
        }
        return new String(dectyptedText);
    }
    
    public String decrypt(byte[] text, PrivateKey key) {
        byte[] dectyptedText = null;
        try {
            // get an RSA cipher object and print the provider
            Cipher cipher = Cipher.getInstance(ALGORITHM);

            // decrypt the text using the private key
            cipher.init(Cipher.DECRYPT_MODE, key);
            dectyptedText = cipher.doFinal(text);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
        }
        return new String(dectyptedText);
    }
    
    public PublicKey getPublicKey(String encodedKey) throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        KeyFactory factory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec encodedKeySpec = new X509EncodedKeySpec(Base64.decode(encodedKey));
        return factory.generatePublic(encodedKeySpec);
    }
}
