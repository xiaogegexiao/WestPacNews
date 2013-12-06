package com.westpac.news.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.graphics.Bitmap;

import com.westpac.news.util.ImageLoadUtil;
import com.westpac.news.util.MethodHandler;

/**
 * NewsFeed model class for every news item
 * @author xiao
 *
 */
public class NewsFeed {
	private String webHref;
	private int identifier;
	private String headLine;
	private String slugLine;
	private long dateline;
	private String tinyUrl;
	private String article;
	private String thumbnailImageHref;

	public NewsFeed(String argWebHref, String argIdentifier,
			String argHeadLine, String argSlugLine, String argDateline,
			String argTinyUrl, String argArticle, String argThumbnailImageHref) {
		webHref = argWebHref;
		identifier = Integer.valueOf(argIdentifier);
		headLine = argHeadLine;
		slugLine = argSlugLine;
		dateline = getDateTimeLongValue(argDateline);
		tinyUrl = argTinyUrl;
		article = argArticle;
		thumbnailImageHref = argThumbnailImageHref;
	}

	public String getWebHref() {
		return webHref;
	}

	public void setWebHref(String webHref) {
		this.webHref = webHref;
	}

	public int getIdentifier() {
		return identifier;
	}

	public void setIdentifier(int identifier) {
		this.identifier = identifier;
	}

	public String getHeadLine() {
		return headLine;
	}

	public void setHeadLine(String headLine) {
		this.headLine = headLine;
	}

	public String getSlugLine() {
		return slugLine;
	}

	public void setSlugLine(String slugLine) {
		this.slugLine = slugLine;
	}
	
	public String getDatelineString() {
		return getDateTimeStringValue(getDateline());
	}

	public long getDateline() {
		return dateline;
	}

	public void setDateline(long dateline) {
		this.dateline = dateline;
	}

	public String getTinyUrl() {
		return tinyUrl;
	}

	public void setTinyUrl(String tinyUrl) {
		this.tinyUrl = tinyUrl;
	}

	public String getArticle() {
		return article;
	}

	public void setArticle(String article) {
		this.article = article;
	}

	public String getThumbnailImageHref() {
		return thumbnailImageHref;
	}

	public void setThumbnailImageHref(String thumbnailImageHref) {
		this.thumbnailImageHref = thumbnailImageHref;
	}
	
	/**
	 * get Bitmap from memory and file system	
	 * @return
	 */
	public Bitmap getBitmap() {
		return ImageLoadUtil.readImg(getThumbnailImageHref());
	}
	
	/**
	 * go to load bitmap in a thread from server
	 * handler is a callback after the image has been loaded
	 * @param context
	 * @param handler
	 */
	public void getPostBitmapAsync(final Context context, final MethodHandler<UrlBitmap> handler) {
		ImageLoadUtil.readBitmapAsync(context, getThumbnailImageHref(),
				new MethodHandler<UrlBitmap>() {
					public void process(UrlBitmap para) {
						if (handler != null)
							handler.process(para);
					}
				});
	}

	public static SimpleDateFormat getSdf() {
		return sdf;
	}

	public static void setSdf(SimpleDateFormat sdf) {
		NewsFeed.sdf = sdf;
	}

	/**
	 * the format of date used in the json result
	 */
	private static SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssZ");

	/**
	 * convert time from format string to long
	 * @param dateline
	 * @return
	 */
	private static long getDateTimeLongValue(String dateline) {
		try {
			Date date = sdf.parse(dateline);
			return date.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * convert time from long to format string
	 * @param dateline
	 * @return
	 */
	private static String getDateTimeStringValue(long dateline) {
		try {
			Date date = new Date(dateline);
			String datestr = sdf.format(date);
			return datestr;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
