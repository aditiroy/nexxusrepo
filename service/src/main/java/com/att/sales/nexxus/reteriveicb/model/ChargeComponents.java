package com.att.sales.nexxus.reteriveicb.model;

/**
 * @author Ruchi
 */

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
public class ChargeComponents {
	
	private String chargeDescription;
	private String componentSpeed;
	private Long newQty;
	private Long existingQty;
	private Long currentMRC;
	private Long listPriceNRC;
	private Long listPriceMRC;
	private Long requestedNRCRate;
	private Long requestedNRCDiscPercentage;
	private Long requestedMRCRate;
	private Long requestedMRCDiscPercentage;


}
