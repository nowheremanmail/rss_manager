package com.dag.news.boss.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dag.news.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dag.news.model.Feed;
import com.dag.news.model.Language;

@RestController
public class InfoController {

    static private Logger logger = LoggerFactory.getLogger(InfoController.class);

    @Autowired
    FeedService feedService;

    @Autowired
    DayService dayService;

    @Autowired
    WordsService wordsService;

    @Autowired
    TwoWordsService twoWordsService;

    @Autowired
    LanguageService languageService;

    @RequestMapping(method = RequestMethod.POST, value = "/addRss", produces = "application/json")
    public Map<String, Object> addRss(@RequestParam("url") String _url) {
        Map<String, Object> res = new HashMap<String, Object>();
        try {

            String[] urls = _url.split("[\r\n \t]+");
            for (String url : urls) {
                try {
                    if (url.trim().length() > 0
                            && (url.trim().startsWith("http://") || url.trim().startsWith("https://"))) {
                        Map<String, Object> resTmp = feedService.addAndCheck(url.trim());
                        res.put(url.trim(), resTmp.get("r"));
                    }
                } catch (Exception ex) {
                    logger.warn("error [" + _url + "]", ex);
                    res.put(url.trim(), ex.getMessage());
                }
            }
        } catch (Exception ex) {
            logger.warn("error [" + _url + "]", ex);
            res.put("r", "KO");
            res.put("m", ex.getMessage());
        }
        return res;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/addHtml", produces = "application/json")
    public Map<String, Object> addHtml(String url) {

        Map<String, Object> res = new HashMap<String, Object>();
        try {
            logger.info("received html " + url);
            for (String tmpUrl : Utils.extractUrlsFromHtml(url)) {
                Map<String, Object> resTmp = feedService.addAndCheck(tmpUrl.trim());
                res.put(tmpUrl.trim(), resTmp.get("r"));
            }

            return res;
        } catch (Exception ex) {
            logger.warn("error [" + url + "]", ex);
            res.put("r", "KO");
            res.put("m", ex.getMessage());
        }
        return res;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{language}/feeds", produces = "application/json")
    public List<Map<String, Object>> feeds(@PathVariable String language, @RequestParam String filter,
                                           @RequestParam int page) {
        return feedService.feeds(language, filter, page);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{language}/days", produces = "application/json")
    public List<String> days(@PathVariable String language, @RequestParam int page) {
        return dayService.days(language, page);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{language}/words", produces = "application/json")
    public List<Map<String, String>> words(@PathVariable String language, @RequestParam int page,
                                           @RequestParam String filter) {
        return wordsService.findAll(language, page, filter);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/languages", produces = "application/json")
    public List<String> languages() {
        return languageService.findAll();
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{language}/{day}/top", produces = "application/json")
    public List<Map<String, Object>> top(@PathVariable String language, @PathVariable String day) {

        return wordsService.findAll(day, language);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{language}/{day}/2top", produces = "application/json")
    public List<Map<String, Object>> top2(@PathVariable String language, @PathVariable String day) {

        return twoWordsService.findAll(day, language);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{language}/{day}/detail", produces = "application/json")
    public List<Map<String, String>> detail(@PathVariable String language, @PathVariable String day,
                                            @RequestParam String word) {
        try {
            return wordsService.findDetail(day, language, word).get(word);
        } catch (Exception ex) {
            logger.error("error", ex);
            return null;
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{language}/{day}/2detail", produces = "application/json")
    public List<Map<String, String>> detail2(@PathVariable String language, @PathVariable String day,
                                             @RequestParam String word) {
        try {
            String[] words = word.split("\\|");
            if (words.length <= 1) {
                return wordsService.findDetail(day, language, word).get(word);
            }
            return twoWordsService.findDetail(day, language, words[0], words[1]).get(word);
        } catch (Exception ex) {
            logger.error("error", ex);
            return null;
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{language}/{day}/details", produces = "application/json")
    public Map<String, List<Map<String, String>>> details(@PathVariable String language,
                                                          @PathVariable String day) {

        return wordsService.findDetail(day, language, null);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{language}/{day}/2details", produces = "application/json")
    public Map<String, List<Map<String, String>>> details2(@PathVariable String language,
                                                           @PathVariable String day) {

        return twoWordsService.findDetail(day, language, null, null);
    }

    @RequestMapping(method = RequestMethod.POST, value = "{language}/updateWord", produces = "application/json")
    public Map<String, Object> updateWord(@PathVariable String language, String word, String category) {
        try {
            return wordsService.update(language, word, category);
        } catch (Exception ex) {
            logger.warn("error [" + language + "-" + word + "-" + category + "]", ex);
            Map<String, Object> res = new HashMap<String, Object>();
            res.put("r", "KO");
            res.put("m", ex.getMessage());
            return res;
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "{language}/relaunch", produces = "application/json")
    public Map<String, Object> relaunchAll(@PathVariable String language) {
        try {
            List<Feed> list = feedService.findAll(language);

            for (Feed feed : list) {

                feedService.start(feed.getUrl(), false);
            }

            Map<String, Object> res = new HashMap<String, Object>();
            res.put("r", "OK");
            res.put("m", "relaunch " + list.size());
            return res;

        } catch (Exception ex) {
            logger.warn("error [" + language + "]", ex);
            Map<String, Object> res = new HashMap<String, Object>();
            res.put("r", "KO");
            res.put("m", ex.getMessage());
            return res;
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/updateFeed", produces = "application/json")
    public Map<String, Object> relaunch(String url, String oper, String param) {
        Map<String, Object> res = new HashMap<String, Object>();
        try {
            Feed feed = feedService.findOne(url);

            if (feed != null) {
                if ("start".equals(oper)) {
                    if ((feed.getDisabled() != null && feed.getDisabled().booleanValue())
                            || feed.getNextUpdate() != null) {
                        logger.info("starting " + feed);

                        feed.setDisabled(false);

                        feedService.save(feed);
                        Map<String, Object> msg = new HashMap<String, Object>();
                        msg.put("id", feed.getId());
                        msg.put("operation", Feed.PROCESS);
                    } else
                        logger.warn("impossible starting " + feed);

                } else if ("language".equals(oper)) {
                    if (url.startsWith("http://") || url.startsWith("https://")) {

                        Language langDst = languageService.find(param);
                        if (langDst == null) {
                            res.put("r", "KO");
                            res.put("m", "language [" + param + "] doesn't exists");
                            return res;
                        }

                        logger.info("changing language to " + param + " " + feed);
                        feed.setDisabled(false);

                        feed.setLanguage(langDst);
                        feedService.save(feed);
                        Map<String, Object> msg = new HashMap<String, Object>();
                        msg.put("id", feed.getId());
                        msg.put("operation", Feed.CHANGE_LANGUAGE);
                        msg.put("language", langDst.getId());

                    } else
                        logger.warn("impossible changing language to " + param + " " + feed);

                } else if ("disable".equals(oper)) {
                    if (feed.getDisabled() != null && !feed.getDisabled().booleanValue()
                            && feed.getNextUpdate() != null) {
                        logger.info("disable " + param + " " + feed);
                        feed.setDisabled(true);
                        feedService.save(feed);

                        res.put("r", "OK");
                        res.put("m", "feed [" + url + "] processed");
                    } else
                        logger.warn("impossible to disable " + param + " " + feed);
                }
                res.put("r", "OK");
            } else {
                res.put("r", "KO");
                res.put("m", "url [" + url + "] doesn't exist");
            }
        } catch (Exception ex) {
            logger.warn("error [" + url + "]", ex);
            res.put("r", "KO");
            res.put("m", ex.getMessage());
        }
        return res;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/feedDetails", produces = "application/json")
    public Map<String, Object> feedDetails(String url) {
        Map<String, Object> ff = new HashMap<String, Object>();
        try {
            Feed f = feedService.findOne(url);
            if (f == null) {
                throw new RuntimeException("feed [" + url + "] not found");
            }
            ff.put("u", f.getUrl());
            if (f.getLanguage() != null) {
                ff.put("i", f.getLanguage().getName());
            } else {
                ff.put("i", "");
            }
            ff.put("t", f.getTitle());
            ff.put("l", f.getLastUpdate());
            // devs
            ff.put("r", f.getTtl());
            ff.put("e", (f.getDisabled() == null || !f.getDisabled().booleanValue()) ? true : false);
            ff.put("d", f.getNextUpdate() != null ? f.getNextUpdate().toGMTString() : "");
            ff.put("x", f.getError() != null ? f.getError() : "");
        } catch (Exception ex) {
            logger.warn("error [" + url + "]", ex);
            ff.put("r", "KO");
            ff.put("m", ex.getMessage());
        }
        return ff;
    }


    @RequestMapping(method = RequestMethod.POST, value = "/changeLanguage", produces = "application/json")
    public Map<String, Object> changeLanguageFeeds(String sourceLang, String destinationLang) {
        Map<String, Object> ff = new HashMap<String, Object>();

        Language langDst = languageService.find(destinationLang);
        if (langDst == null) {
            ff.put("r", "KO");
            ff.put("m", "language [" + destinationLang + "] doesn't exists");
            return ff;
        }

        List<Feed> list = feedService.findAll(sourceLang);

        for (Feed feed : list) {
            if (feed.getUrl().startsWith("http://") || feed.getUrl().startsWith("https://")) try {
                if (!feed.getLanguage().equals(langDst)) {
                    logger.info("changing language to [" + destinationLang + "] for " + feed);

                    feed.setLanguage(langDst);
                    feedService.save(feed);

                    Map<String, Object> msg = new HashMap<String, Object>();
                    msg.put("id", feed.getId());
                    msg.put("operation", Feed.CHANGE_LANGUAGE);
                    msg.put("language", langDst.getId());

                    // feedService.updateLanguage(feed, langDst);
                } else {
                    logger.info("already language to [" + destinationLang + "] for " + feed);
                }
            } catch (Exception ex) {
                logger.warn("error processing feed " + feed, ex);
            }
        }

        return ff;
    }

}
