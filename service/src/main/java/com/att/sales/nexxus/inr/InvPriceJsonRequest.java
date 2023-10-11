package com.att.sales.nexxus.inr;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class InvPriceJsonRequest {
	private Long nxReqId;

	public Long getNxReqId() {
		return nxReqId;
	}

	public void setNxReqId(Long nxReqId) {
		this.nxReqId = nxReqId;
	}

	
}
