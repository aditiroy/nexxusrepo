package com.att.sales.nexxus.myprice.transaction.service;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyMap;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doReturn;

import java.util.HashMap;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.myprice.transaction.model.UpdateTxnSiteUploadResponse;
import com.att.sales.nexxus.util.HttpRestClient;
import com.att.sales.nexxus.util.RestClientUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@ExtendWith(MockitoExtension.class)

public class UpdateTxnSiteUploadServiceImplTest {
	@Spy
	@InjectMocks
	private UpdateTxnSiteUploadServiceImpl updateTxnSiteUploadServiceImpl;
	
	@Mock
	private Environment env;
	
	@Mock
	private RestClientUtil restClient;
	
	@Mock
	private ObjectMapper mapper;
	
	@Mock
	private ObjectNode objNode;
	
	@Mock
	private HttpRestClient httpRest;
	
	@Disabled
	@Test
	public void testUpdateTransactionSiteUpload() {
		try {
			String jsonString = "{\"status\":\"COMPLETED\",\"locations\":[{\"id\":29598,\"globalLocationId\":\"000008K2KG\",\"nxSiteId\":4642,\"locName\":\"loc1\",\"street\":\"100 CONGRESS AVE\",\"city\":\"AUSTIN\",\"state\":\"TX\",\"zip\":\"78701\",\"validationStatus\":\"VALID\",\"avsqResponse\":{\"Location\":{\"GISLocationAttributes\":[{\"globalLocationId\":\"000008K2KG\",\"FieldedAddress\":{\"city\":\"AUSTIN\",\"state\":\"TX\",\"postalCode\":\"78701\",\"singleLineStandardizedAddress\":\"100 CONGRESS AVE,AUSTIN,TX,78701-4072\",\"country\":\"USA\",\"postalCodePlus4\":\"4072\"},\"LocationProperties\":{\"matchStatus\":\"M\",\"buildingClli\":\"AUSTTXDK\",\"regionFranchiseStatus\":\"Y\",\"addressMatchCode\":\"S80\",\"swcCLLI\":\"AUSTTXGR\",\"localProviderName\":\"SOUTHWESTERN BELL\",\"lataCode\":\"558\",\"primaryNpaNxx\":{\"npa\":\"512\",\"nxx\":\"232\"}}}],\"SAGLocationAttributes\":[{\"SAGProperties\":{\"region\":\"SW\"}}]}}}]}";
			Mockito.when(env.getProperty("myprice.updateTransactionSiteUploadRequest")).thenReturn("https://custompricingdev2.att.com/rest/v7/commerceDocumentsOraclecpqo_bmClone_2Transaction/{transactionId}/actions/siteUpload");
			Mockito.when(env.getProperty("myprice.username")).thenReturn("");
			Mockito.when(env.getProperty("myprice.password")).thenReturn("");
			Mockito.when(mapper.createObjectNode()).thenReturn(objNode);
			Mockito.when(objNode.with(any())).thenReturn(objNode);
			String transResponse = new Object().toString();
			Mockito.when(httpRest.callHttpRestClient(anyString(), any(), anyMap(), anyString(), anyMap(), anyString()))
			.thenReturn(transResponse);
			Mockito.when(restClient.processResult(anyString(), any()))
			.thenReturn(new UpdateTxnSiteUploadResponse());
			doReturn("").when(updateTxnSiteUploadServiceImpl).translateSiteJsonRemoveDuplicatedNxSiteId(any());
			updateTxnSiteUploadServiceImpl.updateTransactionSiteUpload("111111", jsonString, new HashMap<String, Object>());
		} catch (SalesBusinessException e) {
			e.getMessage();
		}
	}
	
	@Disabled
	@Test
	public void testException() {
		try {
			String jsonString = "{\"status\":\"COMPLETED\",\"locations\":[{\"id\":29598,\"globalLocationId\":\"000008K2KG\",\"nxSiteId\":4642,\"locName\":\"loc1\",\"street\":\"100 CONGRESS AVE\",\"city\":\"AUSTIN\",\"state\":\"TX\",\"zip\":\"78701\",\"validationStatus\":\"VALID\",\"avsqResponse\":{\"Location\":{\"GISLocationAttributes\":[{\"globalLocationId\":\"000008K2KG\",\"FieldedAddress\":{\"city\":\"AUSTIN\",\"state\":\"TX\",\"postalCode\":\"78701\",\"singleLineStandardizedAddress\":\"100 CONGRESS AVE,AUSTIN,TX,78701-4072\",\"country\":\"USA\",\"postalCodePlus4\":\"4072\"},\"LocationProperties\":{\"matchStatus\":\"M\",\"buildingClli\":\"AUSTTXDK\",\"regionFranchiseStatus\":\"Y\",\"addressMatchCode\":\"S80\",\"swcCLLI\":\"AUSTTXGR\",\"localProviderName\":\"SOUTHWESTERN BELL\",\"lataCode\":\"558\",\"primaryNpaNxx\":{\"npa\":\"512\",\"nxx\":\"232\"}}}],\"SAGLocationAttributes\":[{\"SAGProperties\":{\"region\":\"SW\"}}]}}}]}";
			Mockito.when(env.getProperty("myprice.updateTransactionSiteUploadRequest")).thenReturn("https://custompricingdev2.att.com/rest/v7/commerceDocumentsOraclecpqo_bmClone_2Transaction/{transactionId}/actions/siteUpload");
			Mockito.when(env.getProperty("myprice.username")).thenReturn("");
			Mockito.when(env.getProperty("myprice.password")).thenReturn("");
			Mockito.when(mapper.createObjectNode()).thenReturn(objNode);
			Mockito.when(objNode.with(any())).thenReturn(objNode);
			Mockito.when(httpRest.callHttpRestClient(anyString(), any(), anyMap(), anyString(), anyMap(), anyString()))
			.thenThrow(SalesBusinessException.class);
			doReturn("").when(updateTxnSiteUploadServiceImpl).translateSiteJsonRemoveDuplicatedNxSiteId(any());
			updateTxnSiteUploadServiceImpl.updateTransactionSiteUpload("111111", jsonString, new HashMap<String, Object>());
		} catch (SalesBusinessException e) {
			e.getMessage();
		}
	}



}
