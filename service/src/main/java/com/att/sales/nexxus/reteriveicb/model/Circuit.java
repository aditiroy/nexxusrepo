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
public class Circuit {
	private String typeOfInventory;
	private List<Component> component;
	private String designStatus;
	private String cancellationReason;
	private PriceDetails priceDetails;
	private String purchaseOrderNumber;
	private String designModifiedInd;
	private String designVersion;
	private String icsc;
	private String nssEngagement;
	private String accessCarrierNameAbbreviation;
	private Double specialConstructionCharge;
	private String specialConstructionHandling;
	private String specialConstructionHandlingNotes;
}
