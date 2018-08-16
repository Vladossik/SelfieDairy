package com.vlada.selfie_app.database;

import android.app.Application;
import android.app.ProgressDialog;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import com.vlada.selfie_app.Encryption;
import com.vlada.selfie_app.utils.FileUtils;
import com.vlada.selfie_app.database.dao.DiaryDao;
import com.vlada.selfie_app.database.dao.ImageSourceDao;
import com.vlada.selfie_app.database.entity.Diary;
import com.vlada.selfie_app.database.entity.ImageSource;

import java.util.List;

/**
 * Main class for interacting with data
 */
public class Repository {
    
    private DiaryDao diaryDao;
    private ImageSourceDao imageSourceDao;
    
    private MyRoomDatabase db;
    
    public Repository(Application application) {
        this(MyRoomDatabase.getDatabase(application));
    }
    
    public Repository(MyRoomDatabase db) {
        this.db = db;
        
        diaryDao = db.diaryDao();
        imageSourceDao = db.imageSourceDao();
    }
    
    public LiveData<List<Diary>> getAllDiaries() {
        return diaryDao.getAllDiaries();
    }
    
    public LiveData<List<Diary>> getAllDoneDiaries() {
        return diaryDao.getAllDoneDiaries();
    }
    
    public LiveData<List<Diary>> getAllWaitingDiaries() {
        return diaryDao.getAllWaitingDiaries();
    }
    
    
    public LiveData<List<ImageSource>> getAllImagesForDiary(int diaryId) {
        return imageSourceDao.getImagesForDiaryLive(diaryId);
    }
    
    public MyRoomDatabase getDatabase() {
        return db;
    }
    
    
    /**
     * In separate thread inserts diary. Warning: after insertion id still will be unset.
     * If diary with such id already exists - throws an exception.
     */
    public void insertDiary(final Diary diary) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int newId = (int) diaryDao.insert(diary);
//                diary.setId(newId);
            }
        }).start();
    }
    
    /**
     * In separate thread updates diary.
     * If diary with such id does not exists - nothing happens.
     */
    public void updateDiary(final Diary diary) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                diaryDao.update(diary);
            }
        }).start();
    }
    
    /**
     * In separate thread deletes diary from db and unbinds all photos from it.
     */
    public void deleteDiary(final Diary diary) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // deletion of all images will happen automatically
                
                // delete diary
                diaryDao.deleteById(diary.getId());
                
            }
        }).start();
    }
    
    
    /**
     * In separate thread inserts images in db
     */
    public void insertImage(final ImageSource... images) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // if already exists - nothing changes
                imageSourceDao.insert(images);
                
            }
        }).start();
    }
    
    /**
     * In separate deletes one image by source and diary id.
     */
    public void deleteImageByKeys(final String imageSrc, final int diaryId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                imageSourceDao.deleteByKeys(imageSrc, diaryId);
            }
        }).start();
    }
    
    /**
     * In separate deletes images.
     */
    public void deleteImage(final ImageSource... images) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                imageSourceDao.delete(images);
            }
        }).start();
    }
    
    /**
     * In separate thread updates image.
     */
    public void updateImage(final ImageSource imageSource) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                imageSourceDao.update(imageSource);
            }
        }).start();
    }
    
    public interface BooleanCallback {
        void onResult(boolean result);
    }
    
    /**
     * In other thread tries to find image and returns result in callback in ui thread.
     */
    public void checkIfImageExists(final String source, final int diaryId, final BooleanCallback callback) {
        final Handler handler = new Handler();
        
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<ImageSource> found = imageSourceDao.findByKeys(source, diaryId);
                // return in ui thread.
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResult(found.size() > 0);
                    }
                });
            }
        }).start();
    }
    
    /**
     * Changes source field for imageSource in database by deleting old entity
     * and inserting new one with updated source.
     * WARNING: works in the same thread as called. Can not be called from ui thread!
     * returns true if image with new source does not exists and insertion was successful.
     */
    public boolean replaceImageSourceId(final ImageSource imageSource, final String newSource) {
        
        // trying to find existing image with newSource
        final List<ImageSource> found = imageSourceDao.findByKeys(newSource, imageSource.getDiaryId());
        if (found.size() > 0) {
            return false;
        }
        
        imageSourceDao.delete(imageSource);
        imageSource.setSource(newSource);
        
        imageSourceDao.insert(imageSource);
        return true;
    }
    
    
    public void encryptWholeDiary(final Context context, final Diary diary) {
        // setup progressDialog
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        if (diary.isPrivate()) {
            progressDialog.setMessage("Diary encryption");
        } else {
            progressDialog.setMessage("Diary decryption");
        }
        progressDialog.show();
        
        // Start background task in new thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<ImageSource> images = imageSourceDao.getImagesForDiary(diary.getId());
                
                progressDialog.setMax(images.size());
                int i = 0;
                for (ImageSource imageSource : images) {
                    progressDialog.setProgress(i);
                    i++;
        
                    if (imageSource.isEncrypted() == diary.isPrivate())
                        continue;
        
                    if (imageSource.isEncrypted()) {
                        decryptImage(imageSource);
                    } else {
                        encryptImage(imageSource);
                    }
                }
                
                progressDialog.dismiss();
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
                    boolean result = Repository.this.replaceImageSourceId(imageSource, newSource);
            
                    // we can not update image for some reason, so just do nothing
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
        }).start();
    }
}
