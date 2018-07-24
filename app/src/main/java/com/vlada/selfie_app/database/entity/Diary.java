package com.vlada.selfie_app.database.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Calendar;

@Entity
public class Diary {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    @NonNull
    private String name;
    
    @NonNull
    private String description;
    
    /** reminder notification time*/
    private Calendar reminder;
    
    @NonNull
    private Calendar dateOfCreate;
    
    
    private RemindFrequency remindFrequency;
    
//    public Diary(@NonNull Calendar dateOfCreate) {
//        this.dateOfCreate = dateOfCreate;
//    }
    
    public Diary() {
        dateOfCreate = Calendar.getInstance();
    }
    
    public int getId() {
        return id;
    }
    
    @NonNull
    public String getName() {
        return name;
    }
    
    public void setName(@NonNull String name) {
        this.name = name;
    }
    
    @NonNull
    public String getDescription() {
        return description;
    }
    
    public void setDescription(@NonNull String description) {
        this.description = description;
    }
    
    /** get reminder notification time*/
    public Calendar getReminder() {
        return reminder;
    }
    
    /** set reminder notification time*/
    public void setReminder(Calendar reminder) {
        this.reminder = reminder;
    }
    
    public RemindFrequency getRemindFrequency() {
        return remindFrequency;
    }
    
    public void setRemindFrequency(RemindFrequency remindFrequency) {
        this.remindFrequency = remindFrequency;
    }
    
    @NonNull
    public Calendar getDateOfCreate() {
        return dateOfCreate;
    }
    
    public void setDateOfCreate(@NonNull Calendar dateOfCreate) {
        this.dateOfCreate = dateOfCreate;
    }
    
    public void setId(int id) {
        this.id = id;
    }
}
