package com.att.sales.nexxus.myprice.transaction.model;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
public class GetDealLineItem {

	@JsonProperty("rd_revisionNumber_q")
	private String revisionNumber;

	@JsonProperty("bs_id")
	private String bsId;

	@JsonProperty("version_t")
	private int version;

	private List<Links> links;

}
