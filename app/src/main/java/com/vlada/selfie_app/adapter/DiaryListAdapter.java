package com.vlada.selfie_app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vlada.selfie_app.R;
import com.vlada.selfie_app.database.entity.Diary;

import java.util.List;

public class DiaryListAdapter extends RecyclerView.Adapter<DiaryListAdapter.DiaryViewHolder> {
    
    class DiaryViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final TextView description;
        
        private DiaryViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
        }
    }
    
    private final LayoutInflater mInflater;
    private List<Diary> diaries; // Cached copy of words
    
    public DiaryListAdapter(Context context) { mInflater = LayoutInflater.from(context); }
    
    @Override
    public DiaryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.diary_item, parent, false);
        return new DiaryViewHolder(itemView);
    }
    
    @Override
    public void onBindViewHolder(DiaryViewHolder holder, int position) {
        if (diaries != null) {
            Diary current = diaries.get(position);
            holder.title.setText(current.getName());
            holder.description.setText(current.getDescription());
        } else {
            // Covers the case of data not being ready yet.
            holder.title.setText("No Title");
            holder.description.setText("No description");
        }
    }
    
    public void setDiaries(List<Diary> diary){
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
