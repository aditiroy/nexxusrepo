package com.att.sales.nexxus.reteriveicb.model;

/**
 * @author Ruchi
 */
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
public class PriceScenarios {
	private Long priceScenarioId;
	private Long ethernetPaymentPlan;
	private List<ChargeComponents> chargeComponents;


}
