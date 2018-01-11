package com.dag.news.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dag.news.model.Feed;
import com.dag.news.model.Language;
import com.dag.news.model.repository.FeedRepository;
import com.dag.news.model.repository.LanguageRepository;
import com.dag.news.service.LanguageService;

@Service
public class LanguageServiceImpl implements LanguageService {

	static private Logger logger = LoggerFactory.getLogger(LanguageServiceImpl.class);

	@Autowired
	LanguageRepository languageRepository;
	@Autowired
	FeedRepository feedRepository;

	@Override
	@Transactional(value = TxType.REQUIRED)
	public Language getOrInsert(String language) {
		String codi = language.toLowerCase().trim();

		Language lang = languageRepository.findOne(codi);

		if (lang == null) {
			logger.info("new language detected " + codi);
			lang = languageRepository.getOrInsert(codi, true);

			if (codi.matches("[a-zA-Z]{1,2}([_-][a-zA-Z]{1,2})?")) {
				Feed fed = new Feed("bing-" + lang.getName());
				fed.setTtl(60);
				fed.setDisabled(false);
				fed.setLanguage(lang);
				fed.setNextUpdate(Calendar.getInstance().getTime());
				logger.info("[" + fed.getUrl() + "] created!");
				feedRepository.save(fed);

			} else {
				logger.warn("Invalid language [" + codi + "] codi");
			}
		}
		return lang;
	}

	@Override
	public Language find(Long language) {
		return languageRepository.findOne(language);
	}

	@Override
	public Language find(String language) {
		String codi = language.toLowerCase().trim();
		return languageRepository.getOrInsert(codi, false);
	}

	@Override
	public List<String> findAll() {
		List<String> res = new ArrayList<String>();
		for (Language t : languageRepository.findAll()) {
			res.add(t.getName());
		}
		return res;
	}

}
