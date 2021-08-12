package com.demo.test.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.demo.test.manager.AppConfig;
import com.demo.test.model.GetData;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.chuck.http.HttpResponseListener;
import org.chuck.http.OkHttpUtil;
import org.chuck.http.ProgressListener;
import org.chuck.util.JsonUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2021/8/6.
 */

public class HttpUtil extends OkHttpUtil {
    public static String URI_DEFAULT= AppConfig.URI_BASE+"Mobile/Ashx/MobiSource.ashx";
//    public static String URI= AppConfig.URL;

    private static HttpUtil instance;
    public static Map<String, String> argsssssss;

    private HttpUtil() {
        super();
    }

    public static HttpUtil getInstance(){
        if(instance==null){
            synchronized (HttpUtil.class) {
                if(instance==null){
                    instance=new HttpUtil();
                }
            }
        }
        return instance;
    }

    public void doGetAsync(String url,Callback callback){
        super.doGetAsync(url,callback);
    }

    public <Result> Response doPost(Map<String, String> args) throws IOException {//,HttpResponseListener<Result> listener
//        return super.doPost(URI_DEFAULT,args);
        return super.doPost(AppConfig.URL,args);
    }

    public void doPostAsyncRefresh (Map<String, String> args, final ResponseListener listener){
        this.argsssssss = args;

        if(args.get("param").equals("Get_Session_Id")){
            super.doPostAsyncRefresh( AppConfig.URI_BASE+"Mobile/Ashx/MobiSessionSource.ashx", args, listener);
        }else{
//            super.doPostAsyncRefresh(URI_DEFAULT, args, listener);
            //***新代码
            super.doPostAsyncRefresh(AppConfig.URL, args, listener);
        }
    }

    public void doPostAsyncRefresh (Map<String, String> args, Map<String, File> fileMap, final ResponseListener listener){
        super.doPostAsyncRefresh(URI_DEFAULT, args, fileMap,listener);
    }


    public void doPostAsyncRefresh (Map<String, String> args, final HttpResponseListener listener){
        super.doPostAsyncRefresh(URI_DEFAULT, args, listener);
    }

    public void doAsyncGetLoadRefresh(String uri,ProgressListener progressListener,ProgressResponseListener listener){
        super.doAsyncGetLoadRefresh(uri,progressListener,listener);
    }




    public abstract static class ResponseListener extends HttpResponseListener<GetData> {
        private Context context;
        //		private final SweetDialog dialog;
        public ResponseListener(Context context){
            this.context=context;
//			dialog = DialogUtil.createLoadingDefault(context);
//			dialog.show();
        }
        @Override
        public GetData onSuccess(Response response) throws IOException {
//			dialog.dismiss();
            if(argsssssss!=null){
                for(Map.Entry<String,String > entry : argsssssss.entrySet()){

                    if(entry.getValue().equals("Get_Session_Id")){
                        Headers headers = response.headers();

                        List<String> cookies = headers.values("Set-Cookie");

                        if(cookies!=null&&cookies.size()>0){
                            String session = cookies.get(0);
                            String sessionid = session.substring(0, session.indexOf(";"));

                            saveCookiePreference(context,sessionid);
                        }

                    }
                }
            }




            return JsonUtil.jsonToObj(response.body().string(), GetData.class);
        }
        @Override
        public void onPostFailure(Request request, int statusCode) {
//			dialog.dismiss();
            Toast.makeText(context,"请检查请求资源是否可用,错误码："+statusCode,Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onPostError(Request request, IOException e){
//			dialog.dismiss();
            e.printStackTrace();
            try{
                Toast.makeText(context,"请检查连接资源是否可用",Toast.LENGTH_SHORT).show();
            }catch (Exception e1){e1.printStackTrace();}

        }
    }

    public abstract static class ProgressResponseListener extends HttpResponseListener<String>{
        private Context context;
        public ProgressResponseListener(Context context){
            this.context=context;
        }

        @Override
        public void onPostSuccess(String s) {

        }

        @Override
        public void onPostFailure(Request request, int statusCode) {
            Toast.makeText(context,"错误码："+statusCode,Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPostError(Request request, IOException e) {
            Toast.makeText(context,"请检查连接资源是否可用",Toast.LENGTH_SHORT).show();
        }
    }


    public static final String ISLOGINED = "islogined";
    public static final String COOKIE = "cookie";

    public static void saveCookiePreference(Context context, String value) {
        SharedPreferences preference = context.getSharedPreferences(ISLOGINED, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preference.edit();
        editor.putString(COOKIE, value);
        editor.apply();
    }
}
