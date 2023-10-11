package com.att.sales.nexxus.reteriveicb.model;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class ContractRates {
	private String chargeDescription;
	private String speed;
	private ContractRatesPrice price;
	
	
	public String getChargeDescription() {
		return chargeDescription;
	}

	public void setChargeDescription(String chargeDescription) {
		this.chargeDescription = chargeDescription;
	}

	public String getSpeed() {
		return speed;
	}

	public void setSpeed(String speed) {
		this.speed = speed;
	}

	public ContractRatesPrice getPrice() {
		return price;
	}

	public void setPrice(ContractRatesPrice price) {
		this.price = price;
	}

	@Override
	public String toString() {
		return "ContractRates [chargeDescription=" + chargeDescription + ", price="
				+ price + ", speed=" + speed + "]";
	}

}
