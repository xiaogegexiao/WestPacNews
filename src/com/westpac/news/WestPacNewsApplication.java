package com.westpac.news;

import android.app.Application;
import android.content.res.Resources;

public class WestPacNewsApplication extends Application {
	private static WestPacNewsApplication mApplication;

	@Override
	public void onCreate() {
		super.onCreate();
		mApplication = this;
	}

	public static WestPacNewsApplication getApplication() {
		return mApplication;
	}

	/**
	 * @return the main resources from the Application
	 */
	public static Resources getAppResources() {
		if (mApplication == null)
			return null;
		return mApplication.getResources();
	}
}
