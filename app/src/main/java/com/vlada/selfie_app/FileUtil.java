package com.vlada.selfie_app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;

public class FileUtil {
    private FileUtil() {
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
    
    private static final String FOLDER_NAME = "SelfieDiary";
    
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
        folder = new File(folder, "SelfieDiary");
        return folder;
    }
    
    public static File createImageFromFolder(File folder) {
        return new File(folder, "selfie_" +
                String.valueOf(System.currentTimeMillis()) + ".jpg");
    }
    
    
    
    
    
    public static void deleteImageIfExists(File image) {
        if (image != null && image.exists() && image.isFile()) {
            boolean result = image.delete();
            if (result) {
                Log.d("my_tag", "deleted image: " + image.getAbsolutePath());
            }
        }
    }
}
