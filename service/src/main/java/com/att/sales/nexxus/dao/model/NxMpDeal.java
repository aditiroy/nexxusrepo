package com.att.sales.nexxus.dao.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author ShruthiCJ
 *
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "NX_MP_DEAL")
public class NxMpDeal implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "sequence_nx_mp_deal", sequenceName = "SEQ_NX_MP_DEAL", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence_nx_mp_deal")
	@Column(name = "NX_TXN_ID")
	private Long nxTxnId;

	@Column(name = "NX_SOLUTION_ID")
	private Long solutionId;

	@Column(name = "DEAL_ID")
	private String dealID;

	@Column(name = "MY_PRICE_TXN_ID")
	private String transactionId;

	@Column(name = "REVISION")
	private String revision;

	@Column(name = "VERSION")
	private String version;

	@Column(name = "DEAL_STATUS")
	private String dealStatus;

	@Column(name = "PRICE_SCENARIO_ID")
	private Long priceScenarioId;

	@Column(name = "OFFER_ID")
	private String offerId;
	
	@Column(name = "CREATED_DATE")
	private Date createdDate;
	
	@Column(name = "MODIFIED_DATE")
	private Date modifiedDate;
	
	@Column(name = "ACTIVE_YN")
	private String activeYN;
	
	@Column(name = "RATE_LETTER_EXPIRES_ON")
	private String rateLetterExpiresOn;
	
	@Column(name = "QUOTE_URL")
	private String quoteUrl;
	
	@Column(name = "AUTO_APPROVAL_YN")
	private String autoApproval;
	
	@Column(name = "RATE_LETTER_TYPE")
	private String rlType;
	
	@Column(name = "QUOTE_TYPE")
	private String quoteType;
	
	@Column(name = "nx_mp_status_ind")
	private String nxMpStatusInd;
	
	@Column(name = "SOURCE_ID")
	private String sourceId;
	
	@Column(name = "ACTION")
	private String action;
	
	@Column(name = "NX_PED_STATUS_IND")
	private String nxPedStatusInd;
	
	@Column(name = "OPTY_ID")
	private String optyId;
	
	@Column(name = "OFFER")
	private String offer;
	
	@Column(name = "PRICING_MANAGER")
	private String pricingManager;
	
	@Column(name = "DEAL_DESC")
	private String dealDescription;
	
	@Column(name = "SV_ID")
	private String svId;
	
	@Column(name = "CONTRACT_PRICING_SCOPE")
	private String contractPricingScope;
  
  	@Override
	public String toString() {
		return "NxMpDeal [nxTxnId=" + nxTxnId + ", solutionId=" + solutionId + ", dealID=" + dealID + ", transactionId="
				+ transactionId + ", revision=" + revision + ", version=" + version + ", dealStatus=" + dealStatus
				+ ", offerId=" + offerId + ", nxMpStatusInd=" + nxMpStatusInd + ", sourceId=" + sourceId + ", action="
				+ action + ", nxPedStatusInd=" + nxPedStatusInd + ", optyId=" + optyId + ", offer=" + offer
				+ ", dealDescription=" + dealDescription + ", svId=" + svId + ", contractPricingScope="
				+ contractPricingScope + "]";
	}	
}
