package com.westpac.news.util.comparators;

import java.io.File;
import java.util.Comparator;

/**
 * File time comparator 
 * used to sort the images in memory.
 * when memory is about to reach the threshold, we will delete half part of images. The older half should be deleted firstly.
 * @author Xiao
 *
 */
public class FileTimeComparator implements Comparator<File> {

	public int compare(File object1, File object2) {
		long i1 = object1.lastModified();
		long i2 = object2.lastModified();
		if (i1 > i2)
			return -1;
		else if (i1 < i2)
			return 1;
		return 0;
	}
}
