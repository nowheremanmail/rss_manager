package com.dag.news.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dag.news.model.Feed;
import com.dag.news.model.Language;
import com.dag.news.model.repository.FeedRepository;
import com.dag.news.service.FeedService;
import com.dag.news.service.LanguageService;
import com.dag.news.service.NewsService;

@Service
public class FeedServiceImpl implements FeedService {

	static private Logger logger = LoggerFactory.getLogger(FeedServiceImpl.class);

	@Autowired
	FeedRepository feedRepository;
	@Autowired
	LanguageService languageService;
	@Autowired
	NewsService newsService;

	@Value("${min.ttl:5}")
	private int minTtl;

	@Value("${force.on.restart:true}")
	private boolean forceRestart;

	@Override
	@Transactional(value = TxType.REQUIRED)
	public void save(Feed feed) {
		feedRepository.save(feed);

	}

	@Override
	@Transactional(value = TxType.REQUIRED)
	public void lock(Feed feed) {
		logger.info("lock " + feed);
		// if (!feed.getError().equals(Feed.CHANGE_LANGUAGE))
		feed.setError("To process ...");
		feed.setNextUpdate(null);
		feedRepository.save(feed);

	}

	@Override
	@Transactional(value = TxType.REQUIRED)
	public void fixStart() {
		feedRepository.fixStart(forceRestart);
	}

	@Override
	@Transactional(value = TxType.REQUIRED)
	public void calculateNext(Feed feed) {
		int ttl = -1;
		if (feed.getTtl() != null && feed.getTtl() >= minTtl) {// &&
																// feed.getnextUpdate
			// != null) {
			ttl = feed.getTtl();
		} else {
			ttl = minTtl;
			logger.debug("ttl invalid or too low [" + feed.getTtl() + "]");
		}

		Calendar cal = Calendar.getInstance();
		// cal.setTime(nextUpdate);
		cal.add(Calendar.MINUTE, (int) Math.round(ttl + ((minTtl - 1) * Math.random())));
		feed.setNextUpdate(cal.getTime());

		logger.info("next update for " + feed + "(" + ttl + ")");

		feedRepository.save(feed);
	}

	@Override
	public List<Feed> findRefresh() {
		return feedRepository.findRefresh(Calendar.getInstance().getTime());
	}

	@Override
	public List<Feed> findInvalid() {
		return feedRepository.findInvalid();
	}

	@Override
	public List<Feed> findAll() {
		return feedRepository.findAll(null, null, 0);
	}

	@Override
	@Transactional(value = TxType.REQUIRED)
	public void start(String string, boolean create) {
		Feed feed = feedRepository.findOne(string);
		if (feed == null) {
			if (!create) {
				throw new RuntimeException("feed [" + string + "] not found");
			}
			feed = new Feed(string);
			feedRepository.save(feed);
			logger.info("create and start " + string);
		} else {
			logger.info("start " + string);
		}
		feed.setDisabled(false);
		feed.setLastUpdate(null);
		feed.setNextUpdate(Calendar.getInstance().getTime());
		feedRepository.save(feed);
	}

	@Override
	@Transactional(value = TxType.REQUIRED)
	public Feed getOrInsert(String string, Language orInsert) {
		Feed feed = feedRepository.findOne(string);
		if (feed != null)
			return feed;
		feed = new Feed(string);
		feed.setLanguage(orInsert);
		feedRepository.save(feed);
		return feed;
	}

	@Override
	public List<Map<String, Object>> feeds(String language, String filter, int page) {
		List<Feed> list = null;
		if ("all".equals(language)) {
			list = feedRepository.findAll(null, filter, page);
		} else {
			Language lang = languageService.find(language);

			if (lang == null) {
				throw new RuntimeException("lang [" + language + "] not found");
			}

			list = feedRepository.findAll(lang, filter, page);
		}
		List<Map<String, Object>> feeds = new ArrayList<Map<String, Object>>();
		for (Feed f : list) {
			Map<String, Object> ff = new HashMap<String, Object>();
			ff.put("u", f.getUrl());
			ff.put("t", f.getTitle());
			if (f.getLanguage() != null)
				ff.put("i", f.getLanguage().getName());
			else
				ff.put("i", "");
			ff.put("l", f.getLastUpdate());
			// devs
			ff.put("r", f.getTtl());
			ff.put("e", (f.getDisabled() == null || !f.getDisabled().booleanValue()) ? true : false);
			ff.put("d", f.getNextUpdate() != null ? f.getNextUpdate().toGMTString() : "");
			ff.put("x", f.getError() != null ? f.getError() : "");
			feeds.add(ff);
		}
		return feeds;
	}

	@Override
	@Transactional(value = TxType.REQUIRED)
	public Map<String, Object> addAndCheck(String url) {
		Map<String, Object> ff = new HashMap<String, Object>();
		Feed feed = feedRepository.findOne(url);
		if (feed != null) {
			ff.put("r", "KO");
			ff.put("m", "duplicate");
			return ff;
		}
		feed = new Feed(url);
		feed.setDisabled(false);
		feed.setNextUpdate(Calendar.getInstance().getTime());
		feedRepository.save(feed);

		ff.put("r", "OK");
		return ff;
	}

	@Override
	public List<Feed> findAll(String language) {
		Language lang = languageService.find(language);

		if (lang == null) {
			throw new RuntimeException("lang [" + language + "] not found");
		}

		return feedRepository.findAll(lang, null, -1);
	}

	@Override
	@Transactional(value = TxType.REQUIRED)
	public void resetData(Feed feed, Language langDst, boolean changeLang) {
		logger.info("deleting data for " + feed + " " + changeLang);

		int N = feedRepository.resetData(feed, langDst, changeLang);

		logger.debug("deleted data [" + N + "] for " + feed + " " + changeLang);
	}

	@Override
	@Transactional(value = TxType.REQUIRED)
	public void updateLanguage(Feed feed, Language langDst) {
		logger.info("fix feed for language change " + feed + " [" + langDst.getName() + "]");

		feed.setDisabled(false);
		feed.setError(Feed.CHANGE_LANGUAGE);
		feed.setLanguage(langDst);
		feed.setNextUpdate(Calendar.getInstance().getTime());

		feedRepository.save(feed);
	}

	@Override
	public Feed findOne(String url) {
		Map<String, Object> ff = new HashMap<String, Object>();
		return feedRepository.findOne(url);
	}

	@Override
	@Transactional(value = TxType.REQUIRED)
	public void updateLanguage(String url, String language) {
		Language langDst = languageService.find(language);

		if (langDst == null) {
			throw new RuntimeException("lang [" + language + "] not found");
		}

		Feed f = feedRepository.findOne(url);
		if (f == null) {
			throw new RuntimeException("feed [" + url + "] not found");
		}

		logger.info("fix feed for language change " + url + " [" + langDst.getName() + "]");

		f.setDisabled(false);
		f.setError("$%&CHANGELANGUAGE&%$");
		f.setLanguage(langDst);
		f.setNextUpdate(Calendar.getInstance().getTime());

		feedRepository.save(f);
	}

	@Override
	public Feed findOne(long feedId) {
		return feedRepository.findOne(feedId);
	}

	// static public void main(String[] a) {
	// Calendar cal = Calendar.getInstance();
	// // cal.setTime(nextUpdate);
	//
	// System.out.println(cal.toString());
	//
	// cal.add(Calendar.MINUTE, 10);
	//
	// System.out.println(cal.toString());
	// }
}
