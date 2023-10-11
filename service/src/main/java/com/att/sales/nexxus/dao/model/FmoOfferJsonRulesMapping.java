package com.att.sales.nexxus.dao.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


/**
 * The Class FmoOfferJsonRulesMapping.
 *
 * @author vt393d
 * 
 * Model class use maintain many to many relation with offer id 
 * and JSON rules from FMO_OFFER_JSON_RULES_MAPPING
 */
@Entity
@Table(name="FMO_OFFER_JSON_RULES_MAPPING")
public class FmoOfferJsonRulesMapping {
	
	/** The id. */
	@Id
	@Column(name="ID")
	private Long id;
	
	/** The offer id. */
	@Column(name="OFFER_ID")
	private Long offerId;
	
	/** The json rule id. */
	@Column(name="JSON_RULE_ID")
	private Long jsonRuleId;
	
	/** The active. */
	@Column(name="ACTIVE")
	private String active;
	
	/** The fmo rules. */
	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "JSON_RULE_ID",referencedColumnName="ID",insertable=false, updatable=false)
	private FmoJsonRulesModel fmoRules;

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Gets the offer id.
	 *
	 * @return the offer id
	 */
	public Long getOfferId() {
		return offerId;
	}

	/**
	 * Sets the offer id.
	 *
	 * @param offerId the new offer id
	 */
	public void setOfferId(Long offerId) {
		this.offerId = offerId;
	}

	/**
	 * Gets the json rule id.
	 *
	 * @return the json rule id
	 */
	public Long getJsonRuleId() {
		return jsonRuleId;
	}

	/**
	 * Sets the json rule id.
	 *
	 * @param jsonRuleId the new json rule id
	 */
	public void setJsonRuleId(Long jsonRuleId) {
		this.jsonRuleId = jsonRuleId;
	}

	/**
	 * Gets the active.
	 *
	 * @return the active
	 */
	public String getActive() {
		return active;
	}

	/**
	 * Sets the active.
	 *
	 * @param active the new active
	 */
	public void setActive(String active) {
		this.active = active;
	}

	/**
	 * Gets the fmo rules.
	 *
	 * @return the fmo rules
	 */
	public FmoJsonRulesModel getFmoRules() {
		return fmoRules;
	}

	/**
	 * Sets the fmo rules.
	 *
	 * @param fmoRules the new fmo rules
	 */
	public void setFmoRules(FmoJsonRulesModel fmoRules) {
		this.fmoRules = fmoRules;
	}

	
	
	

}
