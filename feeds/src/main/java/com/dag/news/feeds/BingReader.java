package com.dag.news.feeds;

import java.util.Map;

import com.dag.news.model.Feed;

public interface BingReader {
	public int read(Feed feed, int skip, Map<String, String> currentLinks);

}
