package com.att.sales.nexxus.edf.model;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * @author KRani The Class ValidateAccountDataRequest.
 */
@SuppressWarnings("serial")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ValidateAccountDataRequest {

	/** The accountNumberSet. */
	private List<String> accountNumberSet;

	/** The mcnSet. */
	private List<String> mcnSet;

	/** The product. */
	private String product;

	/** The billMonth. */
	private String billMonth;

	/** The beginBillMonth. */
	private String beginBillMonth;

	/**
	 * @return the accountNumberSet
	 */
	public List<String> getAccountNumberSet() {
		return accountNumberSet;
	}

	/**
	 * @param accountNumberSet the accountNumberSet to set
	 */
	public void setAccountNumberSet(List<String> accountNumberSet) {
		this.accountNumberSet = accountNumberSet;
	}

	/**
	 * @return the mcnSet
	 */
	public List<String> getMcnSet() {
		return mcnSet;
	}

	/**
	 * @param mcnSet the mcnSet to set
	 */
	public void setMcnSet(List<String> mcnSet) {
		this.mcnSet = mcnSet;
	}

	/**
	 * @return the product
	 */
	public String getProduct() {
		return product;
	}

	/**
	 * @param product the product to set
	 */
	public void setProduct(String product) {
		this.product = product;
	}

	/**
	 * @return the billMonth
	 */
	public String getBillMonth() {
		return billMonth;
	}

	/**
	 * @param billMonth the billMonth to set
	 */
	public void setBillMonth(String billMonth) {
		this.billMonth = billMonth;
	}

	/**
	 * @return the beginBillMonth
	 */
	public String getBeginBillMonth() {
		return beginBillMonth;
	}

	/**
	 * @param beginBillMonth the beginBillMonth to set
	 */
	public void setBeginBillMonth(String beginBillMonth) {
		this.beginBillMonth = beginBillMonth;
	}

	@Override
	public String toString() {
		return "ValidateAccountDataRequest [accountNumberSet=" + accountNumberSet + ", mcnSet=" + mcnSet + ", product="
				+ product + ", billMonth=" + billMonth + ", beginBillMonth=" + beginBillMonth + "]";
	}

}
