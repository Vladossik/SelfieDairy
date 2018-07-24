package com.vlada.selfie_app.database;

import android.arch.persistence.room.TypeConverter;

import com.vlada.selfie_app.database.entity.RemindFrequency;

import java.util.Calendar;

/** Type converters for database*/
public class Converters {
    
    @TypeConverter
    public static Calendar calendarToMillis(Long millis) {
        if (millis == null)
            return null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return calendar;
    }
    
    @TypeConverter
    public static Long millisToCalendar(Calendar calendar) {
        return calendar == null ? null : calendar.getTimeInMillis();
    }
    
    @TypeConverter
    public static RemindFrequency codeToRemindFrequency(int code) {
        
        for (RemindFrequency freq : RemindFrequency.values()) {
            if (freq.code == code) {
                return freq;
            }
        }
        
        throw new IllegalArgumentException("Could not recognize code of Enum");
    }
    
    @TypeConverter
    public static int remindFrequencyToCode(RemindFrequency remindFrequency) {
        return remindFrequency.code;
    }
    
    
}
