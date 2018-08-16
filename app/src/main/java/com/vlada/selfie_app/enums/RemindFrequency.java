package com.vlada.selfie_app.enums;

public enum RemindFrequency {
    Never(0, -1),
    Daily(1, 24 * 60 * 60 * 1000),
    Weekly(2, 24 * 60 * 60 * 7 * 1000),
    Monthly(3, 24 * 60 * 60 * 31 * 1000),
    FiveSeconds(4, 5 * 1000),
    TenSeconds(5, 10 * 1000);
    
    public final int code;
    
    RemindFrequency(int code, long timeInMillis) {
        this.code = code;
        this.timeInMillis = timeInMillis;
    }
    
    public final long timeInMillis;
    
}


