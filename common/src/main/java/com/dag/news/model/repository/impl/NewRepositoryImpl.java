package com.dag.news.model.repository.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.dag.news.model.Categories;
import com.dag.news.model.CurrentDay;
import com.dag.news.model.Feed;
import com.dag.news.model.Language;
import com.dag.news.model.New;
import com.dag.news.model.repository.NewRepository;

@Repository
public class NewRepositoryImpl implements NewRepository {

	@Value("${page.size:20}")
	int PAGE_SIZE;

	@PersistenceContext
	private EntityManager em;

	@Override
	public New findOne(Long id) {
		return em.find(New.class, id);
	}

	public New save(New customer) {
		if (customer.getId() == null) {
			em.persist(customer);
			return customer;
		} else {
			return em.merge(customer);
		}
	}

	public New save(String title, String description, Date date, String link, CurrentDay cdate, Language language,
			Feed feed, List<Categories> cats) {

		TypedQuery<New> qry = em.createQuery("select p from New p where p.url = :d", New.class);
		qry.setParameter("d", link);

		for (New f : qry.getResultList()) {
			return f;
		}

		New nw = new New();

		nw.setTitle(title);
		nw.setDescription(description);
		nw.setDayTime(date);
		nw.setUrl(link);
		nw.setDay(cdate);
		nw.setLanguage(language);
		nw.setPlace(feed);

		// TODO
		nw.setCategories(cats);

		save(nw);

		return nw;
	}

	/**
	 * Returns all news from feed with other language than given
	 */
	@Override
	public List<New> find(Feed feed, Language lang, int pageNumber, boolean changeLang) {
		TypedQuery<New> qry = null;
		if (changeLang) {
			qry = em.createQuery("select p from New p where p.place = :d and p.language != :l", New.class);
			qry.setParameter("d", feed);
			qry.setParameter("l", lang);
			qry.setFirstResult((pageNumber - 1) * PAGE_SIZE);
			qry.setMaxResults(PAGE_SIZE);
		} else {
			qry = em.createQuery("select p from New p where p.place = :d", New.class);
			qry.setParameter("d", feed);
			qry.setFirstResult((pageNumber - 1) * PAGE_SIZE);
			qry.setMaxResults(PAGE_SIZE);
		}

		return qry.getResultList();
	}

	@Override
	public List<New> findAll(Language lang, CurrentDay day, int pageNumber) {
		TypedQuery<New> qry = null;
		qry = em.createQuery("select p from New p where p.language= :l and p.day = :d", New.class);
		qry.setParameter("l", lang);
		qry.setParameter("d", day);
		if (pageNumber >= 1) {
			qry.setFirstResult((pageNumber - 1) * PAGE_SIZE);
			qry.setMaxResults(PAGE_SIZE);
		}

		return qry.getResultList();
	}
}