package com.att.sales.nexxus.dmaap.mr.util;

import java.util.Map;

import com.att.sales.nexxus.ped.dmaap.model.NxPEDStatusDMaap;
import com.att.sales.nexxus.rateletter.model.RateLetterStatusRequest;

public interface DmaapPublishEventsService {

	void triggerDmaapEventForPEDRequest(NxPEDStatusDMaap request,Map<String,Object> inputmap);
	
	void triggerDmaapEventForMyprice(RateLetterStatusRequest request,Map<String,Object> inputmap);

}
