package com.att.sales.nexxus.constant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Class FmoConstants.
 *
 * @author vt393d
 * 
 * Constant class for maintaining all constants data
 */
public final class FmoConstants {

	/**
	 * Instantiates a new fmo constants.
	 */
	private FmoConstants() {}
	
	/** The Constant TEMP_OFFER_ID. */
	public static final Long TEMP_OFFER_ID=0l;
	
	/** The Constant SOLUTION_TAG. */
	public static final String SOLUTION_TAG="SOLUTION";
	
	/** The Constant OFFER_TAG. */
	public static final String OFFER_TAG="OFFER";
	
	/** The Constant SITE_TAG. */
	public static final String SITE_TAG="SITE";
	
	/** The Constant DESIGN_TAG. */
	public static final String DESIGN_TAG="DESIGN";
	
	/** The Constant PRICE_DETAIL_TAGS. */
	public static final String PRICE_DETAIL_TAGS="PRICE_DETAILS";
	
	/** The Constant SOLUTION_ATR. */
	public static final String SOLUTION_ATR="solution";
	
	/** The Constant OFFER_ATR. */
	public static final String OFFER_ATR="offers";
	
	/** The Constant SITE_ATR. */
	public static final String SITE_ATR="site";
	
	/** The Constant DESIGN_ATR. */
	public static final String DESIGN_ATR="design";
	
	/** The Constant PRICE_DETAILS_ATR. */
	public static final String PRICE_DETAILS_ATR="priceDetails";
	
	/** The Constant ACCESS_DETAILS_ATR. */
	public static final String ACCESS_DETAILS_ATR="accessDetails";
	
	/** The Constant ACCESS_DETAILS_TAG. */
	public static final String ACCESS_DETAILS_TAG="ACCESS_DETAILS";
	
	/** The Constant PORT_ID. */
	public static final String PORT_ID="portId";
	
	/** The Constant ACCESS_PRICE_UI_DETAILS. */
	public static final String ACCESS_PRICE_UI_DETAILS="accessPriceUIDetails";
	
	/** The Constant COMPONENT_ID. */
	public static final String COMPONENT_ID="componentId";
	
	/** The Constant PRICE_ATTRIBUTE. */
	public static final String PRICE_ATTRIBUTE="priceAttributes";
	
	/** The Constant ALL. */
	public static final String ALL="*";
	
	/** The Constant JSON_DOT. */
	public static final String JSON_DOT=".";
	
	/** The Constant TERM. */
	public static final String TERM="term";
	
	/** The Constant ICB_DESIRED_DISCOUNT. */
	public static final String ICB_DESIRED_DISCOUNT="icbDesiredDiscPerc";
	
	/** The Constant OFFER_ID. */
	public static final String OFFER_ID="offerId";
	
	/** The Constant COUNTRY_CD. */
	public static final String COUNTRY_CD="country";
	
	/** The Constant PRODUCT_RATE_ID. */
	public static final String PRODUCT_RATE_ID="productRateId";
	
	/** The Constant BEID. */
	public static final String BEID="beid";
	
	/** The Constant DISCOUNT_DETAILS. */
	public static final String DISCOUNT_DETAILS="discountDetails";
	
	/** The Constant FREQUENCY. */
	public static final String FREQUENCY="frequency";
	
	/** The Constant MRC. */
	public static final String MRC="MRC";
	
	/** The Constant LOCAL_LST_PRICE. */
	public static final String LOCAL_LST_PRICE="localListPrice";
	
	/** The Constant ACTUAL_PRICE. */
	public static final String ACTUAL_PRICE="actualPrice";
	
	/** The Constant SITE_ID. */
	public static final String SITE_ID="siteId";
	
	/** The Constant SITE_NAME. */
	public static final String SITE_NAME="siteName";
	
	/** The Constant STATE. */
	public static final String STATE="state";
	
	/** The Constant BEID_MRC. */
	public static final String BEID_MRC="beid_mrc";
	
	/** The Constant FMO. */
	public static final String FMO="FMO";
	
	/** The Constant LOCAL_PRICE. */
	public static final String LOCAL_PRICE="local_price";
	
	/** The Constant ACCESS_ARCHITECTURE. */
	public static final String ACCESS_ARCHITECTURE="access_architecture";
	
	/** The Constant PROTOCOL. */
	public static final String PROTOCOL="protocol";
	
	/** The Constant LOCAL_CURRENCY. */
	public static final String LOCAL_CURRENCY="localCurrency";
	
	/** The Constant TOP_PROD_ID. */
	public static final String TOP_PROD_ID="topProdId";
	
	/** The Constant PRICE_DETAILS_BY_BEID. */
	public static final String PRICE_DETAILS_BY_BEID="priceDetailsByBeId";
	
	/** The Constant PRICE_DETAILS_BY_MULTIPLE_RATE_IDS. */
	public static final String PRICE_DETAILS_BY_MULTIPLE_RATE_IDS="priceDetailsByMultRateId";
	
	/** The Constant PRICE_DETAILS_BY_MULTIPLE_BEIDS. */
	public static final String PRICE_DETAILS_BY_MULTIPLE_BEIDS="priceDetailsByMultBeIds";
	
	/** The Constant PROTOCOL_BY_PORTID. */
	public static final String PROTOCOL_BY_PORTID="protocolByPortId";
	
	/** The Constant ACCESS_ARCH_BY_PORTID. */
	public static final String ACCESS_ARCH_BY_PORTID="accessArchitectureByPortId";
	
	/** The Constant NPA_NXX_BY_PORTID. */
	public static final String NPA_NXX_BY_PORTID="npanxxByPortId";
	
	/** The Constant QUANTITY. */
	public static final String QUANTITY="quantity";
	
	/** The Constant PORT_SPEED. */
	public static final String PORT_SPEED="portSpeed";
	
	/** The Constant EXTERNAL_KEY. */
	public static final String EXTERNAL_KEY="externalKey";
	
	/** The Constant REF_SOLUTION_ID. */
	public static final String REF_SOLUTION_ID="referenceSolutionId";
	
	/** The Constant REF_OFFER_ID. */
	public static final String REF_OFFER_ID="referenceOfferId";
	
	/** The Constant REF_SITE_ID. */
	public static final String REF_SITE_ID="referenceSiteId";
	
	/** The Constant REF_PORT_ID. */
	public static final String REF_PORT_ID="referencePortId";
	
	/** The Constant NOT_AVAILABLE. */
	public static final String NOT_AVAILABLE="N/A";
	
	/** The Constant NRC. */
	public static final String NRC="NRC";
	
	/** The Constant SALES_UDF_LOOKUP_SOURCE. */
	public static final String SALES_UDF_LOOKUP_SOURCE="SALES_MS_PRODCOMP_UDF_ATTR_VAL";
	
	/** The Constant IMS2_LOOKUP_SOURCE. */
	public static final String IMS2_LOOKUP_SOURCE="IMS2_LOOKUP_SOURCE";
	
	/** The Constant POSTAL_CD. */
	public static final String POSTAL_CD="postalCode";
	
	/** The Constant ADDRESS. */
	public static final String ADDRESS="address";
	
	/** The Constant CITY. */
	public static final String CITY="city";
	
	/** The Constant INTERMEDIATE_JSON. */
	public static final String INTERMEDIATE_JSON="IntermediateJson";
	
	/** The Constant PREVIEW_INR_AVPN_TAB. */
	public static final String PREVIEW_INR_AVPN_TAB="InR Preview";
	
	/** The Constant OUTPUT_JSON. */
	public static final String OUTPUT_JSON="outputJson";
	
	/** The Constant OUTPUT_FILE. */
	public static final String OUTPUT_FILE="outputFile";
	
	/** The Constant FILE_TYPE. */
	public static final String FILE_TYPE=".xlsx";
	
	/** The Constant STATE_COUNTRY_CRITERIA. */
	public static final String STATE_COUNTRY_CRITERIA="STATE##COUNTRY_CD";
	
	/** The Constant ETHERNET_ACCESS_CRITERIA. */
	public static final String ETHERNET_ACCESS_CRITERIA="ACCESS_ARCH##ACCESS_TYPE##COUNTRY_CD";
	
	/** The Constant INTL_ACCESS_CRITERIA. */
	public static final String INTL_ACCESS_CRITERIA="SiteCountry##Product##Currency##Technology";
	
	/** The Constant COUNTRY_CRITERIA. */
	public static final String COUNTRY_CRITERIA="COUNTRY_CD";
	
	/** The Constant DS_ACCESS_CRITERIA. */
	public static final String DS_ACCESS_CRITERIA="AccessType##AccessSpeed";
	
	/** The Constant CALL_OPTYINFO. */
	public static final String CALL_OPTYINFO = "callOptyInfo";
	
	/** The Constant YES. */
	public static final String YES = "YES";
	
	/** The Constant NO. */
	public static final String NO = "NO";
	
	/** The Constant PRICE_TYPE_MAPPING. */
	public static final  Map<String,String> PRICE_TYPE_MAPPING;
	 static {
		 Map<String, String> temp = new HashMap<>();
	     temp.put("PORTRC", "portRCRateId"); 
	     temp.put("PORTNRC", "portNRCRateId"); 
	     temp.put("ACCESSRC", "accessRCRateId"); 
	     temp.put("ACCESSNRC", "accessNRCRateId"); 
	     PRICE_TYPE_MAPPING = Collections.unmodifiableMap(temp);
	}
	 
	/** The Constant RATE_TYPE. */
	public static final  List<String> RATE_TYPE;
	 static {
		 List<String> temp=new ArrayList<>();
		 temp.add("RC");
		 temp.add("NRC");
	     RATE_TYPE = Collections.unmodifiableList(temp);
	}
	 
	 /** The Constant IMS2_CODE. */
 	public static final  List<String> IMS2_CODE;
	 static {
		 List<String> temp=new ArrayList<>();
		 temp.add("access");
		 temp.add("port");
		 temp.add("cos");
		 IMS2_CODE = Collections.unmodifiableList(temp);
	}
	
	/** The Constant PRICE_TYPE_DATA. */
	public static final String PRICE_TYPE_DATA="priceTypeData"; 
	
	/** The Constant PRICE_TYPE_SOURCE. */
	public static final String PRICE_TYPE_SOURCE="PRICE_TYPE"; 
	
	/** The Constant AVPN. */
	public static final String AVPN="AVPN"; 
	
	/** The Constant ADI. */
	public static final String ADI="ADI";
	
	/** The Constant BVOIP. */
	public static final String BVOIP="BVoIP";
	
	/** The Constant PORT_MRC_BEID_PATH. */
	public static final String PORT_MRC_BEID_PATH="portMrcBeidData";
	
	/** The Constant PORT_NRC_BEID_PATH. */
	public static final String PORT_NRC_BEID_PATH="portNrcBeidData";
	
	/** The Constant ACCESS_MRC_BEID_PATH. */
	public static final String ACCESS_MRC_BEID_PATH="accessMrcBeidData";
	
	/** The Constant ACCESS_NRC_BEID_PATH. */
	public static final String ACCESS_NRC_BEID_PATH="accessNrcBeidData";
	
	/** The Constant MRC_BEID_SOURCE. */
	public static final String MRC_BEID_SOURCE="mrcBeid";
	
	/** The Constant NRC_BEID_SOURCE. */
	public static final String NRC_BEID_SOURCE="nrcBeid";
	
	/** The Constant MRC_RATEID_SOURCE. */
	public static final String MRC_RATEID_SOURCE="rcRateId";
	
	/** The Constant NRC_RATEID_SOURCE. */
	public static final String NRC_RATEID_SOURCE="nrcRateId";
	
	/** The Constant LOOKUP. */
	public static final String LOOKUP="lookup";
	
	/** The Constant COLUMN. */
	public static final String COLUMN="column";
	
	/** The Constant RATE_ID_SOURCE. */
	public static final String RATE_ID_SOURCE="rateId";
	
	/** The Constant PRICE_DETAILS_BY_RATE_ID. */
	public static final String PRICE_DETAILS_BY_RATE_ID="priceDetailsByRateId";
	
	/** The Constant RATE_ID_COUNTRY_CRITERIA. */
	public static final String RATE_ID_COUNTRY_CRITERIA="RCRATEID##COUNTRY_CD";
	
	/** The Constant BEID_ID_COUNTRY_CRITERIA. */
	public static final String BEID_ID_COUNTRY_CRITERIA="BEID##COUNTRY_CD";
	
	/** The Constant ACCESS_RC_RATEID_CRITERIA. */
	public static final String ACCESS_RC_RATEID_CRITERIA="AccessRCRateId";
	
	/** The Constant US_COUNTRY_CD. */
	public static final String US_COUNTRY_CD="US";
	
	/** The Constant ALL_MRC_BEID. */
	public static final String ALL_MRC_BEID="allMrcBeid";
	
	/** The Constant RC_RATE_ID. */
	public static final String RC_RATE_ID="rcRateId";
	
	public static final String NRC_RATE_ID="nrcRateId";
	
	/** The Constant DESIGN_ACCESS_ARCHITECTURE. */
	public static final String DESIGN_ACCESS_ARCHITECTURE="designAccessArch";
	
	/** The Constant ACCESS_SPEED. */
	public static final String ACCESS_SPEED="accessSpeed";
	
	/** The Constant DESIGN_LEVEL_TERM. */
	public static final String DESIGN_LEVEL_TERM="designLevelterm";
	
	/** The Constant INTERFACE_TYPE. */
	public static final String INTERFACE_TYPE="interfaceType";
	
	/** The Constant POP_CILLI. */
	public static final String POP_CILLI="popClli";
	
	/** The Constant SITE_TYPE. */
	public static final String SITE_TYPE="siteType";
	
	/** The Constant IGLOO_ID. */
	public static final String IGLOO_ID="iglooId";
	
	/** The Constant SUPPLIER_NAME. */
	public static final String SUPPLIER_NAME="supplierName";
	
	/** The Constant SPEED. */
	public static final String SPEED="speed";
	
	/** The Constant DQ_ID. */
	public static final String DQ_ID="dqid";
	
	/** The Constant SWC_CLLI. */
	public static final String SWC_CLLI="swcClli";
	
	/** The Constant CUSTOMER_LOC_CLLI. */
	public static final String CUSTOMER_LOC_CLLI="customerLocationClli";
	
	/** The Constant ACCESS_TAIL_TECH_BY_PORTID. */
	public static final String ACCESS_TAIL_TECH_BY_PORTID="accessTailTechByPortId";
	
	/** The Constant ACCESS_SPEED_BY_PORTID. */
	public static final String ACCESS_SPEED_BY_PORTID="accessSpeedByPortId";
	
	/** The Constant MILEAGE_BY_PORTID. */
	public static final String MILEAGE_BY_PORTID="mileageByPortId";
	
	/** The Constant ACCESS. */
	public static final String ACCESS="Access";
	
	/** The Constant DEFAULT_ACCESS_TAIL_TECH. */
	public static final String DEFAULT_ACCESS_TAIL_TECH="Select Technology";
	
	/** The Constant MIS. */
	public static final String MIS="MIS";
	
	/** The Constant ETHERNET_ACCESS. */
	public static final String ETHERNET_ACCESS="EthernetAccess";
	
	/** The Constant DESIGN_LEVEL_CURRENCY. */
	public static final String DESIGN_LEVEL_CURRENCY="designLevelCurrencyCd";
	
	/** The Constant DESIGN_LEVEL_NPANXX. */
	public static final String DESIGN_LEVEL_NPANXX="designLevelNpanxx";
	
	/** The Constant DESIGN_LEVEL_DQID. */
	public static final String DESIGN_LEVEL_DQID="designLevelDqId";
	
	/** The Constant DESIGN_LEVEL_SPEED. */
	public static final String DESIGN_LEVEL_SPEED="designLevelSpeed";
	
	/** The Constant DESIGN_LEVEL_ACEES_TAIL_TECH. */
	public static final String DESIGN_LEVEL_ACEES_TAIL_TECH="designLevelAceesTailTech";
	
	/** The Constant CURRENCY_CD. */
	public static final String CURRENCY_CD="currencyCd";
	
	/** The Constant COUNTRY_CURRENCY_MAPPING. */
	public static final String COUNTRY_CURRENCY_MAPPING="COUNTRY_CURRENCY_MAPPING";
	
	/** The Constant DEFAULT_FMO_QUANTITY. */
	public static final String DEFAULT_FMO_QUANTITY="DEFAULT_FMO_QUANTITY";
	
	/** The Constant DESIGN_LEVEL_ACCESS_SPEED. */
	public static final String DESIGN_LEVEL_ACCESS_SPEED="designLevelAccessSpeed";
	
	/** The Constant DESIGN_LEVEL_MILEAGE. */
	public static final String DESIGN_LEVEL_MILEAGE="designLevelMileage";
	
	/** The Constant ACCESS_SPEED_UDF_ATTR_ID. */
	public static final String ACCESS_SPEED_UDF_ATTR_ID="accessSpeedUdfAttrId";
	
	/** The Constant ACTIVE_FLAG. */
	public static final String ACTIVE_FLAG="Y";
	
	/** The Constant RCRATEID_CON_CALLTYPE_CRITERIA. */
	public static final String RCRATEID_CON_CALLTYPE_CRITERIA="RCRATEID##ConcurrentCallType";
	
	/** The Constant RCRATEID_HAS_VOICEDNA_CRITERIA. */
	public static final String RCRATEID_HAS_VOICEDNA_CRITERIA="RCRATEID##HasVoiceDna";
	
	/** The Constant CONCURRENT_CALL_TYPE. */
	public static final String CONCURRENT_CALL_TYPE="concurrentCallType";
	
	/** The Constant DESIGN_LEVEL_CALL_TYPE. */
	public static final String DESIGN_LEVEL_CALL_TYPE="designLevelConCallType";
	
	/** The Constant OUTPUT_TAB_LIST. */
	public static final  List<String> OUTPUT_TAB_LIST;
	 static {
		 List<String> temp=new ArrayList<>();
		 temp.add("RC");
		 temp.add("NRC");
		 OUTPUT_TAB_LIST = Collections.unmodifiableList(temp);
	}
	 
 	/** The Constant OFFER_ID_MAPPING. */
 	public static final  Map<Long,String> OFFER_ID_MAPPING;
	 static {
		 Map<Long, String> temp = new HashMap<>();
	     temp.put(4l, "AVPN"); 
	     temp.put(1l, "MIS");
	     OFFER_ID_MAPPING = Collections.unmodifiableMap(temp);
	}
	 public static final String COMP_PARENT_ID="componentParentId";
	 public static final String COMP_TYPE="componentType";
	 public static final String ERATE_IND = "erateInd";
	 public static final Long BVOIP_OFFER_ID = 7L;
	 
	 public static final String NX_COUNTRY_SOURCE="nx_country";
	 
	 public static final String AVPN_LOOKUP_BEID_FOR_FALLOUT="avpnLookupBeidForFallout"; 
	 public static final String AVPN_LOOKUP_BEID_DATASET="avpnLookupBeidDataset";
	 public static final String MYPRICE_FMO="MYPRICE_FMO";
	 public static final String IS_COMPLETE_FALLOUT="isFallout";
	 public static final String IS_LINE_ITEM_PICKED="isLineItemPicked";
	 public static final  Map<Long,String> FMO_LINE_ITEM_PROCESSING_ERROR_MSG;
	 static {
		 Map<Long, String> temp = new HashMap<>();
	     temp.put(20L, MessageConstants.SUCCESS_WITH_FALLOUT_MSG); 
	     temp.put(70L, MessageConstants.FALLOUT_MSG); 
	     FMO_LINE_ITEM_PROCESSING_ERROR_MSG = Collections.unmodifiableMap(temp);
	}
	 
	 public static final  Map<Long,Integer> FMO_LINE_ITEM_PROCESSING_ERROR_CD;
	 static {
		 Map<Long, Integer> temp = new HashMap<>();
	     temp.put(20L, MessageConstants.SUCCESS_WITH_FALLOUT); 
	     temp.put(70L, MessageConstants.FALLOUT); 
	     FMO_LINE_ITEM_PROCESSING_ERROR_CD = Collections.unmodifiableMap(temp);
	}
	 public static final String FALLOUT_MAP="falloutMap";
	 
	 public static final String PRICE_TYPE_DATASET="FMO_PRICE_TYPE";
	 
	 public static final String FMO_DESIGN_USOC_FIELDS = "FMO_DESIGN_USOC_FIELDS";
	 
	 public static final String UI_PRODUCT_LIST = "UI_PRODUCT_LIST";
	 
	 public static final String UI_PRODUCT_PRODUCT_SEARCH = "UI_PRODUCT_PRODUCT_SEARCH";
	 
      public static final String UI_BILL_MONTH_USRP = "UI_BILL_MONTH_USRP";
	 
	 public static final String UI_BEGIN_BILL_MONTH_USRP = "UI_BEGIN_BILL_MONTH_USRP";
	 
	 public static final String USRP_PRODUCT_HRID_COND = "USRP_PRODUCT_HRID_COND";
}
