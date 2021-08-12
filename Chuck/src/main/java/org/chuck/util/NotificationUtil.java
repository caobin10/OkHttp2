package org.chuck.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

public class NotificationUtil {
	private Context context;
	public NotificationUtil(Context context) {
		this.context=context;
	}
	public void notify(int resId,int icon,CharSequence title,CharSequence text,
			CharSequence tickerText,int number,PendingIntent intent){
		NotificationCompat.Builder notifyBuilder=
				new NotificationCompat.Builder(context)
				.setSmallIcon(icon)
				.setContentTitle(title)
				.setContentText(text)
				.setTicker(tickerText)
				.setNumber(number)
//				.setSound(sound, streamType)
				.setDefaults(Notification.DEFAULT_VIBRATE);
//				.setContentIntent(intent);
		
		Notification notification=notifyBuilder.build();
//		if(Build.VERSION.SDK_INT<16){
//			notification.contentView=remoteViews;
//		}
		
		NotificationManager notifyManager=
				(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notifyManager.notify("TAGx", 2, notification);
	}


	public void getNotification(RemoteViews remoteViews){
		Notification notification=new Notification();

	}
	
}
