package com.dag.news.service;

import java.util.List;

import com.dag.news.model.Categories;
import com.dag.news.model.Language;

public interface CategoriesService {

	Categories getOrInsert(Language l, String name);
	List<Categories> getOrInsert(Language l, List<String> names);
}
