package com.att.sales.nexxus.admin.model;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class UploadEthTokenRequest {

	private String siteRefId;

	private String quoteId;

	private String portStatus;
	
	private String circuitId;
	
	private Long nxsiteId;

}
