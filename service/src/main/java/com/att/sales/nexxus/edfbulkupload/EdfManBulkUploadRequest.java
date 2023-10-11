package com.att.sales.nexxus.edfbulkupload;

import java.io.InputStream;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * 
 * @author KRani
 *
 */

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class EdfManBulkUploadRequest {
	private String fileName;

	private InputStream inputStream;
	
	//private String action;
	
	private Long nxSolutionId;
	
	private String optyId;

	private String userId;
}