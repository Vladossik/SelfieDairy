package com.vlada.selfie_app.database;

import com.vlada.selfie_app.database.entity.Diary;
import com.vlada.selfie_app.database.entity.DiaryImageJoin;
import com.vlada.selfie_app.database.entity.ImageSource;

import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

public class ImageSourceDaoTest extends DatabaseTest {
    
    
    @Test
    public void insertAndGetImage() throws Exception {
        
        ImageSource image = new ImageSource("src1");
        
        imageSourceDao.insert(image);
        
        
        List<ImageSource> allImages = LiveDataTestUtil.getValue(imageSourceDao.getAllImages());
        
        assertEquals(allImages.get(0).getSource(), image.getSource());
    }
    
    @Test
    public void testEqualSources() throws Exception{
        ImageSource image1 = new ImageSource("src1", "hello1");
        ImageSource image2 = new ImageSource("src2", "hello2");
        ImageSource image3 = new ImageSource("src2", "hello3");
        
        
        imageSourceDao.insert(image1);
        imageSourceDao.insert(image2);
        imageSourceDao.insert(image3);
        
        List<ImageSource> allImages = LiveDataTestUtil.getValue(imageSourceDao.getAllImages());
        assertEquals("images should be unique", allImages.size(), 2);
    
        for (ImageSource img : allImages) {
            if (img.getDescription().equals("hello3"))
                fail("new image with the same source shouldn't replace old");
        }
        
    }
    
    @Test
    public void testInsertWithReplacement() throws Exception{
        ImageSource image1 = new ImageSource("src1", "hello1");
        ImageSource image2 = new ImageSource("src2", "hello2");
        ImageSource image3 = new ImageSource("src2", "hello3");
        
        
        imageSourceDao.insertOrReplace(image1);
        imageSourceDao.insertOrReplace(image2);
        imageSourceDao.insertOrReplace(image3);
        
        List<ImageSource> allImages = LiveDataTestUtil.getValue(imageSourceDao.getAllImages());
        assertEquals("images should be unique", allImages.size(), 2);
    
        for (ImageSource img : allImages) {
            if (img.getDescription().equals("hello2"))
                fail("new image with the same source should replace old");
        }
        
    }
    
    
    @Test
    public void insertImageInDiary() throws Exception {
        Diary diary1 = new Diary("name1");
        Diary diary2 = new Diary("name2");
        
        diary2.setDateOfCreate(calPlusHour(diary1.getDateOfCreate(), 1));
        
        diaryDao.insert(diary1);
        diaryDao.insert(diary2);
    
        
        // UPDATE diary objects to get valid generated id!!!
        
        // May be unstable for 
        diary1 = diaryDao.getByName(diary1.getName());
        diary2 = diaryDao.getByName(diary2.getName());
        
        
        ImageSource image = new ImageSource("src1");
        imageSourceDao.insert(image);
        
        DiaryImageJoin diaryImageJoin = new DiaryImageJoin(diary1.getId(), image.getSource());
    
        
        
        diaryImageJoinDao.insert(diaryImageJoin);
        
        
        // retreive all images in first diary
        List<ImageSource> imagesInDiary1 = LiveDataTestUtil.getValue(diaryImageJoinDao.getImagesForDiaries(diary1.getId()));
    
        assertEquals(imagesInDiary1.size(), 1);
        
        List<ImageSource> imagesInDiary2 = LiveDataTestUtil.getValue(diaryImageJoinDao.getImagesForDiaries(diary2.getId()));
        assertEquals(imagesInDiary2.size(), 0);
        
    }
}
