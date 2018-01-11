package com.dag.news.model.repository;

import java.util.Date;
import java.util.List;

import com.dag.news.model.Categories;
import com.dag.news.model.CurrentDay;
import com.dag.news.model.Feed;
import com.dag.news.model.Language;
import com.dag.news.model.New;

public interface NewRepository
{// extends Repository<New, Long> {
	New findOne ( Long id );

	New save ( New a );

	New save ( String title , String description , Date date , String link , CurrentDay cdate , Language language ,
			Feed feed , List < Categories > cates );

	List < New > find ( Feed feed , Language lang , int pageNumber , boolean changeLang );

	List < New > findAll ( Language lang , CurrentDay day , int pageNumber );

}