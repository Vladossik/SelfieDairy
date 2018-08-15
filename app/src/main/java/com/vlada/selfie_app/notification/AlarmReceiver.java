package com.vlada.selfie_app.notification;

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
import com.vlada.selfie_app.database.entity.Diary;

import java.text.SimpleDateFormat;

import static android.content.Context.VIBRATOR_SERVICE;

public class AlarmReceiver extends BroadcastReceiver {
    
    @Override
    public void onReceive(Context context, Intent intent) {
        
        // Vibrate for 100 milliseconds
        if (Build.VERSION.SDK_INT >= 26) {
            ((Vibrator) context.getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            ((Vibrator) context.getSystemService(VIBRATOR_SERVICE)).vibrate(100);
        }
        
        Diary diary = (Diary) intent.getSerializableExtra("diary");
        
        // For our recurring task, we'll just display a message
        Toast.makeText(context, "I'm running with a diary named " + diary.getName() + ".", Toast.LENGTH_SHORT).show();
        Log.d("my_tag", "received alarm");
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
//                .setContentTitle("Title")
//                .setContentText("add photo")
//                .setAutoCancel(true)
//                .setSmallIcon(R.drawable.camera)
//                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
    }
}
