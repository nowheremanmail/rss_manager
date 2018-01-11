package com.dag.news.model.repository;

import com.dag.news.model.Categories;
import com.dag.news.model.Language;

public interface CategoriesRepository
{ // extends JpaRepository<ExperimentsEntity, Integer> {extends Repository<Categories, Long> {
	Categories findOne ( Long id );

	Categories save ( Categories a );

	Categories findOne ( Language l , String d );

}