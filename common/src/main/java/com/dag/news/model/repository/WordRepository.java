package com.dag.news.model.repository;

import java.util.List;
import java.util.Map;

import com.dag.news.model.CurrentDay;
import com.dag.news.model.Language;
import com.dag.news.model.Word;

public interface WordRepository // extends Repository < Word , Long >
{
	Word findOne ( Long id );

	Word findOne ( String n , Language l );

	Word save ( Word a );

	Word getOrInsert ( String d , Language lang );

	List < Map < String , Object > > findAll ( CurrentDay d , Language lang );

	List < Map < String , Object > > findDetail ( CurrentDay d , Language lang , Word word );

	List < Word > findAll ( Language la , int page , String filter );

	List < Word > findAllByCategory ( Language lang , int pageNumber , List < String > ws );
}