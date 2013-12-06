package com.westpac.news.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

/**
 * Util class with static methods for WestPacNews
 * @author Xiao
 *
 */
public class Util {
	/**
	 * convert pixel to density independent pixel
	 * @param context
	 * @param px
	 * @return
	 */
	public static int convertPxToDp(Context context, int px) {
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);
		float logicalDensity = metrics.density;
		int dp = Math.round(px / logicalDensity);
		return dp;
	}

	/**
	 * convert density independent pixel to normal pixel
	 * @param context
	 * @param dp
	 * @return
	 */
	public static int convertDpToPx(Context context, int dp) {
		return Math.round(TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources()
						.getDisplayMetrics()));
	}
	
	/**
	 * change the file name to which use number instead of letter character
	 * a bit of security
	 * @param url
	 * @return
	 */
	public static String changeFileName(String url) {
		if (TextUtils.isEmpty(url))
			return url;
		if (url.lastIndexOf("/") == -1 || url.lastIndexOf('.') == -1)
			return url;
		try {
			String httpurl = url.substring(0, url.lastIndexOf("/") - 1);
			String photoname = url.substring(url.lastIndexOf("/"),
					url.lastIndexOf("."));
			String suffix = url.substring(url.lastIndexOf("."));

			char[] chars = photoname.toCharArray();
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < chars.length; i++) {
				sb.append((int) chars[i]);
			}
			url = httpurl + sb.toString() + suffix;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return url;
	}
}
