package com.vlada.selfie_app.activity;

import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;

import com.vlada.selfie_app.fragment.DoneFragment;
import com.vlada.selfie_app.R;
import com.vlada.selfie_app.adapter.ViewPagerAdapter;
import com.vlada.selfie_app.fragment.WaitingFragment;

public class MainActivity extends FragmentActivity {
    
    private TabLayout tabLayout;
    private AppBarLayout appBarLayout;
    private ViewPager viewPager;
    private FloatingActionButton fab;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        tabLayout = (TabLayout) findViewById(R.id.tabLayoutId);
//        appBarLayout = (AppBarLayout) findViewById(R.id.appBarId);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        //adding fragments
        adapter.addFragment(new WaitingFragment(), "Expect");
        adapter.addFragment(new DoneFragment(), "Done");
        
        //adapter Setup
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        
        // fab setup
        
        fab = findViewById(R.id.fab);
    
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CreateDiaryActivity.class);
                startActivity(intent);
            }
        });
        
    }
}
