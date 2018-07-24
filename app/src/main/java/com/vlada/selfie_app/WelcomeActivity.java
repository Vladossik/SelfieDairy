package com.vlada.selfie_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class WelcomeActivity extends AppCompatActivity {
    
    String password;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        
        //load the password
        SharedPreferences settings = getSharedPreferences("PREFS", 0);
        password = settings.getString("password", "");
        
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                finish();
//                if (password.equals("")) {
//                    //if there is no password
//                    Intent intent = new Intent(WelcomeActivity.this, CreatePasswordActivity.class);
//                    startActivity(intent);
//                    finish();
//                } else {
//                    //if there ia s password
//                    Intent intent = new Intent(WelcomeActivity.this, EnterPasswordActivity.class);
//                    startActivity(intent);
//                    finish();
//                }
            }
        }, 2000);
        
    }
}
