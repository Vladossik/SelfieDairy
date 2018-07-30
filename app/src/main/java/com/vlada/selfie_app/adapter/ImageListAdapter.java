package com.vlada.selfie_app.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vlada.selfie_app.R;
import com.vlada.selfie_app.ViewModel;
import com.vlada.selfie_app.activity.DiaryActivity;
import com.vlada.selfie_app.activity.MainActivity;
import com.vlada.selfie_app.database.entity.Diary;
import com.vlada.selfie_app.database.entity.ImageSource;

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
            holder.dateOfCreate.setText(current.getDateOfCreate().toString());
            
            // TODO: 31.07.2018  set image from uri
//            holder.imageView.setImage
            
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
                    
                    showDiarySetupDialog(v.getContext(), images.get(position));
                }
                
                return true;
            }
        });
        
    }
    
    public void showDiarySetupDialog(final Context context, final Diary diary) {
        
        final String doneOrWaiting = diary.isDone() ? "Expected" : "Done";
        
        new AlertDialog.Builder(context)
                .setTitle("Setup " + diary.getName())
                .setNegativeButton("Cancel", null)
                .setItems(new String[]{"Edit", "Set " + doneOrWaiting, "Remove"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dlg, int position) {
                        switch (position) {
                            case 0:
                                Toast.makeText(context, "Edition", Toast.LENGTH_SHORT).show();
                                activity.openActivityForEditing(diary);
                                break;
                            case 1:
                                Toast.makeText(context, "Setting diary as " + doneOrWaiting, Toast.LENGTH_SHORT).show();
                                diary.setDone(!diary.isDone());
                                
                                viewModel.getRepo().updateDiary(diary);
                                break;
                            case 2:
                                Toast.makeText(context, "Removing diary", Toast.LENGTH_SHORT).show();
                                viewModel.getRepo().deleteDiary(diary);
                                break;
                        }
                        
                    }
                })
                .create()
                .show();
    }
    
    
    public void setImages(List<Diary> newDiaries) {
        if (images != null && newDiaries != null) {
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new MyDiffCallback(this.images, newDiaries));
            images = newDiaries;
            diffResult.dispatchUpdatesTo(this);
        } else {
            images = newDiaries;
            notifyDataSetChanged();
        }
    }
    
    private class MyDiffCallback extends DiffUtil.Callback {
        List<Diary> oldDiaries;
        List<Diary> newDiaries;
    
        MyDiffCallback(List<Diary> oldDiaries, List<Diary> newDiaries) {
            this.newDiaries = newDiaries;
            this.oldDiaries = oldDiaries;
        }
        @Override
        public int getOldListSize() {
            return oldDiaries.size();
        }
    
        @Override
        public int getNewListSize() {
            return newDiaries.size();
        }
    
        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldDiaries.get(oldItemPosition).getId() == newDiaries.get(newItemPosition).getId();
        }
    
        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldDiaries.get(oldItemPosition).equals(newDiaries.get(newItemPosition));
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
