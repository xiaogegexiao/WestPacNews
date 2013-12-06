package com.westpac.news.util;

import java.io.InputStream;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.graphics.Bitmap;

import com.westpac.news.model.UrlBitmap;
import com.westpac.news.threads.LoadImgThread;

/**
 * ImageLoadUtil class for imageload
 * from network server
 * from memory and local file system
 * @author Xiao
 *
 */
public class ImageLoadUtil {

	/**
	 * Thread pool for image loading threads
	 */
	static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(20, 40, 3,TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(50),new ThreadPoolExecutor.DiscardOldestPolicy());
	/**
	 * hashmap used to track threads in pool
	 */
	public static HashMap<String,LoadImgThread> urlPool = new HashMap<String,LoadImgThread>();

	/**
	 * if there is no equal thread in the pool, start a new thread and put it into the thread pool to fetch the image from server
	 * @param context
	 * @param url
	 * @param handler call back 
	 */
	public static void readBitmapAsync(Context context, String url,
			MethodHandler<UrlBitmap> handler) {
		Bitmap bt = ImageLoadUtil.readImg(url);
		if (bt == null) {
                LoadImgThread thread = new LoadImgThread(context, url, handler);
                threadPool.execute(thread);
                if (!urlPool.containsKey(url)) {
                    urlPool.put(url, thread);
                }
		} else {
            if (handler != null) {
                handler.process(new UrlBitmap(bt, url));
            }
        }
	}

	/**
	 * read image from memory and local file system
	 * @param url
	 * @return
	 */
	public static Bitmap readImg(String url) {
        return ImageBuffer.readImg(url);
	}

	/**
	 * write image inputstream into local file system
	 */
	public static void writeImg(Context context, String url, InputStream is) {
	    ImageBuffer.writeImg(context, url, is);
		urlPool.remove(url);
	}
}
