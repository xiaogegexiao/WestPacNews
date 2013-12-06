package com.westpac.news.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;

public class BitmapManager {
	public static final int compressRatio = 60;

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
	 * minSideLength 表示width 或者 height 可以到的最小的值，设置为-1 表示不设最小值，以最大值为参考
	 * maxNumOfPixels 表示最大的width和height 相乘之后的最大值，除掉 width*height 然后在sqrt， ceil之后可拿到width 和height 最小需要压缩的倍数
	 * @param opts
	 * @param minSideLength
	 * @param maxNumOfPixels
	 * @return 在8以下数值比较接近，使用移位的方式来求最近的2次幂，在8以上的数值，如果使用2次幂，会相差很远，于是就是用8的倍数的方式来计算
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

	/*
	private static int newComputeSampleSize(Options opts, int minSideLength,
			int maxNumOfPixels) {
		int height = opts.outHeight;
		int width = opts.outWidth;
		int samplesize = (int) Math.ceil(Math.sqrt((height * width)
				/ maxNumOfPixels));
		return samplesize * samplesize;
	}
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

	public static void saveBitmap(Bitmap bm, String path) {
		File img = new File(path);
		try {
			FileOutputStream fOut = null;
			fOut = new FileOutputStream(img);
			bm.compress(Bitmap.CompressFormat.JPEG, compressRatio, fOut);
			fOut.flush();
			fOut.close();
		} catch (Exception e) {
		}
	}

	public static class CompressedBitmap {
		private Bitmap compressedBitmap;
		private int sampleSize;

		public CompressedBitmap(Bitmap bt, int sample) {
			this.compressedBitmap = bt;
			this.sampleSize = sample;
		}

		public Bitmap getBitmap() {
			return compressedBitmap;
		}

		public int getSampleSize() {
			return sampleSize;
		}

	}
}
