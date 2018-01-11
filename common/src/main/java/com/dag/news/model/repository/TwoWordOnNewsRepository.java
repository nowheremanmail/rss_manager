package com.dag.news.model.repository;


import java.util.List;
import java.util.Map;

import com.dag.news.model.CurrentDay;
import com.dag.news.model.Language;
import com.dag.news.model.New;
import com.dag.news.model.TwoWordsOnNews;
import com.dag.news.model.Word;

public interface TwoWordOnNewsRepository {
	TwoWordsOnNews save(TwoWordsOnNews a);
	TwoWordsOnNews save(Language l, CurrentDay d, Word w1, Word w2, New nw);

//	Map<String, List<Map<String, String>>> findDetail(String day, String language, String word);
	List<Map<String, Object>> findAll(CurrentDay cd, Language la);
	
	List<Map<String, Object>> findDetail(CurrentDay d, Language lang, Word word1, Word word2);

}