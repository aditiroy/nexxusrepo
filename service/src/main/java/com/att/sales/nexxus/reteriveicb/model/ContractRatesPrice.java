package com.att.sales.nexxus.reteriveicb.model;

public class ContractRatesPrice {
	private String rateId;
	private String usocId;
	private String rateDescription;
	private String rateType;
	private String rateCurrency;
	private double rate;
	private String pricingTier;
	private String portType;

	public String getPortType() {
		return portType;
	}

	public void setPortType(String portType) {
		this.portType = portType;
	}

	public String getRateId() {
		return rateId;
	}

	public void setRateId(String rateId) {
		this.rateId = rateId;
	}

	public String getUsocId() {
		return usocId;
	}

	public void setUsocId(String usocId) {
		this.usocId = usocId;
	}

	public String getRateDescription() {
		return rateDescription;
	}

	public void setRateDescription(String rateDescription) {
		this.rateDescription = rateDescription;
	}

	public String getRateType() {
		return rateType;
	}

	public void setRateType(String rateType) {
		this.rateType = rateType;
	}

	public String getRateCurrency() {
		return rateCurrency;
	}

	public void setRateCurrency(String rateCurrency) {
		this.rateCurrency = rateCurrency;
	}

	public double getRate() {
		return rate;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}

	public String getPricingTier() {
		return pricingTier;
	}

	public void setPricingTier(String pricingTier) {
		this.pricingTier = pricingTier;
	}

	@Override
	public String toString() {
		return "ContractRatesPrice [rateId=" + rateId + ", usocId=" + usocId + ", rateDescription=" + rateDescription
				+ ", rateType=" + rateType + ", rateCurrency=" + rateCurrency + ", rate=" + rate + ", pricingTier="
				+ pricingTier + ", portType=" + portType + "]";
	}

}
