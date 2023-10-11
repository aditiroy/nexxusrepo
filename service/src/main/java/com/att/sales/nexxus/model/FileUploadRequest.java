package com.att.sales.nexxus.model;

import java.io.InputStream;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class FileUploadRequest {
	
	private String fileName;

	private String fileContent;

	private String action;

	private Long nxSolutionId;

	private String userId;
	
	private String product;

}
