package com.att.sales.nexxus.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.att.sales.nexxus.helper.FmoLookUpDataHelper;

/**
 * The Class ApplicationStartupRunner.
 *
 * @author vt393d
 */
@Component
public class ApplicationStartupRunner implements CommandLineRunner{

	/** The fmo look up data helper. */
	@Autowired
	private FmoLookUpDataHelper fmoLookUpDataHelper;
	
	/* (non-Javadoc)
	 * @see org.springframework.boot.CommandLineRunner#run(java.lang.String[])
	 */
	@Override
	public void run(String... args) throws Exception {
		fmoLookUpDataHelper.createPriceTypeDataMap();
		
	}
	

}
