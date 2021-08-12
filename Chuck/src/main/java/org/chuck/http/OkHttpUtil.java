package org.chuck.http;

import android.os.Handler;
import android.os.Looper;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Created by Administrator on 15-10-21.
 */
public class OkHttpUtil {
    public static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    public static final MediaType MEDIA_TYPE_JSON= MediaType.parse("application/json; charset=utf-8");
    public static final MediaType MEDIA_TYPE_STREAM= MediaType.parse("application/octet-stream");
    private static OkHttpUtil instance;
    protected OkHttpClient httpClient;
    protected Handler handler;

    protected OkHttpUtil(){
        httpClient=new OkHttpClient();
        handler=new Handler(Looper.getMainLooper());
    }
    public static OkHttpUtil getInstance(){
        if(instance==null){
            synchronized (OkHttpUtil.class) {
                if(instance==null){
                    instance=new OkHttpUtil();
                }
            }
        }
        return instance;
    }


//    private static class OkHttpFactory{
//        private static OkHttpUtil instance=new OkHttpUtil();
//    }
//    public static OkHttpUtil getInstance(){
//        return OkHttpFactory.instance;
//    }
//    private Object readResolve() throws ObjectStreamException {
//        return getInstance();
//    }

    private Request buildGetRequest(String url){
        return new Request.Builder().url(url).build();
    }
    private Request buildPostRequest(String url,Map<String, String> args){
        FormEncodingBuilder builder=new FormEncodingBuilder();
        if(args!=null){
            for(Entry<String, String> entry:args.entrySet()){
                builder.add(entry.getKey(), entry.getValue());
            }
        }
        RequestBody requestBody=builder.build();
        return new Request.Builder().url(url).post(requestBody).build();
    }

    private Request buildPostMultipartRequest(String url,Map<String, String> args,Map<String, File> files){
        MultipartBuilder builder=new MultipartBuilder().type(MultipartBuilder.FORM);
        if(args!=null){
            for (Entry<String, String> entry:args.entrySet()) {
                builder.addFormDataPart(entry.getKey(), entry.getValue());
            }
        }
        if(files!=null){
            for(Entry<String, File> entry:files.entrySet()){
                File file=entry.getValue();
                builder.addFormDataPart(entry.getKey(), file.getName(), RequestBody.create(MEDIA_TYPE_STREAM, file));
            }
        }
        RequestBody requestBody=builder.build();
        return new Request.Builder().url(url).post(requestBody).build();
    }


    public Response doGet(String url) throws IOException{
        return httpClient.newCall(buildGetRequest(url)).execute();
    }
    public void doGetAsync(String url,Callback callback){
        httpClient.newCall(buildGetRequest(url)).enqueue(callback);
    }


    public Response doPost(String url,Map<String, String> args) throws IOException{
        return httpClient.newCall(buildPostRequest(url,args)).execute();
    }
    public void doPostAsync(String url,Map<String, String> args,Callback callback){
        httpClient.newCall(buildPostRequest(url,args)).enqueue(callback);
    }

    public Response doPost(String url,Map<String, String> args,Map<String, File> files) throws IOException {
        return httpClient.newCall(buildPostMultipartRequest(url,args,files)).execute();
    }

    public void doPostAsync(String url,Map<String, String> args,Map<String, File> files,Callback callback){
        httpClient.newCall(buildPostMultipartRequest(url,args,files)).enqueue(callback);
    }



    public void doGetAsyncRefresh(String url, final ResponseListener listener)  {
        final Request request=buildGetRequest(url);
        listener.onPostStart(request);
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Request request, final IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onPostFailure(request, e);
                        listener.onPostFinish();
                    }
                });
            }
            @Override
            public void onResponse(final Response response) throws IOException {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onPostResponse(response);
                        listener.onPostFinish();
                    }
                });
            }
        });
    }
    public void doPostAsyncRefresh(String url,Map<String, String> args, final ResponseListener listener)  {
        Request request=buildPostRequest(url, args);
        listener.onPostStart(request);
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Request request, final IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onPostFailure(request, e);
                        listener.onPostFinish();
                    }
                });
            }

            @Override
            public void onResponse(final Response response) throws IOException {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onPostResponse(response);
                        listener.onPostFinish();
                    }
                });
            }
        });
    }



    public <Result> void doGetLoadRefresh(String url, final ProgressListener progressListener, final HttpResponseListener<Result> listener){
        final Request request=buildGetRequest(url);
        handler.post(new Runnable() {
            @Override
            public void run() {
                listener.onPostStart(request);
            }
        });
        final OkHttpClient cloneClient=httpClient.clone();
        cloneClient.networkInterceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response originalResponse = chain.proceed(chain.request());
                return originalResponse.newBuilder()
                        .body(new ProgressResponseBody(originalResponse.body(), new ProgressCallback() {
                            @Override
                            public void update(final long bytesRead, final long contentLength, final boolean done) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressListener.postUpdate(bytesRead, contentLength, done);
                                    }
                                });
                            }
                        }))
                        .build();
            }
        });
        try{
            final Response response=cloneClient.newCall(request).execute();
            final int statusCode=response.code();
            switch(statusCode){
                case 200:
                    final Result result=listener.onSuccess(response);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onPostSuccess(result);
                        }
                    });
                    break;
                default:
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onPostFailure(response.request(), statusCode);
                        }
                    });
                    break;
            }
        }catch (final IOException e){
            e.printStackTrace();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onPostError(request, e);
                }
            });
        }
    }

    public <Result> void doGetLoadRefresh(final Request request, final ProgressListener progressListener, final HttpResponseListener<Result> listener){
        handler.post(new Runnable() {
            @Override
            public void run() {
                listener.onPostStart(request);
            }
        });
        final OkHttpClient cloneClient=httpClient.clone();
        if(progressListener!=null){
            cloneClient.networkInterceptors().add(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Response originalResponse = chain.proceed(chain.request());
                    return originalResponse.newBuilder()
                            .body(new ProgressResponseBody(originalResponse.body(), new ProgressCallback() {
                                @Override
                                public void update(final long bytesRead, final long contentLength, final boolean done) {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressListener.postUpdate(bytesRead, contentLength, done);
                                        }
                                    });
                                }
                            }))
                            .build();
                }
            });
        }
        try{
            final Response response=cloneClient.newCall(request).execute();
            final int statusCode=response.code();
            switch(statusCode){
                case 200:
                case 206:
                    final Result result=listener.onSuccess(response);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onPostSuccess(result);
                        }
                    });
                    break;
                default:
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onPostFailure(response.request(), statusCode);
                        }
                    });
                    break;
            }
        }catch (final IOException e){
            e.printStackTrace();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onPostError(request, e);
                }
            });
        }
    }

    public <Result> void doAsyncGetLoadRefresh(String url, final ProgressListener progressListener, final HttpResponseListener<Result> listener){
        final Request request=buildGetRequest(url);
        handler.post(new Runnable() {
            @Override
            public void run() {
                listener.onPostStart(request);
            }
        });
        final OkHttpClient cloneClient=httpClient.clone();
        if(progressListener!=null){
            cloneClient.networkInterceptors().add(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Response originalResponse = chain.proceed(chain.request());
                    return originalResponse.newBuilder()
                            .body(new ProgressResponseBody(originalResponse.body(), new ProgressCallback() {
                                @Override
                                public void update(final long bytesRead, final long contentLength, final boolean done) {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressListener.postUpdate(bytesRead,contentLength,done);
                                        }
                                    });
                                }
                            }))
                            .build();
                }
            });
        }
        cloneClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Request request, final IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onPostError(request, e);
                    }
                });
            }

            @Override
            public void onResponse(final Response response) throws IOException {
                final int statusCode = response.code();
                switch (statusCode) {
                    case 200:
                        final Result content = listener.onSuccess(response);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onPostSuccess(content);
                            }
                        });
                        break;
                    default:
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onPostFailure(response.request(), statusCode);
                            }
                        });
                        break;
                }
            }
        });

    }

    public <Result> void doAsyncGetLoadRefresh(final Request request, final ProgressListener progressListener, final HttpResponseListener<Result> listener){
        handler.post(new Runnable() {
            @Override
            public void run() {
                listener.onPostStart(request);
            }
        });
        final OkHttpClient cloneClient=httpClient.clone();
        if(progressListener!=null){
            cloneClient.networkInterceptors().add(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Response originalResponse = chain.proceed(chain.request());
                    return originalResponse.newBuilder()
                            .body(new ProgressResponseBody(originalResponse.body(), new ProgressCallback() {
                                @Override
                                public void update(final long bytesRead, final long contentLength, final boolean done) {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressListener.postUpdate(bytesRead,contentLength,done);
                                        }
                                    });
                                }
                            }))
                            .build();
                }
            });
        }
        cloneClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Request request, final IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onPostError(request, e);
                    }
                });
            }

            @Override
            public void onResponse(final Response response) throws IOException {
                final int statusCode = response.code();
                switch (statusCode) {
                    case 200:
                    case 206:
                        final Result content = listener.onSuccess(response);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onPostSuccess(content);
                            }
                        });
                        break;
                    default:
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onPostFailure(response.request(), statusCode);
                            }
                        });
                        break;
                }
            }
        });

    }


    public Response downloadProgress(String url, final ProgressCallback callback) throws IOException {
        Request request=buildGetRequest(url);
        OkHttpClient cloneClient=httpClient.clone();
        cloneClient.networkInterceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response originalResponse = chain.proceed(chain.request());
                return originalResponse.newBuilder()
                        .body(new ProgressResponseBody(originalResponse.body(), callback))
                        .build();
            }
        });
        return cloneClient.newCall(request).execute();
    }

    public Response downloadProgress(String url,final Map<String, String> args,final ProgressCallback callback) throws IOException {
        Request request=buildPostRequest(url, args);
        OkHttpClient cloneClient=httpClient.clone();
        cloneClient.networkInterceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response originalResponse = chain.proceed(chain.request());
                return originalResponse.newBuilder()
                        .body(new ProgressResponseBody(originalResponse.body(), callback))
                        .build();
            }
        });
        return cloneClient.newCall(request).execute();
    }

    public Response downloadProgressRefresh(String url, final ProgressListener listener) throws IOException {
        Request request=buildGetRequest(url);
        OkHttpClient cloneClient=httpClient.clone();
        cloneClient.networkInterceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response originalResponse = chain.proceed(chain.request());
                return originalResponse.newBuilder()
                        .body(new ProgressResponseBody(originalResponse.body(), new ProgressCallback() {
                            @Override
                            public void update(final long bytesRead, final long contentLength, final boolean done) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.postUpdate(bytesRead, contentLength, done);
                                    }
                                });
                            }
                        })).build();
            }
        });
        return cloneClient.newCall(request).execute();
    }

    public Response downloadProgressRefresh(String url,final Map<String, String> args, final ProgressListener listener) throws IOException {
        Request request=buildPostRequest(url, args);
        OkHttpClient cloneClient=httpClient.clone();
        cloneClient.networkInterceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response originalResponse = chain.proceed(chain.request());
                return originalResponse.newBuilder()
                        .body(new ProgressResponseBody(originalResponse.body(), new ProgressCallback() {
                            @Override
                            public void update(final long bytesRead, final long contentLength, final boolean done) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.postUpdate(bytesRead, contentLength, done);
                                    }
                                });
                            }
                        })).build();
            }
        });
        return cloneClient.newCall(request).execute();
    }


    public String Test(String url) throws IOException {
        return null;
    }








    public <Result> void doPostAsyncRefresh(String url,Map<String, String> args, final HttpResponseListener<Result> listener)  {
            final Request request=buildPostRequest(url, args);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onPostStart(request);
                }
            });
            httpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(final Request request, final IOException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onPostError(request, e);
                        }
                    });
                }
                @Override
                public void onResponse(final Response response) throws IOException {
                    final int statusCode=response.code();
                    switch (statusCode){
                        case 200:
                            final Result content=listener.onSuccess(response);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onPostSuccess(content);
                                }
                            });
                            break;
                        default:
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onPostFailure(response.request(),statusCode);
                                }
                            });
                            break;
                    }
                }
            });
        }


    public <Result> void doPostAsyncRefresh(final String url, final Map<String, String> args, final Map<String, File> files,final HttpResponseListener<Result> listener) {

        Request request=buildPostMultipartRequest(url, args, files);
        listener.onPostStart(request);
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Request request, final IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onPostError(request, e);
                    }
                });
            }

            @Override
            public void onResponse(final Response response) throws IOException {
                final int statusCode=response.code();
                switch (statusCode){
                    case 200:
                        final Result content=listener.onSuccess(response);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onPostSuccess(content);
                            }
                        });
                        break;
                    default:
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onPostFailure(response.request(),statusCode);
                            }
                        });
                        break;
                }
            }
        });
    }


}
