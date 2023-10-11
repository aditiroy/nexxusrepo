package com.att.sales.nexxus.model;

import java.io.File;
import java.io.Serializable;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.att.sales.framework.model.ServiceResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class ZipFileResponse.
 *
 * @author RudreshWaladaunki
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ZipFileResponse extends ServiceResponse implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The zip file name. */
	@JsonProperty("zipFileName")
	private String zipFileName;
	
	/** The file zip. */
	private File fileZip;
	
	/** The zip file. */
	@JsonProperty("zipFile")
	private String zipFile;
	
	/**
	 * Gets the zip file name.
	 *
	 * @return the zip file name
	 */
	public String getZipFileName() {
		return zipFileName;
	}
	
	/**
	 * Sets the zip file name.
	 *
	 * @param zipFileName the new zip file name
	 */
	public void setZipFileName(String zipFileName) {
		this.zipFileName = zipFileName;
	}
	
	/**
	 * Gets the zip file.
	 *
	 * @return the zipFile
	 */
	public String getZipFile() {
		return zipFile;
	}
	
	/**
	 * Sets the zip file.
	 *
	 * @param zipFile the zipFile to set
	 */
	public void setZipFile(String zipFile) {
		this.zipFile = zipFile;
	}
	
	/**
	 * Gets the file zip.
	 *
	 * @return the fileZip
	 */
	public File getFileZip() {
		return fileZip;
	}
	
	/**
	 * Sets the file zip.
	 *
	 * @param fileZip the fileZip to set
	 */
	public void setFileZip(File fileZip) {
		this.fileZip = fileZip;
	}
		
}
