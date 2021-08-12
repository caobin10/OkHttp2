package org.chuck.network;

import android.content.Context;

public abstract class AccessNetwork {
	private Context context;
	
	public AccessNetwork(Context context) {
		this.context=context;
	}
	
	public void readyAccess(){				
		NetworkType networkType=NetworkUtil.getNetworkType(context);
		switch(networkType){
		case UNRECOGNIZED:
			break;
		case NONE:
			noNetwork();
			break;
		case WIFI:		
			isWifiNetwork();
			break;
		default:
			isMobileNetwork(networkType);
			break;
		}		
	}
	
	public abstract void noNetwork();
	public abstract void isWifiNetwork();
	public abstract void isMobileNetwork(NetworkType networkType);
}
