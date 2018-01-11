package com.dag.news.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dag.news.model.CurrentDay;
import com.dag.news.model.Language;
import com.dag.news.model.repository.DayRepository;
import com.dag.news.model.repository.LanguageRepository;
import com.dag.news.service.DayService;

@Service
public class DayServiceImpl implements DayService {

	static private Logger logger = LoggerFactory.getLogger(DayServiceImpl.class);

	@Autowired
	DayRepository dayRepository;
	@Autowired
	LanguageRepository languageRepository;

	@Override
	public CurrentDay find(String date) {
		date = date.substring(0, 8);
		return dayRepository.findOne(date);
	}

	@Override
	@Transactional(value = TxType.REQUIRED)
	public CurrentDay getOrInsert(String d) {
		d = d.substring(0, 8);
		CurrentDay l = dayRepository.findOne(d);
		if (l != null)
			return l;

		l = new CurrentDay(d);
		dayRepository.save(l);

		return l;
	}

	@Override
	@Transactional(value = TxType.REQUIRED)
	public CurrentDay getOrInsert(Date _d) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
		// we want day to show original user date
		//sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

		String d = sdf.format(_d);

		CurrentDay l = dayRepository.findOne(d);
		if (l != null)
			return l;

		l = new CurrentDay(d);
		dayRepository.save(l);

		return l;
	}

	@Override
	public List<String> days(String lang, int page) {
		List<String> days = new ArrayList<String>();

		if (lang == null) {
			for (CurrentDay t : dayRepository.findAll(page)) {
				days.add(t.getDay());
			}
		} else {
			Language l = languageRepository.findOne(lang);
			if (l == null) throw new RuntimeException("language [" + lang + "] not found");

			for (CurrentDay t : dayRepository.findAllByLang(l, page)) {
				days.add(t.getDay());
			}
		}

		return days;
	}

}
