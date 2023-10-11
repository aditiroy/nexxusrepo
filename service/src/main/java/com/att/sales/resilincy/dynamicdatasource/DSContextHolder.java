package com.att.sales.resilincy.dynamicdatasource;


import org.springframework.stereotype.Component;
import org.springframework.util.Assert;


@Component
public class DSContextHolder {
	
	 private static ThreadLocal<DS> dsCtx = new ThreadLocal<>();	 


		public DS getDS() {
			
			return dsCtx.get();
		}

		public void setDS(DS ds) {
			Assert.notNull(ds, "ds cannot be null");
			
			dsCtx.set(ds);
		}

		public void clearDS() {
			dsCtx.remove();	
		}
}
