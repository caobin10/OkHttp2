package org.chuck.util;

import android.graphics.drawable.AnimationDrawable;
import android.widget.ImageView;

public class AnimUtil {
	public static void anim(ImageView imageView,int resId,boolean oneShot){
		imageView.setBackgroundResource(resId);
		AnimationDrawable animDrawable=(AnimationDrawable) imageView.getBackground();
		animDrawable.setOneShot(oneShot);
		if(animDrawable.isRunning()){
			animDrawable.stop();
		}
		animDrawable.start();
	}
}
