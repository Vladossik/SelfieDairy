package com.vlada.selfie_app.notification;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.vlada.selfie_app.R;
import com.vlada.selfie_app.activity.DiaryActivity;
import com.vlada.selfie_app.database.entity.Diary;
import com.vlada.selfie_app.enums.RemindFrequency;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

public class Notifications {
    
    public static void scheduleRemainder(Context context, Diary diary) {
        
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        
        Calendar firstCallTime = Calendar.getInstance();
//        firstCallTime.set(Calendar.HOUR_OF_DAY, diary.getReminder().get(Calendar.HOUR_OF_DAY));
//        firstCallTime.set(Calendar.MINUTE, diary.getReminder().get(Calendar.MINUTE));
//
//        if (firstCallTime.before(Calendar.getInstance())) {
//            firstCallTime.add(Calendar.DAY_OF_YEAR, 1);
//        }
        PendingIntent pendingIntent = pendingIntentFromDiary(context, diary);
        if (diary.getRemindFrequency() == RemindFrequency.Never) {
            
            alarmManager.cancel(pendingIntent);
        } else {
            Log.d("my_tag", "scheduled " + new SimpleDateFormat("dd.MM.yyyy : hh.mm.ss")
                    .format(firstCallTime.getTime()));
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, firstCallTime.getTimeInMillis()+1,
                    200, pendingIntent);
        }
        
    }
    
    private static PendingIntent pendingIntentFromDiary(Context context, Diary diary) {
        
        /* Retrieve a PendingIntent that will perform a broadcast */
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        alarmIntent.putExtra("diary", diary);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, diary.getId(), alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        
        return pendingIntent;
    }
    
    
}


