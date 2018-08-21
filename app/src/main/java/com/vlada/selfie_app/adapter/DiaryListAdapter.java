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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vlada.selfie_app.R;
import com.vlada.selfie_app.ViewModel;
import com.vlada.selfie_app.activity.MainActivity;
import com.vlada.selfie_app.database.Repository;
import com.vlada.selfie_app.database.dao.ImageSourceDao;
import com.vlada.selfie_app.database.entity.Diary;
import com.vlada.selfie_app.database.entity.ImageSource;
import com.vlada.selfie_app.utils.FileUtils;

import java.io.File;
import java.util.List;
import java.util.ListIterator;

import static android.content.Context.VIBRATOR_SERVICE;

public class DiaryListAdapter extends RecyclerView.Adapter<DiaryListAdapter.DiaryViewHolder> {
    
    class DiaryViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final TextView description;
        private final ImageView ivPrivate;
        /**
         * Root view for item. Usually it is LinearLayout
         */
        private final View root;
        
        private DiaryViewHolder(View root) {
            super(root);
            this.root = root;
            title = root.findViewById(R.id.title);
            description = root.findViewById(R.id.description);
            ivPrivate = root.findViewById(R.id.ivPrivate);
        }
    }
    
    private final LayoutInflater mInflater;
    private ViewModel viewModel;
    
    // Cached copy of diaries
    private List<Diary> diaries;
    private MainActivity activity;
    
    public DiaryListAdapter(MainActivity activity, ViewModel viewModel) {
        mInflater = LayoutInflater.from(activity);
        this.viewModel = viewModel;
        this.activity = activity;
    }
    
    @NonNull
    @Override
    public DiaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = mInflater.inflate(R.layout.diary_item, parent, false);
        return new DiaryViewHolder(root);
    }
    
    @Override
    public void onBindViewHolder(@NonNull DiaryViewHolder holder, final int position) {
        if (diaries != null) {
            Diary diary = diaries.get(position);
            holder.title.setText(diary.getName());
            holder.description.setText(diary.getDescription());
            holder.ivPrivate.setVisibility(diary.isPrivate() ? View.VISIBLE : View.INVISIBLE);
        } else {
            // Covers the case of data not being ready yet.
            holder.title.setText("No Title");
            holder.description.setText("No description");
        }
        
        holder.root.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (diaries != null) {
                    // Vibrate for 100 milliseconds
                    if (Build.VERSION.SDK_INT >= 26) {
                        ((Vibrator) activity.getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        ((Vibrator) activity.getSystemService(VIBRATOR_SERVICE)).vibrate(100);
                    }
                    
                    showDiarySetupDialog(v.getContext(), diaries.get(position));
                }
                
                return true;
            }
        });
        
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (diaries != null) {
                    activity.openDiaryActivity(diaries.get(position));
                }
            }
        });
        
    }
    
    private void showDiarySetupDialog(final Context context, final Diary diary) {
        
        final String doneOrWaiting = diary.isDone() ? "Expected" : "Done";
        
        new AlertDialog.Builder(context)
                .setTitle("Setup " + diary.getName())
                .setNegativeButton("Cancel", null)
                .setItems(new String[]{"Edit", "Set " + doneOrWaiting, "Remove"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dlg, int position) {
                        switch (position) {
                            case 0:
                                activity.openActivityForEditing(diary);
                                break;
                            case 1:
                                diary.setDone(!diary.isDone());
                                viewModel.getRepo().updateDiary(diary);
                                break;
                            case 2:
                                showDeleteDiaryDialog(diary);
                                break;
                        }
                        
                    }
                })
                .create()
                .show();
    }
    
    private void showDeleteDiaryDialog(final Diary diary) {
        String[] items = new String[]{"Delete all image files"};
        final boolean[] checkedItems = new boolean[]{false};
        
        new AlertDialog.Builder(activity)
                .setTitle("Removing " + diary.getName())
                .setMessage("Are you sure?")
                .setMultiChoiceItems(items, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        checkedItems[which] = isChecked;
                    }
                })
                .setNegativeButton("Cancel", null)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final boolean deleteSourceFiles = checkedItems[0];
                        
                        viewModel.getRepo().deleteDiary(diary, deleteSourceFiles, new Repository.DeleteDiaryCallback() {
                            @Override
                            public void onResult(int sourcesCount, int encodedCount, int allImagesCount) {
                                if (deleteSourceFiles || diary.isPrivate()) {
                                    String message = "Deleted:\n";
                                    if (deleteSourceFiles) {
                                        message += "" + sourcesCount + " source image files\n";
                                    }
                                    if (diary.isPrivate()) {
                                        message += "" + encodedCount + " encoded image files\n";
                                    }
                                    message += "from " + allImagesCount + " images";
                                    
                                    final String finalMessage = message;
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(activity, finalMessage, Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }
                        });
                    }
                })
                .create()
                .show();
    }
    
    /**
     * Here we always have private diaries even if they are not visible
     */
    private List<Diary> extraDiaries;
    
    private boolean showPrivateDiaries = true;
    
    public void setShowPrivateDiaries(boolean value) {
        if (value != showPrivateDiaries) {
            showPrivateDiaries = value;
            onShowPrivateDiariesChanged();
        } else {
            this.showPrivateDiaries = value;
        }
    }
    
    private void onShowPrivateDiariesChanged() {
        setDiaries(extraDiaries);
    }
    
    /**
     * Updates diaries in rv, using diffUtil
     */
    public void setDiaries(List<Diary> newDiaries) {
        if (newDiaries == null) {
            return;
        }
        // save all diaries with private
        extraDiaries = newDiaries;
        if (!showPrivateDiaries) {
            // filter newDiaries
            newDiaries = Repository.filterPrivateDiaries(newDiaries);
        }
        
        if (diaries != null && newDiaries != null) {
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new MyDiffCallback(this.diaries, newDiaries));
            diaries = newDiaries;
            diffResult.dispatchUpdatesTo(this);
        } else {
            diaries = newDiaries;
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
    // diaries has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (diaries != null)
            return diaries.size();
        else return 0;
    }
}
