package com.att.sales.nexxus.model.previewinr;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Class PreviewInraccountDetails.
 *
 * @author sn973r
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class PreviewInraccountDetails implements Serializable{
	
	/** The site. */
	@JsonInclude(Include.NON_EMPTY)	
	private List<PreviewInrsite> site;	
	
	
	/**
	 * Instantiates a new preview inraccount details.
	 */
	public PreviewInraccountDetails() {
		
	site=new ArrayList<>();
	
	}
	
	/**
	 * Gets the site.
	 *
	 * @return the site
	 */
	public List<PreviewInrsite> getSite() {
		return site;
	}

	/**
	 * Sets the site.
	 *
	 * @param site the new site
	 */
	public void setSite(List<PreviewInrsite> site) {
		this.site = site;
	}

		
	/** The currency. */
	private String currency;


	/**
	 * Gets the currency.
	 *
	 * @return the currency
	 */
	public String getCurrency() {
		return currency;
	}

	/**
	 * Sets the currency.
	 *
	 * @param currency the new currency
	 */
	public void setCurrency(String currency) {
		this.currency = currency;
	}



}
