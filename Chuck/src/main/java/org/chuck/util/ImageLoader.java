package org.chuck.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.LruCache;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.Request;

import org.chuck.util.AsyncUtil.AsyncTask;

import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.concurrent.Executor;
import java.util.concurrent.Semaphore;

import static org.chuck.util.ImageLoader.*;

/**
 * Created by NTcdshsdK on 2016/1/4.
 */
public class ImageLoader {

    private DisPlayType type;
    private ArrayDeque<AsyncTask<Bitmap>> tasks;
    private ImageCache imageCache;
    private ImageView.ScaleType scaleType;
    private Handler loopHandler;
    private volatile Semaphore poolSemaphore;


    public enum DisPlayType{FIFO,LIFO}
    public ImageLoader(){
        this.imageCache=ImageCacheImpl.getInstance();

        init();
    }

    public void init(){
        type=type==null?DisPlayType.LIFO:type;
        tasks=new ArrayDeque<>();

        Thread loopThread=new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                loopHandler=new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
//                while (true){
                        doTask();
                        Log.i("loopThread", "loopThread");
//                }
                        try
                        {
                            poolSemaphore.acquire();
                        } catch (InterruptedException e)
                        {
                        }
                    }
                };
                Looper.loop();
            }
        });
        loopThread.start();
        poolSemaphore = new Semaphore(5);
    }

    public void loaderImage(final ImageView imageView, final String imageFilePath){
        imageView.setTag(imageFilePath);

        //检测是否需要开启任务(对比Tag检测图片是否需重新加载，需要则创建人物添加到人物队列)
        final Bitmap bitmap=imageCache.getBitmap(imageFilePath);
        if(bitmap!=null){
            imageView.setImageBitmap(bitmap);
        }else {
            addTask(new AsyncTask<Bitmap>() {
                @Override
                public Bitmap doAsync() {
                    Bitmap bitmap=getLocalBitmapSafe(imageView, imageFilePath);
                    if(bitmap!=null){
                        imageCache.putBitmap(imageFilePath,bitmap);
                    }
                    return bitmap;
                }

                @Override
                public void doOnMain(Bitmap bitmap) {
                    imageView.setImageBitmap(bitmap);
                }
            });
        }
    }
    public void loaderImage(final ImageView imageView, final String imageFilePath, final int desiredWidth, final int desiredHeight){
        imageView.setTag(imageFilePath);

        //检测是否需要开启任务(对比Tag检测图片是否需重新加载，需要则创建人物添加到人物队列)
        final Bitmap bitmap=imageCache.getBitmap(imageFilePath);
        if(bitmap!=null){
            imageView.setImageBitmap(bitmap);
        }else {
            addTask(new AsyncTask<Bitmap>() {
                @Override
                public Bitmap doAsync() {
                    Bitmap bitmap=cropLocalBitmapSafe(imageFilePath, desiredWidth, desiredHeight);
                    if(bitmap!=null){
                        imageCache.putBitmap(imageFilePath,bitmap);
                    }
                    return bitmap;
                }

                @Override
                public void doOnMain(Bitmap bitmap) {
                    imageView.setImageBitmap(bitmap);
                }
            });
        }
    }



    public Bitmap cropLocalBitmapSafe(String imageFilePath,final int desiredWidth, final int desiredHeight){
        BitmapFactory.Options options = new BitmapFactory.Options();

        //不分配内存
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageFilePath, options);
        //图片原始宽高
        int origWidth = options.outWidth;
        int origHeight = options.outHeight;
        float widthScale=(float)desiredWidth/(float)origWidth;
        float heightScale=(float)desiredHeight/(float)origHeight;
        float scale=Math.min(widthScale, heightScale);
        options.inSampleSize=Math.round((float)1/scale);

        int realityWidth=(int) (origWidth*scale);
        int realityHeight=(int) (origHeight*scale);
        options.outWidth = desiredWidth;
        options.outHeight = desiredHeight;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inDither = false;

        //分配内存
        options.inJustDecodeBounds = false;
        Bitmap bitmap=BitmapFactory.decodeFile(imageFilePath, options);

        return bitmap;
    }
    public Bitmap getLocalBitmapSafe(ImageView imageView,String imageFilePath){
        DisplayMetrics displayMetrics = imageView.getContext()
                .getResources().getDisplayMetrics();
        ViewGroup.LayoutParams params = imageView.getLayoutParams();

        int width= params.width == ViewGroup.LayoutParams.WRAP_CONTENT ? 0 : imageView.getWidth();
        if(width<=0){width = params.width;}
        if(width<=0){width=getImageFieldValue(imageView,"mMaxWidth");}
        if(width<=0){width=displayMetrics.widthPixels;}

        int height=0;
        if(height<=0){height = params.height;}
        if(height<=0){height=getImageFieldValue(imageView,"mMaxHeight");}
        if(height<=0){height=displayMetrics.heightPixels;}

        return scaleBitmap(imageFilePath,width,height);
    }



//    public ImageContainer getLocal(String imageFilePath, int maxWidth, int maxHeight, ImageView.ScaleType scaleType) {
//
//        // only fulfill requests that were initiated from the main thread.
//        throwIfNotOnMainThread();
//
//        final String cacheKey = getCacheKey(requestUrl, maxWidth, maxHeight);
//
//        // Try to look up the request in the cache of remote images.
//        Bitmap cachedBitmap = mCache.getBitmap(cacheKey);
//        if (cachedBitmap != null) {
//            // Return the cached bitmap.
//            ImageContainer container = new ImageContainer(cachedBitmap, requestUrl, null, null);
//            imageListener.onResponse(container, true);
//            return container;
//        }
//
//        // The bitmap did not exist in the cache, fetch it!
//        ImageContainer imageContainer =
//                new ImageContainer(null, requestUrl, cacheKey, imageListener);
//
//        // Update the caller to let them know that they should use the default bitmap.
//        imageListener.onResponse(imageContainer, true);
//
//        // Check to see if a request is already in-flight.
//        BatchedImageRequest request = mInFlightRequests.get(cacheKey);
//        if (request != null) {
//            // If it is, add this request to the list of listeners.
//            request.addContainer(imageContainer);
//            return imageContainer;
//        }
//
//        // The request is not already in flight. Send the new request to the network and
//        // track it.
//        Request<Bitmap> newRequest = makeImageRequest(requestUrl, maxWidth, maxHeight, scaleType,
//                cacheKey);
//
//        mRequestQueue.add(newRequest);
//        mInFlightRequests.put(cacheKey,
//                new BatchedImageRequest(newRequest, imageContainer));
//        return imageContainer;
//    }

    public void doTask(){
        AsyncTask task=getTask();
        if(task!=null){
            AsyncUtil.getInstance().doTaskThPool(task);
        }
    }
    public synchronized void addTask(AsyncTask<Bitmap> task){
        tasks.add(task);
        poolSemaphore.release();
    }
    public synchronized AsyncTask<Bitmap> getTask(){
        if(type==DisPlayType.FIFO){
            return tasks.pollFirst();
        }else
        if(type==DisPlayType.LIFO){
            return tasks.pollLast();
        }
        return null;
    }
    public void clearTask(){
        tasks.clear();
    }

    private void throwIfNotOnMainThread() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new IllegalStateException("ImageLoader must be invoked from the main thread.");
        }
    }

    public interface ImageCache{
        public Bitmap getBitmap(String key);
        public void putBitmap(String key, Bitmap bitmap);
    }





































    public void display(ImageView imageView,String bmpFilePath){
        DisplayMetrics displayMetrics = imageView.getContext()
                .getResources().getDisplayMetrics();
        ViewGroup.LayoutParams params = imageView.getLayoutParams();

        int width= params.width == ViewGroup.LayoutParams.WRAP_CONTENT ? 0 : imageView.getWidth();
        if(width<=0){width = params.width;}
        if(width<=0){width=getImageFieldValue(imageView,"mMaxWidth");}
        if(width<=0){width=displayMetrics.widthPixels;}

        int height=0;
        if(height<=0){height = params.height;}
        if(height<=0){height=getImageFieldValue(imageView,"mMaxHeight");}
        if(height<=0){height=displayMetrics.heightPixels;}

        imageView.setImageBitmap(scaleBitmap(bmpFilePath,width,height));
    }




    public Bitmap getBitMapSafe(ImageView imageView,String bmpFilePath){
        DisplayMetrics displayMetrics = imageView.getContext()
                .getResources().getDisplayMetrics();
        ViewGroup.LayoutParams params = imageView.getLayoutParams();

        int width= params.width == ViewGroup.LayoutParams.WRAP_CONTENT ? 0 : imageView.getWidth();
        if(width<=0){width = params.width;}
        if(width<=0){width=getImageFieldValue(imageView,"mMaxWidth");}
        if(width<=0){width=displayMetrics.widthPixels;}

        int height=0;
        if(height<=0){height = params.height;}
        if(height<=0){height=getImageFieldValue(imageView,"mMaxHeight");}
        if(height<=0){height=displayMetrics.heightPixels;}

        return scaleBitmap(bmpFilePath,width,height);
    }





    public Bitmap scaleBitmap(String bmpFilePath,int desiredWidth, int desiredHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();

        //不分配内存
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(bmpFilePath, options);
        //图片原始宽高
        int origWidth = options.outWidth;
        int origHeight = options.outHeight;
        float widthScale=(float)desiredWidth/(float)origWidth;
        float heightScale=(float)desiredHeight/(float)origHeight;
        float scale=Math.max(widthScale, heightScale);
        int realityWidth=(int) (origWidth*scale);
        int realityHeight=(int) (origHeight*scale);
        options.inSampleSize=Math.round((float)1/scale);
        options.outWidth = realityWidth;
        options.outHeight = realityHeight;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inDither = false;

        //分配内存
        options.inJustDecodeBounds = false;
        Bitmap bitmap=BitmapFactory.decodeFile(bmpFilePath, options);

        return bitmap;
    }


    public static int getImageFieldValue(ImageView imageView,String fieldName){
        int value = 0;
        try {
            Field field = ImageView.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            int fieldValue = (Integer) field.get(imageView);
            if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE) {
                value = fieldValue;
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return value;
    }
}

