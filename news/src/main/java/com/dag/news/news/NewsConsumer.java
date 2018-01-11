package com.dag.news.news;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;

import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import com.dag.news.model.Language;
import com.dag.news.service.LanguageService;
import com.dag.news.service.NewsService;
import com.dag.news.service.Utils;
import com.dag.news.service.WordsService;

@ Component
public class NewsConsumer implements MessageListener
{

	static private Logger logger = LoggerFactory.getLogger ( NewsConsumer.class );

	@ Autowired
	NewsService newsService;

	@ Autowired
	LanguageService languageService;

	@ Autowired
	WordsService wordsService;

	@ Value ( "${news.to.db:false}" )
	private boolean toDB;

	@ Value ( "${remote:false}" )
	private boolean remote;

	@ Autowired ( required = false )
	private JmsTemplate jmsTemplateRemote;

	@ Cacheable ( cacheNames = "stopWords" , key = "#language.id" )
	public Set < String > stopWords ( Language language )
	{
		return wordsService.stopWords ( language );
	}

	@ Override
	public void onMessage ( Message m )
	{
		MapMessage map = ( MapMessage ) m;

		if ( remote )
		{
			// newToProcessRemote.send ( new GenericMessage < Map < String , Object > > ( map ) );

			jmsTemplateRemote.send ( "new.news" , new MessageCreator ( )
			{

				@ Override
				public Message createMessage ( Session session ) throws JMSException
				{
					return m;
				}
			} );
			logger.info ( "SEND!" );
			// m.acknowledge ( );
		}
		else
		{
			String title = null;
			String pubDate = null;
			String link = null;
			try
			{
				title = map.getString ( "title" );
				pubDate = map.getString ( "date" );
				link = map.getString ( "url" );

				String description = map.getString ( "description" );
				Long feed = map.getLong ( "source" );
				Long language = map.getLong ( "language" );

				SimpleDateFormat sdt = new SimpleDateFormat ( "yyyy-MM-dd'T'HH:mm:ss'Z'" );
				sdt.setTimeZone ( TimeZone.getTimeZone ( "UTC" ) );

				Date __pubDate = sdt.parse ( pubDate );

				List < String > categories = ( List < String > ) map.getObject ( "categories" );

				Language lang = languageService.find ( language );

				if ( lang == null )
				{
					//lang = new Language ( "dasdasdas" );
					logger.info ( language + " lang not found!" );
					return;
				}

				if ( toDB )
				{
					List < String > words = Utils.getWords ( title , lang , stopWords ( lang ) , false );

					newsService.addDb ( title , description , link , feed , __pubDate , lang , categories , words );
				}
				else
				{
					newsService.add ( title , description , link , feed , __pubDate , lang , categories );
				}
			} catch ( HibernateException ex )
			{
				logger.error ( "impossible to process " + title + "[" + pubDate + "] " + link , ex );
				throw ex;
			} catch ( Exception ex )
			{
				logger.warn ( "impossible to process " + title + "[" + pubDate + "] " + link , ex );
			}
		}

	}

}
