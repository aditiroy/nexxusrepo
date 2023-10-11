package com.att.sales.nexxus.nxPEDstatus.model;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 
 * @author Laxman Honawad
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
public class SolutionDetails {

	@JsonProperty("designId")
	private String designId;

	@JsonProperty("siteId")
	private Long siteId;

	@JsonProperty("asrItemId")
	private String asrItemId;

	@JsonProperty("statusCode")
	private String statusCode;

	@JsonProperty("notes")
	private String notes;

	@JsonProperty("statusDescription")
	private String statusDescription;

	@JsonProperty("cancellationReason")
	private String cancellationReason;

	@JsonProperty("estimatedInterval")
	private Integer estimatedInterval;

	@JsonProperty("confirmedInterval")
	private Integer confirmedInterval;

	@JsonProperty("preliminaryServingPlanURL")
	private String preliminaryServingPlanURL;

	@JsonProperty("nssManagerFirstName")
	private String nssManagerFirstName;

	@JsonProperty("nssManagerLastName")
	private String nssManagerLastName;

	@JsonProperty("nssManagerATTUID")
	private String nssManagerATTUID;

	@JsonProperty("endPointDetails")
	@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
	@Autowired
	private List<EndPointDetails> endPointDetails;

	@JsonProperty("kmzMapLink")
	private String kmzMapLink;
}
