package com.att.sales.nexxus.userdetails.service;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.nexxus.userdetails.model.UserDetailsRequest;
import com.att.sales.nexxus.userdetails.model.UserDetailsResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


/**
 * The Interface UserDetailsService.
 */
public interface UserDetailsService {
	
	/**
	 * Retreive user details.
	 *
	 * @param request the request
	 * @return the user details response
	 */
	ServiceResponse retreiveUserDetails(UserDetailsRequest request);
	

}
