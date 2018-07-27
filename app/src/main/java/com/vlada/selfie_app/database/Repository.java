package com.vlada.selfie_app.database;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.content.Context;

import com.vlada.selfie_app.database.dao.DiaryDao;
import com.vlada.selfie_app.database.dao.DiaryImageJoinDao;
import com.vlada.selfie_app.database.dao.ImageSourceDao;
import com.vlada.selfie_app.database.entity.Diary;
import com.vlada.selfie_app.database.entity.DiaryImageJoin;
import com.vlada.selfie_app.database.entity.ImageSource;

import java.util.List;

/**
 * Main class for interacting with data
 */
public class Repository {
    
    private DiaryDao diaryDao;
    private ImageSourceDao imageSourceDao;
    private DiaryImageJoinDao diaryImageJoinDao;
    
    private LiveData<List<Diary>> allDiaries;
    private LiveData<List<ImageSource>> allImages;
    
    private MyRoomDatabase db;
    
    public Repository(Application application) {
        this(MyRoomDatabase.getDatabase(application));
    }
    
    public Repository(MyRoomDatabase db) {
        this.db = db;
        
        diaryDao = db.diaryDao();
        imageSourceDao = db.imageSourceDao();
        diaryImageJoinDao = db.diaryImageJoinDao();
        
        allDiaries = diaryDao.getAllDiaries();
        allImages = imageSourceDao.getAllImages();
    }
    
    public LiveData<List<Diary>> getAllDiaries() {
        return allDiaries;
    }
    
    public LiveData<List<ImageSource>> getAllImages() {
        return allImages;
    }
    
    public MyRoomDatabase getDatabase() {
        return db;
    }
    
    
    /**
     * In separate thread inserts diary and updates id in diary object.
     * If diary with such id already exists - throws an exception.
     */
    public void insertDiary(final Diary diary) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                
                int newId = (int) diaryDao.insert(diary);
                diary.setId(newId);
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
     * In separate thread updates diary from db or creates it
     */
    public void deleteDiary(final Diary diary) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                diaryDao.deleteById(diary.getId());
            }
        }).start();
    }
    
    
    /**
     * In separate thread inserts image in db if it does not exist and binds it to the diary by id
     */
    public void insertImageInDiary(final ImageSource imageSource, final int diaryId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                
                // if already exists - nothing changes
                imageSourceDao.insert(imageSource);
                
                diaryImageJoinDao.insert(new DiaryImageJoin(diaryId, imageSource.getSource()));
                
                
            }
        }).start();
    }
    
    /** In separate thread unbinds image from diary and if there are no left diaries for img - deletes img*/
    public void deleteImageFromDiary(final String imageSrc, final int diaryId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                
                diaryImageJoinDao.delete(new DiaryImageJoin(diaryId, imageSrc));
                
                List<Diary> allDiaries = diaryImageJoinDao.getDiariesForImages(imageSrc);
                
                if (allDiaries.isEmpty()) {
                    imageSourceDao.deleteBySrc(imageSrc);
                }
            }
        }).start();
    }
    
    
    
}
