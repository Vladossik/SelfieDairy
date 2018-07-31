package com.vlada.selfie_app.activity;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import android.widget.Toast;

import com.vlada.selfie_app.ViewModel;
import com.vlada.selfie_app.database.entity.Diary;
import com.vlada.selfie_app.database.entity.ImageSource;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class AddPhotoActivityTest {
    
    
    @Rule
    public ActivityTestRule<AddPhotoActivity> activityRule
            = new ActivityTestRule<>(
            AddPhotoActivity.class,
            true,     // initialTouchMode
            false);   // launchActivity. False to customize the intent
    
    @Test
    public void testUri() throws InterruptedException {
        
        Diary diary = new Diary("d1");
        
        
        Intent intent = new Intent();
        intent.putExtra("diary", diary);
        
        activityRule.launchActivity(intent);
        
        Log.d("my_tag", "in test");
        
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                activityRule.getActivity().ivNewPhoto.callOnClick();
            }
        });
        
        while (activityRule.getActivity().imageSource == null) {
            Thread.sleep(100);
        }
        ImageSource imageSource = activityRule.getActivity().imageSource;
        
        
        Log.d("my_tag", "Image source: " + imageSource.getSource());
        
        Log.d("my_tag", "External directory: " + Environment.getExternalStorageDirectory().getAbsolutePath());
        
        File file = new File(imageSource.getSource());
        
        Log.d("my_tag", "File exists: " + file.exists());
        
        Bitmap bitmap = BitmapFactory.decodeFile(imageSource.getSource());
        
        Log.d("my_tag", "Bitmap size: " + bitmap.getByteCount());
        
        
        Thread.sleep(5000);
        
    }
}