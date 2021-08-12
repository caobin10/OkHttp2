package org.chuck.http;

import org.chuck.downmanager.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Administrator on 16-3-3.
 */
public class DownloadManager {
    private ExecutorService executorService;
    private Map<String,Future> futureMap;
    private Map<String,DownloadTask> taskMap;

    private static DownloadManager instance;
    private DownloadManager(){
        executorService= Executors.newFixedThreadPool(2);
        taskMap=new HashMap<>();
        futureMap=new HashMap<>();
    }

    public static DownloadManager getInstance(){
        if(instance==null){
            synchronized (DownloadManager.class) {
                if(instance==null){
                    instance=new DownloadManager();
                }
            }
        }
        return instance;
    }



    public void addDownloadTask(DownloadTask downloadTask,OnLoadTaskListener loadTaskListener){
        String id=downloadTask.getId();
        if(!taskMap.containsKey(id)){
            taskMap.put(id, downloadTask);
        }else{
            downloadTask=taskMap.get(id);
        }
        if(loadTaskListener!=null){
            downloadTask.setLoadTaskListener(loadTaskListener);
        }
        Future future =  executorService.submit(downloadTask);
        futureMap.put(downloadTask.getId(),future);
    }

    public void removeDownloadTask(DownloadTask downloadTask){
        taskMap.remove(downloadTask.getId());
    }
    public void addDownloadTask(DownloadTask downloadTask){
        addDownloadTask(downloadTask, null);
    }
    public DownloadTask getDownloadTask(DownloadTask downloadTask){
        if(taskMap.containsKey(downloadTask.getId())){
            return taskMap.get(downloadTask.getId());
        }else{
            return  downloadTask;
        }
    }









    public interface DownloadStatus{
        public static final int DOWNLOAD_STATUS_PREPARE = 0;
        public static final int DOWNLOAD_STATUS_START= 1;
        public static final int DOWNLOAD_STATUS_LOADING = 2;
        public static final int DOWNLOAD_STATUS_PAUSE = 3;
        public static final int DOWNLOAD_STATUS_CANCEL = 4;
        public static final int DOWNLOAD_STATUS_ERROR = 5;
        public static final int DOWNLOAD_STATUS_COMPLETED = 6;
    }

    public interface OnLoadTaskListener{
        void onPrepare(DownloadTask task);
        void onStart(DownloadTask task);
//        void onLoading(DownloadTask task);
        void onPause(DownloadTask task);
        void onCancel(DownloadTask task);
        void onError(DownloadTask task, int errCode);
        void onFinish(DownloadTask task);
        int LOAD_IO_EXCEPTION=-1;
    }
}
