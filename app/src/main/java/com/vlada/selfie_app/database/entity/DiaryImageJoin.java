package com.vlada.selfie_app.database.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.support.annotation.NonNull;

/**
 * Diary to ImageSource join table
 * 
 * Copied from here:
 * https://android.jlelse.eu/android-architecture-components-room-relationships-bf473510c14a
 */
@Entity(primaryKeys = {"diaryId", "imageSrc"},
        foreignKeys = {
                @ForeignKey(entity = Diary.class,
                        parentColumns = "id",
                        childColumns = "diaryId"),
                @ForeignKey(entity = ImageSource.class,
                        parentColumns = "source",
                        childColumns = "imageSrc")
        })
public class DiaryImageJoin {
    @NonNull
    public final int diaryId;
    @NonNull
    public final String imageSrc;
    
    public DiaryImageJoin(final int diaryId, @NonNull final String imageSrc) {
        this.diaryId = diaryId;
        this.imageSrc = imageSrc;
    }
}
