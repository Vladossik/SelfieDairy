package com.vlada.selfie_app;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.vlada.selfie_app.database.entity.ImageSource;
import com.vlada.selfie_app.utils.FileUtils;

import java.io.File;

public class ImageLoading {
    
    private ImageLoading() {
    }
    
    
    /**
     * Checks three folders: default image folder, encrypted image folder, cached image folder.
     * And tries to find a decrypted image or create it in cache folder.
     */
    @Nullable
    public static File getDecodedImage(Context context, ImageSource imageSource) {
        File sourceFile = imageSource.getSourceFile();
        File encodedFile = imageSource.getEncodedFile();
        File cachedFile = imageSource.getCachedFile(context);
        
        if (sourceFile.exists()) {
            Log.d("my_tag", "getDecodedImage: found source file");
            return sourceFile;
        }
        
        if (cachedFile.exists()) {
            Log.d("my_tag", "getDecodedImage: found cached file");
            return cachedFile;
        }
        
        if (!encodedFile.exists()) {
            Log.d("my_tag", "getDecodedImage: didn't find any file, returned null");
            return null;
        }
        
        try {
            Encryption.decryptFile(context, encodedFile, cachedFile);
            return cachedFile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static boolean clearImageCache(Context context) {
        File cacheFolder = FileUtils.getImageCacheFolder(context);
        File[] images = cacheFolder.listFiles();
        int counter = 0;
        for (File image : images) {
            boolean result = image.delete();
            if (result)
                counter++;
        }
        
        Log.d("my_tag", "clearImageCache: deleted " 
                + counter + " images from " + images.length + " in " + cacheFolder.getAbsolutePath());
        
        return counter > 0 && counter == images.length;
    }
    
    
    public static void loadImageInView(File imageFile, ImageView imageView) {
        // loading image in imageView from file
        if (imageFile != null) {
            Picasso.get()
                    .load(imageFile)
                    .placeholder(R.drawable.image_placeholder)
                    .resize(800, 800)
                    .onlyScaleDown()
                    .centerInside()
                    .into(imageView);
        }
    }
}
