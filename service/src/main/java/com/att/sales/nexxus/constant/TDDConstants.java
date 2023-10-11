package com.att.sales.nexxus.constant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.att.sales.nexxus.dao.model.NxDesign;
import com.att.sales.nexxus.transmitdesigndata.model.CircuitDetails;

/**
 * The Class TransmitDesignDataConstants.
 */
public class TDDConstants {

	private TDDConstants() {}
	
	public static final String CIRCUIT="Circuit";
	public static final String SOLUTION="Solution";
	 public static final String TEXT="Text";
	 public static final String ID="Id";
	 public static final String ASE_OFFER_NAME="ASE";
	 public static final String ASE_OFFER_ID="103";
	 public static final String ADE_OFFER_NAME="ADE";
	 public static final String ADE_OFFER_ID="120";
	 public static final String ASENOD_OFFER_ID="6";
	 public static final String ASENOD_OFFER_NAME="ASENoD";
	 public static final  Map<String,String> OFFER_ID_BUNDLE_CD_MAPPING;
	 static {
		 Map<String, String> temp = new HashMap<>();
	     temp.put(ASE_OFFER_ID, ASE_OFFER_NAME); 
	     temp.put(ADE_OFFER_ID, ADE_OFFER_NAME); 
	     temp.put(ASENOD_OFFER_ID, ASENOD_OFFER_NAME); 
	     OFFER_ID_BUNDLE_CD_MAPPING = Collections.unmodifiableMap(temp);
	}
	 public static final String UPDATE_DESIGN_ACTIVITY="UpdateDesign";
	 public static final String ATT_ID="attuId";
	 public static final String SOLUTION_ID="solutionId";
	 public static final String NX_SOLUTION_ID="nxSolutionId";
	 public static final String OFFER_ID="offerId";
	 public static final String SITE_ID="siteId";
	 public static final List<String> ACTION_DETERMINANTS_ASE;
	 static {
		 List<String> temp=new ArrayList<>();
		 temp.add("Design");
		 temp.add("Price");
		 temp.add("ASE");
		 ACTION_DETERMINANTS_ASE=Collections.unmodifiableList(temp);
	 }
	 public static final List<String> ACTION_DETERMINANTS_ADE;
	 static {
		 List<String> temp=new ArrayList<>();
		 temp.add("Design");
		 temp.add("Price");
		 temp.add("ADE");
		 ACTION_DETERMINANTS_ADE=Collections.unmodifiableList(temp);
	 }
	 public static final String TRANSMIT_DESIGN_DATA="transmitDesignData";
	 public static final String UDF_MAPPING="udfMapping";
	 public static final String PED_STATUS_MAP="pedStatusMap";
	 public static final String ENDPOINT_A="A";
	 public static final String ENDPOINT_Z="Z";
	 public static final  Map<String,String> ENDPOINT_UDF_MAPPING;
	 static {
		 Map<String, String> temp = new HashMap<>();
	     temp.put(ENDPOINT_A, "21033"); 
	     temp.put(ENDPOINT_Z, "21034");
	     ENDPOINT_UDF_MAPPING = Collections.unmodifiableMap(temp);
	}
	 public static final String SOLUTION_LEVEL_STATUS="solLevelStatus";
	 public static final String CIRCUIT_LEVEL_STATUS="circuitLevelStatus";
	 public static final String RESPONSE_TYPE="responseType";
	 public static final String A_SITE_ID="endPointASiteId";
	 public static final String Z_SITE_ID="endPointZSiteId";
	 public static final String ASR_ITEM_ID="asrItemId";
	 public static final String REQ_ITEM_DATASET="DPP_REQUEST_FIELD";
	 public static final String SOLUTION_LEVEL_CANCEL_REASON="solLevelcancelReason";
	 public static final String CIRCUIT_LEVEL_CANCEL_REASON="circuitLevelcancelReason";
	 public static final String SOLUTION_DATA="solutionData";
	 public static final String BUNDLE_CODE="bundleCode";
	 public static final String TDD_STATUS_FAILED = "Failed";
	 public static final String TDD_KMZ_MAP_LINK = "kmzMapLink";
	 public static final String IPE_INDICATOR="ipeIndicator";
	 public static final String TDD_STATUS="status";
	 public static final String TDD_STATUS_COMPLETED="D";
	 public static final String TDD_STATUS_SOLD="SD";
	 public static final String TDD_STATUS_CANCEL="CL";
	 public static final String circuitDetails="circuitDetails";
	 public static final String nxDesign="nxDesign";
	 public static final String FLOW_TYPE="flowType";
	 public static final String OPTY_ID="optyId";
	 public static final String SOL_COST_IND="solCostInd";
	 
	 
}
