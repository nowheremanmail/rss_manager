package com.dag.news.model.repository;

import java.util.List;

import com.dag.news.model.Language;

public interface LanguageRepository // extends Repository < Language , Long >
{
	Language findOne ( Long id );

	Language findOne ( String name );

	Language save ( Language a );

	Language getOrInsert ( String language , boolean insert );

	List < Language > findAll ( );
}