package com.vlada.selfie_app.database.entity;

public enum RemindFrequency {
    Daily(0), Weekly(1), Monthly(2);
    
    public final int code;
    
    RemindFrequency(int code) {
        this.code = code;
    }
}
