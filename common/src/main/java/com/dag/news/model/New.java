package com.dag.news.model;

import java.util.Collection;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
public class New extends AbstractEntity {
	@NotNull
	@Column(length = 200, columnDefinition="nvarchar(200) not null")
	private String title;

	@NotNull
	@Column(unique = true, length = 500, columnDefinition="nvarchar(500) not null")
	private String url;

	@Column(length = 2048, columnDefinition="nvarchar(2048) null")
	private String description;

	@ManyToOne(fetch = FetchType.LAZY)
	@NotNull
	@JoinColumn(foreignKey = @ForeignKey(name = "to_place"))
	private Feed place;

	@Column(length = 24)
	@NotNull
	private Date dayTime;

	@ManyToOne(fetch = FetchType.LAZY)
	@NotNull
	@JoinColumn(foreignKey = @ForeignKey(name = "new_to_language"))
	private Language language;

	@ManyToOne(fetch = FetchType.LAZY)
	@NotNull
	@JoinColumn(foreignKey = @ForeignKey(name = "new_to_day"))
	private CurrentDay day;
	
	@Column
	private Long cluster;

	public Long getCluster() {
		return cluster;
	}

	public void setCluster(Long cluster) {
		this.cluster = cluster;
	}

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "new_categories", joinColumns = { @JoinColumn(name = "new_id") }, inverseJoinColumns = {
			@JoinColumn(name = "categories_id") })
	private Collection<Categories> categories;

	public Collection<Categories> getCategories() {
		return categories;
	}

	public void setCategories(Collection<Categories> categories) {
		this.categories = categories;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		if (title != null && title.length() > 200)
			this.title = title.substring(0, 200);
		else
			this.title = title;

	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		if (url != null && url.length() > 500)
			this.url = url.substring(0, 500);
		else
			this.url = url;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		if (description != null && description.length() > 2048)
			this.description = description.substring(0, 2048);
		else
			this.description = description;
	}

	public Feed getPlace() {
		return place;
	}

	public void setPlace(Feed place) {
		this.place = place;
	}

	public Date getDayTime() {
		return dayTime;
	}

	public void setDayTime(Date dayTime) {
		this.dayTime = dayTime;
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

	@Override
	public boolean checkUnique(Object obj) {
		if (this.url == null) {
			return false;
		}

		New that = (New) obj;

		return this.url.equals(that.getUrl());
	}

}
