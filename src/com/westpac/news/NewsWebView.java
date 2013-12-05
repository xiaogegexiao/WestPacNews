package com.westpac.news;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.westpac.news.util.Util;

/**
 * NewsWebView for showing the tinyurl of each news item
 * @author Xiao
 *
 */
public class NewsWebView extends Activity {
	
	/**
	 * UI components
	 */
	private RelativeLayout rl_title;
	private Button btn_back;
	private WebView wv_content;

	/*
	 * weburl(tinyurl) for the news item
	 */
	private String weburl = null;

	/**
	 * EXTRA key for url which is passed in intent
	 */
	public static final String EXTRA_DATA_URL = "EXTRA_DATA_URL";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/* if the weburl is empty or null or cannot match the regex for http url
		 *  finish this activity and go back and tell customers the url is invalid */
		weburl = getIntent().getExtras().getString(EXTRA_DATA_URL);
		if (TextUtils.isEmpty(weburl) || !matchUrl(weburl)) {
			Toast.makeText(this, R.string.web_content_invalid,
					Toast.LENGTH_SHORT).show();
			finish();
			/* animation for exiting this activity */
			overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
			return;
		}

		setContentView(R.layout.activity_webview);
		findViews();
		setListeners();
		loadContents();
	}


	/**
	 * bind the ui components from 
	 */
	private void findViews() {
		rl_title = (RelativeLayout) findViewById(R.id.rl_title);
		btn_back = (Button) findViewById(R.id.btn_back);
		wv_content = (WebView) findViewById(R.id.wv_content);
		wv_content.setWebViewClient(new LocalWebViewClient());

		WebSettings ws = wv_content.getSettings();
		ws.setAllowContentAccess(true);
		ws.setAllowFileAccess(true);
		ws.setBuiltInZoomControls(true);
		ws.setDisplayZoomControls(true);
		ws.setJavaScriptEnabled(true);
		ws.setSupportZoom(true);

		LinearLayout.LayoutParams llparams = (LinearLayout.LayoutParams) rl_title
				.getLayoutParams();
		llparams.height = Util.convertDpToPx(this, 44);
		rl_title.setLayoutParams(llparams);

		RelativeLayout.LayoutParams rlparams = (RelativeLayout.LayoutParams) btn_back
				.getLayoutParams();
		rlparams.leftMargin = Util.convertDpToPx(this, 5);
		btn_back.setLayoutParams(rlparams);
		int leftright_padding = Util.convertDpToPx(this, 10);
		int topbottom_padding = Util.convertDpToPx(this, 7);
		btn_back.setPadding(leftright_padding, topbottom_padding,
				leftright_padding, topbottom_padding);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		wv_content.resumeTimers();
	}

	@Override
	protected void onPause() {
		super.onPause();
		wv_content.pauseTimers();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	private void setListeners() {
		btn_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				wv_content.stopLoading();
				wv_content.clearCache(true);
				finish();
				overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
			}
		});
	}

	private void loadContents() {
		wv_content.loadUrl(weburl);
	}

	private class LocalWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	}

	public boolean onKeyDown(int keyCoder, KeyEvent event) {
		if (wv_content.canGoBack() && keyCoder == KeyEvent.KEYCODE_BACK) {
			wv_content.goBack();
			return true;
		}
		return false;
	}

	private static boolean matchUrl(final String url) {
		Pattern p = Pattern.compile(urlPattern);
		Matcher m = p.matcher(url);
		return m.matches();
	}

	private static final String urlPattern = "(http|ftp|https):\\/\\/[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?^=%&amp;:/~\\+#]*[\\w\\-\\@?^=%&amp;/~\\+#])?";
}
