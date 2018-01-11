package com.dag.news.model.repository.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.dag.news.model.Categories;
import com.dag.news.model.Language;
import com.dag.news.model.repository.CategoriesRepository;

@Repository
public class CategoriesRepositoryImpl implements CategoriesRepository {

	@Value("${page.size:20}")
	int PAGE_SIZE;

	@PersistenceContext
	private EntityManager em;

	@Override
	public Categories findOne(Long id) {
		return em.find(Categories.class, id);
	}

	public Categories save(Categories customer) {
		if (customer.getId() == null) {
			em.persist(customer);
			return customer;
		} else {
			return em.merge(customer);
		}
	}

	@Override
	public Categories findOne(Language lal, String d) {
		TypedQuery<Categories> query = em.createQuery("select c from Categories c where c.name = :p and c.language = :l", Categories.class);
		query.setParameter("p", d);
		query.setParameter("l", lal);

		for (Categories l : query.getResultList()) {
			return l;
		}

		return null;
	}
}
