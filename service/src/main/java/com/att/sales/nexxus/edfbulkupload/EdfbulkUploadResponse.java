package com.att.sales.nexxus.edfbulkupload;
import com.att.sales.framework.model.ServiceResponse;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author KRani
 *
 */

@Getter
@Setter
public class EdfbulkUploadResponse extends ServiceResponse {

	private static final long serialVersionUID = 1L;

	private Long nxSolutionId;
	
	private String nxSolutionDesc;
	
	private String inrStatusInd;
	
	private String iglooStatusInd;
}