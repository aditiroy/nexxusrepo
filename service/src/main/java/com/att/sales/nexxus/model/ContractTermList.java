package com.att.sales.nexxus.model;

import java.io.Serializable;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


/**
 * The Class ContractTermList.
 *
 * @author km017g
 */
@SuppressWarnings("serial")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class ContractTermList implements Serializable{

	/** The contract term obj. */
	private ContractTermObj contractTermObj;

	/**
	 * Gets the contract term obj.
	 *
	 * @return the contract term obj
	 */
	public ContractTermObj getContractTermObj() {
		return contractTermObj;
	}

	/**
	 * Sets the contract term obj.
	 *
	 * @param contractTermObj the new contract term obj
	 */
	public void setContractTermObj(ContractTermObj contractTermObj) {
		this.contractTermObj = contractTermObj;
	}
	
}
