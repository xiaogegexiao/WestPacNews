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

public class GetNewsfeedTask extends AsyncTask<Object, Void, Void> {

	private InternetConnection mIC;
	private Context mContext;
	private List<NewsFeed> newsfeedslist;
	private Message msg;
	private StringBuilder sb;
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
		
		mIC = new InternetConnection(mContext);
		int res = mIC.getWestPacNews(
				mContext.getResources().getString(R.string.westpac_newsfeed),
				sb);
		Bundle bd = new Bundle();
		if (res != 200) {
			bd.putBoolean(MainActivity.EXTRA_GET_NEWS_FEED_RESULT, false);
		} else {
			bd.putBoolean(MainActivity.EXTRA_GET_NEWS_FEED_RESULT, true);
			try {
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
				Collections.sort(newsfeedslist, comparator);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		msg.setData(bd);
		msg.sendToTarget();
		return null;
	}
}
