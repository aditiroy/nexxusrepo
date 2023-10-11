package com.att.sales.nexxus.service;

import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.nexxus.model.NexxusSolActionRequest;
import com.att.sales.nexxus.model.NexxusSolActionResponse;

public interface NexxusSolutionActionService {
	
	/**
	 * @param request
	 * @return
	 */
	ServiceResponse nexxusSolutionAction(NexxusSolActionRequest request);
}
