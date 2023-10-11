package com.att.sales.nexxus.constant;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CustomJsonConstants {

	private CustomJsonConstants() {}
	
	 public static final  Map<String,Class<?>> TYPE_MAP;
	 static {
		 Map<String,Class<?>> temp = new HashMap<>();
		 temp.put("Boolean", Boolean.class);
		 temp.put("boolean", Boolean.class);
		 temp.put("String", String.class);
		 temp.put("string", String.class);
		 temp.put("Number", Object.class);
		 temp.put("number", Object.class);
	     TYPE_MAP = Collections.unmodifiableMap(temp);
	}
	
	 public static final String JSON_ROOT_NODE="root";
	 public static final String JSON_TYPE_ARRAY="Array";
	 public static final String BASIC_ARRAY="BasicArray";
	 public static final String JSON_TYPE_OBJECT="Object";
	 public static final String CUSTOM_CODE="Custom";
	 public static final String CUSTOM_JSON_STRING="CustomJsonString";
	 public static final String SINGLE_ITEM_ARRAY="SingleItem";
	 
	 public static final  Map<String,Boolean> BOOELAN_CONVERTER;
	 static {
		 Map<String,Boolean> temp = new HashMap<>();
		 temp.put("true", true);
		 temp.put("TRUE",true);
		 temp.put("false", false);
		 temp.put("FALSE", false);
		 BOOELAN_CONVERTER = Collections.unmodifiableMap(temp);
	}	
	 
	 public static final String RULE_TYPE="ruleType";
	 public static final String IS_CRITERIA_REQUIRED = "isCriteriaRequired";
	 public static final String CONFIG_SOLUTION = "CONFIG_SOLUTION";
	 public static final String CONFIG_DESIGN_UPDATE = "CONFIG_DESIGN";
	 public static final String DESIGN_CRITERIA_RULE = "DESIGN_CRITERIA";
	 public static final String SOLUTION_CRITERIA_RULE = "SOLUTION_CRITERIA";
	 public static final String CONFIG_SYSTEM = "CONFIG_SYSTEM";
	 public static final String CONFIG_ADD_TRANSACTION = "CONFIG_ADD_TXN";
	 public static final String CACHE_INSTANCE_ID = "cacheInstanceId";
	 public static final String NUMBER="Number";
	 public static final String BS_ID = "bsId";
	 public static final String DOCUMENT_ID = "documentId";
	 public static final String CUSTOM_CONFIG_RULES_REST ="CUSTOM_CONFIG_RULES_REST";
	/* public static final  Map<String,String> REST_URL_PRODUCTS;
	 static {
		 Map<String,String> temp = new HashMap<>();
		 temp.put("AVPN", "configwireline.lD.aVPN");
		 temp.put("ADI", "configwireline.lD.ADI");
		 temp.put("Ethernet", "configwireline.lD.LocalAccess");
		 temp.put("International Access", "configwireline.lD.LocalAccess");
		 temp.put("TDM", "configwireline.lD.LocalAccess");
		 temp.put("PrivateLineService", "configwireline.lD.PrivateLineService");
		 temp.put("EPLSWAN", "configwireline.lD.EPLSWAN");
		 temp.put("ASE", "configwireline.telco.aSE");
		 temp.put("asenod_IR", "configwireline.telco.aSE");
		 temp.put("asenod_3PA", "configwireline.telco.aSE");
		 temp.put("ADE", "configwireline.telco.aDE");
		 
		 REST_URL_PRODUCTS = Collections.unmodifiableMap(temp);
	}*/
	 public static final String COUNT_FILTER="countfilter";
	 public static final String ELEMENT_TYPE="elementType";	
	public static final String REST_RESPONSE_ERROR= "restApiResponseErrorMsg";
	public static final String ERROR_WS_NAME = "errorWsName";
	public static final String CONFIG_ERROR_MSG = "configBomErrorMsg";
	//config bom error true false
	public static final String CONFIG_BOM_ERROR = "configBomError";
	//config bom error data in Set<String>
	public static final String CONFIG_BOM_ERROR_DATA="configBonErrorData";
	//site config  error true false
	public static final String SITE_CONFIG_ERROR = "siteConfigError";
	//site config  error data in Map<String,Object>
	public static final String SITE_CONFIG_ERROR_MAP = "siteConfigErrorMap";
	public static final String INT_JURISDICTION="int_Jurisdiction_pf";
	public static final String DEFAULT_CONFIG_ERROR_MSG = "defaultConfogErrMsg";
	public static final String DEFAULT_ERROR_MSG = "Nexxus and MyPrice had communication error";
	public static final String  REST_URL_PRODUCTS="restUrlProducts";
	 public static final String CUSTOM_CONFIG_RULES_REST_STRING ="CUSTOM_CONFIG_RULES_REST_STRING";
	 public static final String REST_V2_CONSOLIDATION_CRITERIA ="REST_V2_CONSOLIDATION_CRITERIA";
}
