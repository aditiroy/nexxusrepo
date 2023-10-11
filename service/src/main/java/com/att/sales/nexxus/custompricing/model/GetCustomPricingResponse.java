package com.att.sales.nexxus.custompricing.model;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.att.sales.framework.model.ServiceResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class GetCustomPricingResponse extends ServiceResponse{
		
	private List<DealSummary> dealSummary;
	
	private List<CustomerDetail> customerDetail;
	
	private List<ProductLevelSummary> productLevelSummary;

	

}
