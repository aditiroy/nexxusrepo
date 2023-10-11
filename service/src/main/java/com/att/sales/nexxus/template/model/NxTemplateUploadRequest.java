package com.att.sales.nexxus.template.model;

import java.io.InputStream;

/**
 * The Class NxTemplateUploadRequest.
 *
 * @author vt393d
 */
public class NxTemplateUploadRequest {
	
	
	/** The file name. */
	private String fileName;
	
	/** The file type. */
	private String fileType;
	
	/** The input stream. */
	private InputStream inputStream;
	
	/** The extension. */
	private String extension;

	/**
	 * Gets the file type.
	 *
	 * @return the file type
	 */
	public String getFileType() {
		return fileType;
	}
	
	/**
	 * Sets the file type.
	 *
	 * @param fileType the new file type
	 */
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	
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
	
	/**
	 * Gets the input stream.
	 *
	 * @return the input stream
	 */
	public InputStream getInputStream() {
		return inputStream;
	}
	
	/**
	 * Sets the input stream.
	 *
	 * @param inputStream the new input stream
	 */
	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}
	
	/**
	 * Gets the extension.
	 *
	 * @return the extension
	 */
	public String getExtension() {
		return extension;
	}
	
	/**
	 * Sets the extension.
	 *
	 * @param extension the new extension
	 */
	public void setExtension(String extension) {
		this.extension = extension;
	}
	
	
	
	

}
