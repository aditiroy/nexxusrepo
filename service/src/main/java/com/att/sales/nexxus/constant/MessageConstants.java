package com.att.sales.nexxus.constant;

/**
 * The Class MessageConstants.
 */
public class MessageConstants {

	/** The Constant REQUEST_COMPLETED_SUCCESSFULLY. */
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
	public static final String DQID_INVALID = "M00090";
	
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
	// CPC error codes
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
	public static final String RULE_EXIST = "M92671";
	
	/** The Constant PROCESS_JSON_ERROR_CODE. */
	public static final String PROCESS_JSON_ERROR_CODE = "M00004";
	
	/** The Constant ATTID_INVALID. */
	public static final String ATTID_INVALID = "M00170";
	
	/** The Constant OPTY_ID_NOT_FOUND. */
	
	//transmitDesignData
	public static final String OPTY_ID_NOT_FOUND = "M01101";
	
	public static final String SOLUTION_ID_NOT_FOUND = "M01102";
	
	/** The Constant PD_SOLUTION_ID_NOT_FOUND. */
	public static final String NX_SOLUTION_ID_NOT_FOUND = "M01103";
	
	/** The Constant ASR_ITEM_ID_NOT_FOUND. */
	public static final String ASR_ITEM_ID_NOT_FOUND = "M01104";
	
	/** The Constant STATUS_CODE_NOT_FOUND. */
	public static final String STATUS_CODE_NOT_FOUND = "M01105";
	
	/** The Constant CONFIRMED_INTERVAL. */
	public static final String CONFIRMED_INTERVAL_NOT_FOUND = "M01106";
	
	/** The Constant ESTIMATED_INTERVAL. */
	public static final String ESTIMATED_INTERVAL_NOT_FOUND = "M01107";
	
	/** The Constant END_POINT_TYPE_NOT_FOUND. */
	public static final String END_POINT_TYPE_NOT_FOUND = "M01108";
	
	/** The Constant EDGELESS_DESIGN_IND_NOT_FOUND. */
	public static final String EDGELESS_DESIGN_IND_NOT_FOUND = "M01109";
	
	/** The Constant LOCATION_CLLI_NOT_FOUND. */
	public static final String LOCATION_CLLI_NOT_FOUND = "M01110";
	
	/** The Constant ALTERNATE_SWC_CLLI_NOT_FOUND. */
	public static final String ALTERNATE_SWC_CLLI_NOT_FOUND = "M01111";
	
	/** The Constant FIRST_NAME_NOT_FOUND. */
	public static final String FIRST_NAME_NOT_FOUND = "M01112";
	
	/** The Constant LAST_NAME_TYPE_NOT_FOUND. */
	public static final String LAST_NAME_TYPE_NOT_FOUND = "M01113";
	
	/** The Constant ATT_UID_TYPE_NOT_FOUND. */
	public static final String ATT_UID_TYPE_NOT_FOUND = "M01114";
	
	public static final String CIRCUIT_CANCEL_REASON_NOT_FOUND = "M01115";
	
	public static final String REQUEST_PROCESSING_ERROR = "M01116";
	
	public static final String TDDR_DATA_NOT_FOUND = "M02010";
	
	public static final String SOAP_CLIENT_PROCESSING_ERROR = "M01117";
	
	public static final String CONFIG_DESIGN_PROCESSING_ERROR = "M01118";
	
	public static final String CONFIG_SOL_PROD_PROCESSING_ERROR = "M01119";
	
	public static final Integer FALLOUT = 700;
	
	public static final Integer SUCCESS_WITH_FALLOUT = 701;
	
	public static final int UPLOAD_MP_FAILED = 702;
	
	public static final Integer OPTY_CALL_FAILED = 703;
	
	public static final String FALLOUT_MSG= "Line Item Processing failed : Fallout.";
	
	public static final String SUCCESS_WITH_FALLOUT_MSG = "Line Item Processing failed : Succeeded with fallouts.";
	
	public static final String UPLOAD_MP_FAILED_MSG = "Interface handling error :  Upload to myPrice failed.";
	
	public static final String OPTY_CALL_FAILED_MSG = "Interface handling error : Failed validation with ROME";
	
	public static final String CUSTOM_JSON_CREATION_ERROR = "M01120";
}
