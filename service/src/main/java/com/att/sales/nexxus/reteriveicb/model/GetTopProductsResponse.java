package com.att.sales.nexxus.reteriveicb.model;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.nexxus.dao.model.TopProductDataEntity;

/**
 * The Class GetTopProductsResponse.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class GetTopProductsResponse extends ServiceResponse {

	/** The top product list. */
	private List<TopProductDataEntity> topProductList = new ArrayList();

	/**
	 * Gets the top product list.
	 *
	 * @return the top product list
	 */
	public List<TopProductDataEntity> getTopProductList() {
		return topProductList;
	}

	/**
	 * Sets the top product list.
	 *
	 * @param topProductList the new top product list
	 */
	public void setTopProductList(List<TopProductDataEntity> topProductList) {
		this.topProductList = topProductList;
	}
}
