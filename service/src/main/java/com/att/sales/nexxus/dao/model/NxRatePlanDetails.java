package com.att.sales.nexxus.dao.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author KRani
 *
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "NX_RATE_PLAN_DETAILS")
@NamedQuery(name="NxRatePlanDetails.findAll", query="SELECT n FROM NxRatePlanDetails n")
public class NxRatePlanDetails implements Serializable  {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "ID")
	private Long id;
	
	@Column(name = "RATE_PLAN_ID_EXTERNAL")
	private Long ratePlanIdExternal;
	
	@Column(name = "RATE_PLAN_ID")
	private Long ratePlanId;
	
	@Column(name = "SOC_DATE")
	private String socDate;

	@Column(name = "RATE_PLAN_EFF_DATE")
	private String ratePlanEffectiveDate;
	
	@Column(name = "RATE_PLAN_END_DATE")
	private String ratePlanenddate;
	
	@Column(name = "PRODUCT")
	private String product;
	
	@Column(name = "ERATE_INDICATOR")
	private String erateIndicator;
	
	@Column(name = "ACTIVE_YN")
	private String activeYn;
}
