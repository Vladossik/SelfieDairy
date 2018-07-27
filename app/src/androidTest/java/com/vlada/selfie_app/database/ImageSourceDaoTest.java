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
    public void testEqualSources() throws Exception {
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
    public void insertWithReplacement() throws Exception {
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
    public void deleteBySrc() throws InterruptedException {
        ImageSource image1 = new ImageSource("src1");
        ImageSource image2 = new ImageSource("src2");
        imageSourceDao.insert(image1);
        imageSourceDao.insert(image2);
        
        List<ImageSource> allImages = LiveDataTestUtil.getValue(imageSourceDao.getAllImages());
        assertEquals(2, allImages.size());
        
        imageSourceDao.deleteBySrc("src1");
        
        allImages = LiveDataTestUtil.getValue(imageSourceDao.getAllImages());
        assertEquals(1, allImages.size());
        assertEquals(image2.getSource(), allImages.get(0).getSource());
        
        
    }
    
    @Test
    public void insertImageInDiary() throws Exception {
        Diary diary1 = new Diary("name1");
        Diary diary2 = new Diary("name2");
        
        diary2.setDateOfCreate(calPlusHour(diary1.getDateOfCreate(), 1));
        
        insertDiaryAndUpdateId(diary1);
        insertDiaryAndUpdateId(diary2);
        
        ImageSource image = new ImageSource("src1");
        imageSourceDao.insert(image);
        
        // Insert in first diary
        diaryImageJoinDao.insert(new DiaryImageJoin(diary1.getId(), image.getSource()));
        
        
        // retrieve all images in first diary
        List<ImageSource> imagesInDiary1 = LiveDataTestUtil.getValue(diaryImageJoinDao.getImagesForDiaries(diary1.getId()));
        
        assertEquals(imagesInDiary1.size(), 1);
        
        List<ImageSource> imagesInDiary2 = LiveDataTestUtil.getValue(diaryImageJoinDao.getImagesForDiaries(diary2.getId()));
        assertEquals(imagesInDiary2.size(), 0);
        
        // insert in second diary
        diaryImageJoinDao.insert(new DiaryImageJoin(diary2.getId(), image.getSource()));
        
        imagesInDiary2 = LiveDataTestUtil.getValue(diaryImageJoinDao.getImagesForDiaries(diary2.getId()));
        assertEquals(imagesInDiary2.size(), 1);
        
        
        // Try to insert connection again
        diaryImageJoinDao.insert(new DiaryImageJoin(diary2.getId(), image.getSource()));
        
        
        imagesInDiary2 = LiveDataTestUtil.getValue(diaryImageJoinDao.getImagesForDiaries(diary2.getId()));
        assertEquals(imagesInDiary2.size(), 1);
    }
    
    
    @Test
    public void deleteImageFromDiary() throws Exception {
        Diary diary = new Diary("name1");
        
        insertDiaryAndUpdateId(diary);
        
        ImageSource image = new ImageSource("src1");
        imageSourceDao.insert(image);
        
        // Insert in diary
        diaryImageJoinDao.insert(new DiaryImageJoin(diary.getId(), image.getSource()));
        
        
        // retrieve all images in diary
        List<ImageSource> imagesInDiary = LiveDataTestUtil.getValue(diaryImageJoinDao.getImagesForDiaries(diary.getId()));
        assertEquals(imagesInDiary.size(), 1);
        
        // delete from diary
        diaryImageJoinDao.delete(new DiaryImageJoin(diary.getId(), image.getSource()));
        
        imagesInDiary = LiveDataTestUtil.getValue(diaryImageJoinDao.getImagesForDiaries(diary.getId()));
        assertEquals(imagesInDiary.size(), 0);
        
        // delete from diary again
        diaryImageJoinDao.delete(new DiaryImageJoin(diary.getId(), image.getSource()));
        
        imagesInDiary = LiveDataTestUtil.getValue(diaryImageJoinDao.getImagesForDiaries(diary.getId()));
        assertEquals(imagesInDiary.size(), 0);
        
    }
}
