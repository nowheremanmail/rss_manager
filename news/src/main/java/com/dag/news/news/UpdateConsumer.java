package com.dag.news.news;

import java.util.List;
import java.util.Set;

import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.dag.news.model.Language;
import com.dag.news.model.New;
import com.dag.news.service.LanguageService;
import com.dag.news.service.NewsService;
import com.dag.news.service.Utils;
import com.dag.news.service.WordsService;

@ Component
public class UpdateConsumer implements MessageListener
{

	static private Logger logger = LoggerFactory.getLogger ( UpdateConsumer.class );

	@ Autowired
	NewsService newsService;

	@ Autowired
	LanguageService languageService;

	@ Autowired
	WordsService wordsService;

	@ Cacheable ( cacheNames = "stopWords" , key = "#language.id" )
	public Set < String > stopWords ( Language language )
	{
		return wordsService.stopWords ( language );
	}

	@ Override
	public void onMessage ( Message m )
	{
		MapMessage map = ( MapMessage ) m;
		Long newId = null;
		Long languageId = null;
		try
		{
			newId = map.getLong ( "source" );
			languageId = map.getLong ( "language" );

			// SimpleDateFormat sdt = new
			// SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			// sdt.setTimeZone(TimeZone.getTimeZone("UTC"));

			Language lang = languageService.find ( languageId );

			if ( lang == null )
			{
				logger.info ( newId + " -> " + languageId + " lang not found!" );
				return;
			}

			New nw = newsService.find ( newId );

			if ( nw == null )
			{
				logger.info ( newId + " -> " + languageId + " nw not found!" );
				return;
			}

			List < String > words = Utils.getWords ( nw.getTitle ( ) , lang , stopWords ( lang ) , false );

			newsService.changeLanguage ( nw , lang , words );
		} catch ( HibernateException ex )
		{
			logger.error ( "impossible to process update " + newId + " to " + languageId , ex );
			throw ex;
		} catch ( Exception ex )
		{
			logger.warn ( "impossible to process update " + newId + " to " + languageId , ex );
		}
	}

}
