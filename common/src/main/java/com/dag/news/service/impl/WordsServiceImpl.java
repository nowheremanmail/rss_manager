package com.dag.news.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.tartarus.snowball.SnowballStemmer;

import com.dag.news.model.CurrentDay;
import com.dag.news.model.Language;
import com.dag.news.model.Word;
import com.dag.news.model.repository.DayRepository;
import com.dag.news.model.repository.LanguageRepository;
import com.dag.news.model.repository.WordRepository;
import com.dag.news.service.WordsService;

@Service
public class WordsServiceImpl implements WordsService {

	@Autowired
	WordRepository wordRepository;
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

		for (Map obj : wordRepository.findAll(cd, la)) {
			Number num = (Number) obj.get("num");
			String word = (String) obj.get("word");

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

			previousList.add(word);
		}

		return result;
	}

	@Override
	public Map<String, List<Map<String, String>>> findDetail(String day, String lang, String _word) {
		CurrentDay cd = dayRepository.findOne(day);
		Language la = languageRepository.findOne(lang);

		if (cd == null)
			throw new RuntimeException("day [" + day + "] not found");
		if (la == null)
			throw new RuntimeException("language [" + lang + "] not found");

		Word wo = _word != null ? wordRepository.findOne(_word, la) : null;

		Map<String, List<Map<String, String>>> result = new HashMap<String, List<Map<String, String>>>();

		for (Map obj : wordRepository.findDetail(cd, la, wo)) {
			String word = (String) obj.get("word");
			String url = (String) obj.get("url");
			String title = (String) obj.get("title");
			String source = (String) obj.get("sourceUrl");

			List<Map<String, String>> cur = result.get(word);
			if (cur == null) {
				cur = new ArrayList<Map<String, String>>();
				result.put(word, cur);
			}
			Map<String, String> detail = new HashMap<String, String>();
			detail.put("t", title);
			detail.put("u", url);
			detail.put("s", source);
			cur.add(detail);

		}

		return result;

	}

	@Override
	public List<Map<String, String>> findAll(String lang, int page, String filter) {
		Language la = languageRepository.findOne(lang);

		if (la == null)
			throw new RuntimeException("language [" + lang + "] not found");

		List<Map<String, String>> res = new ArrayList<Map<String, String>>();

		for (Word w : wordRepository.findAll(la, page, filter)) {
			Map<String, String> detail = new HashMap<String, String>();
			detail.put("w", w.getWord());
			detail.put("c", w.getCategory());
			detail.put("l", lang);
			res.add(detail);
		}

		return res;
	}

	@Override
	@Transactional(value = TxType.REQUIRED)
	public Map<String, Object> update(String lang, String word, String category) {
		Language la = languageRepository.findOne(lang);

		if (la == null)
			throw new RuntimeException("language [" + lang + "] not found");
		Map<String, Object> res = new HashMap<String, Object>();

		Word w = wordRepository.findOne(word, la);
		if (w == null) {
			res.put("r", "KO");
			res.put("m", "not found");
		} else {
			w.setCategory(category);
			wordRepository.save(w);
			res.put("r", "OK");
			res.put("m", "");
		}
		return res;
	}


	/*
	 * Stop words https://en.wikipedia.org/wiki/Stop_words
	 * http://www.ranks.nl/stopwords/
	 * http://www.webpageanalyse.com/blog/lists-of-stop-words-in-9-languages
	 * http://99webtools.com/list-english-stop-words.php
	 * 
	 */

	// static Set<String> stopwords = null;

	@Override
	public Set<String> stopWords(Language language) {
		//System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		Set<String> words = new HashSet<String>();
		List<String> ws = new ArrayList<String>();
		ws.add("stop");

		List<Word> lws = wordRepository.findAllByCategory(language, 0, ws);

		for (Word tmp : lws) {
			words.add(tmp.getWord());
		}
		return words;
	}

	@Override
	@Transactional(value = TxType.REQUIRED)
	public Word getOrInsert(String w, Language lang) {
		return wordRepository.getOrInsert(w, lang);
	}
}
