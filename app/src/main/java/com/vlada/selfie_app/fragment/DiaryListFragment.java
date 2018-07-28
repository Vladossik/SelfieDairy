package com.vlada.selfie_app.fragment;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vlada.selfie_app.R;
import com.vlada.selfie_app.adapter.DiaryListAdapter;

public class DiaryListFragment extends Fragment {

    View root;
    RecyclerView recyclerView;
    
    private DiaryListAdapter diaryListAdapter;
    
    public DiaryListFragment(){
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_diary_list, container,false);
    
        return root;
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    
    
        recyclerView = root.findViewById(R.id.recyclerView);
//        diaryListAdapter = new DiaryListAdapter(getContext(), viewModel);
        recyclerView.setAdapter(diaryListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        
    }
    
    public DiaryListAdapter getDiaryListAdapter() {
        return diaryListAdapter;
    }
    
    public void setDiaryListAdapter(DiaryListAdapter diaryListAdapter) {
        this.diaryListAdapter = diaryListAdapter;
    }
}
