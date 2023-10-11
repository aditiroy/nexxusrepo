package com.att.sales.nexxus.dao.model;

import java.io.Serializable;

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

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "NX_MP_PRICE_DETAILS")
public class NxMpPriceDetails implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "SEQ_NX_MP_PRICE_DETAILS_ID_GENERATOR", sequenceName = "SEQ_NX_MP_PRICE_DETAILS", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_NX_MP_PRICE_DETAILS_ID_GENERATOR")
	@Column(name = "NX_PRICE_DETAILS_ID")
	private Long nxPriceDetailsId;

	@Column(name = "NX_DESIGN_ID")
	private Long nxDesignId;

	@Column(name = "NX_SITE_COUNTRY")
	private String nxSiteCountry;

	@Column(name = "NX_TXN_ID")
	private Long nxTxnId;

	@Column(name = "MP_DOCUMENT_NUMBER")
	private Long mpDocumentNumber;

	@Column(name = "CURRENCY")
	private String currency;

	@Column(name = "REQUESTED_NRC_DISC_PERCENTAGE")
	private Float requestedNRCDiscPercentage;

	@Column(name = "REQUESTED_MRC_DISC_PERCENTAGE")
	private Float requestedMRCDiscPercentage;

	@Column(name = "REQUESTED_MRC_EFFECTIVE_PRICE")
	private Float requestedMRCEffectivePrice;

	@Column(name = "REQUESTED_NRC_EFFECTIVE_PRICE")
	private Float requestedNRCEffectivePrice;

	@Column(name = "APPROVED_NRC_DISC")
	private Float approvedNRCDisc;

	@Column(name = "APPROVED_NRC_EFFECTIVE_PRICE")
	private Double approvedNRCEffectivePrice;

	@Column(name = "APPROVED_NRC_NET_EFFECT_PRICE")
	private Double approvedNRCNetEffectivePrice;

	@Column(name = "APPROVED_MRC_DISC")
	private Float approvedMRCDisc;

	@Column(name = "APPROVED_MRC_EFFECTIVE_PRICE")
	private Double approvedMRCEffectivePrice;

	@Column(name = "APPROVED_MRC_NET_EFFECT_PRICE")
	private Double approvedMRCNetEffectivePrice;

	@Column(name = "FREQUENCY")
	private String frequency;

	@Column(name = "BEID")
	private String beid;

	@Column(name = "COMPONENT_TYPE")
	private String componentType;

	@Column(name = "COMPONENT_ID")
	private Long componentId;

	@Column(name = "COMPONENT_PARENT_ID")
	private Long componentParentId;

	@Column(name = "SPCL_CONSTRUCT_APP_NRC")
	private String specialConstructionAppNRC;

	@Column(name = "ASR_ITEM_ID")
	private String asrItemId;
	
	@Column(name = "TERM")
	private Long term;
	
	@Column(name = "JURISDICTION")
	private String jurisdiction;
	
	@Column(name = "SOC_VERSION")
	private String socVersion;
	
	@Column(name = "PROD_RRATE_ID")
	private Long prodRateId;
	
	@Column(name = "RDS_PRICE_TYPE")
	private String rdsPriceType;
	
	@Column(name = "ETH_TOKEN_ID")
	private String ethTokenId;
	
	@Column(name = "IS_ACCESS")
	private String isAccess;
	
	@Column(name = "UNIQUE_ID")
	private String uniqueId;
	
	@Column(name = "RATE_PLAN_ID")
	private Long ratePlanId;
	
	@Column(name = "RATE_PLAN_ID_EXTERNAL")
	private Long ratePlanIdExternal;
}