package com.dag.news.model;

import javax.persistence.Entity;
import javax.persistence.Id;

/*
 * 
 * "create table if not exists wordsOnNews (idLanguage int NOT NULL, idDay int NOT NULL, idWord int NOT NULL, idNew integer NOT NULL, PRIMARY KEY(idLanguage, idDay, idWord, idNew))");
            r += await db.ExecuteAsync("create table if not exists twoWordsOnNews (idLanguage int NOT NULL, idDay int NOT NULL, idWord1 int NOT NULL, idWord2 int NOT NULL, idNew integer NOT NULL, PRIMARY KEY(idLanguage, idDay, idWord1, idWord2, idNew))");
            
 */
@Entity
public class CountTwoWordsOnNews {
	@Id
	CountTwoWordsOnNewsID key;
	Long countWord;

	public CountTwoWordsOnNewsID getKey() {
		return key;
	}

	public void setKey(CountTwoWordsOnNewsID key) {
		this.key = key;
	}

	public CountTwoWordsOnNews() {

	}

	public CountTwoWordsOnNews(CountTwoWordsOnNewsID key) {
		super();
		this.key = key;
	}

	public Long getCountWord() {
		return countWord;
	}

	public void setCountWord(Long countWord) {
		this.countWord = countWord;
	}
}
