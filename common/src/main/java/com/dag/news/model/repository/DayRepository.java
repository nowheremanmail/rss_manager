package com.dag.news.model.repository;

import java.util.List;

import com.dag.news.model.CurrentDay;
import com.dag.news.model.Language;

public interface DayRepository
{ // extends Repository<CurrentDay, Long> {
	CurrentDay findOne ( Long id );

	CurrentDay save ( CurrentDay a );

	CurrentDay findOne ( String d );

	List < CurrentDay > findAll ( int page );

	List < CurrentDay > findAllByLang ( Language l , int page );
}