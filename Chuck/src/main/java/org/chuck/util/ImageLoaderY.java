package org.chuck.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class ImageLoaderY
{
	private static final int POOL_SIZE_DEFAULT = 1;

	private LoaderType loaderType = LoaderType.LIFO;
	private LruCache<String, Bitmap> lruCache;
	private LinkedList<Runnable> tasks;/**任务队列*/

	private Thread loopThread;/**轮询的线程*/
	private Handler loopHandler;
	private volatile Semaphore loopSemaphore;/**引入一个值为1的信号量，由于线程池内部也有一个阻塞线程，防止加入任务的速度过快，使LIFO效果不明显*/

//	private ExecutorService executorService;
	private Handler loaderHandler;/**运行在UI线程的handler，用于给ImageView设置图片*/
	private volatile Semaphore loaderSemaphore = new Semaphore(0);/**引入一个值为1的信号量，防止mPoolThreadHander未初始化完成*/

	private static ImageLoaderY mInstance;

	/**
	 * 队列的调度方式
	 */
	public enum LoaderType {
		FIFO, LIFO
	}

	private ImageLoaderY() {
		this(POOL_SIZE_DEFAULT);
	}
	private ImageLoaderY(int poolSize) {
		this(poolSize, LoaderType.LIFO);
	}
	private ImageLoaderY(int poolSize, LoaderType loaderType) {
		init(poolSize, loaderType);
	}


	public static ImageLoaderY getInstance() {
		if (mInstance == null) {
			synchronized (ImageLoaderY.class) {
				if (mInstance == null) {
					mInstance = new ImageLoaderY();
				}
			}
		}
		return mInstance;
	}

	private void init(int poolSize, LoaderType loaderType) {
		// 获取应用程序最大可用内存
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int cacheSize = maxMemory / 8;
		lruCache = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getRowBytes() * value.getHeight();
			};
		};
//		executorService = Executors.newFixedThreadPool(poolSize);
		loopSemaphore = new Semaphore(poolSize);
		tasks = new LinkedList<>();
		this.loaderType = loaderType == null ? LoaderType.LIFO : loaderType;

		loopThread = new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				loopHandler = new Handler() {
					@Override
					public void handleMessage(Message msg) {
						ThreadPool.getExecutor().execute(getTask());
//						executorService.execute(getTask());
						try {
							loopSemaphore.acquire();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				};
				loaderSemaphore.release();// 释放一个信号量
				Looper.loop();
			}
		};
		loopThread.start();
	}

	/**
	 * 加载图片
	 * @param path
	 * @param imageView
	 */
	public void loadImage(final String path, final ImageView imageView) {
		// set tag
		imageView.setTag(path);
		// UI线程
		if (loaderHandler == null) {
			loaderHandler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					ImgBeanHolder holder = (ImgBeanHolder) msg.obj;
					ImageView imageView = holder.imageView;
					Bitmap bm = holder.bitmap;
					String path = holder.path;
					if (imageView.getTag().toString().equals(path)) {
						imageView.setImageBitmap(bm);
					}
				}
			};
		}

		Bitmap bm = getBitmapFromLruCache(path);
		if (bm != null) {
			ImgBeanHolder holder = new ImgBeanHolder();
			holder.bitmap = bm;
			holder.imageView = imageView;
			holder.path = path;
			Message message = Message.obtain();
			message.obj = holder;
			loaderHandler.sendMessage(message);
		} else {
			addTask(new Runnable() {
				@Override
				public void run() {
					ImageSize imageSize = getImageViewWidth(imageView);

					int reqWidth = imageSize.width;
					int reqHeight = imageSize.height;

					Bitmap bm = decodeSampledBitmapFromResource(path, reqWidth, reqHeight);
					addBitmapToLruCache(path, bm);
					ImgBeanHolder holder = new ImgBeanHolder();
					holder.bitmap = getBitmapFromLruCache(path);
					holder.imageView = imageView;
					holder.path = path;
					Message message = Message.obtain();
					message.obj = holder;
					loaderHandler.sendMessage(message);
					loopSemaphore.release();
				}
			});
		}
	}
	
	/**
	 * 添加一个任务
	 * @param runnable
	 */
	private synchronized void addTask(Runnable runnable) {
		try {
			// 请求信号量，防止loopHandler为null
			if (loopHandler == null) loaderSemaphore.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		tasks.add(runnable);
		loopHandler.sendEmptyMessage(0x110);
	}

	/**
	 * 取出一个任务
	 * @return
	 */
	private synchronized Runnable getTask() {
		if (loaderType == LoaderType.FIFO) {
			return tasks.removeFirst();
		} else if (loaderType == LoaderType.LIFO) {
			return tasks.removeLast();
		}
		return null;
	}

	/**
	 * 根据ImageView获得适当的压缩的宽和高
	 * @param imageView
	 * @return
	 */
	private ImageSize getImageViewWidth(ImageView imageView) {
		ImageSize imageSize = new ImageSize();
		final DisplayMetrics displayMetrics = imageView.getContext().getResources().getDisplayMetrics();
		final LayoutParams params = imageView.getLayoutParams();
		int width = params.width == LayoutParams.WRAP_CONTENT ? 0 : imageView.getWidth(); // Get actual image width
		if (width <= 0) width = params.width; // Get layout width parameter
		if (width <= 0) width = getImageViewFieldValue(imageView, "mMaxWidth"); // Check // maxWidth // parameter
		if (width <= 0) width = displayMetrics.widthPixels;
		int height = params.height == LayoutParams.WRAP_CONTENT ? 0 : imageView.getHeight(); // Get actual image height
		if (height <= 0) height = params.height; // Get layout height parameter
		if (height <= 0) height = getImageViewFieldValue(imageView, "mMaxHeight"); // Check // maxHeight // parameter
		if (height <= 0) height = displayMetrics.heightPixels;
		imageSize.width = width;
		imageSize.height = height;
		return imageSize;

	}

	/**
	 * 从LruCache中获取一张图片，如果不存在就返回null。
	 */
	private Bitmap getBitmapFromLruCache(String key)
	{
		return lruCache.get(key);
	}

	/**
	 * 往LruCache中添加一张图片
	 * @param key
	 * @param bitmap
	 */
	private void addBitmapToLruCache(String key, Bitmap bitmap) {
		if (getBitmapFromLruCache(key) == null) {
			if (bitmap != null) lruCache.put(key, bitmap);
		}
	}

	/**
	 * 计算inSampleSize，用于压缩图片
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// 源图片的宽度
		int width = options.outWidth;
		int height = options.outHeight;
		int inSampleSize = 1;
		if (width > reqWidth && height > reqHeight) {
			// 计算出实际宽度和目标宽度的比率
			int widthRatio = Math.round((float) width / (float) reqWidth);
			int heightRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = Math.max(widthRatio, heightRatio);
		}
		return inSampleSize;
	}

	/**
	 * 根据计算的inSampleSize，得到压缩后图片
	 * 
	 * @param pathName
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	private Bitmap decodeSampledBitmapFromResource(String pathName, int reqWidth, int reqHeight) {
		// 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(pathName, options);
		// 调用上面定义的方法计算inSampleSize值
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
		// 使用获取到的inSampleSize值再次解析图片
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeFile(pathName, options);
		return bitmap;
	}

	private class ImgBeanHolder {
		Bitmap bitmap;
		ImageView imageView;
		String path;
	}

	private class ImageSize {
		int width;
		int height;
	}

	/**
	 * 反射获得ImageView设置的最大宽度和高度
	 * @param object
	 * @param fieldName
	 * @return
	 */
	private static int getImageViewFieldValue(Object object, String fieldName) {
		int value = 0;
		try {
			Field field = ImageView.class.getDeclaredField(fieldName);
			field.setAccessible(true);
			int fieldValue = (Integer) field.get(object);
			if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE) {
				value = fieldValue;
				Log.e("TAG", value + "");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}
}
