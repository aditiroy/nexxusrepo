package com.att.sales.nexxus.myprice.transaction.model;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@Getter
@Setter
@NoArgsConstructor
public class AllIncPrices3PA {

	private List<AseodCirReqRatesLineItem> allIncCirPrices;
	
	private List<Aseod3PAMileageReqRatesListItem> allIncMileagePrices;
	
	private List<Aseod3PAPortReqRatesLineItem> allIncPortPrices;
}
