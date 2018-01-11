package com.dag.news.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dag.news.model.Categories;
import com.dag.news.model.Language;
import com.dag.news.model.repository.CategoriesRepository;
import com.dag.news.service.CategoriesService;

@Service
public class CategoriesServiceImpl implements CategoriesService {

	static private Logger logger = LoggerFactory.getLogger(CategoriesServiceImpl.class);

	@Autowired
	CategoriesRepository categoriesRepository;

	@Override
	@Transactional(value = TxType.REQUIRED)
	public Categories getOrInsert(Language l, String name) {
		if (logger.isDebugEnabled()) {
			logger.debug("check category [" + l.getName() + " " + name + "]");
		}
		Categories cat = categoriesRepository.findOne(l, name);
		if (cat == null) {
			cat = new Categories(name);
			cat.setLanguage(l);
			
			categoriesRepository.save(cat);
			
			if (logger.isDebugEnabled()) {
				logger.debug("add category [" + l.getName() + " " + name + "]");
			}
		}
		return cat;
	}

	@Override
	@Transactional(value = TxType.REQUIRED)
	public List<Categories> getOrInsert(Language l, List<String> names) {
		List<Categories> result = new ArrayList<Categories>();
		
		for (String n : names) {
			Categories cat = categoriesRepository.findOne(l, n);
			if (logger.isDebugEnabled()) {
				logger.debug("check category [" + l.getName() + " " + n + "]");
			}
			if (cat == null) {
				cat = new Categories(n);
				cat.setLanguage(l);
				
				categoriesRepository.save(cat);
				if (logger.isDebugEnabled()) {
					logger.debug("add category [" + l.getName() + " " + n + "]");
				}
			}
			result.add(cat);
		}
		
		return result;
	}


}
