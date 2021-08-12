package org.chuck.http;

/**
 * Created by Administrator on 15-10-21.
 */
public interface ProgressCallback {
    public void update(long bytesRead, long contentLength, boolean done);
}
