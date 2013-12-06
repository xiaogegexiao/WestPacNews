package com.westpac.news.internet;

import android.content.Context;

/**
 * InternetConnection class for various kinds of http requests
 * @author xiao
 *
 */
public class InternetConnection {
	
	/**
	 * HttpClients for sending http request
	 */
	protected HttpClients mHc;
	private Context mContext;
	
	public InternetConnection(Context context) {
		mContext = context;
		mHc = new HttpClients(mContext); 
	}
	
	/**
	 * get west pac news from server
	 * @param westpacNewsUrl the http link for news
	 * @param sb stringbuiler used to receive json result
	 * @return http response code 
	 */
	public int getWestPacNews(String westpacNewsUrl, StringBuilder sb) {
		mHc.setURL(westpacNewsUrl);
		mHc.openConnection();
		
		String jsonresult = mHc.getJSONResult();
		if (sb != null) {
			sb.append(jsonresult);
		}
		
		return mHc.getResponseCode();
	}
}
