package com.dag.news.boss.service;

import java.text.SimpleDateFormat;
import java.util.*;

import javax.annotation.PostConstruct;

import com.dag.news.feeds.BasicRssReader;
import com.dag.news.service.LanguageService;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.dag.news.model.Feed;
import com.dag.news.service.FeedService;

@Component
public class CheckRss {

    static private Logger logger = LoggerFactory.getLogger(CheckRss.class);

    @Autowired
    FeedService feedService;

    @Value("${force.on.reset:false}")
    private boolean forceRestart;

    @Autowired
    private BasicRssReader basicRssReader;

    @Value("${quality.factor:2}")
    private double qualityFactor;

    @Autowired
    private LanguageService languageService;

    @Value("${language.change:true}")
    boolean changeLanguage;

    //@PostConstruct
    public void start() {
        if (forceRestart) {
            logger.info("Init start ");
            feedService.fixStart();
        }
    }

    @Value("${ttl:60}")
    private int ttl;

    @Scheduled(fixedDelayString = "${ttl:60}")
    public void runAll() {
        logger.info("Check ");
        List<Feed> list = feedService.findInvalid();
        int N = list.size();

        long timeBetween = ttl * 60L * 1000L / N;

        for (Feed _feed : list) {
                logger.info("found " + _feed);
                feedCheck(_feed);
        }
    }


    private void feedCheck(Feed feed) {
        logger.info("processing " + feed.getUrl());

        try {
            SyndFeed info = basicRssReader.readFeed(feed);
            boolean good = info != null; // result != null && result.getError()
            // == null;
            String message = info != null ? "" : "feed not valid?"; // result.getMessage();
            int ttl = -1;

            /*if (good && qualityFactor > 0) {
                Set<String> words = new HashSet<String>();

                for (SyndEntry nw : info.getEntries()) {
                    if (nw.getTitle() != null) {
                        String[] w = nw.getTitle().toLowerCase().split("(\\p{Z})+");
                        words.addAll(Arrays.asList(w));
                    }
                }
                //
                // // check quality
                int N = info.getEntries().size();
                int M = words.size();

                if (good && N <= 0) {
                    message = "No news";
                    good = false;
                }
                if (good && M <= 0) {
                    message = "No words";
                    good = false;
                }
                if (good && M <= (int) Math.round(N * qualityFactor)) {
                    message = "few words " + M + "<= " + (int) Math.round(N * qualityFactor);
                    good = false;
                }
            }
*/

            if (good) {
                feed.setDescription(info.getDescription());
                feed.setTitle(info.getTitle());
                feed.setTtl(ttl);

                if (feed.getLanguage() == null) {
                    if (info.getLanguage() == null || info.getLanguage().length() <= 0) {
                        message = "no language";
                        good = false;
                    } else {
                        feed.setLanguage(languageService.getOrInsert(info.getLanguage()));
                        logger.info("set feed language [" + feed.getUrl() + "]" + info.getLanguage());
                    }
                } else {
                    if (info.getLanguage() != null && info.getLanguage().length() > 0
                            && !info.getLanguage().equalsIgnoreCase(feed.getLanguage().getName())) {
                        logger.warn("LANGUAGE_ERROR: feed[" + feed.getUrl() + "]" + info.getLanguage() + " "
                                + feed.getLanguage().getName());
                        if (changeLanguage) {
                            feed.setLanguage(languageService.getOrInsert(info.getLanguage()));
                        }
                    }
                }
            }

            if (good) {
                feed.setError("");
                feed.setDisabled(false);
                feedService.save(feed);

                logger.warn("feed update " + feed);
            } else {
                if (info != null) {
                    feed.setDescription(info.getDescription());
                    feed.setTitle(info.getTitle());
                }

                feed.setError(message);
                feedService.save(feed);

                logger.warn("feed not update " + feed);
            }
        } catch (Exception ex) {
            feed.setError("Exception: " + ex.getMessage());
            feedService.save(feed);
            logger.warn("feed not update " + feed, ex);
        }
    }
}

