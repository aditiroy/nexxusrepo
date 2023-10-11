package com.att.sales.nexxus.inr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.dao.model.InrXmlToJsonRule;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.repository.InrXmlToJsonRuleDao;
import com.att.sales.nexxus.dao.repository.InrXmlToJsonRuleDao.InrXmlToJsonRuleDaoResult;
import com.att.sales.nexxus.service.NxMyPriceRepositoryServce;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;


@ExtendWith(MockitoExtension.class)
public class OutputJsonServiceTest {
	private ObjectMapper realMapper = new ObjectMapper();
	@Mock
	private ObjectMapper mapper;
	@Mock
	private InrFactory inrFactory;
	@Spy
	@InjectMocks
	private OutputJsonService outputJsonService;
	@Mock
	private OutputJsonGenerator outputJsonGenerator;
	@Mock
	private JsonProcessingException jsonProcessingException;
	@Mock
	private InrXmlToJsonRuleDao inrXmlToJsonRuleDao;
	@Mock
	private NxMyPriceRepositoryServce nxMyPriceRepositoryServce;
	@Mock
	private Map<String, NxLookupData> getLookupDataByItemIdResult;

	@Test
	public void getOutputDataTest() throws SalesBusinessException {
		doReturn(outputJsonGenerator).when(inrFactory).getOutputJsonGenerator(any(), any());
		OutputJsonFallOutData outputJsonFallOutData = new OutputJsonFallOutData(null, null, null, true, true);
		InrFallOutData fallOut = new InrFallOutData();
		fallOut.add(new HashMap<>());
		when(outputJsonGenerator.generate()).thenReturn(outputJsonFallOutData);
		when(outputJsonGenerator.getFallOut()).thenReturn(fallOut);
		assertNotNull(outputJsonService.getOutputData(null, "INR"));
	}

	@Test
	public void isFreeMinsPbiDescTest() {
		String inList = "VOAVPN INT L OFFNET";
		String outList = "out";
		assertTrue(outputJsonService.isFreeMinsPbiDesc(inList));
		assertFalse(outputJsonService.isFreeMinsPbiDesc(outList));
	}

	@Test
	public void preprocessInventoryJsonTest() throws IOException {
		ArrayNode arrayNode = realMapper.createArrayNode();
		String bvoipPricingInventory = "{\r\n" + "    \"BVOIPPricingInventory\": {\r\n"
				+ "        \"UsageDetails\": [{\r\n" + "                \"Jurisdiction\": \"Intrastate InterLATA\",\r\n"
				+ "                \"TotalQuantityAnnual\": 1,\r\n"
				+ "                \"CallDirection\": \"Outbound\",\r\n"
				+ "                \"BillingElementCode\": \"USAGE\",\r\n"
				+ "                \"PBIDescription\": \"VOAVPN INT L OFFNET\",\r\n"
				+ "                \"GrossDiscount\": 0,\r\n" + "                \"GrossCharge\": 1,\r\n"
				+ "                \"GenericQuantity\": 1,\r\n" + "                \"UnitRate\": 1,\r\n"
				+ "                \"PBICode\": \"00079044\",\r\n"
				+ "                \"OriginatingStateCountryName\": \"AA\"\r\n" + "            }, {\r\n"
				+ "                \"Jurisdiction\": \"Interstate InterLATA\",\r\n"
				+ "                \"GrossDiscount\": \"a\",\r\n" + "                \"GenericQuantity\": \"a\",\r\n"
				+ "                \"GrossDiscount\": \"a\",\r\n" + "				\"TotalQuantityAnnual\":1\r\n"
				+ "            }, {}\r\n" + "        ]\r\n" + "    }\r\n" + "}";
		JsonNode bvoipPricingInventoryJson = realMapper.readTree(bvoipPricingInventory);
		doNothing().when(outputJsonService).bvoipModuleCards(any());
		NxLookupData nxLookupData = new NxLookupData();
		nxLookupData.setDescription("description");
		when(nxMyPriceRepositoryServce.getLookupDataByItemId(any())).thenReturn(getLookupDataByItemIdResult);
		when(getLookupDataByItemIdResult.get(any())).thenReturn(nxLookupData);
		outputJsonService.preprocessInventoryJson(bvoipPricingInventoryJson);

		String bvoipPricingInventory1 = "{\r\n" + "    \"BVOIPPricingInventory\": {\r\n"
				+ "        \"UsageDetails\": {\r\n" + "            \"Jurisdiction\": \"Intrastate InterLATA\",\r\n"
				+ "            \"TotalQuantityAnnual\": 1,\r\n" + "            \"CallDirection\": \"Outbound\",\r\n"
				+ "            \"BillingElementCode\": \"USAGE\",\r\n"
				+ "            \"PBIDescription\": \"VoIP US Off-Net LD\",\r\n"
				+ "            \"GrossDiscount\": 0,\r\n" + "            \"GrossCharge\": 1,\r\n"
				+ "            \"GenericQuantity\": 1,\r\n" + "            \"UnitRate\": 1,\r\n"
				+ "            \"PBICode\": \"00079044\",\r\n"
				+ "            \"OriginatingStateCountryName\": \"AA\"\r\n" + "        }\r\n" + "    }\r\n" + "}";
		JsonNode bvoipPricingInventoryJson1 = realMapper.readTree(bvoipPricingInventory1);
		when(mapper.createArrayNode()).thenReturn(arrayNode);
		outputJsonService.preprocessInventoryJson(bvoipPricingInventoryJson1);

		String bvoipFeaturesPricingInventory = "{\r\n" + "    \"BVOIPFeaturesPricingInventory\": {\r\n"
				+ "        \"Site\": [{\r\n" + "                \"StandardTNCount\": 1,\r\n"
				+ "                \"VirtualTNCount\": 1,\r\n" + "                \"FeatureDetails\": [{\r\n"
				+ "                        \"DiscountAmount\": 1,\r\n"
				+ "                        \"GrossAmount\": 1,\r\n"
				+ "                        \"PBICode\": \"00095891\"\r\n" + "                    }, {\r\n"
				+ "                        \"DiscountAmount\": \"A\",\r\n"
				+ "                        \"GrossAmount\": \"A\",\r\n"
				+ "                        \"PBICode\": \"NotInList\",\r\n"
				+ "                        \"GenericQuantity\": 1,\r\n" + "                        \"NetAmount\": 1\r\n"
				+ "                    }, {\r\n" + "                        \"DiscountAmount\": \"A\",\r\n"
				+ "                        \"GrossAmount\": \"A\",\r\n"
				+ "                        \"PBICode\": \"NotInList\",\r\n"
				+ "                        \"GenericQuantity\": \"A\",\r\n"
				+ "                        \"NetAmount\": \"A\"\r\n" + "                    }\r\n"
				+ "                ]\r\n" + "            }, {\r\n" + "                \"StandardTNCount\": 1,\r\n"
				+ "                \"VirtualTNCount\": 0,\r\n" + "                \"FeatureDetails\": [{\r\n"
				+ "                        \"DiscountAmount\": 1,\r\n"
				+ "                        \"GrossAmount\": 1,\r\n"
				+ "                        \"StandardTNCount\": 1,\r\n"
				+ "                        \"VirtualTNCount\": 1,\r\n"
				+ "                        \"PBICode\": \"00095891\"\r\n" + "                    }\r\n"
				+ "                ]\r\n" + "            }, {\r\n" + "                \"StandardTNCount\": 0,\r\n"
				+ "                \"VirtualTNCount\": 1,\r\n" + "                \"FeatureDetails\": [{\r\n"
				+ "                        \"DiscountAmount\": 1,\r\n"
				+ "                        \"GrossAmount\": 1,\r\n"
				+ "                        \"StandardTNCount\": 1,\r\n"
				+ "                        \"VirtualTNCount\": 1,\r\n"
				+ "                        \"PBICode\": \"00095891\"\r\n" + "                    }\r\n"
				+ "                ]\r\n" + "            }, {\r\n" + "                \"StandardTNCount\": \"A\",\r\n"
				+ "                \"VirtualTNCount\": \"A\",\r\n" + "                \"FeatureDetails\": [{\r\n"
				+ "                        \"DiscountAmount\": 1,\r\n"
				+ "                        \"GrossAmount\": 1,\r\n"
				+ "                        \"StandardTNCount\": 1,\r\n"
				+ "                        \"VirtualTNCount\": 1,\r\n"
				+ "                        \"PBICode\": \"00095891\"\r\n" + "                    }\r\n"
				+ "                ]\r\n" + "            }\r\n" + "        ]\r\n" + "    }\r\n" + "}";
		JsonNode bvoipFeaturesPricingInventoryJson = realMapper.readTree(bvoipFeaturesPricingInventory);
		outputJsonService.preprocessInventoryJson(bvoipFeaturesPricingInventoryJson);

		String bvoipFeaturesPricingInventory1 = "{\r\n" + "    \"BVOIPFeaturesPricingInventory\": {\r\n"
				+ "        \"Site\": {\r\n" + "            \"StandardTNCount\": 1,\r\n"
				+ "            \"VirtualTNCount\": 0,\r\n" + "            \"FeatureDetails\": {\r\n"
				+ "                \"DiscountAmount\": 1,\r\n" + "                \"GrossAmount\": 1,\r\n"
				+ "                \"StandardTNCount\": 1,\r\n" + "                \"VirtualTNCount\": 1,\r\n"
				+ "                \"PBICode\": \"00095891\"\r\n" + "            }\r\n" + "        }\r\n" + "    }\r\n"
				+ "}";
		JsonNode bvoipFeaturesPricingInventoryJson1 = realMapper.readTree(bvoipFeaturesPricingInventory1);
		outputJsonService.preprocessInventoryJson(bvoipFeaturesPricingInventoryJson1);

		Set<String> outboundRateSchduleValues = new HashSet<>();
		outboundRateSchduleValues.add("value1");
		when(getLookupDataByItemIdResult.keySet()).thenReturn(outboundRateSchduleValues);
		String sdnOneNetLDVoiceUsage = "{\r\n" + "    \"SDNOneNetLDVoiceUsage\": {\r\n"
				+ "        \"UsageDetails\": [{\r\n" + "                \"OutboundRateSchedule\": \"value1\"\r\n"
				+ "            }, {}, {\r\n" + "                \"OutboundRateSchedule\": \"A\"\r\n"
				+ "            }\r\n" + "        ]\r\n" + "    }\r\n" + "}";
		JsonNode sdnOneNetLDVoiceUsageJson = realMapper.readTree(sdnOneNetLDVoiceUsage);
		outputJsonService.preprocessInventoryJson(sdnOneNetLDVoiceUsageJson);

		String sdnOneNetLDVoiceUsage1 = "{\r\n" + "    \"SDNOneNetLDVoiceUsage\": {\r\n"
				+ "        \"UsageDetails\": {\r\n" + "            \"OutboundRateSchedule\": \"B\"\r\n"
				+ "        }\r\n" + "    }\r\n" + "}";
		JsonNode sdnOneNetLDVoiceUsageJson1 = realMapper.readTree(sdnOneNetLDVoiceUsage1);
		outputJsonService.preprocessInventoryJson(sdnOneNetLDVoiceUsageJson1);

		String ddaResponse = "{\r\n" + "    \"DDAResponse\": {\r\n"
				+ "        \"DomesticEthernetAccessInventory\": {\r\n" + "            \"CustomerLocationInfo\": {\r\n"
				+ "                \"PhysicalInterface\": \"A\"\r\n" + "            }\r\n" + "        }\r\n"
				+ "    }\r\n" + "}";
		JsonNode ddaResponseJson = realMapper.readTree(ddaResponse);
		outputJsonService.preprocessInventoryJson(ddaResponseJson);

		String aniraPricingInventory = "{\r\n" + "    \"ANIRAPricingInventory\": {\r\n"
				+ "        \"AccountDetails\": {\r\n" + "            \"ANIRAService\": {}\r\n" + "        },\r\n"
				+ "        \"SiteDetails\": {\r\n" + "            \"ANIRAAccessList\": {}\r\n" + "        }\r\n"
				+ "    }\r\n" + "}";
		JsonNode aniraPricingInventoryJson = realMapper.readTree(aniraPricingInventory);
		outputJsonService.preprocessInventoryJson(aniraPricingInventoryJson);
	}

	@Test
	public void bvoipModuleCardsTest() {
		ObjectNode objectNode = realMapper.createObjectNode();
		objectNode.put("PBICode", "00070436");
		objectNode.put("GenericQuantity", "GenericQuantity");

		outputJsonService.bvoipModuleCards(objectNode);

		objectNode.put("GenericQuantity", "12");
		outputJsonService.bvoipModuleCards(objectNode);
		assertEquals("VoMIS12", objectNode.path("concurrentCallType").asText());

		objectNode.put("GenericQuantity", "24");
		outputJsonService.bvoipModuleCards(objectNode);
		assertEquals("VoMIS24", objectNode.path("concurrentCallType").asText());

		objectNode.put("GenericQuantity", "48");
		outputJsonService.bvoipModuleCards(objectNode);
		assertEquals("VoMIS48", objectNode.path("concurrentCallType").asText());

		objectNode.put("GenericQuantity", "49");
		outputJsonService.bvoipModuleCards(objectNode);
		assertEquals("T3", objectNode.path("concurrentCallType").asText());
	}

	@Test
	public void trimInventoryJsonTest() throws IOException {
		String inventory = "{\r\n" + "    \"root\": {\r\n" + "        \"arrayOfObj\": [{\r\n"
				+ "                \"field\": \"hasValue\"\r\n" + "            }, {\r\n"
				+ "                \"field\": \"\",\r\n" + "				\"emptyObj\":{}\r\n" + "            }\r\n"
				+ "        ],\r\n" + "        \"arrayOfStr\": [\"1\", null, \"\"]\r\n" + "    }\r\n" + "}";
		JsonNode inventoryJson = realMapper.readTree(inventory);
		Map<String, InrXmlToJsonRule> inrXmlToJsonRuleMap = new HashMap<>();
		List<String> fieldNullTags = new ArrayList<>();
		List<String> falloutMatchingTags = new ArrayList<>();
		falloutMatchingTags.add("/root");
		List<String> nxSiteMatchingTags = new ArrayList<>();
		nxSiteMatchingTags.add("/root");

		InrXmlToJsonRuleDaoResult inrXmlToJsonRuleDaoResult = new InrXmlToJsonRuleDao.InrXmlToJsonRuleDaoResult(
				inrXmlToJsonRuleMap, fieldNullTags, falloutMatchingTags, nxSiteMatchingTags);
		when(inrXmlToJsonRuleDao.getInrXmlToJsonRuleDaoResult(any())).thenReturn(inrXmlToJsonRuleDaoResult);

		assertEquals(2, inventoryJson.at("/root/arrayOfObj").size());
		assertEquals(3, inventoryJson.at("/root/arrayOfStr").size());

		outputJsonService.trimInventoryJson(inventoryJson);

		System.out.println(inventoryJson);

		assertEquals(1, inventoryJson.at("/root/arrayOfObj").size());
		assertEquals(1, inventoryJson.at("/root/arrayOfStr").size());
	}

	@Test
	public void modifyInventoryJsonForIntermediateJsonGenerationTest() {
		doNothing().when(outputJsonService).modifyAvpnSiteDetails(any());

		ObjectNode inventoryJson = realMapper.createObjectNode();
		ObjectNode avpnPricingInventoryObjNode = realMapper.createObjectNode();
		inventoryJson.set("AVPNPricingInventory", avpnPricingInventoryObjNode);
		ObjectNode siteDetailsObjNode = realMapper.createObjectNode();
		avpnPricingInventoryObjNode.withArray("SiteDetails").add(siteDetailsObjNode);
		outputJsonService.modifyInventoryJsonForIntermediateJsonGeneration(inventoryJson);

		avpnPricingInventoryObjNode.remove("SiteDetails");
		avpnPricingInventoryObjNode.set("SiteDetails", siteDetailsObjNode);
		outputJsonService.modifyInventoryJsonForIntermediateJsonGeneration(inventoryJson);
	}

	@Test
	public void modifyAvpnSiteDetailsTest() {
		doNothing().when(outputJsonService).modifyAvpnByAddingDesign(any(), any(), any());
		doNothing().when(outputJsonService).modifyAvpnProcessingDesign(any(), any(), any(), any());

		ObjectNode objNode = realMapper.createObjectNode();
		ArrayNode arrayNode = realMapper.createArrayNode();
		arrayNode.add(objNode);

		when(mapper.createArrayNode()).thenReturn(arrayNode);

		ObjectNode siteDetails = realMapper.createObjectNode();
		ObjectNode portAccessPVCDetails = realMapper.createObjectNode();
		siteDetails.withArray("PortAccessPVCDetails").add(portAccessPVCDetails).add(portAccessPVCDetails);
		outputJsonService.modifyAvpnSiteDetails(siteDetails);

		siteDetails.remove("PortAccessPVCDetails");
		siteDetails.set("PortAccessPVCDetails", portAccessPVCDetails);
		outputJsonService.modifyAvpnSiteDetails(siteDetails);
	}

	@Test
	public void modifyAvpnProcessingDesignTest() {
		doNothing().when(outputJsonService).modifyAvpnByAddingDesign(any(), any(), any());
		ObjectNode siteDetails = realMapper.createObjectNode();
		ObjectNode field = realMapper.createObjectNode();

		siteDetails.set("field", field);
		outputJsonService.modifyAvpnProcessingDesign(siteDetails, null, "field", null);

		siteDetails.remove("field");
		siteDetails.withArray("field").add(field);
		outputJsonService.modifyAvpnProcessingDesign(siteDetails, null, "field", null);
	}

	@Test
	public void modifyAvpnByAddingDesignTest() {
		doNothing().when(outputJsonService).modifyAvpnAddingNewValue(any(), any(), any(), anyInt(), anyInt());
		ArrayNode arrayNode = realMapper.createArrayNode();
		String[][] names = { { "1", "2" } };
		ObjectNode objNode = realMapper.createObjectNode();
		when(mapper.createObjectNode()).thenReturn(objNode);

		outputJsonService.modifyAvpnByAddingDesign(arrayNode, names, null);
	}

	@Test
	public void modifyAvpnAddingNewValueTest() {
		ObjectNode element = realMapper.createObjectNode();
		ObjectNode node = realMapper.createObjectNode();
		node.put("field", "value");
		String[][] names = { { "field" } };

		outputJsonService.modifyAvpnAddingNewValue(element, node, names, 0, 0);
		assertEquals("value", element.path("priceType").asText());
	}
}
