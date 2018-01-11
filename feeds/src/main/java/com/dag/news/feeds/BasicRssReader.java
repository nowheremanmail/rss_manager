package com.dag.news.feeds;

import com.dag.news.bo.TempFeed;
import com.dag.news.model.Feed;

import com.rometools.rome.feed.synd.SyndFeed;

//import com.sun.syndication.feed.synd.SyndFeed;

public interface BasicRssReader {
	SyndFeed readFeed(Feed _feed) throws Exception ;
	TempFeed read(Feed _feed);
}
