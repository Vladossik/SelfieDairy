package com.vlada.selfie_app.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.vlada.selfie_app.database.dao.DiaryDao;
import com.vlada.selfie_app.database.dao.DiaryImageJoinDao;
import com.vlada.selfie_app.database.dao.ImageSourceDao;
import com.vlada.selfie_app.database.entity.Diary;
import com.vlada.selfie_app.database.entity.DiaryImageJoin;
import com.vlada.selfie_app.database.entity.ImageSource;

@Database(entities = {Diary.class, ImageSource.class, DiaryImageJoin.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class MyRoomDatabase extends RoomDatabase {
    
    public abstract DiaryDao diaryDao();
    public abstract ImageSourceDao imageSourceDao();
    public abstract DiaryImageJoinDao diaryImageJoinDao();
    
    private static MyRoomDatabase INSTANCE;
    
    
    static MyRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (MyRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            MyRoomDatabase.class, "database")// name of database in file system
                            .build();
                    
                }
            }
        }
        return INSTANCE;
    }
}
