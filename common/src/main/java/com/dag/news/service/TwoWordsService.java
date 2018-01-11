package com.dag.news.service;

import java.util.List;
import java.util.Map;

public interface TwoWordsService {

	List<Map<String, Object>> findAll(String day, String lang);
	Map<String, List<Map<String, String>>> findDetail(String day, String lang, String word1, String word2);
}
