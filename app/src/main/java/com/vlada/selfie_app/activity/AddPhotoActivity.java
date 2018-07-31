package com.vlada.selfie_app.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vlada.selfie_app.R;
import com.vlada.selfie_app.database.entity.Diary;
import com.vlada.selfie_app.database.entity.ImageSource;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddPhotoActivity extends AppCompatActivity {
    
    public static final int CAMERA_REQUEST_CODE = 0;
    public static final int GALLERY_REQUEST_CODE = 1;
    EditText etPhotoDescription;
    TextView tvDateOfCreate;
    ImageView ivNewPhoto;
    
    
    /**
     * ImageSource for creating new photo.
     */
    ImageSource imageSource;
    
    private Diary diary;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo);
        
        // get diary from previous activity
        diary = (Diary) getIntent().getSerializableExtra("diary");
        
        
        // setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Add new photo");
        toolbar.setTitleTextAppearance(this, R.style.customFontStyle);
        setSupportActionBar(toolbar);
        
        // init views
        etPhotoDescription = findViewById(R.id.etPhotoDescription);
        ivNewPhoto = findViewById(R.id.ivNewPhoto);
        tvDateOfCreate = findViewById(R.id.tvDateOfCreate);
        
        // sets No Date until we don't have an image.
        updateDateOfCreateText();
        
        ivNewPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChooseImageDialog();
            }
        });
    }
    
    // TODO: 31.07.2018 replace choose image dialog with modal bottom sheet 
    private void showChooseImageDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Where to choose image?")
                .setItems(new String[]{"Camera", "Gallery"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dlg, int position) {
                        switch (position) {
                            case 0:
                                Toast.makeText(AddPhotoActivity.this, "Selected camera", Toast.LENGTH_SHORT).show();
                                
                                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(takePicture, CAMERA_REQUEST_CODE);//zero can be replaced with any action code
                                
                                break;
                            case 1:
                                Toast.makeText(AddPhotoActivity.this, "Selected Gallery", Toast.LENGTH_SHORT).show();
                                
                                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(pickPhoto, GALLERY_REQUEST_CODE);//one can be replaced with any action code
                                
                                break;
                        }
                    }
                })
                .create()
                .show();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.create_diary_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.itemDone) {
            saveImage();
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void saveImage() {
        if (imageSource == null) {
            Toast.makeText(this, "No image selected!", Toast.LENGTH_SHORT).show();
            return;
        }
        
        imageSource.setDescription(etPhotoDescription.getText().toString());
        
        Intent intent = new Intent();
        intent.putExtra("imageSource", imageSource);
        setResult(RESULT_OK, intent);
        finish();
    }
    
    
    public void btnSavePhotoClick(View v) {
        saveImage();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_CODE || requestCode == GALLERY_REQUEST_CODE) {
                Uri imageUri = data.getData();
//                ivNewPhoto.setImageURI(imageUri);
                Log.d("my_tag", "received image Uri from picker: " + imageUri);
                
                imageSource = new ImageSource(getRealPathFromURI(imageUri), diary.getId());
                
                Bitmap bitmap = BitmapFactory.decodeFile(imageSource.getSource());
                ivNewPhoto.setImageBitmap(bitmap);

//                ivNewPhoto.setImageURI(Uri.fromFile(new File(imageSource.getSource())));
                
                updateDateOfCreateText();
            }
        }
    }
    
    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }
    
    /**
     * Updates tvDateOfCreate text from imageSource
     */
    private void updateDateOfCreateText() {
        if (imageSource != null) {
            tvDateOfCreate.setText(new SimpleDateFormat("dd.MM.yyyy")
                    .format(imageSource.getDateOfCreate().getTime()));
        } else {
            tvDateOfCreate.setText("No date of create");
        }
    }
}
