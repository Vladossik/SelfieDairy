package com.vlada.selfie_app;

import android.content.Context;
import android.util.Log;

import com.facebook.android.crypto.keychain.AndroidConceal;
import com.facebook.android.crypto.keychain.SharedPrefsBackedKeyChain;
import com.facebook.crypto.Crypto;
import com.facebook.crypto.CryptoConfig;
import com.facebook.crypto.Entity;
import com.facebook.crypto.exception.CryptoInitializationException;
import com.facebook.crypto.exception.KeyChainException;
import com.facebook.crypto.keychain.KeyChain;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Encryption {
    
    private Encryption() {
    }
    
    
    private static Crypto getCrypto(Context context) {
        // Generate key for this device in sharedPrefs. So we couldn't share emcrypted files between devices.
        KeyChain keyChain = new SharedPrefsBackedKeyChain(context, CryptoConfig.KEY_256);
        return AndroidConceal.get().createDefaultCrypto(keyChain);
    }
    
    /**
     * Encrypts file with key, generated for current device in SharedPrefs.
     * It reads all file in memory before encryption, so input and output files may be the same.
     */
    public static void encryptFile(Context context, File inputFile, File outputFile)
            throws IOException, KeyChainException, CryptoInitializationException {
        
//        PasswordBasedKeyDerivation passwordBasedKeyDerivation = new PasswordBasedKeyDerivation(new SecureRandom(), new SystemNativeCryptoLibrary());
//        passwordBasedKeyDerivation.setPassword(password);
        
        Crypto crypto = getCrypto(context);
        
        // Check for whether the crypto functionality is available
        // This might fail if Android does not load libaries correctly.
        if (!crypto.isAvailable()) {
            Log.e("my_tag", "encryptFile: error: crypto is unavailable");
            return;
        }
        
        byte[] inputBytes = FileUtils.fileToBytes(inputFile);
    
        OutputStream fileOutputStream = new BufferedOutputStream(
                new FileOutputStream(outputFile));
        
        
        // Creates an output stream which encrypts the data as
        // it is written to it and writes it out to the file.
        OutputStream cipherOutputStream = crypto.getCipherOutputStream(
                fileOutputStream,
                Entity.create("entity_id"));
        
        // Write plaintext to it.
        cipherOutputStream.write(inputBytes);
        cipherOutputStream.close();
    }
    
    /**
     * Decrypts file from key, generated for current device in SharedPrefs.
     * It reads all file in memory before decryption, so input and output files may be the same.
     */
    public static void decryptFile(Context context, File inputFile, File outputFile)
            throws IOException, KeyChainException, CryptoInitializationException {
        // Get the file to which ciphertext has been written.
        FileInputStream fileInputStream = new FileInputStream(inputFile);
        
        Crypto crypto = getCrypto(context);
        
        // Creates an input stream which decrypts the data as
        // it is read from it.
        InputStream cipherInputStream = crypto.getCipherInputStream(
                fileInputStream,
                Entity.create("entity_id"));
        
        
        // Read into a byte array.
        int read;
        byte[] buffer = new byte[1024];
    
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        
        
        // You must read the entire stream to completion.
        // The verification is done at the end of the stream.
        // Thus not reading till the end of the stream will cause
        // a security bug. For safety, you should not
        // use any of the data until it's been fully read or throw
        // away the data if an exception occurs.
        while ((read = cipherInputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, read);
        }
        cipherInputStream.close();
        
        FileUtils.writeByteFile(byteArrayOutputStream.toByteArray(), outputFile);
    }
    
    
    
//    public class CustomKeyChain implements KeyChain {
//    
//        @Override
//        public byte[] getCipherKey() throws KeyChainException {
//            return new byte[0];
//        }
//    
//        @Override
//        public byte[] getMacKey() throws KeyChainException {
//            return new byte[0];
//        }
//    
//        @Override
//        public byte[] getNewIV() throws KeyChainException {
//            return new byte[0];
//        }
//    
//        @Override
//        public void destroyKeys() {
//        
//        }
//    }
}
