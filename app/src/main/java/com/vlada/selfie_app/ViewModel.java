package com.vlada.selfie_app;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.vlada.selfie_app.database.Repository;
import com.vlada.selfie_app.database.entity.Diary;

import java.util.List;


/**
 * A ViewModel holds your app's UI data in a lifecycle-conscious way
 * that survives configuration changes.
 * Separating your app's UI data from your Activity and Fragment classes lets you
 * better follow the single responsibility principle:
 * Your activities and fragments are responsible for drawing data to the screen,
 * while your ViewModel can take care of holding and processing all the data needed for the UI.
 */
public class ViewModel extends AndroidViewModel {
    
    private Repository repository;
    
    public ViewModel(Application application) {
        super(application);
        repository = new Repository(application);
        
    }
    
    public void insert(final Diary diary) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                repository.getDiaryDao().insert(diary);
            }
        });
    }
    
    public LiveData<List<Diary>> getAllDiaries() {
        return repository.getAllDiaries();
    }
    
    public Repository getRepository() {
        return repository;
    }
}
