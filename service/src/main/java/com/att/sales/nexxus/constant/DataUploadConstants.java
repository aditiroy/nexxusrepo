package com.att.sales.nexxus.constant;

/**
 * The Class DataUploadConstants.
 */
/**
 * @author vt393d
 *
 */
public class DataUploadConstants {
	
	/**
	 * Instantiates a new data upload constants.
	 */
	private DataUploadConstants() {
		
	}
	
	/** The Constant ID. */
	public static final String ID="id";
	
	/** The Constant ACTION. */
	public static final String ACTION="action";
	
	/** The Constant ACTIVITY. */
	public static final String ACTIVITY="activity";
	
	/** The Constant FLOW_TYPE. */
	public static final String FLOW_TYPE="flowType";
	
	/** The Constant TOP_PROD_ID. */
	public static final String TOP_PROD_ID="topProdId";
	
	/** The Constant LITTLE_PROD_ID. */
	public static final String LITTLE_PROD_ID="littleProdId";
	
	/** The Constant YES. */
	public static final String YES="Y";
	
	/** The Constant NO. */
	public static final String NO="N";
	
	/** The Constant START_INDEX. */
	public static final int START_INDEX=1;
	
	/** The Constant START_INDEX. */
	public static final int ETH_TOKNE_START_INDEX=1;
	
	/** The Constant START_INDEX. */
	public static final int EDF_BULK_UPLOAD_START_INDEX=1;
	
	/** The Constant USER_ID. */
	public static final String USER_ID="userId";
	
	/** The Constant FILE_DATA. */
	public static final String FILE_DATA="fileData";
	
	/** The Constant STATUS. */
	public static final String STATUS="status";
	
	/** The Constant DISCRIPTION. */
	public static final String DISCRIPTION="discription";
	
	/** The Constant SUCCESS. */
	public static final String SUCCESS="Success";
	
	/** The Constant FAILED. */
	public static final String FAILED="Failed";
	
	/** The Constant UNMATCHED_SECONDARY_KES. */
	public static final String UNMATCHED_SECONDARY_KES="unMatchedSecondaryKeys";
	
	/** The Constant ID_RANGE. */
	public static final String ID_RANGE="idRange";
	
	/** The Constant ACTIVE. */
	public static final String ACTIVE="Active";
	
	public static final String NX_SOLUTION_ID = "nxSolutionId";
	
	public static final String ACTION_PERFORMED_BY = "actionPerformedBy";
	
	public static final String OPTY_ID="optyId";
	
	 /**
 	 * The Enum DataTypeEnum.
 	 */
 	public enum DataTypeEnum {
			
			/** The double. */
			DOUBLE("Double"), /** The long. */
 //
			LONG("Long"),
			
			/** The integer. */
			INTEGER("Integer"), 
 /** The string. */
 //
			STRING("String"), 
 /** The date. */
 //
			DATE("Date");
			
			/** The type value. */
			public final String typeValue;
			
			/**
			 * Instantiates a new data type enum.
			 *
			 * @param typeValue the type value
			 */
			private DataTypeEnum(final String typeValue) {
				this.typeValue = typeValue;
			}
			
			/**
			 * Gets the name.
			 *
			 * @return the name
			 */
			public String getName() {
				return name();
			}
			
			/**
			 * Gets the value.
			 *
			 * @return the value
			 */
			public String getValue() {
				return typeValue;
			}
			
			/* (non-Javadoc)
			 * @see java.lang.Enum#toString()
			 */
			@Override
			public String toString() {
				return name();
			}
		}
	 
	 /**
 	 * The Enum AUDIT_DISCRIPTION.
 	 */
 	public enum AUDIT_DISCRIPTION {
	    	
	    	/** The upload. */
	    	UPLOAD("File Uploaded"),
		 	
	 		/** The activate. */
	 		ACTIVATE("Data Activate"),
		 	
	 		/** The delete stagging data. */
	 		DELETE_STAGGING_DATA("Stagging Data Deleted");

	        /** The value. */
        	private final String value;

	        /**
        	 * Instantiates a new audit discription.
        	 *
        	 * @param newValue the new value
        	 */
        	AUDIT_DISCRIPTION(final String newValue) {
	            value = newValue;
	        }

	        /**
        	 * Gets the value.
        	 *
        	 * @return the value
        	 */
        	public String getValue() { return value; }
	    }
	    
 	public static final String NX_REQ_ID = "nxReqId";
	
	public static final String NX_PRODUCT_ID = "product";
}
