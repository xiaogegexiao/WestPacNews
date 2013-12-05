package com.westpac.news.internet;

import android.content.Context;

public class InternetConnection {
	
	protected HttpClients mHc;
	private Context mContext;
	
	public static final int INTERNET_CONNECTION_FAIL = 0;
    public static final int INTERNET_CONNECTION_CANCELED = 18;
	
	public static final int CONN_TIMEOUT = 33;
	public static final int UNKNOWNHOST_ERROR = 38;
	
	public InternetConnection(Context context) {
		mContext = context;
		mHc = new HttpClients(mContext); 
	}
	
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
