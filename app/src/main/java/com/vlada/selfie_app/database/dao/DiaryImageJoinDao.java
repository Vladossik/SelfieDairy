package com.vlada.selfie_app.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.vlada.selfie_app.database.entity.Diary;
import com.vlada.selfie_app.database.entity.DiaryImageJoin;
import com.vlada.selfie_app.database.entity.ImageSource;

import java.util.List;

@Dao
public interface DiaryImageJoinDao {
    
    @Insert
    void insert(DiaryImageJoin diaryImageJoin);
    
    @Query("SELECT * FROM Diary INNER JOIN DiaryImageJoin ON " +
            "Diary.id=DiaryImageJoin.diaryId WHERE " +
            "DiaryImageJoin.imageSrc=:imageSrc")
    LiveData<List<Diary>> getDiariesForImages(final String imageSrc);
    
    @Query("SELECT * FROM ImageSource INNER JOIN DiaryImageJoin ON " +
            "ImageSource.source=DiaryImageJoin.imageSrc WHERE " +
            "DiaryImageJoin.diaryId=:diaryId")
    LiveData<List<ImageSource>> getImagesForDiaries(final int diaryId);
}