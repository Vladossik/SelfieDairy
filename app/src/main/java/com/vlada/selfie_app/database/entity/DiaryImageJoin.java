package com.vlada.selfie_app.database.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;

/**
 * Diary to ImageSource join table
 * 
 * Copied from here:
 * https://android.jlelse.eu/android-architecture-components-room-relationships-bf473510c14a
 */
@Entity(primaryKeys = {"diaryId", "imageId"},
        foreignKeys = {
                @ForeignKey(entity = Diary.class,
                        parentColumns = "id",
                        childColumns = "diaryId"),
                @ForeignKey(entity = ImageSource.class,
                        parentColumns = "id",
                        childColumns = "imageId")
        })
public class DiaryImageJoin {
    
    public final int diaryId;
    public final int imageId;
    
    public DiaryImageJoin(final int diaryId, final int imageId) {
        this.diaryId = diaryId;
        this.imageId = imageId;
    }
}
