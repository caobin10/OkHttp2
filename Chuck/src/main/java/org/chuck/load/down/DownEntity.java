package org.chuck.load.down;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Administrator on 16-3-18.
 */
public abstract class DownEntity {
    private String url;
    private long contentLen;
    private long loadedLen;
    private String savePath;

    public DownEntity(){

    }
    public DownEntity(@NonNull String url,@NonNull String savePath,long contentLen,long loadedLen){
        this.url=url;
        this.contentLen=contentLen;
        this.loadedLen=loadedLen;
        this.savePath=savePath;
    }
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public long getLoadedLen() {
        return loadedLen;
    }

    public void setLoadedLen(long loadedLen) {
        this.loadedLen = loadedLen;
    }

    public long getContentLen() {
        return contentLen;
    }

    public void setContentLen(long contentLen) {
        this.contentLen = contentLen;
    }
}
