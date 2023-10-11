package com.att.sales.nexxus.nxPEDstatus.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.beans.factory.annotation.Autowired;

import com.att.sales.framework.model.ServiceResponse;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
public class GetNxPEDStatusResponse extends ServiceResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Autowired
	private StatusMessage statusMessage;

	@Autowired
	private Solution solution;
}
