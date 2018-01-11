package com.dag.news.feeds;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

import com.dag.news.feeds.bing.BingDetailedNewsAnswer;
import com.dag.news.feeds.bing.BingNewsAnswer;
import com.dag.news.model.Feed;
import com.dag.news.model.Language;
import com.dag.news.service.FeedService;
import com.dag.news.service.LanguageService;
import com.dag.news.service.NewsService;
import com.dag.news.service.Utils;
import com.dag.news.service.WordsService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ Component
public class BingReaderV2 implements BingReader
{
	static private Logger logger = LoggerFactory.getLogger ( BingReaderV2.class );

	@ Autowired
	FeedService feedService;

	@ Autowired
	LanguageService languageService;

	@ Autowired
	NewsService newsService;

	@ Autowired
	WordsService wordsService;

	@ Value ( "${interfaces:true}" )
	boolean interfaces;

	@ Value ( "${bing.reader.step:100}" )
	int bingReaderMax;

	@ Autowired
	MessageChannel newToProcess;

	@ Autowired
	ObjectMapper mapper;

	@ Value ( "${bing.key:77043df69b6f435f900ac02ffc46a936}" )
	String acctKey;

	static final String TITLE = "Title";

	static final String DESCRIPTION = "Description";

	static final String CHANNEL = "Source";

	static final String LANGUAGE = "language";

	static final String COPYRIGHT = "copyright";

	static final String LINK = "Url";

	static final String AUTHOR = "author";

	static final String PUB_DATE = "Date";

	static final String GUID = "ID";

	static final String TTL = "ttl";

	static final String ITEM = "entry";

	static final String CONTENT = "content";

	static final String PROPERTIES = "properties";

	@ Override
	public int read ( Feed feed , int skip , Map < String , String > currentLinks )
	{
		// 2016-12-01T01:14:02
		SimpleDateFormat sdf = new SimpleDateFormat ( "yyyy-MM-dd'T'HH:mm:ss" );
		sdf.setTimeZone ( TimeZone.getTimeZone ( "UTC" ) );

		SimpleDateFormat sdfs = new SimpleDateFormat ( "yyyy-MM-dd" );
		sdfs.setTimeZone ( TimeZone.getTimeZone ( "UTC" ) );

		SimpleDateFormat sdt = new SimpleDateFormat ( "yyyy-MM-dd'T'HH:mm:ss'Z'" );
		sdt.setTimeZone ( TimeZone.getTimeZone ( "UTC" ) );

		int N = 0 , M = 0;
		InputStream in = null;

		int day = Calendar.getInstance ( ).get ( Calendar.DAY_OF_YEAR );

		try
		{

			// https://api.cognitive.microsoft.com/bing/v5.0
			// https://api.cognitive.microsoft.com/bing/v5.0/news/trendingtopics
			// https://api.cognitive.microsoft.com/bing/v5.0/news/search

			// https://azure.microsoft.com/en-us/services/cognitive-services/bing-news-search-api/

			// https://api.cognitive.microsoft.com/bing/v5.0/news/search?q=
			String urlTxt;
			if ( skip <= 0 )
				urlTxt = "https://api.cognitive.microsoft.com/bing/v5.0/news/search?q=+&mkt="
						+ feed.getLanguage ( ).getName ( ) + "&safeSearch=Off&count=" + bingReaderMax;
			else
				urlTxt = "https://api.cognitive.microsoft.com/bing/v5.0/news/search?q=+&mkt="
						+ feed.getLanguage ( ).getName ( ) + "&safeSearch=Off&count=" + bingReaderMax + "&offset="
						+ skip;

			URL url = new URL ( urlTxt );

			URLConnection connection = url.openConnection ( );
			connection.setRequestProperty ( "Ocp-Apim-Subscription-Key" , acctKey );

			in = connection.getInputStream ( );

			// if (mapper == null) {
			// mapper = new ObjectMapper();
			// mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
			// false);
			// mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, true);
			// }
			// Map json1 = mapper.readValue(in, Map.class);

			BingNewsAnswer json = mapper.readValue ( in , BingNewsAnswer.class );

			for ( BingDetailedNewsAnswer item : json.getValue ( ) )
			{
				try
				{
					Calendar tmpDay = Calendar.getInstance ( );
					if ( item.getDatePublished ( ).length ( ) > 10 )
					{
						Date _pubDate = sdf.parse ( item.getDatePublished ( ) );
						tmpDay.setTime ( _pubDate );
					}
					else
					{
						Date _pubDate = sdfs.parse ( item.getDatePublished ( ) );
						tmpDay.setTime ( _pubDate );
					}
					if ( ! currentLinks.containsKey ( item.getUrl ( ) ) && day == tmpDay.get ( Calendar.DAY_OF_YEAR ) )
					{
						Map < String , Object > map = new HashMap < String , Object > ( );

						map.put ( "title" , item.getName ( ) );
						map.put ( "description" , item.getDescription ( ) );
						map.put ( "url" , item.getUrl ( ) );
						map.put ( "source" , feed.getId ( ) );
						map.put ( "date" , sdt.format ( tmpDay.getTime ( ) ) );
						map.put ( "language" , feed.getLanguage ( ).getId ( ) );
						map.put ( "categories" , new ArrayList < String > ( ) );

						if ( interfaces )
							newToProcess.send ( new GenericMessage <> ( map ) );
						else
							receiveQueue ( map );
						currentLinks.put ( item.getUrl ( ) , "Y" );
					}
					else
					{
						M ++ ;
					}
				} catch ( Exception ex )
				{
					M ++ ;
					logger.error ( "error processing item " + item.toString ( ) , ex );
				}
				N ++ ;
			}
		} catch ( Exception ex )
		{
			// feed.setDisabled ( true );
			feed.setError ( ex.getMessage ( ) );

			logger.error ( "error processing bing " , ex );
			return - 1;
		} finally
		{
			try
			{
				if ( in != null ) in.close ( );
			} catch ( IOException e )
			{

			}
		}
		logger.info ( "process " + M + " " + N );

		return M != N ? N : 0;
	}

	public void receiveQueue ( Map < String , Object > map )
	{
		String title = ( String ) map.get ( "title" );
		String description = ( String ) map.get ( "description" );
		String link = ( String ) map.get ( "url" );
		Long feed = ( Long ) map.get ( "source" );
		String pubDate = ( String ) map.get ( "date" );
		Long language = ( Long ) map.get ( "language" );

		Language lang = languageService.find ( language );

		List < String > categories = ( List < String > ) map.get ( "categories" );

		SimpleDateFormat sdt = new SimpleDateFormat ( "yyyy-MM-dd'T'HH:mm:ss'Z'" );
		sdt.setTimeZone ( TimeZone.getTimeZone ( "UTC" ) );

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

	public static void main ( String [ ] a )
	{
		Feed f = new Feed ( "http://rss.cnn.com/rss/edition.rss" );
		f.setLanguage ( new Language ( "en-US" ) );
		new BingReaderV2 ( ).read ( f , 0 , new HashMap ( ) );
	}

	// private Feed feed = null;
	//
	// public BingReader (Feed feed) {
	// this.feed = feed;
	// }
	//
	// @Override
	// public void run() {
	//// Feed feed = feedService.getOrInsert("bing",
	// languageService.getOrInsert("es-ES"));
	// while (true) {
	// int skip = 0;
	// int i = 0;
	//
	// logger.info("bing " + skip);
	// while ((i = read(feed, skip)) > 0 && skip < MAX) {
	// skip += i;
	// logger.info("bing " + skip);
	// }
	//
	// clear();
	//
	// try {
	// Thread.sleep(1000 * 60 * 60);
	// } catch (InterruptedException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// }

}
