package com.datab.cn.manager;

import android.app.Activity;
import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Stack;

import test.tree.util.CharSeqUtil;
import test.tree.util.GeneralUtil;

/**
 * Created by Administrator on 2021/6/24.
 */

public class AppManager {
    private static Stack<Activity> activityStack;
    private static AppManager instance;
    private AppManager(){};
    public static AppManager getAppManager(){
        if(instance==null){
            instance=new AppManager();
            activityStack=new Stack<Activity>();
        }
        return instance;
    }

    public void copyDbFileFromAsset(Context context, Map<String, Boolean> dbMap) throws Exception {
        InputStream is;
        OutputStream os;
        File dbDir=new File(context.getFilesDir().getParent()+ "/databases");
        if(!dbDir.exists()){
            dbDir.mkdir();
        }
        File outDbFile;
        int versionCode= GeneralUtil.getAppVersionCode(context);
        ACache cache=ACache.get(context);
        String dbVersion = null;
        for(Map.Entry<String, Boolean> entry:dbMap.entrySet()){
            String dbName=entry.getKey();
            boolean isForce =entry.getValue();
            dbVersion = cache.getAsString(dbName);
            outDbFile = new File(dbDir, dbName);
            boolean overlap=false;
            if(!outDbFile.exists()){
                overlap=true;
            }else
            if(isForce&&(CharSeqUtil.isEmpty(dbVersion)||Integer.valueOf(dbVersion)<versionCode)){
                overlap=true;
            }
            if(overlap){
                os = new FileOutputStream(outDbFile);
                byte[] buffer = new byte[1024];
                int length;
                is = context.getAssets().open(dbName);
                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }
                os.flush();
                is.close();
                os.close();
            }
            cache.put(dbName, String.valueOf(versionCode));
        }
    }
}
