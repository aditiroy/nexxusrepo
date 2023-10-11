package com.att.sales.nexxus.model;

import lombok.Getter;
import lombok.Setter;
import com.att.sales.framework.model.ServiceResponse;

@Getter
@Setter

public class NexxusSolutionActionRes extends ServiceResponse {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long nxSolutionId;
}
