package com.westpac.news.util;

import java.io.File;

import android.content.Context;
import android.os.Environment;

/**
 * local file manager 
 * used to manage the image file storage on mobile devices
 * @author Xiao
 *
 */
public class FileManager {
	/* local image file folder name set to .westpactemp
	 * this can reduce the chance of be deleted by wrong operation */
	private static String FileFolderName = ".westpactemp";
	
	/**
	 * used to check whether current device has a sdcard storage.
	 * @return
	 */
	public static boolean avaiableMedia() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}
	private static boolean hasSDCard = avaiableMedia();

	public static File ImageBufferFolder;
	private static String ImageBufferFolderName = "imgbuffer";

	private static boolean isInited = false;

	/**
	 * should be initialized for only one time
	 * @param context
	 */
	public static void init(Context context) {
		if (isInited)
			return;
		isInited = true;
		/**
		 * set the image file folder address according to the exist of sdcard
		 */
		if (hasSDCard) {
			File FileFolder = new File(
					Environment.getExternalStorageDirectory(), FileFolderName);
			if (!FileFolder.exists())
				FileFolder.mkdir();
			ImageBufferFolder = new File(
					Environment.getExternalStorageDirectory(), FileFolderName
							+ File.separator + ImageBufferFolderName);
		} else {
			ImageBufferFolder = context.getDir(ImageBufferFolderName,
					Context.MODE_PRIVATE);
		}

		if (ImageBufferFolder != null && !ImageBufferFolder.exists()) {
			ImageBufferFolder.mkdir();
		}
	}
	
	/**
	 * check whether the local image file folder exist
	 * incase user delete them by wrong operation
	 * @param context
	 * @return
	 */
	public static boolean checkImageBufferFolderExist(Context context) {
		boolean exist = true;
		if (hasSDCard) {
			File FileFolder = new File(
					Environment.getExternalStorageDirectory(), FileFolderName);
			if (!FileFolder.exists()) {
				exist = false;
				FileFolder.mkdir();
			}
			ImageBufferFolder = new File(
					Environment.getExternalStorageDirectory(), FileFolderName
							+ File.separator + ImageBufferFolderName);
		} else {
			ImageBufferFolder = context.getDir(ImageBufferFolderName,
					Context.MODE_PRIVATE);
		}

		if (ImageBufferFolder != null && !ImageBufferFolder.exists()) {
			exist = false;
			ImageBufferFolder.mkdir();
		}
		return exist;
	}
}
