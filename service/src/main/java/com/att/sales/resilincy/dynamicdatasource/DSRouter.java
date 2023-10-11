package com.att.sales.resilincy.dynamicdatasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class DSRouter extends AbstractRoutingDataSource {
	
	private static Logger log = LoggerFactory.getLogger(DSRouter.class);
	@Autowired
	private DSContextHolder dsCtxHolder;

	@Override
	protected Object determineCurrentLookupKey() {
		//log.info("DSRouter::Current DS : " + dsCtxHolder.getDS());
		return dsCtxHolder.getDS();
	}
}
