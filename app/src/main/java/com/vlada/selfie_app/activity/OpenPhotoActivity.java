package com.vlada.selfie_app.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.media.ExifInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.vlada.selfie_app.ImageLoading;
import com.vlada.selfie_app.R;
import com.vlada.selfie_app.ViewModel;
import com.vlada.selfie_app.database.entity.ImageSource;
import com.vlada.selfie_app.utils.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class OpenPhotoActivity extends AppCompatActivity {
    
    private ImageSource imageSource;
    private EditText etPhotoDescription;
    private ImageView ivPhoto;
    private Button btnAvatar;
    private Button btnShare;
    TextView tvDateOfCreate;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_photo);
        // setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("View photo");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorAccent));
        setSupportActionBar(toolbar);
        
        ivPhoto = findViewById(R.id.ivPhoto);
        etPhotoDescription = findViewById(R.id.etPhotoDescription);
        
        btnAvatar = findViewById(R.id.btnAvatar);
        btnShare = findViewById(R.id.btnShare);
        tvDateOfCreate = findViewById(R.id.tvDateOfCreate);
        
        imageSource = (ImageSource) getIntent().getSerializableExtra("oldImage");
        
        etPhotoDescription.setText(imageSource.getDescription());
        
        
        File imageFile = ImageLoading.getDecodedImage(OpenPhotoActivity.this, imageSource);
        ImageLoading.loadImageInView(imageFile, ivPhoto);
        
        tvDateOfCreate.setText(new SimpleDateFormat("dd.MM.yyyy")
                .format(imageSource.getDateOfCreate().getTime()));
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
            saveDescription();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    
    
    private void saveDescription() {
        imageSource.setDescription(etPhotoDescription.getText().toString());
        ViewModelProviders.of(this).get(ViewModel.class)
                .getRepo().updateImage(imageSource);

//        imageSource.setDescription(etPhotoDescription.getText().toString());
//    
//        Intent intent = new Intent();
//        intent.putExtra("imageSource", imageSource);
//        setResult(RESULT_OK, intent);
//        finish();
    }
    
    
    public void btnShareClick(View v) {
        
    }
    
    public void btnAvatarClick(View v) {
        showAvatarGravityDialog(this, new Runnable() {
            @Override
            public void run() {
                changeAvatarFile();
            }
        });
    }
    
    private void changeAvatarFile() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // just copying this file to avatar path
                File avatarFile = FileUtils.getAvatarFile(OpenPhotoActivity.this);
                avatarFile.delete();
                
                File imageFile = ImageLoading.getDecodedImage(OpenPhotoActivity.this, imageSource);
                
                try {
                    FileUtils.copyFile(imageFile, avatarFile);
                    Picasso.get().invalidate(avatarFile);
                    
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(OpenPhotoActivity.this, "Avatar changed!", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }

//                Bitmap outBitmap = getScaledAvatar(imageFile);
//
//                FileOutputStream fOut;
//                try {
//                    fOut = new FileOutputStream(avatarFile);
//                    outBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
//                    fOut.flush();
//                    fOut.close();
//                    outBitmap.recycle();
//                    Log.d("my_tag", "btnAvatarClick: avatar was saved to: " + avatarFile.getAbsolutePath());
//
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(OpenPhotoActivity.this, "Avatar changed!", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//
//                } catch (IOException e) {
//                    Log.d("my_tag", "btnAvatarClick: IOException: " + e.getMessage());
//                }
            }
        }).start();
    }
    
    
    public static void showAvatarGravityDialog(final Context context, final Runnable onResult) {
        // setup the alert builder
        // add a radio button list
        String[] animals = {"top", "bottom", "left", "right", "center"};
        final int[] checkedItem = {4};
        
        new AlertDialog.Builder(context)
                .setTitle("Choose avatar gravity")
                .setSingleChoiceItems(animals, checkedItem[0], new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkedItem[0] = which;
                    }
                })
                .setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int gravity = Gravity.CENTER;
                        switch (checkedItem[0]) {
                            case 0:
                                gravity = Gravity.TOP;
                                break;
                            case 1:
                                gravity = Gravity.BOTTOM;
                                break;
                            case 2:
                                gravity = Gravity.LEFT;
                                break;
                            case 3:
                                gravity = Gravity.RIGHT;
                                break;
                            case 4:
                                gravity = Gravity.CENTER;
                                break;
                        }
                        //save avatar gravity
                        SharedPreferences settings = context.getSharedPreferences("settings", 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putInt("avatarGravity", gravity);
                        // should save immediately
                        editor.commit();
                        
                        onResult.run();
                    }
                }).create().show();
    }
    
    
    
//    private Bitmap getScaledAvatar(File imageFile) {
//        Bitmap bmp = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
//        
//        Bitmap bmpRotated;
//        // fixing rotation
//        try {
//            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
//            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
//            Log.d("my_tag", "getScaledAvatar: image exif: " + orientation);
//            Matrix matrix = new Matrix();
//            if (orientation == 6) {
//                matrix.postRotate(90);
//            }
//            else if (orientation == 3) {
//                matrix.postRotate(180);
//            }
//            else if (orientation == 8) {
//                matrix.postRotate(270);
//            }
//            
//            bmpRotated = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true); // rotating bitmap
//        }
//        catch (Exception e) {
//            bmpRotated = bmp;
//        }
//        
//        
//        // cropping
//        Bitmap bmpCropped;
//        if (bmpRotated.getWidth() >= bmpRotated.getHeight()){
//        
//            bmpCropped = Bitmap.createBitmap(
//                    bmpRotated,
//                    bmpRotated.getWidth()/2 - bmpRotated.getHeight()/2,
//                    0,
//                    bmpRotated.getHeight(),
//                    bmpRotated.getHeight()
//            );
//        
//        }else{
//            bmpCropped = Bitmap.createBitmap(
//                    bmpRotated,
//                    0,
//                    bmpRotated.getHeight()/2 - bmpRotated.getWidth()/2,
//                    bmpRotated.getWidth(),
//                    bmpRotated.getWidth()
//            );
//        }
//        bmp.recycle();
//        bmpRotated.recycle();
//        
//        // scaling
//        Bitmap bmpScaled = Bitmap.createScaledBitmap(bmpCropped, 400, 400, false);
//        bmpCropped.recycle();
//        
//        
//        return bmpScaled;
//    }
}
