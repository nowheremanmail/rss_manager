package com.dag.news.bo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.dag.news.model.Feed;

public class TempFeed {
	Feed feed;
	List<TempNew> news;
	String language;
	Set<String> words;
	String ttl;
	String pubDate;
	String title;
	String description;
	String message;
	Exception error;
	
	public TempFeed(Feed feed) {
		this.feed = feed;
		news = new ArrayList<TempNew>();
		words = new HashSet<String>();
		error = null;
		message = null;
	}

	public Feed getFeed() {
		return feed;
	}

	public void setFeed(Feed feed) {
		this.feed = feed;
	}

	public List<TempNew> getNews() {
		return news;
	}

	public void setNews(List<TempNew> news) {
		this.news = news;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public Set<String> getWords() {
		return words;
	}

	public void setWords(Set<String> words) {
		this.words = words;
	}

	public String getTtl() {
		return ttl;
	}

	public void setTtl(String ttl) {
		this.ttl = ttl;
	}

	public String getPubDate() {
		return pubDate;
	}

	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}

	public void addNew(TempNew a) {
		if (a.getTitle() != null) {
			String[] w = a.getTitle().toLowerCase().split("(\\p{Space})+");
			words.addAll(Arrays.asList(w));
		}

		news.add(a);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Exception getError() {
		return error;
	}

	public void setError(Exception error) {
		this.error = error;
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
}
