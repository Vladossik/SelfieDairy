package com.vlada.selfie_app.database.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.support.annotation.NonNull;

import com.vlada.selfie_app.FileUtils;

import java.io.File;
import java.io.Serializable;
import java.util.Calendar;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(primaryKeys = {"source", "diaryId"},
        foreignKeys = @ForeignKey(
                entity = Diary.class,
                parentColumns = "id",
                childColumns = "diaryId",
                onDelete = CASCADE
        ))
public class ImageSource implements Serializable {
    /**
     * Path to image
     */
    @NonNull
    private String source;
    
    @NonNull
    private String encodedSource;
    
    /**
     * Id of bound diary. Should be const
     */
    private int diaryId;
    
    /**
     * Date of image creation.
     */
    @NonNull
    private Calendar dateOfCreate;
    
    private String description;
    
    private boolean isEncrypted = false;
    
    /**
     * Primary constructor
     */
    public ImageSource(@NonNull String source, int diaryId) {
        this.diaryId = diaryId;
        this.source = source;
        dateOfCreate = Calendar.getInstance();
        
        genEncodedSource();
    }
    
    @Ignore
    public ImageSource(@NonNull String source, int diaryId, String description) {
        // setup dateOfCreate in this()
        this(source, diaryId);
        this.source = source;
        this.description = description;
    }
    
    /** Generates a path for encoded image copy.*/
    public void genEncodedSource() {
        String sourceName = getSourceFile().getName();
        encodedSource = new File(FileUtils.getEncodedFolder(), sourceName + ".encoded").getAbsolutePath();
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
    
    public int getDiaryId() {
        return diaryId;
    }
    
    @Override
    public String toString() {
        return "ImageSource with source: " + getSource() + " and description: " + getDescription();
    }
    
    
    /**
     * Returns default image source string, converted to File object.
     */
    @Ignore
    public File getSourceFile() {
        return new File(getSource());
    }
    
    @Ignore
    public File getEncodedFile() {
        File encodedSource = new File(getEncodedSource());
        encodedSource.getParentFile().mkdirs();
        return encodedSource;
        
    }
    
    @Ignore
    public File getCachedFile() {
        File cacheFolder = FileUtils.getImageCacheFolder();
        cacheFolder.mkdirs();
        return new File(cacheFolder, "cached_" + getSourceFile().getName());
    }
    
    
    @NonNull
    public String getEncodedSource() {
        return encodedSource;
    }
    
    public void setEncodedSource(@NonNull String encodedSource) {
        this.encodedSource = encodedSource;
    }
    
    public boolean isEncrypted() {
        return isEncrypted;
    }
    
    public void setEncrypted(boolean encrypted) {
        isEncrypted = encrypted;
    }
}
