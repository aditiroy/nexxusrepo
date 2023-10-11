package com.att.sales.nexxus.admin.model;

import com.att.sales.framework.model.ServiceResponse;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class BulkUploadEthTokenResponse extends ServiceResponse {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	private Long nxSolutionId;
	
	private String duplicateTokenId;
	
	
}
