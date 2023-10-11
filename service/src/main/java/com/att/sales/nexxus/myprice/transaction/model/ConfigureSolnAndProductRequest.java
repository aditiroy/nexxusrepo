package com.att.sales.nexxus.myprice.transaction.model;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.att.sales.framework.validation.APIFieldProperty;
import com.att.sales.nexxus.reteriveicb.model.Site;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author KumariMuktta
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
public class ConfigureSolnAndProductRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	@APIFieldProperty(required=true)
	private String myPriceTransId;
	
	private Long nxTxnId;
	
	private Long nxDesignId;
	
	private Site site;
	
	private String offerName;

}
