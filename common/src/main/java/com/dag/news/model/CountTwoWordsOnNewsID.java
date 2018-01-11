package com.dag.news.model;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Embeddable
public class CountTwoWordsOnNewsID implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7413630213206870518L;

	public CountTwoWordsOnNewsID() {

	}

	public CountTwoWordsOnNewsID(Language language, CurrentDay day, Word word1, Word word2) {
		super();
		this.language = language;
		this.day = day;
		this.word1 = word1;
		this.word2 = word2;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@NotNull
	@JoinColumn(foreignKey = @ForeignKey(name = "w2ords_to_language"))
	private Language language;
	@ManyToOne(fetch = FetchType.LAZY)
	@NotNull
	@JoinColumn(foreignKey = @ForeignKey(name = "w2ords_to_day"))
	private CurrentDay day;
	@ManyToOne(fetch = FetchType.LAZY)
	@NotNull
	@JoinColumn(foreignKey = @ForeignKey(name = "w2ords_to_word1"))
	private Word word1;
	@ManyToOne(fetch = FetchType.LAZY)
	@NotNull
	@JoinColumn(foreignKey = @ForeignKey(name = "w2ords_to_word2"))
	private Word word2;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((day == null) ? 0 : day.hashCode());
		result = prime * result + ((language == null) ? 0 : language.hashCode());

		result = prime * result + ((word1 == null) ? 0 : word1.hashCode());
		result = prime * result + ((word2 == null) ? 0 : word2.hashCode());
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
		CountTwoWordsOnNewsID other = (CountTwoWordsOnNewsID) obj;
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

		if (word1 == null) {
			if (other.word1 != null)
				return false;
		} else if (!word1.equals(other.word1))
			return false;
		if (word2 == null) {
			if (other.word2 != null)
				return false;
		} else if (!word2.equals(other.word2))
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

	public Word getWord1() {
		return word1;
	}

	public void setWord1(Word word1) {
		this.word1 = word1;
	}

	public Word getWord2() {
		return word2;
	}

	public void setWord2(Word word2) {
		this.word2 = word2;
	}

}
