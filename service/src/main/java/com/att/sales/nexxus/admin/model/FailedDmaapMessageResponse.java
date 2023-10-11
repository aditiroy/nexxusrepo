package com.att.sales.nexxus.admin.model;

import java.sql.Blob;

import com.att.sales.framework.model.ServiceResponse;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FailedDmaapMessageResponse extends ServiceResponse {
 
	private static final long serialVersionUID = 1L;
	
	private Blob file;
	
	private String fileName;
}
