package com.dag.news.model.repository.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.dag.news.model.Language;
import com.dag.news.model.repository.LanguageRepository;

@Repository
public class LanguageRepositoryImpl implements LanguageRepository {

	@PersistenceContext
	private EntityManager em;

	@Override
	public Language findOne(Long id) {
		return em.find(Language.class, id);
	}

	public Language getOrInsert(String language, boolean insert) {
		Language l = findOne(language);
		if (l != null)
			return l;

		if (insert) {
			l = new Language(language);
			save(l);
		}
		return l;
	}

	@Override
	public Language findOne(String name) {
		TypedQuery<Language> query = em.createQuery("select c from Language c where c.name = :p", Language.class);
		query.setParameter("p", name);

		for (Language l : query.getResultList()) {
			return l;
		}

		return null;
	}

	public Language save(Language customer) {
		if (customer.getId() == null) {
			em.persist(customer);
			return customer;
		} else {
			return em.merge(customer);
		}
	}

	@Override
	public List<Language> findAll() {
		List<Language> res = new ArrayList<Language>();
		TypedQuery<Language> query = em.createQuery("select c from Language c", Language.class);

		for (Language l : query.getResultList()) {
			res.add(l);
		}
		return res;
	}
}
