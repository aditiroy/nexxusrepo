package com.att.sales.nexxus.admin.model;

import java.io.InputStream;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class BulkUploadEthTokenRequest {

	private String fileName;

	private InputStream inputStream;

	private String action;

	private Long nxSolutionId;

	private String userId;

}
