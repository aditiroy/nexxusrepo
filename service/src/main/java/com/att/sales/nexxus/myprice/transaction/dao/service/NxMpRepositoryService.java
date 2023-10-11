package com.att.sales.nexxus.myprice.transaction.dao.service;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.att.sales.nexxus.dao.model.NxDesign;
import com.att.sales.nexxus.dao.model.NxMpConfigMapping;
import com.att.sales.nexxus.dao.model.NxMpDeal;
import com.att.sales.nexxus.dao.model.NxMpDesignDocument;
import com.att.sales.nexxus.dao.model.NxMpSiteDictionary;
import com.att.sales.nexxus.dao.model.NxMpSolutionDetails;
import com.att.sales.nexxus.dao.repository.NxDesignRepository;
import com.att.sales.nexxus.dao.repository.NxMpConfigMappingRepository;
import com.att.sales.nexxus.dao.repository.NxMpDealRepository;
import com.att.sales.nexxus.dao.repository.NxMpDesignDocumentRepository;
import com.att.sales.nexxus.dao.repository.NxMpSiteDictionaryRepository;
import com.att.sales.nexxus.dao.repository.NxMpSolutionDetailsRepository;
import com.att.sales.nexxus.dao.repository.SalesMsDao;

/**
 * @author KumariMuktta
 *
 */
@Repository
@Transactional
public class NxMpRepositoryService {

	@Autowired
	private NxMpDealRepository nxMpDealRepository;

	@Autowired
	private NxMpSolutionDetailsRepository nxMpSolutionDetailsRepository;

	@Autowired
	private NxMpSiteDictionaryRepository nxMpSiteDictionaryRepository;

	@Autowired
	private NxMpDesignDocumentRepository nxMpDesignDocumentRepository;

	@Autowired
	private NxMpConfigMappingRepository nxMpConfigMappingRepository;
	
	@Autowired
	private NxDesignRepository nxDesignRepository;
	
	@Autowired
	private SalesMsDao salesMsDao;

	public NxMpDeal getNxMpDeal(String myPriceTransId) {
		return nxMpDealRepository.findByTransactionId(myPriceTransId);
	}

	public NxMpSolutionDetails getNxMpSolutionDetails(Long nxTransactionId) {
		return nxMpSolutionDetailsRepository.findByNxTxnId(nxTransactionId);
	}

	public NxMpSiteDictionary getNxMpSiteDictionary(Long nxTransactionId, Long siteRefId) {
		return nxMpSiteDictionaryRepository.findByNxTxnIdAndSiteRefId(nxTransactionId, siteRefId);
	}

	public NxMpDesignDocument setNxMpDesignDocument(NxMpDesignDocument nxMpDesignDocument) {
		return nxMpDesignDocumentRepository.saveAndFlush(nxMpDesignDocument);
	}

	public Long getCountNxMpDesignDocument(Long nxTransactionId, Long mpDocumentNumber) {
		return nxMpDesignDocumentRepository.findCountByNxTxnIdAndMpSolutionId(nxTransactionId, mpDocumentNumber);
	}

	public List<NxMpDesignDocument> getNxMpDesignDocument(Long nxTransactionId, Long mpDocumentNumber, String parentDocNum) {
		return nxMpDesignDocumentRepository.findByNxTxnIdAndMpProdLineId(nxTransactionId, mpDocumentNumber,
				parentDocNum);
	}

	public NxMpDesignDocument updateNxMpDesignDocument(NxMpDesignDocument nxMpDesignDocument) {
		return nxMpDesignDocumentRepository.save(nxMpDesignDocument);
	}

	public NxMpDesignDocument findByNxTxnIdAndMpSolutionId(Long nxTransactionId, String parentDocNum) {
		return nxMpDesignDocumentRepository.findByNxTxnIdAndMpSolutionId(nxTransactionId, parentDocNum);
	}

	public List<NxMpConfigMapping> findByOfferAndRuleName(String offerName, String ruleName) {
		return nxMpConfigMappingRepository.findByOfferAndRuleName(offerName, ruleName);
	}
	
	public List<NxMpConfigMapping> findByMultipleOffersAndRuleName(Set<String> offers, String ruleName) {
		return nxMpConfigMappingRepository.findByMultipleOffersAndRuleName(offers, ruleName);
	}
	
	public void saveNxDesignDatas(NxDesign entity) {
		nxDesignRepository.save(entity);
	}
	
	public Map<String,Set<String>>  getDataByNxtxnIdAndDesignId(Long nxTransactionId,Long designId) {
		Map<String,Set<String>>  result=new HashMap<String, Set<String>>();
		List<NxMpDesignDocument> resultList=nxMpDesignDocumentRepository.getDataByNxTxnIdAndNxDesignId(nxTransactionId,designId);
		Optional.ofNullable(resultList).map(List::stream).orElse(Stream.empty()).filter(Objects::nonNull)
		.forEach(data -> {
			if (!result.containsKey(data.getMpSolutionId())) {
				result.put(data.getMpSolutionId(), new HashSet<String>());
			}
			result.get(data.getMpSolutionId()).add(data.getMpProductLineId());
		});
		return result;
	}
	
	
	
	public Map<String,String>  getDataByNxtxnId(Long nxTransactionId) {
		Map<String,String>  result=new HashMap<String, String>();
		List<NxMpDesignDocument> resultList=nxMpDesignDocumentRepository.findByNxTxnId(nxTransactionId);
		Optional.ofNullable(resultList).map(List::stream).orElse(Stream.empty()).filter(Objects::nonNull)
		.forEach(data -> {
			result.put(data.getMpSolutionId(),data.getMpProductLineId());
		});
		return result;
	}
	
	public Map<String,Set<String>>  getDataByNxtxnIdInr(Long nxTransactionId) {
		Map<String,Set<String>>  result=new HashMap<String, Set<String>>();
		List<NxMpDesignDocument> resultList=nxMpDesignDocumentRepository.findByNxTxnId(nxTransactionId);
		Optional.ofNullable(resultList).map(List::stream).orElse(Stream.empty()).filter(Objects::nonNull)
		.forEach(data -> {
			if (!result.containsKey(data.getMpSolutionId())) {
				result.put(data.getMpSolutionId(), new HashSet<String>());
			}
			result.get(data.getMpSolutionId()).add(data.getMpProductLineId());
		});
		return result;
	}
	
	public int updateSolAndProductResponse(String productId,Date modifiedDate,String solutionId,Long nxTxnId) {
		return nxMpDesignDocumentRepository.updateDesignSolIdAndProductResponse(productId, modifiedDate, solutionId, nxTxnId);
	}
	
	public boolean checkProductForUpdate(String mpSolutionId,Long nxTxnId) {
		List<NxMpDesignDocument>  result=nxMpDesignDocumentRepository.checkProductForUpdate(mpSolutionId, nxTxnId);
		if(CollectionUtils.isNotEmpty(result)) {
			return true;
		}
		return false;
	}
	
	public String getOfferNameByOfferId(int id) {
		return salesMsDao.getOfferNameByOfferId(id);
	}
	
	public String getOfferIdByOfferName(String offerabbr) {
		return salesMsDao.getOfferIdByOfferName(offerabbr);
	}
}
