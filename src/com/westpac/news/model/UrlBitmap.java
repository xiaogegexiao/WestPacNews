package com.westpac.news.model;

import android.graphics.Bitmap;

/**
 * model class with imagebitmap and imageurl
 * @author xiao
 *
 */
public class UrlBitmap {
	private Bitmap img;
	private String url;

	public UrlBitmap(Bitmap img, String url) {
		this.img = img;
		this.url = url;
	}

	public Bitmap getImg() {
		return img;
	}

	public String getUrl() {
		return url;
	}

	public void setImg(Bitmap img) {
		this.img = img;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * In java int has 4bytes
	 * 
	 * @return
	 */
	public int getImgSize() {
		if (img != null) {
			return img.getWidth() * img.getHeight() * 4;
		}
		return 0;
	}

	/**
	 * override equals message for this class
	 */
	@Override
	public boolean equals(Object o) {
		if (o.getClass() == this.getClass())
			return this.url.equals(((UrlBitmap) o).getUrl());
		return false;
	}
}
