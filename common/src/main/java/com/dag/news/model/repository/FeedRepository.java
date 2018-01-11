package com.dag.news.model.repository;

import java.util.Date;
import java.util.List;

import com.dag.news.model.Feed;
import com.dag.news.model.Language;

//public interface FeedRepository extends CrudRepository<Feed, Long>, QueryDslPredicateExecutor<Feed>  {
//	@Query("select p from Feed p where p.nextUpdate is not null and p.nextUpdate <= ?1 order by p.nextUpdate")
//	List<Feed> findRefresh(Date date);
//}

public interface FeedRepository
{// extends Repository<Feed, Long> {

	Feed findOne ( Long id );

	Feed save ( Feed a );

	List < Feed > findRefresh ( Date date );

	Feed findOne ( String string );

	List < Feed > findAll ( Language lang , String filter , int page );

	int resetData ( Feed feed , Language langDst , boolean changeLang );

	int fixStart ( boolean force );
}