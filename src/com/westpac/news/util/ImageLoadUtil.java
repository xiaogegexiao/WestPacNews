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

public class ImageLoadUtil {

	// static HashMap<String,Bitmap> buffer = new HashMap<String,Bitmap>();
	static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(20, 40, 3,TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(50),new ThreadPoolExecutor.DiscardOldestPolicy());
	public static HashMap<String,LoadImgThread> urlPool = new HashMap<String,LoadImgThread>();

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

	public static Bitmap readImg(String url) {
        return ImageBuffer.readImg(url);
	}

	public static void writeImg(Context context, String url, InputStream is) {
	    ImageBuffer.writeImg(context, url, is);
		urlPool.remove(url);
	}
	public static void removeThread(String url){
	    if(url != null && urlPool.get(url) != null){
	        threadPool.remove(urlPool.get(url));
	        urlPool.remove(url);
	    }
	}
}
