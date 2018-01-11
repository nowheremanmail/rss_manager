package com.dag.news.model;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Embeddable
public class WordsOnNewsID implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5610720543682594121L;

	public WordsOnNewsID() {
	}

	public WordsOnNewsID(Language language, CurrentDay day, Word word, New news) {
		super();
		this.language = language;
		this.day = day;
		this.word = word;
		this.news = news;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@NotNull
	@JoinColumn(foreignKey = @ForeignKey(name = "words_to_language"))
	private Language language;

	@ManyToOne(fetch = FetchType.LAZY)
	@NotNull
	@JoinColumn(foreignKey = @ForeignKey(name = "words_to_day"))
	private CurrentDay day;
	@ManyToOne(fetch = FetchType.LAZY)
	@NotNull
	@JoinColumn(foreignKey = @ForeignKey(name = "words_to_word"))
	private Word word;
	@ManyToOne(fetch = FetchType.LAZY)
	@NotNull
	@JoinColumn(foreignKey = @ForeignKey(name = "words_to_news"))
	private New news;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((day == null) ? 0 : day.hashCode());
		result = prime * result + ((language == null) ? 0 : language.hashCode());
		result = prime * result + ((news == null) ? 0 : news.hashCode());
		result = prime * result + ((word == null) ? 0 : word.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WordsOnNewsID other = (WordsOnNewsID) obj;
		if (day == null) {
			if (other.day != null)
				return false;
		} else if (!day.equals(other.day))
			return false;
		if (language == null) {
			if (other.language != null)
				return false;
		} else if (!language.equals(other.language))
			return false;
		if (news == null) {
			if (other.news != null)
				return false;
		} else if (!news.equals(other.news))
			return false;
		if (word == null) {
			if (other.word != null)
				return false;
		} else if (!word.equals(other.word))
			return false;
		return true;
	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	public CurrentDay getDay() {
		return day;
	}

	public void setDay(CurrentDay day) {
		this.day = day;
	}

	public Word getWord() {
		return word;
	}

	public void setWord(Word word) {
		this.word = word;
	}

	public New getNews() {
		return news;
	}

	public void setNews(New news) {
		this.news = news;
	}
}
