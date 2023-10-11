package com.att.sales.framework.bootstrap;



import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.boot.autoconfigure.sql.init.SqlInitializationAutoConfiguration;


@SpringBootApplication
@ComponentScan(basePackages= {"com.att.sales"})
@EnableAutoConfiguration(exclude= {DataSourceAutoConfiguration.class,HibernateJpaAutoConfiguration.class,JpaRepositoriesAutoConfiguration.class,SqlInitializationAutoConfiguration.class})
@EnableScheduling
public class Application {
	
		
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	

}
	