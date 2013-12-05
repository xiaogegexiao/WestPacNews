package com.westpac.news.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.westpac.news.R;
import com.westpac.news.model.NewsFeed;
import com.westpac.news.model.UrlBitmap;
import com.westpac.news.util.MethodHandler;
import com.westpac.news.util.Util;

public class NewsFeedItemAdapter extends BaseAdapter {
	
	private static final String TAG = "NewsFeedItemAdapter";

	private static final float image_aspect_ratio = 1.5f;

	public List<NewsFeed> newsFeedItems;
	private Context context;
	private LayoutInflater inflater;
	
	private boolean isBusy = false;

	public NewsFeedItemAdapter(Context c, List<NewsFeed> newsFeedItems) {
		this.newsFeedItems = newsFeedItems;
		this.context = c;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setIsBusy(boolean busy) {
		isBusy = busy;
	}
	
	public boolean isIsBusy() {
		return isBusy;
	}
	
	public Context getContext() {
		return this.context;
	}

	@Override
	public int getCount() {
		return newsFeedItems.size();
	}

	@Override
	public NewsFeed getItem(int arg0) {
		return newsFeedItems.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public View getView(final int position, View convertView, ViewGroup arg2) {
		NewsFeed newsfeeditem = newsFeedItems.get(position);

		final ImageView photo;
		final TextView newsfeedtitle;
		final TextView newsfeedcontent;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.newsfeeditem, null);
		}
		convertView.setTag(newsfeeditem);
		int topbottom_padding = Util.convertDpToPx(context, 10);
		int leftright_padding = Util.convertDpToPx(context, 9);
		convertView.setPadding(leftright_padding, topbottom_padding, leftright_padding, topbottom_padding);

		newsfeedtitle = (TextView) convertView
				.findViewById(R.id.tv_newsfeed_title);
		newsfeedcontent = (TextView) convertView
				.findViewById(R.id.tv_newsfeed_content);
		
		int photoright_margin = Util.convertDpToPx(context, 9);
		photo = (ImageView) convertView.findViewById(R.id.iv_newsfeed);
		LinearLayout.LayoutParams llparams = (LinearLayout.LayoutParams) photo
				.getLayoutParams();
		llparams.height = Util.convertDpToPx(context, 80);
		llparams.width = (int) (image_aspect_ratio * (float) llparams.height);
		llparams.rightMargin = photoright_margin;
		photo.setLayoutParams(llparams);

		newsfeedtitle.setText(newsfeeditem.getDatelineString());
		newsfeedcontent.setText(newsfeeditem.getSlugLine());

		if (TextUtils.isEmpty(newsfeeditem.getThumbnailImageHref())
				|| "null".equals(newsfeeditem.getThumbnailImageHref())) {
			photo.setTag(null);
			photo.setVisibility(View.GONE);
		} else {
			photo.setVisibility(View.VISIBLE);
			photo.setTag(newsfeeditem.getThumbnailImageHref());
			photo.setImageResource(R.drawable.whats_new);
			if (!isBusy) {
				Bitmap bm = newsfeeditem.getBitmap();
				if (bm != null) {
					photo.setImageBitmap(bm);
				} else {
					// set default image
					photo.setImageResource(R.drawable.whats_new);
					newsfeeditem.getPostBitmapAsync(context,
							new MethodHandler<UrlBitmap>() {
								public void process(UrlBitmap para) {
									Message msg = refreshImgHandler
											.obtainMessage(position, photo);
									refreshImgHandler.sendMessage(msg);
								}
							});
				}				
			}
			Log.d(TAG,"isBusy ===================== " + isBusy);
		}
		return convertView;
	}

	Handler refreshImgHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			ImageView iv = (ImageView) msg.obj;
			int position = msg.what;
			if (newsFeedItems == null || position >= newsFeedItems.size()
					|| position < 0)
				return;
			NewsFeed mt = newsFeedItems.get(position);
			if (iv != null
					&& iv.getTag() != null
					&& ((String) iv.getTag())
							.equals(mt.getThumbnailImageHref())) {
				iv.setImageBitmap(mt.getBitmap());
				iv.setVisibility(View.VISIBLE);

			}
		};
	};
}
