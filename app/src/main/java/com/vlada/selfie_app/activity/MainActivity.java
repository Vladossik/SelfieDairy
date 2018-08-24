package com.vlada.selfie_app.activity;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.vlada.selfie_app.PasswordService;
import com.vlada.selfie_app.database.Repository;
import com.vlada.selfie_app.notification.NotificationScheduler;
import com.vlada.selfie_app.utils.BooleanCallback;
import com.vlada.selfie_app.utils.FileUtils;
import com.vlada.selfie_app.utils.PrintUtils;
import com.vlada.selfie_app.ViewModel;
import com.vlada.selfie_app.adapter.DiaryListAdapter;
import com.vlada.selfie_app.database.entity.Diary;
import com.vlada.selfie_app.fragment.DiaryListFragment;
import com.vlada.selfie_app.R;
import com.vlada.selfie_app.adapter.ViewPagerAdapter;

import java.io.File;
import java.util.List;

/**
 * Activity with all diaries
 */
public class MainActivity extends FragmentActivity {
    
    public static final int CREATE_DIARY_REQUEST = 1;
    public static final int EDIT_DIARY_REQUEST = 2;
    private static final int OPEN_SETTINGS_REQUEST = 3;
    
    private TabLayout tabLayout;
    private AppBarLayout appBarLayout;
    private ViewPager viewPager;
    private FloatingActionButton fab;
    private ViewModel viewModel;
    DiaryListFragment doneFragment;
    DiaryListFragment waitingFragment;
    private ImageView ivAvatar;
    
    private PasswordService passwordService;
    private boolean passwordEntered;
    private FloatingActionButton btnEnterPassword;
    
    private boolean isPasswordEntered() {
        return passwordEntered;
    }
    
    /**
     * Sets backing field and updates recycler views.
     */
    public void setPasswordEntered(boolean passwordEntered) {
        // show private diaries only if the password has been entered
        waitingFragment.getDiaryListAdapter().setShowPrivateDiaries(passwordEntered);
        doneFragment.getDiaryListAdapter().setShowPrivateDiaries(passwordEntered);
        this.passwordEntered = passwordEntered;
        
        if (passwordEntered || !passwordService.hasPassword()) {
            btnEnterPassword.hide();
        } else {
            // just in case
            btnEnterPassword.show();
        }
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        checkPermissions();
        
        tabLayout = findViewById(R.id.tabLayout);
//        appBarLayout = (AppBarLayout) findViewById(R.id.appBarId);
        viewPager = findViewById(R.id.viewPager);
        ivAvatar = findViewById(R.id.ivAvatar);
        
        ViewPagerAdapter vpAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        // adding fragments
        
        waitingFragment = new DiaryListFragment();
        doneFragment = new DiaryListFragment();
        
        vpAdapter.addFragment(waitingFragment, "Expect");
        vpAdapter.addFragment(doneFragment, "Done");
        
        // adapter Setup
        viewPager.setAdapter(vpAdapter);
        tabLayout.setupWithViewPager(viewPager);
        
        // fab setup
        fab = findViewById(R.id.fab);
        btnEnterPassword = findViewById(R.id.btnEnterPassword);
        
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CreateDiaryActivity.class);
                intent.putExtra("passwordEntered", isPasswordEntered());
                startActivityForResult(intent, CREATE_DIARY_REQUEST);
            }
        });
        
        btnEnterPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordService.askPasswordOrCreate(new BooleanCallback() {
                    @Override
                    public void onResult(boolean result) {
                        setPasswordEntered(result);
                    }
                });
            }
        });
        
        
        // setup database in viewModel
        viewModel = ViewModelProviders.of(this).get(ViewModel.class);
        
        // connect viewModel with diaryListAdapter in fragments
        doneFragment.setDiaryListAdapter(new DiaryListAdapter(this, viewModel));
        waitingFragment.setDiaryListAdapter(new DiaryListAdapter(this, viewModel));
        
        ivAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                intent.putExtra("passwordEntered", isPasswordEntered());
                startActivityForResult(intent, OPEN_SETTINGS_REQUEST);
            }
        });
        
        // connecting to database with diaries and asking a password
        
        passwordService = new PasswordService(this);
        // we haven't entered any password yet, but default value is true
        setPasswordEntered(false);
        connectDiaryData();
    
        // diary to start DiaryActivity if this activity was called from notification
        final Diary diaryToStart = (Diary) getIntent().getSerializableExtra("diaryToStart");
        
        
        if (diaryToStart != null) {
            if (diaryToStart.isPrivate()) {
                passwordService.askPasswordOrCreate(new BooleanCallback() {
                    @Override
                    public void onResult(boolean result) {
                        setPasswordEntered(result);
                        if (result) {
                            openDiaryActivity(diaryToStart);
                        }
                    }
                });
            } else {
                // just load non-private diary
                openDiaryActivity(diaryToStart);
            }
        }
        
        
    }
    
    private void connectDiaryData() {
        viewModel.getRepo().getAllWaitingDiaries().observe(this, new Observer<List<Diary>>() {
            @Override
            public void onChanged(@Nullable List<Diary> diaries) {
                if (diaries == null) {
                    Log.d("my_tag", "observer: null in waiting diaries");
                } else {
                    Log.d("my_tag", "observer: updated waiting diaries: " + PrintUtils.joinToString(diaries));
                    waitingFragment.getDiaryListAdapter().setDiaries(diaries);
                }
            }
        });
        viewModel.getRepo().getAllDoneDiaries().observe(this, new Observer<List<Diary>>() {
            @Override
            public void onChanged(@Nullable List<Diary> diaries) {
                if (diaries == null) {
                    Log.d("my_tag", "observer: null in done diaries");
                } else {
                    Log.d("my_tag", "observer: updated done diaries: " + PrintUtils.joinToString(diaries));
                    doneFragment.getDiaryListAdapter().setDiaries(diaries);
                }
            }
        });
    }
    
    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            // request permissions again
            checkPermissions();
        }
    }
    
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // handle results of password activities
        passwordService.onActivityResult(requestCode, resultCode, data);
        
        if ((requestCode == CREATE_DIARY_REQUEST || requestCode == EDIT_DIARY_REQUEST) && resultCode == RESULT_OK) {
            Diary diary = (Diary) data.getSerializableExtra("diary");
            
            if (requestCode == EDIT_DIARY_REQUEST) {
                // edited diary
                viewModel.getRepo().updateDiary(diary);
                if (data.getBooleanExtra("privacyChanged", false)) {
                    showEncryptionDialog(diary);
                }
            } else {
                // created diary
                viewModel.getRepo().insertDiary(diary);
            }
            
            NotificationScheduler.scheduleRemainder(this, diary);
            
            // update passwordEntered if we have entered password inside CreateDiaryActivity
            setPasswordEntered(data.getBooleanExtra("passwordEntered", isPasswordEntered()));
        }
        
        if (requestCode == OPEN_SETTINGS_REQUEST) {
            setPasswordEntered(data.getBooleanExtra("passwordEntered", isPasswordEntered()));
        }
    }
    
    private void showEncryptionDialog(Diary diary) {
        
        viewModel.getRepo().encryptWholeDiary(this, diary);
    }
    
    
    public void openActivityForEditing(Diary diary) {
        
        Intent intent = new Intent(this, CreateDiaryActivity.class);
        intent.putExtra("passwordEntered", isPasswordEntered());
        intent.putExtra("oldDiary", diary);
        
        startActivityForResult(intent, EDIT_DIARY_REQUEST);
    }
    
    public void openDiaryActivity(Diary diary) {
        final Intent intent = new Intent(this, DiaryActivity.class);
        intent.putExtra("diary", diary);
        
        startActivity(intent);
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        updateAvatar();
    }
    
    private void updateAvatar() {
        File avatarFile = FileUtils.getAvatarFile(this);
        if (avatarFile.exists()) {
            SharedPreferences settings = getSharedPreferences("settings", 0);
            int gravity = settings.getInt("avatarGravity", Gravity.CENTER);
            
            // invalidation happens when inserting new avatar
//            Picasso.get().invalidate(avatarFile);
            Picasso.get()
                    .load(avatarFile)
                    .placeholder(R.drawable.camera)
                    .resize(400, 400)
                    .onlyScaleDown()
                    .centerCrop(gravity)
                    .into(ivAvatar);
            
            
        } else {
            ivAvatar.setImageResource(R.drawable.camera);
        }
    }
}
