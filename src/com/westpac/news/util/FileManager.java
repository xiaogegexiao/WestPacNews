package com.westpac.news.util;

import java.io.File;

import android.content.Context;
import android.os.Environment;

public class FileManager {
	public static final File EmptyFile = new File("/empty/");

	private static String FileFolderName = ".westpactemp";
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

	public static void init(Context context) {
		if (isInited)
			return;
		isInited = true;
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
