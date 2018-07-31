package com.vlada.selfie_app.activity;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
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
import android.util.Log;
import android.view.View;

import com.vlada.selfie_app.ViewModel;
import com.vlada.selfie_app.adapter.DiaryListAdapter;
import com.vlada.selfie_app.database.entity.Diary;
import com.vlada.selfie_app.fragment.DiaryListFragment;
import com.vlada.selfie_app.R;
import com.vlada.selfie_app.adapter.ViewPagerAdapter;

import java.util.Collections;
import java.util.List;

public class MainActivity extends FragmentActivity {
    
    public static final int CREATE_DIARY_REQUEST = 1;
    public static final int EDIT_DIARY_REQUEST = 2;
    private TabLayout tabLayout;
    private AppBarLayout appBarLayout;
    private ViewPager viewPager;
    private FloatingActionButton fab;
    private ViewModel viewModel;
    DiaryListFragment doneFragment;
    DiaryListFragment waitingFragment;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        checkPermissions();
        
        tabLayout = findViewById(R.id.tabLayout);
//        appBarLayout = (AppBarLayout) findViewById(R.id.appBarId);
        viewPager = findViewById(R.id.viewPager);
        
        ViewPagerAdapter vpAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        //adding fragments
        
        waitingFragment = new DiaryListFragment();
        doneFragment = new DiaryListFragment();
        
        vpAdapter.addFragment(waitingFragment, "Expect");
        vpAdapter.addFragment(doneFragment, "Done");
        
        //adapter Setup
        viewPager.setAdapter(vpAdapter);
        tabLayout.setupWithViewPager(viewPager);
        
        // fab setup
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CreateDiaryActivity.class);
                startActivityForResult(intent, CREATE_DIARY_REQUEST);
            }
        });
        
        // setup database in viewModel
        viewModel = ViewModelProviders.of(this).get(ViewModel.class);
        
        // connect viewModel with diaryListAdapter in fragments
        doneFragment.setDiaryListAdapter(new DiaryListAdapter(this, viewModel));
        waitingFragment.setDiaryListAdapter(new DiaryListAdapter(this, viewModel));
        
        viewModel.getRepo().getAllWaitingDiaries().observe(this, new Observer<List<Diary>>() {
            @Override
            public void onChanged(@Nullable List<Diary> diaries) {
                if (diaries == null) {
                    Log.d("my_tag", "observer: null in waiting diaries");
                } else {
                    Log.d("my_tag", "observer: updated waiting diaries: " + joinToString(diaries));
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
                    Log.d("my_tag", "observer: updated done diaries: " + joinToString(diaries));
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
    
    
    private <T> String joinToString(List<T> list) {
        StringBuilder s = new StringBuilder();
        for (T item : list) {
            s.append(", ").append(item);
        }
        return s.toString();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == CREATE_DIARY_REQUEST) {
            if (resultCode == RESULT_OK) {
                Diary diary = (Diary) data.getSerializableExtra("diary");
                viewModel.getRepo().insertDiary(diary);
            }
        } else if (requestCode == EDIT_DIARY_REQUEST) {
            if (resultCode == RESULT_OK) {
                Diary diary = (Diary) data.getSerializableExtra("diary");
                viewModel.getRepo().updateDiary(diary);
            }
        }
    }
    
    
    public void openActivityForEditing(Diary diary) {
        
        Intent intent = new Intent(this, CreateDiaryActivity.class);
        
        intent.putExtra("oldDiary", diary);
        
        startActivityForResult(intent, EDIT_DIARY_REQUEST);
    }
    
    public void openDiaryActivity(Diary diary) {
        Intent intent = new Intent(this, DiaryActivity.class);
        intent.putExtra("diary", diary);
        startActivity(intent);
    }
}
