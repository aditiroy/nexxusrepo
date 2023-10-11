package com.att.sales.nexxus.myprice.transaction.model;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.att.sales.framework.model.ServiceResponse;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
public class Aseod3PAMileageReqRatesResponse extends ServiceResponse{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<Aseod3PAMileageReqRatesListItem> items;

}
