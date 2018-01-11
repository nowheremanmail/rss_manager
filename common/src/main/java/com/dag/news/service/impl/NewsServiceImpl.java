package com.dag.news.service.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dag.news.model.Categories;
import com.dag.news.model.CurrentDay;
import com.dag.news.model.Feed;
import com.dag.news.model.Language;
import com.dag.news.model.New;
import com.dag.news.model.TwoWordsOnNews;
import com.dag.news.model.TwoWordsOnNewsID;
import com.dag.news.model.Word;
import com.dag.news.model.repository.FeedRepository;
import com.dag.news.model.repository.NewRepository;
import com.dag.news.model.repository.TwoWordOnNewsRepository;
import com.dag.news.model.repository.WordOnNewsRepository;
import com.dag.news.service.CategoriesService;
import com.dag.news.service.DayService;
import com.dag.news.service.LanguageService;
import com.dag.news.service.NewsService;
import com.dag.news.service.WordsService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class NewsServiceImpl implements NewsService {

	static private Logger logger = LoggerFactory.getLogger(NewsServiceImpl.class);

	@Autowired
	LanguageService languageService;

	@Autowired
	NewRepository newRepository;

	@Autowired
	FeedRepository feedRepository;

	@Autowired
	WordsService wordsService;

	// WordRepository wordRepository;
	@Autowired
	WordOnNewsRepository wordOnNewsRepository;

	@Autowired
	TwoWordOnNewsRepository twoWordOnNewsRepository;

	@Autowired
	DayService dayService;

	@Autowired
	CategoriesService categoriesService;

	@Value("${news.root.folder}")
	private String rootFolderName;

	@Autowired
	private ObjectMapper jacksonObjectMapper;

	@Override
	@Transactional(value = TxType.REQUIRED)
	public void addDb(String title, String description, String link, Long feed, Date date, Language lang,
			List<String> _categories, List<String> words) {
		Feed from = feedRepository.findOne(feed);
		CurrentDay day = dayService.getOrInsert(date);
		List<Categories> categories = categoriesService.getOrInsert(lang, _categories);
		logger.info("processing [" + title + "] " + date.toGMTString() + " in " + lang.getName() + " from " + from);

		New nw = newRepository.save(title, description, date, link, day, lang, from, categories);

		updateLanguage(nw, words);
	}

	static Map<String, FileWriter> files = new HashMap<>();

	@Override
	@Transactional(value = TxType.REQUIRED)
	public void add(String title, String description, String link, Long feed, Date date, Language lang,
			List<String> _categories) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
		// we want day to show original user date
		// sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

		String day = sdf.format(date);

		// Feed from = feedRepository.findOne ( feed );
		// CurrentDay day = dayService.getOrInsert ( date );
		// List < Categories > categories = categoriesService.getOrInsert ( lang
		// , _categories );
		logger.info("processing [" + title + "] " + date.toGMTString() + " in " + lang.getName() + " from " + feed);

boolean keep = false;

		Arrays.parallelSort();

		Map<String, Object> map = new HashMap<>();
		map.put("title", title);
		map.put("description", description);
		map.put("link", link);
		map.put("date", date);
		map.put("categories", _categories);

		FileWriter writer = null;
		try {
			writer = files.get(lang.getName() + "-" + day);
			if (writer == null) {
				synchronized (files) {
					writer = files.get(lang.getName() + "-" + day);
					if (writer == null) {
						File root = new File(rootFolderName);
						File langFolder = new File(root, lang.getName());
						langFolder.mkdirs();
						File dateFile = new File(langFolder, day + ".txt");

						writer = new FileWriter(dateFile, true);

						/*if (files.size()>50) {
							for (FileWriter writerTmp : files.values()) {
								writerTmp.close();
							}
							files.clear();
						}*/

						if(keep)  files.put(lang.getName() + "-" + day, writer);
					}
				}
			}


			synchronized (writer) {
				writer.write(jacksonObjectMapper.writeValueAsString(map));
				writer.write("\n");
				writer.flush();
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		finally {
			if(!keep) try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void updateLanguage(New nw, List<String> words) {
		String title = nw.getTitle();
		Language lang = nw.getLanguage();

		Word previous = null, current = null;

		// Set<String> stopWords = Utils.stopWords(language);

		for (String tt : words) {
			// if (!Utils.isNumber(tt, lang)) {
			logger.debug("adding word [" + tt + "]");
			current = wordsService.getOrInsert(tt, nw.getLanguage());
			wordOnNewsRepository.save(nw.getLanguage(), nw.getDay(), current, nw);
			// } else {
			// logger.debug("number word [" + tt + "]");
			// current = null;
			// }
			if (previous != null && current != null) {
				twoWordOnNewsRepository.save(
						new TwoWordsOnNews(new TwoWordsOnNewsID(nw.getLanguage(), nw.getDay(), previous, current, nw)));
			}
			previous = current;
		}
	}

	@Override
	public List<New> findAll(Feed feed, Language lang, int pageNumber, boolean changeLang) {
		return newRepository.find(feed, lang, pageNumber, changeLang);
	}

	@Override
	public New find(Long id) {
		return newRepository.findOne(id);
	}

	@Override
	@Transactional(value = TxType.REQUIRED)
	public void changeLanguage(New nw, Language lang, List<String> words) {
		logger.info("reprocessing [" + nw.getTitle() + "] " + nw.getDayTime().toGMTString() + " "
				+ nw.getLanguage().getName() + " -> " + lang.getName());

		nw.setLanguage(lang);
		//
		// also we reprocess date
		String pre = nw.getDay().getDay();
		nw.setDay(dayService.getOrInsert(nw.getDayTime()));
		logger.info("changing day?? " + pre + " -> " + nw.getDayTime().toGMTString() + " as " + nw.getDay().getDay());
		//
		updateLanguage(nw, words);
		newRepository.save(nw);
	}

	@Override
	public List<New> findAll(Language lang, CurrentDay day, int pageNumber) {
		return newRepository.findAll(lang, day, pageNumber);
	}

	// @Override
	// @Transactional(value = TxType.REQUIRED)
	// public void update(New new1) {
	// newRepository.save(new1);
	// }

	@Override
	@Transactional(value = TxType.REQUIRED)
	public void updateCluster(Long id, int i) {
		New nw = newRepository.findOne(id);
		if (nw != null) {
			nw.setCluster((long) i);
			newRepository.save(nw);
		}
	}

}
