package com.att.sales.nexxus.myprice.transaction.dao.service;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.att.sales.nexxus.dao.model.NxDesign;
import com.att.sales.nexxus.dao.model.NxMpDesignDocument;
import com.att.sales.nexxus.dao.repository.NxDesignRepository;
import com.att.sales.nexxus.dao.repository.NxMpConfigMappingRepository;
import com.att.sales.nexxus.dao.repository.NxMpDealRepository;
import com.att.sales.nexxus.dao.repository.NxMpDesignDocumentRepository;
import com.att.sales.nexxus.dao.repository.NxMpSiteDictionaryRepository;
import com.att.sales.nexxus.dao.repository.NxMpSolutionDetailsRepository;
import com.att.sales.nexxus.dao.repository.SalesMsDao;

@ExtendWith(MockitoExtension.class)
public class NxMpRepositoryServiceTest {
	
	
	@Spy
	@InjectMocks
	private NxMpRepositoryService nxMpRepositoryService;
	
	@Mock
	private NxMpDealRepository nxMpDealRepository;

	@Mock
	private NxMpSolutionDetailsRepository nxMpSolutionDetailsRepository;

	@Mock
	private NxMpSiteDictionaryRepository nxMpSiteDictionaryRepository;

	@Mock
	private NxMpDesignDocumentRepository nxMpDesignDocumentRepository;

	@Mock
	private NxMpConfigMappingRepository nxMpConfigMappingRepository;
	
	@Mock
	private NxDesignRepository nxDesignRepository;
	
	@Mock
	private SalesMsDao salesMsDao;
	
	@Test
	public void getNxMpDealTest() {
		nxMpRepositoryService.getNxMpDeal("A");
	}
	
	@Test
	public void getNxMpSolutionDetailsTest() {
		nxMpRepositoryService.getNxMpSolutionDetails(1l);
	}
	
	@Test
	public void getNxMpSiteDictionary() {
		nxMpRepositoryService.getNxMpSiteDictionary(1l,2l);
	}
	
	
	@Test
	public void setNxMpDesignDocument() {
		NxMpDesignDocument nxMpDesignDocument=new NxMpDesignDocument();
		nxMpRepositoryService.setNxMpDesignDocument(nxMpDesignDocument);
	}
	
	@Test
	public void getCountNxMpDesignDocument() {
		nxMpRepositoryService.getCountNxMpDesignDocument(1l, 2l);
	}
	
	@Test
	public void getNxMpDesignDocument() {
		nxMpRepositoryService.getNxMpDesignDocument(1l, 2l, "a");
	}
	
	@Test
	public void updateNxMpDesignDocument() {
		NxMpDesignDocument nxMpDesignDocument=new NxMpDesignDocument();
		nxMpRepositoryService.updateNxMpDesignDocument(nxMpDesignDocument);
	}
	
	@Test
	public void findByNxTxnIdAndMpSolutionId() {
		nxMpRepositoryService.findByNxTxnIdAndMpSolutionId(1l, "A");
	}
	
	@Test
	public void findByOfferAndRuleName() {
		nxMpRepositoryService.findByOfferAndRuleName("A", "S");
	}
	
	@Test
	public void findByMultipleOffersAndRuleName() {
		nxMpRepositoryService.findByMultipleOffersAndRuleName(new HashSet<String>(),"t");
	}
	
	@Test
	public void saveNxDesignDatas() {
		nxMpRepositoryService.saveNxDesignDatas(new NxDesign());
	}
	
	@Test
	public void getOfferNameByOfferId() {
		nxMpRepositoryService.getOfferNameByOfferId(1);
	}
	
	
	@Test
	public void getOfferIdByOfferName() {
		nxMpRepositoryService.getOfferIdByOfferName("t");
	}
	
	@Test
	public void getDataByNxtxnIdAndDesignIdTest() {
		List<NxMpDesignDocument> resultList=new ArrayList<NxMpDesignDocument>();
		NxMpDesignDocument nm=new NxMpDesignDocument();
		nm.setMpSolutionId("1");
		resultList.add(nm);
		NxMpDesignDocument nm2=new NxMpDesignDocument();
		nm2.setMpSolutionId("5");
		resultList.add(nm2);
		when(nxMpDesignDocumentRepository.getDataByNxTxnIdAndNxDesignId(any(),any())).thenReturn(resultList);
		nxMpRepositoryService.getDataByNxtxnIdAndDesignId(1l, 2l);
	}
	
	@Test
	public void getDataByNxtxnIdTest() {
		List<NxMpDesignDocument> resultList=new ArrayList<NxMpDesignDocument>();
		NxMpDesignDocument nm=new NxMpDesignDocument();
		nm.setMpSolutionId("1");
		resultList.add(nm);
		NxMpDesignDocument nm2=new NxMpDesignDocument();
		nm2.setMpSolutionId("5");
		resultList.add(nm2);
		when(nxMpDesignDocumentRepository.findByNxTxnId(any())).thenReturn(resultList);
		nxMpRepositoryService.getDataByNxtxnId(2l);
	}
	
	@Test
	public void getDataByNxtxnIdInrTest() {
		List<NxMpDesignDocument> resultList=new ArrayList<NxMpDesignDocument>();
		NxMpDesignDocument nm=new NxMpDesignDocument();
		nm.setMpSolutionId("1");
		resultList.add(nm);
		NxMpDesignDocument nm2=new NxMpDesignDocument();
		nm2.setMpSolutionId("5");
		resultList.add(nm2);
		when(nxMpDesignDocumentRepository.findByNxTxnId(any())).thenReturn(resultList);
		nxMpRepositoryService.getDataByNxtxnIdInr(2l);
	}
	
	@Test
	public void updateSolAndProductResponseTest() {
		nxMpRepositoryService.updateSolAndProductResponse("123", new Date(), "67", 7l);
	}
	
	@Test
	public void checkProductForUpdate() {
		List<NxMpDesignDocument> resultList=new ArrayList<NxMpDesignDocument>();
		NxMpDesignDocument nm=new NxMpDesignDocument();
		nm.setMpSolutionId("1");
		resultList.add(nm);
		NxMpDesignDocument nm2=new NxMpDesignDocument();
		nm2.setMpSolutionId("5");
		resultList.add(nm2);
		when(nxMpDesignDocumentRepository.checkProductForUpdate(any(),any())).thenReturn(resultList);
		nxMpRepositoryService.checkProductForUpdate("12", 4l);
	}
	
	@Test
	public void checkProductForUpdate2() {
		List<NxMpDesignDocument> resultList=new ArrayList<NxMpDesignDocument>();
		when(nxMpDesignDocumentRepository.checkProductForUpdate(any(),any())).thenReturn(resultList);
		nxMpRepositoryService.checkProductForUpdate("12", 4l);
	}

}
