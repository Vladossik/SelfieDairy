package com.vlada.selfie_app.database;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.annotation.UiThreadTest;
import android.support.test.runner.AndroidJUnit4;

import com.vlada.selfie_app.database.entity.Diary;
import com.vlada.selfie_app.database.entity.ImageSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.*;


@RunWith(AndroidJUnit4.class)
public class RepositoryTest {
    
    Repository repo;
    
    @Before
    public void setupRepo() {
        Context context = InstrumentationRegistry.getTargetContext();
        MyRoomDatabase db = Room.inMemoryDatabaseBuilder(context, MyRoomDatabase.class)
                // Allowing main thread queries, just for testing.
                .allowMainThreadQueries()
                .build();
        
        repo = new Repository(db);
        
    }
    
    
    @After
    public void closeDb() {
        repo.getDatabase().close();
        repo = null;
    }
    
    /**
     * addes amount of hours for dateOfCreate in diary
     */
    void moveDiaryCreation(Diary diary, int amount) {
        diary.getDateOfCreate().add(Calendar.HOUR, amount);
    }
    
    /**
     * addes amount of hours for dateOfCreate in imageSource
     */
    void moveImageCreation(ImageSource imageSource, int amount) {
        imageSource.getDateOfCreate().add(Calendar.HOUR, amount);
    }
    
    /**
     * We insert in current thread avoid using repo and update id
     */
    void insertDiaryAndUpdateId(Diary diary) {
        // insert in this thread not using repo
        int newId = (int) repo.getDatabase().diaryDao().insert(diary);
        diary.setId(newId);
    }
    
    
    @Test
    public void complexTest() throws InterruptedException {
        Diary d1 = new Diary("d1");
        Diary d2 = new Diary("d2");
        Diary d3 = new Diary("d3");
        
        moveDiaryCreation(d2, -1);
        moveDiaryCreation(d3, -2);
        
        repo.insertDiary(d1);
        repo.insertDiary(d2);
        repo.insertDiary(d3);
        
        List<Diary> allDiaries = LiveDataTestUtil.getValue(repo.getAllDiaries());
        assertEquals(3, allDiaries.size());
        
        assertEquals("d1", allDiaries.get(0).getName());
        assertEquals("d2", allDiaries.get(1).getName());
        assertEquals("d3", allDiaries.get(2).getName());
        
        // update ids from received diaries (just replace diary objects)
        d1 = allDiaries.get(0);
        d2 = allDiaries.get(1);
        d3 = allDiaries.get(2);
        
        
        repo.insertImage(
                new ImageSource("src1", d1.getId()),
                new ImageSource("src2", d1.getId()),
                new ImageSource("src1", d2.getId()),
                new ImageSource("src2", d2.getId()),
                new ImageSource("src2", d3.getId()),
                new ImageSource("src3", d3.getId())
        );
        
        List<ImageSource> fromD1 = LiveDataTestUtil.getValue(repo.getAllImagesForDiary(d1.getId()));
        List<ImageSource> fromD2 = LiveDataTestUtil.getValue(repo.getAllImagesForDiary(d2.getId()));
        List<ImageSource> fromD3 = LiveDataTestUtil.getValue(repo.getAllImagesForDiary(d3.getId()));
        
        assertEquals(2, fromD1.size());
        assertEquals(2, fromD2.size());
        assertEquals(2, fromD3.size());
        
        // delete first diary with 2 images and one image in the second diary
        repo.deleteDiary(d1);
        repo.deleteImageByKeys("src2", d2.getId());
        
        fromD1 = LiveDataTestUtil.getValue(repo.getAllImagesForDiary(d1.getId()));
        fromD2 = LiveDataTestUtil.getValue(repo.getAllImagesForDiary(d2.getId()));
        fromD3 = LiveDataTestUtil.getValue(repo.getAllImagesForDiary(d3.getId()));
        
        assertEquals(0, fromD1.size());
        assertEquals(1, fromD2.size());
        assertEquals(2, fromD3.size());
        
    }
    
    @Test
    public void replaceImageSourceIdTest() throws Exception {
        // LiveDataTestUtil.getValue doesn't work in UIThreadTest for some reason!!!
        
        final Diary d1 = new Diary("d1");
        insertDiaryAndUpdateId(d1);
        
        ImageSource img1 = new ImageSource("src1", d1.getId(), "hello1");
        ImageSource img2 = new ImageSource("src2", d1.getId(), "hello2");
        
        repo.insertImage(img1, img2);
        Thread.sleep(100);
        
        List<ImageSource> allImages = repo.getDatabase().imageSourceDao().getImagesForDiary(d1.getId());
        
        assertEquals(2, allImages.size());
        
        // replace img1 source from src1 to src3
        boolean result = repo.replaceImageSourceId(img1, "src3");
        
        assertEquals(true, result);
        
        allImages = repo.getDatabase().imageSourceDao().getImagesForDiary(d1.getId());
        assertEquals(2, allImages.size());
        
        List<ImageSource> filtered = repo.getDatabase().imageSourceDao().findByKeys("src3", d1.getId());
        
        assertEquals(1, filtered.size());
        assertEquals("hello1", filtered.get(0).getDescription());
        
        
        // trying to replace img1 source from src3 to src2
        // should return false and do not replace anything
        
        result = repo.replaceImageSourceId(img1, "src2");
        assertEquals(false, result);
        // nothing should be replaced
        allImages = repo.getDatabase().imageSourceDao().getImagesForDiary(d1.getId());
        assertEquals(2, allImages.size());
        
        filtered = repo.getDatabase().imageSourceDao().findByKeys("src2", d1.getId());
        
        assertEquals(1, filtered.size());
        // description from old image
        assertEquals("hello2", filtered.get(0).getDescription());
    }
}