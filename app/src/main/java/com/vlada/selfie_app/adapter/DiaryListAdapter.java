package com.vlada.selfie_app.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.vlada.selfie_app.R;
import com.vlada.selfie_app.ViewModel;
import com.vlada.selfie_app.database.entity.Diary;

import java.util.List;

public class DiaryListAdapter extends RecyclerView.Adapter<DiaryListAdapter.DiaryViewHolder> {
    
    public interface OnItemClickListener {
        void onItemClick(Diary item);
    }
    
    class DiaryViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final TextView description;
        /** Root view for item. Usually it is LinearLayout*/
        private final View root;
        
        private DiaryViewHolder(View root) {
            super(root);
            this.root = root;
            title = root.findViewById(R.id.title);
            description = root.findViewById(R.id.description);
        }
    }
    
    private final LayoutInflater mInflater;
    private ViewModel viewModel;
    private List<Diary> diaries; // Cached copy of words
    
    public DiaryListAdapter(Context context, ViewModel viewModel) {
        mInflater = LayoutInflater.from(context);
        this.viewModel = viewModel;
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
            Diary current = diaries.get(position);
            holder.title.setText(current.getName());
            holder.description.setText(current.getDescription());
        } else {
            // Covers the case of data not being ready yet.
            holder.title.setText("No Title");
            holder.description.setText("No description");
        }
        
        holder.root.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(v.getContext(), "Deleting diary on position: " + position, Toast.LENGTH_SHORT).show();
                
                if (diaries != null) {
                    viewModel.getRepo().deleteDiary(diaries.get(position));
                }
                
                return true;
            }
        });
        
    }
    
    public void setDiaries(List<Diary> diary) {
        diaries = diary;
        notifyDataSetChanged();
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
