package com.dag.news.model;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

@Entity
@Cacheable(true)
public class CurrentDay extends AbstractEntity {
	@NotNull
	@Column(unique = true, length = 8)
	private String day;

	public CurrentDay() {

	}

	public CurrentDay(String d) {
		this.day = d;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String word) {
		this.day = word;
	}

	@Override
	public boolean checkUnique(Object obj) {
		if (this.day == null) {
			return false;
		}

		CurrentDay that = (CurrentDay) obj;

		return this.day.equals(that.getDay());
	}
}
