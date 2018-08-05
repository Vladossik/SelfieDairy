package com.vlada.selfie_app.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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

import com.squareup.picasso.Picasso;
import com.vlada.selfie_app.FileUtil;
import com.vlada.selfie_app.R;
import com.vlada.selfie_app.database.entity.Diary;
import com.vlada.selfie_app.database.entity.ImageSource;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddPhotoActivity extends AppCompatActivity {
    
    public static final int CAMERA_REQUEST_CODE = 0;
    public static final int GALLERY_REQUEST_CODE = 1;
    EditText etPhotoDescription;
    TextView tvDateOfCreate;
    ImageView ivNewPhoto;
    
    /** Will be true when we finish activity with RESULT_OK*/
    boolean resultOk = false;
    
    /**
     * True if we are editing old image
     */
    Boolean editing = false;
    /**
     * ImageSource for creating new photo or editing.
     */
    ImageSource imageSource;
    
    private Diary diary;
    
    
    /**
     * Pointer to last saved file from camera.
     */
    private File lastSavedCameraImage;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo);
        
        // get diary from previous activity
        diary = (Diary) getIntent().getSerializableExtra("diary");
        
        
        // setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Add new photo");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorAccent));
        setSupportActionBar(toolbar);
        
        // init views
        etPhotoDescription = findViewById(R.id.etPhotoDescription);
        ivNewPhoto = findViewById(R.id.ivNewPhoto);
        tvDateOfCreate = findViewById(R.id.tvDateOfCreate);
        
        
        if (getIntent().getBooleanExtra("editing", false)) {
            // editing existing imageSource
            editing = true;
            toolbar.setTitle("Edit photo");
            
            imageSource = (ImageSource) getIntent().getSerializableExtra("oldImage");
            
            tvDateOfCreate.setText(new SimpleDateFormat("dd.MM.yyyy")
                    .format(imageSource.getDateOfCreate().getTime()));
            
            fillImageView(imageSource.getSource());
            etPhotoDescription.setText(imageSource.getDescription());
            
        } else {
            // creating new imageSource
            editing = false;
            toolbar.setTitle("Add new photo");
            
            tvDateOfCreate.setText(new SimpleDateFormat("dd.MM.yyyy")
                    .format(Calendar.getInstance().getTime()));
            
        }
        
        
        ivNewPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editing) {
                    Toast.makeText(AddPhotoActivity.this,
                            "Can not select new image in edit mode.", Toast.LENGTH_SHORT).show();
                    return;
                }
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
                        // deleting previous created photo if it exists
                        FileUtil.deleteImageIfExists(lastSavedCameraImage);
                        lastSavedCameraImage = null;
                        
                        switch (position) {
                            case 0:
                                // setup folder and file where to save new photo
                                File folder = FileUtil.geFolderInExternal();
                                lastSavedCameraImage = FileUtil.createImageFromFolder(folder);
                                
                                // intent for creating image from camera
                                Intent cameraIntent = FileUtil.createCameraIntent(AddPhotoActivity.this, lastSavedCameraImage);
                                
                                startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                                
                                break;
                            case 1:
                                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
                                
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
        
        // change color for icon 0
        Drawable myIcon = menu.getItem(0).getIcon();
        myIcon.mutate();
        myIcon.setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);
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
        resultOk = true;
        finish();
    }
    
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_CODE || requestCode == GALLERY_REQUEST_CODE) {
                
                Uri imageUri;
                if (requestCode == CAMERA_REQUEST_CODE) {// from camera
                    
                    if (lastSavedCameraImage != null && lastSavedCameraImage.exists()) {
                        imageUri = Uri.fromFile(lastSavedCameraImage);
                        galleryAddPic(lastSavedCameraImage);
                    } else {
                        Toast.makeText(this, "Error: Image from camera not found.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else { // from gallery
                    imageUri = data.getData();
                }
//                ivNewPhoto.setImageURI(imageUri);
                Log.d("my_tag", "received image Uri from picker: " + imageUri);
                
                imageSource = new ImageSource(getRealPathFromURI(imageUri), diary.getId());
                
                fillImageView(imageSource.getSource());
                
                imageSource.setDateOfCreate(Calendar.getInstance());
            }
        }
    }
    
    private void galleryAddPic(File imageFile) {
        // directly insert in android image database
//        try {
//            MediaStore.Images.Media.insertImage(getContentResolver(),
//                    imageFile.getAbsolutePath(), imageFile.getName(), null);
//        } catch (FileNotFoundException e) {
//            Log.d("my_tag", "galleryAddPic: error: " + e.getMessage());
//            e.printStackTrace();
//        }
        
        // sending broadcast to scan new images.
        sendBroadcast(new Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(imageFile)));
        
        
    }
    
    private void fillImageView(String imagePath) {
        Picasso.get()
                .load(new File(imagePath))
                .resize(800, 800)
                .onlyScaleDown()
                .centerInside()
                .into(ivNewPhoto);
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
    
    @Override
    protected void onDestroy() {
        
        if (!resultOk) {
            FileUtil.deleteImageIfExists(lastSavedCameraImage);
        }
        
        super.onDestroy();
    }
}
