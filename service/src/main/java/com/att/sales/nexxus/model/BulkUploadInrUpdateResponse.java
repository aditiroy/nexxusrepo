package com.att.sales.nexxus.model;

import com.att.sales.framework.model.ServiceResponse;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BulkUploadInrUpdateResponse extends ServiceResponse {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	private Long nxSolutionId;
}
