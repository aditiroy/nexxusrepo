package com.att.sales.nexxus.model;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
public class UpdateTransactionOverrideDocument implements Serializable {

	private static final long serialVersionUID = 3307122297981446317L;

	@JsonProperty("rd_revisionNumber_q")
	private String revision;

	@JsonProperty("version_t")
	private String version;

	@JsonProperty("rd_requestID_q")
	private String requestId;

	@JsonProperty("nx_createNewAdditionalRequest")
	private boolean createNewAdditionalRequest;

	@JsonProperty("siteAddress")
	private String siteAddress;
	
	@JsonProperty("integrationSiteDict1")
	private String integrationSiteDict;
	
	@JsonProperty("wi_updateOverride_q")
	private boolean wiUpdateOverrideQ;
	
	@JsonProperty("externalSolutionId")
	private long externalSolutionId;
	
	@JsonProperty("wi_OriginalClonedTxId")
	private String wiOriginalClonedTxId;
	
	@JsonProperty("wi_contractTerm_q")
	private Long wiContractTermQ;
	
	@JsonProperty("wi_contractTermUpdate_q")
	private boolean wiContractTermUpdateQ;

	@JsonProperty("Wi_solutionVersion_q")
	private Long wiSolutionVersionQ;
}
