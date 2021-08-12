package org.chuck.util;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by Administrator on 15-12-16.
 */
public class AsyncUtil {
    protected Handler handler;
    private static AsyncUtil instance;

    protected AsyncUtil(){
        handler=new Handler(Looper.getMainLooper());
    }

    public static AsyncUtil getInstance(){
        if(instance==null){
            synchronized (AsyncUtil.class) {
                if(instance==null){
                    instance=new AsyncUtil();
                }
            }
        }
        return instance;
    }

    public <T> void doTaskThPool(final AsyncTask<T> asyncTask){
        ThreadPool.getExecutor().execute(new Runnable() {
            @Override
            public void run() {
                final T t=asyncTask.doAsync();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        asyncTask.doOnMain(t);
                    }
                });
            }
        });
    }

    public interface AsyncTask<T>{
        public T doAsync();
        public void doOnMain(T t);
    }
}
