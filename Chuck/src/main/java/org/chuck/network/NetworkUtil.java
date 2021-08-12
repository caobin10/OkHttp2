package org.chuck.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

public class NetworkUtil {	
	public static final String CTWAP="ctwap";//电信
	public static final String CTNET = "ctnet";//电信
	public static final String CMWAP="cmwap";//移动
	public static final String CMNET = "cmnet";//移动
	public static final String UNIWAP="uniwap";//联通
	public static final String UNINET = "uninet";//联通
	public static final String WAP_3G="3gwap";//联通
	public static final String NET_3G = "3gnet";//联通
	
	
	public static NetworkType getNetworkType(Context context){
		NetworkType networkType=NetworkType.UNRECOGNIZED;
		ConnectivityManager cm=(ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if(cm!=null){
			NetworkInfo networkInfo = cm.getActiveNetworkInfo();
			if(networkInfo==null||!networkInfo.isAvailable()){
				networkType=NetworkType.NONE;
			}else{
				if(networkInfo.getType()==ConnectivityManager.TYPE_WIFI){
					WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
					if(wifiManager.pingSupplicant()){
						networkType=NetworkType.WIFI;
		            }	
				}else
				if(networkInfo.getType()==ConnectivityManager.TYPE_MOBILE){
					String extraInfo=networkInfo.getExtraInfo();
					if(extraInfo.equals(CTWAP)){
						networkType=NetworkType.CTWAP;
					}else
					if(extraInfo.equals(CTNET)){
						networkType=NetworkType.CTNET;
					}else 
					if(extraInfo.equals(CMWAP)){
						networkType=NetworkType.CMWAP;
					}else 
					if(extraInfo.equals(CMNET)){
						networkType=NetworkType.CMNET;	
					}else 
					if(extraInfo.equals(UNIWAP)){
						networkType=NetworkType.UNIWAP;	
					}else 
					if(extraInfo.equals(UNINET)){
						networkType=NetworkType.UNINET;	
					}else
					if(extraInfo.equals(WAP_3G)){
						networkType=NetworkType.WAP_3G;
					}else 
					if(extraInfo.equals(NET_3G)){
						networkType=NetworkType.NET_3G;
					}else{
						networkType=NetworkType.UNKNOWN;
					}
				}
			}	
		}		
		return networkType;
	}
	
	public static boolean isNetworkAvailable(Context context){
		ConnectivityManager cm=(ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if(cm!=null){
			NetworkInfo[] networkInfo = cm.getAllNetworkInfo();
			if(networkInfo!=null&&networkInfo.length>0){
				for(int i=0;i<networkInfo.length;i++){
					if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED){
						return true;
					}
				}
			}
		}
		return false;		
	}
}
