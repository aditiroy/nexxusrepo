package com.att.sales.nexxus.edf.model;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.att.sales.nexxus.edfbulkupload.UploadMANbulkRequest;

/**
 * @author KRani 
 * 
 * The Class ValidateAccountDataRequestDetails.
 */
@SuppressWarnings("serial")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ValidateAccountDataRequestDetails {

	/** ValidateAccountDataRequest to edf call */
	private ValidateAccountDataRequest validateAccountDataRequest;
	
	/** partitionedGroupRequest json data */
	private List<UploadMANbulkRequest> partitionedGroupRequest;

	/** The product. */
	private String product;

	/** The billMonth. */
	private String billMonth;

	/** The beginBillMonth. */
	private String beginBillMonth;
	
	/** cpniApprover. */
	private String cpniApprover;

	/** customerName. */
	private String customerName;

	/** userId. */
	private String userId;
	
	/** productType. */
	private String productType;
	
	/**
	 * @return the validateAccountDataRequest
	 */
	public ValidateAccountDataRequest getValidateAccountDataRequest() {
		return validateAccountDataRequest;
	}

	/**
	 * @param validateAccountDataRequest the validateAccountDataRequest to set
	 */
	public void setValidateAccountDataRequest(ValidateAccountDataRequest validateAccountDataRequest) {
		this.validateAccountDataRequest = validateAccountDataRequest;
	}

	/**
	 * @return the partitionedGroupRequest
	 */
	public List<UploadMANbulkRequest> getPartitionedGroupRequest() {
		return partitionedGroupRequest;
	}

	/**
	 * @param partitionedGroupRequest the partitionedGroupRequest to set
	 */
	public void setPartitionedGroupRequest(List<UploadMANbulkRequest> partitionedGroupRequest) {
		this.partitionedGroupRequest = partitionedGroupRequest;
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

	/**
	 * @return the cpniApprover
	 */
	public String getCpniApprover() {
		return cpniApprover;
	}

	/**
	 * @param cpniApprover the cpniApprover to set
	 */
	public void setCpniApprover(String cpniApprover) {
		this.cpniApprover = cpniApprover;
	}

	/**
	 * @return the customerName
	 */
	public String getCustomerName() {
		return customerName;
	}

	/**
	 * @param customerName the customerName to set
	 */
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
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

	@Override
	public String toString() {
		return "ValidateAccountDataRequestDetails [validateAccountDataRequest=" + validateAccountDataRequest
				+ ", partitionedGroupRequest=" + partitionedGroupRequest + ", product=" + product + ", billMonth="
				+ billMonth + ", beginBillMonth=" + beginBillMonth + ", cpniApprover=" + cpniApprover
				+ ", customerName=" + customerName + ", userId=" + userId + ", productType=" + productType + "]";
	}
}