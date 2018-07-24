package com.vlada.selfie_app.database.entity;

public enum RemindFrequency {
    Monthly(0), Weekly(1), Daily(2);
    
    public final int code;
    
    RemindFrequency(int code) {
        this.code = code;
    }
}
