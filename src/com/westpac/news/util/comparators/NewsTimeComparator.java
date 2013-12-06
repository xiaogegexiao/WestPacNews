package com.westpac.news.util.comparators;

import java.util.Comparator;
import com.westpac.news.model.NewsFeed;

/**
 * NewsTImeComparator 
 * used to 
 * @author Xiao
 *
 */
public class NewsTimeComparator implements Comparator<NewsFeed> {
	@Override
	public int compare(NewsFeed arg0, NewsFeed arg1) {
		long dateline0 = arg0.getDateline();
		long dateline1 = arg1.getDateline();
		
		if (dateline0 > dateline1)
			return -1;
		else if (dateline0 < dateline1)
			return 1;
		return 0;
	}
}
