package com.vlada.selfie_app.database;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.Handler;

import com.vlada.selfie_app.database.dao.DiaryDao;
import com.vlada.selfie_app.database.dao.ImageSourceDao;
import com.vlada.selfie_app.database.entity.Diary;
import com.vlada.selfie_app.database.entity.ImageSource;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

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
    
    /** In separate deletes one image by source and diary id.*/
    public void deleteImageByKeys(final String imageSrc, final int diaryId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                imageSourceDao.deleteByKeys(imageSrc, diaryId);
            }
        }).start();
    }
    
    /** In separate deletes images.*/
    public void deleteImage(final ImageSource... images) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                imageSourceDao.delete(images);
            }
        }).start();
    }
    
    /** In separate thread updates image.*/
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
    
    /** In other thread tries to find image and returns result in callback in ui thread.*/
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
}
