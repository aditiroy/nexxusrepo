
package com.att.sales.nexxus.myprice.transaction.model;

/**
 * @author aa316k
 *
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
public class GetTransactionLineItemPriceNew {

	private Double value;
	
	private String currency;

}
