package com.dag.news.model.repository;

import java.util.Date;
import java.util.List;

import com.dag.news.model.Feed;
import com.dag.news.model.Language;

public interface FeedRepository {// extends Repository<Feed, Long> {

    Feed findOne(Long id);

    Feed save(Feed a);

    List<Feed> findRefresh(Date date);

    List<Feed> findInvalid();

    Feed findOne(String string);

    List<Feed> findAll(Language lang, String filter, int page);

    int resetData(Feed feed, Language langDst, boolean changeLang);

    int fixStart(boolean force);
}