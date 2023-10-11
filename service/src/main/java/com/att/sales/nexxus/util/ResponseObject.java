/**
 * 
 */
package com.att.sales.nexxus.util;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.Setter;

/**
 * @author ShruthiCJ
 *
 */
@Getter
@Setter
public class ResponseObject {
	
	private String body;
	private HttpStatus statusCode;
	private Integer statusCodeVal;
	private HttpHeaders headers;
	

}
