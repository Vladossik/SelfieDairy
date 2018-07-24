package com.vlada.selfie_app.database.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Calendar;

@Entity
public class ImageSource {
    
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    /** Path to image*/
    @NonNull
    private String imageSource;
    
    /** Date of image creation*/
    @NonNull
    private Calendar dateOfCreate;
    
    private String description;
    
    @NonNull
    public String getImageSource() {
        return imageSource;
    }
    
    public void setImageSource(@NonNull String imageSource) {
        this.imageSource = imageSource;
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
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
}
