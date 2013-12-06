package com.westpac.news;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.westpac.news.adapter.NewsFeedItemAdapter;
import com.westpac.news.asynctasks.GetNewsfeedTask;
import com.westpac.news.model.NewsFeed;
import com.westpac.news.util.FileManager;
import com.westpac.news.util.Util;

/**
 * MainActivity for news of West Pac with tile bar and listview
 * @author Xiao
 *
 */
public class MainActivity extends Activity implements OnScrollListener{

	/* msg label for getting newsfeed from server */
	private static final int MSG_GET_NEWS_FEED = 0;

	/* extra key for the boolean result of getting newsfeed */
	public static final String EXTRA_GET_NEWS_FEED_RESULT = "EXTRA_GET_NEWS_FEED_RESULT";

	/* list content of newsfeed */
	private List<NewsFeed> newsfeeds = new ArrayList<NewsFeed>();
	
	/* 
	 * UI Components in this activity
	 * */
	private RelativeLayout rl_title;
	private Button btn_refresh;
	private TextView tv_title;
	private ListView lv_newsfeed;
	
	/*
	 * listview adapter
	 */
	private NewsFeedItemAdapter newsFeedItemAdapter;

	/*
	 * progressDialog will be used when fetching data from server and ask customers to wait
	 */
	private ProgressDialog progressDialog;

	/*
	 * UI handler
	 */
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (MainActivity.this.isFinishing())
				return;
			switch (msg.what) {
			case MSG_GET_NEWS_FEED: {
				/*
				 * finish getting news feed, so dismiss progress.
				 */
				if (progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
				Bundle bd = msg.getData();
				if (bd != null) {
					/* show toast to user about the result */
					if (bd.getBoolean(EXTRA_GET_NEWS_FEED_RESULT)) {
						Toast.makeText(MainActivity.this,
								R.string.loading_success, Toast.LENGTH_SHORT)
								.show();
					} else {
						Toast.makeText(MainActivity.this,
								R.string.loading_unsuccess, Toast.LENGTH_SHORT)
								.show();
					}
				}
				/* update the listview with new data */
				newsFeedItemAdapter.notifyDataSetChanged();
				break;
			}
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		FileManager.init(getApplicationContext());
		findViews();
		setListeners();
		loadNews();
	}

	/*
	 * bind ui components from xml config file
	 */
	private void findViews() {
		rl_title = (RelativeLayout) findViewById(R.id.rl_title);
		btn_refresh = (Button) findViewById(R.id.btn_refresh);
		tv_title = (TextView) findViewById(R.id.tv_title);

		lv_newsfeed = (ListView) findViewById(R.id.lv_newsfeed);
		newsFeedItemAdapter = new NewsFeedItemAdapter(this, newsfeeds);
		lv_newsfeed.setAdapter(newsFeedItemAdapter);

		/* set title and message for progressdialog */
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle(R.string.loading);
		progressDialog.setMessage(getResources().getString(
				R.string.loading_news_feed));

		/* adjust the ui layout of title bar */
		LinearLayout.LayoutParams llparams = (LinearLayout.LayoutParams) rl_title
				.getLayoutParams();
		llparams.height = Util.convertDpToPx(this, 44);
		rl_title.setLayoutParams(llparams);

		/* adjust the ui layout of refresh button */
		RelativeLayout.LayoutParams rlparams = (RelativeLayout.LayoutParams) btn_refresh
				.getLayoutParams();
		rlparams.leftMargin = Util.convertDpToPx(this, 5);
		btn_refresh.setLayoutParams(rlparams);
		int leftright_padding = Util.convertDpToPx(this, 10);
		int topbottom_padding = Util.convertDpToPx(this, 7);
		btn_refresh.setPadding(leftright_padding, topbottom_padding,
				leftright_padding, topbottom_padding);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	/*
	 * set callback listeners to some components
	 */
	private void setListeners() {
		/* set refresh onclick callback to reload news */
		btn_refresh.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				loadNews();
			}
		});
		
		/* set on item click to listview to goto the webview */
		lv_newsfeed.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				NewsFeed item = (NewsFeed) view.getTag();
				if (item == null)
					return;
				Intent intent = new Intent(MainActivity.this, NewsWebView.class);
				/* set webview uri data */
				intent.putExtra(NewsWebView.EXTRA_DATA_URL, item.getTinyUrl());
				startActivity(intent);
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			}
		});
		
		/* set onscroll listener to listview to implements lazy-load */
		lv_newsfeed.setOnScrollListener(this);
	}

	/*
	 * start a new async task to load news
	 */
	private void loadNews() {
		newsfeeds.clear();
		newsFeedItemAdapter.notifyDataSetChanged();
		progressDialog.show();
		/* start getnewsfeedtask with these arguments*/
		new GetNewsfeedTask().execute(this, newsfeeds,
				mHandler.obtainMessage(MSG_GET_NEWS_FEED));
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		/*
		 * For Lazy load thumbnail images
		 * get current scroll state of the listview
		 * if it is in idle state, we will go to load the thumbnail images
		 * else in either scroll or fling state, we will not load the thumbnail images
		 */
		if(newsFeedItemAdapter == null){
            return;
        }
        switch (scrollState) {
        case OnScrollListener.SCROLL_STATE_IDLE:
        	newsFeedItemAdapter.setIsBusy(false);
        	newsFeedItemAdapter.notifyDataSetChanged();
            break;
        case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
        	newsFeedItemAdapter.setIsBusy(true);
            break;
        case OnScrollListener.SCROLL_STATE_FLING:
        	newsFeedItemAdapter.setIsBusy(true);
            break;
        }
	}

}
