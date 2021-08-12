package org.chuck.http;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by Administrator on 15-10-21.
 */
public abstract class ResponseListener {
    public void onPostStart(Request request){};
    public abstract void onPostResponse(Response response) ;
    public abstract void onPostFailure(Request request, IOException e);
    public void onPostFinish(){};
}
