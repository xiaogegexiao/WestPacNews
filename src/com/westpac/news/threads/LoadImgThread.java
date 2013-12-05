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

public class LoadImgThread implements Runnable, Serializable {
	public final static int ConnectTimeOutTime = 30000;
	private String url;
	private MethodHandler<UrlBitmap> handler;
	private Context context;

	public LoadImgThread(Context context, String url, MethodHandler<UrlBitmap> postHandler) {
		this.context = context;
		this.url = url;
		handler = postHandler;
	}

	public void run() {
		Bitmap bm = null;
		// if (isInterrupted())
		// return;
		if (url != null && url.length() > 0) {
			try {
				bm = ImageLoadUtil.readImg(url);
				// if (isInterrupted())
				// return;
				if (bm == null) {
					URL mUrl = new URL(url);
					HttpURLConnection conn = (HttpURLConnection) mUrl
							.openConnection();
					conn.setConnectTimeout(ConnectTimeOutTime);
					conn.setDoInput(true);
					conn.connect();
					// if (isInterrupted())
					// return;
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
		// if (isInterrupted())
		// return;
		if (bm != null && handler != null) {
			handler.process(new UrlBitmap(bm, url));
		}
		// FIXME: set url to null in order to let clear it in ThreadPool.
		url = null;
	}

	public String getUrl() {
		return url;
	}

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
