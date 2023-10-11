package com.att.sales.nexxus.handlers;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyMap;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.simple.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.dao.model.NxDesign;
import com.att.sales.nexxus.dao.model.NxDesignAudit;
import com.att.sales.nexxus.dao.model.NxDesignDetails;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.model.NxMpConfigMapping;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.repository.NxDesignAuditRepository;
import com.att.sales.nexxus.dao.repository.NxDesignRepository;
import com.att.sales.nexxus.dao.repository.NxLineItemProcessingDao;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.dao.repository.NxMpConfigMappingRepository;
import com.att.sales.nexxus.dao.repository.NxMpDesignDocumentRepository;
import com.att.sales.nexxus.dao.repository.NxMpPriceDetailsRepository;
import com.att.sales.nexxus.myprice.transaction.dao.service.NxMpRepositoryService;
import com.att.sales.nexxus.myprice.transaction.model.ConfigRespProcessingBean;
import com.att.sales.nexxus.myprice.transaction.service.ConfigAndUpdatePricingUtil;
import com.att.sales.nexxus.myprice.transaction.service.RestCommonUtil;
import com.att.sales.nexxus.reteriveicb.model.ComponentDetails;
import com.att.sales.nexxus.reteriveicb.model.PriceAttributes;
import com.att.sales.nexxus.service.NxMyPriceRepositoryServce;
import com.att.sales.nexxus.util.JacksonUtil;
import com.att.sales.nexxus.util.JsonPathUtil;
import com.att.sales.nexxus.util.NexxusJsonUtility;
import com.att.sales.nexxus.ws.utility.ConfigureSolnAndProductWSClientUtility;
import com.att.sales.nexxus.ws.utility.SoapWSHandler;
import com.att.sales.nexxus.ws.utility.WSProcessingService;
import com.oracle.xmlns.cpqcloud.configuration.wireline.solution.solution.Configure;
import com.oracle.xmlns.cpqcloud.configuration.wireline.solution.solution.ConfigureResponse;
import com.oracle.xmlns.cpqcloud.configuration.wireline.solution.solution.ObjectFactory;

@ExtendWith(MockitoExtension.class)
public class ConfigureDesignWSHandlerTest {
	
	@Spy
	@InjectMocks
	private ConfigureDesignWSHandler test;
	
	@Mock
	private SoapWSHandler configureWSClientUtility;

	@Mock
	private WSProcessingService wsProcessingService;
	
	@Mock
	private NxMpDesignDocumentRepository nxMpDesignDocumentRepo;
	
	@Mock
	private NxMpConfigMappingRepository nxMpConfigMappingRepository;
	
	@Mock
	private NexxusJsonUtility nexxusJsonUtility;
	
	/** The line item processing dao. */
	@Mock
	private NxLineItemProcessingDao lineItemProcessingDao;
	
	@Mock
	private JsonPathUtil jsonPathUtil;
	
	@Mock
	private NxDesignAuditRepository nxDesignAuditRepository;
	
	@Mock
	private NxLookupDataRepository nxLookupDataRepository;
	
	@Mock
	private ConfigAndUpdatePricingUtil configAndUpdatePricingUtil;
	
	@Mock
	private NxDesignRepository nxDesignRepository;
	
	@Mock
	private NxMyPriceRepositoryServce nxMyPriceRepositoryServce;
	
	@Mock
	private ConfigureSolnAndProductWSHandler configureSolnAndProductWSHandler;
	
	@Mock
	private NxMpPriceDetailsRepository nxMpPriceDetailsRepository;
	
	@Mock
	Configure configureRequest;
	
	@Mock
	private RestCommonUtil restCommonUtil;
	
	@Mock
	NxMpRepositoryService nxMpRepositoryService;
	
	Map<String, Object> requestMap=new HashMap<>();
	@BeforeEach
	public void initializeServiceMetaData() {
		Map<String, Object> requestParams = new HashMap<>();
		requestParams.put(ServiceMetaData.OFFER, "AVPN");
		requestParams.put(ServiceMetaData.REST_REQUEST_START_TIME, System.currentTimeMillis());
		requestParams.put(ServiceMetaData.VERSION, "1.0");
		requestParams.put(ServiceMetaData.METHOD, "TestMethod");
		requestParams.put(ServiceMetaData.URI, "Testuri");
		
		
		ServiceMetaData.add(requestParams);
		
		NxDesign nxDesign=new NxDesign();
		nxDesign.setAsrItemId("123");
		nxDesign.setBundleCd("ASE");
		nxDesign.setNxDesignId(11l);
		nxDesign.setNxSolutionDetail(getSolutionDtlsLstForASE().get(0));
		List<NxDesignDetails> nxDesignDetails = new ArrayList<>();
		NxDesignDetails nd=new NxDesignDetails();
		nd.setNxDesignId(11l);
		nd.setDesignData(getSite().toJSONString());
		nxDesignDetails.add(nd);
		nxDesign.setNxDesignDetails(nxDesignDetails);
		requestMap.put(MyPriceConstants.NX_DESIGN, nxDesign);
		requestMap.put(MyPriceConstants.NX_DESIGN_ID, 11l);
		requestMap.put(MyPriceConstants.OFFER_NAME, "ASE");
		requestMap.put(MyPriceConstants.MP_TRANSACTION_ID, 12l);
		requestMap.put(StringConstants.PRICE_SCENARIO_ID, "1");
		requestMap.put(MyPriceConstants.MP_TRANSACTION_ID, "123");
		
		
	}
	
	private JSONObject getSite() {
		
		JSONObject site=JacksonUtil.toJsonObject("{\"siteId\":9032336,\"npanxx\":1254,\"address1\":\"fdsa\",\"city\":\"qwr\",\"state\":\"ewrqw\","
				+ "\"saLecName\":null,\"speedId\":null,\"saLecSwClli\":null,\"popClli\":null,\"country\":\"wqrewq\",\"macdType\":"
				+ "\"take\",\"macdActivity\":null,\"fromInventory\":null,\"dualSiteId\":null,\"onNetCheck\":null,\"ethernetVendor\":null,"
				+ "\"designSiteOfferPort\":[{\"designStatus\":\"D\",\"typeOfInventory\":null,\"milesResult\":null,\"securityDesignDetails\":"
				+ "null,\"macdActivityType\":null,\"component\":[{\"componentCodeId\":10,\"fromInvYN\":null,\"logicalChannelPvcID\":null,"
				+ "\"eteVpnKey\":null,\"diversityGroupId\":null,\"componentCodeType\":\"connection\",\"componentId\":9195162,\"externalField\""
				+ ":null,\"parentComponentId\":null,\"externalKeyRef\":null,\"mvlInd\":null,\"userEnteredVpn\":null,\"siteId\":null,"
				+ "\"logicalChannelId\":null,\"designDetails\":null,\"routeTargets\":null,\"references\":null},{\"componentCodeId\":50,"
				+ "\"fromInvYN\":null,\"logicalChannelPvcID\":null,\"eteVpnKey\":null,\"diversityGroupId\":null,\"componentCodeType\":"
				+ "\"Cos\",\"componentId\":null,\"externalField\":null,\"parentComponentId\":null,\"externalKeyRef\":null,\"mvlInd\":null,"
				+ "\"userEnteredVpn\":null,\"siteId\":null,\"logicalChannelId\":null,\"designDetails\":[{\"udfAttributeId\":[1024724],"
				+ "\"udfId\":20014,\"readOnly\":null,\"udfAttributeText\":null,\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,"
				+ "\"udfValue\":null},{\"udfAttributeId\":[53644],\"udfId\":20210,\"readOnly\":null,\"udfAttributeText\":null,"
				+ "\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[30200],"
				+ "\"udfId\":20013,\"readOnly\":null,\"udfAttributeText\":null,\"defaultUdfAttributeId\":null,\"lovAttributeIdList"
				+ "\":null,\"udfValue\":null},{\"udfAttributeId\":[30263],\"udfId\":20226,\"readOnly\":null,\"udfAttributeText"
				+ "\":null,\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[55065],"
				+ "\"udfId\":1000048,\"readOnly\":null,\"udfAttributeText\":null,\"defaultUdfAttributeId\":null,\"lovAttributeIdList"
				+ "\":null,\"udfValue\":null}],\"routeTargets\":null,\"references\":null},{\"componentCodeId\":30,\"fromInvYN\":null,"
				+ "\"logicalChannelPvcID\":null,\"eteVpnKey\":null,\"diversityGroupId\":null,\"componentCodeType\":\"Port\","
				+ "\"componentId\":null,\"externalField\":null,\"parentComponentId\":null,\"externalKeyRef\":null,\"mvlInd\":null,"
				+ "\"userEnteredVpn\":null,\"siteId\":null,\"logicalChannelId\":null,\"designDetails\":[{\"udfAttributeId\":null,"
				+ "\"udfId\":200046,\"readOnly\":null,\"udfAttributeText\":[\"8790\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList"
				+ "\":null,\"udfValue\":null},{\"udfAttributeId\":null,\"udfId\":200057,\"readOnly\":null,\"udfAttributeText\":[\"VTVT\"],"
				+ "\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null}],\"routeTargets\":null,\"references\":null}],"
				+ "\"routerDetails\":null,\"logicalChannelDetail\":null,\"customDesign\":null,\"accessPricingAQ\":null,\"aggregateBilling\":null,"
				+ "\"portValidationMessage\":null,\"voiceFeatureDetail\":null}],\"priceDetails\":{\"componentDetails\":[{\"componentCodeId\":1324,"
				+ "\"componentCodeType\":\"dfas\",\"componentId\":2131,\"componentType\":null,\"componentParentId\":123,\"componentAttributes\":"
				+ "[{\"componentFieldName\":\"we\",\"componentFieldValue\":\"wer\"}],\"priceAttributes\":[{\"productRateId\":123156,\"beid\":"
				+ "\"awf\",\"rateDescription\":\"adwf\",\"priceCatalog\":\"awfa\",\"localListPrice\":21.0,\"targetListPrice\":23.0,\"priceType\":"
				+ "\"awf\",\"priceUnit\":\"wadf\",\"frequency\":\"asdf\",\"monthlySurcharge\":23,\"discount\":86.0,\"discountId\":\"dfa\",\"quantity"
				+ "\":34.0,\"localNetPrice\":4345.0,\"targetNetPrice\":54.0,\"localTotalPrice\":564.0,\"targetTotalPrice\":464.0,\"localCurrency\":"
				+ "\"ewaf\",\"targetCurrency\":\"dsaf\",\"rdsPriceType\":\"adfa\",\"priceName\":\"sfr\",\"typeOfInventory\":\"fwadf\",\"priceInUSD\":"
				+ "\"sad\",\"priceScenarioId\":8745,\"rategroup\":\"adsfad\",\"externalBillingSystem\":\"adwf\",\"ratePlanId\":4556,\"priceCompType\":"
				+ "\"fdsaf\",\"pvcId\":\"adfs\",\"chargeCodeId\":\"das\"}],\"scpPriceMessages\":null}],\"priceMessage\":null},\"address\":null,\"postalCode"
				+ "\":null,\"siteName\":null,\"swcClli\":\"retw\",\"customerLocationClli\":\"wafraw\",\"active\":null,\"emc\":\"ewqtre\",\"carrierHotel\":"
				+ "\"wetre\",\"attComments\":\"gffd\",\"newBuilding\":\"awerewq\",\"customerReference\":\"ryt\",\"asrItemId\":\"54634\",\"lataCode\":"
				+ "\"36543\",\"zipCode\":\"qwrqw\",\"address2\":\"werqw\",\"room\":\"qwerwq\",\"floor\":\"qwfq\",\"building\":\"qwfe\",\"siteComment\":"
				+ "\"ery\",\"lconFirstName\":null,\"lconLastName\":null,\"lconPhone\":null,\"lconEmail\":null,\"endPointSiteIdentifier\":\"wewqw\","
				+ "\"buildingClli\":\"rtr\",\"regionCode\":\"retW\",\"activityType\":\"reyttry\",\"cancellationReason\":\"ert\",\"product\":null,"
				+ "\"quantity\":null,\"nssEngagement\":\"dsaf\",\"designStatus\":null,\"multiGigeIndicator\":\"qwewq\",\"alias\":\"ewr\",\"macdActionType"
				+ "\":null,\"lconDetails\":[{\"lconType\":null,\"lconFirstName\":\"er\",\"lconLastName\":\"ewtr\",\"lconPhone\":\"etw\","
				+ "\"lconEmail\":\"wetr\"}],\"ethernetVendoe\":null}");
		
		return site;
	}
	
	protected JSONObject getDesignForASE() {
		return JacksonUtil.toJsonObject("{\"solution\":{\"solutionDeterminants\":{\"icbInd\":null,\"solutionType\":\"NS\",\"mvlInd\":null},\"bulkInd\":null,\"offers\":"
				+ "[{\"bundleCode\":\"ASE\",\"site\":[{\"country\":\"wqrewq\",\"zipCode\":\"qwrqw\",\"postalCode\":null,\"speedId\":null,\"lconLastName\":null,"
				+ "\"lataCode\":\"36543\",\"siteComment\":\"ery\",\"building\":\"qwfe\",\"asrItemId\":\"54634\",\"fromInventory\":null,\"buildingClli\":\"rtr\","
				+ "\"endPointSiteIdentifier\":\"wewqw\",\"multiGigeIndicator\":\"qwewq\",\"customerReference\":\"ryt\",\"state\":\"ewrqw\",\"npanxx\":1254,"
				+ "\"saLecSwClli\":null,\"active\":null,\"lconDetails\":[{\"lconType\":null,\"lconLastName\":\"ewtr\",\"lconFirstName\":\"er\",\"lconPhone\":"
				+ "\"etw\",\"lconEmail\":\"wetr\"}],\"macdActionType\":null,\"macdActivity\":null,\"priceDetails\":{\"componentDetails\":[{\"componentType\":null,"
				+ "\"componentId\":2131,\"componentAttributes\":[{\"componentFieldName\":\"we\",\"componentFieldValue\":\"wer\"}],\"componentCodeId\":1324,"
				+ "\"componentCodeType\":\"dfas\",\"componentParentId\":123,\"priceAttributes\":[{\"priceScenarioId\":8745,\"priceCatalog\":\"awfa\",\"localNetPrice"
				+ "\":4345.0,\"rategroup\":\"adsfad\",\"ratePlanId\":4556,\"priceInUSD\":\"sad\",\"monthlySurcharge\":23,\"discount\":86.0,\"targetListPrice\":23.0,"
				+ "\"localCurrency\":\"ewaf\",\"chargeCodeId\":\"das\",\"frequency\":\"asdf\",\"rdsPriceType\":\"adfa\",\"localTotalPrice\":564.0,\"productRateId\":123156,"
				+ "\"targetNetPrice\":54.0,\"priceUnit\":\"wadf\",\"quantity\":34.0,\"targetCurrency\":\"dsaf\",\"priceCompType\":\"fdsaf\",\"priceType\":\"awf\","
				+ "\"typeOfInventory\":\"fwadf\",\"rateDescription\":\"adwf\",\"externalBillingSystem\":\"adwf\",\"beid\":\"awf\",\"targetTotalPrice\":464.0,"
				+ "\"pvcId\":\"adfs\",\"discountId\":\"dfa\",\"priceName\":\"sfr\",\"localListPrice\":21.0}],\"scpPriceMessages\":null}],\"priceMessage\":null},"
				+ "\"designStatus\":null,\"emc\":\"ewqtre\",\"customerLocationClli\":\"wafraw\",\"city\":\"qwr\",\"designSiteOfferPort\":[{\"aggregateBilling\":null,"
				+ "\"typeOfInventory\":null,\"voiceFeatureDetail\":null,\"macdActivityType\":null,\"milesResult\":null,\"securityDesignDetails\":null,\"component\":[{"
				+ "\"diversityGroupId\":null,\"componentId\":9195162,\"references\":null,\"logicalChannelPvcID\":null,\"externalKeyRef\":null,\"fromInvYN\":null,"
				+ "\"mvlInd\":null,\"routeTargets\":null,\"externalField\":null,\"parentComponentId\":null,\"userEnteredVpn\":null,\"componentCodeId\":10,"
				+ "\"componentCodeType\":\"connection\",\"siteId\":null,\"eteVpnKey\":null,\"logicalChannelId\":null,\"designDetails\":null},{\"diversityGroupId\":null,"
				+ "\"componentId\":null,\"references\":null,\"logicalChannelPvcID\":null,\"externalKeyRef\":null,\"fromInvYN\":null,\"mvlInd\":null,\"routeTargets\":null,"
				+ "\"externalField\":null,\"parentComponentId\":null,\"userEnteredVpn\":null,\"componentCodeId\":50,\"componentCodeType\":\"Cos\",\"siteId\":null,"
				+ "\"eteVpnKey\":null,\"logicalChannelId\":null,\"designDetails\":[{\"udfAttributeId\":[1024724],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,"
				+ "\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":null,\"udfId\":20014},{\"udfAttributeId\":[53644],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,"
				+ "\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":null,\"udfId\":20210},{\"udfAttributeId\":[30200],\"defaultUdfAttributeId\":null,"
				+ "\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":null,\"udfId\":20013},{\"udfAttributeId\":[30263],"
				+ "\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":null,\"udfId\":20226},{"
				+ "\"udfAttributeId\":[55065],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText"
				+ "\":null,\"udfId\":1000048}]},{\"diversityGroupId\":null,\"componentId\":null,\"references\":null,\"logicalChannelPvcID\":null,\"externalKeyRef"
				+ "\":null,\"fromInvYN\":null,\"mvlInd\":null,\"routeTargets\":null,\"externalField\":null,\"parentComponentId\":null,\"userEnteredVpn\":null,"
				+ "\"componentCodeId\":30,\"componentCodeType\":\"Port\",\"siteId\":null,\"eteVpnKey\":null,\"logicalChannelId\":null,\"designDetails\":[{"
				+ "\"udfAttributeId\":null,\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":["
				+ "\"8790\"],\"udfId\":200046},{\"udfAttributeId\":null,\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,"
				+ "\"udfAttributeText\":[\"VTVT\"],\"udfId\":200057}]}],\"portValidationMessage\":null,\"accessPricingAQ\":null,\"designStatus\":\"D\","
				+ "\"logicalChannelDetail\":null,\"routerDetails\":null,\"customDesign\":null}],\"siteName\":null,\"nssEngagement\":\"dsaf\",\"regionCode\":"
				+ "\"retW\",\"dualSiteId\":null,\"swcClli\":\"retw\",\"attComments\":\"gffd\",\"newBuilding\":\"awerewq\",\"alias\":\"ewr\",\"floor\":\"qwfq\","
				+ "\"saLecName\":null,\"lconEmail\":null,\"product\":null,\"address\":null,\"quantity\":null,\"address2\":\"werqw\",\"cancellationReason\":"
				+ "\"ert\",\"address1\":\"fdsa\",\"onNetCheck\":null,\"lconFirstName\":null,\"lconPhone\":null,\"room\":\"qwerwq\",\"carrierHotel\":\"wetre\","
				+ "\"ethernetVendor\":null,\"siteId\":9032336,\"popClli\":null,\"macdType\":\"take\",\"activityType\":\"reyttry\",\"ethernetVendoe\":null}],"
				+ "\"offerId\":\"103\"}],\"leadDesignID\":null,\"marketStrata\":\"BNS\",\"cancellationReason\":null,\"pricerDSolutionId\":null,\"automationInd\":"
				+ "\"N\",\"erateInd\":null,\"layer\":\"Wholesale\",\"solutionStatus\":\"N\"},\"actionDeterminants\":[{\"component"
				+ "\":[\"Design\",\"Price\",\"ASE\"],\"activity\":\"UpdateDesign\"}]}");
	}
	
private JSONObject getCircuit() {
		
		JSONObject circuit=JacksonUtil.toJsonObject("{\"component\":[{\"diversityGroupId\":null,\"componentId\":null,\"references\":null,"
				+ "\"logicalChannelPvcID\":null,\"externalKeyRef\":null,\"fromInvYN\":null,\"mvlInd\":null,"
				+ "\"routeTargets\":null,\"externalField\":null,\"parentComponentId\":null,\"userEnteredVpn\":null,"
				+ "\"componentCodeId\":30,\"componentCodeType\":\"Port\",\"siteId\":null,\"eteVpnKey\":null,\"logicalChannelId"
				+ "\":null,\"designDetails\":[{\"udfAttributeId\":null,\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,"
				+ "\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"2\"],\"udfId\":200046},{\"udfAttributeId\":null,"
				+ "\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText"
				+ "\":[\"yy\"],\"udfId\":200057},{\"udfAttributeId\":null,\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,"
				+ "\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"1\"],\"udfId\":200047}]},{\"diversityGroupId\":null,"
				+ "\"componentId\":10175,\"references\":null,\"logicalChannelPvcID\":null,\"externalKeyRef\":null,\"fromInvYN\":null,"
				+ "\"mvlInd\":null,\"routeTargets\":null,\"externalField\":null,\"parentComponentId\":null,\"userEnteredVpn\":null,"
				+ "\"componentCodeId\":1210,\"componentCodeType\":\"Circuit\",\"siteId\":null,\"eteVpnKey\":null,\"logicalChannelId\":null,"
				+ "\"designDetails\":[{\"udfAttributeId\":[],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly"
				+ "\":null,\"udfValue\":null,\"udfAttributeText\":[\"FL\"],\"udfId\":200118},{\"udfAttributeId\":[301788],\"defaultUdfAttributeId"
				+ "\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[],\"udfId\":200174},{"
				+ "\"udfAttributeId\":[301770],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue"
				+ "\":null,\"udfAttributeText\":[],\"udfId\":200163},{\"udfAttributeId\":[301773],\"defaultUdfAttributeId\":null,"
				+ "\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[],\"udfId\":200164},{"
				+ "\"udfAttributeId\":[30605],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue"
				+ "\":null,\"udfAttributeText\":[],\"udfId\":200167},{\"udfAttributeId\":[30604],\"defaultUdfAttributeId\":null,"
				+ "\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[],\"udfId\":200168},{"
				+ "\"udfAttributeId\":[301784],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,"
				+ "\"udfValue\":null,\"udfAttributeText\":[],\"udfId\":200169},{\"udfAttributeId\":[30604],\"defaultUdfAttributeId"
				+ "\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[],\"udfId\":200170},{"
				+ "\"udfAttributeId\":[301820],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,"
				+ "\"udfAttributeText\":[],\"udfId\":200176},{\"udfAttributeId\":[301845],\"defaultUdfAttributeId\":null,\"lovAttributeIdList"
				+ "\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[],\"udfId\":200216},{\"udfAttributeId\":[30604],"
				+ "\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText"
				+ "\":[],\"udfId\":200184},{\"udfAttributeId\":[30604],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,"
				+ "\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[],\"udfId\":200185},{\"udfAttributeId\":[301890],"
				+ "\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[],"
				+ "\"udfId\":200217},{\"udfAttributeId\":[],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,"
				+ "\"udfValue\":null,\"udfAttributeText\":[\"Click to Select\"],\"udfId\":200210},{\"udfAttributeId\":[],\"defaultUdfAttributeId"
				+ "\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"AD78789\"],\"udfId\":200162},{"
				+ "\"udfAttributeId\":[302215],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,"
				+ "\"udfAttributeText\":[\"R\"],\"udfId\":200193},{\"udfAttributeId\":[],\"defaultUdfAttributeId\":null,\"lovAttributeIdList"
				+ "\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"8759\"],\"udfId\":200192},{\"udfAttributeId\":[],"
				+ "\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText"
				+ "\":[\"8432\"],\"udfId\":200158},{\"udfAttributeId\":[30604],\"defaultUdfAttributeId\":null,\"lovAttributeIdList"
				+ "\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[],\"udfId\":200183},{\"udfAttributeId\":[],"
				+ "\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText"
				+ "\":[\"0\"],\"udfId\":20781}]},{\"diversityGroupId\":null,\"componentId\":13052,\"references\":[{\"referenceType\":"
				+ "\"Site\",\"referenceId\":5678}],\"logicalChannelPvcID\":null,\"externalKeyRef\":null,\"fromInvYN\":null,\"mvlInd"
				+ "\":null,\"routeTargets\":null,\"externalField\":null,\"parentComponentId\":10175,\"userEnteredVpn\":null,\"componentCodeId"
				+ "\":1220,\"componentCodeType\":\"Endpoint\",\"siteId\":null,\"eteVpnKey\":null,\"logicalChannelId\":null,\"designDetails"
				+ "\":[{\"udfAttributeId\":[301829],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue"
				+ "\":null,\"udfAttributeText\":[],\"udfId\":200179},{\"udfAttributeId\":[],\"defaultUdfAttributeId\":null,\"lovAttributeIdList"
				+ "\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"A\"],\"udfId\":21033},{\"udfAttributeId\":[301848],"
				+ "\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[],"
				+ "\"udfId\":200205},{\"udfAttributeId\":[30604],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly"
				+ "\":null,\"udfValue\":null,\"udfAttributeText\":[],\"udfId\":200212},{\"udfAttributeId\":[30604],\"defaultUdfAttributeId"
				+ "\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[],\"udfId\":200213},{"
				+ "\"udfAttributeId\":null,\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,"
				+ "\"udfAttributeText\":[\"UU\"],\"udfId\":200218},{\"udfAttributeId\":[301845],\"defaultUdfAttributeId\":null,\"lovAttributeIdList"
				+ "\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[],\"udfId\":200216},{\"udfAttributeId\":[],\"defaultUdfAttributeId"
				+ "\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"GREEN\"],\"udfId\":21036},{"
				+ "\"udfAttributeId\":[],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,"
				+ "\"udfAttributeText\":[\"8759\"],\"udfId\":200192},{\"udfAttributeId\":[301820],\"defaultUdfAttributeId\":null,\"lovAttributeIdList"
				+ "\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[],\"udfId\":200176},{\"udfAttributeId\":[301784],"
				+ "\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[],"
				+ "\"udfId\":200169},{\"udfAttributeId\":[],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,"
				+ "\"udfValue\":null,\"udfAttributeText\":[\"ORLDFLMA\"],\"udfId\":200045},{\"udfAttributeId\":null,\"defaultUdfAttributeId\":null,"
				+ "\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"ty\"],\"udfId\":20184},{\"udfAttributeId"
				+ "\":null,\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":["
				+ "\"UY\"],\"udfId\":21136}]},{\"diversityGroupId\":null,\"componentId\":13053,\"references\":[{\"referenceType\":\"Site\","
				+ "\"referenceId\":1234}],\"logicalChannelPvcID\":null,\"externalKeyRef\":null,\"fromInvYN\":null,\"mvlInd\":null,\"routeTargets"
				+ "\":null,\"externalField\":null,\"parentComponentId\":10175,\"userEnteredVpn\":null,\"componentCodeId\":1220,\"componentCodeType"
				+ "\":\"Endpoint\",\"siteId\":null,\"eteVpnKey\":null,\"logicalChannelId\":null,\"designDetails\":[{\"udfAttributeId\":[],"
				+ "\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":["
				+ "\"YELLOW\"],\"udfId\":21037},{\"udfAttributeId\":[],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly"
				+ "\":null,\"udfValue\":null,\"udfAttributeText\":[\"Z\"],\"udfId\":21034},{\"udfAttributeId\":[],\"defaultUdfAttributeId"
				+ "\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"8759\"],\"udfId\":200192},{"
				+ "\"udfAttributeId\":[301820],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,"
				+ "\"udfAttributeText\":[],\"udfId\":200176},{\"udfAttributeId\":[301784],\"defaultUdfAttributeId\":null,\"lovAttributeIdList"
				+ "\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[],\"udfId\":200169},{\"udfAttributeId\":[],\"defaultUdfAttributeId"
				+ "\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"KSSMFLXA\"],\"udfId\":200045},{"
				+ "\"udfAttributeId\":null,\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,"
				+ "\"udfAttributeText\":[\"IOU\"],\"udfId\":20184},{\"udfAttributeId\":null,\"defaultUdfAttributeId\":null,\"lovAttributeIdList"
				+ "\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"OUIP\"],\"udfId\":21136}]}],\"site\":[{\"country\":"
				+ "\"wqrewq\",\"zipCode\":\"qwrqw\",\"postalCode\":null,\"speedId\":null,\"lconLastName\":null,\"lataCode\":\"36543\","
				+ "\"siteComment\":\"ery\",\"building\":\"qwfe\",\"asrItemId\":\"54634\",\"fromInventory\":null,\"buildingClli\":\"rtr\","
				+ "\"endPointSiteIdentifier\":\"wewqw\",\"multiGigeIndicator\":\"qwewq\",\"customerReference\":\"ryt\",\"state\":\"ewrqw\",\"npanxx"
				+ "\":1254,\"saLecSwClli\":null,\"active\":null,\"lconDetails\":[{\"lconType\":null,\"lconLastName\":\"ewtr\",\"lconFirstName\":\"er\","
				+ "\"lconPhone\":\"etw\",\"lconEmail\":\"wetr\"}],\"macdActionType\":null,\"macdActivity\":null,\"priceDetails\":{\"componentDetails\":[{"
				+ "\"componentType\":null,\"componentId\":2131,\"componentAttributes\":[{\"componentFieldName\":\"we\",\"componentFieldValue\":\"wer\"}],"
				+ "\"componentCodeId\":1324,\"componentCodeType\":\"dfas\",\"componentParentId\":123,\"priceAttributes\":[{\"priceScenarioId\":8745,"
				+ "\"priceCatalog\":\"awfa\",\"localNetPrice\":4345.0,\"rategroup\":\"adsfad\",\"ratePlanId\":4556,\"priceInUSD\":\"sad\",\"monthlySurcharge"
				+ "\":23,\"discount\":86.0,\"targetListPrice\":23.0,\"localCurrency\":\"ewaf\",\"chargeCodeId\":\"das\",\"frequency\":\"asdf\",\"rdsPriceType"
				+ "\":\"adfa\",\"localTotalPrice\":564.0,\"productRateId\":123156,\"targetNetPrice\":54.0,\"priceUnit\":\"wadf\",\"quantity\":34.0,"
				+ "\"targetCurrency\":\"dsaf\",\"priceCompType\":\"fdsaf\",\"priceType\":\"awf\",\"typeOfInventory\":\"fwadf\",\"rateDescription\":\"adwf\","
				+ "\"externalBillingSystem\":\"adwf\",\"beid\":\"awf\",\"targetTotalPrice\":464.0,\"pvcId\":\"adfs\",\"discountId\":\"dfa\",\"priceName\":"
				+ "\"sfr\",\"localListPrice\":21.0}],\"scpPriceMessages\":null}],\"priceMessage\":null},\"designStatus\":null,\"emc\":\"ewqtre\","
				+ "\"customerLocationClli\":\"IOU\",\"city\":\"qwr\",\"designSiteOfferPort\":[{\"aggregateBilling\":null,\"typeOfInventory\":null,"
				+ "\"voiceFeatureDetail\":null,\"macdActivityType\":null,\"milesResult\":null,\"securityDesignDetails\":null,\"component\":[{"
				+ "\"diversityGroupId\":null,\"componentId\":9195162,\"references\":null,\"logicalChannelPvcID\":null,\"externalKeyRef\":null,"
				+ "\"fromInvYN\":null,\"mvlInd\":null,\"routeTargets\":null,\"externalField\":null,\"parentComponentId\":null,\"userEnteredVpn\":null,"
				+ "\"componentCodeId\":10,\"componentCodeType\":\"connection\",\"siteId\":null,\"eteVpnKey\":null,\"logicalChannelId\":null,"
				+ "\"designDetails\":null},{\"diversityGroupId\":null,\"componentId\":null,\"references\":null,\"logicalChannelPvcID\":null,"
				+ "\"externalKeyRef\":null,\"fromInvYN\":null,\"mvlInd\":null,\"routeTargets\":null,\"externalField\":null,\"parentComponentId\":null,"
				+ "\"userEnteredVpn\":null,\"componentCodeId\":50,\"componentCodeType\":\"Cos\",\"siteId\":null,\"eteVpnKey\":null,\"logicalChannelId"
				+ "\":null,\"designDetails\":[{\"udfAttributeId\":[1024724],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,"
				+ "\"udfValue\":null,\"udfAttributeText\":null,\"udfId\":20014},{\"udfAttributeId\":[53644],\"defaultUdfAttributeId\":null,"
				+ "\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":null,\"udfId\":20210},{\"udfAttributeId\":[30200],"
				+ "\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":null,\"udfId\":20013},{"
				+ "\"udfAttributeId\":[30263],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":null,"
				+ "\"udfId\":20226},{\"udfAttributeId\":[55065],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,"
				+ "\"udfAttributeText\":null,\"udfId\":1000048}]},{\"diversityGroupId\":null,\"componentId\":null,\"references\":null,\"logicalChannelPvcID\":null,"
				+ "\"externalKeyRef\":null,\"fromInvYN\":null,\"mvlInd\":null,\"routeTargets\":null,\"externalField\":null,\"parentComponentId\":null,\"userEnteredVpn\":null,"
				+ "\"componentCodeId\":30,\"componentCodeType\":\"Port\",\"siteId\":null,\"eteVpnKey\":null,\"logicalChannelId\":null,\"designDetails\":[{\"udfAttributeId\":null,"
				+ "\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"908\"],\"udfId\":200046},{"
				+ "\"udfAttributeId\":null,\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"IIIII\"],"
				+ "\"udfId\":200057}]}],\"portValidationMessage\":null,\"accessPricingAQ\":null,\"designStatus\":\"D\",\"logicalChannelDetail\":null,"
				+ "\"routerDetails\":null,\"customDesign\":null}],\"siteName\":null,\"nssEngagement\":\"dsaf\",\"regionCode\":\"retW\",\"dualSiteId\":null,\"swcClli\":"
				+ "\"retw\",\"attComments\":\"gffd\",\"newBuilding\":\"awerewq\",\"alias\":\"ewr\",\"floor\":\"qwfq\",\"saLecName\":null,\"lconEmail\":null,\"product\":null,"
				+ "\"address\":null,\"quantity\":null,\"address2\":\"werqw\",\"cancellationReason\":\"ert\",\"address1\":\"fdsa\",\"onNetCheck\":null,\"lconFirstName\":null,"
				+ "\"lconPhone\":null,\"room\":\"qwerwq\",\"carrierHotel\":\"wetre\",\"ethernetVendor\":null,\"siteId\":1234,\"popClli\":null,\"macdType\":\"take\","
				+ "\"activityType\":\"reyttry\",\"ethernetVendoe\":null},{\"country\":\"wqrewq\",\"zipCode\":\"qwrqw\",\"postalCode\":null,\"speedId\":null,"
				+ "\"lconLastName\":null,\"lataCode\":\"36543\",\"siteComment\":\"ery\",\"building\":\"qwfe\",\"asrItemId\":\"54634\",\"fromInventory\":null,"
				+ "\"buildingClli\":\"rtr\",\"endPointSiteIdentifier\":\"wewqw\",\"multiGigeIndicator\":\"qwewq\",\"customerReference\":\"ryt\",\"state\":"
				+ "\"ewrqw\",\"npanxx\":1254,\"saLecSwClli\":null,\"active\":null,\"lconDetails\":[{\"lconType\":null,\"lconLastName\":\"ewtr\",\"lconFirstName\":"
				+ "\"er\",\"lconPhone\":\"etw\",\"lconEmail\":\"wetr\"}],\"macdActionType\":null,\"macdActivity\":null,\"priceDetails\":{\"componentDetails\":[{\"componentType\":null,"
				+ "\"componentId\":2131,\"componentAttributes\":[{\"componentFieldName\":\"we\",\"componentFieldValue\":\"wer\"}],\"componentCodeId\":1324,"
				+ "\"componentCodeType\":\"dfas\",\"componentParentId\":123,\"priceAttributes\":[{\"priceScenarioId\":8745,\"priceCatalog\":\"awfa\",\"localNetPrice"
				+ "\":4345.0,\"rategroup\":\"adsfad\",\"ratePlanId\":4556,\"priceInUSD\":\"sad\",\"monthlySurcharge\":23,\"discount\":86.0,\"targetListPrice\":23.0,"
				+ "\"localCurrency\":\"ewaf\",\"chargeCodeId\":\"das\",\"frequency\":\"asdf\",\"rdsPriceType\":\"adfa\",\"localTotalPrice\":564.0,\"productRateId"
				+ "\":123156,\"targetNetPrice\":54.0,\"priceUnit\":\"wadf\",\"quantity\":34.0,\"targetCurrency\":\"dsaf\",\"priceCompType\":\"fdsaf\",\"priceType"
				+ "\":\"awf\",\"typeOfInventory\":\"fwadf\",\"rateDescription\":\"adwf\",\"externalBillingSystem\":\"adwf\",\"beid\":\"awf\",\"targetTotalPrice"
				+ "\":464.0,\"pvcId\":\"adfs\",\"discountId\":\"dfa\",\"priceName\":\"sfr\",\"localListPrice\":21.0}],\"scpPriceMessages\":null}],\"priceMessage"
				+ "\":null},\"designStatus\":null,\"emc\":\"ewqtre\",\"customerLocationClli\":\"te\",\"city\":\"qwr\",\"designSiteOfferPort\":[{\"aggregateBilling"
				+ "\":null,\"typeOfInventory\":null,\"voiceFeatureDetail\":null,\"macdActivityType\":null,\"milesResult\":null,\"securityDesignDetails\":null,"
				+ "\"component\":[{\"diversityGroupId\":null,\"componentId\":9195162,\"references\":null,\"logicalChannelPvcID\":null,\"externalKeyRef\":null,"
				+ "\"fromInvYN\":null,\"mvlInd\":null,\"routeTargets\":null,\"externalField\":null,\"parentComponentId\":null,\"userEnteredVpn\":null,"
				+ "\"componentCodeId\":10,\"componentCodeType\":\"connection\",\"siteId\":null,\"eteVpnKey\":null,\"logicalChannelId\":null,\"designDetails"
				+ "\":null},{\"diversityGroupId\":null,\"componentId\":null,\"references\":null,\"logicalChannelPvcID\":null,\"externalKeyRef\":null,\"fromInvYN"
				+ "\":null,\"mvlInd\":null,\"routeTargets\":null,\"externalField\":null,\"parentComponentId\":null,\"userEnteredVpn\":null,\"componentCodeId\":50,"
				+ "\"componentCodeType\":\"Cos\",\"siteId\":null,\"eteVpnKey\":null,\"logicalChannelId\":null,\"designDetails\":[{\"udfAttributeId\":[1024724],"
				+ "\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":null,\"udfId\":20014},{"
				+ "\"udfAttributeId\":[53644],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText"
				+ "\":null,\"udfId\":20210},{\"udfAttributeId\":[30200],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue"
				+ "\":null,\"udfAttributeText\":null,\"udfId\":20013},{\"udfAttributeId\":[30263],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,"
				+ "\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":null,\"udfId\":20226},{\"udfAttributeId\":[55065],\"defaultUdfAttributeId\":null,"
				+ "\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":null,\"udfId\":1000048}]},{\"diversityGroupId\":null,"
				+ "\"componentId\":null,\"references\":null,\"logicalChannelPvcID\":null,\"externalKeyRef\":null,\"fromInvYN\":null,\"mvlInd\":null,\"routeTargets\":null,"
				+ "\"externalField\":null,\"parentComponentId\":null,\"userEnteredVpn\":null,\"componentCodeId\":30,\"componentCodeType\":\"Port\",\"siteId\":null,\"eteVpnKey\":null,"
				+ "\"logicalChannelId\":null,\"designDetails\":[{\"udfAttributeId\":null,\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,"
				+ "\"udfAttributeText\":[\"908\"],\"udfId\":200046},{\"udfAttributeId\":null,\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,"
				+ "\"udfAttributeText\":[\"IIIII\"],\"udfId\":200057}]}],\"portValidationMessage\":null,\"accessPricingAQ\":null,\"designStatus\":\"D\",\"logicalChannelDetail\":null,"
				+ "\"routerDetails\":null,\"customDesign\":null}],\"siteName\":null,\"nssEngagement\":\"dsaf\",\"regionCode\":\"retW\",\"dualSiteId\":null,\"swcClli\":\"retw\","
				+ "\"attComments\":\"gffd\",\"newBuilding\":\"awerewq\",\"alias\":\"ewr\",\"floor\":\"qwfq\",\"saLecName\":null,\"lconEmail\":null,\"product\":null,\"address\":null,"
				+ "\"quantity\":null,\"address2\":\"werqw\",\"cancellationReason\":\"ert\",\"address1\":\"fdsa\",\"onNetCheck\":null,\"lconFirstName\":null,\"lconPhone\":null,"
				+ "\"room\":\"qwerwq\",\"carrierHotel\":\"wetre\",\"ethernetVendor\":null,\"siteId\":5678,\"popClli\":null,\"macdType\":\"take\","
				+ "\"activityType\":\"reyttry\",\"ethernetVendoe\":null}]}");
		return circuit;
	}
	
	private List<NxSolutionDetail>  getSolutionDtlsLstForASE(){
		List<NxSolutionDetail> nxSolutionDetail=new ArrayList<>();
		NxSolutionDetail nxs=new NxSolutionDetail();
		nxs.setCreatedDate(new Date());
		nxs.setCreatedUser("ABC");
		nxs.setExternalKey(1l);
		nxs.setNxSolutionId(2l);
		List<NxDesign> nxDesign=new ArrayList<>();
		NxDesign nd=new NxDesign();
		nd.setNxDesignId(1l);
		nd.setAsrItemId("54634");
		nd.setNxSolutionDetail(nxs);
		List<NxDesignDetails> nxDesignDetails = new ArrayList<>();
		NxDesignDetails nxd=new NxDesignDetails();
		nxd.setCreatedDate(new Date());
		nxd.setNxDesignId(3l);
		nxd.setNxDesign(nd);
		nxd.setDesignData(getSite().toJSONString());
		nxDesignDetails.add(nxd);
		nd.setNxDesignDetails(nxDesignDetails);
		nxDesign.add(nd);
		nxs.setNxDesign(nxDesign);
		nxSolutionDetail.add(nxs);
		return nxSolutionDetail;
	}
	
	@Test
	public void initiateConfigDesignWebServiceTestASE() throws SalesBusinessException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("myPriceTransId", "42373160");
		map.put("nxTxnId", 21L);
		map.put("offerName", "ASE");
		map.put("nxDesignId", 10L);
		map.put("site", getSite());
		NxDesign nxDesign = new NxDesign();
		NxSolutionDetail soln = new NxSolutionDetail();
		soln.setNxSolutionId(101L);
		nxDesign.setNxSolutionDetail(soln);
		NxDesignDetails nxDesignDetails = new NxDesignDetails();
		String json=getDesignForASE().toJSONString();
		JSONObject jsonObj = JacksonUtil.toJsonObject(json);
		nxDesignDetails.setDesignData(json);
		nxDesign.setNxDesignId(10L);
		nxDesign.setNxDesignDetails(new ArrayList<NxDesignDetails>() {{add(nxDesignDetails);}});
		Mockito.when(nxDesignRepository.findByNxDesignId(10L)).thenReturn(nxDesign);
		map.put(MyPriceConstants.AUTOMATION_IND, true);
		doNothing().when(test).setNexxusProductSubType(jsonObj, "ASE", map);
		NxDesignAudit nxDesignAudit=new NxDesignAudit();
		nxDesignAudit.setData("{\"solution\":{\"solutionDeterminants\":{\"icbInd\":null,\"solutionType\":\"NS\",\"mvlInd\":null},"
				+ "\"bulkInd\":null,\"offers\":[{\"bundleCode\":\"ASE\",\"site\":[],\"offerId\":\"103\"}],\"leadDesignID\":null,"
				+ "\"marketStrata\":\"BNS\",\"cancellationReason\":null,\"pricerDSolutionId\":null,\"automationInd\":\"N\","
				+ "\"erateInd\":null,\"layer\":\"Wholesale\",\"solutionStatus\":\"N\"},\"actionDeterminants"
				+ "\":[{\"component\":[\"Design\",\"Price\",\"ASE\"],\"activity\":\"UpdateDesign\"}]}");
		when(nxDesignAuditRepository.findByNxRefIdAndTransaction(any(),any())).thenReturn(nxDesignAudit);
		List<NxMpConfigMapping> configMapping=new ArrayList<>();
		NxMpConfigMapping nxConfig=new NxMpConfigMapping();
		nxConfig.setDefaultValue("A");
		nxConfig.setType("DEFAULT");
		nxConfig.setNxMappingId(1l);
		configMapping.add(nxConfig);
		when(nxMpConfigMappingRepository.findByOfferAndRuleName(anyString(),anyString())).thenReturn(configMapping);
		Object marketStrataObj="RTC";
		when(nexxusJsonUtility.getValue(any(),anyString())).thenReturn(marketStrataObj);
		NxLookupData nxLookup=new NxLookupData();
		nxLookup.setDescription("abc");
		when(nxLookupDataRepository.findTopByDatasetNameAndItemId(anyString(),anyString())).thenReturn(nxLookup);
		when(jsonPathUtil.set(any(),anyString(),any(), anyBoolean())).thenReturn(json);
		doNothing().when(configureWSClientUtility).setWsName(MyPriceConstants.CONFIG_SOL_PRODUCT_WS);
		Mockito.when(wsProcessingService.initiateWebService(any(Configure.class),
				any(ConfigureSolnAndProductWSClientUtility.class), anyMap(), any(Class.class)))
				.thenReturn(getConfigureResponse());
		doNothing().when(test).prepareRequestBody(map, configureRequest, jsonObj);
		test.initiateConfigDesignWebService(map);
	}
	
	@Test
	public void testSetProductType() {
		JSONObject jsonObj =  JacksonUtil.toJsonObject(getDesignForASE().toJSONString());
		Mockito.when(configAndUpdatePricingUtil.getDataInString(jsonObj, MyPriceConstants.THIRD_PARTY_IND_PATH)).thenReturn("Y");
		test.setProductType(StringConstants.OFFERNAME_ASENOD, jsonObj, new HashMap<String, Object>());
		
		Mockito.when(configAndUpdatePricingUtil.getDataInString(jsonObj, MyPriceConstants.THIRD_PARTY_IND_PATH)).thenReturn("N");
		test.setProductType(StringConstants.OFFERNAME_ASENOD, jsonObj, new HashMap<String, Object>());
	}
	
	@Test
	public void testGetResultCount() {
		JSONObject jsonObj =  JacksonUtil.toJsonObject(getDesignForASE().toJSONString());
		NxMpConfigMapping mappingData = new NxMpConfigMapping();
		mappingData.setPath("");
		when(jsonPathUtil.search(any(),anyString(), any())).thenReturn(new ArrayList<Object>());
		test.getResultCount(mappingData, jsonObj);
		
		mappingData.setPath("$..component##$..component");
		when(jsonPathUtil.search(any(),anyString(), any())).thenReturn(new ArrayList<Object>());
		test.getResultCount(mappingData, jsonObj);
	}
	
	@Test
	public void testSetNexxusProductSubType() {
		JSONObject jsonObj =  JacksonUtil.toJsonObject(getDesignForASE().toJSONString());
		Map<String, Object> requestMap = new HashMap<String, Object>();
		requestMap.put(MyPriceConstants.AUTOMATION_IND, true);
		Mockito.when(nxLookupDataRepository.findTopByDatasetNameAndItemId(anyString(), anyString())).thenReturn(new NxLookupData());
		test.setNexxusProductSubType(jsonObj, "ASE", requestMap);
	}
	
	@Test
	public void getDataTestCustom1ASE() {
		NxMpConfigMapping nxConfig=new NxMpConfigMapping();
		nxConfig.setType("Custome");
		nxConfig.setOffer("ASE");
		nxConfig.setNxMappingId(1l);
		nxConfig.setDelimiter(",");
		nxConfig.setVariableName("uSOC_pf");
		nxConfig.setPath("$..priceAttributes.[?((@.priceScenarioId == %priceScenarioId% ) && %rateGroups%)].beid");
		nxConfig.setDataSetName(MyPriceConstants.NX_LOOKUP_SOURCE);
		JSONObject inputDesignDetails=getDesignForASE();
		NxLookupData nxLookup=new NxLookupData();
		nxLookup.setId("1");
		when(nxLookupDataRepository.findTopByDatasetNameAndItemId(anyString(),anyString())).thenReturn(nxLookup);
		
		List<NxLookupData> rateGroupObj=new ArrayList<>();
		NxLookupData nxl=new NxLookupData();
		nxl.setItemId("A");
		NxLookupData nxl2=new NxLookupData();
		nxl2.setItemId("B");
		rateGroupObj.add(nxl);
		rateGroupObj.add(nxl2);
		when(nxLookupDataRepository.findByDatasetName(anyString())).thenReturn(rateGroupObj);
		List<String> data=new ArrayList<>();
		data.add("a");
		data.add("b");
		when(jsonPathUtil.search(any(),anyString(),any())).thenReturn(data);
		test.getData(nxConfig, inputDesignDetails,requestMap);
	}
	
	@Test
	public void getDataTestCustom1ADE() {
		NxMpConfigMapping nxConfig=new NxMpConfigMapping();
		nxConfig.setType("Custome");
		nxConfig.setOffer("ADE");
		nxConfig.setNxMappingId(1l);
		nxConfig.setDelimiter(",");
		nxConfig.setVariableName("uSOC_pf");
		nxConfig.setPath("$..priceAttributes.[?((@.priceScenarioId == %priceScenarioId% ) && %rateGroups%)].beid");
		nxConfig.setDataSetName(MyPriceConstants.NX_LOOKUP_SOURCE);
		JSONObject inputDesignDetails=getDesignForASE();
		NxLookupData nxLookup=new NxLookupData();
		nxLookup.setId("1");
		when(nxLookupDataRepository.findTopByDatasetNameAndItemId(anyString(),anyString())).thenReturn(nxLookup);
		
		List<NxLookupData> rateGroupObj=new ArrayList<>();
		NxLookupData nxl=new NxLookupData();
		nxl.setItemId("A");
		NxLookupData nxl2=new NxLookupData();
		nxl2.setItemId("B");
		rateGroupObj.add(nxl);
		rateGroupObj.add(nxl2);
		when(nxLookupDataRepository.findByDatasetName(anyString())).thenReturn(rateGroupObj);
		List<String> data=new ArrayList<>();
		data.add("a");
		data.add("b");
		when(jsonPathUtil.search(any(),anyString(),any())).thenReturn(data);
		test.getData(nxConfig, inputDesignDetails,requestMap);
	}
	
	@Test
	public void getDataTestCustom2ASE() {
		NxMpConfigMapping nxConfig=new NxMpConfigMapping();
		nxConfig.setType("Custome");
		nxConfig.setOffer("ASE");
		nxConfig.setNxMappingId(1l);
		nxConfig.setDelimiter(",");
		nxConfig.setVariableName(MyPriceConstants.DIVERSITY_SERVICE);
		nxConfig.setPath("$..priceAttributes.[?((@.priceScenarioId == %priceScenarioId% ) && %rateGroups%)].beid");
		nxConfig.setDataSetName(MyPriceConstants.NX_LOOKUP_SOURCE);
		JSONObject inputDesignDetails=getDesignForASE();
		NxLookupData nxLookup=new NxLookupData();
		nxLookup.setId("1");
		when(nxLookupDataRepository.findTopByDatasetNameAndItemId(anyString(),anyString())).thenReturn(nxLookup);
		
		List<NxLookupData> rateGroupObj=new ArrayList<>();
		NxLookupData nxl=new NxLookupData();
		nxl.setItemId("A");
		NxLookupData nxl2=new NxLookupData();
		nxl2.setItemId("B");
		rateGroupObj.add(nxl);
		rateGroupObj.add(nxl2);
		when(nxLookupDataRepository.findByDatasetName(anyString())).thenReturn(rateGroupObj);
		List<String> data=new ArrayList<>();
		data.add("a");
		data.add("b");
		when(jsonPathUtil.search(any(),anyString(),any())).thenReturn(data);
		test.getData(nxConfig, inputDesignDetails,requestMap);
	}
	
	@Test
	public void getDataTestCustom2ADE() {
		NxMpConfigMapping nxConfig=new NxMpConfigMapping();
		nxConfig.setType("Custome");
		nxConfig.setOffer("ADE");
		nxConfig.setNxMappingId(1l);
		nxConfig.setDelimiter(",");
		nxConfig.setVariableName(MyPriceConstants.DIVERSITY_SERVICE);
		nxConfig.setPath("$..priceAttributes.[?((@.priceScenarioId == %priceScenarioId% ) && %rateGroups%)].beid");
		nxConfig.setDataSetName(MyPriceConstants.NX_LOOKUP_SOURCE);
		JSONObject inputDesignDetails=getDesignForASE();
		NxLookupData nxLookup=new NxLookupData();
		nxLookup.setId("1");
		when(nxLookupDataRepository.findTopByDatasetNameAndItemId(anyString(),anyString())).thenReturn(nxLookup);
		
		List<NxLookupData> rateGroupObj=new ArrayList<>();
		NxLookupData nxl=new NxLookupData();
		nxl.setItemId("A");
		NxLookupData nxl2=new NxLookupData();
		nxl2.setItemId("B");
		rateGroupObj.add(nxl);
		rateGroupObj.add(nxl2);
		when(nxLookupDataRepository.findByDatasetName(anyString())).thenReturn(rateGroupObj);
		List<String> data=new ArrayList<>();
		data.add("a");
		data.add("b");
		when(jsonPathUtil.search(any(),anyString(),any())).thenReturn(data);
		test.getData(nxConfig, inputDesignDetails,requestMap);
	}
	
	@Test
	public void getDataTestCustom3ASE() {
		NxMpConfigMapping nxConfig=new NxMpConfigMapping();
		nxConfig.setType("Custome");
		nxConfig.setOffer("ASE");
		nxConfig.setNxMappingId(1l);
		nxConfig.setDelimiter(",");
		nxConfig.setVariableName(MyPriceConstants.JURISDICTION);
		nxConfig.setPath("$..priceAttributes.[?((@.priceScenarioId == %priceScenarioId% ) && %rateGroups%)].beid");
		nxConfig.setDataSetName(MyPriceConstants.NX_LOOKUP_SOURCE);
		JSONObject inputDesignDetails=getDesignForASE();
		NxLookupData nxLookup=new NxLookupData();
		nxLookup.setId("1");
		when(nxLookupDataRepository.findTopByDatasetNameAndItemId(anyString(),anyString())).thenReturn(nxLookup);
		
		List<NxLookupData> rateGroupObj=new ArrayList<>();
		NxLookupData nxl=new NxLookupData();
		nxl.setItemId("A");
		NxLookupData nxl2=new NxLookupData();
		nxl2.setItemId("B");
		rateGroupObj.add(nxl);
		rateGroupObj.add(nxl2);
		when(nxLookupDataRepository.findByDatasetName(anyString())).thenReturn(rateGroupObj);
		List<String> data=new ArrayList<>();
		data.add("a");
		data.add("b");
		when(jsonPathUtil.search(any(),anyString(),any())).thenReturn(data);
		test.getData(nxConfig, inputDesignDetails,requestMap);
	}
	
	@Test
	public void getDataTestCustom3ADE() {
		NxMpConfigMapping nxConfig=new NxMpConfigMapping();
		nxConfig.setType("Custome");
		nxConfig.setOffer("ADE");
		nxConfig.setNxMappingId(1l);
		nxConfig.setDelimiter(",");
		nxConfig.setVariableName(MyPriceConstants.JURISDICTION);
		nxConfig.setPath("$..priceAttributes.[?((@.priceScenarioId == %priceScenarioId% ) && %rateGroups%)].beid");
		nxConfig.setDataSetName(MyPriceConstants.NX_LOOKUP_SOURCE);
		JSONObject inputDesignDetails=getDesignForASE();
		NxLookupData nxLookup=new NxLookupData();
		nxLookup.setId("1");
		when(nxLookupDataRepository.findTopByDatasetNameAndItemId(anyString(),anyString())).thenReturn(nxLookup);
		
		List<NxLookupData> rateGroupObj=new ArrayList<>();
		NxLookupData nxl=new NxLookupData();
		nxl.setItemId("A");
		NxLookupData nxl2=new NxLookupData();
		nxl2.setItemId("B");
		rateGroupObj.add(nxl);
		rateGroupObj.add(nxl2);
		when(nxLookupDataRepository.findByDatasetName(anyString())).thenReturn(rateGroupObj);
		List<String> data=new ArrayList<>();
		data.add("a");
		data.add("b");
		when(jsonPathUtil.search(any(),anyString(),any())).thenReturn(data);
		test.getData(nxConfig, inputDesignDetails,requestMap);
	}
	
	@Test
	public void getDataTest2() {
		NxMpConfigMapping nxConfig=new NxMpConfigMapping();
		nxConfig.setPath("${}");
		nxConfig.setDataSetName(MyPriceConstants.SALES_LOOKUP_SOURCE);
		nxConfig.setNxMappingId(1l);
		JSONObject inputDesignDetails=getDesignForASE();
		NxLookupData nxLookup=new NxLookupData();
		nxLookup.setId("1");
		when(nxLookupDataRepository.findTopByDatasetNameAndItemId(anyString(),anyString())).thenReturn(nxLookup);
		test.getData(nxConfig, inputDesignDetails,requestMap);
	}
	
	@Test
	public void getDataTest3() {
		NxMpConfigMapping nxConfig=new NxMpConfigMapping();
		nxConfig.setPath("${}");
		nxConfig.setDataSetName(MyPriceConstants.NX_LOOKUP_SOURCE);
		nxConfig.setNxMappingId(1l);
		JSONObject inputDesignDetails=getDesignForASE();
		test.getData(nxConfig, inputDesignDetails,requestMap);
	}
	
	@Test
	public void getDataTest4() {
		NxMpConfigMapping nxConfig=new NxMpConfigMapping();
		nxConfig.setPath("${}");
		nxConfig.setType("List");
		nxConfig.setDelimiter(",");
		nxConfig.setDataSetName(MyPriceConstants.NX_LOOKUP_SOURCE);
		nxConfig.setNxMappingId(1l);
		JSONObject inputDesignDetails=getDesignForASE();
		List<String> data=new ArrayList<>();
		data.add("a");
		data.add("b");
		when(jsonPathUtil.search(any(),anyString(),any())).thenReturn(data);
		test.getData(nxConfig, inputDesignDetails,requestMap);
	}
	
	@Test
	public void getDataTest5() {
		NxMpConfigMapping nxConfig=new NxMpConfigMapping();
		nxConfig.setPath("$..A##$..B");
		nxConfig.setDelimiter(",");
		nxConfig.setDataSetName(MyPriceConstants.NX_LOOKUP_SOURCE);
		nxConfig.setNxMappingId(1l);
		JSONObject inputDesignDetails=getDesignForASE();
		Object result="RTC";
		when(nexxusJsonUtility.getValue(any(),anyString())).thenReturn(result);
		test.getData(nxConfig, inputDesignDetails,requestMap);
	}
	
	@Test
	public void getDataTest6() {
		NxMpConfigMapping nxConfig=new NxMpConfigMapping();
		nxConfig.setPath("$..A,$..A1##$..B,$..B");
		nxConfig.setDelimiter(",");
		nxConfig.setDataSetName(MyPriceConstants.NX_LOOKUP_SOURCE);
		nxConfig.setNxMappingId(1l);
		JSONObject inputDesignDetails=getDesignForASE();
		Object result="RTC";
		when(nexxusJsonUtility.getValue(any(),anyString())).thenReturn(result);
		test.getData(nxConfig, inputDesignDetails,requestMap);
	}
	
	@Test
	public void getDataTest7() {
		NxMpConfigMapping nxConfig=new NxMpConfigMapping();
		nxConfig.setPath("$..A||$..A1##$..B||$..B");
		nxConfig.setDelimiter(",");
		nxConfig.setDataSetName(MyPriceConstants.NX_LOOKUP_SOURCE);
		nxConfig.setNxMappingId(1l);
		JSONObject inputDesignDetails=getDesignForASE();
		Object result="RTC";
		when(nexxusJsonUtility.getValue(any(),anyString())).thenReturn(result);
		test.getData(nxConfig, inputDesignDetails,requestMap);
	}
	
	@Test
	public void getDataTest8() {
		NxMpConfigMapping nxConfig=new NxMpConfigMapping();
		nxConfig.setPath("$..A||$..A1");
		nxConfig.setDelimiter(",");
		nxConfig.setDataSetName(MyPriceConstants.NX_LOOKUP_SOURCE);
		nxConfig.setNxMappingId(1l);
		JSONObject inputDesignDetails=getDesignForASE();
		Object result="RTC";
		when(nexxusJsonUtility.getValue(any(),anyString())).thenReturn(result);
		test.getData(nxConfig, inputDesignDetails,requestMap);
	}
	
	@Test
	public void mergeSolutionAndDesignDataADETest() {
		
		NxDesign nxDesign=new NxDesign();
		nxDesign.setAsrItemId("123");
		nxDesign.setBundleCd("ASE");
		nxDesign.setNxDesignId(1l);
		nxDesign.setNxSolutionDetail(getSolutionDtlsLstForASE().get(0));
		NxDesignAudit nxDesignAudit=new NxDesignAudit();
		nxDesignAudit.setData("{\"solution\":{\"solutionDeterminants\":{\"icbInd\":null,\"solutionType\":\"NS\",\"mvlInd\":null},"
				+ "\"bulkInd\":null,\"offers\":[{\"bundleCode\":\"ASE\",\"site\":[],\"offerId\":\"103\"}],\"leadDesignID\":null,"
				+ "\"marketStrata\":\"BNS\",\"cancellationReason\":null,\"pricerDSolutionId\":null,\"automationInd\":\"N\","
				+ "\"erateInd\":null,\"layer\":\"Wholesale\",\"solutionStatus\":\"N\"},\"actionDeterminants"
				+ "\":[{\"component\":[\"Design\",\"Price\",\"ADE\"],\"activity\":\"UpdateDesign\"}]}");
		when(nxDesignAuditRepository.findByNxRefIdAndTransaction(any(),any())).thenReturn(nxDesignAudit);
		test.mergeSolutionAndDesignDataADE(nxDesign, getCircuit());
	}
	
	@Test
	public void processJurisdictionASETest() {
		NxMpConfigMapping nxConfig=new NxMpConfigMapping();
		nxConfig.setPath("$..A,$..A1##$..B,$..B");
		nxConfig.setDelimiter(",");
		nxConfig.setDataSetName(MyPriceConstants.NX_LOOKUP_SOURCE);
		nxConfig.setNxMappingId(1l);
		JSONObject inputDesignDetails=getDesignForASE();
		Object result="FCC";
		when(nexxusJsonUtility.getValue(any(),anyString())).thenReturn(result);
		test.processJurisdictionASE(nxConfig,inputDesignDetails);
	}
	
	@Test
	public void processConfigResponseTest() throws SalesBusinessException {
		test.processConfigResponse(getConfigureResponse(), requestMap);
	}
	
	@Test
	public void testCreateResponseBean() {
		Map<String,Object> methodParam = new HashMap<String,Object>();
		methodParam.put(MyPriceConstants.NX_TRANSACTION_ID, 1234L);
		methodParam.put(MyPriceConstants.MP_TRANSACTION_ID, "123456");
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		Document document = db.newDocument();

		Element transaction = document.createElement("bmt:transaction");
		Element lineBomId1 = document.createElement("bmt:_line_bom_id");
		Element lineBomParentId1 = document.createElement("bmt:_line_bom_parent_id");
		Element documentNumber1 = document.createElement("bmt:_document_number");
		Element parentDocNumber1 = document.createElement("bmt:_parent_doc_number");
		Element parentLineItem = document.createElement("bmt:_parent_line_item");
		Element siteReln = document.createElement("bmt:wl_int_ade_site_reln");
		Element uniqueId = document.createElement("bmt:wi_uniqueID_ql");
		Element nxSiteId = document.createElement("bmt:lii_nxSiteId_ql");
		Element usoc = document.createElement("bmt:lii_uSOC_ql");
		Element partNumber = document.createElement("bmt:_line_bom_part_number");
		
		transaction.appendChild(lineBomId1);
		transaction.appendChild(lineBomParentId1);
		transaction.appendChild(documentNumber1);
		transaction.appendChild(parentLineItem);
		transaction.appendChild(parentDocNumber1);
		transaction.appendChild(siteReln);
		transaction.appendChild(uniqueId);
		transaction.appendChild(nxSiteId);
		transaction.appendChild(usoc);
		transaction.appendChild(partNumber);
		transaction.setAttribute("bmt:bs_id", "123456");
		test.createResponseBean(transaction, methodParam);
	}
	
	@Test
	public void testPersistResponse() throws SalesBusinessException {
		Map<String,Object> methodParam = new HashMap<String,Object>();
		methodParam.put(MyPriceConstants.NX_TRANSACTION_ID, 1234L);
		methodParam.put(MyPriceConstants.MP_TRANSACTION_ID, "123456");
		methodParam.put(MyPriceConstants.PRODUCT_NAME, "ASE");
		methodParam.put(MyPriceConstants.TRANSACTION_FLOW_TYPE, "designUpdate");
		methodParam.put(MyPriceConstants.OFFER_NAME, "ASE");
		ConfigRespProcessingBean result = new ConfigRespProcessingBean();
		result.setMpTransactionId("123456");
		result.setNxTransactionId(1234L);
		result.setParentLineItem("BOM_ASE");
		result.setLineBomPartNumber("BOM");
		result.setModelName("ASE");
		result.setModelVariableName("ASE");
		result.setParentDocNumber("2");
		doNothing().when(configureSolnAndProductWSHandler).processSolutionAndProductResponse(anyMap(), anyString(), anyString(), anyString());
		Mockito.when(configAndUpdatePricingUtil.isProductLineIdMatchForConfigDesign(methodParam, result.getParentDocNumber(), "ASE")).thenReturn(true);
		doNothing().when(test).processDesignBlock(result, methodParam);
		test.persistResponse(result, methodParam);
	}
	
	@Test
	public void testProcessDesignBlock() throws SalesBusinessException {
		ConfigRespProcessingBean result = new ConfigRespProcessingBean();
		result.setMpTransactionId("123456");
		result.setNxTransactionId(1234L);
		result.setParentLineItem("BOM_ADE");
		result.setLineBomPartNumber("BOM");
		result.setModelName("ADE");
		result.setModelVariableName("ADE");
		result.setParentDocNumber("2");
		result.setNxSiteId("1231");
		result.setUsocCode("EZXO");
		result.setDocumentNumber("2");
		Map<String,Object> methodParam = new HashMap<String,Object>();
		methodParam.put(MyPriceConstants.NX_DESIGN_ID, 1234L);
		methodParam.put(MyPriceConstants.OFFER_NAME, "ADE");
		methodParam.put(MyPriceConstants.MP_SOLUTION_ID, "2");
		methodParam.put("2", false);
		Mockito.when(test.derivedComponentIdForAde(methodParam, result)).thenReturn("1233");
		Mockito.when(nxMpDesignDocumentRepo.updateDesignBySolIdAndProductIdForAde(1010L, "USOC", "1233", new Date(), "2",
				"2", 10010L)).thenReturn(1);
		test.processDesignBlock(result, methodParam);
		
		methodParam.put("2", true);
		test.processDesignBlock(result, methodParam);
		
		result.setModelName("ASE");
		methodParam.put(MyPriceConstants.OFFER_NAME, "ASE");
		methodParam.put("2", true);
		Mockito.when(nxMpDesignDocumentRepo.updateDesignBySolIdAndProductId(1010L, "USOC", new Date(), "2",
				"2", 10010L)).thenReturn(1);
		test.processDesignBlock(result, methodParam);
		
		methodParam.put("2", false);
		test.processDesignBlock(result, methodParam);
	}
	
	@Test
	public void testDerivedDataForRequestUsocFields() {
		JSONObject jsonObj =  JacksonUtil.toJsonObject(getDesignForASE().toJSONString());
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(MyPriceConstants.OFFER_NAME, "ASE");
		map.put(StringConstants.PRICE_SCENARIO_ID, "8745");
		/*Map<String,List<Map<String,String>>> usocDataMap = new HashMap<String,List<Map<String,String>>>();
		Map<String,String> data = new HashMap<String,String>();
		data.put("NEW", "NEW");
		usocDataMap.put("ECSOP", new ArrayList<Map<String,String>>(){{add(data);}});*/
		Map<String, Map<String,String>> usocDataMap = new HashMap<String, Map<String,String>>();
		Map<String,String> data = new HashMap<String,String>();
		data.put("NEW", "NEW");
		usocDataMap.put("ECSOP", data);
		Mockito.when(test.createDataMapByUsocId(any(), anyMap())).thenReturn(usocDataMap);
		Mockito.when(configAndUpdatePricingUtil.getUsocIdCategory(any(), anyString(), anyMap(), anyMap())).thenReturn("New");
		test.derivedDataForRequestUsocFields(jsonObj, map);
		
		Mockito.when(configAndUpdatePricingUtil.getUsocIdCategory(any(), anyString(), anyMap(),anyMap())).thenReturn("Existing");
		test.derivedDataForRequestUsocFields(jsonObj, map);
		
		Mockito.when(configAndUpdatePricingUtil.getUsocIdCategory(any(), anyString(), anyMap(),anyMap())).thenReturn("Migration");
		test.derivedDataForRequestUsocFields(jsonObj, map);
	}
	
	@Test
	public void testDerivedDataForRequestUsocFieldsADE() {
		JSONObject jsonObj =  JacksonUtil.toJsonObject(getDesignForASE().toJSONString());
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(MyPriceConstants.OFFER_NAME, "ADE");
		map.put(StringConstants.PRICE_SCENARIO_ID, "8745");
		Map<String,List<Map<String,String>>> usocDataMap = new HashMap<String,List<Map<String,String>>>();
		Map<String,String> data = new HashMap<String,String>();
		data.put("NEW", "New");
		usocDataMap.put("ECSOP", new ArrayList<Map<String,String>>(){{add(data);}});
		ComponentDetails comp = new ComponentDetails();
		comp.setComponentCodeId(1222L);
		comp.setComponentCodeType("circuit");
		List<ComponentDetails> cps = new ArrayList<ComponentDetails>() {{add(comp);}};
		NxLookupData nxLookupData = new NxLookupData();
		nxLookupData.setId("path");
		Mockito.when(configAndUpdatePricingUtil.getComponentList(any(), anyString())).thenReturn(cps);
		Mockito.when(configAndUpdatePricingUtil.getUsocIdCategory(any(), anyString(), anyMap(),anyMap())).thenReturn("New");
		PriceAttributes price = new PriceAttributes();
		price.setBeid("ESHD");
		List<PriceAttributes> prices = new ArrayList<PriceAttributes>() {{add(price);}};
		Mockito.when(configAndUpdatePricingUtil.getPriceAttributes(any(), anyString())).thenReturn(prices);
		test.derivedDataForRequestUsocFields(jsonObj, map);

	}
	
	@Test
	public void testCreateDataMapByUsocIdForAde() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("myPriceTransId", "42373160");
		map.put("nxTxnId", 21L);
		map.put("offerName", "ASE");
		map.put("nxDesignId", 10L);
		map.put(StringConstants.PRICE_SCENARIO_ID, "8745");
		String json=getDesignForASE().toJSONString();
		JSONObject jsonObj = JacksonUtil.toJsonObject(json);
		ComponentDetails comp = new ComponentDetails();
		comp.setComponentCodeId(1222L);
		comp.setComponentCodeType("circuit");
		List<ComponentDetails> cps = new ArrayList<ComponentDetails>() {{add(comp);}};
		Mockito.when(configAndUpdatePricingUtil.getComponentList(any(), anyString())).thenReturn(cps);
		Mockito.when(configAndUpdatePricingUtil.getPriceAttributes(any(), anyString())).thenReturn(new ArrayList<PriceAttributes>() {{add(new PriceAttributes());}});
		test.createDataMapByUsocIdForAde(jsonObj, map);
	}
	
	@Test
	public void testCreateDataMapByUsocId() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("myPriceTransId", "42373160");
		map.put("nxTxnId", 21L);
		map.put("offerName", "ASE");
		map.put("nxDesignId", 10L);
		map.put(StringConstants.PRICE_SCENARIO_ID, "8745");
		String json=getDesignForASE().toJSONString();
		JSONObject jsonObj = JacksonUtil.toJsonObject(json);
		ComponentDetails comp = new ComponentDetails();
		comp.setComponentCodeId(1222L);
		comp.setComponentCodeType("circuit");
		List<ComponentDetails> cps = new ArrayList<ComponentDetails>() {{add(comp);}};
		PriceAttributes price = new PriceAttributes();
		price.setBeid("ESHD");
		List<PriceAttributes> prices = new ArrayList<PriceAttributes>() {{add(price);}};
		Mockito.when(configAndUpdatePricingUtil.getPriceAttributes(any(), anyString())).thenReturn(prices);
		test.createDataMapByUsocId(jsonObj, map);
		
		map.put("offerName", "ASENOD");
		test.createDataMapByUsocId(jsonObj, map);
	}
	@Test
	public void testFilter3PAUsoc() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(MyPriceConstants.PRODUCT_TYPE, "asenod_3PA");
		String json=getDesignForASE().toJSONString();
		JSONObject jsonObj = JacksonUtil.toJsonObject(json);
		List<String> str = new ArrayList<String>() {{add("UAS"); add("AAS");}};
		PriceAttributes price = new PriceAttributes();
		price.setBeid("UAS");
		List<PriceAttributes> beidByRateGroup = new ArrayList<PriceAttributes>() {{add(price);}};
		Mockito.when(configAndUpdatePricingUtil.getDataListInString(any(), anyString())).thenReturn(str);
		test.filter3PAUsoc(map, jsonObj, beidByRateGroup);
	}
	
	@Test
	public void testSetSolProductData() {
		requestMap.put(MyPriceConstants.NX_TRANSACTION_ID, 101L);
		Map<String,String> solProductDataMap= new HashMap<String, String>();
		solProductDataMap.put("2", "ASE");
		Mockito.when(nxMpRepositoryService.getDataByNxtxnId(anyLong())).thenReturn(solProductDataMap);
		test.setSolProductData(requestMap, "ASE");
	}
	
	
	
	@Test
	public void testGetConvertedUsocIdFor3PA() {
		Map<String,List<String>> usocCriteriaMap = new HashMap<String,List<String>>();
		usocCriteriaMap.put("Key", new ArrayList<String>() {{add("ECSO");}});
		String inputUsocId = "ECSO";
		test.getConvertedUsocIdFor3PA(usocCriteriaMap, inputUsocId);
	}
	
	@Test
	public void testDeleteExistingRecordsByTxnIdAndDesignId() {
		Map<String,Object> methodParam = new HashMap<String,Object>();
		methodParam.put(MyPriceConstants.NX_DESIGN_ID, 10101L);
		methodParam.put(MyPriceConstants.NX_TRANSACTION_ID, 101L);
		Mockito.when(nxMpPriceDetailsRepository.deleteByNxTxnIdAndNxDesignId(anyLong(), anyLong())).thenReturn(1L);
		Mockito.when(nxMpDesignDocumentRepo.deleteByNxTxnIdAndNxDesignId(anyLong(), anyLong())).thenReturn(1L);
		test.deleteExistingRecordsByTxnIdAndDesignId(methodParam);
	}
	@Test
	public void testDerivedComponentIdForAde()  {
		ConfigRespProcessingBean result = new ConfigRespProcessingBean();
		result.setMpTransactionId("123456");
		result.setNxTransactionId(1234L);
		result.setParentLineItem("BOM_ADE");
		result.setLineBomPartNumber("BOM");
		result.setModelName("ADE");
		result.setModelVariableName("ADE");
		result.setParentDocNumber("2");
		result.setNxSiteId("1231");
		result.setUsocCode("EZXO");
		Map<String,Object> methodParam = new HashMap<String,Object>();
		methodParam.put(MyPriceConstants.NX_DESIGN_ID, 1234L);
		methodParam.put(MyPriceConstants.OFFER_NAME, "ADE");
		methodParam.put(MyPriceConstants.MP_SOLUTION_ID, "2");
		methodParam.put(MyPriceConstants.ENDPOINT_A_NX_SITE_ID, "1231");
		methodParam.put(MyPriceConstants.ENDPOINT_Z_NX_SITE_ID, "1232");
		methodParam.put(MyPriceConstants.CIRCUIT_BEID, new ArrayList<String>() {{add("EZXO");}});
		methodParam.put(MyPriceConstants.CIRCUIT_COMPONENT_ID, "12333");
		test.derivedComponentIdForAde(methodParam, result);
		
		methodParam.remove(MyPriceConstants.CIRCUIT_BEID);
		methodParam.put(MyPriceConstants.ENDPOINT_A_BEID, new ArrayList<String>() {{add("EZXO");}});
		methodParam.put(MyPriceConstants.ENDPOINT_A_COMPONENT_ID, "12333");
		test.derivedComponentIdForAde(methodParam, result);
		
		methodParam.remove(MyPriceConstants.ENDPOINT_A_BEID);
		methodParam.put(MyPriceConstants.ENDPOINT_Z_BEID, new ArrayList<String>() {{add("EZXO");}});
		methodParam.put(MyPriceConstants.ENDPOINT_Z_COMPONENT_ID, "12333");
		test.derivedComponentIdForAde(methodParam, result);
		
		result.setNxSiteId("1232");
		result.setUsocCode("EZXO");
		methodParam.put(MyPriceConstants.ENDPOINT_Z_BEID, new ArrayList<String>() {{add("EZXO");}});
		methodParam.put(MyPriceConstants.ENDPOINT_Z_COMPONENT_ID, "12333");
		test.derivedComponentIdForAde(methodParam, result);
		
	}
	
	private ConfigureResponse getConfigureResponse() {
		ConfigureResponse response = new ConfigureResponse();
		ObjectFactory objectFactory = new ObjectFactory();
		response.setTransaction(
				objectFactory.createConfigureResponseTransaction(objectFactory.createTransactionType()));
		response.getTransaction().getValue().setDataXml(objectFactory.createAnyType());
		response.getTransaction().getValue().getDataXml().getAny().add(createTransactionElement());
		return response;
	}
	
	private Element createTransactionElement() {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		Document document = db.newDocument();

		Element transaction = document.createElement("bmt:transaction");
		Element subDocuments = document.createElement("bmt:sub_documents");

		Element transactionLine1 = document.createElement("bmt:transactionLine");
		Element lineBomId1 = document.createElement("bmt:_line_bom_id");
		Element lineBomParentId1 = document.createElement("bmt:_line_bom_parent_id");
		Element documentNumber1 = document.createElement("bmt:_document_number");
		Element prodLineId1 = document.createElement("bmt:_model_product_line_id");
		Element parentDocNumber1 = document.createElement("bmt:_parent_doc_number");
		Element lineBomPartNo1 = document.createElement("bmt:_line_bom_part_number");
		Element parentLineitem1 = document.createElement("bmt:_parent_line_item");
		Element wiUniqueQl1 = document.createElement("bmt:wi_uniqueID_ql");
		Element liiUsocQl1 = document.createElement("bmt:lii_uSOC_ql");

		lineBomId1.appendChild(document.createTextNode("BOM_Solution"));
		lineBomParentId1.appendChild(document.createTextNode("BOM_Solution"));
		documentNumber1.appendChild(document.createTextNode("1"));
		prodLineId1.appendChild(document.createTextNode("37903795"));
		parentDocNumber1.appendChild(document.createTextNode(null));

		Element transactionLine2 = document.createElement("bmt:transactionLine");
		Element lineBomId2 = document.createElement("bmt:_line_bom_id");
		Element lineBomParentId2 = document.createElement("bmt:_line_bom_parent_id");
		Element documentNumber2 = document.createElement("bmt:_document_number");
		Element prodLineId2 = document.createElement("bmt:_model_product_line_id");
		Element parentDocNumber2 = document.createElement("bmt:_parent_doc_number");
		Element lineBomPartNo2 = document.createElement("bmt:_line_bom_part_number");
		Element parentLineitem2 = document.createElement("bmt:_parent_line_item");
		Element wiUniqueQl2 = document.createElement("bmt:wi_uniqueID_ql");
		Element liiUsocQl2 = document.createElement("bmt:lii_uSOC_ql");
		Element siteId1 = document.createElement("bmt:_site_id");
		Element siteId2 = document.createElement("bmt:_site_id");
		
		

		lineBomId2.appendChild(document.createTextNode("BOM_SolutionASE"));
		lineBomParentId2.appendChild(document.createTextNode("BOM_Solution"));
		documentNumber2.appendChild(document.createTextNode("2"));
		prodLineId2.appendChild(document.createTextNode("37903795"));
		parentDocNumber2.appendChild(document.createTextNode("1"));

		transactionLine1.appendChild(lineBomId1);
		transactionLine1.appendChild(lineBomParentId1);
		transactionLine1.appendChild(documentNumber1);
		transactionLine1.appendChild(prodLineId1);
		transactionLine1.appendChild(parentDocNumber1);
		transactionLine1.appendChild(lineBomPartNo1);
		transactionLine1.appendChild(parentLineitem1);
		transactionLine1.appendChild(wiUniqueQl1);
		transactionLine1.appendChild(liiUsocQl1);
		transactionLine1.appendChild(siteId1);
		transactionLine1.setAttribute("bmt:bs_id", "123");

		transactionLine2.appendChild(lineBomId2);
		transactionLine2.appendChild(lineBomParentId2);
		transactionLine2.appendChild(documentNumber2);
		transactionLine2.appendChild(prodLineId2);
		transactionLine2.appendChild(parentDocNumber2);
		transactionLine2.appendChild(lineBomPartNo2);
		transactionLine2.appendChild(parentLineitem2);
		transactionLine2.appendChild(wiUniqueQl2);
		transactionLine2.appendChild(liiUsocQl2);
		transactionLine2.appendChild(siteId2);
		transactionLine2.setAttribute("bmt:bs_id", "123");

		subDocuments.appendChild(transactionLine1);
		subDocuments.appendChild(transactionLine2);
		transaction.appendChild(subDocuments);
		document.appendChild(transaction);
		return transaction;
	}

}
