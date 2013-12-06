package com.westpac.news.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
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

/**
 * Adapter class for news list
 * @author xiao
 *
 */
public class NewsFeedItemAdapter extends BaseAdapter {
	
	private static final String TAG = "NewsFeedItemAdapter";

	/* the aspect ratio of the image in each news item */
	private static final float image_aspect_ratio = 1.5f;

	public List<NewsFeed> newsFeedItems;
	private Context context;
	private LayoutInflater inflater;
	
	/**
	 * Lazy load 
	 * isBusy is to used to indicate that whether it is in scroll or fling state currently.
	 * if isBusy is true, default img will be set to every img.
	 * otherwise load the image from memory or file system or network
	 */
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
		
		/**
		 * setpadding for every listview item
		 */
		int topbottom_padding = Util.convertDpToPx(context, 10);
		int leftright_padding = Util.convertDpToPx(context, 9);
		convertView.setPadding(leftright_padding, topbottom_padding, leftright_padding, topbottom_padding);

		/* bind ui components and set layout params for photo */
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

		/**
		 * set title with headline and content with slugline
		 */
		newsfeedtitle.setText(newsfeeditem.getHeadLine());
		newsfeedcontent.setText(newsfeeditem.getSlugLine());

		/**
		 * if this news item doesn't contain img resource, hide the imageview
		 * otherwise try to load the image.
		 */
		if (TextUtils.isEmpty(newsfeeditem.getThumbnailImageHref())
				|| "null".equals(newsfeeditem.getThumbnailImageHref())) {
			photo.setTag(null);
			photo.setVisibility(View.GONE);
		} else {
			photo.setVisibility(View.VISIBLE);
			photo.setTag(newsfeeditem.getThumbnailImageHref());
			/* default image */
			photo.setImageResource(R.drawable.whats_new);
			if (!isBusy) {
				/**
				 * in idle sate, we should load image from memory and file system first
				 */
				Bitmap bm = newsfeeditem.getBitmap();
				if (bm != null) {
					photo.setImageBitmap(bm);
				} else {
					/**
					 * if we cannot get the image from memory and file system both
					 * , we should add a new image load thread to thread pool necessarily.
					 * Method Handler will be invoked after the image has been load successfully from network 
					 */
					newsfeeditem.getPostBitmapAsync(context,
							new MethodHandler<UrlBitmap>() {
								public void process(UrlBitmap para) {
									/**
									 * call back to send a message to refresh handler
									 */
									Message msg = refreshImgHandler
											.obtainMessage(position, photo);
									refreshImgHandler.sendMessage(msg);
								}
							});
				}				
			}
		}
		return convertView;
	}

	/**
	 * refresh handler used to async-load image when the image has been downloaded from server 
	 */
	Handler refreshImgHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			ImageView iv = (ImageView) msg.obj;
			int position = msg.what;
			if (newsFeedItems == null || position >= newsFeedItems.size()
					|| position < 0)
				return;
			NewsFeed mt = newsFeedItems.get(position);
			/**
			 * must check whether the tag of imageview equals the thumbnailimagehref
			 * since a lot of situations that the tag will be changed by the view recycle mechanism of baseadapter
			 */
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
