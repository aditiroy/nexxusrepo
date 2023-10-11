package com.att.sales.nexxus.reteriveicb.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The Class PriceAttributes.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
public class PriceAttributes {
	
	/** The product rate id. */
	private Long productRateId;
	
	/** The beid. */
	private String beid;
	
	/** The rate description. */
	private String rateDescription;
	
	/** The price catalog. */
	private String priceCatalog;
	
	/** The local list price. */
	private Double localListPrice;
	
	/** The target list price. */
	private Double targetListPrice;
	
	private Double icbDesiredDiscPerc;
	
	/** The price type. */
	private String priceType;
	
	/** The price unit. */
	private String priceUnit;
	
	/** The frequency. */
	private String frequency;
	
	/** The monthly surcharge. */
	private Long monthlySurcharge;
	
	/** The discount. */
	private Double discount;
	
	/** The discount id. */
	private String discountId;
	
	/** The quantity. */
	private String quantity;
	
	/** The local net price. */
	private Double localNetPrice;
	
	/** The target net price. */
	private Double targetNetPrice;
	
	/** The local total price. */
	private Double localTotalPrice;
	
	/** The target total price. */
	private Double targetTotalPrice;
	
	/** The local currency. */
	private String localCurrency;
	
	/** The target currency. */
	private String targetCurrency;
	
	/** The rds price type. */
	private String rdsPriceType;
	
	/** The price name. */
	private String priceName;
	
	/** The type of inventory. */
	private String typeOfInventory;
	
	/** The price in USD. */
	private String priceInUSD;
	
	/** The price scenario id. */
	private Long priceScenarioId;
	
	/** The rateGroup. */
	private String rateGroup;
	
	/** The external billing system. */
	private String externalBillingSystem;
	
	/** The rate plan id. */
	private Long ratePlanId;
	
	/** The price comp type. */
	private String priceCompType;
	
	private String pvcId;
	
	private String chargeCodeId;

	private Float requestedNRCRate;
	
	private Float requestedNRCDiscPercentage;
	
	private Float requestedMRCRate;
	
	private Float requestedMRCDiscPercentage;
	
	private String priceModifiedInd;
	
	private Float requestedDiscount;
	
	private Float requestedRate;
	
	private Long term;
	
	private String priceGroup;
	
	/** componentId as referencePortId for FMO*/
	private Long referencePortId;
		
	/** The component type for FMO*/
	private String componentType;
	
	/** The component parent id for FMO */
	private Long componentParentId;
	
	private String country;
	
	private String secondaryKeys;
	
	private Long lineItemId;
	
	private String nrcBeid;
	
	private String uniqueId;
	
	private String elementType;
	
	private String reqPriceType;

}
