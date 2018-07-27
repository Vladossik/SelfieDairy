package com.vlada.selfie_app.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.vlada.selfie_app.database.entity.ImageSource;

import java.util.List;

@Dao
public interface ImageSourceDao {
    
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(ImageSource imageSource);
    
    @Query("DELETE FROM ImageSource WHERE source=:src")
    void deleteBySrc(String src);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrReplace(ImageSource imageSource);
    
    @Query("DELETE FROM ImageSource")
    void deleteAll();
    
    @Query("SELECT * from ImageSource ORDER BY dateOfCreate ASC")
    LiveData<List<ImageSource>> getAllImages();
}