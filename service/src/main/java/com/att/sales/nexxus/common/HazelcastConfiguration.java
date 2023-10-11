package com.att.sales.nexxus.common;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hazelcast.config.Config;
import com.hazelcast.config.EvictionConfig;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MaxSizePolicy;
import com.hazelcast.internal.diagnostics.HealthMonitorLevel;


/**
 * The Class HazelcastConfiguration.
 *
 * @author vt393d
 */
@Configuration
@EnableCaching
public class HazelcastConfiguration {

	/**
	 * Hazel cast config.
	 *
	 * @return the config
	 */
	@Bean
	public Config hazelCastConfig() {
		Config config = new Config();
		config.setInstanceName("hazelcast-instance");
		config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
		config.setProperty("hazelcast.health.monitoring.level", HealthMonitorLevel.OFF.toString());
		return config.addMapConfig(new MapConfig().setName("attuid-cache")
				.setEvictionConfig(new EvictionConfig()
                        .setSize(200)
                        .setMaxSizePolicy(MaxSizePolicy.FREE_HEAP_SIZE)
                        .setEvictionPolicy(EvictionPolicy.LRU)
                ));
	}
}
