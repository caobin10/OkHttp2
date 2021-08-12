package org.chuck.http;

import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import org.chuck.util.JsonUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Callable;

/**
 * Created by Administrator on 16-3-3.
 */
public class DownloadTask implements Runnable {
    private DownloadEntity downloadEntity;
    private DownloadManager.OnLoadTaskListener loadTaskListener;
    private ProgressListener progressListener;
    private int downloadStatus= DownloadManager.DownloadStatus.DOWNLOAD_STATUS_PREPARE;
    @Override
    public void run() {
        loadTaskListener.onPrepare(this);
        loadTaskListener.onStart(this);
        Log.i("downloadEntity",JsonUtil.toJson(downloadEntity));
        if(downloadStatus==DownloadManager.DownloadStatus.DOWNLOAD_STATUS_COMPLETED){
            loadTaskListener.onFinish(this);
            return;
        }
        Request request = new Request.Builder().url(downloadEntity.getUrl())
                .header("RANGE", "bytes=" + downloadEntity.getLoadedLen() + "-").build();
        OkHttpUtil.getInstance().doGetLoadRefresh(request, progressListener, new HttpResponseListener<Object>() {
            @Override
            public Object onSuccess(Response response) throws IOException {
                ResponseBody responseBody = response.body();
                long contentLength = responseBody.contentLength();
                Log.i("contentLength",contentLength+"contentLength");
                if(downloadEntity.getContentLen()==0){downloadEntity.setContentLen(contentLength);}
                InputStream inputStream = responseBody.byteStream();
                FileOutputStream fos = new FileOutputStream(new File(downloadEntity.getSavePath()));

                int count=0;
                int readCount = 0;
                byte[] buf = new byte[1024 * 2];
                while ((readCount = inputStream.read(buf)) >0
                        && (downloadStatus != DownloadManager.DownloadStatus.DOWNLOAD_STATUS_PAUSE
                        && downloadStatus != DownloadManager.DownloadStatus.DOWNLOAD_STATUS_CANCEL)) {
                    count += readCount;
                    fos.write(buf, 0, readCount);
                }
                inputStream.close();
                fos.close();

                downloadEntity.setLoadedLen(downloadEntity.getLoadedLen() + count);//重设已下载字节长度
                if (downloadEntity.getLoadedLen() ==contentLength ) {
                    downloadEntity.setDownloadStatus(DownloadManager.DownloadStatus.DOWNLOAD_STATUS_COMPLETED);
                }
                return null;
            }

            @Override
            public void onPostSuccess(Object o) {
                loadTaskListener.onFinish(DownloadTask.this);
            }

            @Override
            public void onPostFailure(Request request, int statusCode) {
                loadTaskListener.onError(DownloadTask.this, statusCode);
            }

            @Override
            public void onPostError(Request request, IOException e) {
                loadTaskListener.onError(DownloadTask.this, DownloadManager.OnLoadTaskListener.LOAD_IO_EXCEPTION);
            }
        });
    }

    public DownloadEntity getDownloadEntity(){
        return downloadEntity;
    }
    public void setDownloadEntity(DownloadEntity downloadEntity){
        this.downloadEntity=downloadEntity;
        downloadStatus=downloadEntity.getDownloadStatus();
    }

    public String getId(){
        return downloadEntity.getId();
    }
    public int getDownloadStatus(){
        return  downloadStatus;
    }

    public ProgressListener getProgressListener() {
        return progressListener;
    }

    public void setProgressListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    public DownloadManager.OnLoadTaskListener getLoadTaskListener() {
        return loadTaskListener;
    }

    public void setLoadTaskListener(DownloadManager.OnLoadTaskListener loadTaskListener) {
        this.loadTaskListener = loadTaskListener;
    }


    public void cancel(){
        downloadStatus= DownloadManager.DownloadStatus.DOWNLOAD_STATUS_CANCEL;
        File file=new File(downloadEntity.getSavePath());
        if(file.exists()){
            file.delete();
        }
        downloadEntity.setDownloadStatus(DownloadManager.DownloadStatus.DOWNLOAD_STATUS_PREPARE);
        if(loadTaskListener!=null){
            loadTaskListener.onCancel(this);
        }
    }
    public void pause(){
        downloadStatus= DownloadManager.DownloadStatus.DOWNLOAD_STATUS_PAUSE;
        downloadEntity.setDownloadStatus(downloadStatus);
        if(loadTaskListener!=null){
            loadTaskListener.onPause(this);
        }
    }
    public void loading(){
        downloadStatus= DownloadManager.DownloadStatus.DOWNLOAD_STATUS_LOADING;
        downloadEntity.setDownloadStatus(downloadStatus);
        if(loadTaskListener!=null){
//            loadTaskListener.onPause(this);
        }
    }

//    @Override
//    public DownloadEntity call() throws Exception {
//        return null;
//    }

    public interface DownloadEntity{
        String getId();
        String getUrl();
        long getLoadedLen();
        void setLoadedLen(long loadedLen);
        long getContentLen();
        void setContentLen(long contentLen);
        String getSavePath();
        int getDownloadStatus();
        void setDownloadStatus(int downloadStatus);
    }
}
