package com.dag.news.boss.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

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
public class RssReaderManager {

	static private Logger logger = LoggerFactory.getLogger(RssReaderManager.class);

	@Autowired
	FeedService feedService;

	@Value("${force.on.reset:false}")
	private boolean forceRestart;

	@Autowired
	MessageChannel feedToProcess;

	@PostConstruct
	public void start() {
		if (forceRestart) {
			logger.info("Init start ");
			feedService.fixStart();
		}
	}

	//@Scheduled(cron = "${cron.expression}")
	public void run() {
		logger.info("Check ");
		List<Feed> list = feedService.findRefresh();

		for (Feed _feed : list) {
			logger.info("found " + _feed);
			feedService.lock(_feed);
			
			Map<String,Object> msg = new HashMap<String, Object>();
			msg.put("id", _feed.getId());
			msg.put("operation", Feed.PROCESS);
			feedToProcess.send(new GenericMessage<Map<String,Object>>(msg));
		}
	}

	@Value("${ttl:30}")
	private int ttl;

	@Scheduled(fixedDelayString = "${ttl:30}")
	public void runAll() {
		logger.info("Check ");
		List<Feed> list = feedService.findRefresh();
		int N = list.size();

		long timeBetween = ttl*60L*1000L / N;

		for (Feed _feed : list) {
			logger.info("found " + _feed);
			feedService.lock(_feed);

			Map<String,Object> msg = new HashMap<String, Object>();
			msg.put("id", _feed.getId());
			msg.put("operation", Feed.PROCESS);
			feedToProcess.send(new GenericMessage<Map<String,Object>>(msg));

			try {
				Thread.sleep(timeBetween);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}


}
