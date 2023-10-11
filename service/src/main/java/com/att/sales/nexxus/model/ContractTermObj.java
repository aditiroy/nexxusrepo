package com.att.sales.nexxus.model;

import java.io.Serializable;
import java.math.BigDecimal;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The Class ContractTermObj.
 *
 * @author km017g
 */
@SuppressWarnings("serial")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class ContractTermObj implements Serializable{
	
	 /** The contract term. */
 	private String contractTerm;
	 
 	/** The base monthly price local. */
 	private BigDecimal baseMonthlyPriceLocal;
	 
 	/** The monthly price local. */
 	private BigDecimal monthlyPriceLocal;
	 
 	/** The one time price local. */
 	private BigDecimal oneTimePriceLocal;
	 
 	/** The base monthly price USD. */
 	private BigDecimal baseMonthlyPriceUSD;
	 
 	/** The monthly price USD. */
 	private BigDecimal monthlyPriceUSD;
	 
 	/** The one time price USD. */
 	private BigDecimal oneTimePriceUSD;
	 
	 /**
 	 * Gets the contract term.
 	 *
 	 * @return the contract term
 	 */
 	public String getContractTerm() {
		return contractTerm;
	}
	
	/**
	 * Sets the contract term.
	 *
	 * @param contractTerm the new contract term
	 */
	public void setContractTerm(String contractTerm) {
		this.contractTerm = contractTerm;
	}
	
	/**
	 * Gets the base monthly price local.
	 *
	 * @return the base monthly price local
	 */
	public BigDecimal getBaseMonthlyPriceLocal() {
		return baseMonthlyPriceLocal;
	}
	
	/**
	 * Sets the base monthly price local.
	 *
	 * @param baseMonthlyPriceLocal the new base monthly price local
	 */
	public void setBaseMonthlyPriceLocal(BigDecimal baseMonthlyPriceLocal) {
		this.baseMonthlyPriceLocal = baseMonthlyPriceLocal;
	}
	
	/**
	 * Gets the monthly price local.
	 *
	 * @return the monthly price local
	 */
	public BigDecimal getMonthlyPriceLocal() {
		return monthlyPriceLocal;
	}
	
	/**
	 * Sets the monthly price local.
	 *
	 * @param monthlyPriceLocal the new monthly price local
	 */
	public void setMonthlyPriceLocal(BigDecimal monthlyPriceLocal) {
		this.monthlyPriceLocal = monthlyPriceLocal;
	}
	
	/**
	 * Gets the one time price local.
	 *
	 * @return the one time price local
	 */
	public BigDecimal getOneTimePriceLocal() {
		return oneTimePriceLocal;
	}
	
	/**
	 * Sets the one time price local.
	 *
	 * @param oneTimePriceLocal the new one time price local
	 */
	public void setOneTimePriceLocal(BigDecimal oneTimePriceLocal) {
		this.oneTimePriceLocal = oneTimePriceLocal;
	}
	
	/**
	 * Gets the base monthly price USD.
	 *
	 * @return the base monthly price USD
	 */
	public BigDecimal getBaseMonthlyPriceUSD() {
		return baseMonthlyPriceUSD;
	}
	
	/**
	 * Sets the base monthly price USD.
	 *
	 * @param baseMonthlyPriceUSD the new base monthly price USD
	 */
	public void setBaseMonthlyPriceUSD(BigDecimal baseMonthlyPriceUSD) {
		this.baseMonthlyPriceUSD = baseMonthlyPriceUSD;
	}
	
	/**
	 * Gets the monthly price USD.
	 *
	 * @return the monthly price USD
	 */
	public BigDecimal getMonthlyPriceUSD() {
		return monthlyPriceUSD;
	}
	
	/**
	 * Sets the monthly price USD.
	 *
	 * @param monthlyPriceUSD the new monthly price USD
	 */
	public void setMonthlyPriceUSD(BigDecimal monthlyPriceUSD) {
		this.monthlyPriceUSD = monthlyPriceUSD;
	}
	
	/**
	 * Gets the one time price USD.
	 *
	 * @return the one time price USD
	 */
	public BigDecimal getOneTimePriceUSD() {
		return oneTimePriceUSD;
	}
	
	/**
	 * Sets the one time price USD.
	 *
	 * @param oneTimePriceUSD the new one time price USD
	 */
	public void setOneTimePriceUSD(BigDecimal oneTimePriceUSD) {
		this.oneTimePriceUSD = oneTimePriceUSD;
	}
	
	
	
}
