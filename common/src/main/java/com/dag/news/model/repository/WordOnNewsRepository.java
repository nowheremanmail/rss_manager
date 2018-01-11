package com.dag.news.model.repository;


import com.dag.news.model.CurrentDay;
import com.dag.news.model.Language;
import com.dag.news.model.New;
import com.dag.news.model.Word;
import com.dag.news.model.WordsOnNews;

public interface WordOnNewsRepository {
	WordsOnNews save(WordsOnNews a);
	WordsOnNews save(Language l, CurrentDay d, Word w, New nw) ;

}