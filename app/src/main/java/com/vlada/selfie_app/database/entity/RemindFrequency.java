package com.vlada.selfie_app.database.entity;

public enum RemindFrequency {
    Never(0), Daily(1), Weekly(2), Monthly(3);
    
    public final int code;
    
    RemindFrequency(int code) {
        this.code = code;
    }
}


