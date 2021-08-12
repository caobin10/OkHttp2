package org.chuck.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.Looper;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;

public class GeneralUtil {
	
	/**
	 * 获取设备CPU核数
	 * @return
	 */
	public static int getDevCoreNum(){
		File dir = new File("/sys/devices/system/cpu/");
		int coreNum=1;
		try{
			File[] files = dir.listFiles(new FileFilter(){
				@Override
				public boolean accept(File pathname) {
					if(Pattern.matches("cpu[0-9]", pathname.getName())) { 
					   return true; 
				    } 
				    return false; 
				}			
			}); 
			coreNum=files.length;
		}catch(Exception e){
			e.printStackTrace();
		}		
		return coreNum;
	}
	
	/**
	 * 获取设备CPU核数
	 * @return
	 */
	public static int getDevCpuCount(){
		return Runtime.getRuntime().availableProcessors();
	}
	
	public static String getAppVersionName(Context context) {		
		String versionName = "UnKnown";
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(),0);
			versionName=packageInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionName;
	}
	
	public static int getAppVersionCode(Context context) {
		int versionCode = 0 ;
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(),0);
			versionCode=packageInfo.versionCode;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return versionCode;
	}
	
	public static void showToast(final Context context,final CharSequence text){
		new Thread(new Runnable() {			
			@Override
			public void run() {
				Looper.prepare();
				Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
				Looper.loop();				
			}
		}).start();;
		
	}
		

	public static String getDevImei(Context context){
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getDeviceId();
	}
	
	public static String getAndroidId(Context context){
		return Secure.getString(context.getContentResolver(), Secure.ANDROID_ID); 
	}
	
	public static int getDevScreenWidthPixels(Activity activity){
		DisplayMetrics metrics=new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		return metrics.widthPixels;
	}

	public static DisplayMetrics getDisplayMetrics(Context context){
		DisplayMetrics dm=context==null? Resources.getSystem().getDisplayMetrics():context.getResources().getDisplayMetrics();
		return dm;
	}

	public static boolean isAppExist(Context context,String appPackage){
		if(CharSeqUtil.isNullOrEmpty(appPackage)){
			return false;
		}
		try{
			ApplicationInfo info = context.getPackageManager().getApplicationInfo(appPackage, PackageManager.GET_UNINSTALLED_PACKAGES);
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}



	public static void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}



	public static float dip2px(Context context, float dipValue) {
		DisplayMetrics displayMetrics = getDisplayMetrics(context);
		return applyDimension(TypedValue.COMPLEX_UNIT_DIP,dipValue,displayMetrics);
	}

	public static float applyDimension(int unit, float value, DisplayMetrics metrics){
		switch (unit) {
			case TypedValue.COMPLEX_UNIT_PX:
				return value;
			case TypedValue.COMPLEX_UNIT_DIP:
				return value * metrics.density;
			case TypedValue.COMPLEX_UNIT_SP:
				return value * metrics.scaledDensity;
			case TypedValue.COMPLEX_UNIT_PT:
				return value * metrics.xdpi * (1.0f/72);
			case TypedValue.COMPLEX_UNIT_IN:
				return value * metrics.xdpi;
			case TypedValue.COMPLEX_UNIT_MM:
				return value * metrics.xdpi * (1.0f/25.4f);
		}
		return 0;
	}
}
