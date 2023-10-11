package com.att.sales.nexxus.datarouter.service;

import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.nexxus.datarouter.model.DataRouterRequest;

public interface PublishDataRouterService {
	
	ServiceResponse publishDataRouter(DataRouterRequest request);

}
