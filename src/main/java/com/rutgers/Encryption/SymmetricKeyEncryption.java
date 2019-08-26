package com.rutgers.Encryption;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * This class is used to perform symmetric key encription.
 * @param keys
 * @return
 */
public class SymmetricKeyEncryption {
    
    private SecretKeySpec secretKey;
    private Cipher cipher;
    private Base64.Encoder encoder;
    private Base64.Decoder decoder;

    public SymmetricKeyEncryption(String secret, int length, String algorithm) throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException {
        byte[] key = new byte[length];
        key = fixSecret(secret, length);
        this.secretKey = new SecretKeySpec(key, algorithm);
        this.cipher = Cipher.getInstance(algorithm);
        encoder = Base64.getEncoder();
        decoder = Base64.getDecoder();
    }

    private byte[] fixSecret(String s, int length) throws UnsupportedEncodingException {
        if (s.length() < length) {
                int missingLength = length - s.length();
                for (int i = 0; i < missingLength; i++) {
                        s += " ";
                }
        }
        return s.substring(0, length).getBytes("UTF-8");
    }

    /**
     * Method used to encrypt a file.
     * @param f
     * @throws InvalidKeyException
     * @throws IOException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public void encryptFile(File f) throws InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException {
        System.out.println("Encrypting file: " + f.getName());
        this.cipher.init(Cipher.ENCRYPT_MODE, this.secretKey);
        this.writeToFile(f);
    }
    
    /**
     * Method used to encript a string.
     * @param s
     * @return
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public String encryptString(String s) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        System.out.println("Encrypting string: " + s);
        this.cipher.init(Cipher.ENCRYPT_MODE, this.secretKey);
        byte[] encrypted = cipher.doFinal(s.getBytes());
        return encoder.encodeToString(encrypted);
    }

    /**
     * Method used to decrypt a file.
     * @param f
     * @throws InvalidKeyException
     * @throws IOException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public void decryptFile(File f) throws InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException {
        System.out.println("Decrypting file: " + f.getName());
        this.cipher.init(Cipher.DECRYPT_MODE, this.secretKey);
        this.writeToFile(f);
    }
    
    public String decryptString(String s) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        this.cipher.init(Cipher.DECRYPT_MODE, this.secretKey);
        return new String(cipher.doFinal(decoder.decode(s)));
    }

    public void writeToFile(File f) throws IOException, IllegalBlockSizeException, BadPaddingException {
        FileInputStream in = new FileInputStream(f);
        byte[] input = new byte[(int) f.length()];
        in.read(input);

        FileOutputStream out = new FileOutputStream(f);
        byte[] output = this.cipher.doFinal(input);
        out.write(output);

        out.flush();
        out.close();
        in.close();
    }
    
    public SecretKeySpec getSecretKeySpec() {
        return secretKey;
    }
    
    public Cipher getCipher() {
        return cipher;
    }
}

//ske = new SymmetricKeyExample("!@#$MySecr3tPassw0rd", 16, "AES");