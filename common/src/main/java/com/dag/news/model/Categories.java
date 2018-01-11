package com.dag.news.model;

import java.util.Collection;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "name", "language_id" }) })
@Cacheable(true)
public class Categories extends AbstractEntity {

	@NotNull
	@Column(length = 100, columnDefinition="nvarchar(100) not null")
	private String name;

	@Column(length = 100 , columnDefinition="nvarchar(100) null")
	private String validName;

	@ManyToOne(fetch = FetchType.LAZY)
	@NotNull
	@JoinColumn(foreignKey = @ForeignKey(name = "category_to_language"))
	private Language language;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "new_categories", joinColumns = { @JoinColumn(name = "categories_id") }, inverseJoinColumns = {
			@JoinColumn(name = "new_id") })
	private Collection<New> news;

	public Collection<New> getNews() {
		return news;
	}

	public void setNews(Collection<New> news) {
		this.news = news;
	}

	public Categories() {

	}

	public Categories(String d) {
		this.name = d;
	}

	public String getName() {
		return name;
	}

	public void setName(String word) {
		if (word != null && word.length() > 100)
			this.name = word.substring(0, 100);
		else
			this.name = word;
	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	public String getValidName() {
		return validName;
	}

	public void setValidName(String validName) {
		this.validName = validName;
	}

	@Override
	public boolean checkUnique(Object obj) {
		if (this.name == null || this.language == null) {
			return false;
		}

		Categories that = (Categories) obj;

		return this.name.equals(that.getName()) && this.language.equals(that.getLanguage());
	}
}
