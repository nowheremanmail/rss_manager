package com.dag.news.model.repository.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import com.dag.news.model.CurrentDay;
import com.dag.news.model.Language;
import com.dag.news.model.New;
import com.dag.news.model.Word;
import com.dag.news.model.WordsOnNews;
import com.dag.news.model.WordsOnNewsID;
import com.dag.news.model.repository.WordOnNewsRepository;

@Repository
public class WordOnNewsRepositoryImpl implements WordOnNewsRepository {

		@PersistenceContext
		private EntityManager em;

		public WordsOnNews save(Language l, CurrentDay d, Word w, New nw) {
			WordsOnNews tmp = em.find(WordsOnNews.class, new WordsOnNewsID(l,d,w,nw));

			if(tmp== null) {
				tmp = new WordsOnNews(new WordsOnNewsID(l,d,w,nw));
				em.persist(tmp);
			}
			return tmp;
		}

		public WordsOnNews save(WordsOnNews customer) {
			if (customer.getKey() == null) {
				em.persist(customer);
				return customer;
			} else {
				return em.merge(customer);
			}
		}
		
}
