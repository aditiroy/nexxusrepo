package com.att.sales.nexxus.myprice.transaction.service;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.helper.GroupingEnitity;
import com.att.sales.nexxus.service.NxMyPriceRepositoryServce;
import com.att.sales.nexxus.util.JsonPathUtil;
import com.att.sales.nexxus.util.NexxusJsonUtility;

import edu.emory.mathcs.backport.java.util.concurrent.atomic.AtomicInteger;

@ExtendWith(MockitoExtension.class)

public class RestCommonUtilTest {

	@Spy
	@InjectMocks
	private RestCommonUtil restCommonUtil;
	
	@Mock
	private JsonPathUtil jsonPathUtil;
	
	@Mock
	private NexxusJsonUtility nexxusJsonUtility;
	
	@Mock
	private NxMyPriceRepositoryServce nxMyPriceRepositoryServce;
	
	@Test
	public void fillAndSetListTest() {
		restCommonUtil.fillAndSetList(1, "s", new ArrayList<String>(Arrays.asList("pot")));
	}
	
	@Test
	public void fillAndSetListTest2() {
		restCommonUtil.fillAndSetList(2, "s", new ArrayList<String>(Arrays.asList("pot")));
	}
	@Test
	public void fillAndSetListTest3() {
		restCommonUtil.fillAndSetList(0, "s", new ArrayList<String>(Arrays.asList("pot")));
	}
	
	@Test
	public void fillAndSetListTest4() {
		restCommonUtil.fillAndSetList(null, "s", new ArrayList<String>(Arrays.asList("pot")));
	}
	
	@Test
	public void hasIndexTest() {
		restCommonUtil.hasIndex(1, new ArrayList<String>(Arrays.asList("pot")));
	}
	
	@Test
	public void hasIndexTest2() {
		restCommonUtil.hasIndex(0, new ArrayList<String>(Arrays.asList("pot")));
	}
	
	@Test
	public void incrementValueTest() {
		Map<String,Object> map=new HashMap<String, Object>();
		map.put("a", 0);
		restCommonUtil.incrementValue(map, "a");
	}
	
	@Test
	public void incrementValueTest2() {
		Map<String,Object> map=new HashMap<String, Object>();
		restCommonUtil.incrementValue(map, "a");
	}
	
	@Test
	public void getKeysByValueTest() {
		Map<String,Object> map=new HashMap<String, Object>();
		map.put("a", 0);
		restCommonUtil.getKeysByValue(map, "a");
	}
	
	@Test
	public void generateGroupIdTest() {
		Map<String,Object> paramMap=new HashMap<String, Object>();
		AtomicInteger o=new  AtomicInteger(0);
		Map<Integer, List<GroupingEnitity>> groupMap=new HashMap<Integer, List<GroupingEnitity>>();
		List<GroupingEnitity> dd=new ArrayList<GroupingEnitity>();
		GroupingEnitity d=new GroupingEnitity();
		d.setValue("usocId",new HashSet<String>(Arrays.asList("123")));
		dd.add(d);
		groupMap.put(0, dd);
		LinkedHashMap<String,String> filterRulesMap=new LinkedHashMap<String, String>();
		filterRulesMap.put("usocId", "123");
		filterRulesMap.put("usocIdCategory", "new");
		restCommonUtil.generateGroupId(null, groupMap, filterRulesMap, o, paramMap);
	}
	
	@Test
	public void generateGroupIdTest2() {
		Map<String,Object> paramMap=new HashMap<String, Object>();
		AtomicInteger o=new  AtomicInteger(0);
		Map<Integer, List<GroupingEnitity>> groupMap=new HashMap<Integer, List<GroupingEnitity>>();
		List<GroupingEnitity> dd=new ArrayList<GroupingEnitity>();
		GroupingEnitity d=new GroupingEnitity();
		d.setValue("usocId",new HashSet<String>(Arrays.asList("123")));
		d.setValue("usocIdCategory",new HashSet<String>(Arrays.asList("new")));
		dd.add(d);
		groupMap.put(0, dd);
		LinkedHashMap<String,String> filterRulesMap=new LinkedHashMap<String, String>();
		filterRulesMap.put("usocId", "123");
		filterRulesMap.put("usocIdCategory", "new");
		restCommonUtil.generateGroupId(null, groupMap, filterRulesMap, o, paramMap);
	}
	
	@Test
	public void generateGroupIdTest3() {
		Map<String,Object> paramMap=new HashMap<String, Object>();
		AtomicInteger o=new  AtomicInteger(0);
		Map<Integer, List<GroupingEnitity>> groupMap=new HashMap<Integer, List<GroupingEnitity>>();
		List<GroupingEnitity> dd=new ArrayList<GroupingEnitity>();
		GroupingEnitity d=new GroupingEnitity();
		d.setValue("usocId",new HashSet<String>(Arrays.asList("123")));
		d.setValue("usocIdCategory",new HashSet<String>(Arrays.asList("new")));
		dd.add(d);
		groupMap.put(0, dd);
		LinkedHashMap<String,String> filterRulesMap=null;;
		restCommonUtil.generateGroupId(null, groupMap, filterRulesMap, o, paramMap);
	}
	
	@Test
	public void geDataByJsonPathTest() {
		restCommonUtil.geDataByJsonPath("{}", "$..");
	}
	
	@Test
	public void geDataByJsonPathTest1() {
		restCommonUtil.geDataByJsonPath(null, "$..");
	}
	
	@Test
	public void getItemValueUsingJsonPathTest() {
		restCommonUtil.getItemValueUsingJsonPath("bcd{$..test}abc", "{}", String.class);
	}
	
	@Test
	public void getItemValueUsingJsonPathTest1() {
		
		restCommonUtil.getItemValueUsingJsonPath("bcd{$..test}abc", "{}", String.class);
	}
	
	@Test
	public void getItemValueUsingJsonPathTest2() {
		restCommonUtil.getItemValueUsingJsonPath("mail", "{}", String.class);
	}
	
	@Test
	public void getItemValueUsingJsonPathTest3() {
		restCommonUtil.getItemValueUsingJsonPath("mail", "{}", Integer.class);
	}
	
	@Test
	public void processAppendedCharWithJsonPathTest() {
		Object f="abc";
		when(nexxusJsonUtility.getValue(any(),any())).thenReturn(f);
		restCommonUtil.processAppendedCharWithJsonPath("$..", "{}");
		restCommonUtil.getItemValueUsingJsonPath("bcd{$..test}", "{}", String.class);
	}
	
	@Test
	public void processAppendedCharWithJsonPathTest2() {
		Object f="abc";
		when(nexxusJsonUtility.getValue(any(),any())).thenReturn(f);
		restCommonUtil.getItemValueUsingJsonPath("{$..test}abd", "{}", String.class);
	}
	
	@Test
	public void processAppendedCharWithJsonPathTest3() {
		Object f="abc";
		when(nexxusJsonUtility.getValue(any(),any())).thenReturn(f);
		restCommonUtil.getItemValueUsingJsonPath("{$..test}", "{}", String.class);
	}
	
	@Test
	public void getSingleObjectTest() {
		List<Object> lst=new ArrayList<Object>();
		lst.add("av");
		when(jsonPathUtil.search(any(),any(),any())).thenReturn(lst);
		restCommonUtil.getSingleObject("{}", "$..", String.class);
	}
	
//	@Test
//	public void getSingleObjectTest2() {
//		List<Object> lst=new ArrayList<Object>();
//		when(jsonPathUtil.search(any(),any(),any())).thenReturn(lst);
//		restCommonUtil.getSingleObject("{}", "$..test", String.class,"");
//	}
	
	@Test
	public void appendCharATest() {
		restCommonUtil.appendCharAt("a", "abb", null);
	}
	
	@Test
	public void appendCharATest2() {
		restCommonUtil.appendCharAt(null, "abb", null);
	}
	
	@Test
	public void handleCastTest() {
		restCommonUtil.handleCast(true, Boolean.class);
	}
	
	@Test
	public void handleCastTest2() {
		restCommonUtil.handleCast(1, Object.class);
	}
	
	@Test
	public void handleCastTest3() {
		restCommonUtil.handleCast(null, Object.class);
	}
	
	@Test
	public void handleCastForListTest() {
		restCommonUtil.handleCastForList(new ArrayList<String>(Arrays.asList("s")), String.class);
	}
	

	@Test
	public void handleCastForListTest2() {
		restCommonUtil.handleCastForList(new ArrayList<Boolean>(Arrays.asList(true)), Boolean.class);
	}
	
	@Test
	public void handleCastForListTest3() {
		restCommonUtil.handleCastForList(new ArrayList<Integer>(Arrays.asList(1)), Object.class);
	}
	
	@Test
	public void processDataSetNameTest() {
		restCommonUtil.processDataSetName("fn","NX_LOOKUP_DATA|IGLOO_MP_ACCESS_TECHNOLOGY_MAPPING", String.class);
	}
	
	@Test
	public void processDataSetNameTest2() {
		restCommonUtil.processDataSetName("fn","NX_LOOKUP_DATA|IGLOO_MP_ACCESS_TECHNOLOGY_MAPPING|abc", String.class);
	}
	
	@Test
	public void processDataSetNameTest3() {
		Map<String,NxLookupData> resultMap=new HashMap<String,NxLookupData>();
		NxLookupData n=new NxLookupData();
		n.setItemId("a");
		n.setDescription("ght");
		resultMap.put("a", n);
		when(nxMyPriceRepositoryServce.getLookupDataByItemId(any())).thenReturn(resultMap);
		restCommonUtil.processDataSetName("a","NX_LOOKUP_DATA|IGLOO_MP_ACCESS_TECHNOLOGY_MAPPING|abc", String.class);
	}
	
	@Test
	public void geDataInSetStringTest() {
		List<Object> lst=new ArrayList<Object>();
		lst.add("av");
		when(nexxusJsonUtility.getValueLst(any(),any())).thenReturn(lst);
		restCommonUtil.geDataInSetString("{}", "$..");
	}
	
	@Test
	public void geDataInListStringTest() {
		List<Object> lst=new ArrayList<Object>();
		lst.add("av");
		when(nexxusJsonUtility.getValueLst(any(),any())).thenReturn(lst);
		restCommonUtil.geDataInListString("{}", "$..");
	}
	
	@Test
	public void listEqualsIgnoreOrderTest() {
		restCommonUtil.listEqualsIgnoreOrder(new HashSet<String>(Arrays.asList("s")), new HashSet<String>(Arrays.asList("s")));
	}
	@Test
	public void listEqualsIgnoreOrderTest2() {
		restCommonUtil.listEqualsIgnoreOrder(new HashSet<String>(Arrays.asList("s")), new HashSet<String>(Arrays.asList("s","h")));
	}
	
	@Test
	public void listEqualsIgnoreOrderTest3() {
		restCommonUtil.listEqualsIgnoreOrder(new HashSet<String>(Arrays.asList("s")), new HashSet<String>(Arrays.asList("ss")));
	}
	

	@Test
	public void determineTypeUsingInputTest() {
		restCommonUtil.determineTypeUsingInput("1");
	}
	
	@Test
	public void determineTypeUsingInputTest3() {
		Double f=5667d;
		restCommonUtil.determineTypeUsingInput(String.valueOf(f));
	}

	@Test
	public void determineTypeUsingInputTest2() {
		Long d=123456789010l;
		restCommonUtil.determineTypeUsingInput(String.valueOf(d));
	}
	
	@Test
	public void determineTypeUsingInputTest4() {
		restCommonUtil.determineTypeUsingInput("kk");
	}
	
	@Test
	public void determineTypeUsingInputTest5() {
		restCommonUtil.determineTypeUsingInput(null);
	}
	
	@Test
	public void convertStringNumberTest() {
		Double f=5667d;
		restCommonUtil.convertStringNumber(String.valueOf(f));
	}

	@Test
	public void convertStringNumberTest1() {
		Long d=123456789010l;
		restCommonUtil.convertStringNumber(String.valueOf(d));
	}
	
	@Test
	public void convertStringNumberTest2() {
		restCommonUtil.convertStringNumber("kk");
	}
	
	@Test
	public void convertStringNumberTest3() {
		restCommonUtil.convertStringNumber(null);
	}

	@Test
	public void convertStringNumberTest4() {
		restCommonUtil.convertStringNumber("1");
	}

}
