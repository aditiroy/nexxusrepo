package com.att.sales.nexxus.myprice.transaction.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.att.sales.nexxus.constant.FmoConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.dao.model.NxDesign;
import com.att.sales.nexxus.dao.model.NxDesignAudit;
import com.att.sales.nexxus.dao.model.NxDesignDetails;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.model.NxMpConfigMapping;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.repository.NxDesignAuditRepository;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.service.NxMyPriceRepositoryServce;
import com.att.sales.nexxus.util.JacksonUtil;
import com.att.sales.nexxus.util.JsonPathUtil;
import com.att.sales.nexxus.util.NexxusJsonUtility;
import com.jayway.jsonpath.TypeRef;

@ExtendWith(MockitoExtension.class)

public class ConfigAndUpdatePricingUtilTest {
	
	@InjectMocks
	private ConfigAndUpdatePricingUtil configAndUpdatePricingUtil;
	
	@Mock
	private NxDesignAuditRepository nxDesignAuditRepository;
	
	@Mock
	private NexxusJsonUtility nexxusJsonUtility;
	
	@Mock
	private NxLookupDataRepository nxLookupDataRepository;
	
	@Mock
	private JsonPathUtil jsonPathUtil;
	
	@Mock
	private NxMyPriceRepositoryServce nxMyPriceRepositoryServce;
	
	private NxDesign nxDesign;
	
	private String data;
	
	private NxMpConfigMapping mappingData;
	
	private JSONObject inputDesignDetails;
	
	private Map<String,Object> methodParam;
	
	@BeforeEach
	public void init() {
		nxDesign = new NxDesign();
		NxSolutionDetail soln = new NxSolutionDetail();
		soln.setNxSolutionId(101l);
		nxDesign.setNxSolutionDetail(soln);
		NxDesignDetails design = new NxDesignDetails();
		List<NxDesignDetails> designs = new ArrayList<>();
		designs.add(design);
		nxDesign.setNxDesignDetails(designs);
		
		data = "{\"nxSiteId\":455911,\"siteId\":99988948745,\"npanxx\":\"987654\",\"address1\":\"2051 MERCY DR\",\"city\":\"ORLANDO\",\"state\":\"FL\",\"saLecName\":null,\"speedId\":null,\"saLecSwClli\":null,\"popClli\":null,\"country\":null,\"macdType\":\"Change\",\"macdActivity\":\"Change COS;\",\"fromInventory\":null,\"dualSiteId\":null,\"onNetCheck\":null,\"ethernetVendor\":null,\"designSiteOfferPort\":[{\"designStatus\":null,\"typeOfInventory\":\"To\",\"milesResult\":null,\"securityDesignDetails\":null,\"macdActivityType\":null,\"component\":[{\"componentCodeId\":10,\"fromInvYN\":null,\"logicalChannelPvcID\":null,\"eteVpnKey\":null,\"diversityGroupId\":null,\"componentCodeType\":\"Connection\",\"componentId\":9270726,\"externalField\":null,\"parentComponentId\":null,\"externalKeyRef\":null,\"mvlInd\":null,\"userEnteredVpn\":null,\"siteId\":null,\"logicalChannelId\":null,\"designDetails\":[{\"udfAttributeId\":[30595],\"udfId\":20030,\"readOnly\":null,\"udfAttributeText\":[\"Primary\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[30515],\"udfId\":20017,\"readOnly\":null,\"udfAttributeText\":[\"AVPN\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null}],\"routeTargets\":null,\"references\":null,\"siteObj\":null},{\"componentCodeId\":30,\"fromInvYN\":null,\"logicalChannelPvcID\":null,\"eteVpnKey\":null,\"diversityGroupId\":null,\"componentCodeType\":\"Port\",\"componentId\":null,\"externalField\":null,\"parentComponentId\":null,\"externalKeyRef\":null,\"mvlInd\":null,\"userEnteredVpn\":null,\"siteId\":null,\"logicalChannelId\":null,\"designDetails\":[{\"udfAttributeId\":[301475],\"udfId\":20173,\"readOnly\":null,\"udfAttributeText\":[\"SGOS\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":null,\"udfId\":20184,\"readOnly\":null,\"udfAttributeText\":[\"ORLDFLMA\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":null,\"udfId\":20184,\"readOnly\":null,\"udfAttributeText\":[\"ORLDFLMA\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":null,\"udfId\":20184,\"readOnly\":null,\"udfAttributeText\":[\"ORLDFLMA\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":null,\"udfId\":20184,\"readOnly\":null,\"udfAttributeText\":[\"ORLDFLMA\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":null,\"udfId\":20169,\"readOnly\":null,\"udfAttributeText\":[\"100002034840\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[301473],\"udfId\":20171,\"readOnly\":null,\"udfAttributeText\":[\"Fiber\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[301473],\"udfId\":20171,\"readOnly\":null,\"udfAttributeText\":[\"Fiber\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[300305],\"udfId\":20013,\"readOnly\":null,\"udfAttributeText\":[\"Business Critical Medium (Profile 2)\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[300305],\"udfId\":20013,\"readOnly\":null,\"udfAttributeText\":[\"Business Critical Medium (Profile 2)\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[300248],\"udfId\":200006,\"readOnly\":null,\"udfAttributeText\":[\"LC/VLAN-level\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[300248],\"udfId\":200006,\"readOnly\":null,\"udfAttributeText\":[\"LC/VLAN-level\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[300251],\"udfId\":200008,\"readOnly\":null,\"udfAttributeText\":[\"Change\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[300251],\"udfId\":200008,\"readOnly\":null,\"udfAttributeText\":[\"Change\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[30604],\"udfId\":200009,\"readOnly\":null,\"udfAttributeText\":[\"N\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[300259],\"udfId\":200010,\"readOnly\":null,\"udfAttributeText\":[\"Change COS\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[30604],\"udfId\":200011,\"readOnly\":null,\"udfAttributeText\":[\"N\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[30605],\"udfId\":200012,\"readOnly\":null,\"udfAttributeText\":[\"Yes\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[30604],\"udfId\":200013,\"readOnly\":null,\"udfAttributeText\":[\"N\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[300273],\"udfId\":200015,\"readOnly\":null,\"udfAttributeText\":[\"IN-SERVICE\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[300277],\"udfId\":200018,\"readOnly\":null,\"udfAttributeText\":[\"Existing\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[300284],\"udfId\":200020,\"readOnly\":null,\"udfAttributeText\":[\"IR/IF End-User Customer\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[300284],\"udfId\":200020,\"readOnly\":null,\"udfAttributeText\":[\"IR/IF End-User Customer\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[300294],\"udfId\":200021,\"readOnly\":null,\"udfAttributeText\":[\"1 Gb\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[300294],\"udfId\":200021,\"readOnly\":null,\"udfAttributeText\":[\"1 Gb\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[301475],\"udfId\":200022,\"readOnly\":null,\"udfAttributeText\":[\"SGOS\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[30604],\"udfId\":200027,\"readOnly\":null,\"udfAttributeText\":[\"N\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[30605],\"udfId\":200028,\"readOnly\":null,\"udfAttributeText\":[\"Yes\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[30605],\"udfId\":200028,\"readOnly\":null,\"udfAttributeText\":[\"Yes\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[74933],\"udfId\":200031,\"readOnly\":null,\"udfAttributeText\":[\"250 Mb\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":null,\"udfId\":200032,\"readOnly\":null,\"udfAttributeText\":[\"0\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[300364],\"udfId\":200033,\"readOnly\":null,\"udfAttributeText\":[\"1 G\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[300367],\"udfId\":200035,\"readOnly\":null,\"udfAttributeText\":[\"Inside\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[300371],\"udfId\":200036,\"readOnly\":null,\"udfAttributeText\":[\"Wall\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[300373],\"udfId\":200037,\"readOnly\":null,\"udfAttributeText\":[\"AC Single\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[300378],\"udfId\":200040,\"readOnly\":null,\"udfAttributeText\":[\"02LNF.A02, (1000 Base LX) Optical\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[300378],\"udfId\":200040,\"readOnly\":null,\"udfAttributeText\":[\"02LNF.A02, (1000 Base LX) Optical\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[30604],\"udfId\":200048,\"readOnly\":null,\"udfAttributeText\":[\"N\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[300401],\"udfId\":200058,\"readOnly\":null,\"udfAttributeText\":[\"IF\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[300401],\"udfId\":200058,\"readOnly\":null,\"udfAttributeText\":[\"IF\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[55064],\"udfId\":200059,\"readOnly\":null,\"udfAttributeText\":[\"Y\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[55064],\"udfId\":200059,\"readOnly\":null,\"udfAttributeText\":[\"Y\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[30604],\"udfId\":200060,\"readOnly\":null,\"udfAttributeText\":[\"N\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[30604],\"udfId\":200075,\"readOnly\":null,\"udfAttributeText\":[\"N\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[30604],\"udfId\":200075,\"readOnly\":null,\"udfAttributeText\":[\"N\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[30604],\"udfId\":200079,\"readOnly\":null,\"udfAttributeText\":[\"N\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[30604],\"udfId\":200079,\"readOnly\":null,\"udfAttributeText\":[\"N\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":null,\"udfId\":200007,\"readOnly\":null,\"udfAttributeText\":[\"22/KRFN/233333/SD\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":null,\"udfId\":200014,\"readOnly\":null,\"udfAttributeText\":[\"2020-07-08 00:00:00.0\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":null,\"udfId\":200017,\"readOnly\":null,\"udfAttributeText\":[\"Recap\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":null,\"udfId\":200045,\"readOnly\":null,\"udfAttributeText\":[\"ORLDFLMA\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":null,\"udfId\":200046,\"readOnly\":null,\"udfAttributeText\":[\"7\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":null,\"udfId\":200047,\"readOnly\":null,\"udfAttributeText\":[\"7\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[55042],\"udfId\":200133,\"readOnly\":null,\"udfAttributeText\":[\"100 Mb\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[55042],\"udfId\":200133,\"readOnly\":null,\"udfAttributeText\":[\"100 Mb\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[301507],\"udfId\":200134,\"readOnly\":null,\"udfAttributeText\":[\"9STATES\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[604701],\"udfId\":200145,\"readOnly\":null,\"udfAttributeText\":[\"SIMPLE_MACD\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[30604],\"udfId\":22019,\"readOnly\":null,\"udfAttributeText\":[\"N\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[30604],\"udfId\":22020,\"readOnly\":null,\"udfAttributeText\":[\"N\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[30604],\"udfId\":22021,\"readOnly\":null,\"udfAttributeText\":[\"N\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[30604],\"udfId\":22021,\"readOnly\":null,\"udfAttributeText\":[\"N\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[302216],\"udfId\":21860,\"readOnly\":null,\"udfAttributeText\":[\"Certified\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[302216],\"udfId\":21861,\"readOnly\":null,\"udfAttributeText\":[\"Certified\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[55065],\"udfId\":21862,\"readOnly\":null,\"udfAttributeText\":[\"N\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[55065],\"udfId\":21862,\"readOnly\":null,\"udfAttributeText\":[\"N\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[605332],\"udfId\":21960,\"readOnly\":null,\"udfAttributeText\":[\"Green\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":null,\"udfId\":21961,\"readOnly\":null,\"udfAttributeText\":[\"1.37\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[301476],\"udfId\":21962,\"readOnly\":null,\"udfAttributeText\":[\"TRUE\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":null,\"udfId\":1000360,\"readOnly\":null,\"udfAttributeText\":[\"Tier 1\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":null,\"udfId\":1000360,\"readOnly\":null,\"udfAttributeText\":[\"Tier 1\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":null,\"udfId\":1000360,\"readOnly\":null,\"udfAttributeText\":[\"Tier 1\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":null,\"udfId\":20187,\"readOnly\":null,\"udfAttributeText\":[\"02LNF.A02\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[301590],\"udfId\":200143,\"readOnly\":null,\"udfAttributeText\":[\"KRE1\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null}],\"routeTargets\":null,\"references\":null,\"siteObj\":null}],\"routerDetails\":null,\"logicalChannelDetail\":null,\"customDesign\":null,\"accessPricingAQ\":null,\"aggregateBilling\":null,\"portValidationMessage\":null,\"voiceFeatureDetail\":null}],\"priceDetails\":{\"componentDetails\":[{\"componentCodeId\":null,\"componentCodeType\":null,\"componentId\":99988948745,\"componentType\":\"Site\",\"componentParentId\":6511617,\"componentAttributes\":[{\"componentFieldName\":\"Country\",\"componentFieldValue\":\"US\"}],\"priceAttributes\":[],\"scpPriceMessages\":null},{\"componentCodeId\":null,\"componentCodeType\":null,\"componentId\":9270726,\"componentType\":\"Port\",\"componentParentId\":99988948745,\"componentAttributes\":[{\"componentFieldName\":\"Country\",\"componentFieldValue\":\"US\"}],\"priceAttributes\":[{\"productRateId\":0,\"beid\":\"OEM1G\",\"rateDescription\":null,\"priceCatalog\":\"Standard Pricing\",\"localListPrice\":2100.0,\"targetListPrice\":2100.0,\"icbDesiredDiscPerc\":null,\"priceType\":\"PORT\",\"priceUnit\":\"Month\",\"frequency\":\"NRC\",\"monthlySurcharge\":null,\"discount\":null,\"discountId\":null,\"quantity\":\"1\",\"localNetPrice\":2100.0,\"targetNetPrice\":2100.0,\"localTotalPrice\":2100.0,\"targetTotalPrice\":null,\"localCurrency\":\"USD\",\"targetCurrency\":\"USD\",\"rdsPriceType\":\"Port\",\"priceName\":\"OEM_Port - 1 Gb - NRC\",\"typeOfInventory\":null,\"priceInUSD\":\"2100\",\"priceScenarioId\":99999908428,\"rateGroup\":\"OEM_Port\",\"externalBillingSystem\":null,\"ratePlanId\":null,\"priceCompType\":\"ASE\",\"pvcId\":null,\"chargeCodeId\":null,\"requestedNRCRate\":null,\"requestedNRCDiscPercentage\":null,\"requestedMRCRate\":null,\"requestedMRCDiscPercentage\":null,\"priceModifiedInd\":null,\"requestedDiscount\":null,\"requestedRate\":null,\"term\":null,\"priceGroup\":null,\"referencePortId\":null,\"componentType\":null,\"componentParentId\":null,\"country\":null,\"secondaryKeys\":null,\"lineItemId\":null,\"nrcBeid\":null,\"uniqueId\":null,\"elementType\":null,\"reqPriceType\":null},{\"productRateId\":0,\"beid\":\"OEM1G\",\"rateDescription\":null,\"priceCatalog\":\"Standard Pricing\",\"localListPrice\":960.0,\"targetListPrice\":960.0,\"icbDesiredDiscPerc\":null,\"priceType\":\"PORT\",\"priceUnit\":\"Month\",\"frequency\":\"MRC\",\"monthlySurcharge\":null,\"discount\":null,\"discountId\":null,\"quantity\":\"1\",\"localNetPrice\":960.0,\"targetNetPrice\":960.0,\"localTotalPrice\":960.0,\"targetTotalPrice\":null,\"localCurrency\":\"USD\",\"targetCurrency\":\"USD\",\"rdsPriceType\":\"Port\",\"priceName\":\"OEM_Port - 1 Gb - 12\",\"typeOfInventory\":null,\"priceInUSD\":\"960\",\"priceScenarioId\":99999908428,\"rateGroup\":\"OEM_Port\",\"externalBillingSystem\":null,\"ratePlanId\":null,\"priceCompType\":\"ASE\",\"pvcId\":null,\"chargeCodeId\":null,\"requestedNRCRate\":null,\"requestedNRCDiscPercentage\":null,\"requestedMRCRate\":null,\"requestedMRCDiscPercentage\":null,\"priceModifiedInd\":null,\"requestedDiscount\":null,\"requestedRate\":null,\"term\":null,\"priceGroup\":null,\"referencePortId\":null,\"componentType\":null,\"componentParentId\":null,\"country\":null,\"secondaryKeys\":null,\"lineItemId\":null,\"nrcBeid\":null,\"uniqueId\":null,\"elementType\":null,\"reqPriceType\":null},{\"productRateId\":0,\"beid\":\"OEM1H\",\"rateDescription\":null,\"priceCatalog\":\"Standard Pricing\",\"localListPrice\":1424.0,\"targetListPrice\":1424.0,\"icbDesiredDiscPerc\":null,\"priceType\":\"COS\",\"priceUnit\":\"Month\",\"frequency\":\"MRC\",\"monthlySurcharge\":null,\"discount\":null,\"discountId\":null,\"quantity\":\"1\",\"localNetPrice\":1424.0,\"targetNetPrice\":1424.0,\"localTotalPrice\":1424.0,\"targetTotalPrice\":null,\"localCurrency\":\"USD\",\"targetCurrency\":\"USD\",\"rdsPriceType\":\"Cos\",\"priceName\":\"OEM_Business - 100 Mb - 12\",\"typeOfInventory\":null,\"priceInUSD\":\"1424\",\"priceScenarioId\":99999908428,\"rateGroup\":\"OEM_Business\",\"externalBillingSystem\":null,\"ratePlanId\":null,\"priceCompType\":\"ASE\",\"pvcId\":null,\"chargeCodeId\":null,\"requestedNRCRate\":null,\"requestedNRCDiscPercentage\":null,\"requestedMRCRate\":null,\"requestedMRCDiscPercentage\":null,\"priceModifiedInd\":null,\"requestedDiscount\":null,\"requestedRate\":null,\"term\":null,\"priceGroup\":null,\"referencePortId\":null,\"componentType\":null,\"componentParentId\":null,\"country\":null,\"secondaryKeys\":null,\"lineItemId\":null,\"nrcBeid\":null,\"uniqueId\":null,\"elementType\":null,\"reqPriceType\":null},{\"productRateId\":0,\"beid\":\"OEM1H\",\"rateDescription\":null,\"priceCatalog\":\"Standard Pricing\",\"localListPrice\":150.0,\"targetListPrice\":150.0,\"icbDesiredDiscPerc\":null,\"priceType\":\"COS\",\"priceUnit\":\"Month\",\"frequency\":\"NRC\",\"monthlySurcharge\":null,\"discount\":null,\"discountId\":null,\"quantity\":\"1\",\"localNetPrice\":150.0,\"targetNetPrice\":150.0,\"localTotalPrice\":150.0,\"targetTotalPrice\":null,\"localCurrency\":\"USD\",\"targetCurrency\":\"USD\",\"rdsPriceType\":\"Cos\",\"priceName\":\"OEM_Business - 100 Mb - NRC\",\"typeOfInventory\":null,\"priceInUSD\":\"150\",\"priceScenarioId\":99999908428,\"rateGroup\":\"OEM_Business\",\"externalBillingSystem\":null,\"ratePlanId\":null,\"priceCompType\":\"ASE\",\"pvcId\":null,\"chargeCodeId\":null,\"requestedNRCRate\":null,\"requestedNRCDiscPercentage\":null,\"requestedMRCRate\":null,\"requestedMRCDiscPercentage\":null,\"priceModifiedInd\":null,\"requestedDiscount\":null,\"requestedRate\":null,\"term\":null,\"priceGroup\":null,\"referencePortId\":null,\"componentType\":null,\"componentParentId\":null,\"country\":null,\"secondaryKeys\":null,\"lineItemId\":null,\"nrcBeid\":null,\"uniqueId\":null,\"elementType\":null,\"reqPriceType\":null}],\"scpPriceMessages\":null}],\"priceMessage\":null},\"address\":null,\"postalCode\":null,\"siteName\":\"loc2\",\"swcClli\":null,\"customerLocationClli\":null,\"active\":null,\"emc\":null,\"carrierHotel\":null,\"attComments\":null,\"newBuilding\":null,\"customerReference\":null,\"asrItemId\":\"100002034840\",\"lataCode\":\"458\",\"zipCode\":\"36740\",\"address2\":null,\"room\":null,\"floor\":null,\"building\":null,\"siteComment\":null,\"lconFirstName\":null,\"lconLastName\":null,\"lconPhone\":null,\"lconEmail\":null,\"endPointSiteIdentifier\":\"A\",\"buildingClli\":null,\"regionCode\":\"Y\",\"activityType\":null,\"cancellationReason\":null,\"product\":null,\"quantity\":\"1\",\"nssEngagement\":null,\"designStatus\":\"N\",\"multiGigeIndicator\":null,\"alias\":\"loc2\",\"macdActionType\":null,\"lconDetails\":[{\"lconType\":null,\"lconFirstName\":null,\"lconLastName\":null,\"lconPhone\":null,\"lconEmail\":null}],\"globalLocationId\":\"000006ZFMP\",\"jurisdiction\":\"FCC\",\"certificationStatus\":null,\"designModifiedInd\":null,\"independentCarrierCompanyLATA\":null,\"independentCarrierCompanyName\":null,\"interDepartmentMeetPointChecklistURL\":null,\"diverseFromReferenceInfo\":null,\"opticalTerminatingNetworkCarrierFacilityAssignment\":null,\"collocationCarrierFacilityAssignment\":null,\"assetInvestmentSheetIndicator\":null,\"swcCertification\":\"Certified\",\"designVersion\":\"1\",\"specialConstructionPaymentUrl\":null,\"thirdPartyInd\":null,\"accessCarrierNameAbbreviation\":null,\"design\":null,\"loopLength\":null,\"numOfCopperRepeaters\":null,\"inventoryNumOfPairs\":null,\"taskClli\":null,\"numberRemoteTerminals\":null,\"referenceOfferId\":null,\"siteNpanxx\":null,\"specialConstructionCharge\":null,\"specialConstructionHandling\":null,\"specialConstructionHandlingNotes\":null,\"_endPointRef\":null}";
		
		mappingData = new NxMpConfigMapping();
		inputDesignDetails = JacksonUtil.toJsonObject(data);
		methodParam = new HashMap<String,Object>();
	}
	
	@Test
	public void testGetInputDesignDetailsASE() {
		String json = "{\"solution\":{\"solutionDeterminants\":{\"icbInd\":null,\"solutionType\":\"NS\",\"mvlInd\":null},\"bulkInd\":\"N\",\"bundleCode\":\"ASE\",\"offers\":[{\"site\":[],\"offerId\":\"103\"}],\"leadDesignID\":4434348,\"marketStrata\":\"Retail\",\"cancellationReason\":null,\"pricerDSolutionId\":4434348,\"automationInd\":\"N\",\"erateInd\":\"N\",\"layer\":\"Retail\",\"solutionStatus\":\"N\"},\"actionDeterminants\":[{\"component\":[\"Design\",\"Price\",\"ASE\"],\"activity\":\"UpdateDesign\"}]}";
		nxDesign.getNxDesignDetails().get(0).setDesignData(data);
		NxDesignAudit audit = new NxDesignAudit();
		audit.setData(json);
		Mockito.when(nxDesignAuditRepository.findByNxRefIdAndTransaction(anyLong(), anyString())).thenReturn(audit);
		Mockito.when(nexxusJsonUtility.getValue(any(), anyString())).thenReturn("Retail");
		NxLookupData lookupData = new NxLookupData();
		lookupData.setDescription("Retail");
		Mockito.when(nxLookupDataRepository.findTopByDatasetNameAndItemId(anyString(), anyString())).thenReturn(lookupData);
		Mockito.when(jsonPathUtil.set(any(), anyString(), anyString(), anyBoolean())).thenReturn(json);
		configAndUpdatePricingUtil.getInputDesignDetails(nxDesign, MyPriceConstants.ASE_OFFER_NAME);
	}
	
	@Test
	public void testGetInputDesignDetailsADE() {
		String json = "{\"solution\":{\"solutionDeterminants\":{\"icbInd\":null,\"solutionType\":\"NS\",\"mvlInd\":null},\"bulkInd\":\"N\",\"bundleCode\":\"ADE\",\"offers\":[{\"site\":[],\"offerId\":\"120\"}],\"leadDesignID\":6666655,\"marketStrata\":\"Retail\",\"cancellationReason\":null,\"pricerDSolutionId\":6666655,\"automationInd\":\"N\",\"erateInd\":\"N\",\"layer\":\"Retail\",\"solutionStatus\":\"U\"},\"actionDeterminants\":[{\"component\":[\"Design\",\"Price\",\"ADE\"],\"activity\":\"UpdateDesign\"}]}";
		nxDesign.getNxDesignDetails().get(0).setDesignData("{\"typeOfInventory\":null,\"component\":[{\"componentCodeId\":1210,\"fromInvYN\":null,\"logicalChannelPvcID\":null,\"eteVpnKey\":null,\"diversityGroupId\":null,\"componentCodeType\":\"Circuit\",\"componentId\":30959,\"externalField\":null,\"parentComponentId\":null,\"externalKeyRef\":null,\"mvlInd\":null,\"userEnteredVpn\":null,\"siteId\":null,\"logicalChannelId\":null,\"designDetails\":[{\"udfAttributeId\":[],\"udfId\":200188,\"readOnly\":null,\"udfAttributeText\":[\"OMGS\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[30604],\"udfId\":200190,\"readOnly\":null,\"udfAttributeText\":[\"No\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[],\"udfId\":200189,\"readOnly\":null,\"udfAttributeText\":[\"100\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[],\"udfId\":20186,\"readOnly\":null,\"udfAttributeText\":[\"OM--\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[343047],\"udfId\":21085,\"readOnly\":null,\"udfAttributeText\":[\"Interstate/Intrastate USOC\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[],\"udfId\":200118,\"readOnly\":null,\"udfAttributeText\":[\"FL\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[],\"udfId\":200162,\"readOnly\":null,\"udfAttributeText\":[\"AD1030944\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[301770],\"udfId\":200163,\"readOnly\":null,\"udfAttributeText\":[\"Add\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[301773],\"udfId\":200164,\"readOnly\":null,\"udfAttributeText\":[\"Newstart Circuit\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[30604],\"udfId\":200167,\"readOnly\":null,\"udfAttributeText\":[\"No\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[30604],\"udfId\":200168,\"readOnly\":null,\"udfAttributeText\":[\"No\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[301785],\"udfId\":200169,\"readOnly\":null,\"udfAttributeText\":[\"24 Months\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[30604],\"udfId\":200170,\"readOnly\":null,\"udfAttributeText\":[\"No\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[],\"udfId\":200172,\"readOnly\":null,\"udfAttributeText\":[\"1\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[301797],\"udfId\":200174,\"readOnly\":null,\"udfAttributeText\":[\"OTU4 (100 Gbps)\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[301820],\"udfId\":200176,\"readOnly\":null,\"udfAttributeText\":[\"Interstate (FCC) Access (Interstate)\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[30604],\"udfId\":200180,\"readOnly\":null,\"udfAttributeText\":[\"No\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[30604],\"udfId\":200184,\"readOnly\":null,\"udfAttributeText\":[\"No\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[30604],\"udfId\":200185,\"readOnly\":null,\"udfAttributeText\":[\"No\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[301845],\"udfId\":200216,\"readOnly\":null,\"udfAttributeText\":[\"Y\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[],\"udfId\":200198,\"readOnly\":null,\"udfAttributeText\":[\"1\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[],\"udfId\":20781,\"readOnly\":null,\"udfAttributeText\":[\"0\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[302216],\"udfId\":200193,\"readOnly\":null,\"udfAttributeText\":[\"G\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[],\"udfId\":200192,\"readOnly\":null,\"udfAttributeText\":[\"37745\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null}],\"routeTargets\":null,\"references\":[],\"siteObj\":null},{\"componentCodeId\":1220,\"fromInvYN\":null,\"logicalChannelPvcID\":null,\"eteVpnKey\":null,\"diversityGroupId\":null,\"componentCodeType\":\"Endpoint\",\"componentId\":47089,\"externalField\":null,\"parentComponentId\":30959,\"externalKeyRef\":null,\"mvlInd\":null,\"userEnteredVpn\":null,\"siteId\":null,\"logicalChannelId\":null,\"designDetails\":[{\"udfAttributeId\":[343047],\"udfId\":21085,\"readOnly\":null,\"udfAttributeText\":[\"Interstate/Intrastate USOC\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[],\"udfId\":20187,\"readOnly\":null,\"udfAttributeText\":[\"02OTF.403\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[],\"udfId\":200118,\"readOnly\":null,\"udfAttributeText\":[\"FL\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[301797],\"udfId\":200174,\"readOnly\":null,\"udfAttributeText\":[\"OTU4 (100 Gbps)\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[301806],\"udfId\":200175,\"readOnly\":null,\"udfAttributeText\":[\"IR / IF End-User Customer\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[301823],\"udfId\":200178,\"readOnly\":null,\"udfAttributeText\":[\"Unprotected\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[301842],\"udfId\":200179,\"readOnly\":null,\"udfAttributeText\":[\"1310 - ER4 Single Mode Fiber (SMF)\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[301848],\"udfId\":200205,\"readOnly\":null,\"udfAttributeText\":[\"IF\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[304240],\"udfId\":200208,\"readOnly\":null,\"udfAttributeText\":[null],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[30604],\"udfId\":200212,\"readOnly\":null,\"udfAttributeText\":[\"No\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[30604],\"udfId\":200213,\"readOnly\":null,\"udfAttributeText\":[\"No\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[301845],\"udfId\":200216,\"readOnly\":null,\"udfAttributeText\":[\"Y\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[301890],\"udfId\":200217,\"readOnly\":null,\"udfAttributeText\":[\"End-User Customer\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[30604],\"udfId\":200218,\"readOnly\":null,\"udfAttributeText\":[\"No\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[],\"udfId\":21033,\"readOnly\":null,\"udfAttributeText\":[\"A\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[605334],\"udfId\":21960,\"readOnly\":null,\"udfAttributeText\":[],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[],\"udfId\":21961,\"readOnly\":null,\"udfAttributeText\":[\"0.31\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[301476],\"udfId\":21962,\"readOnly\":null,\"udfAttributeText\":[],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[],\"udfId\":1000360,\"readOnly\":null,\"udfAttributeText\":[\"Tier 1\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[],\"udfId\":21036,\"readOnly\":null,\"udfAttributeText\":[\"GREEN\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[],\"udfId\":200160,\"readOnly\":null,\"udfAttributeText\":[\"ORLDFLLCH09\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[],\"udfId\":200045,\"readOnly\":null,\"udfAttributeText\":[\"ORLDFLMA\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[],\"udfId\":200192,\"readOnly\":null,\"udfAttributeText\":[\"37745\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[301820],\"udfId\":200176,\"readOnly\":null,\"udfAttributeText\":[\"Interstate (FCC) Access (Interstate)\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[301785],\"udfId\":200169,\"readOnly\":null,\"udfAttributeText\":[\"24 Months\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null}],\"routeTargets\":null,\"references\":[{\"referenceType\":\"Site\",\"referenceId\":99988939546}],\"siteObj\":{\"nxSiteId\":455870,\"siteId\":99988939546,\"npanxx\":\"990088\",\"address1\":\"315 E ROBINSON ST\",\"city\":\"ORL\",\"state\":\"FL\",\"saLecName\":null,\"speedId\":null,\"saLecSwClli\":null,\"popClli\":null,\"country\":\"US\",\"macdType\":null,\"macdActivity\":null,\"fromInventory\":null,\"dualSiteId\":null,\"onNetCheck\":null,\"ethernetVendor\":null,\"designSiteOfferPort\":null,\"priceDetails\":null,\"address\":null,\"postalCode\":null,\"siteName\":\"L1Z\",\"swcClli\":\"ORLDFLMA\",\"customerLocationClli\":\"ORLDFLLCH09\",\"active\":null,\"emc\":null,\"carrierHotel\":null,\"attComments\":null,\"newBuilding\":null,\"customerReference\":null,\"asrItemId\":null,\"lataCode\":\"458\",\"zipCode\":\"32802\",\"address2\":null,\"room\":null,\"floor\":null,\"building\":null,\"siteComment\":null,\"lconFirstName\":null,\"lconLastName\":null,\"lconPhone\":null,\"lconEmail\":null,\"endPointSiteIdentifier\":\"A\",\"buildingClli\":null,\"regionCode\":\"Y\",\"activityType\":null,\"cancellationReason\":null,\"product\":null,\"quantity\":null,\"nssEngagement\":null,\"designStatus\":null,\"multiGigeIndicator\":null,\"alias\":\"L1Z\",\"macdActionType\":null,\"lconDetails\":[{\"lconType\":null,\"lconFirstName\":null,\"lconLastName\":null,\"lconPhone\":null,\"lconEmail\":null}],\"globalLocationId\":null,\"jurisdiction\":null,\"certificationStatus\":null,\"designModifiedInd\":null,\"independentCarrierCompanyLATA\":null,\"independentCarrierCompanyName\":null,\"interDepartmentMeetPointChecklistURL\":null,\"diverseFromReferenceInfo\":null,\"opticalTerminatingNetworkCarrierFacilityAssignment\":null,\"collocationCarrierFacilityAssignment\":null,\"assetInvestmentSheetIndicator\":null,\"swcCertification\":null,\"designVersion\":null,\"specialConstructionPaymentUrl\":null,\"thirdPartyInd\":null,\"accessCarrierNameAbbreviation\":null,\"design\":null,\"loopLength\":null,\"numOfCopperRepeaters\":null,\"inventoryNumOfPairs\":null,\"taskClli\":null,\"numberRemoteTerminals\":null,\"referenceOfferId\":null,\"siteNpanxx\":null,\"specialConstructionCharge\":null,\"specialConstructionHandling\":null,\"specialConstructionHandlingNotes\":null,\"_endPointRef\":21033}},{\"componentCodeId\":1220,\"fromInvYN\":null,\"logicalChannelPvcID\":null,\"eteVpnKey\":null,\"diversityGroupId\":null,\"componentCodeType\":\"Endpoint\",\"componentId\":47090,\"externalField\":null,\"parentComponentId\":30959,\"externalKeyRef\":null,\"mvlInd\":null,\"userEnteredVpn\":null,\"siteId\":null,\"logicalChannelId\":null,\"designDetails\":[{\"udfAttributeId\":[343047],\"udfId\":21085,\"readOnly\":null,\"udfAttributeText\":[\"Interstate/Intrastate USOC\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[],\"udfId\":20187,\"readOnly\":null,\"udfAttributeText\":[\"02OTF.403\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[],\"udfId\":200118,\"readOnly\":null,\"udfAttributeText\":[\"FL\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[301797],\"udfId\":200174,\"readOnly\":null,\"udfAttributeText\":[\"OTU4 (100 Gbps)\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[301806],\"udfId\":200175,\"readOnly\":null,\"udfAttributeText\":[\"IR / IF End-User Customer\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[301823],\"udfId\":200178,\"readOnly\":null,\"udfAttributeText\":[\"Unprotected\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[301842],\"udfId\":200179,\"readOnly\":null,\"udfAttributeText\":[\"1310 - ER4 Single Mode Fiber (SMF)\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[301848],\"udfId\":200205,\"readOnly\":null,\"udfAttributeText\":[\"IF\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[304240],\"udfId\":200208,\"readOnly\":null,\"udfAttributeText\":[null],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[30604],\"udfId\":200212,\"readOnly\":null,\"udfAttributeText\":[\"No\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[30604],\"udfId\":200213,\"readOnly\":null,\"udfAttributeText\":[\"No\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[301845],\"udfId\":200216,\"readOnly\":null,\"udfAttributeText\":[\"Y\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[301890],\"udfId\":200217,\"readOnly\":null,\"udfAttributeText\":[\"End-User Customer\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[30604],\"udfId\":200218,\"readOnly\":null,\"udfAttributeText\":[\"No\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[],\"udfId\":21034,\"readOnly\":null,\"udfAttributeText\":[\"Z\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[605334],\"udfId\":21960,\"readOnly\":null,\"udfAttributeText\":[],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[],\"udfId\":21961,\"readOnly\":null,\"udfAttributeText\":[\"0.01\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[301476],\"udfId\":21962,\"readOnly\":null,\"udfAttributeText\":[],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[],\"udfId\":1000360,\"readOnly\":null,\"udfAttributeText\":[\"Tier 1\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[],\"udfId\":21037,\"readOnly\":null,\"udfAttributeText\":[\"GREEN\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[],\"udfId\":200160,\"readOnly\":null,\"udfAttributeText\":[\"ORLDFLLCH09\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[],\"udfId\":200045,\"readOnly\":null,\"udfAttributeText\":[\"ORLDFLMA\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[],\"udfId\":200192,\"readOnly\":null,\"udfAttributeText\":[\"37745\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[301820],\"udfId\":200176,\"readOnly\":null,\"udfAttributeText\":[\"Interstate (FCC) Access (Interstate)\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[301785],\"udfId\":200169,\"readOnly\":null,\"udfAttributeText\":[\"24 Months\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null}],\"routeTargets\":null,\"references\":[{\"referenceType\":\"Site\",\"referenceId\":99988939548}],\"siteObj\":{\"nxSiteId\":455869,\"siteId\":99988939548,\"npanxx\":\"990088\",\"address1\":\"45 N MAGNOLIA AV\",\"city\":\"ORL\",\"state\":\"FL\",\"saLecName\":null,\"speedId\":null,\"saLecSwClli\":null,\"popClli\":null,\"country\":\"US\",\"macdType\":\"Add\",\"macdActivity\":\"Newstart Circuit\",\"fromInventory\":null,\"dualSiteId\":null,\"onNetCheck\":null,\"ethernetVendor\":null,\"designSiteOfferPort\":null,\"priceDetails\":null,\"address\":null,\"postalCode\":null,\"siteName\":\"L 1A\",\"swcClli\":\"ORLDFLMA\",\"customerLocationClli\":\"ORLDFLLCH09\",\"active\":null,\"emc\":null,\"carrierHotel\":null,\"attComments\":null,\"newBuilding\":null,\"customerReference\":null,\"asrItemId\":\"AD1030944\",\"lataCode\":\"458\",\"zipCode\":\"32802\",\"address2\":null,\"room\":null,\"floor\":null,\"building\":null,\"siteComment\":null,\"lconFirstName\":null,\"lconLastName\":null,\"lconPhone\":null,\"lconEmail\":null,\"endPointSiteIdentifier\":\"Z\",\"buildingClli\":null,\"regionCode\":\"Y\",\"activityType\":\"New Request\",\"cancellationReason\":null,\"product\":null,\"quantity\":\"1\",\"nssEngagement\":null,\"designStatus\":\"U\",\"multiGigeIndicator\":null,\"alias\":\"L 1A\",\"macdActionType\":null,\"lconDetails\":[{\"lconType\":null,\"lconFirstName\":null,\"lconLastName\":null,\"lconPhone\":null,\"lconEmail\":null}],\"globalLocationId\":\"00000L4Z5R\",\"jurisdiction\":\"Interstate (FCC) Access (Interstate)\",\"certificationStatus\":null,\"designModifiedInd\":null,\"independentCarrierCompanyLATA\":null,\"independentCarrierCompanyName\":null,\"interDepartmentMeetPointChecklistURL\":null,\"diverseFromReferenceInfo\":null,\"opticalTerminatingNetworkCarrierFacilityAssignment\":null,\"collocationCarrierFacilityAssignment\":null,\"assetInvestmentSheetIndicator\":null,\"swcCertification\":\"Certified\",\"designVersion\":\"1\",\"specialConstructionPaymentUrl\":null,\"thirdPartyInd\":null,\"accessCarrierNameAbbreviation\":null,\"design\":null,\"loopLength\":null,\"numOfCopperRepeaters\":null,\"inventoryNumOfPairs\":null,\"taskClli\":null,\"numberRemoteTerminals\":null,\"referenceOfferId\":null,\"siteNpanxx\":null,\"specialConstructionCharge\":null,\"specialConstructionHandling\":null,\"specialConstructionHandlingNotes\":null,\"_endPointRef\":21034}}],\"designStatus\":\"U\",\"cancellationReason\":null,\"priceDetails\":{\"componentDetails\":[{\"componentCodeId\":null,\"componentCodeType\":null,\"componentId\":30959,\"componentType\":\"Circuit\",\"componentParentId\":6900237,\"componentAttributes\":[],\"priceAttributes\":[{\"productRateId\":0,\"beid\":\"EYXEX-NRBCL\",\"rateDescription\":null,\"priceCatalog\":\"Standard Pricing\",\"localListPrice\":600.0,\"targetListPrice\":600.0,\"icbDesiredDiscPerc\":null,\"priceType\":\"CIRCUIT\",\"priceUnit\":\"Design Central Office Connection Charge - Per Circuit\",\"frequency\":\"NRC\",\"monthlySurcharge\":null,\"discount\":null,\"discountId\":null,\"quantity\":\"1\",\"localNetPrice\":600.0,\"targetNetPrice\":600.0,\"localTotalPrice\":600.0,\"targetTotalPrice\":null,\"localCurrency\":\"USD\",\"targetCurrency\":\"USD\",\"rdsPriceType\":null,\"priceName\":\"Design Central Office Connection Charge  - NRC\",\"typeOfInventory\":null,\"priceInUSD\":\"600\",\"priceScenarioId\":99999905738,\"rateGroup\":\"Design Central Office Connection Charge\",\"externalBillingSystem\":null,\"ratePlanId\":null,\"priceCompType\":null,\"pvcId\":null,\"chargeCodeId\":null,\"requestedNRCRate\":null,\"requestedNRCDiscPercentage\":null,\"requestedMRCRate\":null,\"requestedMRCDiscPercentage\":null,\"priceModifiedInd\":\"Y\",\"requestedDiscount\":null,\"requestedRate\":null,\"term\":24,\"priceGroup\":null,\"referencePortId\":null,\"componentType\":null,\"componentParentId\":null,\"country\":null,\"secondaryKeys\":null,\"lineItemId\":null,\"nrcBeid\":null,\"uniqueId\":null,\"elementType\":null,\"reqPriceType\":null},{\"productRateId\":0,\"beid\":\"EYXEX-NRBCL\",\"rateDescription\":null,\"priceCatalog\":\"Standard Pricing\",\"localListPrice\":600.0,\"targetListPrice\":600.0,\"icbDesiredDiscPerc\":null,\"priceType\":\"CIRCUIT\",\"priceUnit\":\"Design Central Office Connection Charge - Per Circuit\",\"frequency\":\"NRC\",\"monthlySurcharge\":null,\"discount\":null,\"discountId\":null,\"quantity\":\"1\",\"localNetPrice\":600.0,\"targetNetPrice\":600.0,\"localTotalPrice\":600.0,\"targetTotalPrice\":null,\"localCurrency\":\"USD\",\"targetCurrency\":\"USD\",\"rdsPriceType\":null,\"priceName\":\"Design Central Office Connection Charge  - NRC\",\"typeOfInventory\":null,\"priceInUSD\":\"600\",\"priceScenarioId\":99999905737,\"rateGroup\":\"Design Central Office Connection Charge\",\"externalBillingSystem\":null,\"ratePlanId\":null,\"priceCompType\":null,\"pvcId\":null,\"chargeCodeId\":null,\"requestedNRCRate\":null,\"requestedNRCDiscPercentage\":null,\"requestedMRCRate\":null,\"requestedMRCDiscPercentage\":null,\"priceModifiedInd\":null,\"requestedDiscount\":null,\"requestedRate\":null,\"term\":24,\"priceGroup\":null,\"referencePortId\":null,\"componentType\":null,\"componentParentId\":null,\"country\":null,\"secondaryKeys\":null,\"lineItemId\":null,\"nrcBeid\":null,\"uniqueId\":null,\"elementType\":null,\"reqPriceType\":null},{\"productRateId\":0,\"beid\":\"EYXEX-NRBCL\",\"rateDescription\":null,\"priceCatalog\":\"Standard Pricing\",\"localListPrice\":600.0,\"targetListPrice\":600.0,\"icbDesiredDiscPerc\":null,\"priceType\":\"CIRCUIT\",\"priceUnit\":\"Design Central Office Connection Charge - Per Circuit\",\"frequency\":\"NRC\",\"monthlySurcharge\":null,\"discount\":null,\"discountId\":null,\"quantity\":\"1\",\"localNetPrice\":600.0,\"targetNetPrice\":600.0,\"localTotalPrice\":600.0,\"targetTotalPrice\":null,\"localCurrency\":\"USD\",\"targetCurrency\":\"USD\",\"rdsPriceType\":null,\"priceName\":\"Design Central Office Connection Charge  - NRC\",\"typeOfInventory\":null,\"priceInUSD\":\"600\",\"priceScenarioId\":99999905736,\"rateGroup\":\"Design Central Office Connection Charge\",\"externalBillingSystem\":null,\"ratePlanId\":null,\"priceCompType\":null,\"pvcId\":null,\"chargeCodeId\":null,\"requestedNRCRate\":null,\"requestedNRCDiscPercentage\":null,\"requestedMRCRate\":null,\"requestedMRCDiscPercentage\":null,\"priceModifiedInd\":null,\"requestedDiscount\":null,\"requestedRate\":null,\"term\":24,\"priceGroup\":null,\"referencePortId\":null,\"componentType\":null,\"componentParentId\":null,\"country\":null,\"secondaryKeys\":null,\"lineItemId\":null,\"nrcBeid\":null,\"uniqueId\":null,\"elementType\":null,\"reqPriceType\":null},{\"productRateId\":0,\"beid\":\"EYXEX-ORCMX\",\"rateDescription\":null,\"priceCatalog\":\"Standard Pricing\",\"localListPrice\":60.0,\"targetListPrice\":60.0,\"icbDesiredDiscPerc\":null,\"priceType\":\"CIRCUIT\",\"priceUnit\":\"Administrative Charge - Per Order\",\"frequency\":\"NRC\",\"monthlySurcharge\":null,\"discount\":null,\"discountId\":null,\"quantity\":\"1\",\"localNetPrice\":60.0,\"targetNetPrice\":60.0,\"localTotalPrice\":60.0,\"targetTotalPrice\":null,\"localCurrency\":\"USD\",\"targetCurrency\":\"USD\",\"rdsPriceType\":null,\"priceName\":\"Administrative Charge  - NRC\",\"typeOfInventory\":null,\"priceInUSD\":\"60\",\"priceScenarioId\":99999905738,\"rateGroup\":\"Administrative Charge\",\"externalBillingSystem\":null,\"ratePlanId\":null,\"priceCompType\":null,\"pvcId\":null,\"chargeCodeId\":null,\"requestedNRCRate\":null,\"requestedNRCDiscPercentage\":null,\"requestedMRCRate\":null,\"requestedMRCDiscPercentage\":null,\"priceModifiedInd\":\"Y\",\"requestedDiscount\":null,\"requestedRate\":null,\"term\":24,\"priceGroup\":null,\"referencePortId\":null,\"componentType\":null,\"componentParentId\":null,\"country\":null,\"secondaryKeys\":null,\"lineItemId\":null,\"nrcBeid\":null,\"uniqueId\":null,\"elementType\":null,\"reqPriceType\":null},{\"productRateId\":0,\"beid\":\"EYXEX-ORCMX\",\"rateDescription\":null,\"priceCatalog\":\"Standard Pricing\",\"localListPrice\":60.0,\"targetListPrice\":60.0,\"icbDesiredDiscPerc\":null,\"priceType\":\"CIRCUIT\",\"priceUnit\":\"Administrative Charge - Per Order\",\"frequency\":\"NRC\",\"monthlySurcharge\":null,\"discount\":null,\"discountId\":null,\"quantity\":\"1\",\"localNetPrice\":60.0,\"targetNetPrice\":60.0,\"localTotalPrice\":60.0,\"targetTotalPrice\":null,\"localCurrency\":\"USD\",\"targetCurrency\":\"USD\",\"rdsPriceType\":null,\"priceName\":\"Administrative Charge  - NRC\",\"typeOfInventory\":null,\"priceInUSD\":\"60\",\"priceScenarioId\":99999905737,\"rateGroup\":\"Administrative Charge\",\"externalBillingSystem\":null,\"ratePlanId\":null,\"priceCompType\":null,\"pvcId\":null,\"chargeCodeId\":null,\"requestedNRCRate\":null,\"requestedNRCDiscPercentage\":null,\"requestedMRCRate\":null,\"requestedMRCDiscPercentage\":null,\"priceModifiedInd\":null,\"requestedDiscount\":null,\"requestedRate\":null,\"term\":24,\"priceGroup\":null,\"referencePortId\":null,\"componentType\":null,\"componentParentId\":null,\"country\":null,\"secondaryKeys\":null,\"lineItemId\":null,\"nrcBeid\":null,\"uniqueId\":null,\"elementType\":null,\"reqPriceType\":null},{\"productRateId\":0,\"beid\":\"EYXEX-ORCMX\",\"rateDescription\":null,\"priceCatalog\":\"Standard Pricing\",\"localListPrice\":60.0,\"targetListPrice\":60.0,\"icbDesiredDiscPerc\":null,\"priceType\":\"CIRCUIT\",\"priceUnit\":\"Administrative Charge - Per Order\",\"frequency\":\"NRC\",\"monthlySurcharge\":null,\"discount\":null,\"discountId\":null,\"quantity\":\"1\",\"localNetPrice\":60.0,\"targetNetPrice\":60.0,\"localTotalPrice\":60.0,\"targetTotalPrice\":null,\"localCurrency\":\"USD\",\"targetCurrency\":\"USD\",\"rdsPriceType\":null,\"priceName\":\"Administrative Charge  - NRC\",\"typeOfInventory\":null,\"priceInUSD\":\"60\",\"priceScenarioId\":99999905736,\"rateGroup\":\"Administrative Charge\",\"externalBillingSystem\":null,\"ratePlanId\":null,\"priceCompType\":null,\"pvcId\":null,\"chargeCodeId\":null,\"requestedNRCRate\":null,\"requestedNRCDiscPercentage\":null,\"requestedMRCRate\":null,\"requestedMRCDiscPercentage\":null,\"priceModifiedInd\":null,\"requestedDiscount\":null,\"requestedRate\":null,\"term\":24,\"priceGroup\":null,\"referencePortId\":null,\"componentType\":null,\"componentParentId\":null,\"country\":null,\"secondaryKeys\":null,\"lineItemId\":null,\"nrcBeid\":null,\"uniqueId\":null,\"elementType\":null,\"reqPriceType\":null}],\"scpPriceMessages\":null},{\"componentCodeId\":null,\"componentCodeType\":null,\"componentId\":47089,\"componentType\":\"Endpoint\",\"componentParentId\":30959,\"componentAttributes\":[],\"priceAttributes\":[{\"productRateId\":0,\"beid\":\"EYXEX-EYFOX\",\"rateDescription\":null,\"priceCatalog\":\"Standard Pricing\",\"localListPrice\":42350.0,\"targetListPrice\":42350.0,\"icbDesiredDiscPerc\":null,\"priceType\":\"PORT_CONNECTION\",\"priceUnit\":\"Port Connection\",\"frequency\":\"MRC\",\"monthlySurcharge\":null,\"discount\":null,\"discountId\":null,\"quantity\":\"1\",\"localNetPrice\":42350.0,\"targetNetPrice\":42350.0,\"localTotalPrice\":42350.0,\"targetTotalPrice\":null,\"localCurrency\":\"USD\",\"targetCurrency\":\"USD\",\"rdsPriceType\":null,\"priceName\":\"Port Connection Type - OTU4 (100 Gbps) - 24M\",\"typeOfInventory\":null,\"priceInUSD\":\"42350\",\"priceScenarioId\":99999905738,\"rateGroup\":\"Port Connection\",\"externalBillingSystem\":null,\"ratePlanId\":null,\"priceCompType\":null,\"pvcId\":null,\"chargeCodeId\":null,\"requestedNRCRate\":null,\"requestedNRCDiscPercentage\":null,\"requestedMRCRate\":null,\"requestedMRCDiscPercentage\":null,\"priceModifiedInd\":\"Y\",\"requestedDiscount\":null,\"requestedRate\":null,\"term\":24,\"priceGroup\":null,\"referencePortId\":null,\"componentType\":null,\"componentParentId\":null,\"country\":null,\"secondaryKeys\":null,\"lineItemId\":null,\"nrcBeid\":null,\"uniqueId\":null,\"elementType\":null,\"reqPriceType\":null},{\"productRateId\":0,\"beid\":\"EYXEX-EYFOX\",\"rateDescription\":null,\"priceCatalog\":\"Standard Pricing\",\"localListPrice\":42350.0,\"targetListPrice\":42350.0,\"icbDesiredDiscPerc\":null,\"priceType\":\"PORT_CONNECTION\",\"priceUnit\":\"Port Connection\",\"frequency\":\"MRC\",\"monthlySurcharge\":null,\"discount\":null,\"discountId\":null,\"quantity\":\"1\",\"localNetPrice\":42350.0,\"targetNetPrice\":42350.0,\"localTotalPrice\":42350.0,\"targetTotalPrice\":null,\"localCurrency\":\"USD\",\"targetCurrency\":\"USD\",\"rdsPriceType\":null,\"priceName\":\"Port Connection Type - OTU4 (100 Gbps) - 24M\",\"typeOfInventory\":null,\"priceInUSD\":\"42350\",\"priceScenarioId\":99999905737,\"rateGroup\":\"Port Connection\",\"externalBillingSystem\":null,\"ratePlanId\":null,\"priceCompType\":null,\"pvcId\":null,\"chargeCodeId\":null,\"requestedNRCRate\":null,\"requestedNRCDiscPercentage\":null,\"requestedMRCRate\":null,\"requestedMRCDiscPercentage\":null,\"priceModifiedInd\":null,\"requestedDiscount\":null,\"requestedRate\":null,\"term\":24,\"priceGroup\":null,\"referencePortId\":null,\"componentType\":null,\"componentParentId\":null,\"country\":null,\"secondaryKeys\":null,\"lineItemId\":null,\"nrcBeid\":null,\"uniqueId\":null,\"elementType\":null,\"reqPriceType\":null},{\"productRateId\":0,\"beid\":\"EYXEX-EYFOX\",\"rateDescription\":null,\"priceCatalog\":\"Standard Pricing\",\"localListPrice\":42350.0,\"targetListPrice\":42350.0,\"icbDesiredDiscPerc\":null,\"priceType\":\"PORT_CONNECTION\",\"priceUnit\":\"Port Connection\",\"frequency\":\"MRC\",\"monthlySurcharge\":null,\"discount\":null,\"discountId\":null,\"quantity\":\"1\",\"localNetPrice\":42350.0,\"targetNetPrice\":42350.0,\"localTotalPrice\":42350.0,\"targetTotalPrice\":null,\"localCurrency\":\"USD\",\"targetCurrency\":\"USD\",\"rdsPriceType\":null,\"priceName\":\"Port Connection Type - OTU4 (100 Gbps) - 24M\",\"typeOfInventory\":null,\"priceInUSD\":\"42350\",\"priceScenarioId\":99999905736,\"rateGroup\":\"Port Connection\",\"externalBillingSystem\":null,\"ratePlanId\":null,\"priceCompType\":null,\"pvcId\":null,\"chargeCodeId\":null,\"requestedNRCRate\":null,\"requestedNRCDiscPercentage\":null,\"requestedMRCRate\":null,\"requestedMRCDiscPercentage\":null,\"priceModifiedInd\":null,\"requestedDiscount\":null,\"requestedRate\":null,\"term\":24,\"priceGroup\":null,\"referencePortId\":null,\"componentType\":null,\"componentParentId\":null,\"country\":null,\"secondaryKeys\":null,\"lineItemId\":null,\"nrcBeid\":null,\"uniqueId\":null,\"elementType\":null,\"reqPriceType\":null},{\"productRateId\":0,\"beid\":\"EYXEX-NRBBL\",\"rateDescription\":null,\"priceCatalog\":\"Standard Pricing\",\"localListPrice\":1500.0,\"targetListPrice\":1500.0,\"icbDesiredDiscPerc\":null,\"priceType\":\"PORT_CONNECTION\",\"priceUnit\":\"Customer Connection Charge\",\"frequency\":\"NRC\",\"monthlySurcharge\":null,\"discount\":null,\"discountId\":null,\"quantity\":\"1\",\"localNetPrice\":1500.0,\"targetNetPrice\":1500.0,\"localTotalPrice\":1500.0,\"targetTotalPrice\":null,\"localCurrency\":\"USD\",\"targetCurrency\":\"USD\",\"rdsPriceType\":null,\"priceName\":\"Customer Connection Charge  - NRC\",\"typeOfInventory\":null,\"priceInUSD\":\"1500\",\"priceScenarioId\":99999905738,\"rateGroup\":\"Customer Connection Charge\",\"externalBillingSystem\":null,\"ratePlanId\":null,\"priceCompType\":null,\"pvcId\":null,\"chargeCodeId\":null,\"requestedNRCRate\":null,\"requestedNRCDiscPercentage\":null,\"requestedMRCRate\":null,\"requestedMRCDiscPercentage\":null,\"priceModifiedInd\":\"Y\",\"requestedDiscount\":null,\"requestedRate\":null,\"term\":24,\"priceGroup\":null,\"referencePortId\":null,\"componentType\":null,\"componentParentId\":null,\"country\":null,\"secondaryKeys\":null,\"lineItemId\":null,\"nrcBeid\":null,\"uniqueId\":null,\"elementType\":null,\"reqPriceType\":null},{\"productRateId\":0,\"beid\":\"EYXEX-NRBBL\",\"rateDescription\":null,\"priceCatalog\":\"Standard Pricing\",\"localListPrice\":1500.0,\"targetListPrice\":1500.0,\"icbDesiredDiscPerc\":null,\"priceType\":\"PORT_CONNECTION\",\"priceUnit\":\"Customer Connection Charge\",\"frequency\":\"NRC\",\"monthlySurcharge\":null,\"discount\":null,\"discountId\":null,\"quantity\":\"1\",\"localNetPrice\":1500.0,\"targetNetPrice\":1500.0,\"localTotalPrice\":1500.0,\"targetTotalPrice\":null,\"localCurrency\":\"USD\",\"targetCurrency\":\"USD\",\"rdsPriceType\":null,\"priceName\":\"Customer Connection Charge  - NRC\",\"typeOfInventory\":null,\"priceInUSD\":\"1500\",\"priceScenarioId\":99999905737,\"rateGroup\":\"Customer Connection Charge\",\"externalBillingSystem\":null,\"ratePlanId\":null,\"priceCompType\":null,\"pvcId\":null,\"chargeCodeId\":null,\"requestedNRCRate\":null,\"requestedNRCDiscPercentage\":null,\"requestedMRCRate\":null,\"requestedMRCDiscPercentage\":null,\"priceModifiedInd\":null,\"requestedDiscount\":null,\"requestedRate\":null,\"term\":24,\"priceGroup\":null,\"referencePortId\":null,\"componentType\":null,\"componentParentId\":null,\"country\":null,\"secondaryKeys\":null,\"lineItemId\":null,\"nrcBeid\":null,\"uniqueId\":null,\"elementType\":null,\"reqPriceType\":null},{\"productRateId\":0,\"beid\":\"EYXEX-NRBBL\",\"rateDescription\":null,\"priceCatalog\":\"Standard Pricing\",\"localListPrice\":1500.0,\"targetListPrice\":1500.0,\"icbDesiredDiscPerc\":null,\"priceType\":\"PORT_CONNECTION\",\"priceUnit\":\"Customer Connection Charge\",\"frequency\":\"NRC\",\"monthlySurcharge\":null,\"discount\":null,\"discountId\":null,\"quantity\":\"1\",\"localNetPrice\":1500.0,\"targetNetPrice\":1500.0,\"localTotalPrice\":1500.0,\"targetTotalPrice\":null,\"localCurrency\":\"USD\",\"targetCurrency\":\"USD\",\"rdsPriceType\":null,\"priceName\":\"Customer Connection Charge  - NRC\",\"typeOfInventory\":null,\"priceInUSD\":\"1500\",\"priceScenarioId\":99999905736,\"rateGroup\":\"Customer Connection Charge\",\"externalBillingSystem\":null,\"ratePlanId\":null,\"priceCompType\":null,\"pvcId\":null,\"chargeCodeId\":null,\"requestedNRCRate\":null,\"requestedNRCDiscPercentage\":null,\"requestedMRCRate\":null,\"requestedMRCDiscPercentage\":null,\"priceModifiedInd\":null,\"requestedDiscount\":null,\"requestedRate\":null,\"term\":24,\"priceGroup\":null,\"referencePortId\":null,\"componentType\":null,\"componentParentId\":null,\"country\":null,\"secondaryKeys\":null,\"lineItemId\":null,\"nrcBeid\":null,\"uniqueId\":null,\"elementType\":null,\"reqPriceType\":null}],\"scpPriceMessages\":null},{\"componentCodeId\":null,\"componentCodeType\":null,\"componentId\":47090,\"componentType\":\"Endpoint\",\"componentParentId\":30959,\"componentAttributes\":[],\"priceAttributes\":[{\"productRateId\":0,\"beid\":\"EYXEX-EYFOX\",\"rateDescription\":null,\"priceCatalog\":\"Standard Pricing\",\"localListPrice\":42350.0,\"targetListPrice\":42350.0,\"icbDesiredDiscPerc\":null,\"priceType\":\"PORT_CONNECTION\",\"priceUnit\":\"Port Connection\",\"frequency\":\"MRC\",\"monthlySurcharge\":null,\"discount\":null,\"discountId\":null,\"quantity\":\"1\",\"localNetPrice\":42350.0,\"targetNetPrice\":42350.0,\"localTotalPrice\":42350.0,\"targetTotalPrice\":null,\"localCurrency\":\"USD\",\"targetCurrency\":\"USD\",\"rdsPriceType\":null,\"priceName\":\"Port Connection Type - OTU4 (100 Gbps) - 24M\",\"typeOfInventory\":null,\"priceInUSD\":\"42350\",\"priceScenarioId\":99999905738,\"rateGroup\":\"Port Connection\",\"externalBillingSystem\":null,\"ratePlanId\":null,\"priceCompType\":null,\"pvcId\":null,\"chargeCodeId\":null,\"requestedNRCRate\":null,\"requestedNRCDiscPercentage\":null,\"requestedMRCRate\":null,\"requestedMRCDiscPercentage\":null,\"priceModifiedInd\":\"Y\",\"requestedDiscount\":null,\"requestedRate\":null,\"term\":24,\"priceGroup\":null,\"referencePortId\":null,\"componentType\":null,\"componentParentId\":null,\"country\":null,\"secondaryKeys\":null,\"lineItemId\":null,\"nrcBeid\":null,\"uniqueId\":null,\"elementType\":null,\"reqPriceType\":null},{\"productRateId\":0,\"beid\":\"EYXEX-EYFOX\",\"rateDescription\":null,\"priceCatalog\":\"Standard Pricing\",\"localListPrice\":42350.0,\"targetListPrice\":42350.0,\"icbDesiredDiscPerc\":null,\"priceType\":\"PORT_CONNECTION\",\"priceUnit\":\"Port Connection\",\"frequency\":\"MRC\",\"monthlySurcharge\":null,\"discount\":null,\"discountId\":null,\"quantity\":\"1\",\"localNetPrice\":42350.0,\"targetNetPrice\":42350.0,\"localTotalPrice\":42350.0,\"targetTotalPrice\":null,\"localCurrency\":\"USD\",\"targetCurrency\":\"USD\",\"rdsPriceType\":null,\"priceName\":\"Port Connection Type - OTU4 (100 Gbps) - 24M\",\"typeOfInventory\":null,\"priceInUSD\":\"42350\",\"priceScenarioId\":99999905737,\"rateGroup\":\"Port Connection\",\"externalBillingSystem\":null,\"ratePlanId\":null,\"priceCompType\":null,\"pvcId\":null,\"chargeCodeId\":null,\"requestedNRCRate\":null,\"requestedNRCDiscPercentage\":null,\"requestedMRCRate\":null,\"requestedMRCDiscPercentage\":null,\"priceModifiedInd\":null,\"requestedDiscount\":null,\"requestedRate\":null,\"term\":24,\"priceGroup\":null,\"referencePortId\":null,\"componentType\":null,\"componentParentId\":null,\"country\":null,\"secondaryKeys\":null,\"lineItemId\":null,\"nrcBeid\":null,\"uniqueId\":null,\"elementType\":null,\"reqPriceType\":null},{\"productRateId\":0,\"beid\":\"EYXEX-EYFOX\",\"rateDescription\":null,\"priceCatalog\":\"Standard Pricing\",\"localListPrice\":42350.0,\"targetListPrice\":42350.0,\"icbDesiredDiscPerc\":null,\"priceType\":\"PORT_CONNECTION\",\"priceUnit\":\"Port Connection\",\"frequency\":\"MRC\",\"monthlySurcharge\":null,\"discount\":null,\"discountId\":null,\"quantity\":\"1\",\"localNetPrice\":42350.0,\"targetNetPrice\":42350.0,\"localTotalPrice\":42350.0,\"targetTotalPrice\":null,\"localCurrency\":\"USD\",\"targetCurrency\":\"USD\",\"rdsPriceType\":null,\"priceName\":\"Port Connection Type - OTU4 (100 Gbps) - 24M\",\"typeOfInventory\":null,\"priceInUSD\":\"42350\",\"priceScenarioId\":99999905736,\"rateGroup\":\"Port Connection\",\"externalBillingSystem\":null,\"ratePlanId\":null,\"priceCompType\":null,\"pvcId\":null,\"chargeCodeId\":null,\"requestedNRCRate\":null,\"requestedNRCDiscPercentage\":null,\"requestedMRCRate\":null,\"requestedMRCDiscPercentage\":null,\"priceModifiedInd\":null,\"requestedDiscount\":null,\"requestedRate\":null,\"term\":24,\"priceGroup\":null,\"referencePortId\":null,\"componentType\":null,\"componentParentId\":null,\"country\":null,\"secondaryKeys\":null,\"lineItemId\":null,\"nrcBeid\":null,\"uniqueId\":null,\"elementType\":null,\"reqPriceType\":null},{\"productRateId\":0,\"beid\":\"EYXEX-NRBBL\",\"rateDescription\":null,\"priceCatalog\":\"Standard Pricing\",\"localListPrice\":1500.0,\"targetListPrice\":1500.0,\"icbDesiredDiscPerc\":null,\"priceType\":\"PORT_CONNECTION\",\"priceUnit\":\"Customer Connection Charge\",\"frequency\":\"NRC\",\"monthlySurcharge\":null,\"discount\":null,\"discountId\":null,\"quantity\":\"1\",\"localNetPrice\":1500.0,\"targetNetPrice\":1500.0,\"localTotalPrice\":1500.0,\"targetTotalPrice\":null,\"localCurrency\":\"USD\",\"targetCurrency\":\"USD\",\"rdsPriceType\":null,\"priceName\":\"Customer Connection Charge  - NRC\",\"typeOfInventory\":null,\"priceInUSD\":\"1500\",\"priceScenarioId\":99999905738,\"rateGroup\":\"Customer Connection Charge\",\"externalBillingSystem\":null,\"ratePlanId\":null,\"priceCompType\":null,\"pvcId\":null,\"chargeCodeId\":null,\"requestedNRCRate\":null,\"requestedNRCDiscPercentage\":null,\"requestedMRCRate\":null,\"requestedMRCDiscPercentage\":null,\"priceModifiedInd\":\"Y\",\"requestedDiscount\":null,\"requestedRate\":null,\"term\":24,\"priceGroup\":null,\"referencePortId\":null,\"componentType\":null,\"componentParentId\":null,\"country\":null,\"secondaryKeys\":null,\"lineItemId\":null,\"nrcBeid\":null,\"uniqueId\":null,\"elementType\":null,\"reqPriceType\":null},{\"productRateId\":0,\"beid\":\"EYXEX-NRBBL\",\"rateDescription\":null,\"priceCatalog\":\"Standard Pricing\",\"localListPrice\":1500.0,\"targetListPrice\":1500.0,\"icbDesiredDiscPerc\":null,\"priceType\":\"PORT_CONNECTION\",\"priceUnit\":\"Customer Connection Charge\",\"frequency\":\"NRC\",\"monthlySurcharge\":null,\"discount\":null,\"discountId\":null,\"quantity\":\"1\",\"localNetPrice\":1500.0,\"targetNetPrice\":1500.0,\"localTotalPrice\":1500.0,\"targetTotalPrice\":null,\"localCurrency\":\"USD\",\"targetCurrency\":\"USD\",\"rdsPriceType\":null,\"priceName\":\"Customer Connection Charge  - NRC\",\"typeOfInventory\":null,\"priceInUSD\":\"1500\",\"priceScenarioId\":99999905737,\"rateGroup\":\"Customer Connection Charge\",\"externalBillingSystem\":null,\"ratePlanId\":null,\"priceCompType\":null,\"pvcId\":null,\"chargeCodeId\":null,\"requestedNRCRate\":null,\"requestedNRCDiscPercentage\":null,\"requestedMRCRate\":null,\"requestedMRCDiscPercentage\":null,\"priceModifiedInd\":null,\"requestedDiscount\":null,\"requestedRate\":null,\"term\":24,\"priceGroup\":null,\"referencePortId\":null,\"componentType\":null,\"componentParentId\":null,\"country\":null,\"secondaryKeys\":null,\"lineItemId\":null,\"nrcBeid\":null,\"uniqueId\":null,\"elementType\":null,\"reqPriceType\":null},{\"productRateId\":0,\"beid\":\"EYXEX-NRBBL\",\"rateDescription\":null,\"priceCatalog\":\"Standard Pricing\",\"localListPrice\":1500.0,\"targetListPrice\":1500.0,\"icbDesiredDiscPerc\":null,\"priceType\":\"PORT_CONNECTION\",\"priceUnit\":\"Customer Connection Charge\",\"frequency\":\"NRC\",\"monthlySurcharge\":null,\"discount\":null,\"discountId\":null,\"quantity\":\"1\",\"localNetPrice\":1500.0,\"targetNetPrice\":1500.0,\"localTotalPrice\":1500.0,\"targetTotalPrice\":null,\"localCurrency\":\"USD\",\"targetCurrency\":\"USD\",\"rdsPriceType\":null,\"priceName\":\"Customer Connection Charge  - NRC\",\"typeOfInventory\":null,\"priceInUSD\":\"1500\",\"priceScenarioId\":99999905736,\"rateGroup\":\"Customer Connection Charge\",\"externalBillingSystem\":null,\"ratePlanId\":null,\"priceCompType\":null,\"pvcId\":null,\"chargeCodeId\":null,\"requestedNRCRate\":null,\"requestedNRCDiscPercentage\":null,\"requestedMRCRate\":null,\"requestedMRCDiscPercentage\":null,\"priceModifiedInd\":null,\"requestedDiscount\":null,\"requestedRate\":null,\"term\":24,\"priceGroup\":null,\"referencePortId\":null,\"componentType\":null,\"componentParentId\":null,\"country\":null,\"secondaryKeys\":null,\"lineItemId\":null,\"nrcBeid\":null,\"uniqueId\":null,\"elementType\":null,\"reqPriceType\":null}],\"scpPriceMessages\":null}],\"priceMessage\":null},\"purchaseOrderNumber\":null,\"designModifiedInd\":\"Y\",\"designVersion\":6,\"icsc\":null,\"nssEngagement\":null,\"accessCarrierNameAbbreviation\":null,\"specialConstructionCharge\":null,\"specialConstructionHandling\":null,\"specialConstructionHandlingNotes\":null,\"site\":[{\"nxSiteId\":455870,\"siteId\":99988939546,\"npanxx\":\"990088\",\"address1\":\"315 E ROBINSON ST\",\"city\":\"ORL\",\"state\":\"FL\",\"saLecName\":null,\"speedId\":null,\"saLecSwClli\":null,\"popClli\":null,\"country\":\"US\",\"macdType\":null,\"macdActivity\":null,\"fromInventory\":null,\"dualSiteId\":null,\"onNetCheck\":null,\"ethernetVendor\":null,\"designSiteOfferPort\":null,\"priceDetails\":null,\"address\":null,\"postalCode\":null,\"siteName\":\"L1Z\",\"swcClli\":\"ORLDFLMA\",\"customerLocationClli\":\"ORLDFLLCH09\",\"active\":null,\"emc\":null,\"carrierHotel\":null,\"attComments\":null,\"newBuilding\":null,\"customerReference\":null,\"asrItemId\":null,\"lataCode\":\"458\",\"zipCode\":\"32802\",\"address2\":null,\"room\":null,\"floor\":null,\"building\":null,\"siteComment\":null,\"lconFirstName\":null,\"lconLastName\":null,\"lconPhone\":null,\"lconEmail\":null,\"endPointSiteIdentifier\":\"A\",\"buildingClli\":null,\"regionCode\":\"Y\",\"activityType\":null,\"cancellationReason\":null,\"product\":null,\"quantity\":null,\"nssEngagement\":null,\"designStatus\":null,\"multiGigeIndicator\":null,\"alias\":\"L1Z\",\"macdActionType\":null,\"lconDetails\":[{\"lconType\":null,\"lconFirstName\":null,\"lconLastName\":null,\"lconPhone\":null,\"lconEmail\":null}],\"globalLocationId\":null,\"jurisdiction\":null,\"certificationStatus\":null,\"designModifiedInd\":null,\"independentCarrierCompanyLATA\":null,\"independentCarrierCompanyName\":null,\"interDepartmentMeetPointChecklistURL\":null,\"diverseFromReferenceInfo\":null,\"opticalTerminatingNetworkCarrierFacilityAssignment\":null,\"collocationCarrierFacilityAssignment\":null,\"assetInvestmentSheetIndicator\":null,\"swcCertification\":null,\"designVersion\":null,\"specialConstructionPaymentUrl\":null,\"thirdPartyInd\":null,\"accessCarrierNameAbbreviation\":null,\"design\":null,\"loopLength\":null,\"numOfCopperRepeaters\":null,\"inventoryNumOfPairs\":null,\"taskClli\":null,\"numberRemoteTerminals\":null,\"referenceOfferId\":null,\"siteNpanxx\":null,\"specialConstructionCharge\":null,\"specialConstructionHandling\":null,\"specialConstructionHandlingNotes\":null,\"_endPointRef\":21033},{\"nxSiteId\":455869,\"siteId\":99988939548,\"npanxx\":\"990088\",\"address1\":\"45 N MAGNOLIA AV\",\"city\":\"ORL\",\"state\":\"FL\",\"saLecName\":null,\"speedId\":null,\"saLecSwClli\":null,\"popClli\":null,\"country\":\"US\",\"macdType\":\"Add\",\"macdActivity\":\"Newstart Circuit\",\"fromInventory\":null,\"dualSiteId\":null,\"onNetCheck\":null,\"ethernetVendor\":null,\"designSiteOfferPort\":null,\"priceDetails\":null,\"address\":null,\"postalCode\":null,\"siteName\":\"L 1A\",\"swcClli\":\"ORLDFLMA\",\"customerLocationClli\":\"ORLDFLLCH09\",\"active\":null,\"emc\":null,\"carrierHotel\":null,\"attComments\":null,\"newBuilding\":null,\"customerReference\":null,\"asrItemId\":\"AD1030944\",\"lataCode\":\"458\",\"zipCode\":\"32802\",\"address2\":null,\"room\":null,\"floor\":null,\"building\":null,\"siteComment\":null,\"lconFirstName\":null,\"lconLastName\":null,\"lconPhone\":null,\"lconEmail\":null,\"endPointSiteIdentifier\":\"Z\",\"buildingClli\":null,\"regionCode\":\"Y\",\"activityType\":\"New Request\",\"cancellationReason\":null,\"product\":null,\"quantity\":\"1\",\"nssEngagement\":null,\"designStatus\":\"U\",\"multiGigeIndicator\":null,\"alias\":\"L 1A\",\"macdActionType\":null,\"lconDetails\":[{\"lconType\":null,\"lconFirstName\":null,\"lconLastName\":null,\"lconPhone\":null,\"lconEmail\":null}],\"globalLocationId\":\"00000L4Z5R\",\"jurisdiction\":\"Interstate (FCC) Access (Interstate)\",\"certificationStatus\":null,\"designModifiedInd\":null,\"independentCarrierCompanyLATA\":null,\"independentCarrierCompanyName\":null,\"interDepartmentMeetPointChecklistURL\":null,\"diverseFromReferenceInfo\":null,\"opticalTerminatingNetworkCarrierFacilityAssignment\":null,\"collocationCarrierFacilityAssignment\":null,\"assetInvestmentSheetIndicator\":null,\"swcCertification\":\"Certified\",\"designVersion\":\"1\",\"specialConstructionPaymentUrl\":null,\"thirdPartyInd\":null,\"accessCarrierNameAbbreviation\":null,\"design\":null,\"loopLength\":null,\"numOfCopperRepeaters\":null,\"inventoryNumOfPairs\":null,\"taskClli\":null,\"numberRemoteTerminals\":null,\"referenceOfferId\":null,\"siteNpanxx\":null,\"specialConstructionCharge\":null,\"specialConstructionHandling\":null,\"specialConstructionHandlingNotes\":null,\"_endPointRef\":21034}]}");
		NxDesignAudit audit = new NxDesignAudit();
		audit.setData(json);
		Mockito.when(nxDesignAuditRepository.findByNxRefIdAndTransaction(anyLong(), anyString())).thenReturn(audit);
		Mockito.when(nexxusJsonUtility.getValue(any(), anyString())).thenReturn("Retail");
		NxLookupData lookupData = new NxLookupData();
		lookupData.setDescription("Retail");
		Mockito.when(nxLookupDataRepository.findTopByDatasetNameAndItemId(anyString(), anyString())).thenReturn(lookupData);
		Mockito.when(jsonPathUtil.set(any(), anyString(), anyString(), anyBoolean())).thenReturn(json);
		configAndUpdatePricingUtil.getInputDesignDetails(nxDesign, MyPriceConstants.ADE_OFFER_NAME);
	}

	@Test
	public void testGetData() {
		
		mappingData.setType(MyPriceConstants.IS_DEFAULT);
		mappingData.setDefaultValue("defaultValue");
		assertThat(configAndUpdatePricingUtil.getData(mappingData, inputDesignDetails)).as(" equal to ").isEqualTo("defaultValue");
		
		mappingData.setPath("$..bundleCode,#$..marketStrata");
		mappingData.setType("List");
		List<String> str = new ArrayList<String>();
		str.add("data1");
		str.add("data2");
		Mockito.when(jsonPathUtil.search(anyString(), anyString(), any())).thenReturn(str);
		configAndUpdatePricingUtil.getData(mappingData, inputDesignDetails);
		
		mappingData.setType("Count");
		configAndUpdatePricingUtil.getData(mappingData, inputDesignDetails);
		
		mappingData.setType(null);
		mappingData.setDelimiter("Classic");
		configAndUpdatePricingUtil.getData(mappingData, inputDesignDetails);
		
		mappingData.setPath("$..component.[?(@.componentCodeId==30)].designDetails.[?(@.udfId==200045)].udfAttributeText.[*]||$..swcClli");
		configAndUpdatePricingUtil.getData(mappingData, inputDesignDetails);
		
		mappingData.setPath("$..bundleCode##$..marketStrata");
		mappingData.setDelimiter("Classic");
		configAndUpdatePricingUtil.getData(mappingData, inputDesignDetails);
		
		mappingData.setPath("$..component.[?(@.componentCodeId==30)].designDetails.[?(@.udfId==200058)].udfAttributeId.[*]");
		mappingData.setDataSetName("NX_LOOKUP_DATA|mp_ase_ifOOFIndicator");
		Map<String,NxLookupData> resultMap = new HashMap<String,NxLookupData>();
		NxLookupData lookup = new NxLookupData();
		lookup.setDescription("true");
		resultMap.put("true", lookup);
		Mockito.when(nexxusJsonUtility.getValue(anyString(), anyString())).thenReturn("true");
		Mockito.when(nxMyPriceRepositoryServce.getLookupDataByItemId(anyString())).thenReturn(resultMap);
		configAndUpdatePricingUtil.getData(mappingData, inputDesignDetails);
		mappingData.setDataSetName(null);
	}
	
	@Test
	public void testGetResultCount() {
		mappingData.setPath("$..bundleCode##$..marketStrata");
		List<String> str = new ArrayList<String>();
		str.add("data1");
		str.add("data2");
		Mockito.when(jsonPathUtil.search(anyString(), anyString(), any())).thenReturn(str);
		configAndUpdatePricingUtil.getResultCount(mappingData, inputDesignDetails);
	}
	
	@Test
	public void testProcessMultipleJsonPath() {
		List<String> pathList= new ArrayList<String>();
		pathList.add("$..component.[?(@.componentCodeId==30)].designDetails.[?(@.udfId==20184)].udfAttributeText.[*]||$..customerLocationClli,$..customerLocationClli");
		configAndUpdatePricingUtil.processMultipleJsonPath(mappingData, inputDesignDetails,
				pathList, ",");
	}
	
	@Test
	public void testProcessOrCondition() {
		mappingData.setPath("$..component.[?(@.componentCodeId==30)].designDetails.[?(@.udfId==20184)].udfAttributeText.[*]||$..customerLocationClli");
		List<String> pathList= new ArrayList<String>(Arrays.asList(mappingData.getPath().split(
				Pattern.quote(MyPriceConstants.OR_CONDITION_SEPERATOR))));
		configAndUpdatePricingUtil.processOrCondition(mappingData, inputDesignDetails, pathList);
		
	}
	
	@Test
	public void testIsProductLineIdMatchForConfigDesign() {
		
		methodParam.put(MyPriceConstants.MP_PRODUCT_LINE_ID, "3");
		configAndUpdatePricingUtil.isProductLineIdMatchForConfigDesign(methodParam, "3", MyPriceConstants.ASE_OFFER_NAME);
		
		methodParam.put(MyPriceConstants.MP_PRODUCT_LINE_ID, "3");
		configAndUpdatePricingUtil.isProductLineIdMatchForConfigDesign(methodParam, "2", MyPriceConstants.ADI);
		
		configAndUpdatePricingUtil.isProductLineIdMatchForConfigDesign(methodParam, "2", MyPriceConstants.DDA_OFFER_NAME);
		
	}
	
	@Test
	public void testCollectCircuitDetailsForAde() {
		List<String> dataLst = new ArrayList<>();
		dataLst.add("data1");
		Mockito.when(jsonPathUtil.search(any(), anyString(), any())).thenReturn(dataLst);
		configAndUpdatePricingUtil.collectCircuitDetailsForAde(inputDesignDetails, MyPriceConstants.ADE_OFFER_NAME, methodParam);
	}
	
	@Test
	public void testGetPriceAttributes() {
		configAndUpdatePricingUtil.getPriceAttributes(inputDesignDetails, "$..site..priceDetails.componentDetails..priceAttributes..priceScenarioId");
		
	}
	
	@Test
	public void testGetComponentList() {
		configAndUpdatePricingUtil.getComponentList(inputDesignDetails, "$..site..priceDetails.componentDetails..priceAttributes..componentId");
	}
	
	@Test
	public void testGetNxConfigMapping() {
		methodParam.put(MyPriceConstants.PRODUCT_TYPE, "pricerd");
		Mockito.when( nxMyPriceRepositoryServce.findByOfferAndProductTypeAndRuleName(anyString(), anyString(), anyString())).thenReturn(new ArrayList<NxMpConfigMapping>());
		configAndUpdatePricingUtil.getNxConfigMapping(methodParam, "ASE", "CONFIG_DESIGN");
		
		methodParam.remove(MyPriceConstants.PRODUCT_TYPE);
		Mockito.when( nxMyPriceRepositoryServce.findByOfferAndRuleName(anyString(), anyString())).thenReturn(new ArrayList<NxMpConfigMapping>());
		configAndUpdatePricingUtil.getNxConfigMapping(methodParam, "ASE", "CONFIG_DESIGN");
	}
	
	@Test
	public void testSetProductTypeForInrFmo() {
		methodParam.put(MyPriceConstants.SOURCE, MyPriceConstants.SOURCE_FMO);
		configAndUpdatePricingUtil.setProductTypeForInrFmo(methodParam);
		
		methodParam.put(MyPriceConstants.SOURCE, MyPriceConstants.SOURCE_INR);
		configAndUpdatePricingUtil.setProductTypeForInrFmo(methodParam);
		
		methodParam.remove(MyPriceConstants.SOURCE);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testProcessConfigDataFromCustomeRules() {
		JSONObject inputDesign = JacksonUtil.toJsonObject("{\"country\":\"US\",\"state\":\"ND\",\"city\":\"MINOT\",\"address\":\"200 72ND ST\",\"siteId\":\"90829065\",\"custPostalcode\":\"58703\",\"design\":[{\"siteName\":\"2864321\",\"accessCarrier\":\"AT&T\",\"portType\":\"PPP\",\"circuitId\":\"DHEC495368ATI\",\"portSpeed\":\"1536\",\"clli\":\"BSMRNDJC\",\"accessBandwidth\":\"1536 Kbps\",\"priceDetails\":[{\"priceType\":\"PORTBEID\",\"beid\":\"17986\",\"quantity\":\"1\",\"localListPrice\":\"221\",\"actualPrice\":\"121.55\",\"secondaryKey\":\"#FCC#MPLS Port#Flat Rate#1.544M/1.536M#T1, E1#FR, ATM, IP#VPN Transport Connection#per port#17986#18030#United States#US#USA\",\"elementType\":\"Port\",\"uniqueId\":\"#MPLS Port#MPLS Port - 1.544M/1.536M#FR, ATM, IP#1.544 Mbps/1.536 Mbps#T1/E1#VPN Transport#Connection#Each\"},{\"priceType\":\"COSBEID\",\"beid\":\"18265\",\"quantity\":\"1\",\"localListPrice\":\"0\",\"actualPrice\":\"0\",\"secondaryKey\":\"#FCC#CoS Package#Multimedia Standard Svc#1.544M/1.536M#N/A#N/A#VPN Transport COS Package#per port#18265#18425#United States#US#USA\",\"elementType\":\"PortFeature\",\"uniqueId\":\"#CoS Package Multimedia Standard#CoS Package Multimedia Standard Svc - 1.544M/1.536M#FR, ATM, IP#1.544 Mbps/1.536 Mbps#T1/E1#VPN Transport#COS Package#Port\"}],\"nxSiteId\":112}],\"FALLOUTMATCHINGID\":\"0000000108/AVPNPricingInventory/Body/AccountDetails/AVPNService/AVPNInventoryDetails/SiteDetails/FALLOUTMATCHINGID\",\"product\":\"AVPN\"}");
		methodParam.put(MyPriceConstants.OFFER_NAME, "AVPN");
		methodParam.put(MyPriceConstants.SOURCE, MyPriceConstants.SOURCE_INR);
		List<NxLookupData> rulesData = new ArrayList<>();
		NxLookupData lookup1 = new NxLookupData();
		lookup1.setCriteria("$..priceDetails.[?(@.elementType=='PortFeature')]%{\"UniqueId_PortFeatures_pf\":\"custome#:UniqueId_PortFeatures_pf::uniqueId#Existing_PortFeatures_pf::count#existingMRC_AVPNPortFeatures_pf::mrc#AVPNPortFeatures_ArrayController_pf::size\",\"UniqueId_PortFeatures\":\"collect\",\"portProtocol_pf \":\"portType\"}");
		rulesData.add(lookup1);
		Mockito.when(nxLookupDataRepository.findByDatasetNameAndItemIdAndDescription(anyString(), anyString(), anyString())).thenReturn(rulesData);
		
		TypeRef<List<Object>> mapType = new TypeRef<List<Object>>() {};
		JsonPathUtil jutil = new JsonPathUtil();
		List<Object> data=jutil.search(inputDesign, "$..priceDetails.[?(@.elementType=='PortFeature')]", mapType);
		
		NexxusJsonUtility nj=new NexxusJsonUtility();
		when(nexxusJsonUtility.getValueLst(any(),any())).thenReturn(data);
		HashMap criteriaMap=(HashMap<String, String>) nj.convertStringJsonToMap("{\"UniqueId_PortFeatures_pf\":\"custome#:UniqueId_PortFeatures_pf::uniqueId#Existing_PortFeatures_pf::count#existingMRC_AVPNPortFeatures_pf::mrc#AVPNPortFeatures_ArrayController_pf::size\",\"UniqueId_PortFeatures\":\"collect\",\"portProtocol_pf \":\"portType\"}");
		when(nexxusJsonUtility.convertStringJsonToMap(Mockito.anyString())).thenReturn(criteriaMap);
		
		configAndUpdatePricingUtil.processConfigDataFromCustomeRules(methodParam, inputDesign);
		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testProcessConfigDataFromCustomeRulesCP4() {
		JSONObject inputDesign = JacksonUtil.toJsonObject("{ \"site\": [ { \"callDirection\": \"Outbound\", \"billingElementCode\": \"USAGE\", \"unitRate\": \"0\", \"additionalPeriodRate\": \"0.0081\", \"genericQuantity\": \"360.00\", \"originatingStateCountryName\": \"RI\", \"terminatingStateCountryName\": \"CL\", \"iOBMTIndicator\": \"Y\", \"pBICode\": \"00092978\", \"initialPeriodRate\": \"0.2444\", \"additionalPeriodDefinition\": \"1\", \"siteAddr2\": \"WATERTOWN,MA  02472\", \"siteCountry\": \"US\", \"siteAddr1\": \"480 ARSENAL ST\", \"siteCity\": \"WATERTOWN\", \"siteState\": \"MA\", \"siteZip\": \"02472-0000\", \"siteAddress\": {}, \"grossCharge\": \"2.8799999\", \"initialPeriodDefinition\": \"30\", \"jurisdiction\": \"MOW\", \"totalQuantityAnnual\": \"360.00\", \"pBIDescription\": \"VoAVPN Int l OffNet\", \"FALLOUTMATCHINGID\": \"0000002627/BVOIPPricingInventory/Body/AccountDetails/SubAccountUsage/UsageDetails/FALLOUTMATCHINGID\", \"billedMinutesQty\": \"360.00\", \"discount\": \"0\", \"lptnType\": \"*\", \"inrQty\": \"360.00\", \"currentMRC\": \"0\", \"cdtRegion\": \"US\", \"secondaryKey\": \"#Intl#BVOIP - Outbound Standard#Mobile#Colombia#COM#Ded-Sw/Sw-Ded#Std#30/1\", \"category\": \"BVoIP Intl Standard Outbound\", \"productType\": \"Usage\", \"component\": \"International Outbound\", \"uniqueId\": \"#Intl#BVoIP Intl Standard Outbound#International Outbound#Ded-Sw/Sw-Ded#30/1#Mobile#per Minute#US#United States#COM#Colombia\" }, { \"callDirection\": \"Outbound\", \"billingElementCode\": \"USAGE\", \"unitRate\": \"0\", \"additionalPeriodRate\": \"0.0081\", \"genericQuantity\": \"4116.00\", \"originatingStateCountryName\": \"RI\", \"terminatingStateCountryName\": \"CL\", \"iOBMTIndicator\": \"Y\", \"pBICode\": \"00092978\", \"initialPeriodRate\": \"0.2444\", \"additionalPeriodDefinition\": \"1\", \"siteAddr2\": \"WATERTOWN,MA  02472\", \"siteCountry\": \"US\", \"siteAddr1\": \"480 ARSENAL ST\", \"siteCity\": \"WATERTOWN\", \"siteState\": \"MA\", \"siteZip\": \"02472-0000\", \"siteAddress\": {}, \"grossCharge\": \"33.3600006\", \"initialPeriodDefinition\": \"30\", \"jurisdiction\": \"MOW\", \"totalQuantityAnnual\": \"4116.00\", \"pBIDescription\": \"VoAVPN Int l OffNet\", \"FALLOUTMATCHINGID\": \"0000002585/BVOIPPricingInventory/Body/AccountDetails/SubAccountUsage/UsageDetails/FALLOUTMATCHINGID\", \"billedMinutesQty\": \"4116.00\", \"discount\": \"0\", \"lptnType\": \"*\", \"inrQty\": \"4116.00\", \"currentMRC\": \"0\", \"cdtRegion\": \"US\", \"secondaryKey\": \"#Intl#BVOIP - Outbound Standard#Mobile#Colombia#COM#Ded-Sw/Sw-Ded#Std#30/1\", \"category\": \"BVoIP Intl Standard Outbound\", \"productType\": \"Usage\", \"component\": \"International Outbound\", \"uniqueId\": \"#Intl#BVoIP Intl Standard Outbound#International Outbound#Ded-Sw/Sw-Ded#30/1#Mobile#per Minute#US#United States#COM#Colombia\" }, { \"callDirection\": \"Outbound\", \"billingElementCode\": \"USAGE\", \"unitRate\": \"0\", \"additionalPeriodRate\": \"0.0003\", \"genericQuantity\": \"4620.00\", \"originatingStateCountryName\": \"ID\", \"terminatingStateCountryName\": \"SI\", \"iOBMTIndicator\": \"N\", \"pBICode\": \"00092978\", \"initialPeriodRate\": \"0.0075\", \"additionalPeriodDefinition\": \"1\", \"siteAddr2\": \"AUSTIN,TX  78721\", \"siteCountry\": \"US\", \"siteAddr1\": \"3301 HIBBITS BLVD\", \"siteCity\": \"AUSTIN\", \"siteState\": \"TX\", \"siteZip\": \"78721-0000\", \"siteAddress\": {}, \"grossCharge\": \"1.3199999\", \"initialPeriodDefinition\": \"30\", \"jurisdiction\": \"MOW\", \"totalQuantityAnnual\": \"4620.00\", \"pBIDescription\": \"VoAVPN Int l OffNet\", \"FALLOUTMATCHINGID\": \"0000002406/BVOIPPricingInventory/Body/AccountDetails/SubAccountUsage/UsageDetails/FALLOUTMATCHINGID\", \"billedMinutesQty\": \"4620.00\", \"discount\": \"0\", \"lptnType\": \"*\", \"inrQty\": \"4620.00\", \"currentMRC\": \"0\", \"cdtRegion\": \"US\", \"secondaryKey\": \"#Intl#BVOIP - Outbound Standard#Non-Mobile#Singapore#SG#Ded-Sw/Sw-Ded#Std#30/1\", \"category\": \"BVoIP Intl Standard Outbound\", \"productType\": \"Usage\", \"component\": \"International Outbound\", \"uniqueId\": \"#Intl#BVoIP Intl Standard Outbound#International Outbound#Ded-Sw/Sw-Ded#30/1#Non-Mobile#per Minute#US#United States#SG#Singapore\" }, { \"callDirection\": \"Outbound\", \"billingElementCode\": \"USAGE\", \"unitRate\": \"0\", \"additionalPeriodRate\": \"0.0002\", \"genericQuantity\": \"360.00\", \"originatingStateCountryName\": \"CA\", \"terminatingStateCountryName\": \"UK\", \"iOBMTIndicator\": \"N\", \"pBICode\": \"00092978\", \"initialPeriodRate\": \"0.0057\", \"additionalPeriodDefinition\": \"1\", \"siteAddr2\": \"AUSTIN,TX  78721\", \"siteCountry\": \"US\", \"siteAddr1\": \"3301 HIBBITS BLVD\", \"siteCity\": \"AUSTIN\", \"siteState\": \"TX\", \"siteZip\": \"78721-0000\", \"siteAddress\": {}, \"grossCharge\": \"0.1200000\", \"initialPeriodDefinition\": \"30\", \"jurisdiction\": \"MOW\", \"totalQuantityAnnual\": \"360.00\", \"pBIDescription\": \"VoAVPN Int l OffNet\", \"FALLOUTMATCHINGID\": \"0000002398/BVOIPPricingInventory/Body/AccountDetails/SubAccountUsage/UsageDetails/FALLOUTMATCHINGID\", \"billedMinutesQty\": \"360.00\", \"discount\": \"0\", \"lptnType\": \"*\", \"inrQty\": \"360.00\", \"currentMRC\": \"0\", \"cdtRegion\": \"US\", \"secondaryKey\": \"#Intl#BVOIP - Outbound Standard#Non-Mobile#United Kingdom#GB#Ded-Sw/Sw-Ded#Std#30/1\", \"category\": \"BVoIP Intl Standard Outbound\", \"productType\": \"Usage\", \"component\": \"International Outbound\", \"uniqueId\": \"#Intl#BVoIP Intl Standard Outbound#International Outbound#Ded-Sw/Sw-Ded#30/1#Non-Mobile#per Minute#US#United States#GB#United Kingdom\" } ] }");
		methodParam.put(MyPriceConstants.OFFER_NAME, "BVoIP");
		methodParam.put(MyPriceConstants.SOURCE, MyPriceConstants.SOURCE_INR);
		List<NxLookupData> rulesData = new ArrayList<>();
		NxLookupData lookup1 = new NxLookupData();
		lookup1.setCriteria("$..[?(@.productType=='Usage' && @.jurisdiction=='MOW' && @.category=='BVoIP Intl Standard Outbound' )]%{\"uniqueID_bVoIPStandardOutboundUsageStandalone_pf\":\"uniqueId\",\"bVoIP_Existing_StandardOutboundUsageStandalone_pf\":\"result::totalQuantityAnnual/60/12::round:-1##evaluate##totalQuantityAnnual##minutes\",\"existingMRC_bVoIPStandardUsageStandalone_pf\":\"step1::((60/initialPeriodDefinition)*initialPeriodRate)::round:4&&result::step1*(1-(discount*0.01))::round:4##evaluate##initialPeriodDefinition,initialPeriodRate,discount##mrc\"}");
		rulesData.add(lookup1);
		Mockito.when(nxLookupDataRepository.findByDatasetNameAndItemIdAndDescription(anyString(), anyString(), anyString())).thenReturn(rulesData);
		
		TypeRef<List<Object>> mapType = new TypeRef<List<Object>>() {};
		JsonPathUtil jutil = new JsonPathUtil();
		List<Object> data=jutil.search(inputDesign, "$..[?(@.productType=='Usage' && @.jurisdiction=='MOW' && @.category=='BVoIP Intl Standard Outbound' )]", mapType);
		
		NexxusJsonUtility nj=new NexxusJsonUtility();
		when(nexxusJsonUtility.getValueLst(any(),any())).thenReturn(data);
		HashMap criteriaMap=(HashMap<String, String>) nj.convertStringJsonToMap("{\"uniqueID_bVoIPStandardOutboundUsageStandalone_pf\":\"uniqueId\",\"bVoIP_Existing_StandardOutboundUsageStandalone_pf\":\"result::totalQuantityAnnual/60/12::round:-1##evaluate##totalQuantityAnnual##minutes\",\"existingMRC_bVoIPStandardUsageStandalone_pf\":\"step1::((60/initialPeriodDefinition)*initialPeriodRate)::round:4&&result::step1*(1-(discount*0.01))::round:4##evaluate##initialPeriodDefinition,initialPeriodRate,discount##mrc\"}");
		when(nexxusJsonUtility.convertStringJsonToMap(Mockito.anyString())).thenReturn(criteriaMap);
		
		configAndUpdatePricingUtil.processConfigDataFromCustomeRules(methodParam, inputDesign);
		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testProcessConfigDataFromCustomeRulesBvoipNU() {
		JSONObject inputDesign = JacksonUtil.toJsonObject("{\"featureDetails\":[{\"pBICode\":\"00092945\",\"unitRate\":\"30\",\"netAmount\":\"17550.00\",\"genericQuantity\":\"5000\",\"discount\":\"132450.00\",\"inrQty\":\"5000\",\"currentMRC\":\"3.51\",\"secondaryKey\":\"#FCC#IP Flex Reach - AVPN#Calling Plan (Domestic)#Plan B#United States#per concurrent call\",\"jurisdiction\":\"US\",\"category\":\"Product Services\",\"productType\":\"Product Services\",\"elementType\":\"IP Flex Reach - AVPN\",\"uniqueId\":\"#FCC#Product Services#IP Flex Reach - AVPN#Calling Plan (Domestic)#Plan B#N\\/A#per concurrent call#US Nationwide\"},{\"pBICode\":\"00095887\",\"unitRate\":\"0.3\",\"netAmount\":\"0\",\"genericQuantity\":\"7646\",\"discount\":\"2293.80\",\"lptnType\":\"Standard\",\"inrQty\":\"84009\",\"currentMRC\":\"0.00\",\"secondaryKey\":\"#FCC#IP Flex Reach - AVPN#Telephone Numbers (Domestic)#Standard numbers#United States#per number\",\"jurisdiction\":\"US\",\"category\":\"Product Services\",\"productType\":\"Features\",\"elementType\":\"IP Flex Reach - AVPN\",\"uniqueId\":\"#FCC#Product Services#IP Flex Reach - AVPN#Telephone Numbers (Domestic)#Standard numbers#N\\/A#per number#US Nationwide\"},{\"pBICode\":\"00095887\",\"unitRate\":\"0.3\",\"netAmount\":\"0\",\"genericQuantity\":\"7646\",\"discount\":\"2293.80\",\"lptnType\":\"Virtual\",\"inrQty\":\"18991\",\"currentMRC\":\"0.00\",\"secondaryKey\":\"#FCC#IP Flex Reach - AVPN#Telephone Numbers (Domestic)#Virtual numbers#United States#per number\",\"jurisdiction\":\"US\",\"category\":\"Product Services\",\"productType\":\"Features\",\"elementType\":\"IP Flex Reach - AVPN\",\"uniqueId\":\"#FCC#Product Services#IP Flex Reach - AVPN#Telephone Numbers (Domestic)#Virtual numbers#N\\/A#per number#US Nationwide\"},{\"pBICode\":\"00077043\",\"unitRate\":\"30\",\"netAmount\":\"0\",\"genericQuantity\":\"100\",\"discount\":\"3000.00\",\"inrQty\":\"100\",\"currentMRC\":\"0.00\",\"secondaryKey\":\"#FCC#IP Toll Free#Features - IP InfoPack#IP InfoPack - Standard#N\\/A#per concurrent call\",\"jurisdiction\":\"US\",\"category\":\"Product Services\",\"productType\":\"Features\",\"elementType\":\"IP Toll Free\",\"uniqueId\":\"#FCC#Product Services#IP Toll Free#Features - IP InfoPack#IP InfoPack - Standard#N\\/A#per concurrent call#US Nationwide\"},{\"pBICode\":\"00077035\",\"unitRate\":\"40\",\"netAmount\":\"0\",\"genericQuantity\":\"100\",\"discount\":\"4000.00\",\"inrQty\":\"100\",\"currentMRC\":\"0.00\",\"secondaryKey\":\"#FCC#IP Toll Free#Features - IP Courtesy Transfer#IP Courtesy Transfer to Non-8YY Off-Net#N\\/A#per minute\",\"jurisdiction\":\"US\",\"category\":\"Product Services\",\"productType\":\"Features\",\"elementType\":\"IP Toll Free\",\"uniqueId\":\"#FCC#Product Services#IP Toll Free#Features - IP Courtesy Transfer#IP Courtesy Transfer to Non-8YY#Off Net#per minute#US Nationwide\"},{\"pBICode\":\"00070441\",\"unitRate\":\"20\",\"netAmount\":\"0\",\"genericQuantity\":\"100\",\"discount\":\"2000.00\",\"currentMRC\":\"0.00\",\"secondaryKey\":\"#FCC#IP Flex Reach#Calling Plan#Plan A#N\\/A#per concurrent call\",\"jurisdiction\":\"US\",\"category\":\"Product Services\",\"productType\":\"Product Services\",\"elementType\":\"IP Flex Reach\",\"uniqueId\":\"#FCC#Product Services#IP Flex Reach#Calling Plan#Plan A#N\\/A#per concurrent call#US Nationwide\"},{\"pBICode\":\"00077035\",\"unitRate\":\"40\",\"netAmount\":\"0\",\"genericQuantity\":\"3000\",\"discount\":\"120000.00\",\"inrQty\":\"3000\",\"currentMRC\":\"0.00\",\"secondaryKey\":\"#FCC#IP Toll Free#Features - IP Courtesy Transfer#IP Courtesy Transfer to Non-8YY Off-Net#N\\/A#per minute\",\"jurisdiction\":\"US\",\"category\":\"Product Services\",\"productType\":\"Features\",\"elementType\":\"IP Toll Free\",\"uniqueId\":\"#FCC#Product Services#IP Toll Free#Features - IP Courtesy Transfer#IP Courtesy Transfer to Non-8YY#Off Net#per minute#US Nationwide\"},{\"pBICode\":\"00077043\",\"unitRate\":\"30\",\"netAmount\":\"0\",\"genericQuantity\":\"3000\",\"discount\":\"90000.00\",\"inrQty\":\"3000\",\"currentMRC\":\"0.00\",\"secondaryKey\":\"#FCC#IP Toll Free#Features - IP InfoPack#IP InfoPack - Standard#N\\/A#per concurrent call\",\"jurisdiction\":\"US\",\"category\":\"Product Services\",\"productType\":\"Features\",\"elementType\":\"IP Toll Free\",\"uniqueId\":\"#FCC#Product Services#IP Toll Free#Features - IP InfoPack#IP InfoPack - Standard#N\\/A#per concurrent call#US Nationwide\"},{\"pBICode\":\"00077043\",\"unitRate\":\"30\",\"netAmount\":\"0\",\"genericQuantity\":\"3000\",\"discount\":\"90000.00\",\"inrQty\":\"3000\",\"currentMRC\":\"0.00\",\"secondaryKey\":\"#FCC#IP Toll Free#Features - IP InfoPack#IP InfoPack - Standard#N\\/A#per concurrent call\",\"jurisdiction\":\"US\",\"category\":\"Product Services\",\"productType\":\"Features\",\"elementType\":\"IP Toll Free\",\"uniqueId\":\"#FCC#Product Services#IP Toll Free#Features - IP InfoPack#IP InfoPack - Standard#N\\/A#per concurrent call#US Nationwide\"},{\"pBICode\":\"00077035\",\"unitRate\":\"40\",\"netAmount\":\"0\",\"genericQuantity\":\"3000\",\"discount\":\"120000.00\",\"inrQty\":\"3000\",\"currentMRC\":\"0.00\",\"secondaryKey\":\"#FCC#IP Toll Free#Features - IP Courtesy Transfer#IP Courtesy Transfer to Non-8YY Off-Net#N\\/A#per minute\",\"jurisdiction\":\"US\",\"category\":\"Product Services\",\"productType\":\"Features\",\"elementType\":\"IP Toll Free\",\"uniqueId\":\"#FCC#Product Services#IP Toll Free#Features - IP Courtesy Transfer#IP Courtesy Transfer to Non-8YY#Off Net#per minute#US Nationwide\"},{\"pBICode\":\"00096347\",\"unitRate\":\"30\",\"netAmount\":\"0\",\"genericQuantity\":\"22000\",\"discount\":\"660000.00\",\"inrQty\":\"22000\",\"currentMRC\":\"0.00\",\"secondaryKey\":\"#FCC#IP Toll Free#Features - IP InfoPack#IP InfoPack - Standard#N\\/A#per concurrent call\",\"jurisdiction\":\"US\",\"category\":\"Product Services\",\"productType\":\"Features\",\"elementType\":\"IP Toll Free\",\"uniqueId\":\"#FCC#Product Services#IP Toll Free#Features - IP InfoPack#IP InfoPack - Standard#N\\/A#per concurrent call#US Nationwide\"},{\"pBICode\":\"00095887\",\"unitRate\":\"0.3\",\"netAmount\":\"0\",\"genericQuantity\":\"35569\",\"discount\":\"10670.70\",\"lptnType\":\"Standard\",\"inrQty\":\"110955\",\"currentMRC\":\"0.00\",\"secondaryKey\":\"#FCC#IP Flex Reach - AVPN#Telephone Numbers (Domestic)#Standard numbers#United States#per number\",\"jurisdiction\":\"US\",\"category\":\"Product Services\",\"productType\":\"Features\",\"elementType\":\"IP Flex Reach - AVPN\",\"uniqueId\":\"#FCC#Product Services#IP Flex Reach - AVPN#Telephone Numbers (Domestic)#Standard numbers#N\\/A#per number#US Nationwide\"},{\"pBICode\":\"00095887\",\"unitRate\":\"0.3\",\"netAmount\":\"0\",\"genericQuantity\":\"35569\",\"discount\":\"10670.70\",\"lptnType\":\"Virtual\",\"inrQty\":\"5766\",\"currentMRC\":\"0.00\",\"secondaryKey\":\"#FCC#IP Flex Reach - AVPN#Telephone Numbers (Domestic)#Virtual numbers#United States#per number\",\"jurisdiction\":\"US\",\"category\":\"Product Services\",\"productType\":\"Features\",\"elementType\":\"IP Flex Reach - AVPN\",\"uniqueId\":\"#FCC#Product Services#IP Flex Reach - AVPN#Telephone Numbers (Domestic)#Virtual numbers#N\\/A#per number#US Nationwide\"},{\"pBICode\":\"00092945\",\"unitRate\":\"30\",\"netAmount\":\"17550.00\",\"genericQuantity\":\"5000\",\"discount\":\"132450.00\",\"inrQty\":\"5000\",\"currentMRC\":\"3.51\",\"secondaryKey\":\"#FCC#IP Flex Reach - AVPN#Calling Plan (Domestic)#Plan B#United States#per concurrent call\",\"jurisdiction\":\"US\",\"category\":\"Product Services\",\"productType\":\"Product Services\",\"elementType\":\"IP Flex Reach - AVPN\",\"uniqueId\":\"#FCC#Product Services#IP Flex Reach - AVPN#Calling Plan (Domestic)#Plan B#N\\/A#per concurrent call#US Nationwide\"},{\"pBICode\":\"00096347\",\"unitRate\":\"30\",\"netAmount\":\"0\",\"genericQuantity\":\"10000\",\"discount\":\"300000.00\",\"inrQty\":\"10000\",\"currentMRC\":\"0.00\",\"secondaryKey\":\"#FCC#IP Toll Free#Features - IP InfoPack#IP InfoPack - Standard#N\\/A#per concurrent call\",\"jurisdiction\":\"US\",\"category\":\"Product Services\",\"productType\":\"Features\",\"elementType\":\"IP Toll Free\",\"uniqueId\":\"#FCC#Product Services#IP Toll Free#Features - IP InfoPack#IP InfoPack - Standard#N\\/A#per concurrent call#US Nationwide\"},{\"pBICode\":\"00096347\",\"unitRate\":\"30\",\"netAmount\":\"0\",\"genericQuantity\":\"20\",\"discount\":\"600.00\",\"inrQty\":\"20\",\"currentMRC\":\"0.00\",\"secondaryKey\":\"#FCC#IP Toll Free#Features - IP InfoPack#IP InfoPack - Standard#N\\/A#per concurrent call\",\"jurisdiction\":\"US\",\"category\":\"Product Services\",\"productType\":\"Features\",\"elementType\":\"IP Toll Free\",\"uniqueId\":\"#FCC#Product Services#IP Toll Free#Features - IP InfoPack#IP InfoPack - Standard#N\\/A#per concurrent call#US Nationwide\"},{\"pBICode\":\"00096347\",\"unitRate\":\"30\",\"netAmount\":\"0\",\"genericQuantity\":\"20\",\"discount\":\"600.00\",\"inrQty\":\"20\",\"currentMRC\":\"0.00\",\"secondaryKey\":\"#FCC#IP Toll Free#Features - IP InfoPack#IP InfoPack - Standard#N\\/A#per concurrent call\",\"jurisdiction\":\"US\",\"category\":\"Product Services\",\"productType\":\"Features\",\"elementType\":\"IP Toll Free\",\"uniqueId\":\"#FCC#Product Services#IP Toll Free#Features - IP InfoPack#IP InfoPack - Standard#N\\/A#per concurrent call#US Nationwide\"},{\"pBICode\":\"00095887\",\"unitRate\":\"0.3\",\"netAmount\":\"0\",\"genericQuantity\":\"200\",\"discount\":\"60.00\",\"lptnType\":\"Standard\",\"inrQty\":\"310\",\"currentMRC\":\"0.00\",\"secondaryKey\":\"#FCC#IP Flex Reach - AVPN#Telephone Numbers (Domestic)#Standard numbers#United States#per number\",\"jurisdiction\":\"US\",\"category\":\"Product Services\",\"productType\":\"Features\",\"elementType\":\"IP Flex Reach - AVPN\",\"uniqueId\":\"#FCC#Product Services#IP Flex Reach - AVPN#Telephone Numbers (Domestic)#Standard numbers#N\\/A#per number#US Nationwide\"},{\"pBICode\":\"00095887\",\"unitRate\":\"0.3\",\"netAmount\":\"0\",\"genericQuantity\":\"200\",\"discount\":\"60.00\",\"lptnType\":\"Virtual\",\"inrQty\":\"101\",\"currentMRC\":\"0.00\",\"secondaryKey\":\"#FCC#IP Flex Reach - AVPN#Telephone Numbers (Domestic)#Virtual numbers#United States#per number\",\"jurisdiction\":\"US\",\"category\":\"Product Services\",\"productType\":\"Features\",\"elementType\":\"IP Flex Reach - AVPN\",\"uniqueId\":\"#FCC#Product Services#IP Flex Reach - AVPN#Telephone Numbers (Domestic)#Virtual numbers#N\\/A#per number#US Nationwide\"},{\"pBICode\":\"00092945\",\"unitRate\":\"30\",\"netAmount\":\"140.40\",\"genericQuantity\":\"40\",\"discount\":\"1059.60\",\"inrQty\":\"40\",\"currentMRC\":\"3.51\",\"secondaryKey\":\"#FCC#IP Flex Reach - AVPN#Calling Plan (Domestic)#Plan B#United States#per concurrent call\",\"jurisdiction\":\"US\",\"category\":\"Product Services\",\"productType\":\"Product Services\",\"elementType\":\"IP Flex Reach - AVPN\",\"uniqueId\":\"#FCC#Product Services#IP Flex Reach - AVPN#Calling Plan (Domestic)#Plan B#N\\/A#per concurrent call#US Nationwide\"},{\"pBICode\":\"00092945\",\"unitRate\":\"30\",\"netAmount\":\"70.20\",\"genericQuantity\":\"20\",\"discount\":\"529.80\",\"inrQty\":\"20\",\"currentMRC\":\"3.51\",\"secondaryKey\":\"#FCC#IP Flex Reach - AVPN#Calling Plan (Domestic)#Plan B#United States#per concurrent call\",\"jurisdiction\":\"US\",\"category\":\"Product Services\",\"productType\":\"Product Services\",\"elementType\":\"IP Flex Reach - AVPN\",\"uniqueId\":\"#FCC#Product Services#IP Flex Reach - AVPN#Calling Plan (Domestic)#Plan B#N\\/A#per concurrent call#US Nationwide\"},{\"pBICode\":\"00095887\",\"unitRate\":\"0.3\",\"netAmount\":\"0\",\"genericQuantity\":\"100\",\"discount\":\"30.00\",\"lptnType\":\"Standard\",\"inrQty\":\"222\",\"currentMRC\":\"0.00\",\"secondaryKey\":\"#FCC#IP Flex Reach - AVPN#Telephone Numbers (Domestic)#Standard numbers#United States#per number\",\"jurisdiction\":\"US\",\"category\":\"Product Services\",\"productType\":\"Features\",\"elementType\":\"IP Flex Reach - AVPN\",\"uniqueId\":\"#FCC#Product Services#IP Flex Reach - AVPN#Telephone Numbers (Domestic)#Standard numbers#N\\/A#per number#US Nationwide\"},{\"pBICode\":\"00095887\",\"unitRate\":\"0.3\",\"netAmount\":\"0\",\"genericQuantity\":\"100\",\"discount\":\"30.00\",\"lptnType\":\"Virtual\",\"inrQty\":\"10\",\"currentMRC\":\"0.00\",\"secondaryKey\":\"#FCC#IP Flex Reach - AVPN#Telephone Numbers (Domestic)#Virtual numbers#United States#per number\",\"jurisdiction\":\"US\",\"category\":\"Product Services\",\"productType\":\"Features\",\"elementType\":\"IP Flex Reach - AVPN\",\"uniqueId\":\"#FCC#Product Services#IP Flex Reach - AVPN#Telephone Numbers (Domestic)#Virtual numbers#N\\/A#per number#US Nationwide\"},{\"pBICode\":\"00070447\",\"unitRate\":\"0.3\",\"netAmount\":\"0\",\"genericQuantity\":\"100\",\"discount\":\"30.00\",\"lptnType\":\"Standard\",\"inrQty\":\"100\",\"currentMRC\":\"0.00\",\"secondaryKey\":\"#FCC#IP Flex Reach#Telephone Numbers#Standard numbers#N\\/A#per number\",\"jurisdiction\":\"US\",\"category\":\"Product Services\",\"productType\":\"Features\",\"elementType\":\"IP Flex Reach\",\"uniqueId\":\"#FCC#Product Services#IP Flex Reach#Telephone Numbers#Standard numbers#N\\/A#per number#US Nationwide\"},{\"pBICode\":\"00070444\",\"unitRate\":\"21.48\",\"netAmount\":\"0\",\"genericQuantity\":\"10\",\"discount\":\"214.80\",\"inrQty\":\"10\",\"currentMRC\":\"0.00\",\"secondaryKey\":\"#FCC#IP Flex Reach#Calling Plan#Plan C#N\\/A#per concurrent call\",\"jurisdiction\":\"US\",\"category\":\"Product Services\",\"productType\":\"Product Services\",\"elementType\":\"IP Flex Reach\",\"uniqueId\":\"#FCC#Product Services#IP Flex Reach#Calling Plan#Plan C#N\\/A#per concurrent call#US Nationwide\"}]}");
		methodParam.put(MyPriceConstants.OFFER_NAME, "BVoIP Non-Usage");
		methodParam.put(MyPriceConstants.SOURCE, MyPriceConstants.SOURCE_INR);
		List<NxLookupData> rulesData = new ArrayList<>();
		NxLookupData lookup1 = new NxLookupData();
		lookup1.setCriteria("$..featureDetails.[?(@.productType=='Product Services' && @.jurisdiction=='US')]%{\"bVoIPProductServicesUniqueID_pf\":\"uniqueId##type##uniqueId\",\"bVoIPProductServicesExisting_pf\": \"inrQty##type##minutes\",\"bVoIPProductServicesExistingIntermediate_pf\": \"genericQuantity##type##totalMinutes\",\"existingMRC_bVoIPProductServices_pf\":\"netAmount##type##mrc\"}");
		rulesData.add(lookup1);
		Mockito.when(nxLookupDataRepository.findByDatasetNameAndItemIdAndDescription(anyString(), anyString(), anyString())).thenReturn(rulesData);
		
		TypeRef<List<Object>> mapType = new TypeRef<List<Object>>() {};
		JsonPathUtil jutil = new JsonPathUtil();
		List<Object> data=jutil.search(inputDesign, "$..featureDetails.[?(@.productType=='Product Services' && @.jurisdiction=='US')]", mapType);
		
		NexxusJsonUtility nj=new NexxusJsonUtility();
		when(nexxusJsonUtility.getValueLst(any(),any())).thenReturn(data);
		HashMap criteriaMap=(HashMap<String, String>) nj.convertStringJsonToMap("{\"bVoIPProductServicesUniqueID_pf\":\"uniqueId##type##uniqueId\",\"bVoIPProductServicesExisting_pf\": \"inrQty##type##minutes\",\"bVoIPProductServicesExistingIntermediate_pf\": \"genericQuantity##type##totalMinutes\",\"existingMRC_bVoIPProductServices_pf\":\"netAmount##type##mrc\"}");
		when(nexxusJsonUtility.convertStringJsonToMap(Mockito.anyString())).thenReturn(criteriaMap);
		
		configAndUpdatePricingUtil.processConfigDataFromCustomeRules(methodParam, inputDesign);
		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testProcessConfigDataFromCustomeRulesAnira() {
		JSONObject inputDesign = JacksonUtil.toJsonObject("{\"sohoList\":[{\"soho\":[{\"sohoMrc\":\"48.51\",\"aniraSohoBeid\":\"19239\",\"sohoQuantity\":\"1\",\"elementType\":\"SOHO\",\"uniqueId\":\"#MOW#Static NB#SOHO#ANIRA SOHO Hi-end CPE#u110/u115#Turkey#TR#Per Unit\",\"secondaryKey\":\"#Intl#SOHO#ANIRA SOHO Hi-end CPE#NetGate 8100 / 8200#Per Unit#19239#20832#M72202#O94041\"}]}],\"FALLOUTMATCHINGID\":\"0000000002/ANIRAPricingInventory/Body/InventoryResponse/AccountDetails/ANIRAService/ANIRAInventoryDetails/SiteDetailsList/SiteDetails/FALLOUTMATCHINGID\",\"nxSiteId\":10,\"product\":\"ANIRA\"}");
		methodParam.put(MyPriceConstants.OFFER_NAME, "ANIRA");
		methodParam.put(MyPriceConstants.SOURCE, MyPriceConstants.SOURCE_INR);
		List<NxLookupData> rulesData = new ArrayList<>();
		NxLookupData lookup1 = new NxLookupData();
		lookup1.setCriteria("\"$..soho.[?(@.elementType=='SOHO')]%{\"ANIRA_SOHOUniqueID_pf\":\"uniqueId\",\"ANIRA_SOHOExistingMRC_pf\":\"sohoMrc\",\"ANIRA_SOHOExisting_pf\":\"quantity\",\"ANIRA_Soho_UniqueID_port\":\"collect\"}");
		rulesData.add(lookup1);
		Mockito.when(nxLookupDataRepository.findByDatasetNameAndItemIdAndDescription(anyString(), anyString(), anyString())).thenReturn(rulesData);
		
		TypeRef<List<Object>> mapType = new TypeRef<List<Object>>() {};
		JsonPathUtil jutil = new JsonPathUtil();
		List<Object> data=jutil.search(inputDesign, "$..soho.[?(@.elementType=='SOHO')]", mapType);
		
		NexxusJsonUtility nj=new NexxusJsonUtility();
		when(nexxusJsonUtility.getValueLst(any(),any())).thenReturn(data);
		HashMap criteriaMap=(HashMap<String, String>) nj.convertStringJsonToMap("{\"ANIRA_SOHOUniqueID_pf\":\"uniqueId\",\"ANIRA_SOHOExistingMRC_pf\":\"sohoMrc\",\"ANIRA_SOHOExisting_pf\":\"quantity\",\"ANIRA_Soho_UniqueID_port\":\"collect\"}");
		when(nexxusJsonUtility.convertStringJsonToMap(Mockito.anyString())).thenReturn(criteriaMap);
		
		configAndUpdatePricingUtil.processConfigDataFromCustomeRules(methodParam, inputDesign);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testProcessConfigDataFromCustomeRulesADI() {
		JSONObject inputDesign = JacksonUtil.toJsonObject("{\"sitePostal\":\"75201-0000\",\"coSInd\":\"Y\",\"hicapFlxMinBwCommit\":\"20\",\"siteState\":\"TX\",\"accessSpeed\":\"GE:20M\",\"coSRate\":\"79.5\",\"shadowArrangement\":\"Service\",\"pNTUpliftRate\":\"0\",\"billOption\":\"MIS Hi-Cap Flex\",\"accessTechnology\":\"GIGABIT ETHERNET\",\"misPortRate\":\"346.2\",\"siteCountry\":\"US\",\"siteAddr1\":\"1601 ELM ST,DALLAS,TX  75201\",\"managedRouter\":\"N\",\"pNTSvcInd\":\"MIS\",\"siteCity\":\"DALLAS\",\"portSpeed\":\"20M\",\"coSSpeed\":\"20000\",\"circuitId\":\"MMEC976644ATI\",\"quantity\":\"1\",\"FALLOUTMATCHINGID\":\"0000000004/InrMISPNTResponse/Body/AccountDetails/SAPIDDetails/FALLOUTMATCHINGID\",\"uniqueIds\":[{\"elementType\":\"Features\",\"uniqueId\":\"#US#Schedule 3#ADI Port#Ethernet Port#20 Mbps#Ethernet#MIS (w/ Managed Router)#20 Mbps#Tele-Install#w/ Router Bundle#2821-EA#US#United States#per Port\",\"misPortRate\":\"346.2\",\"coSRate\":\"79.5\",\"pNTUpliftRate\":\"0\"},{\"elementType\":\"CoS\",\"uniqueId\":\"#US#ADI CoS#Hi Cap Flex#20 Mbps#per CoS#US#United States#Schedule 3\",\"misPortRate\":\"346.2\",\"coSRate\":\"79.5\",\"pNTUpliftRate\":\"0\"}],\"nxSiteId\":6,\"product\":\"MIS/PNT\"}");
		methodParam.put(MyPriceConstants.OFFER_NAME, "MIS/PNT");
		methodParam.put(MyPriceConstants.SOURCE, MyPriceConstants.SOURCE_INR);
		List<NxLookupData> rulesData = new ArrayList<>();
		NxLookupData lookup1 = new NxLookupData();
		lookup1.setCriteria("$..[?(@.elementType=='CoS')]%{\"uniqueID_ADIFeatures_pf\":\"uniqueId\",\"billingArrayController_ADIFeatures_pf\":\"size\",\"existing_ADIFeatures_pf\":\"quantity\",\"existingMRC_ADIFeatures_pf\":\"coSRate\"}");
		rulesData.add(lookup1);
		Mockito.when(nxLookupDataRepository.findByDatasetNameAndItemIdAndDescription(anyString(), anyString(), anyString())).thenReturn(rulesData);
		
		TypeRef<List<Object>> mapType = new TypeRef<List<Object>>() {};
		JsonPathUtil jutil = new JsonPathUtil();
		List<Object> data=jutil.search(inputDesign, "$..[?(@.elementType=='Features')]", mapType);
		
		NexxusJsonUtility nj=new NexxusJsonUtility();
		when(nexxusJsonUtility.getValueLst(any(),any())).thenReturn(data);
		HashMap criteriaMap=(HashMap<String, String>) nj.convertStringJsonToMap("{\"uniqueID_ADIFeatures_pf\":\"uniqueId\",\"billingArrayController_ADIFeatures_pf\":\"size\",\"existing_ADIFeatures_pf\":\"quantity\",\"existingMRC_ADIFeatures_pf\":\"AccessRate,UnilinkRate\"}");
		when(nexxusJsonUtility.convertStringJsonToMap(Mockito.anyString())).thenReturn(criteriaMap);
		
		configAndUpdatePricingUtil.processConfigDataFromCustomeRules(methodParam, inputDesign);
		
	}
	
	@Test
	public void testFilterDocument() {
		configAndUpdatePricingUtil.getSolutionFilterDocument();
		configAndUpdatePricingUtil.getDesignFilterDocument();
	}
	
	@Test
	public void testGetHideInTransactionResponseValue() {
		configAndUpdatePricingUtil.getHideInTransactionResponseValue(methodParam);
		methodParam.put(MyPriceConstants.IS_LAST_DESIGN, true);
		configAndUpdatePricingUtil.getHideInTransactionResponseValue(methodParam);
		methodParam.remove(MyPriceConstants.IS_LAST_DESIGN);
	}
	
	@Test
	public void testGetProductLine() {
		methodParam.put(MyPriceConstants.OFFER_NAME, "BVoIP Non-Usage");
		configAndUpdatePricingUtil.getProductLine(methodParam);
		
		methodParam.put(MyPriceConstants.OFFER_NAME, "ADI");
		configAndUpdatePricingUtil.getProductLine(methodParam);
		methodParam.remove(MyPriceConstants.OFFER_NAME);
	}
	
	@Test
	public void testGetUsocIdFromRequestFmo() {
		configAndUpdatePricingUtil.getUsocIdFromRequestFmo("uniqueId", inputDesignDetails);
	}
	
	@Test
	public void testGetDesignDetailsByPortId() {
		List<NxDesignDetails> nxDesignDetailslst = new ArrayList<>();
		NxDesignDetails design = new NxDesignDetails();
		design.setComponentId("1010");
		nxDesignDetailslst.add(design);
		configAndUpdatePricingUtil.getDesignDetailsByPortId(methodParam, nxDesignDetailslst);
		
		methodParam.put(FmoConstants.PORT_ID, "1010");
		configAndUpdatePricingUtil.getDesignDetailsByPortId(methodParam, nxDesignDetailslst);
		methodParam.remove(FmoConstants.PORT_ID);
	}
	
	@Test
	public void testGetCountryFromRequest() {
		configAndUpdatePricingUtil.getCountryFromRequest(data);
	}
	
	@Test
	public void testProcessCustomeFieldsUsingNxLookupData() {
		methodParam.put(MyPriceConstants.OFFER_NAME, "ADI/TDM");
		methodParam.put(MyPriceConstants.SOURCE, MyPriceConstants.SOURCE_FMO);
		List<String> dataLst = new ArrayList<>();
		dataLst.add("30538");
		Mockito.when(jsonPathUtil.search(any(), anyString(), any())).thenReturn(dataLst);
		NxLookupData rulesData = new NxLookupData();
		rulesData.setCriteria("{\"Speed_LocalAccess_pf\":\"1.544 Mbps\",\"Type_LocalAccess_pf\":\"Interstate\",\"Category_LocalAccess_pf\":\"LD DS0/DS1 Access Bundled\",\"TDMQuantity_pf\":\"4\"}");
		Mockito.when(nxLookupDataRepository.findTopByDatasetNameAndItemId(anyString(), anyString())).thenReturn(rulesData);
		configAndUpdatePricingUtil.processCustomeFieldsUsingNxLookupData(methodParam, data);
	}
	
	@Test
	public void testCollectDataByProductIdForResponseProcessing() {
		Map<String,Map<String,Object>> configResponseProcessingData = new HashMap<String,Map<String,Object>>();
		methodParam.put(MyPriceConstants.CONFIG_RESPONSE_PROCESSING_DATA, configResponseProcessingData);
		methodParam.put(MyPriceConstants.UNIQUEID_BEID_MAP, "EXPTQ");
		methodParam.put(MyPriceConstants.NX_DESIGN_ID, 1010L);
		methodParam.put(FmoConstants.PORT_ID, "1010");
		methodParam.put(MyPriceConstants.NX_ACCESS_PRICE_ID, "111");
		configAndUpdatePricingUtil.collectDataByProductIdForResponseProcessing(methodParam, "3");
		
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testGetUsocIdCategory() {
		String offerName="ASE";
		Map<String,String> usocFieldsDataMap=new HashMap<String, String>();
		usocFieldsDataMap.put("rateGroup", "OEM_Business_OOR");
		methodParam.put(MyPriceConstants.REQ_MCAD_ACTIVITY_UDFATTR, Arrays.asList("123","234"));
		List<NxLookupData> mcadOrder=new ArrayList<NxLookupData>();
		NxLookupData d=new NxLookupData();
		d.setItemId("New");
		d.setDescription("New");
		mcadOrder.add(d);
		methodParam.put(MyPriceConstants.ASE_MCAD_ORDER_DATA, mcadOrder);
		LinkedHashMap<String,String> usocLookupDataAse=new LinkedHashMap<String, String>();
		usocLookupDataAse.put("123", "New");
		Mockito.when(nxMyPriceRepositoryServce.getDataFromLookup(anyString())).thenReturn(usocLookupDataAse);
		methodParam.put(MyPriceConstants.REQ_MCAD_TYPE_VALUE, "Change");
		List<String> str = new ArrayList<String>();
		str.add("Change");
		Mockito.when(jsonPathUtil.search(anyString(), anyString(), any())).thenReturn(str);
		
		List<NxLookupData> rules=new ArrayList<NxLookupData>();
		NxLookupData r=new NxLookupData();
		r.setDescription("{\"rateGroup\":[\"OEM_Business_OOR\",\"OEM_Business\",\"OEM_NonCritical\",\"OEM_RealTime_OOR\",\"OEM_RealTime\",\"OEM_CritData\",\"OEM_NonCritical_OOR\",\"OEM_BusData\",\"OEM_BusinessH\"]}");
		rules.add(r);
		Mockito.when(nxLookupDataRepository.getNewExistingMigrationRules(anyString(), anyString())).thenReturn(rules);
		Map  ruleMap=new HashMap<String, List<String>>();
		ruleMap.put("rateGroup", new ArrayList<String>(Arrays.asList("OEM_Business_OOR")));
		Mockito.when(nexxusJsonUtility.convertStringJsonToMap(any())).thenReturn(ruleMap);
		configAndUpdatePricingUtil.getUsocIdCategory(inputDesignDetails, offerName, usocFieldsDataMap, methodParam);
	}
	

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testGetUsocIdCategory1() {
		String offerName="ASE";
		Map<String,String> usocFieldsDataMap=new HashMap<String, String>();
		usocFieldsDataMap.put("rateGroup", "PORT");
		methodParam.put(MyPriceConstants.REQ_MCAD_ACTIVITY_UDFATTR, Arrays.asList("123"));
		LinkedHashMap<String,String> usocLookupDataAse=new LinkedHashMap<String, String>();
		usocLookupDataAse.put("123", "New");
		Mockito.when(nxMyPriceRepositoryServce.getDataFromLookup(anyString())).thenReturn(usocLookupDataAse);
		List<String> str = new ArrayList<String>();
		str.add("Change");
		Mockito.when(jsonPathUtil.search(anyString(), anyString(), any())).thenReturn(str);
		
		List<NxLookupData> rules=new ArrayList<NxLookupData>();
		NxLookupData r=new NxLookupData();
		r.setDescription("{\"rateGroup\":[\"OEM_Business_OOR\",\"OEM_Business\",\"OEM_NonCritical\",\"OEM_RealTime_OOR\",\"OEM_RealTime\",\"OEM_CritData\",\"OEM_NonCritical_OOR\",\"OEM_BusData\",\"OEM_BusinessH\"]}");
		rules.add(r);
		Mockito.when(nxLookupDataRepository.getNewExistingMigrationRules(anyString(), anyString())).thenReturn(rules);
		Map  ruleMap=new HashMap<String, List<String>>();
		ruleMap.put("rateGroup", new ArrayList<String>(Arrays.asList("OEM_Business_OOR")));
		Mockito.when(nexxusJsonUtility.convertStringJsonToMap(any())).thenReturn(ruleMap);
		configAndUpdatePricingUtil.getUsocIdCategory(inputDesignDetails, offerName, usocFieldsDataMap, methodParam);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testGetUsocIdCategory2() {
		String offerName="ASE";
		Map<String,String> usocFieldsDataMap=new HashMap<String, String>();
		usocFieldsDataMap.put("rateGroup", "PORT");
		methodParam.put(MyPriceConstants.REQ_MCAD_ACTIVITY_UDFATTR, Arrays.asList("123"));
		LinkedHashMap<String,String> usocLookupDataAse=new LinkedHashMap<String, String>();
		usocLookupDataAse.put("123", "New");
		Mockito.when(nxMyPriceRepositoryServce.getDataFromLookup(anyString())).thenReturn(usocLookupDataAse);
		methodParam.put(MyPriceConstants.REQ_MCAD_TYPE_VALUE, "Change");
		List<String> str = new ArrayList<String>();
		str.add("Change");
		Mockito.when(jsonPathUtil.search(anyString(), anyString(), any())).thenReturn(str);
		
		List<NxLookupData> rules=new ArrayList<NxLookupData>();
		NxLookupData r=new NxLookupData();
		r.setDescription("{\"rateGroup\":[\"OEM_Business_OOR\",\"OEM_Business\",\"OEM_NonCritical\",\"OEM_RealTime_OOR\",\"OEM_RealTime\",\"OEM_CritData\",\"OEM_NonCritical_OOR\",\"OEM_BusData\",\"OEM_BusinessH\"]}");
		rules.add(r);
		Mockito.when(nxLookupDataRepository.getNewExistingMigrationRules(anyString(), anyString())).thenReturn(rules);
		Map  ruleMap=new HashMap<String, List<String>>();
		ruleMap.put("rateGroup", new ArrayList<String>(Arrays.asList("OEM_Business_OOR")));
		Mockito.when(nexxusJsonUtility.convertStringJsonToMap(any())).thenReturn(ruleMap);
		configAndUpdatePricingUtil.getUsocIdCategory(inputDesignDetails, offerName, usocFieldsDataMap, methodParam);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testGetUsocIdCategory3() {
		String offerName="ASENoD";
		Map<String,String> usocFieldsDataMap=new HashMap<String, String>();
		usocFieldsDataMap.put("rateGroup", "PORT");
		methodParam.put(MyPriceConstants.REQ_MCAD_ACTIVITY_UDFATTR, Arrays.asList("123","234"));
		LinkedHashMap<String,String> usocLookupDataAse=new LinkedHashMap<String, String>();
		usocLookupDataAse.put("123", "New");
		Mockito.when(nxMyPriceRepositoryServce.getDataFromLookup(anyString())).thenReturn(usocLookupDataAse);
		
		List<String> str = new ArrayList<String>();
		str.add("Change");
		Mockito.when(jsonPathUtil.search(anyString(), anyString(), any())).thenReturn(str);
		
		List<NxLookupData> rules=new ArrayList<NxLookupData>();
		NxLookupData r=new NxLookupData();
		r.setDescription("{\"rateGroup\":[\"OEM_Business_OOR\",\"OEM_Business\",\"OEM_NonCritical\",\"OEM_RealTime_OOR\",\"OEM_RealTime\",\"OEM_CritData\",\"OEM_NonCritical_OOR\",\"OEM_BusData\",\"OEM_BusinessH\"]}");
		rules.add(r);
		Mockito.when(nxLookupDataRepository.getNewExistingMigrationRules(anyString(), anyString())).thenReturn(rules);
		Map  ruleMap=new HashMap<String, List<String>>();
		ruleMap.put("rateGroup", new ArrayList<String>());
		Mockito.when(nexxusJsonUtility.convertStringJsonToMap(any())).thenReturn(ruleMap);
		configAndUpdatePricingUtil.getUsocIdCategory(inputDesignDetails, offerName, usocFieldsDataMap, methodParam);
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testGetUsocIdCategory4() {
		String offerName="ADE";
		Map<String,String> usocFieldsDataMap=new HashMap<String, String>();
		usocFieldsDataMap.put("rateGroup", "PORT");
		methodParam.put(MyPriceConstants.REQ_MCAD_ACTIVITY_UDFATTR, Arrays.asList("123","234"));
		LinkedHashMap<String,String> usocLookupDataAse=new LinkedHashMap<String, String>();
		usocLookupDataAse.put("123", "New");
		Mockito.when(nxMyPriceRepositoryServce.getDataFromLookup(anyString())).thenReturn(usocLookupDataAse);
		
		List<String> str = new ArrayList<String>();
		str.add("Change");
		Mockito.when(jsonPathUtil.search(anyString(), anyString(), any())).thenReturn(str);
		
		List<NxLookupData> rules=new ArrayList<NxLookupData>();
		NxLookupData r=new NxLookupData();
		r.setDescription("{\"rateGroup\":[\"OEM_Business_OOR\",\"OEM_Business\",\"OEM_NonCritical\",\"OEM_RealTime_OOR\",\"OEM_RealTime\",\"OEM_CritData\",\"OEM_NonCritical_OOR\",\"OEM_BusData\",\"OEM_BusinessH\"]}");
		rules.add(r);
		Mockito.when(nxLookupDataRepository.getNewExistingMigrationRules(anyString(), anyString())).thenReturn(rules);
		Map  ruleMap=null;
		Mockito.when(nexxusJsonUtility.convertStringJsonToMap(any())).thenReturn(ruleMap);
		configAndUpdatePricingUtil.getUsocIdCategory(inputDesignDetails, offerName, usocFieldsDataMap, methodParam);
	}
	
	@Test
	public void getPortQtyPfTest() {
		methodParam.put(MyPriceConstants.REQ_MCAD_ACTIVITY_UDFATTR, Arrays.asList("123","234"));
		methodParam.put(MyPriceConstants.REQ_MCAD_TYPE_VALUE, "Change");
		LinkedHashMap<String,String> portQtyPfLookupData=new LinkedHashMap<String, String>();
		portQtyPfLookupData.put("123", "New");
		List<NxLookupData> mcadOrder=new ArrayList<NxLookupData>();
		NxLookupData d=new NxLookupData();
		d.setItemId("New");
		d.setDescription("New");
		mcadOrder.add(d);
		methodParam.put(MyPriceConstants.ASE_MCAD_ORDER_DATA, mcadOrder);
		Mockito.when(nxMyPriceRepositoryServce.getDataFromLookup(any())).thenReturn(portQtyPfLookupData);
		configAndUpdatePricingUtil.getPortQtyPf(methodParam);
	}
	
	@Test
	public void getPortQtyPfTest1() {
		methodParam.put(MyPriceConstants.REQ_MCAD_ACTIVITY_UDFATTR, Arrays.asList("123"));
		methodParam.put(MyPriceConstants.REQ_MCAD_TYPE_VALUE, "Change");
		LinkedHashMap<String,String> portQtyPfLookupData=new LinkedHashMap<String, String>();
		portQtyPfLookupData.put("123", "New");
		List<NxLookupData> mcadOrder=new ArrayList<NxLookupData>();
		NxLookupData d=new NxLookupData();
		d.setItemId("New");
		d.setDescription("New");
		mcadOrder.add(d);
		methodParam.put(MyPriceConstants.ASE_MCAD_ORDER_DATA, mcadOrder);
		Mockito.when(nxMyPriceRepositoryServce.getDataFromLookup(any())).thenReturn(portQtyPfLookupData);
		configAndUpdatePricingUtil.getPortQtyPf(methodParam);
	}
	
	@Test
	public void getPortQtyPfTest2() {
		methodParam.put(MyPriceConstants.REQ_MCAD_ACTIVITY_UDFATTR, Arrays.asList("123"));
		
		LinkedHashMap<String,String> portQtyPfLookupData=new LinkedHashMap<String, String>();
		portQtyPfLookupData.put("123", "New");
		List<NxLookupData> mcadOrder=new ArrayList<NxLookupData>();
		NxLookupData d=new NxLookupData();
		d.setItemId("New");
		d.setDescription("New");
		mcadOrder.add(d);
		methodParam.put(MyPriceConstants.ASE_MCAD_ORDER_DATA, mcadOrder);
		Mockito.when(nxMyPriceRepositoryServce.getDataFromLookup(any())).thenReturn(portQtyPfLookupData);
		configAndUpdatePricingUtil.getPortQtyPf(methodParam);
	}
	
	
	@Test
	public void getCIRQtyPfTest() {
		methodParam.put(MyPriceConstants.REQ_MCAD_ACTIVITY_UDFATTR, Arrays.asList("123","234"));
		methodParam.put(MyPriceConstants.REQ_MCAD_TYPE_VALUE, "Change");
		LinkedHashMap<String,String> portQtyPfLookupData=new LinkedHashMap<String, String>();
		portQtyPfLookupData.put("123", "New");
		List<NxLookupData> mcadOrder=new ArrayList<NxLookupData>();
		NxLookupData d=new NxLookupData();
		d.setItemId("New");
		d.setDescription("New");
		mcadOrder.add(d);
		methodParam.put(MyPriceConstants.ASE_MCAD_ORDER_DATA, mcadOrder);
		Mockito.when(nxMyPriceRepositoryServce.getDataFromLookup(any())).thenReturn(portQtyPfLookupData);
		configAndUpdatePricingUtil.getCIRQtyPf(methodParam);
	}
	
	@Test
	public void getCIRQtyPfTest1() {
		methodParam.put(MyPriceConstants.REQ_MCAD_ACTIVITY_UDFATTR, Arrays.asList("123"));
		methodParam.put(MyPriceConstants.REQ_MCAD_TYPE_VALUE, "Change");
		LinkedHashMap<String,String> portQtyPfLookupData=new LinkedHashMap<String, String>();
		portQtyPfLookupData.put("123", "New");
		List<NxLookupData> mcadOrder=new ArrayList<NxLookupData>();
		NxLookupData d=new NxLookupData();
		d.setItemId("New");
		d.setDescription("New");
		mcadOrder.add(d);
		methodParam.put(MyPriceConstants.ASE_MCAD_ORDER_DATA, mcadOrder);
		Mockito.when(nxMyPriceRepositoryServce.getDataFromLookup(any())).thenReturn(portQtyPfLookupData);
		configAndUpdatePricingUtil.getCIRQtyPf(methodParam);
	}
	
	@Test
	public void getCIRQtyPfTest2() {
		methodParam.put(MyPriceConstants.REQ_MCAD_ACTIVITY_UDFATTR, Arrays.asList("123"));
		
		LinkedHashMap<String,String> portQtyPfLookupData=new LinkedHashMap<String, String>();
		portQtyPfLookupData.put("123", "New");
		List<NxLookupData> mcadOrder=new ArrayList<NxLookupData>();
		NxLookupData d=new NxLookupData();
		d.setItemId("New");
		d.setDescription("New");
		mcadOrder.add(d);
		methodParam.put(MyPriceConstants.ASE_MCAD_ORDER_DATA, mcadOrder);
		Mockito.when(nxMyPriceRepositoryServce.getDataFromLookup(any())).thenReturn(portQtyPfLookupData);
		configAndUpdatePricingUtil.getCIRQtyPf(methodParam);
	}

}
