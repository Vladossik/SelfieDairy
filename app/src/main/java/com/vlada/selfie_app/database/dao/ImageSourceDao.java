package com.vlada.selfie_app.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.vlada.selfie_app.database.entity.ImageSource;

import java.util.List;

@Dao
public interface ImageSourceDao {
    
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(ImageSource... imageSources);
    
    @Query("DELETE FROM ImageSource WHERE source=:source AND diaryId=:diaryId")
    void deleteByKeys(String source, int diaryId);
    
    @Query("SELECT * FROM ImageSource WHERE source=:source AND diaryId=:diaryId")
    List<ImageSource> findByKeys(String source, int diaryId);
    
    @Delete
    void delete(ImageSource... imageSources);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrReplace(ImageSource imageSource);
    
    @Query("DELETE FROM ImageSource")
    void deleteAll();
    
    @Query("SELECT * from ImageSource WHERE diaryId=:diaryId ORDER BY dateOfCreate DESC")
    List<ImageSource> getImagesForDiary(int diaryId);
    
    @Query("SELECT * from ImageSource WHERE diaryId=:diaryId ORDER BY dateOfCreate DESC")
    LiveData<List<ImageSource>> getImagesForDiaryLive(int diaryId);
    
    
    @Query("SELECT * from ImageSource ORDER BY dateOfCreate DESC")
    LiveData<List<ImageSource>> getAllImagesLive();
    
    @Query("SELECT * from ImageSource ORDER BY dateOfCreate DESC")
    List<ImageSource> getAllImages();
    
    @Update
    void update(ImageSource imageSource);
}