package com.vlada.selfie_app.database.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Calendar;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(primaryKeys = {"source", "diaryId"},
        foreignKeys = @ForeignKey(
                entity = Diary.class,
                parentColumns = "id",
                childColumns = "diaryId",
                onDelete = CASCADE
        ))
public class ImageSource {
    /**
     * Path to image
     */
    @NonNull
    private String source;
    
    
    /** Id of bound diary. Should be const*/
    private int diaryId;
    
    /**
     * Date of image creation.
     */
    @NonNull
    private Calendar dateOfCreate;
    
    private String description;
    
    /** Primary constructor*/
    public ImageSource(@NonNull String source, int diaryId) {
        this.diaryId = diaryId;
        this.source = source;
        dateOfCreate = Calendar.getInstance();
    }
    
    @Ignore
    public ImageSource(@NonNull String source, int diaryId, String description) {
        // setup dateOfCreate in this()
        this(source, diaryId);
        this.source = source;
        this.description = description;
    }
    
    @NonNull
    public String getSource() {
        return source;
    }
    
    @NonNull
    public Calendar getDateOfCreate() {
        return dateOfCreate;
    }
    
    public void setDateOfCreate(@NonNull Calendar dateOfCreate) {
        this.dateOfCreate = dateOfCreate;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public int getDiaryId() {
        return diaryId;
    }
}
