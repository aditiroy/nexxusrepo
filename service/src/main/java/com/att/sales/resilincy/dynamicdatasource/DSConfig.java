package com.att.sales.resilincy.dynamicdatasource;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitManager;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EntityScan(basePackages= {"${ds.entity.package}"})
@ComponentScan(basePackages= {"${ds.entity.package}"})

@EnableJpaRepositories(basePackages = {"${ds.repository.package}"}, entityManagerFactoryRef = "entityManager", transactionManagerRef = "transactionManager")

@EnableTransactionManagement 


public class DSConfig {
	
	private static Logger log = LoggerFactory.getLogger(DSConfig.class);
	
	@Autowired(required = false)
	private PersistenceUnitManager persistenceUnitManager;
	
	
	
	@Value("${ds.entity.package}")
	private String entityPackage;
	
	@Value("${ds.repository.package}")
	private String repositoryPackage;
	
	@Autowired
	@Bean
	@ConfigurationProperties(prefix = "ds.primary")
	public DataSource primaryDS() {
		try {
			return DataSourceBuilder.create().build();
		} catch (Exception e) {
			log.error("Exception in creating datasource"+e.getCause());
			return null;
		}
	}

	@Autowired
	@Bean
	@ConfigurationProperties(prefix = "ds.failover")
	public DataSource failoverDS() {
		try {
			return DataSourceBuilder.create().build();
		} catch (Exception e) {
			log.error("Exception in creating failover datasource"+e.getCause());
			return null;
		}
	}
	
	@Autowired
	@Bean
	@ConfigurationProperties(prefix = "ds.secondary")
	public DataSource secondaryDS() {
		try {
			return DataSourceBuilder.create().build();
		} catch (Exception e) {
			log.error("Exception in creating secondary datasource"+e.getCause());
			return null;
		}
	}
	
	@Autowired
	@Bean
	@ConfigurationProperties(prefix = "ds.tertiary")
	public DataSource tertiaryDS() {
		try {
			return DataSourceBuilder.create().build();
		} catch (Exception e) {
			log.error("Exception in creating tertiary datasource"+e.getCause());
			return null;
		}
	}
	
	@Autowired
	@Bean
	@ConfigurationProperties(prefix = "ds.quaternary")
	public DataSource quaternaryDS() {
		try {
			return DataSourceBuilder.create().build();
		} catch (Exception e) {
			log.error("Exception in creating quaternary datasource"+e.getCause());
			return null;
		}
	}

	@PostConstruct
	@Bean
	@Primary
	public DataSource dsRouter() {
		DSRouter dsRouter = new DSRouter();

		Map<Object, Object> targetDataSources = new HashMap<>();

		dsRouter.setDefaultTargetDataSource(primaryDS());

		targetDataSources.put(DS.PRIMARY, primaryDS());
		targetDataSources.put(DS.FAILOVER, failoverDS());
		targetDataSources.put(DS.SECONDARY, secondaryDS());
		targetDataSources.put(DS.TERTIARY, tertiaryDS());
		targetDataSources.put(DS.QUATERNARY, quaternaryDS());
		
		dsRouter.setTargetDataSources(targetDataSources);

		dsRouter.afterPropertiesSet();

		return dsRouter;
	}
	
	
	

	@Bean
	@Primary
	@ConfigurationProperties("ds.jpa")
	public JpaProperties jpaProperties() {
	    return new JpaProperties();
	}


	@Bean
	@Primary
	public LocalContainerEntityManagerFactoryBean entityManager(
	    final JpaProperties jpaProperties) {

	    EntityManagerFactoryBuilder builder =
	        createEntityManagerFactoryBuilder(jpaProperties);

	    return builder.dataSource(dsRouter()).packages(new String[]{ entityPackage, repositoryPackage})
	        .persistenceUnit("entityManager").build();
	}

	@Bean
	@Primary
	public JpaTransactionManager transactionManager(
	    @Qualifier("entityManager") final EntityManagerFactory factory) {
	    return new JpaTransactionManager(factory);
	}

	private EntityManagerFactoryBuilder createEntityManagerFactoryBuilder(
	    JpaProperties jpaProperties) {
	    JpaVendorAdapter jpaVendorAdapter =
	        createJpaVendorAdapter(jpaProperties);
	    return new EntityManagerFactoryBuilder(jpaVendorAdapter,
	    		jpaProperties.getProperties(), this.persistenceUnitManager);
	}

	private JpaVendorAdapter createJpaVendorAdapter(
	    JpaProperties jpaProperties) {
	    AbstractJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
	    adapter.setShowSql(jpaProperties.isShowSql());
	   adapter.setGenerateDdl(false);
	    return adapter;
	}
	
	
	
}
