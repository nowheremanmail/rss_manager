package com.dag.news.service;

import java.util.List;

import com.dag.news.model.Language;

public interface LanguageService {

	Language getOrInsert(String language);

	Language find(Long language);

	Language find(String language);

	List<String> findAll();

}
