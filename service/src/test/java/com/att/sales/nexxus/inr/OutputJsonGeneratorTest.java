package com.att.sales.nexxus.inr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.constant.InrConstants;
import com.att.sales.nexxus.constant.OutputFileConstants;
import com.att.sales.nexxus.dao.model.NxKeyFieldPathModel;
import com.att.sales.nexxus.dao.model.NxLineItemLookUpDataModel;
import com.att.sales.nexxus.dao.model.NxLineItemLookUpFieldModel;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.model.NxOutputProductMappingModel;
import com.att.sales.nexxus.output.entity.NxAvpnOutputBean;
import com.att.sales.nexxus.output.entity.NxOutputBean;
import com.att.sales.nexxus.service.NexxusAIService;
import com.att.sales.nexxus.service.NxMyPriceRepositoryServce;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
@ExtendWith(MockitoExtension.class)
public class OutputJsonGeneratorTest {
	private JsonNodeFactory jsonNodeFactory = JsonNodeFactory.instance;
	@Mock
	private ObjectMapper mapper;
	@Mock
	private EntityManager em;
	@Mock
	private JsonNode intermediateJson;
	@Mock
	private NxOutputBean nxOutputBean;
	@Mock
	private Map<String, String> jsonEntry;
	@Mock
	private List<QueryLookup> lookups;
	@Mock
	private InrFallOutData fallOut;
	@Mock
	private Map<Map<String, String>, List<NxLineItemLookUpDataModel>> lineItemCache;
	@Spy
	@InjectMocks
	private OutputJsonGenerator outputJsonGenerator;
	@Mock
	private JsonNode jsonNode;
	@Mock
	private ObjectNode objectNode;
	@Mock
	private ArrayNode arrayNode;
	@Mock
	private Iterator<JsonNode> iterator;
	@Mock
	private Iterator<JsonNode> iterator1;
	@Mock
	private QueryLookup queryLookup;
	@Mock
	private Iterator<Entry<String, JsonNode>> iteratorEntry;
	@Mock
	private Entry<String, JsonNode> entry;
	@Mock
	private Iterator<String> iteratorString;
	@Mock
	private TypedQuery<NxLineItemLookUpFieldModel> typedQueryNxLineItemLookUpFieldModel;
	@Mock
	private TypedQuery<NxLineItemLookUpDataModel> typedQueryNxLineItemLookUpDataModel;
	@Mock
	private NexxusAIService nexxusAIService;
	@Mock
	private OutputJsonFallOutData outputJsonFallOutData;
	@Mock
	private List<Map<String, String>> queryParams;
	@Mock
	private Set<String> flags = new HashSet<>();
	@Mock
	private Map<String, ObjectNode> objectNodeMap;
	@Mock
	private NxMyPriceRepositoryServce nxMyPriceRepositoryServce;
	@Mock
	private Map<String,NxLookupData> nxKeyIdRules;
	@Mock
	private JsonProcessingException jsonProcessingException;
	
	private ObjectMapper realMapper = new ObjectMapper();

	@Test
	public void initTest() throws SalesBusinessException {
		doNothing().when(outputJsonGenerator).initializeLookups();
		ReflectionTestUtils.setField(outputJsonGenerator, "lookups", lookups);
		outputJsonGenerator.init();
	}

	@Test
	public void initTestException() throws SalesBusinessException {
		doNothing().when(outputJsonGenerator).initializeLookups();
		outputJsonGenerator.init();
	}

	@Test
	public void generateTest() throws SalesBusinessException, JsonProcessingException {
		doNothing().when(outputJsonGenerator).init();
		doNothing().when(outputJsonGenerator).generateHelper(any(), any());
		ReflectionTestUtils.setField(outputJsonGenerator, "fallOut", fallOut);
		when(fallOut.hasValue()).thenReturn(true);
		when(mapper.writeValueAsString(any())).thenReturn("");
		outputJsonGenerator.generate();
	}
	
	@Test
	public void generateTestException() throws SalesBusinessException, JsonProcessingException {
		doNothing().when(outputJsonGenerator).init();
		doNothing().when(outputJsonGenerator).generateHelper(any(), any());
		ReflectionTestUtils.setField(outputJsonGenerator, "fallOut", fallOut);
		when(fallOut.hasValue()).thenReturn(true);
		when(mapper.writeValueAsString(any())).thenThrow(jsonProcessingException);
		outputJsonGenerator.generate();
	}

	@Test
	public void generateHelperTest() {
		JsonPath rootPath = JsonPath.getRootPath();

		// case 1
		when(jsonNode.getNodeType()).thenReturn(JsonNodeType.ARRAY);
		doNothing().when(outputJsonGenerator).processArrayNode(any(), any());
		outputJsonGenerator.generateHelper(jsonNode, rootPath);

		// case 2
		when(jsonNode.getNodeType()).thenReturn(JsonNodeType.OBJECT);
		doNothing().when(outputJsonGenerator).processObjectNode(any(), any());
		outputJsonGenerator.generateHelper(jsonNode, rootPath);
		
		//case 3
		when(jsonNode.getNodeType()).thenReturn(JsonNodeType.STRING);
		outputJsonGenerator.generateHelper(jsonNode, rootPath);
	}
	
	@Test
	public void processObjectNodeTest() {
		ObjectNode node = realMapper.createObjectNode();
		node.put(InrIntermediateJsonGenerator.FALLOUTMATCHINGID, "0000002139/BVOIPPricingInventory/Body/AccountDetails/SubAccountUsage/UsageDetails/FALLOUTMATCHINGID");
		JsonPath path = new JsonPath("/path");
		doNothing().when(outputJsonGenerator).populateJsonFields(any());
		doNothing().when(outputJsonGenerator).lineItemLookup(any(), any());
		Map<String,NxLookupData> nxKeyIdRulesSample = new HashMap<>();
		ReflectionTestUtils.setField(outputJsonGenerator, "nxKeyIdRules", nxKeyIdRulesSample);
		NxLookupData nxLookupData = new NxLookupData();
		nxKeyIdRulesSample.put("/path", nxLookupData);
		ReflectionTestUtils.setField(outputJsonGenerator, "mapper", realMapper);
		nxLookupData.setCriteria("{\r\n" + 
				"    \"nxItemId\": \"Y\",\r\n" + 
				"    \"nxItemFields\": \"actualPrice\",\r\n" + 
				"    \"otherFields\": \"country\"\r\n" + 
				"}");
		node.put("nxItemId", "1");
		node.put("actualPrice", 1.1);
		ObjectNode copy = node.deepCopy();
		node.withArray("array").add(copy);
		Map<String, String> jsonEntrySample = new HashMap<>();
		ReflectionTestUtils.setField(outputJsonGenerator, "jsonEntry", jsonEntrySample);
		jsonEntrySample.put("country", "US");
		doNothing().when(outputJsonGenerator).unPopulateJsonFields(any());
		outputJsonGenerator.processObjectNode(node, path);
		
		List<Map<String, String>> queryParams = new ArrayList<>();
		List<InventoryJsonLookup> inventoryJsonLookups = new ArrayList<>();
		Map<String, String> queryParam = new HashMap<>();
		queryParam.put("1", "1");
		queryParams.add(queryParam);
		InventoryJsonLookup inventoryJsonLookup = new InventoryJsonLookup();
		inventoryJsonLookup.setLongForm(queryParam);
		inventoryJsonLookup.setShortForm(queryParam);
		inventoryJsonLookups.add(inventoryJsonLookup);
		doReturn(queryParams).when(outputJsonGenerator).getQueryParams();
		doReturn(inventoryJsonLookups).when(outputJsonGenerator).getInventoryJsonLookups();
		nxLookupData.setCriteria("{\r\n" + 
				"    \"nxItemId\": \"Y\",\r\n" + 
				"    \"otherFields\": \"country\"\r\n" + 
				"}");
		jsonEntrySample.remove("country");
		outputJsonGenerator.processObjectNode(node, path);
	}

	@Test
	public void lineItemLookupTest() {
		List<QueryLookup> lookups = Arrays.asList(queryLookup);
		ReflectionTestUtils.setField(outputJsonGenerator, "lookups", lookups);
		doReturn(true).when(outputJsonGenerator).isLookupApplicable(any(), any());
		NxLineItemLookUpDataModel nxLineItemLookUpDataModel = new NxLineItemLookUpDataModel();
		List<NxLineItemLookUpDataModel> lineItems = Arrays.asList(nxLineItemLookUpDataModel);
		doReturn(lineItems).when(outputJsonGenerator).findLineItem(any(), any());
		doNothing().when(outputJsonGenerator).createNexusOutput(any(), any());
		outputJsonGenerator.lineItemLookup(null, null);
	}

	@Test
	public void populateJsonFieldsTest() {
		ObjectNode node = realMapper.createObjectNode();
		node.put("1", "1");
		node.set("2", null);
		outputJsonGenerator.populateJsonFields(node);
	}

	@Test
	public void unPopulateJsonFieldsTest() {
		ReflectionTestUtils.setField(outputJsonGenerator, "jsonEntry", jsonEntry);
		when(jsonNode.fieldNames()).thenReturn(iteratorString);
		when(iteratorString.hasNext()).thenReturn(true, false);
		outputJsonGenerator.unPopulateJsonFields(jsonNode);
	}

	@Test
	public void initializeLookupsTest() {
		doReturn(null).when(outputJsonGenerator).findOfferName();
		doReturn(null).when(outputJsonGenerator).findLookupByOfferNameAndInputType(any(), any());
		ObjectNode node = realMapper.createObjectNode();
		ReflectionTestUtils.setField(outputJsonGenerator, "intermediateJson", node);
		outputJsonGenerator.initializeLookups();
	}

	@Test
	public void findLookupByOfferNameAndInputTypeTest() {
		when(em.createQuery(any(), eq(NxLineItemLookUpFieldModel.class)))
				.thenReturn(typedQueryNxLineItemLookUpFieldModel);
		NxLineItemLookUpFieldModel nxLineItemLookUpFieldModel = new NxLineItemLookUpFieldModel();
		List<NxLineItemLookUpFieldModel> resultList = Arrays.asList(nxLineItemLookUpFieldModel);
		when(typedQueryNxLineItemLookUpFieldModel.getResultList()).thenReturn(resultList);
		Set<NxKeyFieldPathModel> keyFieldMapping = new HashSet<>();
		nxLineItemLookUpFieldModel.setKeyFieldMapping(keyFieldMapping);
		NxKeyFieldPathModel nxKeyFieldPathModel = new NxKeyFieldPathModel();
		nxKeyFieldPathModel.setFieldName("FIELD1_VALUE");
		nxKeyFieldPathModel.setKeyFieldName("country");
		keyFieldMapping.add(nxKeyFieldPathModel);
		outputJsonGenerator.findLookupByOfferNameAndInputType(null, null);
		
		keyFieldMapping.clear();
		outputJsonGenerator.findLookupByOfferNameAndInputType(null, null);
	}

	@Test
	public void findOfferNameTest() {
		ObjectNode intermediateJson = jsonNodeFactory.objectNode();
		intermediateJson.put("service", "AVPN -");
		ReflectionTestUtils.setField(outputJsonGenerator, "intermediateJson", intermediateJson);
		assertEquals("AVPN", outputJsonGenerator.findOfferName());
		intermediateJson.put("service", "X");
		assertEquals("X", outputJsonGenerator.findOfferName());
		intermediateJson.put("service", "MIS -");
		assertEquals("ADI", outputJsonGenerator.findOfferName());
	}

	@Test
	public void isLookupApplicableTest() {
		ReflectionTestUtils.setField(outputJsonGenerator, "jsonEntry", jsonEntry);
		NxLineItemLookUpFieldModel nxLineItemLookUpFieldModel = new NxLineItemLookUpFieldModel();
		QueryLookup lookup = new QueryLookup(new ArrayList<>(), new HashMap<>(), nxLineItemLookUpFieldModel);
		JsonPath path = new JsonPath("/path");
		
		// case 0
		assertFalse(outputJsonGenerator.isLookupApplicable(lookup, path));

		// case 1
		nxLineItemLookUpFieldModel.setKeyFieldCondition("/path");
		nxLineItemLookUpFieldModel.setCountryCd("US");
		doReturn(false).when(outputJsonGenerator).isCountryUS();
		assertFalse(outputJsonGenerator.isLookupApplicable(lookup, path));

		// case 2
		nxLineItemLookUpFieldModel.setCountryCd("MOW");
		doReturn(true).when(outputJsonGenerator).isCountryUS();
		assertFalse(outputJsonGenerator.isLookupApplicable(lookup, path));
		
		// case 3
		nxLineItemLookUpFieldModel.setKeyFieldName("BEID##COUNTRY_CD");
		doReturn(false).when(outputJsonGenerator).isCountryUS();
		when(jsonEntry.get(any())).thenReturn("ACCESSBEID");
		assertFalse(outputJsonGenerator.isLookupApplicable(lookup, path));
		
		// case 4
		nxLineItemLookUpFieldModel.setKeyFieldName("SiteCountry##Product##Currency##Technology@@INR");
		doReturn(false).when(outputJsonGenerator).isFlowTypeInr();
		assertFalse(outputJsonGenerator.isLookupApplicable(lookup, path));

		// case 5
		nxLineItemLookUpFieldModel.setKeyFieldName("SiteCountry##Product##Currency##Technology@@IGL");
		doReturn(false).when(outputJsonGenerator).isFlowTypeIgl();
		assertFalse(outputJsonGenerator.isLookupApplicable(lookup, path));
		
		// case 5
		doReturn(true).when(outputJsonGenerator).isFlowTypeIgl();
		assertTrue(outputJsonGenerator.isLookupApplicable(lookup, path));
	}

	@Test
	public void isFlowTypeIglTest() {
		ReflectionTestUtils.setField(outputJsonGenerator, "jsonEntry", jsonEntry);
		when(jsonEntry.get(any())).thenReturn(InrConstants.IGL);
		assertTrue(outputJsonGenerator.isFlowTypeIgl());
	}

	@Test
	public void isFlowTypeInrTest() {
		ReflectionTestUtils.setField(outputJsonGenerator, "jsonEntry", jsonEntry);
		when(jsonEntry.get(any())).thenReturn(InrConstants.INR);
		assertTrue(outputJsonGenerator.isFlowTypeInr());
	}

	@Test
	public void isCountryUSTest() {
		ReflectionTestUtils.setField(outputJsonGenerator, "jsonEntry", jsonEntry);
		when(jsonEntry.get("country")).thenReturn("US");
		assertTrue(outputJsonGenerator.isCountryUS());
		
		when(jsonEntry.get("country")).thenReturn("USA");
		assertTrue(outputJsonGenerator.isCountryUS());
		
		when(jsonEntry.get("country")).thenReturn(null);
		when(jsonEntry.get("siteCountry")).thenReturn("United States");
		assertTrue(outputJsonGenerator.isCountryUS());
	}

	@Test
	public void isNodeHasAnyOfTheFieldsTest() {
		ObjectNode node = jsonNodeFactory.objectNode();
		List<String> requiredFields = Arrays.asList("field1");

		// case 1
		node.put("field1", "field1");
		assertTrue(outputJsonGenerator.isNodeHasAnyOfTheFields(node, requiredFields));

		// case 2
		node.remove("field1");
		assertFalse(outputJsonGenerator.isNodeHasAnyOfTheFields(node, requiredFields));
	}

	@Test
	public void findLineItemTest() {
		ReflectionTestUtils.setField(outputJsonGenerator, "lineItemCache", lineItemCache);
		String field1 = "field1";
		String value = "value";
		Map<String, NxKeyFieldPathModel> queryMapping = new HashMap<>();
		NxKeyFieldPathModel nxKeyFieldPathModel = new NxKeyFieldPathModel();
		queryMapping.put(field1, nxKeyFieldPathModel);
		nxKeyFieldPathModel.setKeyFieldName("keyFieldName");
		nxKeyFieldPathModel.setLongKeyName("longKeyName");
		
		List<String> requiredFields = Arrays.asList("beid");
		QueryLookup lookup = new QueryLookup(requiredFields, queryMapping, new NxLineItemLookUpFieldModel());
		doReturn(value).when(outputJsonGenerator).getParameter(any());

		// case 1
		when(lineItemCache.containsKey(any())).thenReturn(true);
		List<NxLineItemLookUpDataModel> resultList = new ArrayList<>();
		resultList.add(new NxLineItemLookUpDataModel());
		resultList.add(new NxLineItemLookUpDataModel());
		when(lineItemCache.get(any())).thenReturn(resultList);
		outputJsonGenerator.findLineItem(lookup, null);

		// case 2
		when(lineItemCache.containsKey(any())).thenReturn(false);
		when(em.createQuery(any(), eq(NxLineItemLookUpDataModel.class)))
				.thenReturn(typedQueryNxLineItemLookUpDataModel);
		when(typedQueryNxLineItemLookUpDataModel.getResultList()).thenReturn(resultList);
		outputJsonGenerator.findLineItem(lookup, null);

		// case 3
		ReflectionTestUtils.setField(outputJsonGenerator, "fallOut", fallOut);
		List<NxLineItemLookUpDataModel> resultList1 = new ArrayList<>();
		when(typedQueryNxLineItemLookUpDataModel.getResultList()).thenReturn(resultList1);
		outputJsonGenerator.findLineItem(lookup, null);
		
		List<InventoryJsonLookup> inventoryJsonLookups = new ArrayList<>();
		InventoryJsonLookup inventoryJsonLookup = new InventoryJsonLookup();
		inventoryJsonLookups.add(inventoryJsonLookup);
		ReflectionTestUtils.setField(outputJsonGenerator, "inventoryJsonLookups", inventoryJsonLookups);
		outputJsonGenerator.findLineItem(lookup, null);
	}

	@Test
	public void getParameterTest() {
		NxKeyFieldPathModel nxKeyFieldPathModel = new NxKeyFieldPathModel();
		nxKeyFieldPathModel.setDefaultValue("null");
		assertNull(outputJsonGenerator.getParameter(nxKeyFieldPathModel));
		
		nxKeyFieldPathModel.setDefaultValue("defaultValue");
		assertEquals("defaultValue", outputJsonGenerator.getParameter(nxKeyFieldPathModel));
		
		nxKeyFieldPathModel.setDefaultValue(null);
		nxKeyFieldPathModel.setKeyFieldName("keyFieldName");
		ReflectionTestUtils.setField(outputJsonGenerator, "jsonEntry", jsonEntry);
		when(jsonEntry.get(any())).thenReturn("AVPN - ");
		assertEquals("AVPN - ", outputJsonGenerator.getParameter(nxKeyFieldPathModel));
		
		nxKeyFieldPathModel.setKeyFieldName("service");
		assertEquals("AVPN", outputJsonGenerator.getParameter(nxKeyFieldPathModel));
	}

	@Test
	public void createNexusOutputTest() {
		ReflectionTestUtils.setField(outputJsonGenerator, "jsonEntry", jsonEntry);
		NxLineItemLookUpDataModel lineItem = new NxLineItemLookUpDataModel();
		NxOutputProductMappingModel mapping = new NxOutputProductMappingModel();
		lineItem.setNexusOutputMapping(mapping);
		doNothing().when(outputJsonGenerator).collectNxOutputData(any(), any(), any());
		NxLineItemLookUpFieldModel nxLineItemLookUpFieldModel = new NxLineItemLookUpFieldModel();
		QueryLookup queryLookup = new QueryLookup(null, null, nxLineItemLookUpFieldModel);
		

		// case 1
		mapping.setTabName(OutputFileConstants.AVPN_TAB);
		outputJsonGenerator.createNexusOutput(lineItem, queryLookup);

		// case 2
		mapping.setTabName(OutputFileConstants.AVPN_INTERNATIONAL_TAB);
		outputJsonGenerator.createNexusOutput(lineItem, queryLookup);

		// case 3
		mapping.setTabName(OutputFileConstants.ETHERNET_ACCESS_TAB);
		outputJsonGenerator.createNexusOutput(lineItem, queryLookup);
		
		// case 4
		mapping.setTabName(OutputFileConstants.MIS_TAB);
		lineItem.setLittleProdId(6005L);
		outputJsonGenerator.createNexusOutput(lineItem, queryLookup);
		lineItem.setLittleProdId(6006L);
		outputJsonGenerator.createNexusOutput(lineItem, queryLookup);
		lineItem.setLittleProdId(6007L);
		outputJsonGenerator.createNexusOutput(lineItem, queryLookup);
		
		// case 5
		mapping.setTabName(OutputFileConstants.BVOIP_TAB);
		outputJsonGenerator.createNexusOutput(lineItem, queryLookup);
		
		String outputJsonMappingJson = "{\r\n" + 
				"    \"destName\": \"priceDetails\",\r\n" + 
				"    \"mappings\": [{\r\n" + 
				"            \"sourceField\": \"secondaryKey\",\r\n" + 
				"            \"destField\": \"secondaryKey\",\r\n" + 
				"            \"source\": \"sourceLineItemQuery\",\r\n" + 
				"			\"destName\": \"destOverWrite\",\r\n" + 
				"            \"type\": \"fieldStr\"\r\n" + 
				"        }, {\r\n" + 
				"            \"sourceField\": \"secondaryKey\",\r\n" + 
				"			\"destField\": \"secondaryKey\",\r\n" + 
				"            \"source\": \"sourceLineItemQuery\",\r\n" + 
				"            \"type\": \"fieldStr\"\r\n" + 
				"        }, {\r\n" + 
				"            \"sourceField\": \"field20Value\",\r\n" + 
				"            \"destField\": \"uniqueId\",\r\n" + 
				"            \"source\": \"objectNodeMap\",\r\n" + 
				"			\"destName\": \"destOverWrite\",\r\n" + 
				"            \"type\": \"fieldDouble\"\r\n" + 
				"        }, {\r\n" + 
				"            \"sourceField\": \"field20Value\",\r\n" + 
				"			\"destField\": \"uniqueId\",\r\n" + 
				"            \"source\": \"objectNodeMap\",\r\n" + 
				"            \"type\": \"fieldDouble\"\r\n" + 
				"        }, {\r\n" + 
				"            \"sourceField\": \"field20Value\",\r\n" + 
				"            \"destField\": \"uniqueId\",\r\n" + 
				"            \"source\": \"objectNodeMap\",\r\n" + 
				"			\"destName\": \"destOverWrite\",\r\n" + 
				"            \"type\": \"fieldInt\"\r\n" + 
				"        }, {\r\n" + 
				"            \"sourceField\": \"field20Value\",\r\n" + 
				"			\"destField\": \"uniqueId\",\r\n" + 
				"            \"source\": \"objectNodeMap\",\r\n" + 
				"            \"type\": \"fieldInt\"\r\n" + 
				"        }, {\r\n" + 
				"            \"sourceField\": \"field20Value\",\r\n" + 
				"            \"destField\": \"uniqueId\",\r\n" + 
				"            \"source\": \"objectNodeMap\",\r\n" + 
				"			\"destName\": \"destOverWrite\",\r\n" + 
				"            \"type\": \"fieldLong\"\r\n" + 
				"        }, {\r\n" + 
				"            \"sourceField\": \"field20Value\",\r\n" + 
				"			\"destField\": \"uniqueId\",\r\n" + 
				"            \"source\": \"objectNodeMap\",\r\n" + 
				"            \"type\": \"fieldLong\"\r\n" + 
				"        }\r\n" + 
				"    ]\r\n" + 
				"}";
		ReflectionTestUtils.setField(outputJsonGenerator, "mapper", realMapper);
		nxLineItemLookUpFieldModel.setOutputJsonMapping(outputJsonMappingJson);
		ReflectionTestUtils.setField(outputJsonGenerator, "objectNodeMap", objectNodeMap);
		when(objectNodeMap.get(any())).thenReturn(objectNode);
		lineItem.setSecondaryKey("secondaryKey");
		ObjectNode node = realMapper.createObjectNode();
		node.put("field", 1);
		when(objectNode.path(any())).thenReturn(node.path("field"));
		outputJsonGenerator.createNexusOutput(lineItem, queryLookup);
		
		outputJsonMappingJson = "{\r\n" + 
				"    \"arrayName\": \"priceDetails\",\r\n" + 
				"    \"mappings\": [{\r\n" + 
				"            \"sourceField\": \"secondaryKey\",\r\n" + 
				"            \"destField\": \"secondaryKey\",\r\n" + 
				"            \"source\": \"sourceLineItemQuery\",\r\n" + 
				"			\"destName\": \"destOverWrite\",\r\n" + 
				"            \"type\": \"fieldStr\"\r\n" + 
				"        }, {\r\n" + 
				"            \"sourceField\": \"secondaryKey\",\r\n" + 
				"			\"destField\": \"secondaryKey\",\r\n" + 
				"            \"source\": \"sourceLineItemQuery\",\r\n" + 
				"            \"type\": \"fieldStr\"\r\n" + 
				"        }, {\r\n" + 
				"            \"sourceField\": \"field20Value\",\r\n" + 
				"            \"destField\": \"uniqueId\",\r\n" + 
				"            \"source\": \"objectNodeMap\",\r\n" + 
				"			\"destName\": \"destOverWrite\",\r\n" + 
				"            \"type\": \"fieldDouble\"\r\n" + 
				"        }, {\r\n" + 
				"            \"sourceField\": \"field20Value\",\r\n" + 
				"			\"destField\": \"uniqueId\",\r\n" + 
				"            \"source\": \"objectNodeMap\",\r\n" + 
				"            \"type\": \"fieldDouble\"\r\n" + 
				"        }, {\r\n" + 
				"            \"sourceField\": \"field20Value\",\r\n" + 
				"            \"destField\": \"uniqueId\",\r\n" + 
				"            \"source\": \"objectNodeMap\",\r\n" + 
				"			\"destName\": \"destOverWrite\",\r\n" + 
				"            \"type\": \"fieldInt\"\r\n" + 
				"        }, {\r\n" + 
				"            \"sourceField\": \"field20Value\",\r\n" + 
				"			\"destField\": \"uniqueId\",\r\n" + 
				"            \"source\": \"objectNodeMap\",\r\n" + 
				"            \"type\": \"fieldInt\"\r\n" + 
				"        }, {\r\n" + 
				"            \"sourceField\": \"field20Value\",\r\n" + 
				"            \"destField\": \"uniqueId\",\r\n" + 
				"            \"source\": \"objectNodeMap\",\r\n" + 
				"			\"destName\": \"destOverWrite\",\r\n" + 
				"            \"type\": \"fieldLong\"\r\n" + 
				"        }, {\r\n" + 
				"            \"sourceField\": \"field20Value\",\r\n" + 
				"			\"destField\": \"uniqueId\",\r\n" + 
				"            \"source\": \"objectNodeMap\",\r\n" + 
				"            \"type\": \"fieldLong\"\r\n" + 
				"        }\r\n" + 
				"    ]\r\n" + 
				"}";
		nxLineItemLookUpFieldModel.setOutputJsonMapping(outputJsonMappingJson);
		when(objectNode.withArray(any())).thenReturn(arrayNode);
		outputJsonGenerator.createNexusOutput(lineItem, queryLookup);
	}

	@Test
	public void collectNxOutputDataTest() {
		NxOutputBean nxOutputBean = new NxOutputBean();
		NxAvpnOutputBean bean = new NxAvpnOutputBean();
		NxLineItemLookUpDataModel lineItem = new NxLineItemLookUpDataModel();
		outputJsonGenerator.collectNxOutputData(nxOutputBean.getNxAvpnOutput(), bean, lineItem);
	}
	
	@Test
	public void findSiteNameTest() {
		assertEquals("siteRefID", outputJsonGenerator.findSiteName("siteRefID", "streetAddress", "city"));
		assertEquals("city", outputJsonGenerator.findSiteName(null, null, "city"));
		assertEquals("streetAddress city", outputJsonGenerator.findSiteName(null, "streetAddress", "city"));
	}

	@Test
	public void getFallOutTest() {
		ReflectionTestUtils.setField(outputJsonGenerator, "fallOut", fallOut);
		assertSame(fallOut, outputJsonGenerator.getFallOut());
	}
	
	@Test
	public void findNonNullTest() {
		String res = outputJsonGenerator.findNonNull(null, "1", "2");
		assertEquals("1", res);
		
		res = outputJsonGenerator.findNonNull(null, null);
		assertNull(res);
	}
	
	@Test
	public void getInventoryJsonLookupsTest() {
		List<InventoryJsonLookup> inventoryJsonLookups = new ArrayList<>();
		ReflectionTestUtils.setField(outputJsonGenerator, "inventoryJsonLookups", inventoryJsonLookups);
		assertSame(inventoryJsonLookups, outputJsonGenerator.getInventoryJsonLookups());
	}
}
