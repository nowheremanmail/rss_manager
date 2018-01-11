package com.dag.news.model.repository.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.dag.news.model.CurrentDay;
import com.dag.news.model.Language;
import com.dag.news.model.Word;
import com.dag.news.model.repository.WordRepository;

@Repository
public class WordRepositoryImpl implements WordRepository {

	@Value("${page.size:20}")
	int PAGE_SIZE;

	@PersistenceContext
	private EntityManager em;

	@Override
	public Word findOne(Long id) {
		return em.find(Word.class, id);
	}

	public Word save(Word customer) {
		if (customer.getId() == null) {
			em.persist(customer);
			return customer;
		} else {
			return em.merge(customer);
		}
	}

	@Override
	public Word findOne(String d, Language lang) {
		TypedQuery<Word> query = em.createQuery("select c from Word c where c.word = :p and c.language = :l",
				Word.class);
		query.setParameter("p", d);
		query.setParameter("l", lang);

		for (Word l : query.getResultList()) {
			return l;
		}

		return null;
	}

	public Word getOrInsert(String d, Language lang) {
		Word l = findOne(d, lang);
		if (l != null)
			return l;

		l = new Word(d, lang);
		save(l);

		return l;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> findAll(CurrentDay d, Language lang) {
		List<String> ws = new ArrayList<String>();
		ws.add("word");
		Query query = em.createQuery("select new map(c.key.word.word as word , count(*) as num)"
				+ "from WordsOnNews c join c.key.language f "
				+ "where f = :l and c.key.day = :cd and (c.key.word.category is null or c.key.word.category in (:kk)) "
				+ "group by c.key.word.word " // TODO having num > 1"
				+ "order by 2 desc");

		query.setParameter("cd", d);
		query.setParameter("l", lang);
		query.setParameter("kk", ws);

		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> findDetail(CurrentDay d, Language lang, Word word) {

		// CriteriaQuery<Account> c = em.createQuery(Account.class);
		// 02.Root<Account> account = c.from(Account.class);
		// 03.Path<Person> owner = account.get(Account_.owner);
		// 04.Path<String> name = owner.get(Person_.name);
		// 05.c.where(cb.in(name).value("X").value("Y").value("Z"));

		List<String> ws = new ArrayList<String>();
		ws.add("word");
		Query query = null;
		if (word == null) {
			query = em.createQuery(
					"select new map(c.key.word.word as word, c.key.news.url as url, c.key.news.title as title, c.key.news.place.url as sourceUrl) "
							+ "from WordsOnNews c join c.key.language f "
							+ "where f = :l and c.key.day = :cd and (c.key.word.category is null or c.key.word.category in (:kk)) "
							+ "order by 1");

			query.setParameter("cd", d);
			query.setParameter("l", lang);
			query.setParameter("kk", ws);
		} else {
			query = em.createQuery(
					"select new map(c.key.word.word as word, c.key.news.url as url, c.key.news.title as title, c.key.news.place.url as sourceUrl) "
							+ "from WordsOnNews c join c.key.language f "
							+ "where f = :l and c.key.day = :cd and c.key.word = :w "
							// + " and (c.key.word.category is null or
							// c.key.word.category in (:kk)) "
							+ "order by 1");

			query.setParameter("cd", d);
			query.setParameter("l", lang);
			// query.setParameter("kk", ws);
			query.setParameter("w", word);
		}

		return query.getResultList();
	}

	@Override
	public List<Word> findAll(Language lang, int pageNumber, String filter) {
		TypedQuery<Word> query;

		if (filter == null || filter.length() <= 0)
			query = em.createQuery("select c from Word c where c.language = :l order by c.word", Word.class);
		else {
			query = em.createQuery("select c from Word c where c.language = :l and c.word like :f order by c.word",
					Word.class);
			query.setParameter("f", "%" + filter + "%");
		}

		query.setFirstResult((pageNumber - 1) * PAGE_SIZE);
		query.setMaxResults(PAGE_SIZE);

		query.setParameter("l", lang);

		return query.getResultList();
	}

	@Override
	public List<Word> findAllByCategory(Language lang, int pageNumber, List<String> ws) {
		TypedQuery<Word> query;

		query = em.createQuery("select c from Word c where c.language = :l and c.category in (:kk) order by c.word",
				Word.class);
		query.setParameter("kk", ws);
		query.setParameter("l", lang);

		if (pageNumber >= 1) {
			query.setFirstResult((pageNumber - 1) * PAGE_SIZE);
			query.setMaxResults(PAGE_SIZE);
		}

		return query.getResultList();
	}
}
