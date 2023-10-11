package com.att.sales.nexxus.template.model;

import com.att.sales.framework.model.ServiceResponse;

/**
 * The Class NxTemplateUploadResponse.
 */
public class NxTemplateUploadResponse extends ServiceResponse {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 8231083854985493188L;
	
	
	/** The file name. */
	private String fileName;
	
	/**
	 * Gets the file name.
	 *
	 * @return the file name
	 */
	public String getFileName() {
		return fileName;
	}
	
	/**
	 * Sets the file name.
	 *
	 * @param fileName the new file name
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	

}
