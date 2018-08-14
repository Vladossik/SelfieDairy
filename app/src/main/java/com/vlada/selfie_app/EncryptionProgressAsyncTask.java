package com.vlada.selfie_app;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.vlada.selfie_app.database.Repository;
import com.vlada.selfie_app.database.dao.ImageSourceDao;
import com.vlada.selfie_app.database.entity.Diary;
import com.vlada.selfie_app.database.entity.ImageSource;

import java.util.List;

public class EncryptionProgressAsyncTask extends AsyncTask<Void, Integer, Void> {
    
    private ProgressDialog progressDialog;
    private Repository repository;
    private Diary diary;
    private ImageSourceDao imageSourceDao;
    private Context context;
    
    public EncryptionProgressAsyncTask(ProgressDialog progressDialog, Repository repository, Diary diary, Context context) {
        this.progressDialog = progressDialog;
        this.repository = repository;
        this.diary = diary;
        imageSourceDao = repository.getDatabase().imageSourceDao();
        this.context = context;
    }
    
    @Override
    protected Void doInBackground(Void... params) {
        List<ImageSource> images = imageSourceDao.getImagesForDiary(diary.getId());
        
        int i = 0;
        for (ImageSource imageSource : images) {
            publishProgress(i, images.size());
            i++;
            
            if (imageSource.isEncrypted() == diary.isPrivate())
                continue;
            
            if (imageSource.isEncrypted()) {
                decryptImage(imageSource);
            } else {
                encryptImage(imageSource);
            }
            
            
        }
        return null;
    }
    
    private void decryptImage(ImageSource imageSource) {
        // decrypting
        imageSource.setEncrypted(false);
        imageSourceDao.update(imageSource);
        
        // source of decrypted image already exists, so we can just delete encoded file
        if (imageSource.getSourceFile().exists()) {
            // delete encoded image
            FileUtils.deleteImageIfExists(imageSource.getEncodedFile());
            return;
        }
        
        // if we can not write decoded image to default path
        if (!imageSource.getSourceFile().canWrite()) {
            // update source as default location for created in this app images.
            String newSource = FileUtils.createImageInFolder(FileUtils.getImageFolder()).getAbsolutePath();
            boolean result = repository.replaceImageSourceId(imageSource, newSource);
            
            // we can not update image for some reason, so just do nothing
            // even don't change image isEncrypted field in db
            if (!result) {
                // ???????? what to do?
                Toast.makeText(context, "Failed to change image source to\n" + newSource, Toast.LENGTH_LONG).show();
                return;
            }
        }
        
        // we have default image source where we can write decrypted file
        
        try {
            Encryption.decryptFile(context, imageSource.getEncodedFile(), imageSource.getSourceFile());
            FileUtils.scanGalleryForImage(context, imageSource.getSourceFile());
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        FileUtils.deleteImageIfExists(imageSource.getEncodedFile());
    }
    
    private void encryptImage(ImageSource imageSource) {
        // encrypting
        imageSource.setEncrypted(true);
        imageSourceDao.update(imageSource);
        
        if (imageSource.getEncodedFile().exists()) {
            return;
        }
        
        if (!imageSource.getSourceFile().exists()) {
            return;
        }
        
        try {
            Encryption.encryptFile(context, imageSource.getSourceFile(), imageSource.getEncodedFile());
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        
        if (imageSource.getSourceFile().getParentFile().getAbsolutePath()
                .equals(FileUtils.getImageFolder().getAbsolutePath())) {
            
            // delete source file only if it was stored in our default app folder.
            FileUtils.deleteImageIfExists(imageSource.getSourceFile());
            FileUtils.scanGalleryForImage(context, imageSource.getSourceFile());
        }
    }
    
    
    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        progressDialog.dismiss();
    }
    
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.show();
    }
    
    @Override
    protected void onProgressUpdate(Integer... values) {
        progressDialog.setMax(values[1]);
        progressDialog.setProgress(values[0]);
    }
}
