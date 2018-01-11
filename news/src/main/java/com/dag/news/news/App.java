package com.dag.news.news;

import java.text.SimpleDateFormat;
import java.util.Locale;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.EmbeddedServletContainerAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Primary;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.dag.news.ExitException;
import com.dag.news.model.PlainJpaConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

// BE UPDATED
@ SpringBootApplication
@ Configuration
@ EnableAutoConfiguration ( exclude = { EmbeddedServletContainerAutoConfiguration.class ,
		WebMvcAutoConfiguration.class } )
@ ImportResource ( "spring/jms.xml" )
@ IntegrationComponentScan ( basePackages = "com.dag.news.service.service" )
@ ComponentScan ( basePackages = { "com.dag.news" } )
@ Import ( PlainJpaConfig.class )
@ EnableScheduling
@ EnableCaching
public class App implements CommandLineRunner
{

	// @Autowired
	// private RssReaderManager rssReaderManager;

	@ Override
	public void run ( String ... args ) throws Exception
	{
		// System.out.println(helloWorldService.getHelloMessage());

		// rssReaderManager.run();

		if ( ( args.length > 0 ) && args [ 0 ].equals ( "exitcode" ) )
		{
			throw new ExitException ( );
		}
	}

	@ Bean
	@ Primary
	public ObjectMapper serializingObjectMapper ( )
	{
		ObjectMapper objectMapper = new ObjectMapper ( );
		objectMapper.setDateFormat ( new SimpleDateFormat ( "yyyy-MM-dd'T'HH:mm:ss'Z'" ) );
		objectMapper.setLocale ( Locale.US );

		objectMapper.configure ( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES , false );
		return objectMapper;
	}

	public static void main ( String [ ] args ) throws Exception
	{
		SpringApplication.run ( App.class , args );
	}
}
/**
 * @Component
 * 			public class SampleBeanImpl implements SampleBean {
 * 
 * @Async
 * 		void doSomething() { … }
 *        }
 * 
 * 
 * @Component
 * 			public class SampleBeanInititalizer {
 * 
 * @Autowired
 * 			private final SampleBean bean;
 * 
 * @PostConstruct
 * 				public void initialize() {
 *                bean.doSomething();
 *                }
 *                }
 */
