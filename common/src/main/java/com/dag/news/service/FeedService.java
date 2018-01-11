package com.dag.news.service;

import java.util.List;
import java.util.Map;

import com.dag.news.model.Feed;
import com.dag.news.model.Language;

public interface FeedService {

	void save(Feed feed);
	void lock(Feed feed);
	void calculateNext (Feed feed);
	List<Feed> findRefresh();
	List<Feed> findInvalid();
	List<Feed> findAll(String language);
	void start(String string, boolean create);
	Feed getOrInsert(String string, Language orInsert);
	List<Map<String, Object>> feeds(String language, String filter, int page);
	Map<String, Object> addAndCheck(String url);
	void resetData(Feed feed, Language langDst, boolean changeLang);
	void updateLanguage(Feed feed, Language langDst);
	void updateLanguage(String url, String langDst);
//	Map<String, Object> findOne(String url);
	Feed findOne(String url);
	void fixStart();
	Feed findOne(long feedId);


  List<Feed> findAll();
}
