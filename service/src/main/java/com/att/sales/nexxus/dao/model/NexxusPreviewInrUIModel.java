package com.att.sales.nexxus.dao.model;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * The Class NexxusPreviewInrUIModel.
 */
public class NexxusPreviewInrUIModel {
	
	/** The customer name. */
	private String customerName;
	
	/** The opty id. */
	private String optyId;
	
	/** The duns number. */
	private String dunsNumber;
	
	/** The product cd. */
	private String productCd;
	
	/** The intermediate json. */
	private String intermediateJson;
	
	/** The output json. */
	private String outputJson;
	
	/**
	 * Gets the customer name.
	 *
	 * @return the customer name
	 */
	public String getCustomerName() {
		return customerName;
	}

	/**
	 * Sets the customer name.
	 *
	 * @param customerName the new customer name
	 */
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	/**
	 * Gets the opty id.
	 *
	 * @return the opty id
	 */
	public String getOptyId() {
		return optyId;
	}

	/**
	 * Sets the opty id.
	 *
	 * @param optyId the new opty id
	 */
	public void setOptyId(String optyId) {
		this.optyId = optyId;
	}

	/**
	 * Gets the duns number.
	 *
	 * @return the duns number
	 */
	public String getDunsNumber() {
		return dunsNumber;
	}

	/**
	 * Sets the duns number.
	 *
	 * @param dunsNumber the new duns number
	 */
	public void setDunsNumber(String dunsNumber) {
		this.dunsNumber = dunsNumber;
	}

	/**
	 * Gets the product cd.
	 *
	 * @return the product cd
	 */
	public String getProductCd() {
		return productCd;
	}

	/**
	 * Sets the product cd.
	 *
	 * @param productCd the new product cd
	 */
	public void setProductCd(String productCd) {
		this.productCd = productCd;
	}

	/**
	 * Gets the intermediate json.
	 *
	 * @return the intermediate json
	 */
	public String getIntermediateJson() {
		return intermediateJson;
	}

	/**
	 * Sets the intermediate json.
	 *
	 * @param intermediateJson the new intermediate json
	 */
	public void setIntermediateJson(String intermediateJson) {
		this.intermediateJson = intermediateJson;
	}

	/**
	 * Gets the output json.
	 *
	 * @return the output json
	 */
	public String getOutputJson() {
		return outputJson;
	}

	/**
	 * Sets the output json.
	 *
	 * @param outputJson the new output json
	 */
	public void setOutputJson(String outputJson) {
		this.outputJson = outputJson;
	}

	/**
	 * Instantiates a new nexxus preview inr UI model.
	 */
	public NexxusPreviewInrUIModel() {
		super();
	}

	/**
	 * Instantiates a new nexxus preview inr UI model.
	 *
	 * @param customerName the customer name
	 * @param optyId the opty id
	 * @param dunsNumber the duns number
	 * @param productCd the product cd
	 * @param intermediateJson the intermediate json
	 * @param outputJson the output json
	 */
	public NexxusPreviewInrUIModel(String customerName, String optyId, String dunsNumber,
			String productCd, String intermediateJson, String outputJson) {
		super();
		this.customerName = customerName;
		this.optyId = optyId;
		this.dunsNumber = dunsNumber;
		this.productCd = productCd;
		this.intermediateJson = intermediateJson;
		this.outputJson = outputJson;
		

	}

}
