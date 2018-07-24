package com.vlada.selfie_app.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.vlada.selfie_app.database.entity.Diary;

import java.util.List;

@Dao
public interface DiaryDao {
    
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @Insert
    void insert(Diary diary);
    
    @Query("DELETE FROM Diary")
    void deleteAll();
    
    @Query("SELECT * from Diary ORDER BY dateOfCreate ASC")
    LiveData<List<Diary>> getAllDiaries();
}