package com.att.sales.nexxus.inr;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

import com.att.sales.nexxus.dao.model.NxKeyFieldPathModel;
import com.att.sales.nexxus.dao.model.NxLineItemLookUpFieldModel;

public class QueryLookupTest {
	
	@Test
	public void test() {
		QueryLookup queryLookup = new QueryLookup(null, null, null);
		List<String> requiredFields = new ArrayList<>();
		Map<String, NxKeyFieldPathModel> queryMapping = new HashMap<>();
		NxLineItemLookUpFieldModel lookupEntity = new NxLineItemLookUpFieldModel();
		queryLookup.setRequiredFields(requiredFields);
		queryLookup.setQueryMapping(queryMapping);
		queryLookup.setLookupEntity(lookupEntity);
		assertSame(requiredFields, queryLookup.getRequiredFields());
		assertSame(queryMapping, queryLookup.getQueryMapping());
		assertSame(lookupEntity, queryLookup.getLookupEntity());
		assertNotNull(queryLookup.toString());
	}
}
