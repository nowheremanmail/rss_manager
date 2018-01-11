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
@EnableIntegration
@ImportResource("spring/spring-integration.xml")
@ComponentScan(basePackages = { "com.dag.news"   })
@Import(PlainJpaConfig.class)
@EnableScheduling
@EnableCaching
public class App implements CommandLineRunner {

//	@Autowired
//	private RssReaderManager rssReaderManager;

	@Override
	public void run(String... args) throws Exception {
		// System.out.println(helloWorldService.getHelloMessage());

		//rssReaderManager.run();

		if ((args.length > 0) && args[0].equals("exitcode")) {
			throw new ExitException();
		}
	}

	public static void main(String[] args) throws Exception {
//		 String a = System.getProperty(AbstractEnvironment.DEFAULT_PROFILES_PROPERTY_NAME);
//	     String b = System.getProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME);
	        
		SpringApplication.run(App.class, args);
	}
}
/**
 * @Component
public class SampleBeanImpl implements SampleBean {

  @Async
  void doSomething() { … }
}


@Component
public class SampleBeanInititalizer {

  @Autowired
  private final SampleBean bean;

  @PostConstruct
  public void initialize() {
    bean.doSomething();
  }
}
 */
