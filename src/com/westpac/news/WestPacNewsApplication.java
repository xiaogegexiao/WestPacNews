package com.westpac.news;

import android.app.Application;
import android.content.res.Resources;

/**
 * Application class for this project
 * similar with a singleton class with only one instance of itself.
 * @author xiao
 *
 */
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
