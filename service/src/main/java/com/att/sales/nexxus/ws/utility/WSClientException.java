package com.att.sales.nexxus.ws.utility;

import java.util.List;

import com.att.sales.framework.exception.SalesBusinessException;

import lombok.Getter;

@Getter
public class WSClientException extends SalesBusinessException{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5720824997565351397L;
	
	private  String faultString;
	private  String faultCode;
	private String messageCode;
	private  Throwable exception;
	private List<String> faultStrings;
	
	
	public WSClientException(String messageCode,String faultCode,String faultString,Throwable exception){
		super(messageCode);
		this.faultCode=faultCode;
		this.faultString=faultString;
		this.exception=exception;
	}
	
	public WSClientException(String messageCode,String faultString,Throwable exception){
		super(messageCode);
		this.faultString=faultString;
		this.exception=exception;
	}
	
	@Override
	public String toString() {
		return "{\"faultstring\":\"" + faultString + "\", \"faultcode\":\"" + faultCode + "\"}";
	}
}
