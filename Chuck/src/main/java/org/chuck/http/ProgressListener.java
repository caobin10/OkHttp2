package org.chuck.http;

public interface ProgressListener {
	public void postUpdate(long bytesRead, long contentLength, boolean done);
}
