package com.dag.news.feeds;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

import com.dag.news.model.Feed;
import com.dag.news.model.Language;
import com.dag.news.model.New;
import com.dag.news.service.FeedService;
import com.dag.news.service.LanguageService;
import com.dag.news.service.NewsService;
import com.dag.news.service.Utils;
import com.dag.news.service.WordsService;
import com.rometools.rome.feed.synd.SyndCategory;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;

//import com.sun.syndication.feed.synd.SyndFeed;
//import com.sun.syndication.io.FeedException;

@ Component
public class FeedProcess
{

	static private Logger logger = LoggerFactory.getLogger ( FeedProcess.class );

	@ Value ( "${page.size:20}" )
	int PAGE_SIZE;

	@ Value ( "${interfaces:true}" )
	boolean interfaces;

	@ Value ( "${language.change:true}" )
	boolean changeLanguage;

	@ Value ( "${bing.reader.max:15}" )
	int bingReaderMax;

	// @Value("${days.too.old:2}")
	// int daysTooOld;

	@ Value ( "${quality.factor:2}" )
	double qualityFactor;

	@ Value ( "${bing.time.refresh:60}" )
	int bingRefresh;

	@ Value ( "${ttl.factor:2}" )
	double ttlFactor;

	@ Autowired
	MessageChannel newToProcess;

	@ Autowired
	MessageChannel updateToProcess;

	@ Autowired
	NewsService newsService;

	@ Autowired
	BasicRssReader basicRssReader;

	@ Autowired
	FeedService feedService;

	@ Autowired
	BingReader bingReader;

	@ Autowired
	WordsService wordsService;

	@ Autowired
	LanguageService languageService;

	@ ServiceActivator
	public void process ( Map < String , Object > maps )
	{
		Feed feed = null;
		try
		{
			Long feedId = ( Long ) maps.get ( "id" );
			String operation = ( String ) maps.get ( "operation" );
			Long languageId = ( Long ) maps.get ( "language" );

			feed = feedService.findOne ( feedId );

			if ( feed == null )
			{
				logger.warn ( "feed not found " + feedId );
				return;
			}

			if ( feed.getDisabled ( ) != null && feed.getDisabled ( ) && operation.equals ( Feed.PROCESS ) )
			{
				logger.warn ( "feed disabled " + feed );
				return;
			}

			if ( feed.getUrl ( ).startsWith ( "bing-" ) )
			{
				runB ( feed , operation );
			}
			else
			{
				if ( operation.equals ( Feed.CHANGE_LANGUAGE ) )
				{
					feed.setLanguage ( languageService.find ( languageId ) );
					reprocess ( feed , true );
				}
				else if ( operation.equals ( Feed.REPROCESS ) )
				{
					reprocess ( feed , false );
				}

				runRN ( feed , operation );
			}
		} catch ( Exception ex )
		{
			if ( feed != null )
			{
				// feed.setDisabled ( true );
				feed.setError ( "UNEXPECTED: " + ex.getMessage ( ) );

				if ( ! feed.getDisabled ( ) )
					feedService.calculateNext ( feed );
				else
					feedService.save ( feed );
				logger.warn ( "UNKNOW feed not update " + feed , ex );
			}
			else
			{
				logger.error ( "UNKNOW feed not update " + maps , ex );
			}
		}
	}

	private void runB ( Feed feed , String operation )
	{
		logger.info ( "processing " + feed.getUrl ( ) );

		// Feed feed = feedService.getOrInsert("bing",
		// languageService.getOrInsert("es-ES"));
		int skip = 0;
		int i = 0;
		Map < String , String > currentLinks = new HashMap < String , String > ( bingReaderMax );

		// logger.info(feed.getUrl() + " " + skip);
		while ( ( i = bingReader.read ( feed , skip , currentLinks ) ) > 0 && skip < bingReaderMax )
		{
			skip += i;
			logger.info ( " continue " + feed.getUrl ( ) + " " + skip );
		}

		currentLinks.clear ( );

		if ( i >= 0 )
		{
			feed.setTtl ( bingRefresh );
			feed.setError ( "" );
			// feed.setDisabled(false);

			feedService.calculateNext ( feed );
		}
		else
		{
			feedService.save ( feed );
		}
	}

	private void reprocess ( Feed feed , boolean changeLang )
	{
		feedService.resetData ( feed , feed.getLanguage ( ) , changeLang );

		SimpleDateFormat sdt = new SimpleDateFormat ( "yyyy-MM-dd'T'HH:mm:ss'Z'" );
		sdt.setTimeZone ( TimeZone.getTimeZone ( "UTC" ) );

		int page = 1;
		List < New > listNews = newsService.findAll ( feed , feed.getLanguage ( ) , page , changeLang );
		while ( listNews.size ( ) > 0 )
		{
			logger.info ( "processing language change [" + page + "] for " + feed );
			for ( New nw : listNews )
			{
				Map < String , Object > map = new HashMap < String , Object > ( );
				map.put ( "source" , nw.getId ( ) );
				map.put ( "language" , feed.getLanguage ( ).getId ( ) );

				Date d = nw.getDayTime ( );
				map.put ( "date" , d != null ? sdt.format ( d ) : null );

				if ( interfaces )
					updateToProcess.send ( new GenericMessage <> ( map ) );
				else
					receiveQueueUpdate ( map );
			}
			if ( listNews.size ( ) >= PAGE_SIZE )
			{
				page ++ ;
				listNews = newsService.findAll ( feed , feed.getLanguage ( ) , page , changeLang );
			}
			else
			{
				break;
			}
		}

		feed.setError ( "" );
		feedService.save ( feed );

	}

	public void receiveQueueUpdate ( Map < String , Object > map )
	{
		Long newId = ( Long ) map.get ( "source" );
		Long languageId = ( Long ) map.get ( "language" );

		try
		{
			Language lang = languageService.find ( languageId );

			if ( lang == null )
			{
				logger.info ( newId + " -> " + languageId + " lang not found!" );
				return;
			}

			New nw = newsService.find ( newId );

			List < String > words = Utils.getWords ( nw.getTitle ( ) , lang , wordsService.stopWords ( lang ) , false );

			newsService.changeLanguage ( nw , lang , words );
		} catch ( Exception ex )
		{
			logger.warn ( "impossible to process " + newId + " -> " + languageId , ex );
		}
	}

	private void runRN ( Feed feed , String operation )
	{
		logger.info ( "processing " + feed.getUrl ( ) );

		try
		{
			SyndFeed info = basicRssReader.readFeed ( feed );
			boolean good = info != null; // result != null && result.getError()
											// == null;
			String message = info != null ? "" : "feed not valid?"; // result.getMessage();
			int ttl = - 1;

			// Date pubDate = info.getPublishedDate();
			// if (good) {
			// if (pubDate == null) {
			// logger.info("date not found, looking for one inside items");
			// for (SyndEntry nw : info.getEntries()) {
			// Date d = nw.getPublishedDate();
			// if (d == null) {
			// d = nw.getUpdatedDate();
			// }
			//
			// if (d != null) {
			// logger.debug("cheking date " + d.toGMTString());
			// if (pubDate == null) {
			// pubDate = d;
			// } else if (pubDate.before(d)) {
			// pubDate = d;
			// }
			// }
			// }
			// if (pubDate != null) {
			// logger.info("selected date " + pubDate.toGMTString());
			// }
			// }
			//
			// if (pubDate == null) { // ||
			// // pubDate.after(Calendar.getInstance().getTime()))
			// // {
			// message = "invalid null date"; // [" +
			// // result.getPubDate()
			// // + "]";
			// good = false;
			// }
			// }

			// if (good) {
			// String dat = pubDate != null ? pubDate.toGMTString() : null;
			//
			// if (feed.getLastUpdate() != null && dat != null &&
			// feed.getLastUpdate().equals(dat)) {
			// feed.setError("not updated [" + feed.getLastUpdate() + "]");
			//
			// // if (ttl > 0)
			// // feed.setTtl((int) Math.round((double) ttl
			// // * ttlFactor));
			//
			// // feed.setNextUpdate(convertDate(result.getPubDate()));
			// // feed.setError("");
			// // feed.setDisabled(false);
			//
			// feedService.calculateNext(feed);
			//
			// // feedService.save(feed);
			// logger.warn("feed require no update " + feed);
			//
			// return;
			// }
			//
			// logger.info("feed updated " + feed + " [" + feed.getLastUpdate()
			// + "] vs [" + dat + "]");
			// }
			// Date publishedDate = info.getPublishedDate();
			if ( good && qualityFactor > 0 )
			{
				Set < String > words = new HashSet < String > ( );

				for ( SyndEntry nw : info.getEntries ( ) )
				{
					if ( nw.getTitle ( ) != null )
					{
						String [ ] w = nw.getTitle ( ).toLowerCase ( ).split ( "(\\p{Z})+" );
						words.addAll ( Arrays.asList ( w ) );
					}
				}
				//
				// // check quality
				int N = info.getEntries ( ).size ( );
				int M = words.size ( );

				if ( good && N <= 0 )
				{
					message = "No news";
					good = false;
				}
				if ( good && M <= 0 )
				{
					message = "No words";
					good = false;
				}
				if ( good && M <= ( int ) Math.round ( N * qualityFactor ) )
				{
					message = "few words " + M + "<= " + ( int ) Math.round ( N * qualityFactor );
					good = false;
				}
			}

			if ( good )
			{
				// feed.setLanguage(languageService.getOrInsert("ar"));
				if ( feed.getLanguage ( ) == null )
				{
					if ( info.getLanguage ( ) == null || info.getLanguage ( ).length ( ) <= 0 )
					{
						message = "no language";
						good = false;
					}
					else
					{
						feed.setLanguage ( languageService.getOrInsert ( info.getLanguage ( ) ) );
					}
				}
				else
				{
					if ( info.getLanguage ( ) != null && info.getLanguage ( ).length ( ) > 0
							&& ! info.getLanguage ( ).equalsIgnoreCase ( feed.getLanguage ( ).getName ( ) ) )
					{
						logger.warn ( "LANGUAGE_ERROR: feed[" + feed.getId ( ) + "]" + info.getLanguage ( ) + " "
								+ feed.getLanguage ( ).getName ( ) );
						if ( changeLanguage )
						{
							feed.setLanguage ( languageService.getOrInsert ( info.getLanguage ( ) ) );
						}
					}
				}
			}

			SimpleDateFormat sdtFinal = new SimpleDateFormat ( "yyyy-MM-dd'T'HH:mm:ssX" , Locale.ENGLISH );
			if ( good )
			{
				feed.setDescription ( info.getDescription ( ) );
				feed.setTitle ( info.getTitle ( ) );
				feed.setTtl ( ttl );

				Calendar lastStoredDate = null;

				if ( feed.getLastUpdate ( ) != null )
				{
					try
					{
						Date d = sdtFinal.parse ( feed.getLastUpdate ( ) );
						if ( d != null )
						{
							lastStoredDate = Calendar.getInstance ( );
							lastStoredDate.setTime ( d );
						}
					} catch ( Exception ex )
					{

					}
				}

				SimpleDateFormat sdt = new SimpleDateFormat ( "yyyy-MM-dd'T'HH:mm:ss'Z'" );
				sdt.setTimeZone ( TimeZone.getTimeZone ( "UTC" ) );

				Calendar newestPubDate = null;

				List < String > categoriesFeed = new ArrayList < String > ( );
				for ( SyndCategory cat : info.getCategories ( ) )
				{
					String tax = cat.getTaxonomyUri ( );
					if ( tax == null )
						tax = "";
					else
						tax = ":" + tax;
					categoriesFeed.add ( cat.getName ( ) + tax );
				}

				for ( SyndEntry nw : info.getEntries ( ) )
				{

					Date d = nw.getPublishedDate ( );
					if ( d == null )
					{
						d = nw.getUpdatedDate ( );
					}

					if ( d != null )
					{

						List < String > categoriesNew = new ArrayList < String > ( categoriesFeed );
						for ( SyndCategory cat : nw.getCategories ( ) )
						{
							String tax = cat.getTaxonomyUri ( );
							if ( tax == null )
								tax = "";
							else
								tax = ":" + tax;
							categoriesNew.add ( cat.getName ( ) + tax );
						}

						Calendar publishedDate = Calendar.getInstance ( );
						publishedDate.setTime ( d );

						if ( newestPubDate == null )
						{
							newestPubDate = publishedDate;
						}
						else if ( newestPubDate.before ( publishedDate ) )
						{
							newestPubDate = publishedDate;
						}

						if ( lastStoredDate == null || lastStoredDate.before ( publishedDate ) )
						{
							Map < String , Object > map = new HashMap < String , Object > ( );
							map.put ( "title" , nw.getTitle ( ) );
							map.put ( "description" ,
									nw.getDescription ( ) != null ? nw.getDescription ( ).getValue ( ) : "" );
							map.put ( "url" , nw.getLink ( ) );
							map.put ( "source" , feed.getId ( ) );
							map.put ( "date" , sdt.format ( d ) );
							map.put ( "language" , feed.getLanguage ( ).getId ( ) );
							map.put ( "categories" , categoriesNew );

							if ( interfaces )
							{
								newToProcess.send ( new GenericMessage <> ( map ) );
							}
							else
							{
								receiveQueue ( map );
							}
						}
						else
						{
							if (logger.isDebugEnabled())
							logger.debug ( "new [" + nw.getTitle ( ) + "] already processed? [" + d.toGMTString ( )
									+ "]-[" + feed.getLastUpdate ( ) + "] of [" + feed + "]" );
						}
					}
					else
					{
						logger.info ( "new [" + nw.getTitle ( ) + "] with no date of [" + feed + "]" );
					}
				}

				if ( newestPubDate != null )
				{
					feed.setLastUpdate ( sdtFinal.format ( newestPubDate.getTime ( ) ) );
					// feed.setNextUpdate(info.getPublishedDate());
				}
				feed.setError ( "" );

				feedService.calculateNext ( feed );

			}
			else
			{
				if ( info != null )
				{
					feed.setDescription ( info.getDescription ( ) );
					feed.setTitle ( info.getTitle ( ) );
					// feed.setLastUpdate(sdtFinal.format(newestPubDate.getTime()));
				}

				feed.setError ( message );
				if ( feed.getTitle ( ) == null || feed.getLanguage ( ) == null || feed.getLastUpdate ( ) == null )
				{
					// feed.setDisabled ( true );
				}

				if ( ! feed.getDisabled ( ) )
					feedService.calculateNext ( feed );
				else
					feedService.save ( feed );

				logger.warn ( "feed not update " + feed );
			}
		} catch ( Exception ex )
		{
			if ( feed.getTitle ( ) == null || feed.getLanguage ( ) == null )
			{
				// feed.setDisabled ( true ); // true
			}
			feed.setError ( "Exception: " + ex.getMessage ( ) );

			if ( ! feed.getDisabled ( ) )
				feedService.calculateNext ( feed );
			else
				feedService.save ( feed );
			logger.warn ( "feed not update " + feed , ex );
		}
	}

	// private void runR(Feed feed) {
	//
	// if (feed.getError() != null &&
	// feed.getError().equals("$%&CHANGELANGUAGE&%$")) {
	// changeLanguage(feed);
	// }
	//
	// logger.info("processing " + feed.getUrl());
	//
	// TempFeed result = basicRssReader.read(feed);
	// boolean good = result != null && result.getError() == null;
	// String message = result.getMessage();
	// int ttl = -1;
	//
	// if (good) {
	// try {
	// ttl = Integer.parseInt(result.getTtl());
	// } catch (NumberFormatException ex) {
	// message = "invalid ttl " + result.getTtl();
	// // good = false;
	// }
	//
	// if (feed.getLastUpdate() != null &&
	// feed.getLastUpdate().equals(result.getPubDate())) {
	// feed.setError("not updated [" + feed.getLastUpdate() + "]");
	// feedService.calculateNext(feed);
	//
	// // feedService.save(feed);
	// logger.warn("feed require no update " + feed);
	//
	// return;
	// }
	//
	// logger.info("feed updated " + feed + " [" + feed.getLastUpdate() + "] vs
	// [" + result.getPubDate());
	//
	// //
	// // check quality
	// int N = result.getNews().size();
	// int M = result.getWords().size();
	//
	// if (good && N <= 0) {
	// message = "No news";
	// good = false;
	// }
	// if (good && M <= 0) {
	// message = "No words";
	// good = false;
	// }
	// if (good && M <= (int) Math.round((double) N * qualityFactor)) {
	// message = "few words " + M + "<= " + (int) Math.round((double) N *
	// qualityFactor);
	// good = false;
	// }
	//
	// Date pubDate = convertDate(result.getPubDate());
	// if (pubDate == null) {
	// logger.info("date not found, looking for one inside items");
	// for (TempNew nw : result.getNews()) {
	// Date d = convertDate(nw.getPubDate());
	// logger.debug("cheking date " + d.toGMTString());
	// if (d != null) {
	// if (pubDate == null) {
	// pubDate = d;
	// } else if (pubDate.before(d)) {
	// pubDate = d;
	// }
	// }
	// }
	// if (pubDate != null) {
	// logger.info("selected date " + pubDate.toGMTString());
	// }
	// }
	//
	// if (pubDate == null) { // ||
	// message = "invalid null date"; // [" +
	// good = false;
	// }
	//
	// // if (feed.getLanguage() == null) {
	// if (result.getLanguage() == null || result.getLanguage().length() <= 0) {
	// message = "no language";
	// good = false;
	// } else {
	// // // we don't change language
	// if (!result.getLanguage().equals(feed.getLanguage().getName())) {
	// logger.warn("LANGUAGE_ERROR: feed[" + feed.getId() + "]" +
	// result.getLanguage() + " "
	// + feed.getLanguage().getName());
	// feed.setLanguage(languageService.getOrInsert(result.getLanguage()));
	// }
	// }
	// // } else {
	// // if (result.getLanguage() == null || result.getLanguage().length()
	// // <= 0) {
	// //
	// // } else {
	// // if (!result.getLanguage().equals(feed.getLanguage().getName())) {
	// // logger.warn("LANGUAGE_ERROR: feed[" + +feed.getId() + "]" +
	// // result.getLanguage() + " "
	// // + feed.getLanguage().getName());
	// // }
	// // }
	// // }
	// }
	//
	// if (good) {
	// feed.setDescription(result.getDescription());
	// feed.setTitle(result.getTitle());
	// feed.setTtl(ttl);
	//
	// Calendar _LastDate = null;
	//
	// if (feed.getLastUpdate() != null) {
	// Date dLastUpdate = convertDate(feed.getLastUpdate());
	// if (dLastUpdate != null) {
	// _LastDate = Calendar.getInstance();
	// _LastDate.setTime(dLastUpdate);
	// }
	// }
	// SimpleDateFormat sdt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	// sdt.setTimeZone(TimeZone.getTimeZone("UTC"));
	//
	// for (TempNew nw : result.getNews()) {
	// Date d = convertDate(nw.getPubDate());
	// Calendar _pubDate = Calendar.getInstance();
	// _pubDate.setTime(d);
	// /*
	// * Calendar _today = Calendar.getInstance();
	// *
	// * int sdays = (int) TimeUnit.MILLISECONDS
	// * .toDays(_today.getTimeInMillis() -
	// * _pubDate.getTimeInMillis());
	// *
	// * if (sdays >= daysTooOld) { logger.info("new [" +
	// * nw.getTitle() + "] too old [" + result.getPubDate() +
	// * "] of [" + feed + "]"); } else
	// */ if (_LastDate == null || _LastDate.before(_pubDate)) {
	// Map<String, Object> map = new HashMap<String, Object>();
	// map.put("title", nw.getTitle());
	// map.put("description", nw.getDescription());
	// map.put("url", nw.getLink());
	// map.put("source", feed.getId());
	//
	// map.put("date", d != null ? sdt.format(d) : null);
	// map.put("language", feed.getLanguage().getId());
	//
	// if (interfaces)
	// newToProcess.send(new GenericMessage<>(map));
	// else
	// receiveQueue(map);
	//
	// // logger.info("sending new " + nw.getTitle() + " " +
	// // _LastDate.getTime().toGMTString() + " " +
	// // _pubDate.getTime().toGMTString());
	// } else {
	//
	// // Sat, 28 May 2016 07:45:37 +0200 - Sat, 28 May 2016
	// // 07:30:36 +0200]
	// // of [[http://ep00.epimg.net/rss/tags/o_video.xml, -,
	// // com.dag.news.model.Language@4]]
	//
	// logger.info("new [" + nw.getTitle() + "] already processed? [" +
	// result.getPubDate() + " - "
	// + feed.getLastUpdate() + "] of [" + feed + "]");
	// }
	// }
	// // if (feed.getLanguage() == null
	// // ||
	// // !feed.getLanguage().getName().equals(result.getLanguage()))
	// // {
	// // logger.info("changing language for " +
	// // feed.toString());
	// // feed.setLanguage(languageService.getOrInsert(result.getLanguage()));
	// // }
	//
	// feed.setLastUpdate(result.getPubDate());
	// feed.setNextUpdate(convertDate(result.getPubDate()));
	// feed.setError("");
	// // feed.setDisabled(false);
	//
	// feedService.calculateNext(feed);
	// } else {
	// Exception ex = result.getError();
	// if (ex != null) {
	// if (ex instanceof java.net.UnknownHostException
	// || ex instanceof org.apache.http.conn.ConnectTimeoutException
	// || ex instanceof org.apache.http.client.ClientProtocolException
	// || ex instanceof java.net.SocketTimeoutException) {
	// feed.setDisabled(true); // true
	// }
	// }
	// feed.setError(message);
	//
	// if (!feed.getDisabled())
	// feedService.calculateNext(feed);
	// else
	// feedService.save(feed);
	// if (ex != null)
	// logger.warn("feed not update " + feed, ex);
	// else
	// logger.warn("feed not update " + feed);
	// }
	// }

	public void receiveQueue ( Map < String , Object > map )
	{
		String title = ( String ) map.get ( "title" );
		String description = ( String ) map.get ( "description" );
		String link = ( String ) map.get ( "url" );
		Long feed = ( Long ) map.get ( "source" );
		String pubDate = ( String ) map.get ( "date" );
		Long language = ( Long ) map.get ( "language" );
		List < String > categories = ( List < String > ) map.get ( "categories" );

		SimpleDateFormat sdt = new SimpleDateFormat ( "yyyy-MM-dd'T'HH:mm:ss'Z'" );
		sdt.setTimeZone ( TimeZone.getTimeZone ( "UTC" ) );

		Language lang = languageService.find ( language );

		try
		{
			List < String > words = Utils.getWords ( title , lang , wordsService.stopWords ( lang ) , false );

			newsService.addDb ( title , description , link , feed , sdt.parse ( pubDate )/* convertDate(pubDate) */ ,
					lang , categories , words );
		} catch ( Exception ex )
		{
			logger.warn ( "impossible to process " + title + "[" + pubDate + "] " + link , ex );
		}
	}
	//
	// private static String ConvertZoneToLocalDifferential(String s) {
	// String zoneRepresentedAsLocalDifferential = "";
	//
	// if (s.endsWith(" UT")) {
	// zoneRepresentedAsLocalDifferential = s.replace(" UT", "+00:00");
	// } else if (s.endsWith(" GMT")) {
	// zoneRepresentedAsLocalDifferential = s.replace(" GMT", "+00:00");
	// } else if (s.endsWith(" EST")) {
	// zoneRepresentedAsLocalDifferential = s.replace(" EST", "-05:00");
	// } else if (s.endsWith(" EDT")) {
	// zoneRepresentedAsLocalDifferential = s.replace(" EDT", "-04:00");
	// } else if (s.endsWith(" CST")) {
	// zoneRepresentedAsLocalDifferential = s.replace(" CST", "-06:00");
	// } else if (s.endsWith(" CDT")) {
	// zoneRepresentedAsLocalDifferential = s.replace(" CDT", "-05:00");
	// } else if (s.endsWith(" MST")) {
	// zoneRepresentedAsLocalDifferential = s.replace(" MST", "-07:00");
	// } else if (s.endsWith(" MDT")) {
	// zoneRepresentedAsLocalDifferential = s.replace(" MDT", "-06:00");
	// } else if (s.endsWith(" PST")) {
	// zoneRepresentedAsLocalDifferential = s.replace(" PST", "-08:00");
	// } else if (s.endsWith(" PDT")) {
	// zoneRepresentedAsLocalDifferential = s.replace(" PDT", "-07:00");
	// } else if (s.endsWith(" Z")) {
	// zoneRepresentedAsLocalDifferential = s.replace(" Z", "+00:00");
	// } else if (s.endsWith(" A")) {
	// zoneRepresentedAsLocalDifferential = s.replace(" A", "-01:00");
	// } else if (s.endsWith(" M")) {
	// zoneRepresentedAsLocalDifferential = s.replace(" M", "-12:00");
	// } else if (s.endsWith(" N")) {
	// zoneRepresentedAsLocalDifferential = s.replace(" N", "+01:00");
	// } else if (s.endsWith(" Y")) {
	// zoneRepresentedAsLocalDifferential = s.replace(" Y", "+12:00");
	// } else {
	// zoneRepresentedAsLocalDifferential = s;
	// }
	//
	// return zoneRepresentedAsLocalDifferential;
	// }

	// private Date convertDate(String date) {
	// if (date == null || date.length() <= 0)
	// return null;
	//
	// SimpleDateFormat rfc822DateFormats[] = new SimpleDateFormat[] {
	// new SimpleDateFormat("EEE, d MMM yy HH:mm:ss z", Locale.ENGLISH),
	// new SimpleDateFormat("EEE, d MMM yy HH:mm z", Locale.ENGLISH),
	// new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z", Locale.ENGLISH),
	// new SimpleDateFormat("EEE, d MMM yyyy HH:mm z", Locale.ENGLISH),
	// new SimpleDateFormat("d MMM yy HH:mm z", Locale.ENGLISH),
	// new SimpleDateFormat("d MMM yy HH:mm:ss z", Locale.ENGLISH),
	// new SimpleDateFormat("d MMM yyyy HH:mm z", Locale.ENGLISH),
	// new SimpleDateFormat("d MMM yyyy HH:mm:ss z", Locale.ENGLISH),
	// // 2016-05-27T13:22:17+02:00
	// new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.ENGLISH) };
	//
	// // detect ... (spanish) Sat, 30 Abr 2016 14:59:22 GMT
	//
	// for (SimpleDateFormat sdf : rfc822DateFormats) {
	// // sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
	//
	// try {
	// return sdf.parse(date);
	// } catch (Exception ex) {
	// // ex.printStackTrace();
	// }
	// }
	// for (SimpleDateFormat sdf : rfc822DateFormats) {
	// try {
	// // sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
	//
	// return sdf.parse(ConvertZoneToLocalDifferential(date));
	// } catch (Exception ex) {
	// // ex.printStackTrace();
	// }
	// }
	//
	// logger.warn("impossible to parse date [" + date + "]");
	// return null;
	// }

	// static public void main(String[] a) {
	// // yyyy-MM-dd'T'HH:mm:ss'Z'
	// SimpleDateFormat sdt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.ENGLISH);
	// try {
	//
	// Calendar _today = Calendar.getInstance();
	// Calendar xx = Calendar.getInstance();
	// xx.setTime(sdt.parse("2016-05-20T13:22:17+02:00"));
	//
	// int sdays = (int) TimeUnit.MILLISECONDS.toDays(_today.getTimeInMillis() - xx.getTimeInMillis());
	//
	// System.out.println(sdays);
	//
	// } catch (ParseException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
}
