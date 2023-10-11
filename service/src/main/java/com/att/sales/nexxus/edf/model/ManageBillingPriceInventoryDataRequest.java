package com.att.sales.nexxus.edf.model;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.att.sales.nexxus.model.Report_Key;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * The Class ManageBillingPriceInventoryDataRequest.
 */
@SuppressWarnings("serial")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_EMPTY)
@JsonInclude(Include.NON_EMPTY)
@JsonRootName("manageBillingPriceInventoryDataRequest")
public class ManageBillingPriceInventoryDataRequest {

	/** The Report key. */
	private List<Report_Key> Report_key = new ArrayList<>();

	/** The Customer name. */
	private String Customer_name;

	/** The Bill month. */
	private String Bill_Month;
	
	/** The Begin bill month. */
	private String Begin_Bill_Month;

	/** The Product. */
	private String Product;
	
	@JsonIgnore
	private String productType;
	
	private InputDetailModel inputDetail;

	/**
	 * Gets the report key.
	 *
	 * @return the report key
	 */
	@JsonProperty("Report_key")
	public List<Report_Key> getReport_key() {
		return Report_key;
	}

	/**
	 * Sets the report key.
	 *
	 * @param report_key the new report key
	 */
	public void setReport_key(List<Report_Key> report_key) {
		Report_key = report_key;
	}

	/**
	 * Gets the customer name.
	 *
	 * @return the customer name
	 */
	@JsonProperty("Customer_name")
	public String getCustomer_name() {
		return Customer_name;
	}

	/**
	 * Sets the customer name.
	 *
	 * @param customer_name the new customer name
	 */
	public void setCustomer_name(String customer_name) {
		Customer_name = customer_name;
	}

	/**
	 * Gets the bill month.
	 *
	 * @return the bill month
	 */
	@JsonProperty("Bill_Month")
	public String getBill_Month() {
		return Bill_Month;
	}

	/**
	 * Sets the bill month.
	 *
	 * @param bill_Month the new bill month
	 */
	public void setBill_Month(String bill_Month) {
		Bill_Month = bill_Month;
	}
	
	/**
	 * Gets the begin bill month.
	 *
	 * @return the begin bill month
	 */
	@JsonProperty("Begin_Bill_Month")
	public String getBegin_Bill_Month() {
		return Begin_Bill_Month;
	}

	/**
	 * Sets the begin bill month.
	 *
	 * @param begin_Bill_Month the new begin bill month
	 */
	public void setBegin_Bill_Month(String begin_Bill_Month) {
		Begin_Bill_Month = begin_Bill_Month;
	}

	/**
	 * Gets the product.
	 *
	 * @return the product
	 */
	@JsonProperty("Product")
	public String getProduct() {
		return Product;
	}

	/**
	 * Sets the product.
	 *
	 * @param product the new product
	 */
	public void setProduct(String product) {
		Product = product;
	}

	/**
	 * @return the productType
	 */
	public String getProductType() {
		return productType;
	}

	/**
	 * @param productType the productType to set
	 */
	public void setProductType(String productType) {
		this.productType = productType;
	}

	public InputDetailModel getInputDetail() {
		return inputDetail;
	}

	public void setInputDetail(InputDetailModel inputDetail) {
		this.inputDetail = inputDetail;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ManageBillingPriceInventoryDataRequest [Report_key=" + Report_key + ", Customer_name=" + Customer_name
				+ ", Bill_Month=" + Bill_Month + ", Begin_Bill_Month=" + Begin_Bill_Month + ", Product=" + Product
				+ ", productType=" + productType + "]";
	}

	public ManageBillingPriceInventoryDataRequest(ManageBillingPriceInventoryDataRequest currentData) {
		this.Customer_name = currentData.getCustomer_name();
		this.Bill_Month = currentData.getBill_Month();
		this.Begin_Bill_Month = currentData.getBegin_Bill_Month();
		this.Product = currentData.getProduct();
		this.productType = currentData.getProductType();
	}

	public ManageBillingPriceInventoryDataRequest() {
		super();
	}
	
}
