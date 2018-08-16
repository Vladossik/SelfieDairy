package com.vlada.selfie_app.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.text.format.Time;
import android.util.Log;

import com.vlada.selfie_app.database.entity.Diary;
import com.vlada.selfie_app.enums.RemindFrequency;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class NotificationsTest {
    
//    @Test
//    public void scheduleRemainderTest() throws InterruptedException {
//        Context context = InstrumentationRegistry.getTargetContext();
////        Diary diary = new Diary();
////        diary.setRemindFrequency(RemindFrequency.Secondly);
////        Notifications.scheduleRemainder(context, diary);
////        Thread.sleep(1000);
//        
//        
//        Intent intent = new Intent(context, AlarmReceiver.class);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        
//        Calendar cal = Calendar.getInstance();
//        cal.add(Calendar.MILLISECOND, 4000);
//        
//        Log.d("my_tag", "scheduleRemainderTest: setting alarm at " + new SimpleDateFormat("dd.MM.yyyy : HH.mm.ss.SSS")
//                .format(cal.getTime()));
//        
//        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
//        
//        Thread.sleep(3000);
//        Log.d("my_tag", "scheduleRemainderTest: 2 seconds left, time: " + new SimpleDateFormat("dd.MM.yyyy : HH.mm.ss.SSS")
//                .format(Calendar.getInstance().getTime()));
//        Thread.sleep(2000);
//        Log.d("my_tag", "scheduleRemainderTest: end, time: " + new SimpleDateFormat("dd.MM.yyyy : HH.mm.ss.SSS")
//                .format(Calendar.getInstance().getTime()));
//    }
}