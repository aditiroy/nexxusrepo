package com.att.sales.nexxus.service;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import com.att.sales.nexxus.constant.FmoConstants;
import com.att.sales.nexxus.dao.model.FmoJsonRulesModel;
import com.att.sales.nexxus.reteriveicb.model.PricePlanDetails;
import com.att.sales.nexxus.reteriveicb.model.Site;
import com.att.sales.nexxus.util.NexxusJsonUtility;

@ExtendWith(MockitoExtension.class)
public class FmoExecutorServiceTest {

	@Spy
	@InjectMocks
	FmoExecutorService fmoExecutorService;
	
	@Mock
	private FmoProcessingService fmoProcessingService;
	
	@Mock
	private NexxusJsonUtility nexusJsonUtility;
	
	@Mock
	List<FmoJsonRulesModel> value = new ArrayList<>();
	@Mock
	FmoJsonRulesModel fmoJsonRulesModel;
	@Test
	public void getSiteLevelDataTest() throws Exception {
		when(fmoExecutorService.getFmoProcessingService()).thenReturn(fmoProcessingService);
		when(fmoExecutorService.getNexusJsonUtility()).thenReturn(nexusJsonUtility);
		when(fmoProcessingService.createNewJSONObject()).thenReturn(new JSONObject());
		Site site=new Site();
		site.setAddress("address");
		site.setSiteId(12345L);
		List<FmoJsonRulesModel> value = new ArrayList<>();
		FmoJsonRulesModel fmoJsonRulesModel  =new FmoJsonRulesModel();
		fmoJsonRulesModel.setUdfQuery("AFFA");
		value.add(fmoJsonRulesModel);
		Map<String,List<FmoJsonRulesModel>> itemMap=new HashMap<>();
		itemMap.put("SITE", value);
		List<PricePlanDetails> pricePlanDetails=new ArrayList<>();
		PricePlanDetails p=new PricePlanDetails();
		p.setOfferId("11");
		p.setCountryCd("US");
		pricePlanDetails.add(p);
		Map<String,Object> methodInputMap=new HashMap<>();
		methodInputMap.put(FmoConstants.OFFER_ID, 11l);
		methodInputMap.put(FmoConstants.COUNTRY_CD, "US");
		Long offerId=9l;
		fmoExecutorService.getSiteLevelData(site, itemMap, pricePlanDetails, methodInputMap, offerId);
	}

}
