package com.vlada.selfie_app.adapter;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vlada.selfie_app.utils.FileUtils;
import com.vlada.selfie_app.ImageLoading;
import com.vlada.selfie_app.R;
import com.vlada.selfie_app.ViewModel;
import com.vlada.selfie_app.activity.DiaryActivity;
import com.vlada.selfie_app.database.entity.ImageSource;

import java.io.File;
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
    
    /**
     * Handler, bound to async looper thread for background image downloading
     */
    private Handler getImageLoadingHandler() {
        return activity.getImageLoadingHandler();
    }
    
    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = mInflater.inflate(R.layout.image_item, parent, false);
        return new ImageViewHolder(root);
    }
    
    @Override
    public void onBindViewHolder(@NonNull final ImageViewHolder holder, final int position) {
        if (images != null) {
            final ImageSource imageSource = images.get(position);
            
            holder.dateOfCreate.setText(new SimpleDateFormat("dd.MM.yyyy HH:mm")
                    .format(imageSource.getDateOfCreate().getTime()));
            Log.d("my_tag", "onBindViewHolder-" + position + ": received path: " + imageSource.getSource());
            
            
            if (imageSource.isEncrypted()) {
                // start image loading in other thread
                getImageLoadingHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        // encoding or just picking from cache or default directory
                        final File imageFile = ImageLoading.getDecodedImage(activity, imageSource);
                        // returning back to main thread
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ImageLoading.loadImageInView(imageFile, holder.imageView);
                            }
                        });
                    }
                });
            } else { // if image is not encrypted - load file and launch picasso in main thread
                final File imageFile = ImageLoading.getDecodedImage(activity, imageSource);
                ImageLoading.loadImageInView(imageFile, holder.imageView);
            }
            
            // setup visibility for empty description
            if (imageSource.getDescription().equals("")) {
                holder.description.setVisibility(View.GONE);
            } else {
                holder.description.setVisibility(View.VISIBLE);
            }
            // set description text to view
            holder.description.setText(imageSource.getDescription());
            
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
                }
                showImageDeletionDialog(images.get(position));
                return true;
            }
        });
        
        
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.openActivityToEditImage(images.get(position));
            }
        });
    }
    
    
    private void showImageDeletionDialog(final ImageSource image) {
        String[] items = new String[]{"Delete file with image"};
        final boolean[] checkedItems = new boolean[]{false};
        
        final boolean hasImageSource = image.getSourceFile().exists();
        
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        
        // ask to delete source file, using check box. (if we have anything to delete)
        if (hasImageSource) {
            builder.setMultiChoiceItems(items, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    checkedItems[which] = isChecked;
                }
            });
        }
        
        builder
                .setTitle("Delete image")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // just delete image from path
                        viewModel.getRepo().deleteImage(image);
                        Log.d("my_tag", "showImageDeletionDialog: deleted image from db");
                        
                        // trying to delete encoded source and cached file
                        FileUtils.deleteImageIfExists(image.getEncodedFile());
                        FileUtils.deleteImageIfExists(image.getCachedFile(activity));
                        
                        if (hasImageSource && checkedItems[0]) {
                            // delete primary source file as well
                            File sourceFile = image.getSourceFile();
                            if (FileUtils.deleteImageIfExists(sourceFile)) {
                                Log.d("my_tag", "showImageDeletionDialog: deleted source file as well");
                                Toast.makeText(activity, "File was deleted", Toast.LENGTH_SHORT).show();
                                FileUtils.scanGalleryForImage(activity, sourceFile);
                            } else {
                                Toast.makeText(activity, "Failed file deleting.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        
                        
                    }
                })
                .create()
                .show();
    }
    
    /**
     * Updates images in rv, using diffUtil
     */
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
