package com.vlada.selfie_app;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.facebook.crypto.exception.CryptoInitializationException;
import com.facebook.crypto.exception.KeyChainException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class EncryptionTest {
    
    
    private File folder;
    private Context context = InstrumentationRegistry.getTargetContext();
    
    public EncryptionTest() {
        folder = FileUtils.geFolderInExternal();
        folder.mkdirs();
    }
    
    
    @Before
    public void clearTextFiles() {
        File[] files = folder.listFiles();
        
        
        for (File file : files) {
            if (FileUtils.getFileExtension(file).equals("txt")) {
                file.delete();
            }
        }
    }
    
    @Test
    public void encryptTextFile() throws IOException, KeyChainException, CryptoInitializationException {
        File inputFile = new File(folder, "test_text.txt");
        
        final String sourceText = "Hello world!";
        
        FileUtils.writeTextFile(sourceText, inputFile);
        
        assertTrue(inputFile.exists());
        
        File encryptedFile = new File(folder, "encrypted_text.txt");
        
        
        Encryption.encryptFile(context, inputFile, encryptedFile);
        
        assertTrue(encryptedFile.exists());
        
        
        File decryptedFile = new File(folder, "decrypted_text.txt");
        
        Encryption.decryptFile(context, encryptedFile, decryptedFile);
        
        assertTrue(decryptedFile.exists());
        
        
        String encryptedText = FileUtils.readTextFile(decryptedFile);
        
        assertEquals(sourceText, encryptedText);
    }
    
    @Test
    public void encryptionWithReplacement() throws IOException, KeyChainException, CryptoInitializationException {
        
        File replacedFile = new File(folder, "replaced_text.txt");
    
        final String sourceText = "Hello world from Africa!";
    
        FileUtils.writeTextFile(sourceText, replacedFile);
    
        assertTrue(replacedFile.exists());
    
    
    
        Encryption.encryptFile(context, replacedFile, replacedFile);
    
        assertTrue(replacedFile.exists());
        
    
        Encryption.decryptFile(context, replacedFile, replacedFile);
    
        assertTrue(replacedFile.exists());
    
        String encryptedText = FileUtils.readTextFile(replacedFile);
        
        assertEquals(sourceText, encryptedText);
        
        
    }
}