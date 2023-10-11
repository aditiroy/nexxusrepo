package com.att.sales.nexxus.dao.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.att.sales.nexxus.dao.model.InrXmlToJsonRule;
import com.att.sales.nexxus.dao.model.NxLineItemLookUpDataModel;
import com.att.sales.nexxus.dao.model.NxLineItemLookUpFieldModel;
import com.att.sales.nexxus.dao.repository.InrXmlToJsonRuleDao.InrXmlToJsonRuleDaoResult;
import com.att.sales.nexxus.inr.InrIntermediateJsonGenerator;
@ExtendWith(MockitoExtension.class)
public class InrXmlToJsonRuleDaoTest {
	@Mock
	private EntityManager em;

	@InjectMocks
	private InrXmlToJsonRuleDao inrXmlToJsonRuleDao;

	@Mock
	private TypedQuery<InrXmlToJsonRule> queryInrXmlToJsonRule;

	@Mock
	private TypedQuery<NxLineItemLookUpFieldModel> queryNxLineItemLookUpFieldModel;

	@Mock
	private TypedQuery<NxLineItemLookUpDataModel> queryNxLineItemLookUpDataModel;

//	@Test
//	public void testGetClassPropertyFromTableColumn() {
//		String tableColumn = "FIELD1_VALUE";
//		String classProperty = inrXmlToJsonRuleDao.getClassPropertyFromTableColumn(tableColumn);
//		assertEquals("field1Value", classProperty);
//	}
	@Test
	public void testGetInrXmlToJsonRuleMap() {
		String tag1 = "tag1";
		String tag2 = "tag2";
		when(em.createQuery(any(), eq(InrXmlToJsonRule.class))).thenReturn(queryInrXmlToJsonRule);

		InrXmlToJsonRule inrXmlToJsonRule = new InrXmlToJsonRule();
		inrXmlToJsonRule.setXmlStartTag(tag1);
		InrXmlToJsonRule inrXmlToJsonRule1 = new InrXmlToJsonRule();
		inrXmlToJsonRule1.setXmlStartTag(tag2);
		List<InrXmlToJsonRule> resultList = new ArrayList<>();
		resultList.add(inrXmlToJsonRule);
		resultList.add(inrXmlToJsonRule1);

		when(queryInrXmlToJsonRule.getResultList()).thenReturn(resultList);

		Map<String, InrXmlToJsonRule> inrXmlToJsonRuleMap = inrXmlToJsonRuleDao.getInrXmlToJsonRuleMap(null);
		assertSame(inrXmlToJsonRule, inrXmlToJsonRuleMap.get(tag1));
		assertSame(inrXmlToJsonRule1, inrXmlToJsonRuleMap.get(tag2));
		assertEquals(2, inrXmlToJsonRuleMap.size());
	}

//	@Test 
//	public void testFindLineItemQueryConditionLookUpByOfferNameAndInputType() {
//		when(em.createQuery(any(), eq(NxLineItemLookUpFieldModel.class))).thenReturn(queryNxLineItemLookUpFieldModel);
//		
//		NxLineItemLookUpFieldModel nxLineItemLookUpFieldModel = new NxLineItemLookUpFieldModel();
//		NxKeyFieldPathModel nxKeyFieldPathModel = new NxKeyFieldPathModel();
//		nxKeyFieldPathModel.setKeyFieldName("country");
//		nxKeyFieldPathModel.setFieldName("FIELD4_VALUE");
//		NxKeyFieldPathModel nxKeyFieldPathModel1 = new NxKeyFieldPathModel();
//		nxKeyFieldPathModel1.setKeyFieldName("state");
//		nxKeyFieldPathModel1.setFieldName("FIELD8_VALUE");
//		Set<NxKeyFieldPathModel> keyFieldMapping = new HashSet<>();
//		keyFieldMapping.add(nxKeyFieldPathModel);
//		keyFieldMapping.add(nxKeyFieldPathModel1);
//		nxLineItemLookUpFieldModel.setKeyFieldMapping(keyFieldMapping);
//		List<NxLineItemLookUpFieldModel> resultList = new ArrayList<>();
//		resultList.add(nxLineItemLookUpFieldModel);
//		
//		when(queryNxLineItemLookUpFieldModel.getResultList()).thenReturn(resultList);
//		List<Map<String, String>> lineItemQueryConditionLookUp = inrXmlToJsonRuleDao.findLineItemQueryConditionLookUpByOfferNameAndInputType(null, null);
//		assertEquals(1, lineItemQueryConditionLookUp.size());
//		Map<String, String> map = lineItemQueryConditionLookUp.get(0);
//		assertEquals("country", map.get("field4Value"));
//		assertEquals("state", map.get("field8Value"));
//	}

//	@Test
//	public void testFindLineItem() {
//		when(em.createQuery(any(), eq(NxLineItemLookUpDataModel.class))).thenReturn(queryNxLineItemLookUpDataModel);
//		
//		Map<String, String> queryCondition = new HashMap<>();
//		queryCondition.put("field4Value", "country");
//		queryCondition.put("field8Value", "state");
//		Map<String, String> jsonEntry = new HashMap<>();
//		jsonEntry.put("country", "US");
//		jsonEntry.put("state", "NJ");
//		
//		NxLineItemLookUpDataModel nxLineItemLookUpDataModel = new NxLineItemLookUpDataModel();
//		NxLineItemLookUpDataModel nxLineItemLookUpDataModel1 = new NxLineItemLookUpDataModel();
//		List<NxLineItemLookUpDataModel> resultList = new ArrayList<>();
//		resultList.add(nxLineItemLookUpDataModel);
//		resultList.add(nxLineItemLookUpDataModel1);
//		
//		when(queryNxLineItemLookUpDataModel.getResultList()).thenReturn(resultList);
//		
//		List<NxLineItemLookUpDataModel> result = inrXmlToJsonRuleDao.findLineItem(queryCondition, jsonEntry, null);
//		assertEquals(2, result.size()); 
//	}

	@Test
	public void testGetInrXmlToJsonRuleDaoResult() {
		String rootTag = "rootTag";
		when(em.createQuery(any(), eq(InrXmlToJsonRule.class))).thenReturn(queryInrXmlToJsonRule);

		String tag1 = "tag1";
		String tag2 = "tag2";
		String tag3 = "tag3";

		InrXmlToJsonRule inrXmlToJsonRule = new InrXmlToJsonRule();
		inrXmlToJsonRule.setXmlStartTag(tag1);
		inrXmlToJsonRule.setFieldNullYn("Y");
		inrXmlToJsonRule.setOperations(" ");


		InrXmlToJsonRule inrXmlToJsonRule1 = new InrXmlToJsonRule();
		inrXmlToJsonRule1.setXmlStartTag(tag2);
		inrXmlToJsonRule1.setFieldNullYn("Y");
		inrXmlToJsonRule1.setOperations(InrIntermediateJsonGenerator.FALLOUTMATCHINGID);

		InrXmlToJsonRule inrXmlToJsonRule2 = new InrXmlToJsonRule();
		inrXmlToJsonRule2.setXmlStartTag(tag3);

		List<InrXmlToJsonRule> resultList = new ArrayList<>();
		resultList.add(inrXmlToJsonRule);
		resultList.add(inrXmlToJsonRule1);
		resultList.add(inrXmlToJsonRule2);

		when(queryInrXmlToJsonRule.getResultList()).thenReturn(resultList);

		InrXmlToJsonRuleDaoResult inrXmlToJsonRuleDaoResult = inrXmlToJsonRuleDao.getInrXmlToJsonRuleDaoResult(rootTag);
		String falloutMatchingTag = inrXmlToJsonRuleDaoResult.getFalloutMatchingTags().get(0);
		Map<String, InrXmlToJsonRule> inrXmlToJsonRuleMap = inrXmlToJsonRuleDaoResult.getInrXmlToJsonRuleMap();
		assertSame(inrXmlToJsonRule, inrXmlToJsonRuleMap.get(tag1));
		assertSame(inrXmlToJsonRule1, inrXmlToJsonRuleMap.get(tag2));
		assertEquals(tag2, falloutMatchingTag);
		assertEquals(2,inrXmlToJsonRuleDaoResult.getFieldNullTags().size());
	}

}
