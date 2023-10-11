package com.att.sales.nexxus.myprice.transaction.model;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.att.sales.framework.model.ServiceResponse;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author Laxman Honawad
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
@ToString
public class GetTransactionLineResponse extends ServiceResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<GetTransactionLineItem> items;
	
	private Long totalResults;
	
	private Long offset;
	
	private Long limit;
	
	private Long count;
	
	private boolean hasMore;
	
	//private GetTransactionPricingDocuments documents;
	
	private List<Links> links;

}
