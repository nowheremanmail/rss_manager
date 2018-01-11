package com.dag.news.model;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@ Configuration
@ EnableJpaRepositories
@ EntityScan ( basePackageClasses = { Word.class , Language.class , New.class , Feed.class , WordsOnNews.class ,
		TwoWordsOnNews.class , Categories.class } )
@ EnableTransactionManagement
@ EnableCaching
public class PlainJpaConfig
{

	static private Logger logger = LoggerFactory.getLogger ( PlainJpaConfig.class );

	@ PostConstruct
	public void start ( )
	{
		logger.info ( "Starting PlainJpaConfig ..." );
	}

	@ Bean ( name = "mainDataSource" )
	@ Primary
	@ ConfigurationProperties ( prefix = "spring.datasource" )
	public DataSource mainDataSource ( )
	{
		return DataSourceBuilder.create ( ).build ( );
	}
}
