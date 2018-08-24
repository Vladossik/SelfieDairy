package com.vlada.selfie_app.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.vlada.selfie_app.PasswordService;
import com.vlada.selfie_app.R;
import com.vlada.selfie_app.utils.BooleanCallback;
import com.vlada.selfie_app.utils.FileUtils;

import java.io.File;

public class SettingsActivity extends AppCompatActivity {
    
    private PasswordService passwordService;
    private Switch swchAskPasswordOnStart;
    private SharedPreferences preferences;
    private ImageView ivAvatar;
    
    /**
     * Flag if the password has been entered in MainActivity (and we can create there private diaries).
     * Should be received from getIntent() and returned with setResult()
     * 
     * It changes when we remove password or create password.
     */
    private boolean passwordEntered;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        
        preferences = getSharedPreferences("settings", 0);
        
        passwordEntered = getIntent().getBooleanExtra("passwordEntered", false);
        
        ivAvatar = findViewById(R.id.ivAvatar);
        updateAvatar();
        
        passwordService = new PasswordService(this);
        
        swchAskPasswordOnStart = findViewById(R.id.swchAskPasswordOnStart);
        swchAskPasswordOnStart.setChecked(preferences.getBoolean("askPasswordOnStart", false));
        swchAskPasswordOnStart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("askPasswordOnStart", isChecked);
                editor.apply();
            }
        });
        
        
        findViewById(R.id.btnDeleteAvatar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileUtils.deleteImageIfExists(FileUtils.getAvatarFile(SettingsActivity.this));
                updateAvatar();
            }
        });
        findViewById(R.id.btnChangeAvatarGravity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenPhotoActivity.showAvatarGravityDialog(SettingsActivity.this, new Runnable() {
                    @Override
                    public void run() {
                        updateAvatar();
                    }
                });
            }
        });
        findViewById(R.id.btnRemovePassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordService.deletePasswordWithDialog(new Runnable() {
                    @Override
                    public void run() {
                        passwordEntered = false;
                        Toast.makeText(SettingsActivity.this, "Done!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        findViewById(R.id.btnChangePassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordService.changePasswordOrCreate(new BooleanCallback() {
                    @Override
                    public void onResult(boolean result) {
                        if (result) {
                            passwordEntered = true;
                        }
                    }
                });
            }
        });
    }
    
    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra("passwordEntered", passwordEntered);
        setResult(RESULT_CANCELED, intent);
        
        super.finish();
    }
    
    private void updateAvatar() {
        File avatarFile = FileUtils.getAvatarFile(this);
        if (avatarFile.exists()) {
            int gravity = preferences.getInt("avatarGravity", Gravity.CENTER);
            
            // invalidation happens when inserting new avatar
//            Picasso.get().invalidate(avatarFile);
            Picasso.get()
                    .load(avatarFile)
                    .placeholder(R.drawable.camera)
                    .resize(600, 600)
                    .onlyScaleDown()
                    .centerCrop(gravity)
                    .into(ivAvatar);
        } else {
            ivAvatar.setImageResource(R.drawable.camera);
        }
    }
}
