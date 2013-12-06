package com.westpac.news.asynctasks;

import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;

import com.westpac.news.MainActivity;
import com.westpac.news.R;
import com.westpac.news.internet.InternetConnection;
import com.westpac.news.model.NewsFeed;
import com.westpac.news.util.comparators.NewsTimeComparator;

/**
 * Async Task for loading newsfeed
 * @author xiao
 *
 */
public class GetNewsfeedTask extends AsyncTask<Object, Void, Void> {

	/* internet connection variable which is used to do http request */
	private InternetConnection mIC;
	
	/* parameters that will be passed in arg0 */
	private Context mContext;
	private List<NewsFeed> newsfeedslist;
	private Message msg;
	
	/* StringBuilder to get the json result */
	private StringBuilder sb;
	
	/* time comparator to sort the json result. the latest news item will be the first one */
	private NewsTimeComparator comparator;

	@Override
	protected Void doInBackground(Object... arg0) {
		comparator = new NewsTimeComparator();
		mContext = (Context) arg0[0];
		newsfeedslist = (List<NewsFeed>)arg0[1];
		msg = (Message) arg0[2];
		if (mContext == null || newsfeedslist == null || msg == null)
			return null;
		sb = new StringBuilder();
		
		/* new internet connection */
		mIC = new InternetConnection(mContext);
		/* try to get west pac news from server */
		int res = mIC.getWestPacNews(
				mContext.getResources().getString(R.string.westpac_newsfeed),
				sb);
		Bundle bd = new Bundle();
		if (res != 200) {
			/**
			 * http return code is not 200 Fail!
			 */
			bd.putBoolean(MainActivity.EXTRA_GET_NEWS_FEED_RESULT, false);
		} else {
			/**
			 * http return code is 200 OK!
			 */
			bd.putBoolean(MainActivity.EXTRA_GET_NEWS_FEED_RESULT, true);
			try {
				/* parse json result and add the newsfeed item to the list one by one */
				JSONObject joparent = new JSONObject(sb.toString());
				JSONArray ja = joparent.getJSONArray("items");
				JSONObject jo;
				for (int i = 0; i < ja.length(); i++) {
					jo = ja.getJSONObject(i);
					newsfeedslist.add(new NewsFeed(jo.getString("webHref"), jo
							.getString("identifier"), jo.getString("headLine"),
							jo.getString("slugLine"), jo.getString("dateLine"),
							jo.getString("tinyUrl"), jo.getString("type"), jo
									.getString("thumbnailImageHref")));
				}
				/**
				 * sort the newsfeed list with the time comparator.
				 * The latest news will be the first one. The oldest is the last one.
				 */
				Collections.sort(newsfeedslist, comparator);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		/**
		 * send message back to MainActivity to refresh listview
		 */
		msg.setData(bd);
		msg.sendToTarget();
		return null;
	}
}
