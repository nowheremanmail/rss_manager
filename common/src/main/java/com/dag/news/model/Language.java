package com.dag.news.model;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

@Entity
@Cacheable(true)
public class Language extends AbstractEntity {
	@NotNull
	@Column(unique = true, length = 50)
	private String name;

	@Column(length = 5)
	private String isoName;

	public String getIsoName() {
		return isoName;
	}

	public void setIsoName(String isoname) {
		this.isoName = isoname;
	}

	public Language() {
		super();
		this.name = "unknown";
	}

	public Language(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	// @OneToMany(mappedBy="language")
	// private List<Word> words;

	@Override
	public boolean checkUnique(Object obj) {
		if (this.name == null) {
			return false;
		}

		Language that = (Language) obj;

		return this.name.equals(that.getName());
	}

	@Override
	public String toString()
	{
		return "[lang=" + this.name+"]";
	}
}
