package com.dag.news.model.repository.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.dag.news.model.CurrentDay;
import com.dag.news.model.Language;
import com.dag.news.model.New;
import com.dag.news.model.TwoWordsOnNews;
import com.dag.news.model.TwoWordsOnNewsID;
import com.dag.news.model.Word;
import com.dag.news.model.repository.TwoWordOnNewsRepository;

@Repository
public class TwoWordOnNewsRepositoryImpl implements TwoWordOnNewsRepository {

	@PersistenceContext
	private EntityManager em;

	public TwoWordsOnNews save(Language l, CurrentDay d, Word w1, Word w2, New nw) {
		TwoWordsOnNews tmp = em.find(TwoWordsOnNews.class, new TwoWordsOnNewsID(l, d, w1, w2, nw));

		if (tmp == null) {
			tmp = new TwoWordsOnNews(new TwoWordsOnNewsID(l, d, w1, w2, nw));
			em.persist(tmp);
		}
		return tmp;
	}

	public TwoWordsOnNews save(TwoWordsOnNews customer) {
		if (customer.getKey() == null) {
			em.persist(customer);
			return customer;
		} else {
			return em.merge(customer);
		}
	}

	@Override
	public List<Map<String, Object>> findAll(CurrentDay d, Language lang) {
		List<String> ws = new ArrayList<String>();
		ws.add("word");
		
		Query query = em
				.createQuery("select new map(c.key.word1.word as word1, c.key.word2.word as word2, count(*) as num)"
						+ "from TwoWordsOnNews c join c.key.language f " 
						+ "where f = :l and c.key.day = :cd "
						+ "and (c.key.word1.category is null or c.key.word1.category in (:kk) or c.key.word2.category is null or c.key.word2.category in (:kk)) "
						+ "group by c.key.word1.word, c.key.word2.word " 
						+ "order by 3 desc");

		query.setParameter("cd", d);
		query.setParameter("l", lang);
		query.setParameter("kk", ws);
		
		return query.getResultList();
	}

	@Override
	public List<Map<String, Object>> findDetail(CurrentDay d, Language lang, Word word1, Word word2) {
		List<String> ws = new ArrayList<String>();
		ws.add("word");
		
		Query query = null;
		if (word1 == null || word2 == null) {
			query = em.createQuery(
					"select new map(c.key.word1.word as word1, c.key.word2.word as word2, c.key.news.url as url, c.key.news.title as title, c.key.news.place.url as sourceUrl) "
							+ "from TwoWordsOnNews c join c.key.language f "
							+ "where f = :l and c.key.day = :cd "
							+ "and (c.key.word1.category is null or c.key.word1.category in (:kk) or c.key.word2.category is null or c.key.word2.category in (:kk)) "
							+ "order by 1");

			query.setParameter("cd", d);
			query.setParameter("l", lang);
			query.setParameter("kk", ws);
		} else {
			query = em.createQuery(
					"select new map(c.key.word1.word as word1, c.key.word2.word as word2, c.key.news.url as url, c.key.news.title as title, c.key.news.place.url as sourceUrl) "
							+ "from TwoWordsOnNews c join c.key.language f "
							+ "where f = :l and c.key.day = :cd and c.key.word1 = :w1 and c.key.word2 = :w2 "
							+ "order by 1");

			query.setParameter("cd", d);
			query.setParameter("l", lang);
			query.setParameter("w1", word1);
			query.setParameter("w2", word2);
		}

		return query.getResultList();
	}

}
