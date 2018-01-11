package com.dag.news.model;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * Base class to derive entity classes from.
 * 
 * @author Oliver Gierke
 */
@MappedSuperclass
abstract public class AbstractEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	protected AbstractEntity() {
		id = null;
	}

	/**
	 * Returns the identifier of the entity.
	 * 
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	public abstract boolean checkUnique(Object obj);

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}

		if (this.id == null || obj == null || !(this.getClass().equals(obj.getClass()))) {
			if (obj != null && (this.getClass().equals(obj.getClass()))) {
				return checkUnique(obj);
			}
			return false;
		}

		AbstractEntity that = (AbstractEntity) obj;

		if (this.getId() == null) {
			return checkUnique(obj);
		}
		//System.out.println("CHECK");
		return this.id.equals(that.getId());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return id == null ? 0 : id.hashCode();
	}
}