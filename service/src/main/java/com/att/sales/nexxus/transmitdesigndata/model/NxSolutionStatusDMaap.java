/**
 * 
 */
package com.att.sales.nexxus.transmitdesigndata.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

/**
 * @author aa316k
 *
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
public class NxSolutionStatusDMaap {
	private String opportunityId;
	private Long externalKey;
	private String eventType;
	private String nxStatus;
	private String ipeIndicator;
	public String getOpportunityId() {
		return opportunityId;
	}
	public void setOpportunityId(String opportunityId) {
		this.opportunityId = opportunityId;
	}
	public String getEventType() {
		return eventType;
	}
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	public String getNxStatus() {
		return nxStatus;
	}
	public void setNxStatus(String nxStatus) {
		this.nxStatus = nxStatus;
	}
	public String getIpeIndicator() {
		return ipeIndicator;
	}
	public void setIpeIndicator(String ipeIndicator) {
		this.ipeIndicator = ipeIndicator;
	}
	public Long getExternalKey() {
		return externalKey;
	}
	public void setExternalKey(Long externalKey) {
		this.externalKey = externalKey;
	}
	
	@Override
	public String toString() {
		return "NxSolutionStatusDMaap {opportunityId=" + opportunityId + ", externalKey=" + externalKey + ",eventType=" + eventType + ", "
				+ "nxStatus=" + nxStatus + "ipeIndicator=" + ipeIndicator + " }";
	}
	
}
