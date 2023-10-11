package com.att.sales.nexxus.constant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The Class InrConstants.
 */
public class InrConstants {
	
	/** The Constant EMPTY_XML_JSON_RULE_EXCEPTION. */
	public static final String EMPTY_XML_JSON_RULE_EXCEPTION = "INR001";
	
	/** The Constant XML_FILE_NOT_FOUND_EXCEPTION. */
	public static final String XML_FILE_NOT_FOUND_EXCEPTION = "INR002";
	
	/** The Constant XML_PARSE_EXCEPTION. */
	public static final String XML_PARSE_EXCEPTION = "INR003";
	
	/** The Constant EMPTY_LOOKUP_RULE_EXCEPTION. */
	public static final String EMPTY_LOOKUP_RULE_EXCEPTION = "INR004";
	
	/** The Constant JSON_PROCESSING_EXCEPTION. */
	public static final String JSON_PROCESSING_EXCEPTION = "INR005";
	
	/** The Constant ROOT_JSON_MAP_KEY. */
	public static final String ROOT_JSON_MAP_KEY = "root";
	
	/** The Constant JSON_TYPE_OBJECT. */
	public static final String JSON_TYPE_OBJECT = "object";
	
	/** The Constant JSON_TYPE_ARRAY. */
	public static final String JSON_TYPE_ARRAY = "array";
	
	/** The Constant JSON_TYPE_FIELD_STR. */
	public static final String JSON_TYPE_FIELD_STR = "fieldStr";
	
	/** The Constant JSON_TYPE_FIELD_TAG. */
	public static final String JSON_TYPE_FIELD_TAG = "fieldTag";
	
	/** The Constant JSON_TYPE_FIELD_INT. */
	public static final String JSON_TYPE_FIELD_INT = "fieldInt";
	
	/** The Constant JSON_TYPE_FIELD_LONG. */
	public static final String JSON_TYPE_FIELD_LONG = "fieldLong";
	
	public static final String JSON_TYPE_FIELD_DATE = "fieldDate";
	
	/** The Constant JSON_TYPE_FIELD_DOUBLE. */
	public static final String JSON_TYPE_FIELD_DOUBLE = "fieldDouble";
	
	/** The Constant JSON_FIELD_SERVICE. */
	public static final String JSON_FIELD_SERVICE = "service";
	
	/** The Constant INR. */
	public static final String INR = "INR";
	
	public static final String INR_BETA = "INR_BETA";
	
	/** The Constant IGL. */
	public static final String IGL = "IGL";
	
	public static final String USRP = "USRP";
	
	/** The Constant SITE_ID. */
	public static final String SITE_ID = "siteId";
	
	/** The Constant NOT_AVAILABLE. */
	public static final String NOT_AVAILABLE = "N/A";
	
	/** The Constant SITE_NAME. */
	public static final String SITE_NAME = "siteName";
	
	/** The Constant STATE. */
	public static final String STATE = "state";
	
	/** The Constant LOCAL_LIST_PRICE. */
	public static final String LOCAL_LIST_PRICE = "localListPrice";
	
	/** The Constant ACTUAL_PRICE. */
	public static final String ACTUAL_PRICE = "actualPrice";
	
	/** The Constant COUNTRY. */
	public static final String COUNTRY = "country";
	
	/** The Constant CURRENCY. */
	public static final String CURRENCY = "currency";
	
	/** The Constant QUANTITY. */
	public static final String QUANTITY = "quantity";
	
	/** The Constant BEID. */
	public static final String BEID = "beid";
	
	/** The Constant CITY. */
	public static final String CITY = "city";
	
	/** The Constant SITE_ALIASE. */
	public static final String SITE_ALIASE="siteAliase";
	
	/** The Constant PORT_SPEED. */
	public static final String PORT_SPEED = "portSpeed";
	
	/** The Constant ETHERNET_TAB. */
	public static final String ETHERNET_TAB = "Ethernet_Access";
	
	/** The Constant FLOW_TYPE. */
	public static final String FLOW_TYPE = "flowType";
	public static final String SOURCE = "source";
	public static final String SEPARATOR = "\\s*,\\s*";
	public static final String BACKUP_NODE_NAME = "_backup";
	public static final String FLATTEN_ROOT_TAG = "rootTag";
	public static final String FLATTEN_DATA_TAG = "data";
	
	public static final String REQUEST_META_DATA_KEY = "requestMetaDataMap";
	public static final String FALLOUT_REASON = "FALLOUTREASON";
	public static final String EDF_INR_DMAAP = "EDF_INR_DMAAP";
	public static final String EDF_BULK_INR_DMAAP = "EDF_BULK_INR_DMAAP";
	public static final String USRP_INR_DMAAP = "USRP_INR_DMAAP";
	public static final String PYTHON_POD = "PYTHON_POD";
	public static final String DW_DMAAP = "DW_DMAAP";
	public static final String USRP_POD = "USRP_POD";
	public static final String INR_BETA_JSON_CREATION = "INR_BETA_JSON_CREATION";
	
	public static final  List<String> DMAAP_STATUS;
	 static {
		 List<String> temp=new ArrayList<>();
		  temp.add("N"); 
		  temp.add("IP"); 
		  DMAAP_STATUS = Collections.unmodifiableList(temp);
	};
	
	
	/**
	 * Instantiates a new inr constants.
	 */
	private InrConstants() {
	
	}
	
	public static final String DESIGNELEMENT_JSON_MAP_KEY = "designElement";
	public static final String CIRCUITSDETAILS_JSON_MAP_KEY = "circuitsDetailsElement";
	public static final String NEXXUS_FALLOUT = "nexxusFallout";
	public static final String NEXXUS_FALLOUT_IGNORE = "nexxusFalloutIgnore";
	public static final String NEXXUS_FALLOUT_REASON = "nexxusFalloutReason";
	
	public static final  Set<String> QUALIFIED_CIRCUIT_TYPE_OF_CHARGE;
	 static {
		 Set<String> temp=new HashSet<>();
		  temp.add("P"); 
		  temp.add("A");
		  QUALIFIED_CIRCUIT_TYPE_OF_CHARGE = Collections.unmodifiableSet(temp);
	}

	public static final String NRC_CHARGES_ARE_NOT_NEEDED = "NRC Charges. Not needed for existing circuits.";

}
