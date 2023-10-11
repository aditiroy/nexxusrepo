package com.att.sales.nexxus.myprice.publishValidatedAddresses.model;

import com.att.aft.dme2.internal.jackson.map.annotate.JsonSerialize;
import com.att.sales.framework.model.ServiceResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author IndraSingh
 * */

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class PublishValidatedAddressesStatusResponse extends ServiceResponse{

	private static final long serialVersionUID = 1L;

}
