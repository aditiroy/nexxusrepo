package com.att.sales.nexxus.common;

/**
 * The Interface MessageConstants.
 *
 * @author Lijo Manickathan John
 */
public interface MessageConstants {

	// Common Error Constants
	/** The constant for "service not defined". */
	public static final String REQUEST_COMPLETED_SUCCESSFULLY = "M00000";

	/** The Constant SERVICE_NOT_DEFINED. */
	public static final String SERVICE_NOT_DEFINED = "M00001";

	/** The Constant BAD_REQUEST_CODE. */
	public static final String BAD_REQUEST_CODE = "M00002";

	/** The Constant PROCESS_ERROR_CODE. */
	public static final String PROCESS_ERROR_CODE = "M00003";

	/** The Constant USERNAME_INVALID. */
	// Example Error Codes
	public static final String USERNAME_INVALID = "M00089";
	
	/** The Constant DQID_INVALID. */
	public static final String DQID_INVALID = "M00089";

	/** The Constant MESSAGE_EXCEPTION. */
	public static final String MESSAGE_EXCEPTION = "M00101";

	/** The Constant ADDRESS_EXCEPTION. */
	public static final String ADDRESS_EXCEPTION = "M00103";

	/** The Constant HTTP_URL_EXCEPTION. */
	public static final String HTTP_URL_EXCEPTION = "M04310";

	/** The Constant MANDATORY_FIELDS_MISSING. */
	public static final String MANDATORY_FIELDS_MISSING = "M04331";

	/** The Constant DATA_NOT_FOUND. */
	public static final String DATA_NOT_FOUND = "M04332";

	/** The Constant DATALOAD_FILE_NAME_EXCEPTION. */
	public static final String DATALOAD_FILE_NAME_EXCEPTION = "M04306";

	/** The Constant OFFER_NOT_FOUND. */
	public static final String OFFER_NOT_FOUND = "M06934";

	/** The Constant PRODUCT_COMPONENT_NOT_FOUND. */
	public static final String PRODUCT_COMPONENT_NOT_FOUND = "M00301";

	/** The Constant PRODUCT_NOT_FOUND. */
	public static final String PRODUCT_NOT_FOUND = "M03431";

	/** The Constant FILE_PROCESSING_DELAYED. */
	//PDDM error codes
	public static final String FILE_PROCESSING_DELAYED = "WF.ERR.001";

	/** The Constant FILE_FORMAT_INVALID_OR_CORRUPTED. */
	public static final String FILE_FORMAT_INVALID_OR_CORRUPTED = "WF.ERR.002";

	/** The Constant INVALID_DATA. */
	public static final String INVALID_DATA = "WF.ERR.003";

	/** The Constant OTHER_ISSUES. */
	public static final String OTHER_ISSUES = "WF.ERR.004";

	/** The Constant REGISTRATION_SUCCESSFUL. */
	public static final String REGISTRATION_SUCCESSFUL = "M10001";
	
	/** The Constant REQUEST_ID_NOT_FOUND. */
	public static final String REQUEST_ID_NOT_FOUND = "M10239";
	
	/** The Constant FILE_ID_NOT_FOUND. */
	public static final String FILE_ID_NOT_FOUND = "M09231";

	/** The Constant FE4001. */
	public static final String FE4001 = "FE4001";
	
	/** The Constant RULE_EXIST. */
	public static final String RULE_EXIST="M92671";
	
	/** The Constant FAIL. */
	public static final String FAIL = "FAIL";

	/** The Constant INVALID_OPTYID_OR_HRID. */
	public static final String INVALID_OPTYID_OR_HRID = "M00077";

	/** The Constant INVALID_URL. */
	public static final String INVALID_URL = "M00022";
	
	/** The Constant FILE_GENERATION_FAILED. */
	public static final String FILE_GENERATION_FAILED = "M09232";
	
	/** The Constant INVALID_SOLUTION_ID. */
	public static final String INVALID_SOLUTION_ID = "M00021";
	
	/** The Constant FILE_DOWNLOAD_FAILED. */
	public static final String FILE_DOWNLOAD_FAILED = "M00202";
	
	/** The Constant INVALID_REQUEST. */
	public static final String INVALID_REQUEST = "M00203";
	
	/** The Constant STATUS_NOT_SUCCESS. */
	public static final String STATUS_NOT_SUCCESS = "M00204";
	
	/** The Constant INPROGRESS. */
	public static final String INPROGRESS = "M00205";
	
	/** The Constant SUCCESS_WITH_FALLOUT. */
	public static final String SUCCESS_WITH_FALLOUT = "M00206";
	
	/** The Constant SUCCESS. */
	public static final String SUCCESS = "M00207";
	
	/** The Constant FAILED. */
	public static final String FAILED = "M00208";
	
	/** The Constant FALLOUT. */
	public static final String FALLOUT = "M00209";
	
	/** The Constant RETRIGGER_FAILURE_INTERNAL. */
	public static final String RETRIGGER_FAILURE_INTERNAL = "M00171";
	
	/** The Constant RETRIGGER_FAILURE_EXTERNAL. */
	public static final String RETRIGGER_FAILURE_EXTERNAL = "M00172";
	
	public static final String NO_DATA_FOUND = "M00201";
	
	/** The Constant INVALID_DATA_CODE. */
	public static final String INVALID_DATA_CODE= "204";
	
	/** The Constant INVALID_DATA_MSG. */
	public static final String INVALID_DATA_MSG = "INVALID DATA";
	
	public static final String OPTYID_CALL_FAILLED = "M00225";
	
}
