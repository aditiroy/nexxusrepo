package com.att.sales.nexxus.service;

import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.nexxus.model.NewEnhancementRequest;
import com.att.sales.nexxus.model.NewEnhancementResponse;

public interface EnhancementService {
	
	ServiceResponse fetchNewEnhancements(NewEnhancementRequest request);
}
