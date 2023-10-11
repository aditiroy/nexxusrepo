package com.att.sales.nexxus.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UsageRuleObj {

	@JsonProperty("usageRule")
	private List<UsageRule> UsageRule;

	public List<UsageRule> getUsageRule() {
		return UsageRule;
	}

	public void setUsageRule(List<UsageRule> usageRule) {
		UsageRule = usageRule;
	}
	
	

}
