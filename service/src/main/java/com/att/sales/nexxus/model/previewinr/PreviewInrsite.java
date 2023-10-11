package com.att.sales.nexxus.model.previewinr;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Class PreviewInrsite.
 *
 * @author sn973r
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class PreviewInrsite implements Serializable{
	
	/** The site id. */
	private String siteId;
	
	/** The city. */
	private String city;
	
	/** The country. */
	private String country;
	
	/** The design. */
	@JsonInclude(Include.NON_EMPTY)	
	private List<PreviewInrdesign> design;
		

	/**
	 * Instantiates a new preview inrsite.
	 */
	public PreviewInrsite() {
		
		design=new ArrayList<>();
		
		}
	
	
	/**
	 * Gets the design.
	 *
	 * @return the design
	 */
	public List<PreviewInrdesign> getDesign() {
		return design;
	}

	/**
	 * Sets the design.
	 *
	 * @param design the new design
	 */
	public void setDesign(List<PreviewInrdesign> design) {
		this.design = design;
	}

		
	/**
	 * Gets the site id.
	 *
	 * @return the site id
	 */
	public String getSiteId() {
		return siteId;
	}
	
	/**
	 * Sets the site id.
	 *
	 * @param siteId the new site id
	 */
	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}
	
	/**
	 * Gets the city.
	 *
	 * @return the city
	 */
	public String getCity() {
		return city;
	}
	
	/**
	 * Sets the city.
	 *
	 * @param city the new city
	 */
	public void setCity(String city) {
		this.city = city;
	}
	
	/**
	 * Gets the country.
	 *
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}
	
	/**
	 * Sets the country.
	 *
	 * @param country the new country
	 */
	public void setCountry(String country) {
		this.country = country;
	}


}
