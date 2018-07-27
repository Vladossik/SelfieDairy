package com.vlada.selfie_app.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.support.annotation.NonNull;

import com.vlada.selfie_app.database.entity.Diary;

import java.util.List;

@Dao
public interface DiaryDao {
    
    @Insert
    long insert(Diary diary);
    
    @Update
    void update(Diary diary);
    
    @Query("DELETE FROM Diary")
    void deleteAll();
    
    @Query("DELETE FROM Diary WHERE id=:id")
    void deleteById(int id);
    
    @Query("SELECT * from Diary ORDER BY dateOfCreate ASC")
    LiveData<List<Diary>> getAllDiaries();
}