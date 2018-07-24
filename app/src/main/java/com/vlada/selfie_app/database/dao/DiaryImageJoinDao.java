package com.vlada.selfie_app.database.dao;

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
            "DiaryImageJoin.imageId=:imageId")
    List<Diary> getUsersForRepository(final int imageId);
    
    @Query("SELECT * FROM ImageSource INNER JOIN DiaryImageJoin ON " +
            "ImageSource.id=DiaryImageJoin.imageId WHERE " +
            "DiaryImageJoin.diaryId=:diaryId")
    List<ImageSource> getRepositoriesForUsers(final int diaryId);
}