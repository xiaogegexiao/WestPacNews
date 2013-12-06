package com.westpac.news.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;

/**
 * Bitmap Manager 
 * used to get the appropriate size bitmap from file system 
 * @author Xiao
 *
 */
public class BitmapManager {
	public static final int compressRatio = 60;

	/**
	 * get the proper size bitmap with maximum pixels of 500 * 500
	 * @param pathname
	 * @return
	 * @throws OutOfMemoryError
	 */
	public static Bitmap getAppropriateBitmapFromFile(String pathname)
			throws OutOfMemoryError {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(pathname, opts);
		opts.inSampleSize = computeSampleSize(opts, -1, 500 * 500);
		opts.inJustDecodeBounds = false;
		Bitmap bm = BitmapFactory.decodeFile(pathname, opts);
		return bm;
	}

	/**
	 * minSideLength indicate the minimum value of width or height.
	 * maxNumOfPixels indicates the maximum value of width*height. width*height should be divided by maxNumOfPixels with return value. 
	 * Then we can sqrt the returnvalue to get the minimum times of shrinking the image
	 * @param opts
	 * @param minSideLength
	 * @param maxNumOfPixels
	 * @return 
	 */
	private static int computeSampleSize(Options opts, int minSideLength,
			int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(opts, minSideLength,
				maxNumOfPixels);
		int roundedSize;
		if (initialSize < 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}
		return roundedSize;
	}

	/**
	 * get the initial sample size that we should shrink the bitmap to
	 * @param opts
	 * @param minSideLength
	 * @param maxNumOfPixels
	 * @return
	 */
	private static int computeInitialSampleSize(Options opts,
			int minSideLength, int maxNumOfPixels) {
		double w = opts.outWidth;
		double h = opts.outHeight;
		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBount = (minSideLength == -1) ? 128 : (int) Math.min(
				Math.floor(w / minSideLength), h / minSideLength);

		if (upperBount < lowerBound) {
			return upperBount;
		}

		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBount;
		}
	}
}
