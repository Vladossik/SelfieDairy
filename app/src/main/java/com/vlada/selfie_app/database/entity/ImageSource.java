package com.vlada.selfie_app.database.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Calendar;

@Entity
public class ImageSource {
    
//    @PrimaryKey(autoGenerate = true)
//    private int id;
    
    /** Path to image*/
    @NonNull
    @PrimaryKey
    private String source;
    
    /** Date of image creation*/
    @NonNull
    private Calendar dateOfCreate;
    
    private String description;
    
    
    private ImageSource() {
        dateOfCreate = Calendar.getInstance();
    }
    
    public ImageSource(@NonNull String source) {
        this();
        this.source = source;
    }
    
    @Ignore
    public ImageSource(@NonNull String source, String description) {
        // setup dateOfCreate in this()
        this();
        this.source = source;
        this.description = description;
    }
    
    @NonNull
    public String getSource() {
        return source;
    }
    
    public void setSource(@NonNull String source) {
        this.source = source;
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
}
