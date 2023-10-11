package com.att.sales.nexxus.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class Inventoryfiles.
 *
 * * @author(ar896d) 
 * 
 * The Inventoryfiles class 
 *
 */
public class Inventoryfiles {
	@JsonProperty("fileCount")
	  private Integer fileCount ;

	  @JsonProperty("fileNames")
	  private List<String> fileNames = null;

	
	public Integer getFileCount() {
		return fileCount;
	}

	public void setFileCount(Integer fileCount) {
		this.fileCount = fileCount;
	}

	public List<String> getFileNames() {
		return fileNames;
	}

	public void setFileNames(List<String> fileNames) {
		this.fileNames = fileNames;
	}

	  
}
