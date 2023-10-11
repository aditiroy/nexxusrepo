package com.att.sales.nexxus.myprice.transaction.service;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyMap;
import static org.mockito.Mockito.anyString;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.model.NxMpSiteDictionary;
import com.att.sales.nexxus.dao.model.NxMpSolutionDetails;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.dao.repository.NxMpSiteDictionaryRepository;
import com.att.sales.nexxus.dao.repository.NxMpSolutionDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxSolutionDetailsRepository;
import com.att.sales.nexxus.handlers.GetOptyInfoWSHandler;
import com.att.sales.nexxus.myprice.transaction.model.CreateTransactionResponse;
import com.att.sales.nexxus.reteriveicb.model.Component;
import com.att.sales.nexxus.reteriveicb.model.Contact;
import com.att.sales.nexxus.reteriveicb.model.Offer;
import com.att.sales.nexxus.reteriveicb.model.Port;
import com.att.sales.nexxus.reteriveicb.model.RetreiveICBPSPRequest;
import com.att.sales.nexxus.reteriveicb.model.Site;
import com.att.sales.nexxus.reteriveicb.model.Solution;
import com.att.sales.nexxus.reteriveicb.model.UDFBaseData;
import com.att.sales.nexxus.rome.model.GetOptyResponse;
import com.att.sales.nexxus.util.HttpRestClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@ExtendWith(MockitoExtension.class)

public class IUpdateTransactionImplTest {

	@InjectMocks
	private IUpdateTransactionImpl iUpdateTransactionImpl;

	@Mock
	private Environment env;

	@Mock
	private EntityManager em;

	@Mock
	private NxMpSolutionDetailsRepository nxMpSolutionDetailRepository;

	@Mock
	private NxMpSiteDictionaryRepository nxMpSiteDictionaryRepository;

	@Mock
	private GetOptyInfoWSHandler getOptyInfoWSHandler;

	@Mock
	private Query query;
	
	@Mock
	private NxLookupDataRepository nxLookupDataRepository;
	
	@Mock
	private UpdateTxnSiteUploadServiceImpl updateTxnSiteUploadServiceImpl;
	
	@Mock
	private NxSolutionDetailsRepository nxSolutionDetailsRepository;
	
	private RetreiveICBPSPRequest retreiveICBPSPRequest;
	private CreateTransactionResponse createTransactionResponse;
	private Map<String, Object> parammap;
	private Map<String, Object> response;
	
	@Mock
	private HttpRestClient httpRest;
	
	@BeforeEach
	public void init() {
		createTransactionResponse = new CreateTransactionResponse();
		createTransactionResponse.setMyPriceTransacId("12345");
		
		retreiveICBPSPRequest = new RetreiveICBPSPRequest();
		Solution solution = new Solution();
		//solution.setMarketStrata("1234");
		solution.setUserId("lh121m");
		solution.setOptyId("1-abph");
		solution.setSolutionId(1L);
		solution.setAutomationInd("Y");
		List<String> opportunitySalesTeam = new ArrayList<>();
		opportunitySalesTeam.add("1");
		opportunitySalesTeam.add("2");
		solution.setOpportunitySalesTeam(opportunitySalesTeam);
		List<Offer> offers = new ArrayList<>();
		Offer offer = new Offer();
		offer.setOfferId("103");
		List<Site> sites = new ArrayList<>();
		Site site = new Site();
		List<Port> designSiteOfferPortList = new ArrayList<>();
		Port port = new Port();
		List<Component> componentList = new ArrayList<>();
		Component componet = new Component();
		componet.setComponentCodeType("Port");
		componentList.add(componet);
		port.setComponent(componentList);
		designSiteOfferPortList.add(port);
		site.setDesignSiteOfferPort(designSiteOfferPortList);
		sites.add(site);
		offer.setSite(sites);
		offers.add(offer);
		solution.setOffers(offers);
		Contact contact = new Contact();
		contact.setContactType("Customer");
		List<Contact> contacts = new ArrayList<>();
		contacts.add(contact);
		solution.setContact(contacts);
		retreiveICBPSPRequest.setSolution(solution);
		
		parammap = new HashMap<>();
		
		response = new HashMap<String, Object>();
		response.put(MyPriceConstants.RESPONSE_CODE, 200);
		response.put(MyPriceConstants.RESPONSE_MSG, "Success");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testUpdateTransactionCleanSave() throws SalesBusinessException, IOException, URISyntaxException {
		Mockito.when(getOptyInfoWSHandler.initiateGetOptyInfoWebService(any())).thenReturn(new GetOptyResponse());
		Mockito.when(em.createNativeQuery(anyString())).thenReturn(query);
		Mockito.when(nxMpSiteDictionaryRepository.findByNxTxnId(anyLong())).thenReturn(null);
		Mockito.when(nxMpSiteDictionaryRepository.save(any(NxMpSiteDictionary.class))).thenReturn(new NxMpSiteDictionary());
		Mockito.when(nxMpSolutionDetailRepository.findByNxTxnId(anyLong())).thenReturn(null);
		Mockito.when(nxMpSolutionDetailRepository.save(any(NxMpSolutionDetails.class))).thenReturn(new NxMpSolutionDetails());
		Mockito.when(nxSolutionDetailsRepository.findByExternalKey(anyLong())).thenReturn(null);
		Mockito.when(em.createNamedQuery(anyString())).thenReturn(query);
		BigDecimal b = new BigDecimal(11);
		Mockito.when(query.getSingleResult()).thenReturn(b);
		Mockito.when(env.getProperty("myPrice.updateTransactionCleanSaveRequest")).thenReturn("https://custompricingst.att.com/rest/v8/commerceDocumentsOraclecpqo_bmClone_2Transaction/{transactionId}/actions/cleanSave");
		String res = new Object().toString();
		Mockito.when(httpRest.callHttpRestClient(anyString(), any(), anyMap(), anyString(), anyMap(), anyString()))
		.thenReturn(res);
		retreiveICBPSPRequest.getSolution().setExternalKey(1234L);
		iUpdateTransactionImpl.updateTransactionCleanSave(retreiveICBPSPRequest, createTransactionResponse, parammap);
	}
	
	
	@Test
	public void getCLLiValues() {
		List<UDFBaseData> udfList = new ArrayList<>();
		UDFBaseData uDFBaseData1 = new UDFBaseData();
		List<String> texts = new ArrayList<>();
		texts.add("1");
		uDFBaseData1.setUdfAttributeText(texts);
		uDFBaseData1.setUdfId(200045);
		UDFBaseData uDFBaseData2 = new UDFBaseData();
		List<String> texts1 = new ArrayList<>();
		texts1.add("1");
		uDFBaseData2.setUdfAttributeText(texts1);
		uDFBaseData2.setUdfId(20184);

		udfList.add(uDFBaseData1);
		udfList.add(uDFBaseData2);
		Map<String, String> cLLiMap = null;
		cLLiMap = new HashMap<String, String>();
		Predicate<UDFBaseData> p = (
				obj) -> (obj.getUdfId() == 200045 || obj.getUdfId() == 20184 || obj.getUdfId() == 200160);
		List<UDFBaseData> udfdata = (List<UDFBaseData>) udfList.stream().filter(p).collect(Collectors.toList());
		// String swcLLi
		AtomicReference<String> swcLLiRef = new AtomicReference<String>();
		udfdata.stream().forEach(o -> {
			String swcLLi = o.getUdfAttributeText().get(0);
			swcLLiRef.set(swcLLi);
		});
		cLLiMap.put("swcLLi", swcLLiRef.get());
		iUpdateTransactionImpl.getCLLiValues(udfList);
	}

	@Test
	public void getCLLiValuesTest() {

		List<UDFBaseData> udfList = new ArrayList<>();
		UDFBaseData uDFBaseData2 = new UDFBaseData();
		List<String> texts1 = new ArrayList<>();
		texts1.add("1");
		uDFBaseData2.setUdfAttributeText(texts1);
		uDFBaseData2.setUdfId(20184);
		UDFBaseData uDFBaseData3 = new UDFBaseData();
		List<String> texts2 = new ArrayList<>();
		texts2.add("1");
		uDFBaseData3.setUdfAttributeText(texts2);
		uDFBaseData3.setUdfId(200160);
		udfList.add(uDFBaseData2);
		udfList.add(uDFBaseData3);
		Map<String, String> cLLiMap = null;
		cLLiMap = new HashMap<String, String>();
		Predicate<UDFBaseData> p = (
				obj) -> (obj.getUdfId() == 200045 || obj.getUdfId() == 20184 || obj.getUdfId() == 200160);
		List<UDFBaseData> udfdata = (List<UDFBaseData>) udfList.stream().filter(p).collect(Collectors.toList());
		// String swcLLi
		AtomicReference<String> buildingClliRef = new AtomicReference<String>();
		udfdata.stream().forEach(o -> {
			String buildingClli = o.getUdfAttributeText().get(0);
			buildingClliRef.set(buildingClli);
		});
		cLLiMap.put("buildingClli", buildingClliRef.get());
		iUpdateTransactionImpl.getCLLiValues(udfList);
	}

	@Test
	public void getOptyDetails() throws SalesBusinessException {
		RetreiveICBPSPRequest retreiveICBPSPRequest = new RetreiveICBPSPRequest();
		Solution solution = new Solution();
		solution.setMarketStrata("12345");
		solution.setUserId("lh121m");
		solution.setOptyId("1-abph");
		solution.setSolutionId(1L);
		List<String> opportunitySalesTeam = new ArrayList<>();
		opportunitySalesTeam.add("1");
		opportunitySalesTeam.add("2");
		solution.setOpportunitySalesTeam(opportunitySalesTeam);
		List<Offer> offers = new ArrayList<>();
		Offer offer = new Offer();
		offers.add(offer);
		solution.setOffers(offers);
		Contact contact = new Contact();
		contact.setContactType("Customer");
		List<Contact> contacts = new ArrayList<>();
		contacts.add(contact);
		solution.setContact(contacts);
		retreiveICBPSPRequest.setSolution(solution);

		String attUid = retreiveICBPSPRequest.getSolution().getUserId();
		String optyId = retreiveICBPSPRequest.getSolution().getOptyId();

		Map<String, Object> getOptyInfoRequest = new HashMap<>();
		getOptyInfoRequest.put("attuid", attUid);
		getOptyInfoRequest.put("optyId", optyId);
		getOptyInfoRequest.put("myPriceIntiated", "Y");

		GetOptyResponse getOptyResponse = new GetOptyResponse();
		Mockito.when(getOptyInfoWSHandler.initiateGetOptyInfoWebService(getOptyInfoRequest))
				.thenReturn(getOptyResponse);
		iUpdateTransactionImpl.getOptyDetails(retreiveICBPSPRequest);
	}

	
	@Test
	public void getupdateTransactionCSRTJSON() throws JsonProcessingException {
		Map<Object, Object> updateCleanTransactionCleanSaveRequestMap = new HashMap<>();
		ObjectMapper obj = new ObjectMapper();
		obj.enable(SerializationFeature.INDENT_OUTPUT);
		obj.writeValueAsString(updateCleanTransactionCleanSaveRequestMap);
		iUpdateTransactionImpl.getupdateTransactionCSRTJSON(updateCleanTransactionCleanSaveRequestMap);
	}

	@Test
	public void getSiteAddressJson() throws JsonProcessingException {
		List<Object> siteAddress = new ArrayList<>();
		ObjectMapper obj = new ObjectMapper();
		obj.writerWithDefaultPrettyPrinter().writeValueAsString(siteAddress);
		iUpdateTransactionImpl.getSiteAddressJson(siteAddress);
	}
	
	@Test
	public void getLowerCaseString() {
		String data = "ABCD12345";
		data.toLowerCase();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testUpdateTransactionCleanSave1() throws SalesBusinessException, IOException, URISyntaxException {
		Mockito.when(getOptyInfoWSHandler.initiateGetOptyInfoWebService(any())).thenReturn(new GetOptyResponse());
		Mockito.when(em.createNativeQuery(anyString())).thenReturn(query);
		Mockito.when(nxMpSiteDictionaryRepository.findByNxTxnId(anyLong())).thenReturn(null);
		Mockito.when(nxMpSiteDictionaryRepository.save(any(NxMpSiteDictionary.class))).thenReturn(new NxMpSiteDictionary());
		Mockito.when(nxMpSolutionDetailRepository.findByNxTxnId(anyLong())).thenReturn(null);
		Mockito.when(nxMpSolutionDetailRepository.save(any(NxMpSolutionDetails.class))).thenReturn(new NxMpSolutionDetails());
		Mockito.when(em.createNamedQuery(anyString())).thenReturn(query);
		BigDecimal b = new BigDecimal(11);
		Mockito.when(query.getSingleResult()).thenReturn(b);
		Mockito.when(env.getProperty("myPrice.updateTransactionCleanSaveRequest")).
		thenReturn("https://custompricingst.att.com/rest/v8/commerceDocumentsOraclecpqo_bmClone_2Transaction/{transactionId}/actions/cleanSave");
		String res = new Object().toString();
		Mockito.when(httpRest.callHttpRestClient(anyString(), any(), anyMap(), anyString(), anyMap(), anyString()))
		.thenReturn(res);
		retreiveICBPSPRequest.getSolution().getOffers().get(0).setOfferId("6");
		retreiveICBPSPRequest.getSolution().setContractType("New");
		retreiveICBPSPRequest.getSolution().setPpcosUser("Y");
		retreiveICBPSPRequest.getSolution().setContractNumber("1224");
		NxLookupData nxLookup=new NxLookupData();
		nxLookup.setDescription("ABC");
		Mockito.when(nxLookupDataRepository.findTopByDatasetNameAndItemId(any(),any())).thenReturn(nxLookup);
		retreiveICBPSPRequest.getSolution().setExternalKey(1234L);
		iUpdateTransactionImpl.updateTransactionCleanSave(retreiveICBPSPRequest, createTransactionResponse, parammap);
	}

}