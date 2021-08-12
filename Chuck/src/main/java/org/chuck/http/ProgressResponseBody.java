package org.chuck.http;

import java.io.IOException;

import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.ResponseBody;

public class ProgressResponseBody extends ResponseBody{
	private ResponseBody responseBody;
    private ProgressCallback progressCallback;
    private BufferedSource bufferedSource;

    public ProgressResponseBody(ResponseBody responseBody, ProgressCallback progressCallback) {
      this.responseBody = responseBody;
      this.progressCallback = progressCallback;
    }

    @Override
    public MediaType contentType() {
      return responseBody.contentType();
    }

    @Override 
    public long contentLength() throws IOException {
      return responseBody.contentLength();
    }

    @Override 
    public BufferedSource source() throws IOException {
      if (bufferedSource == null) {
        bufferedSource = Okio.buffer(source(responseBody.source()));
      }
      return bufferedSource;
    }

    private Source source(Source source) {
      return new ForwardingSource(source) {
        long totalBytesRead = 0L;
        @Override 
        public long read(Buffer sink, long byteCount) throws IOException {
          long bytesRead = super.read(sink, byteCount);
          // read() returns the number of bytes read, or -1 if this source is exhausted.
          totalBytesRead += bytesRead != -1 ? bytesRead : 0;
          progressCallback.update(totalBytesRead, responseBody.contentLength(), bytesRead == -1);
          return bytesRead;
        }
      };
    }

}
