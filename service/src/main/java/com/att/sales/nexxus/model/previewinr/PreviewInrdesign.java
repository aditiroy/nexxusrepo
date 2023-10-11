package com.att.sales.nexxus.model.previewinr;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Class PreviewInrdesign.
 *
 * @author sn973r
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class PreviewInrdesign implements Serializable{
	
	
	
	/** The port speed. */
	private String portSpeed;
	
	/** The technology. */
	private String technology;
	
	/** The site name. */
	private String siteName;
	
	/**
	 * Gets the port speed.
	 *
	 * @return the port speed
	 */
	public String getPortSpeed() {
		return portSpeed;
	}

	/**
	 * Sets the port speed.
	 *
	 * @param portSpeed the new port speed
	 */
	public void setPortSpeed(String portSpeed) {
		this.portSpeed = portSpeed;
	}

	/**
	 * Gets the technology.
	 *
	 * @return the technology
	 */
	public String getTechnology() {
		return technology;
	}

	/**
	 * Sets the technology.
	 *
	 * @param technology the new technology
	 */
	public void setTechnology(String technology) {
		this.technology = technology;
	}

	/**
	 * Gets the site name.
	 *
	 * @return the site name
	 */
	public String getSiteName() {
		return siteName;
	}

	/**
	 * Sets the site name.
	 *
	 * @param siteName the new site name
	 */
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	
	/** The price details. */
	@JsonInclude(Include.NON_EMPTY)	
	private List<PreviewInrpriceDetails> priceDetails;
		
	/**
	 * Instantiates a new preview inrdesign.
	 */
	public PreviewInrdesign() {
				
		priceDetails=new ArrayList<>();
			
		}
	
	/**
	 * Gets the price details.
	 *
	 * @return the price details
	 */
	public List<PreviewInrpriceDetails> getPriceDetails() {
		return priceDetails;
	}

	/**
	 * Sets the price details.
	 *
	 * @param priceDetails the new price details
	 */
	public void setPriceDetails(List<PreviewInrpriceDetails> priceDetails) {
		this.priceDetails = priceDetails;
	}
	

}
