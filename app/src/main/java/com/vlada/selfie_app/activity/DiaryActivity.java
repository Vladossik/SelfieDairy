package com.vlada.selfie_app.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.vlada.selfie_app.R;
import com.vlada.selfie_app.adapter.ImageListAdapter;

public class DiaryActivity extends AppCompatActivity {
    
    RecyclerView rvImageList;
    
    private ImageListAdapter imageListAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);
        
        imageListAdapter = new ImageListAdapter();
        rvImageList = findViewById(R.id.rvImageList);
        rvImageList.setAdapter(imageListAdapter);
        rvImageList.setLayoutManager(new LinearLayoutManager(this));
        
    }
}
