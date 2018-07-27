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
    
    private Repository repo;
    
    public ViewModel(Application application) {
        super(application);
        repo = new Repository(application);
        
    }
    
    
    public LiveData<List<Diary>> getAllDiaries() {
        return repo.getAllDiaries();
    }
    
    public Repository getRepo() {
        return repo;
    }
}
