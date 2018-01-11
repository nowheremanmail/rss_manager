package com.dag.news.feeds;

import com.dag.news.model.Feed;
import com.rometools.rome.feed.synd.SyndFeed;

public interface BasicRssReader {
	SyndFeed readFeed(Feed _feed) throws Exception ;
}
