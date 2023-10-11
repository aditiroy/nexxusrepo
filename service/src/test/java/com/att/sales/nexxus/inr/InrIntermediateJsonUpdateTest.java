/**
 * 
 */
package com.att.sales.nexxus.inr;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.util.NexxusJsonUtility;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
/**
 * @author sj0546
 */
@ExtendWith(MockitoExtension.class)
public class InrIntermediateJsonUpdateTest {
	
	@Spy
	@InjectMocks
	private InrIntermediateJsonUpdate inrIntermediateJsonUpdate;
	
	@Mock
	private ObjectNode objectNode;
	
	@Mock
	private JsonPath path;
	
	private Set<String> excludedCkts = new HashSet<String>();
	private Set<String> inrbetaexcludeckt = new HashSet<String>();

	private Set<String> cktUpdate = new HashSet<String>();
	private Map<String, LinkedHashMap<String, Object>> cdirInput = new HashMap<String, LinkedHashMap<String, Object>>();
	
	@Test
	public void testInredits() throws SalesBusinessException {
		doNothing().when(inrIntermediateJsonUpdate).inreditsHelper(any(), any(), any(), any(), any(),any());
		inrIntermediateJsonUpdate.inredits(null, null, null,null);
	}
	
	@Test
	public void testInreditsHelper() {
		when(objectNode.getNodeType()).thenReturn(JsonNodeType.ARRAY, JsonNodeType.OBJECT, JsonNodeType.STRING);
		doNothing().when(inrIntermediateJsonUpdate).processArrayNode(any(), any(), any(), any(), any(),any());
		doNothing().when(inrIntermediateJsonUpdate).processObjectNode(any(), any(), any(), any(), any(),any());
		inrIntermediateJsonUpdate.inreditsHelper(objectNode, path, null, null, null,null);
		inrIntermediateJsonUpdate.inreditsHelper(objectNode, path, null, null, null,null);
	}
	
	@Test
	public void testprocessObjectNode() {
		ObjectMapper mapper = new ObjectMapper();
		String json = "{\"beginBillMonth\":\"September 2019\",\"service\":\"DOMESTIC DEDICATED ACCESS\",\"customerName\":\"AETNA\",\"DomesticEthernetAccessInventory\":{\"CustomerAccountInfo\":[{\"CustomerSubAccountInfo\":{\"CustomerCircuitInfo\":[{\"ServiceIndicator\":\"ADI\",\"CktId\":\"BFEC558376ATI\",\"CustomerLocationInfo\":[{\"ServiceState\":\"VA\",\"USOCInfo\":[{\"DisplaySpeed\":\"10000\",\"USOC\":\"1LNET\",\"NetRate\":\"0\",\"AccessSpeed\":\"10 GBPS BASIC EPL-WAN\"}],\"PhysicalInterface\":\"10GBASELR\",\"ServiceZip\":\"20147-6207\",\"ServiceCity\":\"ASHBURN\",\"ServiceProvider\":\"SBC*LNS\",\"AttCtrOffcCLLICd\":\"WASHDCDT\",\"ServiceAddress2\":\"FLR 2 RM CAGE 2113\",\"ServiceAddress1\":\"21721  FILIGREE CT\",\"AccessArchitecture\":\"DEDICATED\",\"CustSrvgWireCtrCLLICd\":\"WASHDCDN\",\"NXSITEMATCHINGID\":4,\"nxSiteId\":2,\"endPointType\":\"A\",\"nxKeyId\":\"10 GBPS BASIC EPL-WAN$DEDICATED$10GBASELR\"}],\"dataUpdated\":\"Y\"}]}},{\"CustomerSubAccountInfo\":{\"CustomerCircuitInfo\":[{\"ServiceIndicator\":\"AVPN\",\"CktId\":\"IZEC579038ATI\",\"CustomerLocationInfo\":[{\"ServiceState\":\"CT\",\"USOCInfo\":[{\"DisplaySpeed\":\"1000\",\"USOC\":\"1LNET\",\"NetRate\":\"0\",\"AccessSpeed\":\"1 GBPS BASIC EPL-WAN\"}],\"PhysicalInterface\":\"1000BASELX\",\"ServiceZip\":\"06457-0000\",\"ServiceCity\":\"MIDDLETOWN\",\"ServiceProvider\":\"SBC*LNS\",\"AttCtrOffcCLLICd\":\"CHSHCT02\",\"ServiceAddress2\":\"BLDG DATA CTR FLR 3RD RM COMPUTER\",\"ServiceAddress1\":\"1000  MIDDLE ST\",\"AccessArchitecture\":\"SWITCHED\",\"CustSrvgWireCtrCLLICd\":\"CRWLCT00\",\"NXSITEMATCHINGID\":2,\"nxSiteId\":3,\"endPointType\":\"A\",\"nxKeyId\":\"1 GBPS BASIC EPL-WAN$SWITCHED$1000BASELX\"},{\"ServiceState\":\"NY\",\"USOCInfo\":[{\"DisplaySpeed\":\"1000\",\"USOC\":\"1LNET\",\"NetRate\":\"0\",\"AccessSpeed\":\"1 GBPS BASIC EPL-WAN\"}],\"PhysicalInterface\":\"1000BASELX\",\"ServiceZip\":\"10013\",\"ServiceCity\":\"NEW YORK\",\"ServiceProvider\":\"SBC*LNS\",\"AttCtrOffcCLLICd\":\"NYCMNY54\",\"ServiceAddress2\":\"BLDG LNS FLR 10TH RM LNS CAGE\",\"ServiceAddress1\":\"32  AVENUE OF THE AMERICAS\",\"AccessArchitecture\":\"DEDICATED\",\"CustSrvgWireCtrCLLICd\":\"NYCMNYVS\",\"NXSITEMATCHINGID\":1,\"nxSiteId\":4,\"endPointType\":\"Z\",\"nxKeyId\":\"1 GBPS BASIC EPL-WAN$DEDICATED$1000BASELX\"}],\"dataUpdated\":\"Y\"}]}}]},\"DomesticDS3OCXAccessInventory\":{\"CustomerAccountInfo\":[{\"CustomerSubAccountInfo\":{\"CustomerCircuitInfo\":[{\"PrimClsOfSvcCd\":\"AVAU5\",\"ServiceIndicator\":\"POS\",\"CktId\":\"AGEC572447ATI\",\"CustomerLocationInfo\":[{\"AttCtrOffcCLLICd\":\"PTLDMEFO\",\"SiteNPANXX\":\"207774\",\"USOCInfo\":[{\"USOC\":\"1LNM2\",\"NetRate\":\"3780.00\",\"AccessSpeed\":\"OC3\",\"secondaryKey\":\"#FCC#OC3 Local Channel Service#OC3#Unprotected\",\"nxItemId\":1788208}],\"CustSrvgWireCtrCLLICd\":\"PTLDMEFO\",\"NXSITEMATCHINGID\":15,\"nxSiteId\":5,\"endPointType\":\"A\",\"TypeLocalAccess\":\"Unprotected\",\"LittleProductName\":\"LD DS3 OCx Access\",\"ProductTypeLocalAccess\":\"OC3 Local Channel Service\",\"nxKeyId\":\"1788208\"}],\"FALLOUTMATCHINGID\":\"0000000016/DDAResponse/Body/DomesticDS3OCXAccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\"},{\"PrimClsOfSvcCd\":\"AVAU5\",\"ServiceIndicator\":\"POS\",\"CktId\":\"AGEC648238ATI\",\"CustomerLocationInfo\":[{\"AttCtrOffcCLLICd\":\"FTLDFLOV\",\"SiteNPANXX\":\"954693\",\"USOCInfo\":[{\"USOC\":\"1LNM1\",\"NetRate\":\"1925.00\",\"AccessSpeed\":\"OC3\",\"secondaryKey\":\"#FCC#OC3 Local Channel Service#OC3#Unprotected\",\"nxItemId\":1788208}],\"CustSrvgWireCtrCLLICd\":\"FTLDFLJA\",\"NXSITEMATCHINGID\":13,\"nxSiteId\":6,\"endPointType\":\"A\",\"TypeLocalAccess\":\"Unprotected\",\"LittleProductName\":\"LD DS3 OCx Access\",\"ProductTypeLocalAccess\":\"OC3 Local Channel Service\",\"nxKeyId\":\"1788208\"}],\"FALLOUTMATCHINGID\":\"0000000014/DDAResponse/Body/DomesticDS3OCXAccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\"},{\"PrimClsOfSvcCd\":\"AVAU5\",\"ServiceIndicator\":\"POS\",\"CktId\":\"AGEC880883ATI\",\"CustomerLocationInfo\":[{\"AttCtrOffcCLLICd\":\"BSMRNDJC\",\"SiteNPANXX\":\"701221\",\"USOCInfo\":[{\"USOC\":\"1LNM1\",\"NetRate\":\"3250.00\",\"AccessSpeed\":\"OC3\",\"secondaryKey\":\"#FCC#OC3 Local Channel Service#OC3#Unprotected\",\"nxItemId\":1788208}],\"CustSrvgWireCtrCLLICd\":\"BSMRNDBC\",\"NXSITEMATCHINGID\":11,\"nxSiteId\":7,\"endPointType\":\"A\",\"TypeLocalAccess\":\"Unprotected\",\"LittleProductName\":\"LD DS3 OCx Access\",\"ProductTypeLocalAccess\":\"OC3 Local Channel Service\",\"nxKeyId\":\"1788208\"}],\"FALLOUTMATCHINGID\":\"0000000012/DDAResponse/Body/DomesticDS3OCXAccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\"},{\"PrimClsOfSvcCd\":\"AVFV5\",\"ServiceIndicator\":\"POS\",\"CktId\":\"IWEC723444ATI\",\"CustomerLocationInfo\":[{\"AttCtrOffcCLLICd\":\"CHSHCT02\",\"SiteNPANXX\":\"860613\",\"USOCInfo\":[{\"USOC\":\"1LNVE\",\"NetRate\":\"7102.00\",\"AccessSpeed\":\"OC48\",\"secondaryKey\":\"#FCC#OC48 Local Channel Service#OC48#Unprotected\",\"nxItemId\":1788237}],\"CustSrvgWireCtrCLLICd\":\"CRWLCT00\",\"NXSITEMATCHINGID\":9,\"nxSiteId\":8,\"endPointType\":\"A\",\"TypeLocalAccess\":\"Unprotected\",\"LittleProductName\":\"LD DS3 OCx Access\",\"ProductTypeLocalAccess\":\"OC48 Local Channel Service\",\"nxKeyId\":\"1788237\"}],\"FALLOUTMATCHINGID\":\"0000000010/DDAResponse/Body/DomesticDS3OCXAccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\"}]}},{\"CustomerSubAccountInfo\":{\"CustomerCircuitInfo\":[{\"PrimClsOfSvcCd\":\"HPPKE\",\"ServiceIndicator\":\"PL\",\"CktId\":\"IVEC990793ATI\",\"CustomerLocationInfo\":[{\"AttCtrOffcCLLICd\":\"HRFRCT03\",\"SiteNPANXX\":\"860624\",\"USOCInfo\":[{\"USOC\":\"ZDRMK\",\"NetRate\":\"1927.00\",\"AccessSpeed\":\"OC12\",\"secondaryKey\":\"#FCC#OC12 Local Channel Service#OC12#Unprotected\",\"nxItemId\":1788234}],\"CustSrvgWireCtrCLLICd\":\"WNDSCT00\",\"NXSITEMATCHINGID\":7,\"nxSiteId\":9,\"endPointType\":\"A\",\"TypeLocalAccess\":\"Unprotected\",\"LittleProductName\":\"LD DS3 OCx Access\",\"ProductTypeLocalAccess\":\"OC12 Local Channel Service\",\"nxKeyId\":\"1788234\"},{\"AttCtrOffcCLLICd\":\"NWRKNJ02\",\"SiteNPANXX\":\"973275\",\"USOCInfo\":[{\"USOC\":\"ZDRML\",\"NetRate\":\"2508.00\",\"AccessSpeed\":\"OC12\",\"secondaryKey\":\"#FCC#OC12 Local Channel Service#OC12#Unprotected\",\"nxItemId\":1788234}],\"CustSrvgWireCtrCLLICd\":\"SORGNJSO\",\"NXSITEMATCHINGID\":6,\"nxSiteId\":10,\"endPointType\":\"Z\",\"TypeLocalAccess\":\"Unprotected\",\"LittleProductName\":\"LD DS3 OCx Access\",\"ProductTypeLocalAccess\":\"OC12 Local Channel Service\",\"nxKeyId\":\"1788234\"}],\"FALLOUTMATCHINGID\":\"0000000008/DDAResponse/Body/DomesticDS3OCXAccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\"}]}}]},\"flowType\":\"INR\"}";
		JsonNode node = null;
		try {
			node = mapper.readTree(json);
		} catch (IOException e) {
			e.printStackTrace();
		}
		NexxusJsonUtility nexxusJsonUtility = new NexxusJsonUtility();
		//case 1
		LinkedHashMap<String, Object> rules = (LinkedHashMap<String, Object>) nexxusJsonUtility
				.convertStringJsonToMap("{ \"ethernet\": { \"whereClausePath\": \"/DomesticEthernetAccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo\", \"whereClauseAttriName\": \"nxSiteId\", \"whereClauseExcelColName\": \"NX_Site ID\", \"whereClauseDatatype\": \"Integer\", \"cdirKey\": \"NXSITEMATCHINGID\", \"data\": { \"Transport Type\": { \"path\": \"/DomesticEthernetAccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo\", \"jsonAttriName\": \"ServiceIndicator\" }, \"Access Architecture\": { \"path\": \"CustomerLocationInfo\", \"jsonAttriName\": \"AccessArchitecture\" }, \"Circuit ID\": { \"path\": \"/DomesticEthernetAccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo\", \"jsonAttriName\": \"CktId\", \"operation\": \"REPLACE\" }, \"removeNxSiteId\": { \"path\": \"CustomerLocationInfo\", \"jsonAttriName\": \"nxSiteId,nxSiteIdZ,endPointType\" } } }, \"ds3\": { \"whereClausePath\": \"/DomesticDS3OCXAccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo\", \"whereClauseAttriName\": \"nxSiteId\", \"whereClauseExcelColName\": \"NX_Site ID\", \"whereClauseDatatype\": \"Integer\", \"cdirKey\": \"NXSITEMATCHINGID\", \"data\": { \"Transport Type\": { \"path\": \"/DomesticDS3OCXAccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo\", \"jsonAttriName\": \"ServiceIndicator\" }, \"Circuit ID\": { \"path\": \"/DomesticDS3OCXAccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo\", \"jsonAttriName\": \"CktId\", \"operation\": \"REPLACE\" }, \"removeNxSiteId\": { \"path\": \"CustomerLocationInfo\", \"jsonAttriName\": \"nxSiteId,nxSiteIdZ,endPointType\" } } }, \"ds0ds1\": { \"whereClausePath\": \"/DomesticDSODS1AccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo\", \"whereClauseAttriName\": \"nxSiteId\", \"whereClauseExcelColName\": \"NX_Site ID\", \"whereClauseDatatype\": \"Integer\", \"cdirKey\": \"NXSITEMATCHINGID\", \"data\": { \"Transport Type\": { \"path\": \"/DomesticDSODS1AccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo\", \"jsonAttriName\": \"ServiceIndicator\" }, \"Circuit ID\": { \"path\": \"/DomesticDS3OCXAccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo\", \"jsonAttriName\": \"CktId\", \"operation\": \"REPLACE\" }, \"removeNxSiteId\": { \"path\": \"CustomerLocationInfo\", \"jsonAttriName\": \"nxSiteId,nxSiteIdZ,endPointType\" } } } }");
		LinkedHashMap<String, Object> dataMap = new LinkedHashMap<String, Object>();
		dataMap.put("Action", "Circuit Augmentation");
		dataMap.put("NX_Site ID", "2");
		dataMap.put("Access Architecture", "SWITCHED");
		dataMap.put("Transport Type", "AVPN");
		dataMap.put("Circuit ID", "BFEC558376ATI");
		ReflectionTestUtils.setField(inrIntermediateJsonUpdate, "action", "Circuit Augmentation");
		ReflectionTestUtils.setField(inrIntermediateJsonUpdate, "criteriaMap", rules);
		ReflectionTestUtils.setField(inrIntermediateJsonUpdate, "dataMap", dataMap);
		ReflectionTestUtils.setField(inrIntermediateJsonUpdate, "product", "DOMESTIC DEDICATED ACCESS");
		JsonPath path = new JsonPath("");
		inrIntermediateJsonUpdate.processObjectNode(node, path, excludedCkts, cdirInput, cktUpdate,inrbetaexcludeckt);
	}
	
	@Test
	public void testprocessObjectNodeCase2() {
		ObjectMapper mapper = new ObjectMapper();
		String json = "{\"beginBillMonth\":\"September 2019\",\"service\":\"DOMESTIC DEDICATED ACCESS\",\"customerName\":\"AETNA\",\"DomesticEthernetAccessInventory\":{\"CustomerAccountInfo\":[{\"CustomerSubAccountInfo\":{\"CustomerCircuitInfo\":[{\"ServiceIndicator\":\"ADI\",\"CktId\":\"BFEC558376ATI\",\"CustomerLocationInfo\":[{\"ServiceState\":\"VA\",\"USOCInfo\":[{\"DisplaySpeed\":\"10000\",\"USOC\":\"1LNET\",\"NetRate\":\"0\",\"AccessSpeed\":\"10 GBPS BASIC EPL-WAN\"}],\"PhysicalInterface\":\"10GBASELR\",\"ServiceZip\":\"20147-6207\",\"ServiceCity\":\"ASHBURN\",\"ServiceProvider\":\"SBC*LNS\",\"AttCtrOffcCLLICd\":\"WASHDCDT\",\"ServiceAddress2\":\"FLR 2 RM CAGE 2113\",\"ServiceAddress1\":\"21721  FILIGREE CT\",\"AccessArchitecture\":\"DEDICATED\",\"CustSrvgWireCtrCLLICd\":\"WASHDCDN\",\"NXSITEMATCHINGID\":4,\"nxSiteId\":2,\"endPointType\":\"A\",\"nxKeyId\":\"10 GBPS BASIC EPL-WAN$DEDICATED$10GBASELR\"}],\"dataUpdated\":\"Y\"}]}},{\"CustomerSubAccountInfo\":{\"CustomerCircuitInfo\":[{\"ServiceIndicator\":\"AVPN\",\"CktId\":\"IZEC579038ATI\",\"CustomerLocationInfo\":[{\"ServiceState\":\"CT\",\"USOCInfo\":[{\"DisplaySpeed\":\"1000\",\"USOC\":\"1LNET\",\"NetRate\":\"0\",\"AccessSpeed\":\"1 GBPS BASIC EPL-WAN\"}],\"PhysicalInterface\":\"1000BASELX\",\"ServiceZip\":\"06457-0000\",\"ServiceCity\":\"MIDDLETOWN\",\"ServiceProvider\":\"SBC*LNS\",\"AttCtrOffcCLLICd\":\"CHSHCT02\",\"ServiceAddress2\":\"BLDG DATA CTR FLR 3RD RM COMPUTER\",\"ServiceAddress1\":\"1000  MIDDLE ST\",\"AccessArchitecture\":\"SWITCHED\",\"CustSrvgWireCtrCLLICd\":\"CRWLCT00\",\"NXSITEMATCHINGID\":2,\"nxSiteId\":3,\"endPointType\":\"A\",\"nxKeyId\":\"1 GBPS BASIC EPL-WAN$SWITCHED$1000BASELX\"},{\"ServiceState\":\"NY\",\"USOCInfo\":[{\"DisplaySpeed\":\"1000\",\"USOC\":\"1LNET\",\"NetRate\":\"0\",\"AccessSpeed\":\"1 GBPS BASIC EPL-WAN\"}],\"PhysicalInterface\":\"1000BASELX\",\"ServiceZip\":\"10013\",\"ServiceCity\":\"NEW YORK\",\"ServiceProvider\":\"SBC*LNS\",\"AttCtrOffcCLLICd\":\"NYCMNY54\",\"ServiceAddress2\":\"BLDG LNS FLR 10TH RM LNS CAGE\",\"ServiceAddress1\":\"32  AVENUE OF THE AMERICAS\",\"AccessArchitecture\":\"DEDICATED\",\"CustSrvgWireCtrCLLICd\":\"NYCMNYVS\",\"NXSITEMATCHINGID\":1,\"nxSiteId\":4,\"endPointType\":\"Z\",\"nxKeyId\":\"1 GBPS BASIC EPL-WAN$DEDICATED$1000BASELX\"}],\"dataUpdated\":\"Y\"}]}}]},\"DomesticDS3OCXAccessInventory\":{\"CustomerAccountInfo\":[{\"CustomerSubAccountInfo\":{\"CustomerCircuitInfo\":[{\"PrimClsOfSvcCd\":\"AVAU5\",\"ServiceIndicator\":\"POS\",\"CktId\":\"AGEC572447ATI\",\"CustomerLocationInfo\":[{\"AttCtrOffcCLLICd\":\"PTLDMEFO\",\"SiteNPANXX\":\"207774\",\"USOCInfo\":[{\"USOC\":\"1LNM2\",\"NetRate\":\"3780.00\",\"AccessSpeed\":\"OC3\",\"secondaryKey\":\"#FCC#OC3 Local Channel Service#OC3#Unprotected\",\"nxItemId\":1788208}],\"CustSrvgWireCtrCLLICd\":\"PTLDMEFO\",\"NXSITEMATCHINGID\":15,\"nxSiteId\":5,\"endPointType\":\"A\",\"TypeLocalAccess\":\"Unprotected\",\"LittleProductName\":\"LD DS3 OCx Access\",\"ProductTypeLocalAccess\":\"OC3 Local Channel Service\",\"nxKeyId\":\"1788208\"}],\"FALLOUTMATCHINGID\":\"0000000016/DDAResponse/Body/DomesticDS3OCXAccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\"},{\"PrimClsOfSvcCd\":\"AVAU5\",\"ServiceIndicator\":\"POS\",\"CktId\":\"AGEC648238ATI\",\"CustomerLocationInfo\":[{\"AttCtrOffcCLLICd\":\"FTLDFLOV\",\"SiteNPANXX\":\"954693\",\"USOCInfo\":[{\"USOC\":\"1LNM1\",\"NetRate\":\"1925.00\",\"AccessSpeed\":\"OC3\",\"secondaryKey\":\"#FCC#OC3 Local Channel Service#OC3#Unprotected\",\"nxItemId\":1788208}],\"CustSrvgWireCtrCLLICd\":\"FTLDFLJA\",\"NXSITEMATCHINGID\":13,\"nxSiteId\":6,\"endPointType\":\"A\",\"TypeLocalAccess\":\"Unprotected\",\"LittleProductName\":\"LD DS3 OCx Access\",\"ProductTypeLocalAccess\":\"OC3 Local Channel Service\",\"nxKeyId\":\"1788208\"}],\"FALLOUTMATCHINGID\":\"0000000014/DDAResponse/Body/DomesticDS3OCXAccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\"},{\"PrimClsOfSvcCd\":\"AVAU5\",\"ServiceIndicator\":\"POS\",\"CktId\":\"AGEC880883ATI\",\"CustomerLocationInfo\":[{\"AttCtrOffcCLLICd\":\"BSMRNDJC\",\"SiteNPANXX\":\"701221\",\"USOCInfo\":[{\"USOC\":\"1LNM1\",\"NetRate\":\"3250.00\",\"AccessSpeed\":\"OC3\",\"secondaryKey\":\"#FCC#OC3 Local Channel Service#OC3#Unprotected\",\"nxItemId\":1788208}],\"CustSrvgWireCtrCLLICd\":\"BSMRNDBC\",\"NXSITEMATCHINGID\":11,\"nxSiteId\":7,\"endPointType\":\"A\",\"TypeLocalAccess\":\"Unprotected\",\"LittleProductName\":\"LD DS3 OCx Access\",\"ProductTypeLocalAccess\":\"OC3 Local Channel Service\",\"nxKeyId\":\"1788208\"}],\"FALLOUTMATCHINGID\":\"0000000012/DDAResponse/Body/DomesticDS3OCXAccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\"},{\"PrimClsOfSvcCd\":\"AVFV5\",\"ServiceIndicator\":\"POS\",\"CktId\":\"IWEC723444ATI\",\"CustomerLocationInfo\":[{\"AttCtrOffcCLLICd\":\"CHSHCT02\",\"SiteNPANXX\":\"860613\",\"USOCInfo\":[{\"USOC\":\"1LNVE\",\"NetRate\":\"7102.00\",\"AccessSpeed\":\"OC48\",\"secondaryKey\":\"#FCC#OC48 Local Channel Service#OC48#Unprotected\",\"nxItemId\":1788237}],\"CustSrvgWireCtrCLLICd\":\"CRWLCT00\",\"NXSITEMATCHINGID\":9,\"nxSiteId\":8,\"endPointType\":\"A\",\"TypeLocalAccess\":\"Unprotected\",\"LittleProductName\":\"LD DS3 OCx Access\",\"ProductTypeLocalAccess\":\"OC48 Local Channel Service\",\"nxKeyId\":\"1788237\"}],\"FALLOUTMATCHINGID\":\"0000000010/DDAResponse/Body/DomesticDS3OCXAccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\"}]}},{\"CustomerSubAccountInfo\":{\"CustomerCircuitInfo\":[{\"PrimClsOfSvcCd\":\"HPPKE\",\"ServiceIndicator\":\"PL\",\"CktId\":\"IVEC990793ATI\",\"CustomerLocationInfo\":[{\"AttCtrOffcCLLICd\":\"HRFRCT03\",\"SiteNPANXX\":\"860624\",\"USOCInfo\":[{\"USOC\":\"ZDRMK\",\"NetRate\":\"1927.00\",\"AccessSpeed\":\"OC12\",\"secondaryKey\":\"#FCC#OC12 Local Channel Service#OC12#Unprotected\",\"nxItemId\":1788234}],\"CustSrvgWireCtrCLLICd\":\"WNDSCT00\",\"NXSITEMATCHINGID\":7,\"nxSiteId\":9,\"endPointType\":\"A\",\"TypeLocalAccess\":\"Unprotected\",\"LittleProductName\":\"LD DS3 OCx Access\",\"ProductTypeLocalAccess\":\"OC12 Local Channel Service\",\"nxKeyId\":\"1788234\"},{\"AttCtrOffcCLLICd\":\"NWRKNJ02\",\"SiteNPANXX\":\"973275\",\"USOCInfo\":[{\"USOC\":\"ZDRML\",\"NetRate\":\"2508.00\",\"AccessSpeed\":\"OC12\",\"secondaryKey\":\"#FCC#OC12 Local Channel Service#OC12#Unprotected\",\"nxItemId\":1788234}],\"CustSrvgWireCtrCLLICd\":\"SORGNJSO\",\"NXSITEMATCHINGID\":6,\"nxSiteId\":10,\"endPointType\":\"Z\",\"TypeLocalAccess\":\"Unprotected\",\"LittleProductName\":\"LD DS3 OCx Access\",\"ProductTypeLocalAccess\":\"OC12 Local Channel Service\",\"nxKeyId\":\"1788234\"}],\"FALLOUTMATCHINGID\":\"0000000008/DDAResponse/Body/DomesticDS3OCXAccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\"}]}}]},\"flowType\":\"INR\"}";
		JsonNode node = null;
		try {
			node = mapper.readTree(json);
		} catch (IOException e) {
			e.printStackTrace();
		}
		NexxusJsonUtility nexxusJsonUtility = new NexxusJsonUtility();
		//case 2
		LinkedHashMap<String, Object> rules = (LinkedHashMap<String, Object>) nexxusJsonUtility
				.convertStringJsonToMap("{ \"ethernet\": { \"whereClausePath\": \"/DomesticEthernetAccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo\", \"whereClauseAttriName\": \"nxSiteId\", \"whereClauseExcelColName\": \"NX_Site ID\", \"whereClauseDatatype\": \"Integer\", \"cktIdAttriName\": \"CktId\" }, \"ds3\": { \"whereClausePath\": \"/DomesticDS3OCXAccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo\", \"whereClauseAttriName\": \"nxSiteId\", \"whereClauseExcelColName\": \"NX_Site ID\", \"whereClauseDatatype\": \"Integer\", \"cktIdCheck\": \"Y\", \"cktIdAttriName\": \"CktId\", \"cktIdExcelName\": \"Circuit ID\" }, \"ds0ds1\": { \"whereClausePath\": \"/DomesticDSODS1AccessInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo\", \"whereClauseAttriName\": \"nxSiteId\", \"whereClauseExcelColName\": \"NX_Site ID\", \"whereClauseDatatype\": \"Integer\", \"cktIdCheck\": \"Y\", \"cktIdAttriName\": \"CktId\", \"cktIdExcelName\": \"Circuit ID\" } }");
		LinkedHashMap<String, Object> dataMap = new LinkedHashMap<String, Object>();
		dataMap.put("Action", "Exclude line items");
		dataMap.put("NX_Site ID", "3");
		dataMap.put("Circuit ID", "IZEC579038ATI");
		ReflectionTestUtils.setField(inrIntermediateJsonUpdate, "action", "Exclude line items");
		ReflectionTestUtils.setField(inrIntermediateJsonUpdate, "criteriaMap", rules);
		ReflectionTestUtils.setField(inrIntermediateJsonUpdate, "dataMap", dataMap);
		JsonPath path = new JsonPath("");
		inrIntermediateJsonUpdate.processObjectNode(node, path, excludedCkts, cdirInput, cktUpdate,inrbetaexcludeckt);
	}

}
