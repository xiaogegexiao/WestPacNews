package com.westpac.news.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.Bitmap;

import com.westpac.news.model.UrlBitmap;
import com.westpac.news.util.comparators.FileTimeComparator;

/**
 * ImageBuffer class is responsible for memory image management and local file system image management
 * 2-level buffer for images
 * first level is in memory
 * second level is in local file system
 * @author Xiao
 *
 */
public class ImageBuffer {
	private static String bufferFolderPath = FileManager.ImageBufferFolder
			.getAbsolutePath();
	private static File bufferFolder = FileManager.ImageBufferFolder;

	/**
	 * memory size threshold for images, when the images size in memory exceed this value, the images in memory should be delete by half
	 */
	public final static int MaxMemorySize = (int) (10 * 1024 * 1024);
	/**
	 * file size threshold for images, when the images size in local file folder exceed this value, the images in local file folder should be delete by half
	 */
	public final static int MaxBufferSize = (int) (5 * 1024 * 1024);
	
	/* track of local file system */
	private static List<File> bufferImgs;
	/* track of local image file size */
	private static int curBufferSize;

	/* track of images in memory */
	private static List<UrlBitmap> memoryImgs;
	/* track of memory image size */
	public static int curMemorySize;

	static {
		initBuffer();
	}

	/**
	 * init both memory buffer and local file buffer
	 */
	private static void initBuffer() {
		bufferImgs = new ArrayList<File>();
		File[] imgs = bufferFolder.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")
						|| filename.endsWith(".gif")
						|| filename.endsWith(".png")
						|| filename.endsWith(".bmp"))
					return true;
				return false;
			}
		});
		
		/**
		 * get the image file size
		 */
		curBufferSize = 0;
		for (File f : imgs) {
			bufferImgs.add(f);
			curBufferSize += f.length();
		}
		Collections.sort(bufferImgs, new FileTimeComparator());
		
		/**
		 * allocate memory for images
		 */
		memoryImgs = new LinkedList<UrlBitmap>();
		curMemorySize = 0;
	}

	/*
	 * writelock for writing image to file system
	 */
	private static Object writeLock = new Object();

	/**
	 * write image to file system
	 * @param context
	 * @param url
	 * @param is
	 */
	public static void writeImg(Context context, String url, InputStream is) {
		url = Util.changeFileName(url);
		synchronized (writeLock) {
			String path = getPathFromUrl(url);
			File file = new File(path);
			if (file.exists())
				return;
			byte[] buffer = new byte[1024];
			/**
			 * attention: here we should check the exist of the local file folder in case user delete it by wrong operation
			 */
			if (FileManager.checkImageBufferFolderExist(context)) {
				bufferFolderPath = FileManager.ImageBufferFolder
						.getAbsolutePath();
				bufferFolder = FileManager.ImageBufferFolder;
			}
			try {
				OutputStream writer = new FileOutputStream(file);
				int len;
				while ((len = is.read(buffer)) > 0)
					writer.write(buffer, 0, len);
				writer.flush();
				writer.close();
				addFile(file);
			} catch (IOException e) {
			}
		}
	}

	/**
	 * add a file to file buffer track
	 */
	private static void addFile(File file) {
		bufferImgs.add(file);
		curBufferSize += file.length();
		if (curBufferSize > MaxBufferSize)
			deleteFileByTime();
	}

	/**
	 * add a bitmap to memory buffer track
	 */
	private static void addUrlBitmap(UrlBitmap ub) {
		memoryImgs.add(ub);
		curMemorySize += ub.getImgSize();
		if (curMemorySize > MaxMemorySize)
			deleteHalfMemoryImg();
	}

	/**
	 * delete half files in buffer
	 */
	private static void deleteFileByTime() {
		int halfCount = bufferImgs.size() / 2;
		for (int i = 0; i < halfCount; i++) {
			curBufferSize -= bufferImgs.get(0).length();
			bufferImgs.get(0).delete();
			bufferImgs.remove(0);
		}
	}

	/**
	 * delete half reference in memory
	 */
	public static void deleteHalfMemoryImg() {
		int halfCount = memoryImgs.size() / 2;
		for (int i = 0; i < halfCount; i++) {
			curMemorySize -= memoryImgs.get(0).getImgSize();
			UrlBitmap u = memoryImgs.remove(0);
			u.getImg().recycle();
		}
	}

	/**
	 * readlock for reading image from memory or file
	 * used to maintain thread safety
	 */
	private static Object readLock = new Object();

	/* set maximum normal image size to 400k not gif */
	private static final int MaxSingleFileSize = 400 * 1024;
	
	/* set maximum normal gif size to 2M */
	private static final int MaxSingleGIFFileSize = 2 * 1024 * 1024;

	/***
	 * first try to read url from memory. if not exists then try to read it from
	 * buffer. if still not exists then return null.
	 */
	public static Bitmap readImg(String url) {
		if (url == null || url.length() == 0)
			return null;
		url = Util.changeFileName(url);
		try {
			synchronized (readLock) {
				UrlBitmap ub = readImgFromMem(url);
				if (ub != null) {
					return ub.getImg();
				}
				String pathName = getPathFromUrl(url);
				File file = new File(pathName);
				if (!file.exists())
					return null;
				if (file.length() > MaxSingleFileSize
						&& !pathName.toLowerCase().endsWith("gif")
						|| file.length() > MaxSingleGIFFileSize) {
					return null;
				}
				Bitmap bt = BitmapManager
						.getAppropriateBitmapFromFile(pathName);
				// Bitmap bt = BitmapFactory.decodeFile(pathName);

				if (bt != null) {
					if (bt.getWidth() <= 200 && bt.getHeight() <= 200) {
						addUrlBitmap(new UrlBitmap(bt, url));
					}
				} else {
					deleteFileFromBuffer(url);
					throw new Exception("Cannot decode " + url);
				}
				return bt;
			}
		} catch (OutOfMemoryError err) {
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * try to read img from memory
	 */
	private static UrlBitmap readImgFromMem(String url) {
		UrlBitmap res = null;
		for (UrlBitmap ub : memoryImgs)
			if (ub.getUrl().equals(url))
				res = ub;
		if (res != null) {
			memoryImgs.remove(res);
			memoryImgs.add(res);
		}
		return res;
	}

	/**
	 * delete specified file by url.
	 * 
	 * @param url
	 */
	private static void deleteFileFromBuffer(String url) {
		String name = getNameFromUrl(url);
		File file = null;
		for (File f : bufferImgs)
			if (f.getName().equals(name)) {
				file = f;
				break;
			}
		if (file != null) {
			bufferImgs.remove(file);
			curBufferSize -= file.length();
			file.delete();
		}
	}

	private static Pattern FileNamePattern = Pattern.compile("[^\\d\\w\\._]+");

	private static String getNameFromUrl(String url) {
		String res = url;
		Matcher m = FileNamePattern.matcher(res);
		res = m.replaceAll("_");
		return res;
	}

	private static String getPathFromUrl(String url) {
		return bufferFolderPath + "/" + getNameFromUrl(url);
	}

}
