package com.dag.news.feeds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Component;

import com.dag.news.service.FeedService;

@Component
public class HtmlConsumer {

	static private Logger logger = LoggerFactory.getLogger(HtmlConsumer.class);

	@Autowired
	FeedService feedService;

	@ServiceActivator
	public void addHtml(String url) {

		logger.info("processing url : " + url);
		Map<String, Object> res = new HashMap<String, Object>();
		try {
			Document doc;

			// need http protocol
			doc = Jsoup.connect(url).get();

			// get page title
			String title = doc.title();
			logger.info("processing title : " + title);

			// get all links
			Elements links = doc.select("a[href]");

			List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

			for (Element link : links) {
				String tUrl = link.attr("href");
				// // get the value from href attribute
				// System.out.println("\nlink : " + );
				// System.out.println("text : " + link.text());

				if (tUrl.startsWith("http://") || tUrl.startsWith("https://")) {
					Map<String, Object> tRes = feedService.addAndCheck(tUrl);
					tRes.put("u", tUrl);
					result.add(tRes);
					
					logger.info("added " + tUrl + " -> " + tRes.get("r") + " " + tRes.get("m"));
				} else {
					Map<String, Object> tRes = new HashMap<String, Object>();
					tRes.put("r", "KO");
					tRes.put("u", tUrl);
					tRes.put("m", "invalid url");
					result.add(tRes);
					logger.info("added " + tUrl + " -> " + tRes.get("r") + " " + tRes.get("m"));
				}
			}
			res.put("r", "OK");
			res.put("l", result);

			// return res;
		} catch (Exception ex) {
			logger.warn("error [" + url + "]", ex);
			res.put("r", "KO");
			res.put("m", ex.getMessage());
			logger.info("added html " + url + " -> " + res.get("r") + " " + res.get("m"));
		}
		// return res;
	}

}
