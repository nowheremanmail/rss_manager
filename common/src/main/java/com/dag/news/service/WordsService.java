package com.dag.news.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dag.news.model.Language;
import com.dag.news.model.Word;

public interface WordsService {

	List<Map<String, Object>> findAll(String day, String lang);

	Map<String, List<Map<String, String>>> findDetail(String day, String lang, String word);

	List<Map<String, String>> findAll(String language, int page, String filter);

	Map<String, Object> update(String language, String word, String category);

	Word getOrInsert(String tt, Language language);

	Set<String> stopWords(Language language);
}
