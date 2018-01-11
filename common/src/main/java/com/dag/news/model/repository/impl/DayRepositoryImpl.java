package com.dag.news.model.repository.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.dag.news.model.CurrentDay;
import com.dag.news.model.Language;
import com.dag.news.model.repository.DayRepository;

@Repository
public class DayRepositoryImpl implements DayRepository {

	@Value("${page.size:20}")
	int PAGE_SIZE;
	
	
		@PersistenceContext
		private EntityManager em;

		
		@Override
		public CurrentDay findOne(Long id) {
			return em.find(CurrentDay.class, id);
		}

	
		public CurrentDay save(CurrentDay customer) {
			if (customer.getId() == null) {
				em.persist(customer);
				return customer;
			} else {
				return em.merge(customer);
			}
		}


		@Override
		public CurrentDay findOne(String d) {
			TypedQuery<CurrentDay> query = em.createQuery("select c from CurrentDay c where c.day = :p",
					CurrentDay.class);
			query.setParameter("p", d);

			for (CurrentDay l : query.getResultList()) {
				return l;
			}
			
			return null;
		}


		@Override
		public List<CurrentDay> findAll(int pageNumber) {
			TypedQuery<CurrentDay> query = em.createQuery("select c from CurrentDay c order by c.day desc",
					CurrentDay.class);
			
			if (pageNumber > 0) {
				query.setFirstResult((pageNumber - 1) * PAGE_SIZE);
				query.setMaxResults(PAGE_SIZE);
			}

			
			return query.getResultList();
			
		}


		@Override
		public List<CurrentDay> findAllByLang(Language l, int pageNumber) {
			List<String> ws = new ArrayList<String>();
			ws.add("word");
//			and (c.key.word.category is null or c.key.word.category in (:kk))
			
			TypedQuery<CurrentDay> query = em.createQuery("select distinct cd from CurrentDay cd where exists (select 1 from WordsOnNews c "
								+ "where c.key.language = :l  and c.key.day = cd) order by cd.day desc",
					CurrentDay.class);
			query.setParameter("l", l);
			//query.setParameter("kk", ws);

			if (pageNumber > 0) {
				query.setFirstResult((pageNumber - 1) * PAGE_SIZE);
				query.setMaxResults(PAGE_SIZE);
			}

			return query.getResultList();
		}
}
