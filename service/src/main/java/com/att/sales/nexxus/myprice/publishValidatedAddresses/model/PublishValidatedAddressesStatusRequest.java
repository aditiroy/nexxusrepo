package com.att.sales.nexxus.myprice.publishValidatedAddresses.model;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * @author IndraSingh
 * */

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class PublishValidatedAddressesStatusRequest {

	private String id;
	private String action;

}
