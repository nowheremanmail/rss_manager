package com.dag.news.model;

import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

@Cacheable(true)
@Entity
public class Feed extends AbstractEntity {
	public static final String CHANGE_LANGUAGE = "$%&CHANGELANGUAGE&%$";
	public static final String REPROCESS = "$%&REPROCESS&%$";
	public static final Object PROCESS = "$%&PROCESS&%$";

	@NotNull
	@Column(unique = true, length = 500)
	private String url;

	private Date nextUpdate;
	private Integer ttl;
	@Column(length = 200, columnDefinition="nvarchar(200) null")
	private String title;
	@Column(length = 400, columnDefinition="nvarchar(400) null")
	private String description;

	@Column(length = 50)
	private String lastUpdate;

	@Column(length = 400)
	private String error;
	
	@Column() // updatable=false
	private Boolean disabled;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(foreignKey = @ForeignKey(name = "feed_to_language"))
	private Language language;

	@Version
	@Column
	private int rowver;

	public Feed() {

	}

	public Feed(String url) {
		this.url = url;
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

	public Date getNextUpdate() {
		return nextUpdate;
	}

	public void setNextUpdate(Date nextUpdate) {
		this.nextUpdate = nextUpdate;
	}

	public Integer getTtl() {
		return ttl;
	}

	public void setTtl(Integer ttl) {
		this.ttl = ttl;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		if (description != null && description.length() > 400)
			this.description = description.substring(0, 400);
		else
			this.description = description;
	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		if (error != null && error.length() > 400)
			this.error = error.substring(0, 400);
		else
			this.error = error;
	}

	public Boolean getDisabled() {
		return disabled;
	}

	public void setDisabled(Boolean disabled) {
		this.disabled = disabled;
	}

	@Override
	public boolean checkUnique(Object obj) {
		return false;
	}

	public String getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(String lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[" + url + ", " + (nextUpdate != null ? nextUpdate.toGMTString() : "-") + ", " + language + "]");
		return sb.toString();
	}

	public int getRowver() {
		return rowver;
	}

	public void setRowver(int rowver) {
		this.rowver = rowver;
	}
}
