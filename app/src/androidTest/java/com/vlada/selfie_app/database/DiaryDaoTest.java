package com.vlada.selfie_app.database;

import com.vlada.selfie_app.database.entity.Diary;
import com.vlada.selfie_app.enums.RemindFrequency;

import org.junit.Test;

import java.util.Calendar;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertTrue;

public class DiaryDaoTest extends DatabaseTest {
    
    // before and after have already been defined in super class
    
    
    @Test
    public void insertAndGetDiary() throws Exception {
        
        Diary diary = new Diary("name1", "description1", Calendar.getInstance(), RemindFrequency.Weekly);
        
        diaryDao.insert(diary);
        
        List<Diary> allDiaries = LiveDataTestUtil.getValue(diaryDao.getAllDiariesLive());
        
        assertEquals(allDiaries.get(0).getName(), diary.getName());
        assertEquals(allDiaries.get(0).getReminder(), diary.getReminder());
        assertEquals(allDiaries.get(0).getRemindFrequency(), diary.getRemindFrequency());
        assertEquals(allDiaries.get(0).getReminder(), diary.getReminder());
    }
    
    
    /**
     * Checks adding two diaries and reading them in right order
     */
    @Test
    public void getAllDiaries() throws Exception {
        Diary diary1 = new Diary("name1");
        Diary diary2 = new Diary("name2");
        
        // inverse date of create for two diaries
        
        Calendar dateOfCreate = Calendar.getInstance();
        
        diary1.setDateOfCreate(dateOfCreate);
        
        // reCreate calendar to unbind from previous link
        diary2.setDateOfCreate(calPlusHour(dateOfCreate, 1));
        
        assertTrue("diary1 dateOfCreate in millis should be less then diary2 dateOfCreate in millis",
                Converters.calendarToMillis(diary1.getDateOfCreate())
                        < Converters.calendarToMillis(diary2.getDateOfCreate()));
        
        
        diaryDao.insert(diary1);
        diaryDao.insert(diary2);
        
        
        // check sorting in dateOfCreate order:
        List<Diary> allDiaries = LiveDataTestUtil.getValue(diaryDao.getAllDiariesLive());
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
        
        
        List<Diary> allDiaries = LiveDataTestUtil.getValue(diaryDao.getAllDiariesLive());
        assertEquals(allDiaries.size(), 3);
        
    }
    
    
    @Test
    public void deleteAll() throws Exception {
        Diary diary1 = new Diary("name1", "description1", Calendar.getInstance(), RemindFrequency.Weekly);
        Diary diary2 = new Diary("name2", "description2", Calendar.getInstance(), RemindFrequency.Daily);
        
        diaryDao.insert(diary1);
        diaryDao.insert(diary2);
        
        diaryDao.deleteAll();
        List<Diary> allDiaries = LiveDataTestUtil.getValue(diaryDao.getAllDiariesLive());
        assertTrue(allDiaries.isEmpty());
    }
    
    @Test
    public void testIdGeneration() throws InterruptedException {
        Diary diary1 = new Diary("name1");
        Diary diary2 = new Diary("name2");
        Diary diary3 = new Diary("name2");
    
        // 0 means unset id
        assertEquals(0, diary1.getId());
    
        insertDiaryAndUpdateId(diary1);
        
        assertNotSame(0, diary1.getId());
        
        insertDiaryAndUpdateId(diary2);
        insertDiaryAndUpdateId(diary3);
        
        assertNotSame(diary1.getId(), diary2.getId());
        
//        List<Diary> allDiaries = LiveDataTestUtil.getValue(diaryDao.getAllDiariesLive());
    }
    
    
    @Test
    public void deleteOneById() throws Exception {
        Diary diary1 = new Diary("name1");
        Diary diary2 = new Diary("name2");
        
        
        insertDiaryAndUpdateId(diary1);
        insertDiaryAndUpdateId(diary2);
        
        List<Diary> allDiaries = LiveDataTestUtil.getValue(diaryDao.getAllDiariesLive());
        assertEquals(2, allDiaries.size());
        
        diaryDao.deleteById(diary1.getId());
        
        allDiaries = LiveDataTestUtil.getValue(diaryDao.getAllDiariesLive());
        assertEquals(1, allDiaries.size());
    }
    
    @Test(expected = Exception.class)
    public void insertExisting() throws Exception {
        Diary diary1 = new Diary("name1");
        
        
        
        insertDiaryAndUpdateId(diary1);
        insertDiaryAndUpdateId(diary1);
    }
    
    @Test
    public void update() throws Exception {
        Diary diary1 = new Diary("name1");
    
        insertDiaryAndUpdateId(diary1);
        
        diary1.setName("hello");
        
        diaryDao.update(diary1);
        
        List<Diary> allDiaries = LiveDataTestUtil.getValue(diaryDao.getAllDiariesLive());
        
        assertEquals(1, allDiaries.size());
        assertEquals("hello", allDiaries.get(0).getName());
    }
    
    @Test
    public void updateNotExisting() throws Exception {
        Diary diary1 = new Diary("name1");
        
        // Nothing should happen
        diaryDao.update(diary1);
        
        List<Diary> allDiaries = LiveDataTestUtil.getValue(diaryDao.getAllDiariesLive());
        assertEquals(0, allDiaries.size());
    }
    
    
}
