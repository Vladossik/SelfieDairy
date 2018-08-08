package com.vlada.selfie_app.database;

import android.arch.persistence.room.TypeConverter;

import com.vlada.selfie_app.enums.RemindFrequency;

import java.util.Calendar;

/** Type converters for database*/
public class Converters {
    
    @TypeConverter
    public static Calendar millisToCalendar(Long millis) {
        if (millis == null)
            return null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return calendar;
    }
    
    @TypeConverter
    public static Long calendarToMillis(Calendar calendar) {
        return calendar == null ? null : calendar.getTimeInMillis();
    }
    
    @TypeConverter
    public static RemindFrequency codeToRemindFrequency(Integer code) {
        if (code == null)
            return null;
        
        for (RemindFrequency freq : RemindFrequency.values()) {
            if (freq.code == code) {
                return freq;
            }
        }
        
        throw new IllegalArgumentException("Could not recognize code of Enum");
    }
    
    @TypeConverter
    public static Integer remindFrequencyToCode(RemindFrequency remindFrequency) {
        return remindFrequency == null ? null : remindFrequency.code;
    }
}
