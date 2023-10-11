package com.att.sales.nexxus.inr;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.att.sales.framework.model.ServiceResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class InvPriceJsonResponse  extends ServiceResponse{
	
	private static final long serialVersionUID = 1L;
	
	@JsonProperty("nxDwPriceId")
	private Long nxDwPriceId;

	public Long getNxDwPriceId() {
		return nxDwPriceId;
	}

	public void setNxDwPriceId(Long nxDwPriceId) {
		this.nxDwPriceId = nxDwPriceId;
	}

	

	
}
