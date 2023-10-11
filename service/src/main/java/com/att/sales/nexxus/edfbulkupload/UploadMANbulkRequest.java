package com.att.sales.nexxus.edfbulkupload;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * 
 * @author KRani
 *
 */

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class UploadMANbulkRequest {

	private String manAccountNumber;
	
	private String mcnNumber;
	
	private String product;
	
	private String usageOrNonUsageIndicator;
	
	private String beginBillMonth;
	
	private String billMonth;
	
	private String cpniApprover;
	
	private String customerName;
	
	private String type;
	
	/**
	 * @param manAccountNumber
	 * @param mcnNumber
	 * @param type
	 * @param product
	 * @param usageOrNonUsageIndicator
	 * @param beginBillMonth
	 * @param billMonth
	 * @param cpniApprover
	 * @param customerName
	 */
	public UploadMANbulkRequest(String manAccountNumber, String mcnNumber, String type, String product,
			String usageOrNonUsageIndicator, String beginBillMonth, String billMonth, String cpniApprover,
			String customerName) {
		super();
		this.manAccountNumber = manAccountNumber;
		this.mcnNumber = mcnNumber;
		this.type = type;
		this.product = product;
		this.usageOrNonUsageIndicator = usageOrNonUsageIndicator;
		this.beginBillMonth = beginBillMonth;
		this.billMonth = billMonth;
		this.cpniApprover = cpniApprover;
		this.customerName = customerName;
	}


	/**
	 *  UploadMANbulkRequest.
	 */
	public UploadMANbulkRequest() {
		super();
	}

	@Override
	public String toString() {
		return "UploadMANbulkRequest [manAccountNumber=" + manAccountNumber + ", mcnNumber=" + mcnNumber + ", product="
				+ product + ", usageOrNonUsageIndicator=" + usageOrNonUsageIndicator + ", beginBillMonth="
				+ beginBillMonth + ", billMonth=" + billMonth + ", cpniApprover=" + cpniApprover + ", customerName="
				+ customerName + ", type=" + type + "]";
	}
	
}