package com.vlada.selfie_app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.vlada.selfie_app.database.entity.ImageSource;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {
    private FileUtils() {
    }
    
    private static final String FOLDER_NAME = "SelfieDiary";
    
    private static class Folders {
        
        
        /**
         * storage/emulated/0/SelfieDiary
         */
        public static File geFolderInExternal() {
            return new File(Environment.getExternalStorageDirectory(), FOLDER_NAME);
        }
        
        /**
         * storage/emulated/0/Android/data/com.vlada.selfie_app/files/Pictures
         */
        public static File getFolderInAndroidData(Context context) {
            return context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        }
        
        /**
         * storage/emulated/0/Android/data/com.vlada.selfie_app/files/Pictures/SelfieDiary
         */
        public static File getFolderInPictures() {
            File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            folder = new File(folder, FOLDER_NAME);
            return folder;
        }
    }
    
    
    public static Intent createCameraIntent(Context context, File fileToSave) {
        File folder = fileToSave.getParentFile();
        if (folder != null)
            folder.mkdirs();
        
        Uri photoURI = FileProvider.getUriForFile(context,
                "com.vlada.selfie_app.fileprovider",
                fileToSave);
        
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        return intent;
    }
    
    
    /**
     * Returns folder where images are placed
     */
    public static File getImageFolder() {
        return Folders.geFolderInExternal();
    }
    
    public static File getEncodedFolder() {
        return new File(getImageFolder(), "encoded");
    }
    
    
    public static File getImageCacheFolder() {
        return new File(getImageFolder(), "cache");
    }
    
    /**
     * Creates a jpg file in folder with identical name to store selfie.
     */
    public static File createImageInFolder(File folder) {
        return new File(folder, "selfie_" +
                String.valueOf(System.currentTimeMillis()) + ".jpg");
    }
    
    
    public static void scanGalleryForImage(Context context, File imageFile) {
        // directly insert in android image database
//        try {
//            MediaStore.Images.Media.insertImage(getContentResolver(),
//                    imageFile.getAbsolutePath(), imageFile.getName(), null);
//        } catch (FileNotFoundException e) {
//            Log.d("my_tag", "scanGalleryForImage: error: " + e.getMessage());
//            e.printStackTrace();
//        }
        
        // sending broadcast to scan new images.
        context.sendBroadcast(new Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(imageFile)));
    }
    
    
    public static boolean deleteImageIfExists(File image) {
        if (image != null && image.exists() && image.isFile()) {
            boolean result = image.delete();
            if (result) {
                Log.d("my_tag", "deleted image: " + image.getAbsolutePath());
                return true;
            }
        }
        return false;
    }
    
    
    public static byte[] fileToBytes(File file) throws IOException {
        byte[] bytes = new byte[(int) file.length()];
        InputStream fileInputStream = new BufferedInputStream(new FileInputStream(file));
        fileInputStream.read(bytes, 0, bytes.length);
        fileInputStream.close();
        return bytes;
    }
    
    
    public static void writeTextFile(String text, File file) throws IOException {
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(text);
        fileWriter.close();
    }
    
    public static void writeByteFile(byte[] bytes, File file) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(bytes);
        fileOutputStream.close();
    }
    
    
    public static String readTextFile(File file) throws IOException {
        return new String(readByteFile(file), "UTF-8");
    }
    
    
    public static byte[] readByteFile(File f) throws IOException {
        int size = (int) f.length();
        byte bytes[] = new byte[size];
        byte tmpBuff[] = new byte[size];
        FileInputStream fis = new FileInputStream(f);
        ;
        try {
            
            int read = fis.read(bytes, 0, size);
            if (read < size) {
                int remain = size - read;
                while (remain > 0) {
                    read = fis.read(tmpBuff, 0, remain);
                    System.arraycopy(tmpBuff, 0, bytes, size - remain, read);
                    remain -= read;
                }
            }
        } catch (IOException e) {
            throw e;
        } finally {
            fis.close();
        }
        
        return bytes;
    }
    
    
    public static String getFileExtension(File file) {
        String name = file.getName();
        try {
            return name.substring(name.lastIndexOf(".") + 1);
        } catch (Exception e) {
            return "";
        }
    }
}
