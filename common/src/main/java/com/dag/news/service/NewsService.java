package com.dag.news.service;

import java.util.Date;
import java.util.List;

import com.dag.news.model.CurrentDay;
import com.dag.news.model.Feed;
import com.dag.news.model.Language;
import com.dag.news.model.New;

public interface NewsService
{
	void addDb(String title, String description, String link, Long News, Date date, Language lang,
			List<String> categories, List<String> words);

	void add(String title, String description, String link, Long News, Date date, Language lang,
			List<String> categories);

	List<New> findAll(Feed feed, Language lang, int pageNumber, boolean changeLang);

	New find(Long newId);

	void changeLanguage(New nw, Language find, List<String> words);

	List<New> findAll(Language lang, CurrentDay day, int i);

	// void update(New new1);

	void updateCluster(Long id, int i);

}
