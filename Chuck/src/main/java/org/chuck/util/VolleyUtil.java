package org.chuck.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.http.HttpStatus;
import org.chuck.R;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.ImageLoader.ImageContainer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;

public class VolleyUtil {
	private Context context;
//	private int loadingImage;
	private int emptyImage;
	private int errorImage;
	private static int MAX_WIDTH=300;
	private static int MAX_HEIGHT=550;
	private ScaleType scaleType;
//	private int expiresTime;
	
	private RequestQueue requestQueue;
	private ImageLoader imageLoader;
//	private OnImageListener onImageListener;
	public VolleyUtil(Context context) {
		this.context=context;
//		this.expiresTime=Config.IMAGE_CACHE_EXPIRES_TIME;
		requestQueue=Volley.newRequestQueue(context);
//		imageLoader=new ImageLoader(requestQueue, ImageCacheImpl.getInstance());
	}
	
	
	public void display(ImageView imageView,String url){
		if(!CharSeqUtil.isNullOrEmpty(url)){
			displayImageOnNetwork(imageView,url);
		}
	}
	
//	public void setLoadingImage(int resId){
//		this.loadingImage=resId;
//	}
	public void setEmptyImage(int resId){
		this.emptyImage=resId;
	}
	public void setErrorImage(int resId){
		this.errorImage=resId;
	}
//	public void setExpiresTime(int expiresTime){
//		this.expiresTime=expiresTime;
//	}
	public void setMaxWidth(int maxWidth) {
		MAX_WIDTH = maxWidth;
	}
	public void setMaxHeight(int maxHeight) {
		MAX_HEIGHT = maxHeight;
	}


	private void displayImageOnNetwork(final ImageView imageView,String url){
//		if(loadingImage!=0){
			AnimUtil.anim(imageView, R.anim.loading_anim_test, false);
//		}		
		
		//设置标记,目的解决闪烁问题
	    imageView.setTag(url);
	    
	    imageLoader.get(url, new ImageListener() {		
			@Override
			public void onErrorResponse(VolleyError error) {		
				if(error.networkResponse==null){
					Toast.makeText(context, "网络连接失败！", Toast.LENGTH_SHORT).show();
					imageView.setImageResource(errorImage);
				}else
				if(error.networkResponse.statusCode==HttpStatus.SC_NOT_FOUND){
					if(emptyImage!=0){
						imageView.setImageResource(emptyImage);
					}
				}else
				if(errorImage!=0){
					imageView.setImageResource(errorImage);
				}	
			}
			
			@Override
			public void onResponse(ImageContainer response, boolean isImmediate) {			
				Bitmap bitmap=response.getBitmap();
				if(bitmap!=null){
					imageView.setImageBitmap(bitmap);
				}else {
					if(isImmediate){
						return;
					}
					if(emptyImage!=0){
						imageView.setImageResource(emptyImage);
					}
				}
			}
		}, MAX_WIDTH, MAX_HEIGHT,scaleType);
	}
	
	
	
	
	
//	public interface ResponseOnFinshed{
//		public void onErrorResponseFinished(VolleyError error) ;
//		public void onResponseFinished(ImageContainer response, boolean isImmediate);
//	}
	
	
	
	
	
	
//	public static Bitmap scaleBitmap(Bitmap bitmap,File saveBmpFile){
//		if(bitmap!=null){
//			float scale=1;
//			int orig_width=bitmap.getWidth();
//			int orig_height=bitmap.getHeight();
//			if(orig_width>300){
//				scale=(float)300/(float)orig_width;
//			}
//			Matrix matrix = new Matrix();
//			matrix.postScale(scale, scale);
//			Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, orig_width, orig_height, matrix, true);				
//			
//			FileOutputStream fos = null;
//			try {
//				fos = new FileOutputStream(saveBmpFile);
//				newBitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
//			} catch (FileNotFoundException e) {
//				e.printStackTrace();
//			}finally{
//				try {
//					fos.flush();
//					fos.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}			
//			
//			//及时释放Bitmap内存
//			recycle(bitmap);
//			//及时释放Bitmap内存
//			recycle(newBitmap);
//		}		
//		return BitmapFactory.decodeFile(saveBmpFile.getAbsolutePath());
//	}
//	public static void scaleBitmap(String bmpFilePath,File saveBmpFile){
//		Bitmap bmp=BitmapFactory.decodeFile(bmpFilePath);
//		scaleBitmap(bmp, saveBmpFile);
//		
//	}
	
//	public static void scaleBitmap(String bmpFilePath,String saveBmpFilePath){
//		File saveBmpFile=new File(saveBmpFilePath);
//		scaleBitmap(bmpFilePath, saveBmpFile);
//	}
	
	
	public static Bitmap scaleBitmap(File bmpFile,int desiredWidth, int desiredHeight){
		return scaleBitmap(bmpFile.getPath(),desiredWidth, desiredHeight,null);		
	}
	public static Bitmap scaleBitmap(String bmpFilePath){
		return scaleBitmap(bmpFilePath,MAX_WIDTH, MAX_HEIGHT,null);
	}
	public static Bitmap scaleBitmap(String bmpFilePath,int desiredWidth, int desiredHeight,String saveFilePath){
		BitmapFactory.Options options = new BitmapFactory.Options();
		
		//不分配内存
		options.inJustDecodeBounds=true;
		BitmapFactory.decodeFile(bmpFilePath, options);
		
		//图片原始宽高
		int origWidth=options.outWidth;
		int origHeight=options.outHeight;		
		
		
		float scale=getMinScale(origWidth, origHeight, desiredWidth, desiredHeight);
		
//		options.inSampleSize=Math.round((float)1/scale);		
		int realityWidth=(int) (origWidth*scale);
		int realityHeight=(int) (origHeight*scale);

		//设置期望得到的图片的宽高
		options.outWidth = realityWidth;
		options.outHeight = realityHeight;
		
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		options.inPurgeable = true;
		options.inInputShareable = true;
		options.inDither = false;
		//分配内存
		options.inJustDecodeBounds = false;		
		Bitmap bitmap=BitmapFactory.decodeFile(bmpFilePath, options);
		bitmap=getScaleBitmap(bitmap, scale);
		if(!CharSeqUtil.isNullOrEmpty(saveFilePath)){
			png2Jpeg(bitmap,saveFilePath);
			recycle(bitmap);
			bitmap=BitmapFactory.decodeFile(saveFilePath);
		}	
		return bitmap;
	}
	
	
	
	
	public static Bitmap getScaleBitmap(Bitmap bitmap,float scale){	
		if(scale==1){
			return bitmap;
		}
		
		Bitmap newBitmap=null;
		try{
			int width=bitmap.getWidth();
			int height=bitmap.getHeight();
			Matrix matrix = new Matrix();
			matrix.postScale(scale, scale);
			newBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			recycle(bitmap);
		}		
		return newBitmap;
	}
	
	
	
	public static float getMinScale(int origWidth,int origHeight,int desiredWidth, int desiredHeight){
		float widthScale=(float)desiredWidth/(float)origWidth;
		float heightScale=(float)desiredHeight/(float)origHeight;
		return widthScale<heightScale?widthScale:heightScale;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static void png2Jpeg(Bitmap pngBmp,String jpgSaveFilePath){
		FileOutputStream fos = null;
		try {
			File jpgSaveFile=new File(jpgSaveFilePath);
			File saveFileDir=new File(jpgSaveFile.getParent());
			if(!saveFileDir.exists()){
				saveFileDir.mkdirs();
			 }
			
			fos = new FileOutputStream(jpgSaveFile);
			pngBmp.compress(Bitmap.CompressFormat.JPEG, 80, fos);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}finally{
			try {
//					fos.flush();
					fos.close();		
			} catch (IOException e) {
				e.printStackTrace();
			}
		}	
	}
	
	
	
	
	public static void recycle(Bitmap bitmap){
		if(bitmap!=null&&!bitmap.isRecycled()){
			bitmap.recycle();
			bitmap=null;			
		}
		System.gc();
	}
}
