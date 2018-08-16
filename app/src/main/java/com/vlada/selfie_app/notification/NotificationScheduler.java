package com.vlada.selfie_app.notification;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.vlada.selfie_app.R;
import com.vlada.selfie_app.activity.DiaryActivity;
import com.vlada.selfie_app.database.entity.Diary;
import com.vlada.selfie_app.enums.RemindFrequency;
import com.vlada.selfie_app.utils.SerializationUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.content.Context.VIBRATOR_SERVICE;

public class NotificationScheduler extends BroadcastReceiver {
    
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("my_tag", "in onReceive!!!!!!!!!!!!!!!!!!!!!!!!!!! at " + new SimpleDateFormat("dd.MM.yyyy : HH.mm.ss.SSS")
                .format(Calendar.getInstance().getTime()));
        Log.d("my_tag", "onReceive: message: " + intent.getStringExtra("message"));
        
        // Vibrate for 100 milliseconds
        if (Build.VERSION.SDK_INT >= 26) {
            ((Vibrator) context.getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            ((Vibrator) context.getSystemService(VIBRATOR_SERVICE)).vibrate(500);
        }
        
        Diary diary = null;
        try {
            diary = (Diary) SerializationUtils.convertFromBytes(intent.getByteArrayExtra("diary"));
        } catch (IOException ignored) {
        } catch (ClassNotFoundException e) {
            Log.d("my_tag", "onReceive: ClassNotFoundException");
        }
        
        // For our recurring task, we'll just display a message
        Toast.makeText(context, "Alarm from diary: " + diary.getName(), Toast.LENGTH_SHORT).show();
        Log.d("my_tag", "received alarm from diary: " + diary.getName());
        
         NotificationPublisher.sendNotification(context, diary);
        
        // setting new alarm
        scheduleRemainder(context, diary);
        
    }
    
    
    public static void scheduleRemainder(Context context, Diary diary) {
        
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        
        Calendar firstCallTime = Calendar.getInstance();
        firstCallTime.add(Calendar.MILLISECOND, 3000);
//        firstCallTime.set(Calendar.HOUR_OF_DAY, diary.getReminder().get(Calendar.HOUR_OF_DAY));
//        firstCallTime.set(Calendar.MINUTE, diary.getReminder().get(Calendar.MINUTE));
//
//        if (firstCallTime.before(Calendar.getInstance())) {
//            firstCallTime.add(Calendar.DAY_OF_YEAR, 1);
//        }
        PendingIntent pendingIntent = pendingIntentFromDiary(context, diary);
        
        if (diary.getRemindFrequency() == RemindFrequency.Never) {
            alarmManager.cancel(pendingIntent);
            Log.d("my_tag", "scheduleRemainder: canceled alarm for diary: " + diary.getName());
        } else {
            Log.d("my_tag", "scheduleRemainder: scheduled alarm at " + new SimpleDateFormat("dd.MM.yyyy : HH.mm.ss.SSS")
                    .format(firstCallTime.getTime()) + " for diary: " + diary.getName());

//            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, firstCallTime.getTimeInMillis(),
//                    1000 * 60, pendingIntent);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, firstCallTime.getTimeInMillis(), pendingIntent);
        }
    }
    
    /**
     * Returns a pending intent with request code, identical for each diary.
     */
    private static PendingIntent pendingIntentFromDiary(Context context, Diary diary) {
        
        /* Retrieve a PendingIntent that will perform a broadcast */
        Intent alarmIntent = new Intent(context, NotificationScheduler.class);
        alarmIntent.putExtra("message", "hello from scheduler!");
        
        try {
            alarmIntent.putExtra("diary", SerializationUtils.convertToBytes(diary));
        } catch (IOException e) {
        }
        
        return PendingIntent.getBroadcast(context, diary.getId(), alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
    
    
}


