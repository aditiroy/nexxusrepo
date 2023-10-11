package com.att.sales.nexxus.model;

import java.io.Serializable;
import java.util.List;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import com.att.sales.framework.model.ServiceResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The Class RetrieveAdminDataResponse.
 *
 * @author RudreshWaladaunki
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class RetrieveAdminDataResponse extends ServiceResponse implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The top product list. */
	private List<TopProduct> topProductList;

	/**
	 * Gets the top product list.
	 *
	 * @return the topProductList
	 */
	public List<TopProduct> getTopProductList() {
		return topProductList;
	}

	/**
	 * Sets the top product list.
	 *
	 * @param topProductList the topProductList to set
	 */
	public void setTopProductList(List<TopProduct> topProductList) {
		this.topProductList = topProductList;
	}

}
