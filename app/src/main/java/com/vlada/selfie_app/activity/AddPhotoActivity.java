package com.vlada.selfie_app.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.vlada.selfie_app.Encryption;
import com.vlada.selfie_app.utils.FileUtils;
import com.vlada.selfie_app.ImageLoading;
import com.vlada.selfie_app.R;
import com.vlada.selfie_app.ViewModel;
import com.vlada.selfie_app.database.Repository;
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
     * Will be true when we finish activity with RESULT_OK
     */
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
     * Store image placeholder in case of Vlada changes her opinion about add icon
     */
    private Drawable imagePlaceholder;
    
    /**
     * Pointer to last saved file from camera.
     */
    private File lastSavedCameraImage;
    private ViewModel viewModel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo);
        
        // setup database in viewModel
        viewModel = ViewModelProviders.of(this).get(ViewModel.class);
        
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
        
        // remember image placeholder in case of Vlada changes her opinion about add icon
        imagePlaceholder = ivNewPhoto.getDrawable();
        
        if (getIntent().getBooleanExtra("editing", false)) {
            // editing existing imageSource
            editing = true;
            toolbar.setTitle("Edit photo");
            
            imageSource = (ImageSource) getIntent().getSerializableExtra("oldImage");
            
            tvDateOfCreate.setText(new SimpleDateFormat("dd.MM.yyyy")
                    .format(imageSource.getDateOfCreate().getTime()));
            
            fillImageView();
            etPhotoDescription.setText(imageSource.getDescription());
            
        } else {
            // creating new imageSource
            editing = false;
            toolbar.setTitle("Add new photo");
            
            tvDateOfCreate.setText(new SimpleDateFormat("dd.MM.yyyy")
                    .format(Calendar.getInstance().getTime()));
            
            showChooseImageDialog();
            
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
                        FileUtils.deleteImageIfExists(lastSavedCameraImage);
                        lastSavedCameraImage = null;
                        
                        switch (position) {
                            case 0:
                                // setup folder and file where to save new photo
                                File folder = FileUtils.getImageFolder();
                                lastSavedCameraImage = FileUtils.createImageInFolder(folder);
                                
                                // intent for creating image from camera
                                Intent cameraIntent = FileUtils.createCameraIntent(AddPhotoActivity.this, lastSavedCameraImage);
                                
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
                
                if (requestCode == CAMERA_REQUEST_CODE) {// from camera
                    
                    if (lastSavedCameraImage != null && lastSavedCameraImage.exists()) {
                    } else {
                        Toast.makeText(this, "Error: Image from camera not found.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    imageSource = new ImageSource(lastSavedCameraImage.getAbsolutePath(), diary.getId());
                    
                    if (diary.isPrivate()) {
                        try {
                            Encryption.encryptFile(this, imageSource.getSourceFile(), imageSource.getEncodedFile());
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Encryption error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            imageSource = null;
                            return;
                        }
                        imageSource.setEncrypted(true);
                        FileUtils.deleteImageIfExists(imageSource.getSourceFile());
                    } else {
                        // notify gallery about new non-encrypted image
                        FileUtils.scanGalleryForImage(this, lastSavedCameraImage);
                    }
                } else { // from gallery
                    Uri imageUri = data.getData();
                    imageSource = new ImageSource(getRealPathFromURI(imageUri), diary.getId());
                    
                    if (diary.isPrivate()) {
                        try {
                            Encryption.encryptFile(this, imageSource.getSourceFile(), imageSource.getEncodedFile());
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Encryption error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            imageSource = null;
                            return;
                        }
                        imageSource.setEncrypted(true);
                    }
                }
                
                // check if image does not exist in database.
                
                viewModel.getRepo().checkIfImageExists(imageSource.getSource(),
                        imageSource.getDiaryId(), new Repository.BooleanCallback() {
                            @Override
                            public void onResult(boolean result) {
                                if (result) {
                                    Toast.makeText(AddPhotoActivity.this,
                                            "Can not add this image. It already exists in this diary.", Toast.LENGTH_SHORT).show();
                                    // unset imageSource
                                    FileUtils.deleteImageIfExists(imageSource.getEncodedFile());
                                    imageSource = null;
                                    // set default image for iv
                                    ivNewPhoto.setImageDrawable(imagePlaceholder);
                                } else {
                                    // if no image was found - fill imageView.
                                    fillImageView();
                                    imageSource.setDateOfCreate(Calendar.getInstance());
                                }
                            }
                        });
            }
        }
    }
    
    private void fillImageView() {
        if (imageSource == null)
            return;
        
        File imageFile = ImageLoading.getDecodedImage(this, imageSource);
        
        if (imageFile == null) {
            Toast.makeText(this, "Error while loading image", Toast.LENGTH_SHORT).show();
            return;
        }
        
        Picasso.get()
                .load(imageFile)
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
            FileUtils.deleteImageIfExists(lastSavedCameraImage);
        }
        
        super.onDestroy();
    }
}
