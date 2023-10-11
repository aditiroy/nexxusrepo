package com.att.sales.nexxus.constant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyPriceConstants {
	
	private MyPriceConstants() {}
	public static final String STANDART_PRICING_IND = "standardPricingInd";
	public static final String NX_REQ_ID="nxReqId";
	public static final String NX_TRANSACTION_ID="nxTxnId";
	public static final String NX_DESIGN_ID="nxDesignId";
	public static final String MP_TRANSACTION_ID="myPriceTransId";
	public static final String ASE_OFFER_NAME="ASE";
	public static final String BVOIP_OFFER_NAME="BVoIP";
	public static final long ASE_OFFER_ID=103l;
	public static final String ADE_OFFER_NAME="ADE";
	public static final String EPLSWAN_OFFER_NAME="EPLSWAN";
	public static final String ETHERNET_OFFER_NAME="Ethernet";
	public static final String EPLSWAN_PRODUCT_NAME="EPLS%20WAN";
	public static final String LOCALACCESS_PRODUCT_NAME="Local%20Access";
	public static final String AVPN_OFFER_NAME="AVPN";
	public static final String DDA_OFFER_NAME="DOMESTIC DEDICATED ACCESS";
	public static final String ADI_OFFER_NAME="ADI";
	public static final long ADE_OFFER_ID=120l;
	public static final String ASEoD_OFFER_NAME = "ASEoD";
	public static final String OFFER_ID="offerId";
	public static final String OFFER_NAME="offerName";
	public static final String OFFER_TYPE="offerType";
	public static final String DESIGN_RULE="DESIGN";
	public static final String SOLUTION_RULE="SOLUTION";
	public static final String PRICE_UPDATE="priceUpdate";
	public static final String CONTRACT_TERM="contractTerm";
	public static final String EPLS_WAN_OFFER_NAME="EPLS-WAN";
	public static final String ADSL="ADSL";
	public static final String AVPN="AVPN";
	public static final String ADI="ADI";
	public static final String MISPNT="MIS/PNT";
	public static final String GMIS="GMIS";
	public static final String ADIG="ADIG";
	public static final String ANIRA="ANIRA";
	public static final String AVTS="AVTS";
	public static final String ONENET="OneNet";
	public static final String ONENET_FEATURE="OneNet Feature";
	public static final String DDA_CKTS_MRC="DDA_CKTS_MRC";
	public static final String Migration="Migration";
	public static final String Growth="Growth";
	public static final String Existing="Existing";
	public static final String DOMESTIC_DEDICATED_ACCESS="DOMESTIC DEDICATED ACCESS";
	public static final String MPS_INDICATOR = "mpsIndicator";
	public static final String ITEM_EMPTY = "itemEmpty";
	
	public static final String BVoIP="BVoIP";
	public static final String OPTEWAN ="OPT-E-WAN";
	public static final String ATTCollaborate="ATTCollaborate";
	public static final String LocalAccess="LocalAccess";
	public static final String ABN="ABN";
	
	public static final String ADDRESS_DELIM=",";
	public static final String MP_DELIM="$,$";
	 
	public static final  Map<String,Long> OFFER_NAME_OFFER_ID_MAP;
	 static {
		 Map<String, Long> temp = new HashMap<>();
	     temp.put(ASE_OFFER_NAME, ASE_OFFER_ID); 
	     temp.put(ADE_OFFER_NAME, ADE_OFFER_ID); 
	     OFFER_NAME_OFFER_ID_MAP = Collections.unmodifiableMap(temp);
	}
	 public static final String NX_DESIGN = "nxDesign";
	 public static final String NX_ACCESS_PRICING_DATA = "nxAccessPricingData";
	 
	 public static final String AUDIT_CREATE = "MYPRICE_CREATE";
	 public static final String AUDIT_UPDATE_CS = "MYPRICE_CLEANSAVE";
	 public static final String AUDIT_CONFIG_SOLN = "MYPRICE_CONFIG_SOLN";
	 public static final String AUDIT_CONFIG_DESIGN = "MYPRICE_CONFIG_DESIGN";
	 public static final String AUDIT_UPDATE_PR = "MYPRICE_PRICING";
	 public static final String AUDIT_COPY_RECONFIGURE = "MYPRICE_COPY_RC";
	 public static final String AUDIT_COPY_PRICE_SCENARIO = "MYPRICE_COPY_MPS";
	 public static final String AUDIT_UPDATE_OR_RECONFIGURE = "MYPRICE_OVERRIDE_RC";
	 public static final String AUDIT_UPDATE_OR_PRICE_SCENARIO = "MYPRICE_OVERRIDE_MPS";
	 public static final String AUDIT_CONFIG_SOLN_RC = "MYPRICE_CONFIG_SOLN_RC";
	 public static final String AUDIT_CONFIG_DESIGN_RC = "MYPRICE_CONFIG_DESIGN_RC";
	 public static final String AUDIT_UPDATE_PR_RC = "MYPRICE_PRICING_RC";
	 public static final String AUDIT_UPDATE_PR_PRICE_SCENARIO = "MYPRICE_PRICING_MPS";
	 public static final String AUDIT_FALLOUT_STATUS ="Fallout";
	 public static final String AUDIT_IPE_TRACE = "IPE_TRACE";

	 /** The Constant  SOURCE_SYSTEM */
	public static final String SOURCE_SYSTEM = "myPrice";
	public static final String MULTI_JSON_PATH_SEPERATOR="##";
	public static final String OR_CONDITION_SEPERATOR="||";
	public static final String COMMA_SEPERATOR=",";
	public static final String YES="Yes";
	public static final String NO="No";
	public static final String SALES_LOOKUP_SOURCE="SALES_MS_PRODCOMP_UDF_ATTR_VAL";
	public static final String NX_LOOKUP_SOURCE="NX_LOOKUP_DATA";
	public static final String SITE_JSON ="siteJson";
	public static final String DESIGN_STATUS ="designStatus";
	public static final String DESIGN_STATUS_NEW ="N";
	public static final String DESIGN_STATUS_CANCEL ="C";
	public static final String DESIGN_STATUS_UPDATE ="U";
	public static final String DESIGN_STATUS_REOPEN ="R";
	public static final String DOCUMENT_ID ="documentId";
	public static final String JURISDICTION="jurisdiction_pf";
	public static final String AVPN_JURISDICTION="AVPNJurisdiction_pf";
	public static final String DIVERSITY_SERVICE="diversity_service_pf";
	public static final long IF_OOF_IND_UDF_ID_ADE=200205l;
	public static final long IF_OOF_IND_UDF_ID_ASE=200058l;
	public static final long END_POINT_COMPONENT_CD_ID=1220l;
	public static final long PORT_COMPONENT_CD_ID=30l;
	public static final long REGION_ID_UDF_ID=200134l;
	public static final String RATE_GROUP ="rategroup";
	public static final String BE_ID ="beid";
	public static final String IS_DEFAULT="DEFAULT";
	public static final String TRANSACTION_FLOW_TYPE = "transactionFlowType";
	public static final String TRANSACTION_FLOW_TYPE_DESIGN = "designUpdate";
	
	public static final String ADE_CLLI_VALUE = "Z-CLLI";
	public static final String ASE_NX_SITE_ID_PATH = "$..nxSiteId";
	public static final String ENDPOINT_A_NX_SITE_ID_PATH = "$..[?(@._endPointRef== '21033' )].nxSiteId";
	public static final String ENDPOINT_Z_NX_SITE_ID_PATH = "$..[?(@._endPointRef== '21034' )].nxSiteId";
	public static final String ENDPOINT_A_COMPONENT_ID_PATH = "$..[?(@.componentCodeId ==1220 && 21033 in @.designDetails[*].udfId)].componentId";
	public static final String ENDPOINT_Z_COMPONENT_ID_PATH = "$..[?(@.componentCodeId ==1220 && 21034 in @.designDetails[*].udfId)].componentId";
	public static final String CIRCUIT_COMPONENT_ID_PATH = "$..[?(@.componentCodeId ==1210)].componentId";
	public static final String ENDPOINT_A_NX_SITE_ID="endpointASiteId";
	public static final String ENDPOINT_Z_NX_SITE_ID="endpointZSiteId";
	public static final String ENDPOINT_A_COMPONENT_ID="endpointAComponentId";
	public static final String ENDPOINT_Z_COMPONENT_ID="endpointZComponentId";
	public static final String CIRCUIT_COMPONENT_ID="circuitComponentId";
	public static final String ENDPOINT_A_BEID="endpointABeId";
	public static final String ENDPOINT_Z_BEID="endpointZBeId";
	public static final String CIRCUIT_BEID="circuitBeId";
	public static final String NX_SITE_ID= "nxSiteId";
	public static final String MP_PRODUCT_LINE_ID="mpProductLineId";
	public static final String CURRENT_NX_SITE_ID = "currentNxSiteId";
	public static final String ASE_USOC_PATH="$..priceAttributes.[?((@.priceScenarioId == %priceScenarioId% ) && %rateGroups%)]";
	public static final String ADE_USOC_PATH="$..priceAttributes.[?((@.priceScenarioId == %priceScenarioId% && @.frequency=='MRC') && (%priceUnits%))]";
	public static final String ASE_USOC_DATASET="mp_ase_usocValue";
	public static final String ADE_USOC_DATASET = "mp_ade_usocValue";
	public static final String ASENOD_USOC_DATASET = "mp_asenod_usocValue";
	public static final String ASENOD__3PA_USOC_DATASET = "mp_asenod3PA_usocValue";
	public static final String ASE_USOC_PF = "uSOC_pf";
	public static final String ADE_USOC_PF = "uSOC_ade_pf";
	public static final String ASENOD_USOC_PF = "uSOC_ase3pa_pf";
	public static final String USOC_VALUE = "usocValue";
	public static final String ASE_TELCO_BILLING_ARRAY = "telcoBillingElementArrayController_pf";
	public static final String ADE_TELCO_BILLING_ARRAY = "telcoADEBillingArrayController_pf";
	public static final String ASENOD_TELCO_BILLING_ARRAY ="arrayControllerASE3PA_ase3pa_pf";
	public static final String ASE_NEW_PF = "newQty_telcoBillingElementArray_pf";
	public static final String ASE_EXISTING_PF = "existingQty_telcoBillingElementArray_pf";
	public static final String ASE_MIGRATION_PF = "migrationQty_telcoBillingElementArray_pf";
	public static final String ADE_NEW_PF = "new_ADE_pf";
	public static final String ADE_EXISTING_PF = "existing_ADE_pf";
	public static final String ADE_MIGRATION_PF = "migration_ADE_pf";
	public static final String ASE_MCAD_ACTIVITY_PATH="$..component.[?(@.componentCodeId==30)].designDetails.[?(@.udfId==200010)].udfAttributeId.[*]";
	public static final String ADE_MCAD_ACTIVITY_PATH="$..component.[?(@.componentCodeId==1210)].designDetails.[?(@.udfId==200164)].udfAttributeId.[*]";
	public static final String ASE_RULES_DATASET="mp_ase_newExistingMigrationRules";
	public static final String ASE_RULES_DATASET_MCAD="mp_ase_newExistingMigrationRules_mcad";
	public static final String ADE_RULES_DATASET = "mp_ade_newExistingMigrationRules";
	public static final String NEW = "New";
	public static final String EXISTING = "Existing";
	public static final String MIGRATION = "Migration";
	public static final String TELCO_BILLING_ARRAY = "telcoBillingArray";
	public static final String ASE_USOC_PRICE_GROUP = "mp_ase_usocPriceGroup";
	public static final List<String> RL_TYPE_WITH_CAVEAT_ARRAY = new ArrayList<String>(Arrays.asList("preNSS_CertifiedwithCaveats"));
	public static final List<String> RL_TYPE_WITHOUT_CAVEAT_ARRAY = new ArrayList<String>(Arrays.asList("preNSS_Certified", "preNSS_NotCertified", "postNSS_"));
	public static final String RL_TYPE_WITH_CAVEAT = "Rate Letter With Caveat";
	public static final String RL_TYPE_WITHOUT_CAVEAT = "Rate Letter Without Caveat";
	public static final String AUTOMATION_IND="automationInd";
	public static final String PRODUCT_SUB_TYPE="productSubType_pf";
	public static final String AUTOMATION_PRODUCT_SUB_TYPE="MP_AutomationProductSubType";
	public static final String NSS_ENGAGEMENT_DATASET="MP_ASE_NSSReviewRequired";
	public static final String NSS_ENGAGEMENT_JSON_PATH_ASE="$..nssEngagement||$..component.[?(@.componentCodeId==30)].designDetails.[?(@.udfId==200011)].udfAttributeId.[*]";
	public static final String NSS_ENGAGEMENT_JSON_PATH_ADE="$..component.[?(@.componentCodeId==1210)].designDetails.[?(@.udfId==200011)].udfAttributeId.[*]";
	public static final String DEAL_STATUS_APPROVED = "Approved";
	
	public static final String NX_AUDIT_ID="nxAuditId";
	public static final String MYPRICE_DESIGN = "design";
	public static final String CONFIG_WS = "configWs";
	public static final String OPTY_INFO_WS = "optyInfoWs";
	public static final String GET_BILLING_CHARGES_WS = "billingChargesWs";
	
	public static final String RESPONSE_CODE = "code";
	public static final String RESPONSE_MSG = "message";
	public static final String RESPONSE_STATUS = "status";
	public static final String RESPONSE_DATA = "data";
	
	public static final String ASR_ITEM_ID = "asrItemId";
	
	public static final String TRANSACTION_TYPE = "transType";

	public static final String THIRD_PARTY_IND_PATH = "$..thirdPartyInd";
	public static final String PRODUCT_TYPE = "productType";
	public static final String PRODUCT_TYPE_REST_ERROR = "productTypeRestError";
	public static final String ASENOD_3PA = "asenod_3PA";
	public static final String ASENOD_IR = "asenod_IR";
	public static final String ASE_CLASSIC_OFFER_NAME = "ASE Classic";
	public static final String ASENOD_OFFER_NAME = "ASENoD";
	public static final String ASENOD_3PA_ALL_USOC_PATH = "$..beid";
	public static final String ASENOD_3PA_USOC_RANGE_DATASET = "MP_ASENOD_3PA_RANGE";
	public static final String SOLUTION_PRODUCT_DATA = "solutionProdData";
	public static final String MP_SOLUTION_ID = "mpSolutionId";

	public static final String PRODUCT_LINE = "productLine";
	public static final String SEGMENT = "segment";
	public static final String MODEL = "model";
	public static final String MRC = "MRC";
	public static final String USOC_ID = "usocId";
	public static final String NET_RATE = "netRate";

	public static final String CONFIG_SOL_PRODUCT_WS = "configSolutionAndProduct_WS";
	public static final String CONFIG_DESIGN_WS = "configDesign_WS";
	public static final String 	MILLISEC="milliseconds";
	
	public static final String SOURCE_INTERNATIONAL_ACCESS = "International Access";
	public static final String PRODUCT_TYPE_ETH = "ETHERNET";
	public static final String SOURCE_IGLOO = "IGLOO";
	public static final String SOURCE_INR = "INR";
	public static final String SOURCE_FMO = "FMO";
	public static final String SOURCE_ADOPT = "adopt";
	public static final String SOURCE_PD = "PD";
	public static final String INTERMEDIATE_JSON = "INTERMEDIATE_JSON";
	public static final String PRODUCT_NAME="productName";
	public static final String SOURCE="source";
	public static final String SOLUTION_VERSION="solutionVersion";
	public static final String EPLSWAN="EPLSWAN";
	public static final String ETHERNET="Ethernet";
	public static final String EPLSWAN_Ethernet = "EPLSWAN/Ethernet";
	public static final String TDM="TDM";
	public static final String PL_IOC="PrivateLineService";
	public static final String OUTPUT_JSON = "outputJson";
	public static final String CIRCUIT_ID = "circuitId";
	public static final String SUB_PRODUCT = "subProduct";
	public static final String AVPN_LOCALACCESS = "LocalAccess";
	public static final String IGLOO_PRODUCT_NAME = "LocalAccess";
	public static final String NX_DESIGN_ID_INR="nxDesignIdInr";
	public static final String IS_GROUP_REQUEST="isGroupRequest";
	public static final String GROUP_PRODUCT="GROUP_PRODUCT";
	public static final String INR_MP_PRODUCT_DATASET = "INR_CONFIG_PRODUCT";
	public static final String FMO_MP_PRODUCT_DATASET = "FMO_CONFIG_PRODUCT";
	public static final String INR_CONFIG_SOL_PRODUCT_DATASET ="INR_CONFIG_SOL_PRODUCT";
	public static final String FMO_ACCESS_PRODUCT_NAME_DATASET ="FMO_ACCESS_PRODUCT_NAME";
	public static final String FMO_ACCESS_PRODUCT_NAME_DATA_MAP ="fmoAccesspProductNameDataMap";
	public static final String INR_CONFIG_DESIGN_PRODUCT_DATASET ="INR_CONFIG_DESIGN_PRODUCT";
	public static final String INR_PRODUCTS ="INR_PRODUCTS";
	public static final String INR_MP_PRODUCT_INFO_DATA_MAP = "inrConfigProductInfoData";
	public static final String FMO_MP_PRODUCT_INFO_DATA_MAP = "fmoConfigProductInfoData";
	public static final String AVPN_UNIQUE_ID = "AVPNUniqueID_pf";
	public static final String PORT_QUANTITY = "port_qty_pf";
	public static final String EXISTING_PORT_MRC = "existingMRC_AVPN_pf";
	public static final String AVPN_UNIQUE_ID_JSON_PATH ="$.design..priceDetails[?(@.element=='port')]";
	public static final String AVPN_UNIQUE_PORTFEATURE_ID = "UniqueId_PortFeatures_pf";
	public static final String AVPN_PORT_FEATURE_EXISTING_MRC= "existingMRC_AVPNPortFeatures_pf";
	public static final String AVPN_PORT_UNIQUE_ID_JSON_PATH="$.design..priceDetails[?(@.element=='portFeature')]";
	public static final String AVPN_STANDARD_ROUTERS_UNIQUE_ID= "uniqueId_standardrouters_AVPN_pf";
	public static final String AVPN_STANDARD_ROUTERS_EXISTING_MRC= "existingMRC_AVPNStdRouters_pf";
	public static final String AVPN_STANDARD_ROUTERS_QUANTITY= "AVPNExistingQty_standardRouters_pf";	
	public static final String AVPN_STANDARDROUTERS_UNIQUE_ID_JSON_PATH="$.design..priceDetails[?(@.element=='portFeature')]";
	public static final String AVPN_FEATURES_UNIQUE_ID= "AVPNUniqueID_Features_pf";
	public static final String AVPN_FEATURES_EXISTING_MRC= "existingMRC_AVPNFeatures_pf";
	public static final String AVPN_FEATURES_QUANTITY= "AVPNExistingQty_Features_pf";	
	public static final String AVPN_FEATURES_UNIQUE_ID_JSON_PATH="$.design..priceDetails[?(@.element=='Features')]";
	public static final String AVPN_EXCEPTIONAL_LITTLE_PRODUCT_ID = "5678";
	
	public static final String EPLSWAN_POPCLLI_UDFID = "1000374";
	public static final String EPLSWAN_TOKEN_ID_ETHERNET_UDFID = "1000375";
	public static final String EPLSWAN_VENDOR_ZONE_UDFID = "1000373";
	public static final String ENDPOINT_A="endpointA";
	public static final String ENDPOINT_Z="endpointZ";
	public static final String CIRCUIT="circuit";
	public static final String LOCA="LocA";
	public static final String LOCZ="LocZ";
	public static final List<String> NX_REQ_GROUP_NAMES = new ArrayList<String>(){{
		add("ACCESS_GROUP");
		add("SERVICE_GROUP");
		add("SERVICE_ACCESS_GROUP");
	}};
	
	public static final List<String> ACCESS_SERVICE_GROUP_NAMES = new ArrayList<String>(){{
		add("ACCESS_GROUP");
		add("SERVICE_GROUP");
	}};
	
	public static final List<Long> INR_RC_REQUEST_STATUS = new ArrayList<Long>(){{
		add(30L);
		add(80L);
		add(90L);
		add(100L);
	}};
	
	public static final List<Long> REQUEST_STATUS = new ArrayList<Long>(){{
		add(30L);
		add(80L);
	}};
	
	public static final List<Long> REQUEST_SUBMITTED_TO_MP_STATUS = new ArrayList<Long>(){{
		add(90L);
		add(100L);
	}};
	
	public static final List<String> FLOW_TYPES = new ArrayList<String>(){{
		add("INR");
		add("FMO");
		add("USRP");
	}};
	
	public static final String NOT_APPLICABLE = "10";
	public static final String QUALIFIED = "11";
	public static final String NOT_QUALIFIED = "12";
	public static final String MANUALLY_QUALIFIED = "13";
	public static final String IN_PROGRESS = "14";
	public static final String SYSTEM_FAILURE = "16";
	public static final String NO_DMAAP_NOTIFICATION = "17";
	
	public static final String ACCESS_GROUP = "ACCESS_GROUP";
	public static final String SERVICE_GROUP = "SERVICE_GROUP";
	public static final String SERVICE_ACCESS_GROUP = "SERVICE_ACCESS_GROUP";
	
	 public static final  List<String> MP_API_CONST;
	 static {
		 List<String> temp=new ArrayList<>();
		  temp.add("N"); 
		  temp.add("U"); 
		  MP_API_CONST = Collections.unmodifiableList(temp);
	}
	 
	 public static final  List<String> REST_MP_API_CONST;
	 static {
		 List<String> temp=new ArrayList<>();
		  temp.add("RN"); 
		  temp.add("RU"); 
		  REST_MP_API_CONST = Collections.unmodifiableList(temp);
	}
	
	public static final String API_NOT_INVOKED = "N";
	public static final String API_SUCCEED = "S";
	public static final String API_FAILED = "F";
	public static final String SOLUTION_SITE_ADDRESS_PM = "solutionSiteAddress_pm";
	public static final String LOC_A_ADDRESS_PF = "locAAddressDetails_pf";
	public static final String LOC_Z_ADDRESS_PF = "locZAddressDetails_pf";
	public static final String SPEED_LOCAL_ACCESS_PF = "Speed_LocalAccess_pf";
	public static final String INT_SPEED_LOCAL_ACCESS_PF = "int_Speed_LocalAccess_pf";
	public static final String START = "S";
	public static final String END = "E";
	public static final String avpnUniqueID_pf ="AVPNUniqueID_pf";
	public static final  Map<String,String> DDA_PRODUCT_MAP;
	public static final  Map<String,String> BVOIP_PRODUCT_MAP;
	static {
		 Map<String, String> temp = new HashMap<>();
	     temp.put("Ethernet", "LocalAccess"); 
	     temp.put("TDM", "LocalAccess"); 
	     DDA_PRODUCT_MAP = Collections.unmodifiableMap(temp);
	     
	     Map<String, String> tempdata = new HashMap<>();
	     tempdata.put("BVoIP", "BVoIP"); 
	     tempdata.put("BVoIP Non-Usage", "BVoIP"); 
	     BVOIP_PRODUCT_MAP = Collections.unmodifiableMap(tempdata);
	}
	
	public static final  Map<String,String> ONENET_PRODUCT_MAP;
	static {
	     Map<String, String> tempdata = new HashMap<>();
	     tempdata.put("SDN/OneNet LD VOICE Usage", "OneNet"); 
	     tempdata.put("SDN/OneNet LD VOICE Features", "OneNet"); 
	     ONENET_PRODUCT_MAP = Collections.unmodifiableMap(tempdata);
	}
	
	 
	 /** The Constant RATE_TYPE. */
	 public static final  List<String> TDM_GROUP;
		 static {
			 List<String> temp=new ArrayList<>();
			  temp.add("DSODS1"); 
			  temp.add("DS3"); 
			 TDM_GROUP = Collections.unmodifiableList(temp);
		}
	 
	 
	 public static final String SUB_DATA="data";
	 public static final  Map<String,String> TDM_ACCESS_SPEED_CONVERSION;
	 static {
		 Map<String, String> temp = new HashMap<>();
	     temp.put("GBPS", "Gbps"); 
	     temp.put("gbps", "Gbps"); 
	     temp.put("gb", "Gbps"); 
	     temp.put("mb", "Mbps"); 
	     temp.put("mbps", "Mbps"); 
	     temp.put("MBPS", "Mbps"); 
	     temp.put("KBPS", "Kbps"); 
	     temp.put("kbps", "Kbps"); 
	     temp.put("kb", "Kbps"); 
	     TDM_ACCESS_SPEED_CONVERSION = Collections.unmodifiableMap(temp);
	}
	 
	public static final String FLOW_TYPE = "flowType";
	public static final String CUSTOM_CONFIG_RULES="CUSTOM_CONFIG_RULES";
	public static final String LD_PRODUCT_LINE="LD";
	public static final String TELCO_PRODUCT_LINE="telco";
	 public static final  List<String> LD_PRODUCT_LINE_PRODUCTS;
	 static {
		 List<String> temp=new ArrayList<>();
		  temp.add("AVPN"); 
		  temp.add("ADI"); 
		  LD_PRODUCT_LINE_PRODUCTS = Collections.unmodifiableList(temp);
	}
	public static final String NX_SOLIUTION_ID="nxSolutionId";
	public static final String INPUT_JSON="inputJson";
	public static final String UNIQUEID_BEID_MAP="uniqueBeidMap";
	public static final String NX_ACCESS_PRICE_ID="nxAccessPriceId";
	public static final String REQUEST_SITE_DATA_SOURCE="REQUEST_SITE_DATA";
	public static final String LINE_ITEM_DATA_SOURCE="LINE_ITEM_LOOKUP_DATA";
	public static final String PRODUCT_MODEL="productModel_pm";
	public static final String NO_OF_SITE_SEARCH="numberOfSiteSearch_pm";
	public static final String NO_OF_PRODUCTS="numberOfProducts_pm";
	public static final  Map<String,String> FMO_CONFIG_ACCESS_PRODUCT;
	 static {
		 Map<String, String> temp = new HashMap<>();
	     temp.put("AVPN/Ethernet", "LocalAccess"); 
	     temp.put("AVPN/TDM", "LocalAccess"); 
	     temp.put("AVPN/InternationalAccess", "LocalAccess"); 
	     temp.put("ADI/Ethernet", "LocalAccess"); 
	     temp.put("ADI/TDM", "LocalAccess"); 
	     FMO_CONFIG_ACCESS_PRODUCT = Collections.unmodifiableMap(temp);
	}
	 public static final String FMO_DOMESTIC_ACCESS_TYPE_DATASET="FMO_DOMESTIC_ACCESS_TYPE";
	 public static final String AVPN_ETHERNET="AVPN/Ethernet";
	 public static final String AVPN_TDM="AVPN/TDM";
	 public static final String AVPN_INTL_ACCESS="AVPN/InternationalAccess";
	 public static final String ADI_ETHERNET="ADI/Ethernet";
	 public static final String ADI_TDM="ADI/TDM";
	 
	 public static final String NO_UNIQUE_KEY = "NO_UNIQUE_KEY";
	 public static final String SUBMITTED = "SUBMITTED";
	 public static final String COUNTRY_JSON_PATH = "$..country";
	 public static final  Map<String,List<String>> FMO_OFFER_NAME;
	 static {
		 Map<String, List<String>> temp = new HashMap<>();
		 temp.put("AVPN", new ArrayList<String>(Arrays.asList("AVPN")));
		 temp.put("ADI", new ArrayList<String>(Arrays.asList("ADI")));
	     temp.put("BVoIP/AVPN", new ArrayList<String>(Arrays.asList("BVoIP","AVPN"))); 
	     temp.put("BVoIP/ADI", new ArrayList<String>(Arrays.asList("BVoIP","ADI"))); 
	     FMO_OFFER_NAME = Collections.unmodifiableMap(temp);
	}
	 public static final String BVoIP_INTERNATIONAL="BVoIP/International";
	 public static final String OPTY_CALL_FAILLED="Opty processing failed in Nexxus";

	 public static final String DESCRIPTION ="DESCRIPTION";
	 public static final String SUB_DATA_PATH ="SUB_DATA_PATH";
	 public static final String NX_INR_DESIGN_DETAILS_COUNT ="NX_INR_DESIGN_DETAILS_COUNT";
	 public static final String JSON_DATA ="JSON_DATA";
     public static final String INR_CONFIG_SOL_PRODUCT_SKIP ="INR_CONFIG_SOL_PRODUCT_SKIP";
     public static final String FMO_PROCESSED_CUSTOM_FIELDS="fmoProcessedCustomFields";
 	 public static final String BVoIP_NON_USAGE ="BVoIP Non-Usage";
 	 public static final String BVOIP_DOMESTIC_USAGE_EXISTING_PF ="bVoIPDomesticUsageExisting_pf";
 	 public static final String GET_OPTY_WS = "GET_OPTY_WS";
 	 public static final String LINE_ITEM_PROCESSING = "lineItemProcessing";
 	 public static final String MP_TDM_CUSTOME_FIELDS_DATASET = "mp_tdm_speed_fields_fmo";
 	 public static final String FMO_TDM_SPEED_FIELDS_PATH = "$..accessSpeedUdfAttrId";
 	 public static final String CONFIG_SOLUTION_PRODUCT_FAILED = "configSolitionProductFailed";
 	 public static final String CONFIG_DESIGN_FAILED = "configDesignFailed ";
 	 public static final String UPDATE_PRICING_FAILED = "updatePricingFalied";
 	 public static final String MP_API_ERROR = "myPriceApiCallingFailed";
 	 public static final String NEXXUS_API_ERROR = "nexxusApiError";
 	 public static final String VTNS ="VTNS";
    public static final String DISQUALIFIED_CIRCUITS = "DISQUALIFIED_CIRCUITS";
	public static final String SOAP_WS_RETRIGGER_COUNT = "soapWsRetriggerCount";
	public static final String MYPRICE_FAILED_CIRCUITS = "MYPRICE_FAILED_CIRCUITS";
	public static final String FAILED_DMAAP_DESCRIPTION = "FAILED_DMAAP_DESCRIPTION";
	public static final String MISSING_MANDATORY_DESIGN_DATA ="MISSING_MANDATORY_DESIGN_DATA";
	public static final String REST_CONFIG_FAILURE_DATA = "REST_CONFIG_FAILURE_DATA";
	public static final String MP_TDM_CUSTOME_FIELDS_REST_DATASET = "mp_tdm_speed_fields_fmo_rest";
	 
	
	 public static final  List<String> CP4PRODUCTS;
	 static {
		 List<String> temp=new ArrayList<>();
		  temp.add("ABN"); 
		  temp.add("VTNS"); 
		  temp.add("OneNet"); 
		  temp.add("BVoIP"); 
		  CP4PRODUCTS = Collections.unmodifiableList(temp);
	}
	 
	 public static final String DEFAULT_VALUES = "DEFAULT_VALUES";
	 public static final String CONFIG_DESIGN_RESPONSE  = "configDesignResponse";
	 public static final String UNIQUE_ID_MAP_BY_PRODUCT_ID  = "uniqueIdDataMapByProductId";
	 public static final String PRODUCT_ID_SOLUTION_ID_MAP  = "productIdSolutionIdMap";
	 public static final String CONFIG_RESPONSE_PROCESSING_DATA  = "configResponseProcessingData";
	 public static final String PROXY  = "proxy";
	 public static final String IS_LAST_DESIGN  = "isLastDesign";
	 public static final String AUTOMATION_DMAAP_FAILED  = "AUTO_SUBMISSION_FAILED";
	 public static final String AUTOMATION_DMAAP_REJECTED  = "AUTO_SUBMISSION_REJECTED";
	 public static final String INR_BULK_MERGE="INR_BULK_MERGE";
 	 public static final String PARENT_PATH="PARENT_PATH";
	 public static final String [] endType = {"A", "Z"};
	 public static final String APPEND_LOCATIONS_IN_SERVICE_DATASET = "APPEND_LOCATIONS_IN_SERVICE";
	 public static final String REQUEST_SITE_ID_REF = "REQUEST_SITE_ID_REF";
	 public static final String CONFIG_SOLN_DESIGN_RESPONSE  = "configSolnDesignResponse";
	 public static final String NX_GROUP_ID = "nxGroupId";
	 public static final String NX_GROUP_IDS = "nxGroupIds";
	 public static final String DESIGN_DATA = "designData";
	 public static final String NX_KEY_ID = "nxKeyId";
	 public static final String USRP_AVPN_ADI_NXDWINV_CRITERIA = "product IN (:product) AND bill_month=:reqBillDate";
	 public static final String USRP_CIRCUITID_LIST = "USRP_CIRCUITID_LIST";
	 public static final String REST_PRODUCTS = "REST_PRODUCTS";
	 public static final String REST_API_NOT_INVOKED = "RN";
	 public static final String REST_API_SUCCEED = "RS";
	 public static final String REST_API_PARTIAL_SUCCEED = "RP";
	 public static final String REST_API_FAILED = "RF";
	 public static final String REST_API_UPDATED = "RU";
	 public static final String REST_ENABLED = "REST_ENABLED";
	 public static final String STATUS_DERIVED = "STATUS_DERIVED";
	 public static final String ARRAY_NAME = "arrayName";
	 public static final String MP_REST_DESIGN_LIMIT_DATASET = "MP_REST_DESIGN_LIMIT";
	 public static final String USRP_EPLS_ETHERNET = "USRP_EPLS_ETHERNET";//USRP EPLSWAN ETHERNET
	 public static final String MP_REST_DESIGN_LIMIT = "mpRestDesignLimit";
	 public static final String INR_REST_PRODUCTS = "INR_REST_PRODUCTS";
	 public static final String SUB_OFFER_NAME="subOfferName";
	 public static final String CURRENT_RESULT = "currentResult";
	 public static final String QUALIFIED_CIRCUITS = "QUALIFIED_CIRCUITS";
	 public static final String SAVE_MP_OUTPUT_JSON_OBJECT_NODE = "SAVE_MP_OUTPUT_JSON_OBJECT_NODE";
	 public static final  List<String> CIRCUITS;
	 static {
		 List<String> temp=new ArrayList<>();
		  temp.add("QUALIFIED_CIRCUITS"); 
		  temp.add("DISQUALIFIED_CIRCUITS"); 
		  CIRCUITS = Collections.unmodifiableList(temp);
	 }
	 public static final String ASENOD_3PA_USOC_RANGE="asenod3PaUsocIdRange";
	 public static final String ITEM_ID = "itemId";
	 public static final String NX_SITE_ID_ASR_ITEM_ID_MAP = "nxsiteIdAsrItemIdMap";
	 public static final String UPDATE_PRICING_UNMATCHING_ASR_IDS = "updatePricingUnmatchingAsrIds";
	 public static final String UPDATE_PRICING_UNMATCHING_NX_DESIGN_IDS = "updatePricingUnmatchingNxDesignIds";

	 public static final String AUDIT_CONFIG_SOLN_DESIGN_REST = "MYPRICE_CONFIG_SOLN_DESIGN_REST";
	 public static final String AUDIT_CONFIG_SOLN_DESIGN_REST_RC = "MYPRICE_CONFIG_SOLN_DESIGN_REST_RC";
	 public static final String AUDIT_UPDATE_PR_REST = "MYPRICE_UPDATE_PRICING_REST";
	 public static final String AUDIT_UPDATE_PR_REST_RC = "MYPRICE_UPDATE_PRICING_REST_RC";
	 public static final String NX_DESIGN_ID_SUCCESSFULLY_CONFIGURED="nxDesignIdConfigured";
	 public static final String REST_MP_CONFIG="REST_MP_CONFIG";
	 public static final String REST_CONFIG_SOL_DESIGN_STATUS="REST_CONFIG_SOL_DESIGN_STATUS";
	 public static final String REST_ERROR_MSG="REST_ERROR_MSG";
	 public static final String REST_UPDATE_PRICING_STATUS="REST_UPDATE_PRICING_STATUS";
	 public static final String NX_DESIGN_ID_LIST_UPDATE_PRICING_RECONFIGURE="NX_DESIGN_ID_LIST_UPDATE_PRICING_RECONFIGURE";
	 public static final String UPDATE_PRICING_RECONFIGURE="UPDATE_PRICING_RECONFIGURE";
	 public static final String AUDIT_UPDATE_MULTI_PRICE_REST = "MULTIPRICE_UPDATE_PRICING_REST";
	 public static final String DESIGN_ID_FOR_UPDATE_PRICING="designIdForUpDatePricing";
	 public static final String DESIGN_ID_FOR_UPDATE_PRICING_RECONFIGURE="designIdForUpDatePricingReconfigure";
	 public static final String CIR_QUANTITY = "cIR_qty_pf";
	 public static final String REST_PORT_QTY_VAL = "port_qty_pf/value/value";
	 public static final String REST_PORT_QTY_DISPLAY_VAL = "port_qty_pf/value/displayValue";
	 public static final String REST_CIR_QTY_VAL = "cIR_qty_pf/value/value";
	 public static final String REST_CIR_QTY_DISPLAY_VAL = "cIR_qty_pf/value/displayValue";
	 public static final String REQ_MCAD_ACTIVITY_UDFATTR ="reqMacdActivityUdfAttr";
	 public static final String REQ_MCAD_TYPE_VALUE ="reqMacdTypeValue";
	 public static final String ASE_MCAD_TYPE_DATASET="aseAMcadTypeDataset";
	 public static final String ASE_MCAD_ORDER_DATA ="mcadOrderData";
	 public static final String MCAD_TYPE_PATH_ASE="$..component.[?(@.componentCodeId==30)].designDetails.[?(@.udfId==200008)].udfAttributeId.[*]";
	 public static final String MCAD_TYPE_CHANGE ="Change";
		public static final List<String> PRODUCT_RATE_ID_MAP;
		static {
			List<String> temp=new ArrayList<>();
			  temp.add("ADI"); 
			  temp.add("AVPN");
			  temp.add("BVoIP");
			  temp.add("BVoIP Non-Usage");
			  temp.add("MIS/PNT");
			  PRODUCT_RATE_ID_MAP = Collections.unmodifiableList(temp);
		}
		 public static final String REST_WS_RETRIGGER_COUNT = "restWsRetriggerCount";
		 public static final String REST_CLIENT_ERROR = "restClientError";
		 public static final String	DOCUMENT_NUMBER="_document_number";
		 public static final  List<String> RESTUSAGE;
		 static {
			 List<String> temp=new ArrayList<>();
			  temp.add("VTNS"); 
			  temp.add("OneNet");
			  temp.add("ABN");
			  temp.add("BVoIP");
			  RESTUSAGE = Collections.unmodifiableList(temp);
		}
		 public static final String MULTIPLE_CONFIG = "multipleConfig";
		 public static final String UPDATE_PRICING_ERROR_MAP = "updatePricingErrorMap";
		 
		 public static final String GET_USERS_WS = "getUsers_WS";
		 public static final String REST_PRODUCTS_V2 = "REST_PRODUCTS_V2";
		 
	public static final String PORT_TYPE_STANDARD = "Standard";
	public static final String SSDF_COMPONENT = "SSDF_COMPONENT";
	public static final String WHOLESALE_MARKETSEGMENT = "Wholesale";
	public static final String ASE_ASR_ITEM_ID_PATH = "$..designSiteOfferPort[0,1].[?(@.typeOfInventory == 'To' || @.typeOfInventory  == null)].component.[?(@.componentCodeId==30)].designDetails.[?(@.udfId==20169)].udfAttributeText.[*]";
	public static final String EPLSWAN_NX_SITE_ID_PATH = "$..[?(@.offerId==210)]..nxSiteId";
	public static final String EPLSWAN_CIRCUIT_ID_PATH = "$..[?(@.offerId==210)]..[?(@.componentCodeId==1210)].designDetails.[?(@.udfId==200158)].udfAttributeText.[*]";
	public static final String ADE_ASR_ITEM_ID_PATH = "$..[?(@.offerId==120)]..[?(@.componentCodeId==1210)].designDetails.[?(@.udfId==200162)].udfAttributeText.[*]";
	public static final String NX_DESIGN_ID_LIST = "nxDesignIdList";
	public static final String MP_SOLUTION_ID_LIST = "mpSolutionIdList";
	public static final  List<String> ASR_STATUS_FOR_RECONFIGURE;
	 static {
		 List<String> temp=new ArrayList<>();
		  temp.add("C");
		  temp.add("CL");
		  ASR_STATUS_FOR_RECONFIGURE = Collections.unmodifiableList(temp);
	}
	 
	 public static final  Map<String,String> OFFER_NAME_MAP;
	 static {
		 Map<String, String> temp = new HashMap<>();
	     temp.put("MIS/PNT", "ADI"); 
	     OFFER_NAME_MAP = Collections.unmodifiableMap(temp);
	}
	 
	public static final String TDM_NXT1_CIRCUIT_ID = "TDM_NXT1_CIRCUIT_ID";
	public static final String SUB_OFFER = "SUB_OFFER";
	public static final String NXT1_CKT = "NXT1_CKT";
	
	public static final List<Long> INR_EDIT_REQUEST_STATUS ;
	 static {
		List<Long> temp=new ArrayList<>();
		temp.add(30L);
		temp.add(80L);
		temp.add(20L);
		temp.add(270L);
		INR_EDIT_REQUEST_STATUS = Collections.unmodifiableList(temp);
	}

	 public static final String REVISION="revision";
	 public static final String VERSION="version";
	 public static final String BSID="bsid";
	 public static final String MP_LATEST_REVISION="mpLatestRevision";
	 public static final String MP_LATEST_VERSION="mpLatestversion";
	 
	 public static final String BILL_CHRGS_WS = "billingChargesWs";

	 
	 public static final String USAGE_NON_USAGE_PRODUCT_NAME="USAGE_NON_USAGE_PRODUCT_NAME";
	 public static final String USAGE_NON_USAGE_OFFER="USAGE_NON_USAGE_OFFER";
	 
	 public static final String SOURCE_USRP = "USRP";
	 
	 public static final  Map<String,String> USRP_OFFER_NAME_MAP;
	 static {
		 Map<String, String> temp = new HashMap<>();
	     temp.put("TDM", "DOMESTIC DEDICATED ACCESS"); 
	     temp.put("Ethernet", "DOMESTIC DEDICATED ACCESS"); 
	     USRP_OFFER_NAME_MAP = Collections.unmodifiableMap(temp);
	}
	public static final String CIRCUIT_TYPE_STANDARD ="STANDARD";
	public static final String CIRCUIT_TYPE_ATX ="ATX";
	 
	public static final String FLOWTYPE="FLOWTYPE";
	public static final String POPCOLLECTOR_ASR_PATH="$..[?(@.componentCodeId ==30 && 22565 in @.designDetails[*].udfId && %s in @.designDetails.[?(@.udfId==22565)].udfAttributeText.[*] && 30605 in @.designDetails.[?(@.udfId==22564)].udfAttributeId.[*])].designDetails.[?(@.udfId==20169)].udfAttributeText.[*]";

	public static final  List<String> USRP_PORT_OFFER;
	 static {
		 List<String> temp=new ArrayList<>();
		  temp.add("AVPN"); 
		  temp.add("GMIS");
		  temp.add("MIS/PNT");
		  temp.add("AVTS");
		  temp.add("ANIRA");
		  USRP_PORT_OFFER = Collections.unmodifiableList(temp);
	}
	 public static final  List<String> USRP_ACCESS_OFFER;
	 static {
		 List<String> temp=new ArrayList<>();
		  temp.add("Ethernet"); 
		  temp.add("TDM");
		  temp.add("International");
		  USRP_ACCESS_OFFER = Collections.unmodifiableList(temp);
	}	
	
	public static final String NX_CLONE = "nxClone";
	public static final String ACCESS_PROVIDED_SKIP_MP = "ACCESS_PROVIDED_SKIP_MP";	
	
	public static final  List<String> USRP_ACCESS_PRODUCT;
	 static {
		 List<String> temp=new ArrayList<>();
		  temp.add("AVPN"); 
		  temp.add("GMIS");
		  temp.add("MIS/PNT");
		  USRP_ACCESS_PRODUCT = Collections.unmodifiableList(temp);
	}
	 public static final  List<String> DW_INVENTORY_PRODUCTS;
	 static {
		 List<String> temp=new ArrayList<>();
		  temp.add("AVPN"); 
		  temp.add("GMIS");
		  temp.add("MIS/PNT");
		  temp.add("AVTS");
          temp.add("ANIRA");
          temp.add("BVoIP Non-Usage");
          temp.add("EPLSWAN");
		  DW_INVENTORY_PRODUCTS = Collections.unmodifiableList(temp);
	}
	 public static final  List<String> DW_UB_CALL_PRODUCTS;
	 static {
		 List<String> temp=new ArrayList<>();
          temp.add("BVoIP");
          temp.add("ABN LD Voice");
          DW_UB_CALL_PRODUCTS = Collections.unmodifiableList(temp);
	}
	public static final List<String> INR_BETA_ONENET_PRODUCTS;
	static {
		List<String> temp = new ArrayList<>();
		temp.add("SDN/ONENET LD Voice Usage");
		temp.add("SDN/OneNet LD Voice Features");
		INR_BETA_ONENET_PRODUCTS = Collections.unmodifiableList(temp);
	}
	
	public static final String ONENET_USAGE_PRODUCT_CD = "SDN/ONENET LD Voice Usage";
	public static final String ONENET_FEATURE_PRODUCT_CD = "SDN/OneNet LD Voice Features";
	public static final String ABN_LD_VOICE= "ABN LD Voice";
	
	public static final  List<String> DW_VTNS_LD;
	 static {
		 List<String> temp=new ArrayList<>();
         temp.add("VTNS LD Voice Usage");
         temp.add("VTNS LD Voice Features");
         DW_VTNS_LD = Collections.unmodifiableList(temp);
	}

 	 public static final String VTNS_LD = "VTNS LD Voice Usage";
 	 public static final String VTNS_LD_FEATURE = "VTNS LD Voice Features";
 	 public static final String VTNS_FEATURE="VTNS Feature";
}
