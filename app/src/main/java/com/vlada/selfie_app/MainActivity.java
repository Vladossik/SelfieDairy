package com.vlada.selfie_app;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private AppBarLayout appBarLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = (TabLayout) findViewById(R.id.tabLayoutId);
        appBarLayout = (AppBarLayout) findViewById(R.id.appBarId);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        ViewPagerAdapter adapter =  new ViewPagerAdapter(getSupportFragmentManager());
        //adding fragments
        adapter.AddFragment(new WaitingFragmentActivity(), "Expect");
        adapter.AddFragment(new DoneFragmentActivity(),"Done");

        //adapter Setup
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}
