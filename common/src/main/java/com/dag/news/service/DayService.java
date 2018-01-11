package com.dag.news.service;

import java.util.Date;
import java.util.List;

import com.dag.news.model.CurrentDay;

public interface DayService {

	CurrentDay getOrInsert(String day);
	CurrentDay getOrInsert(Date day);
	List<String> days(String lang, int page);
	CurrentDay find(String date);

}
