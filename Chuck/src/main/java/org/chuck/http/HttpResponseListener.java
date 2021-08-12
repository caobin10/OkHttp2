package org.chuck.http;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by Administrator on 15-10-21.
 */
public abstract class HttpResponseListener<Result> {
    public void onPostStart(Request request){};
    public abstract Result onSuccess(Response response) throws IOException;
    public abstract void onPostSuccess(Result result) ;
    public abstract void onPostFailure(Request request, int statusCode);
    public abstract void onPostError(Request request, IOException e);
}
