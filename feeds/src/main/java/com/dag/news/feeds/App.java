package com.dag.news.feeds;

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
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.dag.news.ExitException;
import com.dag.news.model.PlainJpaConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

// BE UPDATED
@SpringBootApplication
@Configuration
@EnableIntegration
@EnableAutoConfiguration(exclude = {EmbeddedServletContainerAutoConfiguration.class,
	    WebMvcAutoConfiguration.class}) 
@ImportResource("spring/spring-integration.xml")
@IntegrationComponentScan(basePackages = "com.dag.news.service.service")
@ComponentScan(basePackages = { "com.dag.news"   })
@Import(PlainJpaConfig.class)
@EnableScheduling
@EnableCaching
public class App implements CommandLineRunner {


	@Bean
	public ObjectMapper getObjectMapper () {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, true);
		return mapper;
	}
	
	@Override
	public void run(String... args) throws Exception {

		if ((args.length > 0) && args[0].equals("exitcode")) {
			throw new ExitException();
		}
	}

	public static void main(String[] args) throws Exception {
		SpringApplication.run(App.class, args);
	}
}
