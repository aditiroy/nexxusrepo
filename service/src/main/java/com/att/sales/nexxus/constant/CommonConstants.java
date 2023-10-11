package com.att.sales.nexxus.constant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The Class CommonConstants.
 */
public class CommonConstants {
	
	/** The Constant REQUEST_COMPLETED_SUCCESSFULLY. */
	public static final String REQUEST_COMPLETED_SUCCESSFULLY = "M00000";
	
	/** The Constant OFFER. */
	public static final String OFFER="Offer";
	
	/**
	 * Following constants is used for different STATUS related constants.
	 * Currently they are being used for EDF calls.
	 * 
	 * @author sw088d
	 *
	 */
    public enum STATUS_CONSTANTS {
    	
	    /** The in progress. */
	    IN_PROGRESS(10L),
    	
	    /** The success with fallout. */
	    SUCCESS_WITH_FALLOUT(20L),
        
        /** The success. */
        SUCCESS(30L),
        
        /** The error. */
        ERROR(40L),
        
        /** The file not found. */
        FILE_NOT_FOUND(50L),
        
        /** The no dmaap notification received. */
        NO_DMAAP_NOTIFICATION_RECEIVED(60L),
        
        /** The fallout. */
        FALLOUT(70L),

    	/** The fallout ignored. */
        FALLOUT_IGNORED(80L),
    	
    	/** The fallout ignored. */
        SUBMIT_MYPRICE_FALLOUT_IGNORED(100L),
        
        /** The success. */
        SUBMIT_MYPRICE_SUCCESS(90L),
    	
    	/** The system failure. */
        SYSTEM_FAILURE(170L),
        
        FALLOUT_DMAAP_RECEIVED(180L),
    	
    	COPY_IN_PROGRESS(190L),
    	
    	COPY_IN_PROGRESS_FALLOUT(200L),
    	
    	PARTIAL_FALLOUT_LINE_ITEMS_IGNORED(270L),
    	
    	REGENERATE_LINE_ITEMS(320L);
    	
        
        /** The value. */
        private final long value;

        /**
         * Instantiates a new status constants.
         *
         * @param newValue the new value
         */
        STATUS_CONSTANTS(final long newValue) {
            value = newValue;
        }

        /**
         * Gets the value.
         *
         * @return the value
         */
        public long getValue() { return value; }
    }
    
    /** The Constant LANDING_PAD_MAX_SOLUTIONS_FETCH_COUNT. */
    public static final int LANDING_PAD_MAX_SOLUTIONS_FETCH_COUNT = 200;
    
    /** The Constant OUTPUT_FILE_NAME. */
    public static final String OUTPUT_FILE_NAME="_NexusDataSet_Sheet_";
    
    /** The Constant SUCCESS. */
    public static final String SUCCESS="Succeed";
	
	/** The Constant FAIL. */
	public static final String FAIL="failed";
	
	/** The Constant INPROGRESS. */
	public static final String INPROGRESS="InProgress";
	
	/** The Constant FALLOUT. */
	public static final String FALLOUT="fallOut";
	
	/** The Constant SUCCESS_WITH_FALLOUT. */
	public static final String SUCCESS_WITH_FALLOUT="Succeed with fallOut";
	
	/** The Constant PROC_ADD_NEW_TOP_PRODUCT. */
	public static final String PROC_ADD_NEW_TOP_PRODUCT = "NX_ADD_NEW_TOP_PRODUCT";
	
	/** The Constant PROC_ACTIVE_LINE_ITEM_DATA. */
	public static final String PROC_ACTIVE_LINE_ITEM_DATA = "";
	
	public static final String COMPONENT="component";
	public static final String COMPONENT_CODE_ID="componentCodeId";
	public static final String DESIGN_DETAILS="designDetails";
	public static final String UDF_ID="udfId";
	public static final String TEXT="Text";
	public static final String ID="Id";
	
	public enum AUDIT_TRANSACTION_CONSTANTS {
		
	    TDD_TRIGGER("TDD_Request"),
	    
	    TDD_ORCH_UPDATE("TDD_DppOrch"),
	    
	    TDD_DMAAP("TDD_Dmaap"),
	    
	    TDD_MAIL("TDD_Mail"),
		
		PED_SNSD_PROGRESS_DMAAP("PED_SNSD_Progress_Dmaap"),
		
		PED_SNSD_STATUS_DMAAP("PED_SNSD_Status_Dmaap"),
		
		RATE_LETTER_DMAAP("RateLetter_Dmaap"),
		
		REJECTED("MP_Rejected_Dmaap"),
		
		FAILED("MP_Failed_Dmaap"),
		
		SUBMITTED("MP_Submitted_Dmaap"),
		
		CREATED("MP_Created_Dmaap");
		
        /** The value. */
        private final String value;

        /**
         * Instantiates a new status constants.
         *
         * @param newValue the new value
         */
        AUDIT_TRANSACTION_CONSTANTS(final String newValue) {
            value = newValue;
        }

        /**
         * Gets the value.
         *
         * @return the value
         */
        public String getValue() { return value; }
	}
	
	public static final String STATUS_DATASET_NAME="STATUS";
	public static final String NX_TRANSATION_DATASET_NAME="NX_TRANSACTION";
	public static final String SUCCESS_STATUS="SS";
	public static final String FAIL_STATUS="F";
	
	public static final String USER_NAME="userName";
	//public static final String PASS="password";
	
	public static final String HTTP_PROXY_HOST="http.proxyHost";
	public static final String HTTP_PROXY_PORT="http.proxyPort";
	public static final String HTTPS_PROXY_HOST="https.proxyHost";
	public static final String HTTPS_PROXY_PORT="https.proxyPort";
	public static final String HTTP_PROXY_USER="http.proxyUser";
	//public static final String HTTP_PROXY_PSWD="http.proxyPassword";
	public static final String HTTP_PROXY_SET="http.proxySet";
	
	public static final String TRUE="true";
	public static final String FALSE="false";
	public static final String SOLUTION_DATA="SOLUTION_DATA";
	
	public static final String TSI = "Transfer Sites Initiated";
	public static final String AUDIT_TRANSACTION = "auditTransaction";
	public static final String AUDIT_ID = "auditId";
	
	public static final String SOLUTION_REQUEST_STATUS = "SOLUTION_REQUEST_STATUS";
	
	public static final String NEXXUS_COPY_STATUS = "NEXXUS_COPY_STATUS";
	
	// INR data update constants
	public static final String INR_EDITS_DATASET = "INR_DATA_EDIT";
	public static final String INR_BETA_EDITS_DATASET = "INR_BETA_DATA_EDIT";
	public static final String INR_BETA_EDIT_EXCEL_DOWNLOAD_MAPPING = "INR_BETA_EDIT_EXCEL_DOWNLOAD_MAPPING";
	public static final String INR_EDIT_EXCEL_DOWNLOAD_MAPPING = "INR_EDIT_EXCEL_DOWNLOAD_MAPPING";

	
	public static final String WHERE_CLAUSE_PATH = "whereClausePath";
	public static final String WHERE_CLAUSE_ATTRINAME = "whereClauseAttriName";
	public static final String WHERE_CLAUSE_DATATYPE = "whereClauseDatatype";
	public static final String WHERE_CLAUSE_EXCEL_COLNAME = "whereClauseExcelColName";
	public static final String DATA_UPDATE = "Data Update";
	public static final String JSON_ATTRINAME = "jsonAttriName";
	public static final String EXCLUDE_FROM_MP = "Exclude line items";
	public static final String CIRCUITE_ID_AUGMENTATION = "Circuit Augmentation";
	public static final String CDIR_UPDATE_KEY = "cdirKey";
	public static final String CKTID_CHECK = "cktIdCheck";
	public static final String CKTID_EXCEL_NAME = "cktIdExcelName";
	public static final String CKTID_ATTRINAME = "cktIdAttriName";
	public static final String REMOVENXSITEID = "removeNxSiteId";
	public static final String STRING_LONG = "Long";
	public static final String STRING_INTEGER = "Integer";
	public static final String DATA_KEY = "data";
	public static final String PATH_KEY = "path";
	public static final List<Long> INR_REQUEST_STATUS;
	 static {
		 List<Long> temp=new ArrayList<Long>();
		  temp.add(30L); 
		  temp.add(80L);
		  temp.add(20L);
		  temp.add(190L);
		  temp.add(200L);
		  temp.add(210L);
		  temp.add(240L);
		  temp.add(250L);
		  temp.add(260L);
		  temp.add(290L); 
		  temp.add(310L);
		  temp.add(320L);
		  INR_REQUEST_STATUS = Collections.unmodifiableList(temp);
	}
	public static final String INR_EXCLUDE_LINE_ITEMS = "INR_EXCLUDE_LINE_ITEMS";
	public static final List<String> INR_DATA_UDAPTE_ACTIONS;
	 static {
		 List<String> temp=new ArrayList<String>();
		  temp.add("Data Update"); 
		  temp.add("Exclude line items");
		  temp.add("Circuit Augmentation"); 
		  INR_DATA_UDAPTE_ACTIONS = Collections.unmodifiableList(temp);
	 }
	public static final String ACTION_COL_NAME = "Action";
	public static final String INR_ERROR_INVALID_ACTION = "IU0001";
	public static final String INR_ERROR_DATA_NOT_FOUND = "IU0002";
	public static final String REGENERATE_NXSITEID = "R";
	public static final String DDA_PRODUCT_NAME = "DOMESTIC DEDICATED ACCESS";
	public static final String DDA_ACCESS_ARCHITECTURE = "Access Architecture";
	public static final String EXCEL_NX_SITE_ID_COLNAME = "NX_Site ID";
	public static final String JSON_NXSITEID_NAME = "nxSiteId";
	public static final String OPERATION = "operation";
	public static final String REPLACE = "REPLACE";
	public static final String UPPERCASE = "UPPERCASE";
	public static final List<String> INR_ADDRESS_UDAPTE_ACTIONS;
	 static {
		 List<String> temp=new ArrayList<String>();
		  temp.add("Update Address"); 
		  temp.add("No Change");
		  INR_ADDRESS_UDAPTE_ACTIONS = Collections.unmodifiableList(temp);
	 }
	public static final String BULK_INR_DATA_UPDATE = "dataUpdate";
	public static final String BULK_INR_ADDRESS_UPDATE = "addressUpdate";
	public static final String NO_CHANGE = "No Change";
	public static final String INR_EDIT_REQUEST_STATUS = "INR_EDIT_REQUEST_STATUS";
	public static final String PRE_DATA = "preData";
	public static final String POST_DATA = "postData";

	public static final String PARTIAL_FALLOUT_LINE_ITEMS_IGNORED = "PARTIAL_FALLOUT_LINE_ITEMS_IGNORED";
}
