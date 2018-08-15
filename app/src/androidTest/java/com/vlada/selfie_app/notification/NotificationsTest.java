package com.vlada.selfie_app.notification;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.vlada.selfie_app.database.entity.Diary;
import com.vlada.selfie_app.enums.RemindFrequency;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class NotificationsTest {
    
    @Test
    public void scheduleRemainderTest() throws InterruptedException {
        Context context = InstrumentationRegistry.getTargetContext();
        Diary diary = new Diary();
        diary.setRemindFrequency(RemindFrequency.Secondly);
        Notifications.scheduleRemainder(context, diary);
        Thread.sleep(1000);
    }
}