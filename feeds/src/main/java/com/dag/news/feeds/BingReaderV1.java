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

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;

import com.dag.news.model.Feed;
import com.dag.news.model.Language;
import com.dag.news.service.FeedService;
import com.dag.news.service.LanguageService;
import com.dag.news.service.NewsService;
import com.dag.news.service.Utils;
import com.dag.news.service.WordsService;

public class BingReaderV1 implements BingReader
{
	static private Logger logger = LoggerFactory.getLogger ( BingReader.class );

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

	@ Autowired
	MessageChannel newToProcess;

	final private String acctKey = "U8TgenEpYzMRTcilSbNPGLlA6kyz8bhv+16NQO/B70s";

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
		SimpleDateFormat sdf = new SimpleDateFormat ( "yyyy-MM-dd'T'HH:mm:ss'Z'" );
		sdf.setTimeZone ( TimeZone.getTimeZone ( "UTC" ) );

		int N = 0 , M = 0;
		InputStream in = null;

		int day = Calendar.getInstance ( ).get ( Calendar.DAY_OF_YEAR );

		try
		{

			String urlTxt;
			if ( skip <= 0 )
				urlTxt = "https://api.datamarket.azure.com/Bing/Search/v1/News?Query=%27%20%27&Options=%27DisableLocationDetection%27&Market=%27"
						+ feed.getLanguage ( ).getName ( ) + "%27&Adult=%27Off%27&NewsSortBy=%27Date%27";
			else
				urlTxt = "https://api.datamarket.azure.com/Bing/Search/v1/News?Query=%27%20%27&Options=%27DisableLocationDetection%27&Market=%27"
						+ feed.getLanguage ( ).getName ( ) + "%27&Adult=%27Off%27&NewsSortBy=%27Date%27&$skip=" + skip;

			URL url = new URL ( urlTxt );

			boolean isFeedHeader = true;
			// Set header values intial to the empty string
			String description = "";
			String title = "";
			String link = "";
			String language = "";
			String copyright = "";
			String author = "";
			String pubdate = "";
			String guid = "";
			String ttl = "";

			// First create a new XMLInputFactory
			XMLInputFactory inputFactory = XMLInputFactory.newInstance ( );
			// Setup a new eventReader
			inputFactory.setProperty ( "javax.xml.stream.isReplacingEntityReferences" , true );

			URLConnection connection = url.openConnection ( );
			connection.setRequestProperty ( "Authorization" ,
					"Basic " + java.util.Base64.getEncoder ( ).encodeToString ( ( ":" + acctKey ).getBytes ( ) ) );

			in = connection.getInputStream ( );
			XMLEventReader eventReader = inputFactory.createXMLEventReader ( in );
			// read the XML document
			while ( eventReader.hasNext ( ) )
			{
				XMLEvent event = eventReader.nextEvent ( );
				if ( event.isStartElement ( ) )
				{
					String localPart = event.asStartElement ( ).getName ( ).getLocalPart ( );
					switch ( localPart )
					{
					case CONTENT :
					case PROPERTIES :
					case ITEM :
						if ( isFeedHeader )
						{
							isFeedHeader = false;
						}
						event = eventReader.nextEvent ( );
						break;
					case TITLE :
						title = getCharacterData ( event , eventReader );
						break;
					case DESCRIPTION :
						description = getCharacterData ( event , eventReader );
						break;
					case LINK :
						link = getCharacterData ( event , eventReader );
						break;
					case GUID :
						guid = getCharacterData ( event , eventReader );
						break;
					case LANGUAGE :
						language = getCharacterData ( event , eventReader );
						break;
					case CHANNEL :
						author = getCharacterData ( event , eventReader );
						break;
					case PUB_DATE :
						pubdate = getCharacterData ( event , eventReader ); // 2016-04-28T18:01:33Z
																			// yyyy-MM-dd'T'HH:mm:ss'Z'
						break;
					case COPYRIGHT :
						copyright = getCharacterData ( event , eventReader );
						break;
					}
				}
				else if ( event.isEndElement ( ) )
				{
					if ( event.asEndElement ( ).getName ( ).getLocalPart ( ) == ( ITEM ) )
					{

						Date _pubDate = sdf.parse ( pubdate );
						Calendar tmpDay = Calendar.getInstance ( );
						tmpDay.setTime ( _pubDate );

						if ( ! currentLinks.containsKey ( link ) && day == tmpDay.get ( Calendar.DAY_OF_YEAR ) )
						{
							Map < String , Object > map = new HashMap < String , Object > ( );

							map.put ( "title" , title );
							map.put ( "description" , description );
							map.put ( "url" , link );
							map.put ( "source" , feed.getId ( ) );
							map.put ( "date" , pubdate );
							map.put ( "language" , feed.getLanguage ( ).getId ( ) );
							map.put ( "categories" , new ArrayList < String > ( ) );

							if ( interfaces )
								newToProcess.send ( new GenericMessage <> ( map ) );
							else
								receiveQueue ( map );
							currentLinks.put ( link , "Y" );
						}
						else
						{
							M ++ ;
						}
						N ++ ;
						event = eventReader.nextEvent ( );
						continue;
					}
				}
			}
		} catch ( Exception ex )
		{
			// feed.setDisabled(true);
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

	private String getCharacterData ( XMLEvent event , XMLEventReader eventReader ) throws XMLStreamException
	{
		String result = "";
		event = eventReader.nextEvent ( );

		while ( true )
		{
			if ( event instanceof Characters )
			{
				result += event.asCharacters ( ).getData ( );
			}
			if ( eventReader.peek ( ) instanceof Characters )
			{
				event = eventReader.nextEvent ( );
			}
			else
			{
				break;
			}
		}

		return result;
	}

	// public static void main(String[] a) {
	// // Feed f = new Feed("http://rss.cnn.com/rss/edition.rss");
	// // f.setLanguage(new Language("en-US"));
	// new BingReader().read(0);
	// }

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
