package com.att.sales.nexxus.myprice.transaction.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
public class ConfigRespProcessingBean {
	
	private String nxSiteId;
	private Long nxTransactionId;
	private String documentNumber;
	private String parentLineItem;
	private String parentDocNumber;
	private String lineBomId;
	private String lineBomParentId;
	private String lineBomPartNumber;
	private String usocCode;
	private String mpTransactionId;
	private String adeSiteRelation;
	private String modelName;
	private String modelVariableName;

}
