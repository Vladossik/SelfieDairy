package com.vlada.selfie_app.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.TextView;

import com.vlada.selfie_app.R;
import com.vlada.selfie_app.database.entity.ImageSource;

public class AddPhotoActivity extends AppCompatActivity {
    
    private EditText photoDescription;
    private TextView dateOfCreate;
    
    /**
     * ImageSource for creating new photo.
     */
    ImageSource imageSource;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo);
    
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Add new photo");
        toolbar.setTitleTextAppearance(this,R.style.customFontStyle);
        setSupportActionBar(toolbar);
    
        photoDescription = findViewById(R.id.photoDescription);
        photoDescription.setText(imageSource.getDescription());
        
        dateOfCreate = findViewById(R.id.dateOfCreate);
//        dateOfCreate.setText(imageSource.getDateOfCreate());
    }
}
