package com.att.sales.nexxus.ped.dmaap.model;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The Class NxPEDStatusDMaap.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
public class NxPEDStatusDMaap {

	private String opportunityId;
	private Integer solutionId;
	private String nxSolutionId;
	private String offerId;
	private List<String> asrItemId;
	private List<String> successAsrItemId;
	private String ipeIndicator;
	private String eventType;
	private String nxStatus;
	private String designStatus;
	private String cancellationReason;
	private String dealId;
	private String versionNumber;
	private String rlExpirationDate;
	private String rlQuoteUrl;
	private String rlDiscountApprovalType;
	private String notes;
	private Integer confirmedInterval;
	private Integer estimateInterval;
	private String preliminaryServingPlanURL;
	private String nssManagerFirstName;
	private String nssManagerLastName;
	private String nssManagerATTUID;
	@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
	private List<EndPointDetails> endPointDetails;
	private Object message;
	private String kmzMapLink;
	private String costCompletedStatus;
	@Override
	public String toString() {
		return "NxPEDStatusDMaap [opportunityId=" + opportunityId + ", solutionId=" + solutionId + ", nxSolutionId="
				+ nxSolutionId + ", offerId=" + offerId + ", asrItemId=" + asrItemId + ", successAsrItemId="
				+ successAsrItemId + ", ipeIndicator=" + ipeIndicator + ", eventType=" + eventType + ", nxStatus="
				+ nxStatus + ", designStatus=" + designStatus + ", cancellationReason=" + cancellationReason
				+ ", dealId=" + dealId + ", versionNumber=" + versionNumber + ", rlExpirationDate=" + rlExpirationDate
				+ ", rlQuoteUrl=" + rlQuoteUrl + ", rlDiscountApprovalType=" + rlDiscountApprovalType + ", notes="
				+ notes + ", confirmedInterval=" + confirmedInterval + ", estimateInterval=" + estimateInterval
				+ ", preliminaryServingPlanURL=" + preliminaryServingPlanURL + ", nssManagerFirstName="
				+ nssManagerFirstName + ", nssManagerLastName=" + nssManagerLastName + ", nssManagerATTUID="
				+ nssManagerATTUID + ", endPointDetails=" + endPointDetails + ", message=" + message + ", kmzMapLink="
				+ kmzMapLink + ",costCompletedStatus=" + costCompletedStatus + ", ]";
	}
	
}
