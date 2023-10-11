package com.att.sales.nexxus.constant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The Class TemplateFileConstants.
 *
 * @author vt393d
 */
public class TemplateFileConstants {
	
	/**
	 * Instantiates a new template file constants.
	 */
	private TemplateFileConstants() {}
	
	/** The Constant XLSX_EXT. */
	public static final String XLSX_EXT = ".xlsx";
	
	/** The Constant XLSM_EXT. */
	public static final String XLSM_EXT = ".xlsm";
	
	/** The Constant ERROR_FILE_EXTENSION_INVALID. */
	public static final String ERROR_FILE_EXTENSION_INVALID = "TF0002";
	
	/** The Constant ERROR_FILE_NOT_EXISTS. */
	public static final String ERROR_FILE_NOT_EXISTS = "TF0001";
	
	/** The Constant ERROR_FILE_TYPE_NOT_EXISTS. */
	public static final String ERROR_FILE_TYPE_NOT_EXISTS = "TF0003";
	
	/** The Constant ERROR_FILE_INVALID_FOR_OUTPUT_FILE. */
	public static final String ERROR_FILE_INVALID_FOR_OUTPUT_FILE = "TF0004";
	
	/** The Constant ERROR_FILE_INVALID_FOR_CDT_FILE. */
	public static final String ERROR_FILE_INVALID_FOR_CDT_FILE= "TF0005";
	
	/** The Constant OUTPUT_FILE. */
	public static final String OUTPUT_FILE="outputFile";
	
	/** The Constant CDT_FILE. */
	public static final String CDT_FILE="cdtFile";
	
	/** The Constant FILE_TYPE. */
	public static final String FILE_TYPE="fileType";
	
	/** The Constant FILE. */
	public static final String FILE="file";
	
	/** The Constant REQUEST_COMPLETED_SUCCESSFULLY_MSG. */
	public static final String REQUEST_COMPLETED_SUCCESSFULLY_MSG="REQUEST_COMPLETED_SUCCESSFULLY";
	
	/** The Constant VALID_FILE_TYPES. */
	public static final  List<String> VALID_FILE_TYPES;
	 static {
		 List<String> temp=new ArrayList<>();
		 temp.add(OUTPUT_FILE);
		 temp.add(CDT_FILE);
		 VALID_FILE_TYPES = Collections.unmodifiableList(temp);
	}
}
