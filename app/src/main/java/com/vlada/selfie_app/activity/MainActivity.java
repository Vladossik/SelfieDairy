package com.vlada.selfie_app.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.persistence.room.util.StringUtil;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.vlada.selfie_app.ViewModel;
import com.vlada.selfie_app.adapter.DiaryListAdapter;
import com.vlada.selfie_app.database.entity.Diary;
import com.vlada.selfie_app.fragment.DoneFragment;
import com.vlada.selfie_app.R;
import com.vlada.selfie_app.adapter.ViewPagerAdapter;
import com.vlada.selfie_app.fragment.WaitingFragment;

import java.util.List;

public class MainActivity extends FragmentActivity {
    
    private TabLayout tabLayout;
    private AppBarLayout appBarLayout;
    private ViewPager viewPager;
    private FloatingActionButton fab;
    private ViewModel viewModel;
    DoneFragment doneFragment;
    WaitingFragment waitingFragment;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
//        appBarLayout = (AppBarLayout) findViewById(R.id.appBarId);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        
        ViewPagerAdapter vpAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        //adding fragments
        
        waitingFragment = new WaitingFragment();
        doneFragment = new DoneFragment();
        
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
                startActivityForResult(intent, 1);
            }
        });
        
        // setup database
        
        viewModel = ViewModelProviders.of(this).get(ViewModel.class);
        
        // setup viewModel for fragment to put it to rvAdapter
        doneFragment.setViewModel(viewModel);
    
        viewModel.getRepo().getAllDiaries().observe(this, new Observer<List<Diary>>() {
            @Override
            public void onChanged(@Nullable List<Diary> diaries) {
                
                if (diaries == null) {
                    Log.d("my_tag", "null in liveData observer");
                } else {
                    
                    StringBuilder s = new StringBuilder();
                    for (Diary diary : diaries) {
                        s.append(", ").append(diary);
                    }
                    
                    Log.d("my_tag", "updated data received: " + s.toString());
                    doneFragment.getRvAdapter().setDiaries(diaries);
                }
            }
        });
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Diary diary = (Diary) data.getSerializableExtra("diary");
                viewModel.getRepo().insertDiary(diary);
                // ??? when diary id changes ???
            }
        }
    }
}
