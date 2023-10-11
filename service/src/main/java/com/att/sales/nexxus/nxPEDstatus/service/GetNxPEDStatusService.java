package com.att.sales.nexxus.nxPEDstatus.service;

import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.nexxus.nxPEDstatus.model.GetNxPEDStatusRequest;
import com.att.sales.nexxus.nxPEDstatus.model.GetNxPEDStatusResponse;

public interface GetNxPEDStatusService {

	public ServiceResponse getnXPEDStatus(GetNxPEDStatusRequest request);

}
