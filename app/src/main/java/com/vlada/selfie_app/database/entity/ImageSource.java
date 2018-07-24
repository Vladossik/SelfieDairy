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
    
    
}
