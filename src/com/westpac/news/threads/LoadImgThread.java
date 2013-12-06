package com.westpac.news.threads;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;

import com.westpac.news.model.UrlBitmap;
import com.westpac.news.util.ImageLoadUtil;
import com.westpac.news.util.MethodHandler;

/**
 * Load image thread load image from server
 * 
 * @author xiao
 * 
 */
public class LoadImgThread implements Runnable, Serializable {
	public static final long serialVersionUID = 00000000000000;

	/**
	 * url connection time out is 30 seconds
	 */
	public final static int ConnectTimeOutTime = 30000;
	/**
	 * url of image
	 */
	private String url;
	/**
	 * callback object which will be run after loading image successfully
	 */
	private MethodHandler<UrlBitmap> handler;
	private Context context;

	public LoadImgThread(Context context, String url,
			MethodHandler<UrlBitmap> postHandler) {
		this.context = context;
		this.url = url;
		handler = postHandler;
	}

	public void run() {
		Bitmap bm = null;
		if (url != null && url.length() > 0) {
			try {
				/* load image from memory again in case it has been loaded successfully by other thread */
				bm = ImageLoadUtil.readImg(url);
				if (bm == null) {
					/* open the url connection and save the image to file
					 * and load the image to memory for use*/
					URL mUrl = new URL(url);
					HttpURLConnection conn = (HttpURLConnection) mUrl
							.openConnection();
					conn.setConnectTimeout(ConnectTimeOutTime);
					conn.setDoInput(true);
					conn.connect();
					InputStream is = conn.getInputStream();
					ImageLoadUtil.writeImg(context, url, is);
					bm = ImageLoadUtil.readImg(url);
					is.close();
				}
			} catch (NumberFormatException nfe) {
			} catch (MalformedURLException e) {
			} catch (IOException e) {
			} catch (Exception e) {
			}
		}
		if (bm != null && handler != null) {
			handler.process(new UrlBitmap(bm, url));
		}
		// set url to null in order to let clear it in ThreadPool.
		url = null;
	}

	public String getUrl() {
		return url;
	}

	/**
	 * override equals method to compare two threads
	 * two threads with the same image url are the same
	 * used the check whether we should add this thread to thread pool
	 */
	@Override
	public boolean equals(Object o) {
		if (o == null || o.getClass() != this.getClass())
			return false;
		LoadImgThread t = (LoadImgThread) o;
		String u = t.getUrl();
		if (u == null || u.length() == 0)
			return false;
		return u.equals(url);
	}

}
