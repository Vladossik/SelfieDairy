package com.vlada.selfie_app.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vlada.selfie_app.R;
import com.vlada.selfie_app.ViewModel;
import com.vlada.selfie_app.activity.DiaryActivity;
import com.vlada.selfie_app.database.entity.ImageSource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.List;

import static android.content.Context.VIBRATOR_SERVICE;

public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ImageViewHolder> {
    
    class ImageViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView dateOfCreate;
        private final TextView description;
        
        /**
         * Root view for item. Usually it is LinearLayout
         */
        private final View root;
        
        private ImageViewHolder(View root) {
            super(root);
            this.root = root;
            imageView = root.findViewById(R.id.imageView);
            dateOfCreate = root.findViewById(R.id.dateOfCreate);
            description = root.findViewById(R.id.description);
        }
    }
    
    private final LayoutInflater mInflater;
    private ViewModel viewModel;
    private List<ImageSource> images;
    private DiaryActivity activity;
    
    public ImageListAdapter(DiaryActivity activity, ViewModel viewModel) {
        mInflater = LayoutInflater.from(activity);
        this.viewModel = viewModel;
        this.activity = activity;
    }
    
    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = mInflater.inflate(R.layout.image_item, parent, false);
        return new ImageViewHolder(root);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, final int position) {
        if (images != null) {
            ImageSource current = images.get(position);
            
            holder.dateOfCreate.setText(new SimpleDateFormat("dd.MM.yyyy")
                    .format(current.getDateOfCreate().getTime()));
            Log.d("my_tag", "onBindViewHolder: received path: " + current.getSource());
            
            // create bitmap
            
//            Bitmap bitmap = BitmapFactory.decodeFile(current.getSource());
            holder.imageView.setImageBitmap(getBitmap(current.getSource()));
            
            holder.description.setText(current.getDescription());
        } else {
            // Covers the case of data not being ready yet.
            holder.description.setText("No description");
        }
        
        holder.root.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (images != null) {
                    // Vibrate for 100 milliseconds
                    if (Build.VERSION.SDK_INT >= 26) {
                        ((Vibrator) activity.getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        ((Vibrator) activity.getSystemService(VIBRATOR_SERVICE)).vibrate(100);
                    }
                    
                    // just delete image from path
                    viewModel.getRepo().deleteImage(images.get(position));
                }

                return true;
            }
        });
    }
    
    
    private Bitmap getBitmap(String path) {
        String TAG = "my_tag";
        Uri uri = Uri.fromFile(new File(path));
        InputStream in = null;
        try {
            final int IMAGE_MAX_SIZE = 1200000; // 1.2MP
            in = activity.getContentResolver().openInputStream(uri);
    
            // Decode image size
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, options);
            in.close();
    
    
            int scale = 1;
            while ((options.outWidth * options.outHeight) * (1 / Math.pow(scale, 2)) >
                    IMAGE_MAX_SIZE) {
                scale++;
            }
            Log.d(TAG, "scale = " + scale + ", orig-width: " + options.outWidth
                    + ", orig-height: " + options.outHeight);
    
            Bitmap resultBitmap = null;
            in = activity.getContentResolver().openInputStream(uri);
            if (scale > 1) {
                scale--;
                // scale to max possible inSampleSize that still yields an image
                // larger than target
                options = new BitmapFactory.Options();
                options.inSampleSize = scale;
                resultBitmap = BitmapFactory.decodeStream(in, null, options);
    
                // resize to desired dimensions
                int height = resultBitmap.getHeight();
                int width = resultBitmap.getWidth();
                Log.d(TAG, "1th scale operation dimenions - width: " + width
                        + ", height: " + height);
    
                double y = Math.sqrt(IMAGE_MAX_SIZE
                        / (((double) width) / height));
                double x = (y / height) * width;
    
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(resultBitmap, (int) x,
                        (int) y, true);
                resultBitmap.recycle();
                resultBitmap = scaledBitmap;
    
                System.gc();
            } else {
                resultBitmap = BitmapFactory.decodeStream(in);
            }
            in.close();
    
            Log.d(TAG, "bitmap size - width: " + resultBitmap.getWidth() + ", height: " +
                    resultBitmap.getHeight());
            return resultBitmap;
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
            return null;
        }
    }
//    public void showDiarySetupDialog(final Context context, final Diary diary) {
//        
//        final String doneOrWaiting = diary.isDone() ? "Expected" : "Done";
//        
//        new AlertDialog.Builder(context)
//                .setTitle("Setup " + diary.getName())
//                .setNegativeButton("Cancel", null)
//                .setItems(new String[]{"Edit", "Set " + doneOrWaiting, "Remove"}, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dlg, int position) {
//                        switch (position) {
//                            case 0:
//                                Toast.makeText(context, "Edition", Toast.LENGTH_SHORT).show();
//                                activity.openActivityForEditing(diary);
//                                break;
//                            case 1:
//                                Toast.makeText(context, "Setting diary as " + doneOrWaiting, Toast.LENGTH_SHORT).show();
//                                diary.setDone(!diary.isDone());
//                                
//                                viewModel.getRepo().updateDiary(diary);
//                                break;
//                            case 2:
//                                Toast.makeText(context, "Removing diary", Toast.LENGTH_SHORT).show();
//                                viewModel.getRepo().deleteDiary(diary);
//                                break;
//                        }
//                        
//                    }
//                })
//                .create()
//                .show();
//    }
    
    /** Updates images in rv, using diffUtil*/
    public void setImages(List<ImageSource> newImages) {
        if (images != null && newImages != null) {
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new MyDiffCallback(this.images, newImages));
            images = newImages;
            diffResult.dispatchUpdatesTo(this);
        } else {
            images = newImages;
            notifyDataSetChanged();
        }
    }
    
    private class MyDiffCallback extends DiffUtil.Callback {
        List<ImageSource> oldImages;
        List<ImageSource> newImages;
    
        MyDiffCallback(List<ImageSource> oldImages, List<ImageSource> newImages) {
            this.newImages = newImages;
            this.oldImages = oldImages;
        }
        @Override
        public int getOldListSize() {
            return oldImages.size();
        }
    
        @Override
        public int getNewListSize() {
            return newImages.size();
        }
    
        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldImages.get(oldItemPosition).getSource().equals(newImages.get(newItemPosition).getSource());
        }
    
        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldImages.get(oldItemPosition).equals(newImages.get(newItemPosition));
        }
    }
    
    
    // getItemCount() is called many times, and when it is first called,
    // images has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (images != null)
            return images.size();
        else return 0;
    }
}
