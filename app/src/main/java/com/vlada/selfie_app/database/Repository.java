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

/** Main class for interacting with data*/
public class Repository {
    
    private DiaryDao diaryDao;
    private ImageSourceDao imageSourceDao;
    private DiaryImageJoinDao diaryImageJoinDao;
    
    private LiveData<List<Diary>> allDiaries;
    private LiveData<List<ImageSource>> allImages;
    
    public Repository(Application application) {
        MyRoomDatabase db = MyRoomDatabase.getDatabase(application);
        
        diaryDao = db.diaryDao();
        imageSourceDao = db.imageSourceDao();
        diaryImageJoinDao = db.diaryImageJoinDao();
        
        allDiaries = diaryDao.getAllDiaries();
        allImages = imageSourceDao.getAllImages();
        
    }
    
    public DiaryDao getDiaryDao() {
        return diaryDao;
    }
    
    public ImageSourceDao getImageSourceDao() {
        return imageSourceDao;
    }
    
    public DiaryImageJoinDao getDiaryImageJoinDao() {
        return diaryImageJoinDao;
    }
    
    public LiveData<List<Diary>> getAllDiaries() {
        return allDiaries;
    }
    
    public LiveData<List<ImageSource>> getAllImages() {
        return allImages;
    }
    
    // TODO: 24.07.2018 all db inserts in asyncTask
    
    // maybe do it with simple threads
    
    
//    private static class insertAsyncTask extends AsyncTask<Word, Void, Void> {
//        
//        private WordDao mAsyncTaskDao;
//        
//        insertAsyncTask(WordDao dao) {
//            mAsyncTaskDao = dao;
//        }
//        
//        @Override
//        protected Void doInBackground(final Word... params) {
//            mAsyncTaskDao.insert(params[0]);
//            return null;
//        }
//    }
    
}
