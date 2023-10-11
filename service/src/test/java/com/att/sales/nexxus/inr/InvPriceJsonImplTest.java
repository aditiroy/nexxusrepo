package com.att.sales.nexxus.inr;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.dao.model.NxDwPriceDetails;
import com.att.sales.nexxus.dao.model.NxDwToJsonRules;
import com.att.sales.nexxus.dao.model.NxRequestDetails;
import com.att.sales.nexxus.dao.repository.NxDwInventoryDao;
import com.att.sales.nexxus.dao.repository.NxDwPriceDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxDwToJsonRulesRepository;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.dao.repository.NxRequestDetailsRepository;
import com.att.sales.nexxus.inr.InvPriceJsonImpl.Args;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@ExtendWith(MockitoExtension.class)
public class InvPriceJsonImplTest {
	@Spy
	@InjectMocks
	private InvPriceJsonImpl invPriceJsonImpl;
	
	@Mock
	private NxDwInventoryDao nxDwInventoryDao;
	@Mock
	private NxRequestDetailsRepository nxRequestDetailsRepository;
	@Mock
	private NxDwToJsonRulesRepository nxDwToJsonRulesRepository;
	@Mock
	private NxDwPriceDetailsRepository nxDwPriceDetailsRepository;
	@Spy
	private ObjectMapper mapper;
	@Mock
	private NxLookupDataRepository nxLookupDataRepository;
	private ObjectMapper realMapper = new ObjectMapper();
	
	@Test
	public void invPriceJsonTest() throws JsonMappingException, JsonProcessingException, SalesBusinessException, ParseException {
		doReturn("").when(invPriceJsonImpl).createJsonNodeString1(any());
		NxDwPriceDetails savedentity = new NxDwPriceDetails();
		when(nxDwPriceDetailsRepository.save(any(NxDwPriceDetails.class))).thenReturn(savedentity);
		InvPriceJsonRequest request = new InvPriceJsonRequest();
		invPriceJsonImpl.invPriceJson(request);
	}
	
	@Test
	public void createJsonNodeString1Test() throws JsonMappingException, JsonProcessingException, ParseException {
		Args args = new Args();
		doReturn(args).when(invPriceJsonImpl).initializeArgs(any());
		NxRequestDetails nxRequestDetails = new NxRequestDetails();
		Queue<Map<String, Object>> res = new LinkedList<>();
		Map<String, Object> row = new HashMap<>();
		res.add(row);
		when(nxDwInventoryDao.getNxInventoryBySearchCriteriaWithSize(any(), anyInt(), any())).thenReturn(res);
		List<NxDwToJsonRules> jsonRuleList = new ArrayList<>();
		NxDwToJsonRules rule = new NxDwToJsonRules();
		jsonRuleList.add(rule);
		args.jsonRuleList = jsonRuleList;
		rule.setFieldType("String");
		doReturn(null).when(invPriceJsonImpl).getParentNode(any(), any(), any());
		doReturn(null).when(invPriceJsonImpl).convertData(any(), any(), any(), any(), any());
		doNothing().when(invPriceJsonImpl).putValueToNode(any(), any(), any(), any(), any(), any());
		invPriceJsonImpl.createJsonNodeString1(nxRequestDetails);
	}
	
	@Test
	public void getParentNodeTest() throws ParseException {
		Args args = new Args();
		NxDwToJsonRules jsonBuildRule = new NxDwToJsonRules();
		Map<String, NxDwToJsonRules> jsonBuildRuleMap = new HashMap<>();
		args.jsonBuildRuleMap = jsonBuildRuleMap;
		invPriceJsonImpl.getParentNode(jsonBuildRule, null, args);
		
		jsonBuildRule.setFieldParent("parent");
		NxDwToJsonRules parentRule = new NxDwToJsonRules();
		jsonBuildRuleMap.put("parent", parentRule);
		doReturn("identifierKey").when(invPriceJsonImpl).getIdentifierKey(any(), any(), any());
		args.nodeMap.put("identifierKey", null);
		invPriceJsonImpl.getParentNode(jsonBuildRule, null, args);
		
		args.nodeMap.clear();
		doReturn(null).when(invPriceJsonImpl).createParentNode(any(), any(), any());
		parentRule.setFieldType("list");
		parentRule.setFieldParent("root");
		ObjectNode root = realMapper.createObjectNode();
		args.nodeMap.put("root", root);
		invPriceJsonImpl.getParentNode(jsonBuildRule, null, args);
		
		args.nodeMap.clear();
		args.nodeMap.put("root", root);
		parentRule.setFieldType("object");
		invPriceJsonImpl.getParentNode(jsonBuildRule, null, args);
	}
	
	@Test
	public void createParentNodeTest() throws ParseException {
		NxDwToJsonRules jsonBuildRule = new NxDwToJsonRules();
		jsonBuildRule.setIdentifierKey("A|B");
		jsonBuildRule.setFieldName("design");
		Map<String, Object> dwRow = new HashMap<>();
		dwRow.put("TYPE_OF_CHARGE", "A");
		doNothing().when(invPriceJsonImpl).putValueToNode(any(), any(), any(), any(), any(), any());
		invPriceJsonImpl.createParentNode(jsonBuildRule, dwRow, null);
		
		dwRow.put("TYPE_OF_CHARGE", "P");
		invPriceJsonImpl.createParentNode(jsonBuildRule, dwRow, null);
		
		jsonBuildRule.setIdentifierKey("identifierKey");
		jsonBuildRule.setIdentifierType("identifierType");
		jsonBuildRule.setDwKey("dwKey");
		invPriceJsonImpl.createParentNode(jsonBuildRule, dwRow, null);
	}
	
	@Test
	public void putValueToNodeTest() {
		invPriceJsonImpl.putValueToNode(null, null, null, null, null, null);
		
		ObjectNode parentNode = realMapper.createObjectNode();
		NxDwToJsonRules jsonBuildRule = new NxDwToJsonRules();
		invPriceJsonImpl.putValueToNode("String", "1", parentNode, "tagName", jsonBuildRule, null);
		invPriceJsonImpl.putValueToNode("int", "1", parentNode, "tagName", jsonBuildRule, null);
		invPriceJsonImpl.putValueToNode("long", "1", parentNode, "tagName", jsonBuildRule, null);
		invPriceJsonImpl.putValueToNode("double", "1", parentNode, "tagName", jsonBuildRule, null);
		
		jsonBuildRule.setFieldName("pbi");
		when(nxLookupDataRepository.fetchDescriptionByDataSetName(any())).thenReturn(Arrays.asList("1"));
		invPriceJsonImpl.putValueToNode("String", "1", parentNode, "tagName", jsonBuildRule, null);
	}
	
	@Test
	public void convertDataTest() throws ParseException {
		NxDwToJsonRules jsonBuildRule = new NxDwToJsonRules();
		jsonBuildRule.setType("CUSTOM");
		jsonBuildRule.setFieldName("priceType");
		Map<String, Object> dwRow = new HashMap<>();
		dwRow.put("TYPE_OF_CHARGE", "P");
		invPriceJsonImpl.convertData(jsonBuildRule, null, dwRow, null, null);
		
		dwRow.put("TYPE_OF_CHARGE", "A");
		invPriceJsonImpl.convertData(jsonBuildRule, null, dwRow, null, null);
		
		dwRow.remove("TYPE_OF_CHARGE");
		invPriceJsonImpl.convertData(jsonBuildRule, null, dwRow, null, null);
		
		jsonBuildRule.setFieldName("discount");
		invPriceJsonImpl.convertData(jsonBuildRule, null, dwRow, null, null);
		
		dwRow.put("LOCALLISTPRICE", 1);
		dwRow.put("ACTUALPRICE", 1);
		invPriceJsonImpl.convertData(jsonBuildRule, null, dwRow, null, null);
	}
	
	@Test
	public void getIdentifierKeyTest() {
		NxDwToJsonRules jsonBuildRule = new NxDwToJsonRules();
		jsonBuildRule.setIdentifierKey("A|B");
		jsonBuildRule.setFieldName("design");
		Map<String, Object> dwRow = new HashMap<>();
		dwRow.put("TYPE_OF_CHARGE", "A");
		Map<String, NxDwToJsonRules> jsonBuildRuleMap = new HashMap<>();
		Args args = new Args();
		args.jsonBuildRuleMap = jsonBuildRuleMap;
		invPriceJsonImpl.getIdentifierKey(jsonBuildRule, args, dwRow);
		
		dwRow.put("TYPE_OF_CHARGE", "P");
		invPriceJsonImpl.getIdentifierKey(jsonBuildRule, args, dwRow);
		
		jsonBuildRule.setIdentifierKey("A,B");
		jsonBuildRule.setDwKey("A,B");
		invPriceJsonImpl.getIdentifierKey(jsonBuildRule, args, dwRow);
	}
	
	@Test
	public void initializeArgsTest() {
		List<NxDwToJsonRules> rulesList = new ArrayList<>();
		when(nxDwToJsonRulesRepository.findByOfferAndRuleNameAndActive(any(), any(), any())).thenReturn(rulesList);
		invPriceJsonImpl.initializeArgs("MIS/PNT");
		
		invPriceJsonImpl.initializeArgs("GMIS");
	}
	
	@Test
	public void removeLeadingZerosTest() {
		invPriceJsonImpl.removeLeadingZeros(null);
	}
}
