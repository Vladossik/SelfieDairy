package com.vlada.selfie_app.database;

import com.vlada.selfie_app.database.entity.Diary;
import com.vlada.selfie_app.database.entity.RemindFrequency;

import org.junit.Test;

import java.util.Calendar;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class DiaryDaoTest extends DatabaseTest {

    // before and after have already been defined in super class
    
    
    @Test
    public void insertAndGetDiary() throws Exception {
        
        Diary diary = new Diary("name1", "description1", Calendar.getInstance(), RemindFrequency.Weekly);
        
        diaryDao.insert(diary);
        
        List<Diary> allDiaries = LiveDataTestUtil.getValue(diaryDao.getAllDiaries());
        
        assertEquals(allDiaries.get(0).getName(), diary.getName());
        assertEquals(allDiaries.get(0).getReminder(), diary.getReminder());
        assertEquals(allDiaries.get(0).getRemindFrequency(), diary.getRemindFrequency());
        assertEquals(allDiaries.get(0).getReminder(), diary.getReminder());
    }
    
    
    /** Checks adding two diaries and reading them in right order*/
    @Test
    public void getAllDiaries() throws Exception {
        Diary diary1 = new Diary("name1");
        Diary diary2 = new Diary("name2");
        
        // inverse date of create for two diaries
        
        Calendar dateOfCreate = Calendar.getInstance();
        
        diary1.setDateOfCreate(dateOfCreate);
        
        // reCreate calendar to unbind from previous link
        diary2.setDateOfCreate(calPlusHour(dateOfCreate, -1));
        
        assertTrue("diary1 dateOfCreate in millis should be more then diary2 dateOfCreate in millis",
                Converters.calendarToMillis(diary1.getDateOfCreate())
                        > Converters.calendarToMillis(diary2.getDateOfCreate()));
        
        
        diaryDao.insert(diary1);
        diaryDao.insert(diary2);
        
        
        // check sorting in dateOfCreate order:
        List<Diary> allDiaries = LiveDataTestUtil.getValue(diaryDao.getAllDiaries());
        assertEquals(allDiaries.get(0).getName(), diary2.getName());
        assertEquals(allDiaries.get(1).getName(), diary1.getName());
    }
    
    
    @Test
    public void diariesWithEqualNames() throws InterruptedException {
        Diary diary1 = new Diary("name1");
        Diary diary2 = new Diary("name2");
        Diary diary3 = new Diary("name2");
        
        diaryDao.insert(diary1);
        diaryDao.insert(diary2);
        diaryDao.insert(diary3);
    
    
        // check sorting in dateOfCreate order:
        List<Diary> allDiaries = LiveDataTestUtil.getValue(diaryDao.getAllDiaries());
        assertEquals(allDiaries.size(), 3);
        
    }
    
    
    /** Checks searching diaries by name*/
    @Test
    public void getByNameTest() throws Exception {
        Diary diary1 = new Diary("name1");
        Diary diary2 = new Diary("name2");
        
        diaryDao.insert(diary1);
        diaryDao.insert(diary2);
        
        assertEquals(diaryDao.getByName("abc"), null);
        assertEquals(diaryDao.getByName("name1").getName(), "name1");
        assertEquals(diaryDao.getByName("name2").getName(), "name2");
        
    }
    
    
    
    
    @Test
    public void deleteAll() throws Exception {
        Diary diary1 = new Diary("name1", "description1", Calendar.getInstance(), RemindFrequency.Weekly);
        Diary diary2 = new Diary("name2", "description2", Calendar.getInstance(), RemindFrequency.Daily);
        
        diaryDao.insert(diary1);
        diaryDao.insert(diary2);
        
        diaryDao.deleteAll();
        List<Diary> allDiaries = LiveDataTestUtil.getValue(diaryDao.getAllDiaries());
        assertTrue(allDiaries.isEmpty());
    }
}
