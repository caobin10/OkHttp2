package org.chuck.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 15-10-23.
 */
public class ImageUtil {
    private static int MAX_WIDTH=0;
    private static int MAX_HEIGHT=0;
    public static Bitmap scaleBitmap(File bmpFile,int desiredWidth, int desiredHeight){
        return scaleBitmap(bmpFile.getPath(),desiredWidth, desiredHeight,null);
    }
    public static Bitmap scaleBitmap(String bmpFilePath){
        return scaleBitmap(bmpFilePath,MAX_WIDTH, MAX_HEIGHT,null);
    }
    public static Bitmap scaleBitmap(String bmpFilePath,int desiredWidth, int desiredHeight,String saveFilePath){
        BitmapFactory.Options options = new BitmapFactory.Options();

        //不分配内存
        options.inJustDecodeBounds=true;
        BitmapFactory.decodeFile(bmpFilePath, options);

        //图片原始宽高
        int origWidth=options.outWidth;
        int origHeight=options.outHeight;


        float scale=getMinScale(origWidth, origHeight, desiredWidth, desiredHeight);

//		options.inSampleSize=Math.round((float)1/scale);
        int realityWidth=(int) (origWidth*scale);
        int realityHeight=(int) (origHeight*scale);

        //设置期望得到的图片的宽高
        options.outWidth = realityWidth;
        options.outHeight = realityHeight;

        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inDither = false;
        //分配内存
        options.inJustDecodeBounds = false;
        Bitmap bitmap=BitmapFactory.decodeFile(bmpFilePath, options);
        bitmap=getScaleBitmap(bitmap, scale);
        if(!CharSeqUtil.isNullOrEmpty(saveFilePath)){
            png2Jpeg(bitmap,saveFilePath);
            recycle(bitmap);
            bitmap=BitmapFactory.decodeFile(saveFilePath);
        }
        return bitmap;
    }




    public static Bitmap getScaleBitmap(Bitmap bitmap,float scale){
        if(scale==1){
            return bitmap;
        }

        Bitmap newBitmap=null;
        try{
            int width=bitmap.getWidth();
            int height=bitmap.getHeight();
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            newBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            recycle(bitmap);
        }
        return newBitmap;
    }



    public static float getMinScale(int origWidth,int origHeight,int desiredWidth, int desiredHeight){
        float widthScale=(float)desiredWidth/(float)origWidth;
        float heightScale=(float)desiredHeight/(float)origHeight;
        return widthScale<heightScale?widthScale:heightScale;
    }



















    public static void png2Jpeg(Bitmap pngBmp,String jpgSaveFilePath){
        FileOutputStream fos = null;
        try {
            File jpgSaveFile=new File(jpgSaveFilePath);
            File saveFileDir=new File(jpgSaveFile.getParent());
            if(!saveFileDir.exists()){
                saveFileDir.mkdirs();
            }

            fos = new FileOutputStream(jpgSaveFile);
            pngBmp.compress(Bitmap.CompressFormat.JPEG, 80, fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally{
            try {
//					fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }







    public void setBitmap(View view,Bitmap bitmap){
        int width=view.getWidth();
        int height=view.getHeight();

    }


    public static void recycle(Bitmap bitmap){
        if(bitmap!=null&&!bitmap.isRecycled()){
            bitmap.recycle();
            bitmap=null;
        }
        System.gc();
    }
}
