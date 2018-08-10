package com.vlada.selfie_app.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.vlada.selfie_app.ImageLoading;
import com.vlada.selfie_app.R;
import com.vlada.selfie_app.Utils;
import com.vlada.selfie_app.ViewModel;
import com.vlada.selfie_app.adapter.ImageListAdapter;
import com.vlada.selfie_app.database.entity.Diary;
import com.vlada.selfie_app.database.entity.ImageSource;

import java.util.ArrayList;
import java.util.List;

public class DiaryActivity extends AppCompatActivity {
    
    public static final int ADD_PHOTO_REQUEST = 1;
    public static final int EDIT_PHOTO_REQUEST = 2;
    RecyclerView rvImageList;
    
    private ImageListAdapter imageListAdapter;
    ViewModel viewModel;
    
    private Diary diary;
    
    private HandlerThread imageLoadingThread;
    private Handler imageLoadingHandler;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);
        
        imageLoadingThread = new HandlerThread("loading");
        imageLoadingThread.start();
        imageLoadingHandler = new Handler(imageLoadingThread.getLooper());
        
        
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
                    List<ImageSource> deleted = findDeletedImages(imageSources);
                    
                    if (!deleted.isEmpty()) {
                        startDialogToDeleteImages(deleted, imageSources);
                    } else {
                        imageListAdapter.setImages(imageSources);
                    }
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
    
    @Override
    protected void onDestroy() {
        imageLoadingThread.quit();
        if (ImageLoading.clearImageCache())
            Toast.makeText(this, "All cached images are destroyed", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }
    
    List<ImageSource> findDeletedImages(List<ImageSource> imageList) {
        List<ImageSource> deleted = new ArrayList<>();
        
        for (ImageSource image : imageList) {
            if (!image.getSourceFile().exists() && !image.getEncodedFile().exists()) {
                deleted.add(image);
            }
        }
        
        if (!deleted.isEmpty()) {
            Log.d("my_tag", "findDeletedImages: Found deleted images: " + Utils.joinToString(deleted));
        }
        
        return deleted;
    }
    
    public void startDialogToDeleteImages(final List<ImageSource> deletedImages, final List<ImageSource> allImages) {
        
        new AlertDialog.Builder(this)
                .setTitle("Remove deleted photos")
                .setMessage("Found some deleted files. Remove them from diary?")
                .setCancelable(false)
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // updating images in rv as usual
                        imageListAdapter.setImages(allImages);
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // requesting deletion of files in database and then 
                        // waiting for new data update in observer
                        ImageSource[] array = deletedImages.toArray(new ImageSource[deletedImages.size()]);
                        viewModel.getRepo().deleteImage(array);
                    }
                })
                .create()
                .show();
        
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
                    
                    // Scrolling rv on top with delay 100 ms
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            LinearLayoutManager layoutManager = (LinearLayoutManager) rvImageList.getLayoutManager();
                            layoutManager.smoothScrollToPosition(rvImageList, new RecyclerView.State(), 0);
                        }
                    }, 100);
                }
            }
        }
    }
    
    public Handler getImageLoadingHandler() {
        return imageLoadingHandler;
    }
}
