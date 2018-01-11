package com.dag.news.feeds.bing;

import java.util.List;

/**
 * { "_type": "News", "readLink":
 * "https://api.cognitive.microsoft.com/api/v5/news/search?q=+", "value": [ ] }
 */

public class BingNewsAnswer {
	public String get_type() {
		return _type;
	}
 
	public void set_type(String _type) {
		this._type = _type;
	}

	public String getReadLink() {
		return readLink;
	}

	public void setReadLink(String readLink) {
		this.readLink = readLink;
	}

	public List<BingDetailedNewsAnswer> getValue() {
		return value;
	}

	public void setValue(List<BingDetailedNewsAnswer> value) {
		this.value = value;
	}

	Integer totalEstimatedMatches;
	public Integer getTotalEstimatedMatches() {
		return totalEstimatedMatches;
	}

	public void setTotalEstimatedMatches(Integer totalEstimatedMatches) {
		this.totalEstimatedMatches = totalEstimatedMatches;
	}

	String _type;
	String readLink;
	List<BingDetailedNewsAnswer> value;
	@Override
	public String toString() {
		return "BingNewsAnswer [totalEstimatedMatches=" + totalEstimatedMatches + ", _type=" + _type + ", readLink="
				+ readLink + ", value=" + value + "]";
	}
}
