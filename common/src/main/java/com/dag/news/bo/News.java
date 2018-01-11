package com.dag.news.bo;

import java.util.HashMap;
import java.util.Map;


public class News {
	private int id = -1;
	private String title;
	private String description;
	private String url;
	private String source;
	private String date;
	private String language;

	public News(String title, String description, String link, String language, String source, String pubdate) {
		this.title = title;
		this.description = description;
		this.url = link;
		this.source = source;
		this.language = language;
		this.date = pubdate;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}
	
	public Map<String, Object> serialize () {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		map.put("title", title);
		map.put("description", description);
		map.put("url", url);
		map.put("source", source);
		map.put("date", date);
		map.put("language", language);
		
		return map;
	}
}
