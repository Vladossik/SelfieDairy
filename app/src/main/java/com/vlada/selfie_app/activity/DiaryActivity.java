package com.vlada.selfie_app.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.vlada.selfie_app.R;
import com.vlada.selfie_app.ViewModel;
import com.vlada.selfie_app.adapter.ImageListAdapter;
import com.vlada.selfie_app.database.entity.Diary;
import com.vlada.selfie_app.database.entity.ImageSource;

import java.util.List;

public class DiaryActivity extends AppCompatActivity {
    
    public static final int ADD_PHOTO_REQUEST = 1;
    public static final int EDIT_PHOTO_REQUEST = 2;
    RecyclerView rvImageList;
    
    private ImageListAdapter imageListAdapter;
    ViewModel viewModel;
    
    private Diary diary;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);
        
        // get diary from previous activity
        diary = (Diary) getIntent().getSerializableExtra("diary");
        
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(diary.getName());
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorAccent));
        
        rvImageList = findViewById(R.id.rvImageList);
        rvImageList.setLayoutManager(new LinearLayoutManager(this));
        
        
        // setup database in viewModel
        viewModel = ViewModelProviders.of(this).get(ViewModel.class);
        
        
        imageListAdapter = new ImageListAdapter(this, viewModel);
        rvImageList.setAdapter(imageListAdapter);
        
        viewModel.getRepo().getAllImagesForDiary(diary.getId()).observe(this, new Observer<List<ImageSource>>() {
            @Override
            public void onChanged(@Nullable List<ImageSource> imageSources) {
                if (imageSources != null) {
                    imageListAdapter.setImages(imageSources);
                }
            }
        });
        
        // fab setup
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DiaryActivity.this, AddPhotoActivity.class);
                intent.putExtra("diary", diary);
                intent.putExtra("editing", false);
                
                startActivityForResult(intent, ADD_PHOTO_REQUEST);
            }
        });
    }
    
    public void openActivityToEditImage(ImageSource image) {
        Intent intent = new Intent(this, AddPhotoActivity.class);
        intent.putExtra("diary", diary);
        intent.putExtra("editing", true);
        intent.putExtra("oldImage", image);
        startActivityForResult(intent, EDIT_PHOTO_REQUEST);
    }
    
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == ADD_PHOTO_REQUEST || requestCode == EDIT_PHOTO_REQUEST) {
            if (resultCode == RESULT_OK) {
                ImageSource imageSource = (ImageSource) data.getSerializableExtra("imageSource");
                
                if (requestCode == EDIT_PHOTO_REQUEST) {
                    viewModel.getRepo().updateImage(imageSource);
                } else {
                    viewModel.getRepo().insertImage(imageSource);
                    
                    // Scrolling rv on top
                    LinearLayoutManager layoutManager = (LinearLayoutManager) rvImageList.getLayoutManager();
                    layoutManager.smoothScrollToPosition(rvImageList, new RecyclerView.State(), 0);
                }
            }
        }
    }
}
