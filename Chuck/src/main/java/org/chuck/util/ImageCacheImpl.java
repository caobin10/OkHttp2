package org.chuck.util;

import android.graphics.Bitmap;
import android.util.LruCache;


public class ImageCacheImpl implements ImageLoader.ImageCache {
	private static LruCache<String, Bitmap> bitmapCache;
	private static ImageCacheImpl imageCacheImpl;
	
	public ImageCacheImpl() {
		super();
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int maxSize = maxMemory / 8;
		bitmapCache=new LruCache<String, Bitmap>(maxSize){
			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getRowBytes() * value.getHeight();
			}
		};
	}

	/**
	 * 防止多次创建缓存
	 * @return
	 */
	public static ImageCacheImpl getInstance(){
		if(imageCacheImpl==null){
			synchronized (ImageCacheImpl.class){
				if(imageCacheImpl==null){
					imageCacheImpl=new ImageCacheImpl();
				}
			}
		}
		return imageCacheImpl;
	}
	
	@Override
	public Bitmap getBitmap(String key) {
		return bitmapCache.get(key);
	}

	@Override
	public void putBitmap(String key, Bitmap bitmap) {
		bitmapCache.put(key, bitmap);
	}
	
	public void removeBitMap(String key){
		bitmapCache.remove(key);
	}
	
	public void clearCache(){
		bitmapCache.evictAll();
	}
}
