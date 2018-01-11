package com.dag.news.model;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

/**
 * In order to move feed into another language .... 1.- move all existing words
 * from one language to other 2.- move all TwoWordsOnNews and WordsOnNews to
 * another language 3.- Move feed to another language
 * 
 * once all feeds has been moved, it's possible to remove language (previously
 * we should remove words ...)
 * 
 * @author David
 *
 */

@Entity
@Cacheable(true)
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "language_id", "word" }) })
public class Word extends AbstractEntity {
	@NotNull
	@Column(length = 100, columnDefinition="nvarchar(100) not null")
	private String word;

	// @NotNull
	// @Column(length=100)
	// private String originalWord;

	@Column(length = 10)
	private String category;

	@ManyToOne(fetch = FetchType.LAZY)
	@NotNull
	@JoinColumn(foreignKey = @ForeignKey(name = "word_to_language"))
	private Language language;

	public Word() {

	}

	public Word(String d, Language l) {
		this.word = d;
		this.language = l;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	@Override
	public boolean checkUnique(Object obj) {
		if (this.word == null || this.language == null) {
			return false;
		}

		Word that = (Word) obj;

		return this.word.equals(that.getWord()) && this.language.equals(that.getLanguage());
	}

	// public String getOriginalWord() {
	// return originalWord;
	// }
	//
	// public void setOriginalWord(String originalWord) {
	// this.originalWord = originalWord;
	// }

}
