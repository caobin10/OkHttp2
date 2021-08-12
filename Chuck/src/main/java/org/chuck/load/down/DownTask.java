package org.chuck.load.down;

import android.util.Log;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.chuck.http.HttpResponseListener;
import org.chuck.http.OkHttpUtil;
import org.chuck.http.ProgressListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

/**
 * Created by Administrator on 16-3-18.
 */
public class DownTask implements Runnable {
    private DownListener downListener;
    private boolean isPause=false;
    private boolean isCancel=false;

    private DownEntity downEntity;
    @Override
    public void run() {
        if(downEntity==null){
            throw new IllegalArgumentException("downEntity is null");
        }
        Log.i("downEntity.getLoadedLen",downEntity.getLoadedLen()+"");
        Request request = new Request.Builder()
                .url(downEntity.getUrl())
                .header("RANGE", "bytes=" + downEntity.getLoadedLen() + "-").build();
        OkHttpUtil.getInstance().doAsyncGetLoadRefresh(request, new ProgressListener() {
            @Override
            public void postUpdate(long bytesRead, long contentLength, boolean done) {
                downListener.onLoading(bytesRead, contentLength,done);
            }
        }, new HttpResponseListener<Long>() {
            @Override
            public void onPostStart(Request request) {
                super.onPostStart(request);
                if (downListener != null) {
                    downListener.onStart();
                }
            }

            @Override
            public Long onSuccess(Response response) throws IOException {
                long count = 0;
                try {
                    long contentLen = response.body().contentLength();
                    if(downEntity.getContentLen()<=0){
                        downEntity.setContentLen(contentLen);
                    }
                    RandomAccessFile file = new RandomAccessFile(downEntity.getSavePath(),"rwd");
                    file.seek(downEntity.getLoadedLen());

                    InputStream is = response.body().byteStream();
                    byte[] buf = new byte[1024 * 2];
                    int readLen ;
                    while ((readLen = is.read(buf)) > 0) {
                        if(isPause){
                            downEntity.setLoadedLen(count);
                            break;
                        }
                        file.write(buf, 0, readLen);
                        count += readLen;
                    }
                    is.close();
                    file.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return count;
            }

            @Override
            public void onPostSuccess(Long loadedLen) {
                if (downListener != null) {
                    if(isPause){
                        downListener.onPause();
                    }else{
                        downListener.onSuccess();
                    }
                }
            }

            @Override
            public void onPostFailure(Request request, int statusCode) {
                if (downListener != null) {
                    downListener.onFailure();
                }
            }

            @Override
            public void onPostError(Request request, IOException e) {
                if (downListener != null) {
                    downListener.onFailure();
                }
            }
        });
    }

    public DownListener getDownListener() {
        return downListener;
    }

    public void setDownListener(DownListener downListener) {
        this.downListener = downListener;
    }

    public boolean isPause() {
        return isPause;
    }

    public void pause() {
        isPause = true;
        if(downListener!=null){
            downListener.onPause();
        }
    }

    public boolean isCancel() {
        return isCancel;
    }

    public void cancel() {
        isCancel=true;
        if(downListener!=null){
            downListener.onCancel();
        }
    }


    public void continueLoad() {
        isPause=false;
    }

    public DownEntity getDownEntity() {
        return downEntity;
    }

    public void setDownEntity(DownEntity downEntity) {
        this.downEntity = downEntity;
    }

    public interface DownListener{
        void onStart();
        void onLoading(long bytesRead, long contentLength,boolean done);
        void onPause();
        void onCancel();
        void onFailure();
        void onSuccess();
    }



}
