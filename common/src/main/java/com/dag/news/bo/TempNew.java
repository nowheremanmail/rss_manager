package com.dag.news.bo;

public 	class TempNew {
	String title;
	String description;
	String link;
	String pubDate;
	String language;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getPubDate() {
		return pubDate;
	}
	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public TempNew(String title, String description, String link, String pubDate, String language) {
		super();
		this.title = title;
		this.description = description;
		this.link = link;
		this.pubDate = pubDate;
		this.language = language;
	}
}