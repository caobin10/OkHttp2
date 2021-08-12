package org.chuck.imgsector;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 16-1-5.
 */
public class SectorUtil {
    public static List<SectorItem> getAllImages(ContentResolver resolver){
        List<SectorItem> sectorItems =new ArrayList<>();
        Cursor cursor = null;
        try{
            //查询图片
            cursor=resolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.ImageColumns.DATA,
                            MediaStore.Images.ImageColumns.DATE_ADDED, MediaStore.Images.ImageColumns.SIZE}, null, null, MediaStore.Images.ImageColumns.DATE_ADDED);
            if(cursor!=null){
                while (cursor.moveToNext()) {
                    SectorItem item=new SectorItem(cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)));
                    sectorItems.add(item);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            if(cursor!=null){
                cursor.close();
            }
        }
        return sectorItems;
    }

    public static List<SectorItem> getAlbumImages(ContentResolver resolver,String albumId){
        List<SectorItem> sectorItems =new ArrayList<>();
        Cursor cursor = null;
        try{
            cursor = resolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[] { MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                            MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.DATE_ADDED, MediaStore.Images.ImageColumns.SIZE },
                    "bucket_id = ?",new String[] { albumId }, MediaStore.Images.ImageColumns.DATE_ADDED);
            if(cursor!=null){
                while (cursor.moveToNext()) {
                    SectorItem item=new SectorItem(cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)));
                    sectorItems.add(item);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(cursor!=null){
                cursor.close();
            }
        }
        return sectorItems;
    }

    public static List<Album> getAlbums(ContentResolver resolver){
        List<Album> albums=new ArrayList<>();
        Cursor cursor=null;
        try{
            cursor = resolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    new String[] { MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, MediaStore.Images.ImageColumns.BUCKET_ID},
                    null, null, null);
            if(cursor!=null){
                Map<String, Album> map = new HashMap<>();
                Album all=new Album("all","全部图片", 0, cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)),true);
                albums.add(all);
                while (cursor.moveToNext()){
                    String id=cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_ID));
                    if (map.containsKey(id)){
                        map.get(id).increaseCount();
                    }else {
                        Album album=new Album(id,cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME)), 1,
                                cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)));
                        map.put(id, album);
                        albums.add(album);
                    }
                    all.increaseCount();
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            if(cursor!=null){
                cursor.close();
            }
        }
        return albums;
    }
}
