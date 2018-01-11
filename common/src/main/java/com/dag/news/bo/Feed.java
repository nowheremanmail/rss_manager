package com.dag.news.bo;

import java.util.Date;

public class Feed {
	private String url;
	private Date nextUpdate;
	private int ttl;

	public Feed (String url, int ttl, Date nextUpdate) {
		this.url = url;
		this.ttl = ttl;
		this.nextUpdate = nextUpdate;
	}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Date getNextUpdate() {
		return nextUpdate;
	}
	public void setNextUpdate(Date nextUpdate) {
		this.nextUpdate = nextUpdate;
	}
	public int getTtl() {
		return ttl;
	}
	public void setTtl(int ttl) {
		this.ttl = ttl;
	}
}
