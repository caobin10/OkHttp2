package test.tree.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
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
	
//	/**
//	 * 获取设备CPU核数
//	 * @return
//	 */
//	public static int getDevCpuCount(){
//		return Runtime.getRuntime().availableProcessors();
//	}
	
//	public static String getAppVersionName(Context context) {
//		String versionName = "UnKnown";
//		try {
//			PackageManager packageManager = context.getPackageManager();
//			PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(),0);
//			versionName=packageInfo.versionName;
//		} catch (PackageManager.NameNotFoundException e) {
//			e.printStackTrace();
//		}
//		return versionName;
//	}
	
	public static int getAppVersionCode(Context context) {
		int verionCode = 0 ;
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(),0);
			verionCode=packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return verionCode;
	}
	
//	public static void showToast(final Context context, final CharSequence text){
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				Looper.prepare();
//				Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
//				Looper.loop();
//			}
//		}).start();;
//
//	}
		

//	public static String getDevImei(Context context){
//		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//		return telephonyManager.getDeviceId();
//	}
	
//	public static String getAndroidId(Context context){
//		return Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
//	}
	
//	public static int getDevScreenWidthPixels(Activity activity){
//		DisplayMetrics metrics=new DisplayMetrics();
//		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
//		return metrics.widthPixels;
//	}

//	public static DisplayMetrics getDisplayMetrics(Context context){
//		DisplayMetrics dm=context==null? Resources.getSystem().getDisplayMetrics():context.getResources().getDisplayMetrics();
//		return dm;
//	}

//	public static boolean isAppExist(Context context, String appPackage){
//		if(CharSeqUtil.isNullOrEmpty(appPackage)){
//			return false;
//		}
//		try{
//			ApplicationInfo info = context.getPackageManager().getApplicationInfo(appPackage, PackageManager.GET_META_DATA);
//			return true;
//		}catch(Exception e){
//			e.printStackTrace();
//			return false;
//		}
//	}
}
