package com.demo.test.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.util.Xml;
import android.view.View;
import android.widget.Toast;

import com.demo.test.R;
import com.demo.test.model.Version;
import com.demo.test.util.DialogUtil;
import com.demo.test.util.HttpUtil;
import com.demo.test.util.LfStorageUtil;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.chuck.http.HttpResponseListener;
import org.chuck.http.ProgressListener;
import org.chuck.util.GeneralUtil;
import org.chuck.view.SweetDialog;
import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Stack;

/**
 * Created by Administrator on 2021/8/11.
 */

public class AppManager {
    private static Stack<Activity> activityStack;
    private static AppManager instance;

    public static AppManager getAppManager(){
        if(instance==null){
            instance=new AppManager();
            activityStack=new Stack<Activity>();
        }
        return instance;
    }

    public void AppExit(Context context) {
        try {
//            finishAllActivity();
//			ActivityManager activityMgr= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//			activityMgr.killBackgroundProcesses(context.getPackageName());
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void checkUpdate(final Context context){
        try {
            HttpUtil.getInstance().doAsyncGetLoadRefresh(AppConfig.APK_DOWN_URI + AppConfig.SERVER_MOBILE_CONFIG_DIR + "version_dz.xml", null, new HttpResponseListener<Version>() {
                @Override
                public Version onSuccess(Response response) throws IOException {
                    Version version = null;
                    try {
                        XmlPullParser pullParser = Xml.newPullParser();
                        pullParser.setInput(response.body().charStream());//UTF-8+BOM 此解析会报错
//						InputStream is=response.body().byteStream();
//						pullParser.setInput(is, "utf-8");
                        int eventType = pullParser.getEventType();
                        while (eventType != XmlPullParser.END_DOCUMENT) {
                            switch (eventType) {
                                case XmlPullParser.START_DOCUMENT:
                                    break;
                                case XmlPullParser.START_TAG:
                                    if (pullParser.getName().equals("Version")) {
                                        version = new Version();
                                    } else if (pullParser.getName().equals("code")) {
                                        eventType = pullParser.next();
                                        version.setCode(pullParser.getText());
                                    } else if (pullParser.getName().equals("apkForce")) {
                                        eventType = pullParser.next();
                                        version.setApkForce(Boolean.valueOf(pullParser.getText()));
                                    } else if (pullParser.getName().equals("dbForce")) {
                                        eventType = pullParser.next();
                                        version.setDbForce(Boolean.valueOf(pullParser.getText()));
                                    } else if (pullParser.getName().equals("description")) {
                                        eventType = pullParser.next();
                                        version.setDescription(pullParser.getText());
                                    }
                                    break;
                                case XmlPullParser.END_TAG:
                                    break;
                            }
                            eventType = pullParser.next();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return version;
                }

                @Override
                public void onPostSuccess(final Version version) {
                    String msg = "当前已是最新版本";
                    View.OnClickListener ensureListener = null;
                    View.OnClickListener cancelListener = null;
                    final SweetDialog dialog = new SweetDialog(context);
                    if (GeneralUtil.getAppVersionCode(context) < Integer.parseInt(version.getCode())) {
                        msg = version.getDescription();
                        ensureListener = new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                updateApp(context,version);
                            }
                        };
                        if (version.isApkForce()) {
                            cancelListener = new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    getAppManager().AppExit(context);
                                }
                            };
                        }
                    }
                    dialog.setContentView(R.layout.dialog_msg_default);
                    dialog.setText(R.id.tv_msg, msg)//Html.fromHtml(msg)
                            .setOnClickListener(R.id.btn_ensure, ensureListener == null ? new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            } : ensureListener).setOnClickListener(R.id.btn_cancel, cancelListener == null ? new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    } : cancelListener).setDefault();
                    dialog.show();
                }

                @Override
                public void onPostFailure(Request request, int statusCode) {
                    Toast.makeText(context, "错误码：" + statusCode, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onPostError(Request request, IOException e) {
                    Toast.makeText(context, "请检查连接资源是否可用", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateApp(final Context context,Version version){
        final DecimalFormat df =new DecimalFormat("#0.0");
        final SweetDialog dialog= DialogUtil.createProgressDefault(context, "正在连接...");//
        final ProgressWheel progressWheel=dialog.getView(R.id.pw_progressWheel);
//		progressWheel.stopSpinning();
        dialog.show();
        String url = AppConfig.APK_DOWN_URI + AppConfig.SERVER_MOBILE_APK_DIR + AppConfig.APK_NAME + version.getCode() + ".apk";
        HttpUtil.getInstance().doAsyncGetLoadRefresh( url , new ProgressListener() {
            @Override
            public void postUpdate(long bytesRead, long contentLength, boolean done)
            {
                int progress = (int) (bytesRead * 100 / contentLength);
                dialog.setText(R.id.tv_msg,"已下载 "+df.format((float) bytesRead / (1024 * 1024)) + "/" + df.format((float) contentLength / (1024 * 1024)) + "M      " + progress + "%");
                progressWheel.setInstantProgress((float) progress / 100);
            }
        }, new HttpResponseListener<File>() {
            @Override
            public File onSuccess(Response response) throws IOException {
                File file = null;
                try {
                    long length = response.body().contentLength();
                    file = new File(LfStorageUtil.EXTERNAL_STORAGE, AppConfig.APP_APK_DIR );
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    file=new File(file,AppConfig.APK_NAME);
                    InputStream is = response.body().byteStream();
                    FileOutputStream fos = new FileOutputStream(file);
                    int count = 0;
                    byte[] buf = new byte[1024];
                    while (count < length) {
                        int readCount = is.read(buf);
                        count += readCount;
                        fos.write(buf, 0, readCount);
                    }
                    is.close();
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return file;
            }
            @Override
            public void onPostSuccess(File file) {
                dialog.dismiss();
                if(file!=null&&file.exists()){

//					Intent intent = new Intent(Intent.ACTION_VIEW);
//					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//					intent.setDataAndType(Uri.parse("file://" + file.toString()), "application/vnd.android.package-archive");
//					context.startActivity(intent);

                    //升级时清除用户信息 让用户重新登录
                    try {
                        Intent intent = new Intent();
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setAction(Intent.ACTION_VIEW);
                        if (Build.VERSION.SDK_INT >= 24) {
                            String appName = context.getPackageName();

                            Uri apkUri = FileProvider.getUriForFile(context, appName+".fileprovider", file);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                        } else {
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                        }
                        context.startActivity(intent);


                        ACache cache = ACache.get(context);
                        cache.remove("user");

                        AppApplication application = (AppApplication) ((Activity)context).getApplication();
                        application.setUser(null);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(context,"下载失败", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onPostFailure(Request request, int statusCode) {
                Toast.makeText(context,"下载失败", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }

            @Override
            public void onPostError(Request request, IOException e) {
                Toast.makeText(context,"下载出错", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }
}
