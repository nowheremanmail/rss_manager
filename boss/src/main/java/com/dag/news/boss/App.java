package com.dag.news.boss;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.dag.news.ExitException;
import com.dag.news.model.PlainJpaConfig;

@SpringBootApplication
@Configuration
@ComponentScan(basePackages = { "com.dag.news"   })
@Import(PlainJpaConfig.class)
@EnableScheduling
public class App implements CommandLineRunner {
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
