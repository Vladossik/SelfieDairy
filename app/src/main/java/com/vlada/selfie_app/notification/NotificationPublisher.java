package com.vlada.selfie_app.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.vlada.selfie_app.R;
import com.vlada.selfie_app.activity.DiaryActivity;
import com.vlada.selfie_app.database.entity.Diary;

public class NotificationPublisher {
    
    
    private static final String CHANNEL_ID = "diary_channel";
    
    public static void sendNotification(Context context, Diary diary) {
        // needed only for android 8
        createNotificationChannel(context);
        
        Intent intent = new Intent(context, DiaryActivity.class);
        intent.putExtra("diary", diary);
        
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, diary.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("SelfieDiary")
                .setContentText("add photo to diary: " + diary.getName())
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.camera)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent)
                .build();
        
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        
        mNotificationManager.notify(diary.getId(), notification);
        
    }
    
    
    private static void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.diary_notification_channel_name);
            String description = context.getString(R.string.diary_notification_channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
