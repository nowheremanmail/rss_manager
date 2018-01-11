package com.dag.news.model.repository.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.dag.news.model.Feed;
import com.dag.news.model.Language;
import com.dag.news.model.repository.FeedRepository;

@Repository
public class FeedRepositoryImpl implements FeedRepository {

	@Value("${page.size:20}")
	int PAGE_SIZE;

	@PersistenceContext
	private EntityManager em;

	@Override
	public Feed findOne(Long id) {
		return em.find(Feed.class, id);
	}

	@Override
	public Feed save(Feed customer) {
		if (customer.getId() == null) {
			em.persist(customer);
			return customer;
		} else {
			return em.merge(customer);
		}
	}

	@Override
	public List<Feed> findRefresh(Date date) {
		TypedQuery<Feed> qry = em.createQuery(
				"select p from Feed p where p.nextUpdate is not null and (p.disabled is null or p.disabled = :v) and p.nextUpdate <= :d order by p.nextUpdate",
				Feed.class);
		qry.setParameter("d", date);
		qry.setParameter("v", false);
		return qry.getResultList();
	}

	@Override
	public Feed findOne(String string) {
		TypedQuery<Feed> qry = em.createQuery("select p from Feed p where p.url = :d", Feed.class);
		qry.setParameter("d", string);

		for (Feed f : qry.getResultList()) {
			return f;
		}
		return null;
	}

	@Override
	public List<Feed> findAll(Language lang, String filter, int pageNumber) {
		TypedQuery<Feed> qry;

		if (filter != null && filter.length() > 0) {
			if (lang == null) { // "all".equals(lang)) {
				qry = em.createQuery("select p from Feed p where p.url like :ll or p.error like :ll", Feed.class);
			} else {
				qry = em.createQuery(
						"select p from Feed p where p.language = :l and (p.url like :ll or p.error like :ll)",
						Feed.class);
				qry.setParameter("l", lang);
			}
			qry.setParameter("ll", "%" + filter + "%");
		} else {
			if (lang == null) { // "all".equals(lang)) {
				qry = em.createQuery("select p from Feed p", Feed.class);
			} else {
				qry = em.createQuery("select p from Feed p where p.language = :l", Feed.class);
				qry.setParameter("l", lang);
			}
		}
		if (pageNumber > 0) {
			qry.setFirstResult((pageNumber - 1) * PAGE_SIZE);
			qry.setMaxResults(PAGE_SIZE);
		}

		return qry.getResultList();
	}

	/**
	 * Remove all assignations from previous language
	 */
	@Override
	public int resetData(Feed feed, Language langDst, boolean changeLang) {
		if (changeLang) {
			Query query1 = em.createQuery(
					"DELETE FROM WordsOnNews wc WHERE wc.key.language != :l and wc.key.news in (from New nw where nw.place = :p)");
			query1.setParameter("l", langDst);
			int r1 = query1.setParameter("p", feed).executeUpdate();

			Query query2 = em.createQuery(
					"DELETE FROM TwoWordsOnNews wc WHERE wc.key.language != :l and wc.key.news in (from New nw where nw.place = :p)");
			query2.setParameter("l", langDst);
			int r2 = query2.setParameter("p", feed).executeUpdate();

			return r1 + r2;
		} else {
			Query query1 = em.createQuery(
					"DELETE FROM WordsOnNews wc WHERE wc.key.news in (from New nw where nw.place = :p)");

			int r1 = query1.setParameter("p", feed).executeUpdate();

			Query query2 = em.createQuery(
					"DELETE FROM TwoWordsOnNews wc WHERE wc.key.news in (from New nw where nw.place = :p)");
			int r2 = query2.setParameter("p", feed).executeUpdate();

			return r1 + r2;
		}
	}

	@Override
	public int fixStart(boolean force) {
		Query query1 = null;
		// rowver=0,
		if (force) {
			query1 = em.createQuery("UPDATE Feed p SET nextUpdate =:t, disabled = :v, lastUpdate = null");
		} else {
			// rowver=0,
			query1 = em.createQuery(
					"UPDATE Feed p SET nextUpdate =:t where p.nextUpdate is null and (p.disabled is null or p.disabled = :v) ");
		}
		query1.setParameter("t", Calendar.getInstance().getTime());
		query1.setParameter("v", false);
		return query1.executeUpdate();

		// TODO Auto-generated method stub
		// feed.setDisabled(false);
		// feed.setLastUpdate(null);
		// feed.setNextUpdate(Calendar.getInstance().getTime());
		// feedRepository.save(feed);
		//

	}
}
