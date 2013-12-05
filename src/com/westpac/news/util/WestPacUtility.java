package com.westpac.news.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.app.NotificationManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.location.Criteria;
import android.location.Location;
import android.net.ConnectivityManager;
import android.text.TextUtils;

public class WestPacUtility {
	static String bestProvider;
	static Location m_location;
	static boolean location_flag = true;
	static Criteria criteria;
    static NotificationManager mNotificationManager = null;
    static Location mLocation = null;
    private static Context mContext;
    private static Resources mResources;
    
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public static void init(Context context1) {
        mContext = context1;
        mResources = mContext.getResources();
    }

	public static boolean isCapitalCharacter(String letter) {
		return (letter.compareTo("Z") <= 0 && letter.compareTo("A") >= 0);
	}

	public static Bitmap zoomImage(Bitmap image, float max_size) {
	    if(image == null){
	        return null;
	    }
		int width = image.getWidth();
		int height = image.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidth = max_size / image.getWidth();
		float scaleHeight = max_size / image.getHeight();
		float properScale;
		if (scaleWidth < 1.0 || scaleHeight < 1.0) {
			if (scaleWidth < scaleHeight) {
				properScale = scaleWidth;
			} else {
				properScale = scaleHeight;
			}
		} else {
			properScale = 1.0f;
		}
		matrix.postScale(properScale, properScale);
		Bitmap bitmap = Bitmap.createBitmap(image, 0, 0, width, height, matrix,
				true);
		return bitmap;

	}

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

	// used to cut text to a limited length end with "..."
	public static String getLimitedText(String text, int length) {
		if (text.length() <= length) {
			return text;
		}
		int offset = 0;
		int left_length = length - 3;
		if (!(left_length % 2 == 0)) {
			offset = (left_length - 1) / 2;
		} else {
			offset = left_length / 2;
		}
		String frontname = text.subSequence(0, offset) + "...";
		String backname = text.subSequence(text.length() - offset,
				text.length()).toString();
		return frontname + backname;
	}

	public static String getLocaleDecimal(long num) {
		Locale loc = Locale.getDefault();
		NumberFormat df;
		df = (DecimalFormat) DecimalFormat.getNumberInstance(loc);
		return df.format(num);
	}
	
    public static Number parseLocaleDecimal(String decimal)
            throws ParseException {
        Locale loc = Locale.getDefault();
        NumberFormat df = (DecimalFormat) DecimalFormat.getNumberInstance(loc);
        return df.parse(decimal);
    }


    public static boolean checkNetworkAccessable(Context context) {
    	boolean networkAccessable = false;
        ConnectivityManager cwjManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cwjManager.getActiveNetworkInfo() == null
                || !cwjManager.getActiveNetworkInfo().isAvailable()) {
        	networkAccessable = false;
        } else {
        	networkAccessable = true;
        }
        return networkAccessable;
    }
	
    public static String DateFormat(long millisecond, boolean isMilli) {
        return sdf.format(isMilli ? millisecond : millisecond * 1000);
    }
    
    public static String generateJSONOpt(int[] app_ids, boolean[] allows) {
        StringBuffer sb = new StringBuffer("{");
        int minLength = Math.min(app_ids.length, allows.length);
        for (int i = 0; i < minLength; i++) {
            if (i != 0) {
                sb.append(",");
            }
            sb.append("\"");
            sb.append(app_ids[i]);
            sb.append("\":");
            sb.append(allows[i]);
        }
        sb.append("}");
        return sb.toString();
    }
    
    
    /**
     * getStringFromId
     * @param srcid
     * @return
     */
    public static String getStringFromId(int srcid){
        return mResources.getString(srcid);
    }
    
    /**
     * getStringFromIdWithParams
     * @param srcid
     * @param params
     * @return
     */
    public static String getStringFromIdWithParams(int srcid, Object... params) {
        return mResources.getString(srcid, params);
    }
    
    /**
     * getStringArrayFromId
     * @param arrayid
     * @return
     */
    public static String[] getStringArrayFromId(int arrayid) {
        return mResources.getStringArray(arrayid);
    }
}
