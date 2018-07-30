package com.vlada.selfie_app.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.vlada.selfie_app.database.dao.DiaryDao;
import com.vlada.selfie_app.database.dao.ImageSourceDao;
import com.vlada.selfie_app.database.entity.Diary;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Calendar;


/**
 * Base class for testing with database
 */
@RunWith(AndroidJUnit4.class)
public abstract class DatabaseTest {
    
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    
    DiaryDao diaryDao;
    ImageSourceDao imageSourceDao;
    MyRoomDatabase db;
    
    @Before
    public void createDb() {
//        Log.d("tag", "IN BEFORE!");
//        System.out.println("IN BEFORE!!!!!");
//        if (true)
//            throw new RuntimeException("");
        
        Context context = InstrumentationRegistry.getTargetContext();
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        db = Room.inMemoryDatabaseBuilder(context, MyRoomDatabase.class)
                // Allowing main thread queries, just for testing.
                .allowMainThreadQueries()
                .build();
        
        diaryDao = db.diaryDao();
        imageSourceDao = db.imageSourceDao();
    }
    
    @After
    public void closeDb() throws IOException {
        db.close();
    }
    
    /**
     * creates calendar from previous and adds amount of hours
     */
    Calendar calPlusHour(Calendar oldCal, int amount) {
        Calendar newCal = (Calendar) oldCal.clone();
        newCal.add(Calendar.HOUR, amount);
        return newCal;
    }
    
    
    <T> T getValueFromLiveData(LiveData<T> liveData) throws InterruptedException {
        return LiveDataTestUtil.getValue(liveData);
    }
    
    
    void insertDiaryAndUpdateId(Diary diary) {
        int newId = (int) diaryDao.insert(diary);
        diary.setId(newId);
    }
    
}
