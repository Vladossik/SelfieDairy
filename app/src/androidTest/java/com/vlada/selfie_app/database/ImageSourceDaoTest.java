package com.vlada.selfie_app.database;

import com.vlada.selfie_app.database.entity.Diary;
import com.vlada.selfie_app.database.entity.ImageSource;

import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertEquals;

public class ImageSourceDaoTest extends DatabaseTest {
    
    @Test
    public void insertImageInDiary() throws Exception {
        Diary diary1 = new Diary("name1");
        Diary diary2 = new Diary("name2");
        
        diary2.setDateOfCreate(calPlusHour(diary1.getDateOfCreate(), -1));
        
        insertDiaryAndUpdateId(diary1);
        insertDiaryAndUpdateId(diary2);
    
        // Insert in the first diary
        
        ImageSource image = new ImageSource("src1", diary1.getId());
        imageSourceDao.insert(image);
        
        // retrieve all images in first diary
        List<ImageSource> imagesInDiary1 = imageSourceDao.getImagesForDiary(diary1.getId());
        assertEquals(imagesInDiary1.size(), 1);
        
        List<ImageSource> imagesInDiary2 = imageSourceDao.getImagesForDiary(diary2.getId());
        assertEquals(imagesInDiary2.size(), 0);
        
        // insert in the second diary
        image = new ImageSource("src1", diary2.getId());
        imageSourceDao.insert(image);
    
    
        imagesInDiary1 = imageSourceDao.getImagesForDiary(diary1.getId());
        assertEquals(imagesInDiary1.size(), 1);
        
        
        // number of images in total should be 2
        
        List<ImageSource> allImages = imageSourceDao.getAllImages();
        
        assertEquals(2, allImages.size());
    }
    
    
    @Test
    public void deleteImageByKeys() throws Exception {
        Diary diary = new Diary("name1");
        
        insertDiaryAndUpdateId(diary);
        
        ImageSource image = new ImageSource("src1", diary.getId());
        imageSourceDao.insert(image);
    
        // retrieve all images in the diary
        List<ImageSource> imagesInDiary = imageSourceDao.getImagesForDiary(diary.getId());
        assertEquals(imagesInDiary.size(), 1);
        
        // delete by keys
        imageSourceDao.deleteByKeys("src1", diary.getId());
    
        // retrieve all images in the diary
        imagesInDiary = imageSourceDao.getImagesForDiary(diary.getId());
        assertEquals(imagesInDiary.size(), 0);
        
        // delete by keys again
        imageSourceDao.deleteByKeys("src1", diary.getId());
    
        // retrieve all images in the diary
        imagesInDiary = imageSourceDao.getImagesForDiary(diary.getId());
        assertEquals(imagesInDiary.size(), 0);
    }
    
    @Test
    public void findImageByKeys() throws Exception {
        Diary diary = new Diary("d1");
        
        insertDiaryAndUpdateId(diary);
        
        // delete by keys
        List<ImageSource> found = imageSourceDao.findByKeys("src1", diary.getId());
        assertEquals(0, found.size());
        
        ImageSource image = new ImageSource("src1", diary.getId());
        imageSourceDao.insert(image);
    
        found = imageSourceDao.findByKeys("src1", diary.getId());
        assertEquals(1, found.size());
    }
    
    
}
