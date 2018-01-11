package com.dag.news.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dag.news.model.CurrentDay;
import com.dag.news.model.Language;
import com.dag.news.model.Word;
import com.dag.news.model.repository.DayRepository;
import com.dag.news.model.repository.LanguageRepository;
import com.dag.news.model.repository.TwoWordOnNewsRepository;
import com.dag.news.model.repository.WordRepository;
import com.dag.news.service.TwoWordsService;

@Service
public class TwoWordsServiceImpl implements TwoWordsService {

	@Autowired
	WordRepository wordRepository;

	@Autowired
	TwoWordOnNewsRepository twoWordOnNewsRepository;

	@Autowired
	LanguageRepository languageRepository;
	
	@Autowired
	DayRepository dayRepository;

	@Override
	public List<Map<String, Object>> findAll(String day, String lang) {
		CurrentDay cd = dayRepository.findOne(day);
		Language la = languageRepository.findOne(lang);

		if (cd == null)
			throw new RuntimeException("day [" + day + "] not found");
		if (la == null)
			throw new RuntimeException("language [" + lang + "] not found");

		Number previousNumber = null;
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		Map<String, Object> previous = null;
		List<String> previousList = null;

		for (Map<String, Object> obj : twoWordOnNewsRepository.findAll(cd, la)) {
			Number num = (Number) obj.get("num");
			String word1 = (String) obj.get("word1");
			String word2 = (String) obj.get("word2");

			if (previousNumber == null || !previousNumber.equals(num)) {

				if (result.size() == 10)
					break;

				previousNumber = num;
				previous = new HashMap<String, Object>(2);
				previous.put("n", num);
				previousList = new ArrayList<String>();
				previous.put("l", previousList);
				result.add(previous);
			}

			previousList.add(word1+"|"+word2);
		}

		return result;
	}

	@Override
	public Map<String, List<Map<String, String>>> findDetail(String day, String lang, String _word1, String _word2) {
		CurrentDay cd = dayRepository.findOne(day);
		Language la = languageRepository.findOne(lang);

		if (cd == null)
			throw new RuntimeException("day [" + day + "] not found");
		if (la == null)
			throw new RuntimeException("language [" + lang + "] not found");

		Word wo1 = _word1 != null ? wordRepository.findOne(_word1, la) : null;
		Word wo2 = _word2 != null ? wordRepository.findOne(_word2, la) : null;

		Map<String, List<Map<String, String>>> result = new HashMap<String, List<Map<String, String>>>();

		for (Map<String,Object> obj : twoWordOnNewsRepository.findDetail(cd, la, wo1, wo2)) {
			String word1 = (String) obj.get("word1");
			String word2 = (String) obj.get("word2");
			String url = (String) obj.get("url");
			String title = (String) obj.get("title");
			String source = (String) obj.get("sourceUrl");

			List<Map<String, String>> cur = result.get(word1 + "|" + word2);
			if (cur == null) {
				cur = new ArrayList<Map<String, String>>();
				result.put(word1 + "|" + word2, cur);
			}
			Map<String, String> detail = new HashMap<String, String>();
			detail.put("t", title);
			detail.put("u", url);
			detail.put("s", source);
			cur.add(detail);

		}

		return result;

	}

}
