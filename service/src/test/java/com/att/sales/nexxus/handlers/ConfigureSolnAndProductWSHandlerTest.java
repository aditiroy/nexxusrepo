package com.att.sales.nexxus.handlers;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyMap;
import static org.mockito.Mockito.doNothing;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.simple.JSONObject;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ws.soap.client.SoapFaultClientException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.nexxus.common.CommonConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.dao.model.NxDesign;
import com.att.sales.nexxus.dao.model.NxDesignDetails;
import com.att.sales.nexxus.dao.model.NxMpConfigMapping;
import com.att.sales.nexxus.dao.model.NxMpDesignDocument;
import com.att.sales.nexxus.dao.repository.NxDesignRepository;
import com.att.sales.nexxus.myprice.transaction.dao.service.NxMpRepositoryService;
import com.att.sales.nexxus.myprice.transaction.service.ConfigAndUpdatePricingUtil;
import com.att.sales.nexxus.reteriveicb.model.Site;
import com.att.sales.nexxus.util.JacksonUtil;
import com.att.sales.nexxus.util.NexxusJsonUtility;
import com.att.sales.nexxus.ws.utility.ConfigureSolnAndProductWSClientUtility;
import com.att.sales.nexxus.ws.utility.WSProcessingService;
import com.oracle.xmlns.cpqcloud.configuration.wireline.solution.solution.Configure;
import com.oracle.xmlns.cpqcloud.configuration.wireline.solution.solution.ConfigureResponse;
import com.oracle.xmlns.cpqcloud.configuration.wireline.solution.solution.ObjectFactory;
@ExtendWith(MockitoExtension.class)
public class ConfigureSolnAndProductWSHandlerTest {
	
	@Spy
	@InjectMocks
	private ConfigureSolnAndProductWSHandler configureSolnAndProductWSHandler;

	@Mock
	private NxMpRepositoryService nxMpRepositoryService;

	@Mock
	private WSProcessingService wsProcessingService;

	@Mock
	private NexxusJsonUtility nexxusJsonUtility;

	@Mock
	private ConfigAndUpdatePricingUtil configAndUpdatePricingUtil;
	
	@Mock
	private NxDesignRepository nxDesignRepository;
	
	@Mock
	private ConfigureSolnAndProductWSClientUtility configureSolnAndProductWSClientUtility;

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@BeforeEach
	public void initializeServiceMetaData() {
		Map<String, Object> requestParams = new HashMap<>();
		requestParams.put(ServiceMetaData.OFFER, "AVPN");
		requestParams.put(ServiceMetaData.REST_REQUEST_START_TIME, System.currentTimeMillis());
		requestParams.put(ServiceMetaData.VERSION, "1.0");
		requestParams.put(ServiceMetaData.METHOD, "TestMethod");
		requestParams.put(ServiceMetaData.URI, "Testuri");
		ServiceMetaData.add(requestParams);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void test() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("myPriceTransId", "42373160");
		map.put("nxTxnId", 21L);
		map.put("offerName", "ASE");
		map.put("nxDesignId", 10L);
		map.put("site", getSite());
		NxDesign nxDesign = new NxDesign();
		NxDesignDetails nxDesignDetails = new NxDesignDetails();
		String json = "{\"nxSiteId\":443977,\"siteId\":99988946330,\"npanxx\":\"714229\",\"address1\":\"6555 Katella Ave\",\"city\":\"LAKE ZURICH\",\"state\":\"IL\",\"saLecName\":null,\"speedId\":null,\"saLecSwClli\":null,\"popClli\":null,\"country\":\"US\",\"macdType\":\"Change\",\"macdActivity\":\"Change Ethernet Payment Plan\",\"fromInventory\":null,\"dualSiteId\":null,\"onNetCheck\":null,\"ethernetVendor\":null,\"designSiteOfferPort\":[{\"designStatus\":null,\"typeOfInventory\":null,\"milesResult\":null,\"securityDesignDetails\":null,\"macdActivityType\":null,\"component\":[{\"componentCodeId\":10,\"fromInvYN\":null,\"logicalChannelPvcID\":null,\"eteVpnKey\":null,\"diversityGroupId\":null,\"componentCodeType\":\"Connection\",\"componentId\":null,\"externalField\":null,\"parentComponentId\":null,\"externalKeyRef\":null,\"mvlInd\":null,\"userEnteredVpn\":null,\"siteId\":null,\"logicalChannelId\":null,\"designDetails\":[{\"udfAttributeId\":[30595],\"udfId\":20030,\"readOnly\":null,\"udfAttributeText\":null,\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":\"Site Type\"},{\"udfAttributeId\":[30515],\"udfId\":20017,\"readOnly\":null,\"udfAttributeText\":null,\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":\"ServiceId\"}],\"routeTargets\":null,\"references\":null},{\"componentCodeId\":30,\"fromInvYN\":null,\"logicalChannelPvcID\":null,\"eteVpnKey\":null,\"diversityGroupId\":null,\"componentCodeType\":\"Port\",\"componentId\":null,\"externalField\":null,\"parentComponentId\":null,\"externalKeyRef\":null,\"mvlInd\":null,\"userEnteredVpn\":null,\"siteId\":null,\"logicalChannelId\":null,\"designDetails\":[{\"udfAttributeId\":[301475],\"udfId\":20173,\"readOnly\":null,\"udfAttributeText\":null,\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":null,\"udfId\":20184,\"readOnly\":null,\"udfAttributeText\":[\"CYPRCAAA\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":null,\"udfId\":20169,\"readOnly\":null,\"udfAttributeText\":[\"100000899000\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[301473],\"udfId\":20171,\"readOnly\":null,\"udfAttributeText\":null,\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[300302],\"udfId\":20013,\"readOnly\":null,\"udfAttributeText\":null,\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[300249],\"udfId\":200006,\"readOnly\":null,\"udfAttributeText\":null,\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[300251],\"udfId\":200008,\"readOnly\":null,\"udfAttributeText\":null,\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[30604],\"udfId\":200009,\"readOnly\":null,\"udfAttributeText\":null,\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[301777],\"udfId\":200010,\"readOnly\":null,\"udfAttributeText\":null,\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[30604],\"udfId\":200011,\"readOnly\":null,\"udfAttributeText\":null,\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[30605],\"udfId\":200012,\"readOnly\":null,\"udfAttributeText\":null,\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[30604],\"udfId\":200013,\"readOnly\":null,\"udfAttributeText\":null,\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[300277],\"udfId\":200018,\"readOnly\":null,\"udfAttributeText\":null,\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[300284],\"udfId\":200020,\"readOnly\":null,\"udfAttributeText\":null,\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[300294],\"udfId\":200021,\"readOnly\":null,\"udfAttributeText\":null,\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[301475],\"udfId\":200022,\"readOnly\":null,\"udfAttributeText\":null,\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[30604],\"udfId\":200027,\"readOnly\":null,\"udfAttributeText\":null,\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[30605],\"udfId\":200028,\"readOnly\":null,\"udfAttributeText\":null,\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[74933],\"udfId\":200031,\"readOnly\":null,\"udfAttributeText\":null,\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":null,\"udfId\":200032,\"readOnly\":null,\"udfAttributeText\":[\"0\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[300364],\"udfId\":200033,\"readOnly\":null,\"udfAttributeText\":null,\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[300367],\"udfId\":200035,\"readOnly\":null,\"udfAttributeText\":null,\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[300370],\"udfId\":200036,\"readOnly\":null,\"udfAttributeText\":null,\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[300373],\"udfId\":200037,\"readOnly\":null,\"udfAttributeText\":null,\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[300378],\"udfId\":200040,\"readOnly\":null,\"udfAttributeText\":null,\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[30604],\"udfId\":200048,\"readOnly\":null,\"udfAttributeText\":null,\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[300401],\"udfId\":200058,\"readOnly\":null,\"udfAttributeText\":null,\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[55064],\"udfId\":200059,\"readOnly\":null,\"udfAttributeText\":null,\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[30604],\"udfId\":200060,\"readOnly\":null,\"udfAttributeText\":null,\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[30604],\"udfId\":200075,\"readOnly\":null,\"udfAttributeText\":null,\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[30604],\"udfId\":200079,\"readOnly\":null,\"udfAttributeText\":null,\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":null,\"udfId\":200007,\"readOnly\":null,\"udfAttributeText\":[\"15/KRGS/141954/CE\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":null,\"udfId\":200014,\"readOnly\":null,\"udfAttributeText\":[\"2020-07-22 00:00:00.0\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":null,\"udfId\":200017,\"readOnly\":null,\"udfAttributeText\":[\"Recap\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[300359],\"udfId\":200029,\"readOnly\":null,\"udfAttributeText\":null,\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":null,\"udfId\":200045,\"readOnly\":null,\"udfAttributeText\":[\"ANHMCA11\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":null,\"udfId\":200046,\"readOnly\":null,\"udfAttributeText\":[\"7\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":null,\"udfId\":200047,\"readOnly\":null,\"udfAttributeText\":[\"7\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":null,\"udfId\":200057,\"readOnly\":null,\"udfAttributeText\":[\"yy\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":null,\"udfId\":200062,\"readOnly\":null,\"udfAttributeText\":[\"PACIFIC BELL\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[300320],\"udfId\":200133,\"readOnly\":null,\"udfAttributeText\":null,\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[301508],\"udfId\":200134,\"readOnly\":null,\"udfAttributeText\":null,\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[30604],\"udfId\":22019,\"readOnly\":null,\"udfAttributeText\":null,\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[30604],\"udfId\":22020,\"readOnly\":null,\"udfAttributeText\":null,\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[30604],\"udfId\":22021,\"readOnly\":null,\"udfAttributeText\":null,\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[605128],\"udfId\":21860,\"readOnly\":null,\"udfAttributeText\":null,\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[302216],\"udfId\":21861,\"readOnly\":null,\"udfAttributeText\":null,\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[55065],\"udfId\":21862,\"readOnly\":null,\"udfAttributeText\":null,\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[605334],\"udfId\":21960,\"readOnly\":null,\"udfAttributeText\":null,\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":null,\"udfId\":21961,\"readOnly\":null,\"udfAttributeText\":[\"999\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[605339],\"udfId\":21962,\"readOnly\":null,\"udfAttributeText\":null,\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null}],\"routeTargets\":null,\"references\":null}],\"routerDetails\":null,\"logicalChannelDetail\":null,\"customDesign\":null,\"accessPricingAQ\":null,\"aggregateBilling\":null,\"portValidationMessage\":null,\"voiceFeatureDetail\":null}],\"priceDetails\":{\"componentDetails\":[{\"componentCodeId\":null,\"componentCodeType\":null,\"componentId\":99988946330,\"componentType\":\"Site\",\"componentParentId\":6512689,\"componentAttributes\":[{\"componentFieldName\":\"Country\",\"componentFieldValue\":\"US\"}],\"priceAttributes\":null,\"scpPriceMessages\":null},{\"componentCodeId\":null,\"componentCodeType\":null,\"componentId\":10818682,\"componentType\":\"Port\",\"componentParentId\":99988946330,\"componentAttributes\":[{\"componentFieldName\":\"Country\",\"componentFieldValue\":\"US\"}],\"priceAttributes\":[{\"productRateId\":0,\"beid\":\"EYQFX\",\"rateDescription\":null,\"priceCatalog\":\"Standard Pricing\",\"localListPrice\":660.0,\"targetListPrice\":null,\"icbDesiredDiscPerc\":null,\"priceType\":\"PORT\",\"priceUnit\":\"Monthly\",\"frequency\":\"MRC\",\"monthlySurcharge\":null,\"discount\":null,\"discountId\":null,\"quantity\":\"1\",\"localNetPrice\":null,\"targetNetPrice\":null,\"localTotalPrice\":null,\"targetTotalPrice\":null,\"localCurrency\":\"USD\",\"targetCurrency\":\"USD\",\"rdsPriceType\":\"PORT\",\"priceName\":\"1 Gb\",\"typeOfInventory\":null,\"priceInUSD\":null,\"priceScenarioId\":6494605,\"rateGroup\":\"OEM_PORT\",\"externalBillingSystem\":null,\"ratePlanId\":null,\"priceCompType\":\"ASE\",\"pvcId\":null,\"chargeCodeId\":null,\"requestedNRCRate\":null,\"requestedNRCDiscPercentage\":null,\"requestedMRCRate\":null,\"requestedMRCDiscPercentage\":null,\"priceModifiedInd\":null,\"requestedDiscount\":null,\"requestedRate\":null,\"term\":null,\"priceGroup\":null,\"referencePortId\":null,\"componentType\":null,\"componentParentId\":null,\"country\":null,\"secondaryKeys\":null,\"lineItemId\":null,\"nrcBeid\":null,\"uniqueId\":null,\"elementType\":null,\"reqPriceType\":null},{\"productRateId\":0,\"beid\":\"R6EZX\",\"rateDescription\":null,\"priceCatalog\":\"Standard Pricing\",\"localListPrice\":2772.0,\"targetListPrice\":null,\"icbDesiredDiscPerc\":null,\"priceType\":\"COS\",\"priceUnit\":\"Monthly\",\"frequency\":\"MRC\",\"monthlySurcharge\":null,\"discount\":null,\"discountId\":null,\"quantity\":\"1\",\"localNetPrice\":null,\"targetNetPrice\":null,\"localTotalPrice\":null,\"targetTotalPrice\":null,\"localCurrency\":\"USD\",\"targetCurrency\":\"USD\",\"rdsPriceType\":\"COS\",\"priceName\":\"1000 Mb\",\"typeOfInventory\":null,\"priceInUSD\":null,\"priceScenarioId\":6494605,\"rateGroup\":\"OEM_REALTIME\",\"externalBillingSystem\":null,\"ratePlanId\":null,\"priceCompType\":\"ASE\",\"pvcId\":null,\"chargeCodeId\":null,\"requestedNRCRate\":null,\"requestedNRCDiscPercentage\":null,\"requestedMRCRate\":null,\"requestedMRCDiscPercentage\":null,\"priceModifiedInd\":null,\"requestedDiscount\":null,\"requestedRate\":null,\"term\":null,\"priceGroup\":null,\"referencePortId\":null,\"componentType\":null,\"componentParentId\":null,\"country\":null,\"secondaryKeys\":null,\"lineItemId\":null,\"nrcBeid\":null,\"uniqueId\":null,\"elementType\":null,\"reqPriceType\":null}],\"scpPriceMessages\":null}],\"priceMessage\":null},\"address\":null,\"postalCode\":null,\"siteName\":\"tes1\",\"swcClli\":null,\"customerLocationClli\":null,\"active\":null,\"emc\":null,\"carrierHotel\":null,\"attComments\":null,\"newBuilding\":null,\"customerReference\":null,\"asrItemId\":\"100000899000\",\"lataCode\":\"520\",\"zipCode\":\"90630\",\"address2\":null,\"room\":null,\"floor\":null,\"building\":null,\"siteComment\":null,\"lconFirstName\":null,\"lconLastName\":null,\"lconPhone\":null,\"lconEmail\":null,\"endPointSiteIdentifier\":\"A\",\"buildingClli\":null,\"regionCode\":\"Y\",\"activityType\":null,\"cancellationReason\":null,\"product\":\"ASEoD\",\"quantity\":\"1\",\"nssEngagement\":null,\"designStatus\":\"N\",\"multiGigeIndicator\":null,\"alias\":\"tes1\",\"macdActionType\":null,\"lconDetails\":[{\"lconType\":null,\"lconFirstName\":null,\"lconLastName\":null,\"lconPhone\":null,\"lconEmail\":null}],\"globalLocationId\":null,\"jurisdiction\":\"FCC - 12 States\",\"certificationStatus\":null,\"designModifiedInd\":\"Y\",\"independentCarrierCompanyLATA\":null,\"independentCarrierCompanyName\":null,\"interDepartmentMeetPointChecklistURL\":null,\"diverseFromReferenceInfo\":null,\"opticalTerminatingNetworkCarrierFacilityAssignment\":null,\"collocationCarrierFacilityAssignment\":null,\"assetInvestmentSheetIndicator\":null,\"swcCertification\":\"Not Certified\",\"designVersion\":\"1\",\"specialConstructionPaymentUrl\":null,\"thirdPartyInd\":\"N\",\"accessCarrierNameAbbreviation\":null,\"design\":null,\"loopLength\":null,\"numOfCopperRepeaters\":null,\"inventoryNumOfPairs\":null,\"taskClli\":null,\"numberRemoteTerminals\":null,\"referenceOfferId\":null,\"siteNpanxx\":null,\"specialConstructionCharge\":null,\"specialConstructionHandling\":null,\"specialConstructionHandlingNotes\":null}";
		nxDesignDetails.setDesignData(json);
		nxDesign.setNxDesignId(1010L);
		nxDesign.setNxDesignDetails(new ArrayList<NxDesignDetails>() {{add(nxDesignDetails);}});
		Mockito.when(nxDesignRepository.findByNxDesignId(10L)).thenReturn(nxDesign);
		Mockito.when(configAndUpdatePricingUtil.getInputDesignDetails(nxDesign, "ASE")).thenReturn(JacksonUtil.toJsonObject(json));
		doNothing().when(configureSolnAndProductWSClientUtility).setWsName(MyPriceConstants.CONFIG_SOL_PRODUCT_WS);
		Mockito.when(nxMpRepositoryService.getDataByNxtxnId(21L)).thenReturn(new HashMap<String,String>());
		Mockito.when(wsProcessingService.initiateWebService(any(Configure.class),
				any(ConfigureSolnAndProductWSClientUtility.class), anyMap(), any(Class.class)))
				.thenReturn(getConfigureResponse());
		configureSolnAndProductWSHandler.initiateConfigSolnAndProdWebService(map);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testWithExceptionScenario() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("myPriceTransId", "42373160");
		map.put("nxTxnId", 21L);
		map.put("offerName", "ASE");
		map.put("designId", 10L);
		map.put("site", getSite());
		Mockito.when(wsProcessingService.initiateWebService(any(Configure.class),
				any(ConfigureSolnAndProductWSClientUtility.class), anyMap(), any(Class.class)))
				.thenThrow(SoapFaultClientException.class);
		configureSolnAndProductWSHandler.initiateConfigSolnAndProdWebService(map);
	}

	private List<NxMpConfigMapping> getconfigMappings() {
		List<NxMpConfigMapping> configMappings = new ArrayList<>();
		NxMpConfigMapping prodModelMapping = new NxMpConfigMapping();
		prodModelMapping.setVariableName("productModel_pm");
		prodModelMapping.setDefaultValue("ASE");
		NxMpConfigMapping siteAddressMapping = new NxMpConfigMapping();
		siteAddressMapping.setVariableName("solutionSiteAddress_pm");
		siteAddressMapping.setPath("$.siteId,#$.address1,#$.city,#$.state,#$.postalCode,#$.country");
		NxMpConfigMapping siteCountryMapping = new NxMpConfigMapping();
		siteCountryMapping.setVariableName("solutionSiteCountry_pm");
		siteCountryMapping.setPath("$.country");
		configMappings.add(prodModelMapping);
		configMappings.add(siteAddressMapping);
		configMappings.add(siteCountryMapping);
		return configMappings;
	}

	private NxMpDesignDocument getNxMpDesignDocument(Long transactionId, String docNum) {
		NxMpDesignDocument nxMpDesignDocument = new NxMpDesignDocument();
		nxMpDesignDocument.setNxTxnId(transactionId);
		nxMpDesignDocument.setMpSolutionId(docNum);
		nxMpDesignDocument.setActiveYN(CommonConstants.ACTIVE_Y);
		nxMpDesignDocument.setCreatedDate(new Date());
		nxMpDesignDocument.setNxDesignId(10L);
		return nxMpDesignDocument;
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

		transactionLine2.appendChild(lineBomId2);
		transactionLine2.appendChild(lineBomParentId2);
		transactionLine2.appendChild(documentNumber2);
		transactionLine2.appendChild(prodLineId2);
		transactionLine2.appendChild(parentDocNumber2);

		subDocuments.appendChild(transactionLine1);
		subDocuments.appendChild(transactionLine2);
		transaction.appendChild(subDocuments);
		document.appendChild(transaction);
		return transaction;
	}

	private Site getSite() {
		Site site = new Site();
		site.setSiteId(1L);
		site.setNxSiteId(3L);
		site.setAddress1("3500 PAN AMERICAN DR");
		site.setAddress2(null);
		site.setCity("MIAMI");
		site.setState("FL");
		site.setPostalCode("33133-5504");
		site.setCountry("US");
		return site;
	}
	
	@Test
	public void testGetThreadSize() {
		configureSolnAndProductWSHandler.getThreadSize();
	}
		
	@Test
	public void testProcessSolutionAndProductResponse() {
		Map<String, Object> requestMap = new HashMap<String, Object>();
		requestMap.put("nxTxnId", 12234L);
		Map<String,String> solProductIdDataMap = new HashMap<String,String>();
		solProductIdDataMap.put("2", "avpn");
		requestMap.put("solutionProdData", solProductIdDataMap);
		Mockito.when(nxMpRepositoryService.updateSolAndProductResponse("3", new Date(), "2", 12234L)).thenReturn(1);
		configureSolnAndProductWSHandler.processSolutionAndProductResponse(requestMap, "BOM_SolutionASE", "3", "2");
	}
	
	@Test
	public void testPrepareRequestBody() {
		Map<String, Object> requestMap = new HashMap<String, Object>();
		Configure configureRequest = new Configure();
		JSONObject site = JacksonUtil.toJsonObject("");
		//configureSolnAndProductWSHandler.prepareRequestBody(requestMap, configureRequest, site);
	}
}
