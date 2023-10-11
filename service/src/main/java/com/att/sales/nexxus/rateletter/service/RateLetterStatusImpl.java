package com.att.sales.nexxus.rateletter.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.att.aft.dme2.internal.apache.commons.collections.CollectionUtils;
import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.framework.service.BaseServiceImpl;
import com.att.sales.nexxus.common.CommonConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.constant.TDDConstants;
import com.att.sales.nexxus.dao.model.NxMpDeal;
import com.att.sales.nexxus.dao.model.NxMpPriceDetails;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.repository.NxMpDealRepository;
import com.att.sales.nexxus.dao.repository.NxMpPriceDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxSolutionDetailsRepository;
import com.att.sales.nexxus.dmaap.mr.util.DmaapPublishEventsService;
import com.att.sales.nexxus.myprice.transaction.model.GetTransactionResponse;
import com.att.sales.nexxus.myprice.transaction.service.GetTransactionLineServiceImpl;
import com.att.sales.nexxus.myprice.transaction.service.GetTransactionServiceImpl;
import com.att.sales.nexxus.rateletter.model.RateLetterStatusRequest;
import com.att.sales.nexxus.rateletter.model.RateLetterStatusResponse;
import com.att.sales.nexxus.util.JacksonUtil;

@Service("RateLetterStatusImpl")
public class RateLetterStatusImpl extends BaseServiceImpl implements RateLetterStatus{
	public static String IS_ERATE = "isErate";
	
	@Autowired
	private DmaapPublishEventsService dmaapPublishEventsService;
	
	@Autowired
	private NxMpDealRepository nxMpDealRepository; 
	
	@Autowired
	private GetTransactionLineServiceImpl getTransactionLineServiceImpl;

	@Autowired
	private GetTransactionServiceImpl getTransactionServiceImpl;
	
	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(RateLetterStatusImpl.class);
	
	
	@Autowired
	private NxSolutionDetailsRepository repository;
	
	@Autowired
	private NxMpPriceDetailsRepository nxMpPriceDetailsRepository;
	
	@Override
	public ServiceResponse rateLetterStatus(RateLetterStatusRequest request) throws SalesBusinessException {
		logger.info("Entering rateLetterStatus() method");

		RateLetterStatusResponse resp = null;
		try {
			logger.info("rateLetterStatus Request :==>> {}", JacksonUtil.toString(request));
			
			String cpqId = request.getCpqId();
			List<NxMpDeal> nxMpDealList = nxMpDealRepository.findAllByTransactionId(cpqId);
			if(Optional.ofNullable(request.getDealStatus()).isPresent() && (request.getDealStatus().equalsIgnoreCase(CommonConstants.APPROVED_PRICING) || request.getDealStatus().equalsIgnoreCase(CommonConstants.APPROVED))) {
				
				request.setDealStatus(CommonConstants.APPROVED);
				NxMpDeal nxMpDeal = null;
				// check for new entry in nx_mp_deal table
				boolean isNewEntry=false;
				if(CollectionUtils.isEmpty(nxMpDealList)) {
					//call new method, insert record in nx_solution_details and nx_mp_deal 
					nxMpDeal=this.createEntry(request);
					logger.info("New Entry created in nx_mp_deal entry {}",nxMpDeal);
					isNewEntry=true;
				}
				// if nx_mp_deal already exist update it the record
				if((StringUtils.isNotEmpty(request.getQuoteType()) && request.getQuoteType().equalsIgnoreCase("firm")) || !isNewEntry ) {
					if(CollectionUtils.isNotEmpty(nxMpDealList) && nxMpDealList.size() > 0) {
						for(NxMpDeal nxMpDealEntry : nxMpDealList) {
							nxMpDealEntry.setDealStatus(request.getDealStatus());
							nxMpDealEntry.setQuoteType(request.getQuoteType());
							nxMpDealEntry.setVersion(request.getDealVersion());
							nxMpDealEntry.setRevision(request.getDealRevisionNumber());
							nxMpDealEntry.setOptyId(request.getOptyId());
							nxMpDealEntry.setOffer(request.getOffer());
							nxMpDealEntry.setPricingManager(request.getPricingManager());
							nxMpDealEntry.setDealDescription(request.getDealDescription());
							nxMpDealEntry.setSvId(request.getSvId());
							nxMpDealEntry.setDealID(request.getDealId());
							nxMpDealRepository.save(nxMpDealEntry);
						
						}
					}
					resp = new RateLetterStatusResponse();
					Map<String, Boolean> isErateHoldingMap = new HashMap<>();
					CompletableFuture.runAsync(() -> {
						try {
							TimeUnit.SECONDS.sleep(30);
							GetTransactionResponse getTransactionResponse = (GetTransactionResponse) getTransactionServiceImpl.getTransaction(request.getCpqId());
							isErateHoldingMap.put(IS_ERATE, getTransactionResponse.isWiIsErate());
						} catch (Exception e) {
							logger.error("Error : While performing get transacion from rateletter status {}", e.getMessage());
						}
					}).thenAccept(action -> {
						try {
							TimeUnit.SECONDS.sleep(30);
							getTransactionLineServiceImpl.getTransactionLine(request.getCpqId(), isErateHoldingMap);
						} catch (Exception e) {
							logger.error("Error : While performing get transacion line from rateletter status {}", e.getMessage());
						}
					}); 
				}
			} else {
                if(Optional.ofNullable(request.getDealStatus()).isPresent() && (request.getDealStatus().equalsIgnoreCase(CommonConstants.APPROVED_WITHDRAWN))){
					
					if(CollectionUtils.isNotEmpty(nxMpDealList) && nxMpDealList.size() > 0) {
						for(NxMpDeal nxMpDealEntry : nxMpDealList) {
							nxMpDealEntry.setDealStatus(request.getDealStatus());
							nxMpDealRepository.save(nxMpDealEntry);
						}
					}
	
				}
				resp = new RateLetterStatusResponse();
				CompletableFuture.runAsync(() -> {
					triggerDmaapEvent(request, false);
				});
			}
			setSuccessResponse(resp);
		} catch (Exception e) {
			logger.error(" Error : While processing rateLetterStatus() method {}", e.getMessage());
			throw new SalesBusinessException();
			
		}
		logger.info("Exiting rateLetterStatus() method");
		
		return resp;
		
	}
	
	@Override
	public void triggerDmaapEvent(RateLetterStatusRequest request) {
		Map<String,Object> inputmap=new HashMap<>();
		inputmap.put(com.att.sales.nexxus.constant.CommonConstants.AUDIT_ID,Long.valueOf(request.getCpqId()));
		inputmap.put(com.att.sales.nexxus.constant.CommonConstants.AUDIT_TRANSACTION,
				com.att.sales.nexxus.constant.CommonConstants.AUDIT_TRANSACTION_CONSTANTS.RATE_LETTER_DMAAP.getValue());
		dmaapPublishEventsService.triggerDmaapEventForMyprice(request,inputmap);
	}
	
	public void triggerDmaapEvent(RateLetterStatusRequest request, boolean saveData) {
		Map<String,Object> inputmap=new HashMap<>();
		String ipeInd = null;
		String flowType = null;
		
		if(saveData) {
			inputmap.put(com.att.sales.nexxus.constant.CommonConstants.AUDIT_ID,Long.valueOf(request.getCpqId()));
			inputmap.put(com.att.sales.nexxus.constant.CommonConstants.AUDIT_TRANSACTION,com.att.sales.nexxus.constant.CommonConstants.AUDIT_TRANSACTION_CONSTANTS.RATE_LETTER_DMAAP.getValue());
			
		}
		List<String> ns= nxMpDealRepository.findFlowtypeByDealId(request.getDealId());
		if(!ns.isEmpty() && ns.get(0) != null) {
			flowType= ns.get(0);
		}
		
		if (StringConstants.SALES_IPNE.equalsIgnoreCase(flowType)) {
			ipeInd = StringConstants.CONSTANT_Y;
		}
		inputmap.put(TDDConstants.IPE_INDICATOR, ipeInd);
		
		dmaapPublishEventsService.triggerDmaapEventForMyprice(request,inputmap);
	}
	
	protected NxMpDeal createEntry(RateLetterStatusRequest request) {
		NxMpDeal oldNxMpDeal = nxMpDealRepository.getDealByDealId(request.getDealId());
		if(null == oldNxMpDeal) {
			oldNxMpDeal = nxMpDealRepository.getMpDealByDealId(request.getDealId());
		}
		Date date = new Date();
		NxSolutionDetail solnData = new NxSolutionDetail();
		if(null == oldNxMpDeal) {
			solnData.setCreatedDate(date);
			solnData.setModifiedDate(date);
			solnData.setActiveYn(com.att.sales.nexxus.common.CommonConstants.ACTIVE_Y);
			String flowType=StringConstants.FLOW_TYPE_MP_BUDGETARY;
			String description="[AUTOMATION:" + StringConstants.FLOW_TYPE_MP_BUDGETARY + "]";
			if(StringUtils.isNotEmpty(request.getQuoteType()) && request.getQuoteType().equalsIgnoreCase("firm")) {
				flowType=StringConstants.FLOW_TYPE_MP_FIRM;
				description="[AUTOMATION:" + StringConstants.FLOW_TYPE_MP_FIRM + "]";
			}
			solnData.setNxsDescription(description);
			solnData.setFlowType(flowType);
			solnData.setCustomerName(request.getCustomerName());
			repository.save(solnData);
		}
		logger.info("Service Log :: [Nexxus Info] :: createEntryNxMpDeal Invoked");
		NxMpDeal nxMpDeal = new NxMpDeal();
		nxMpDeal.setTransactionId(request.getCpqId());
		nxMpDeal.setDealID(request.getDealId());
		nxMpDeal.setRevision(request.getDealRevisionNumber());
		nxMpDeal.setVersion(request.getDealVersion());
		nxMpDeal.setDealStatus(request.getDealStatus());
		nxMpDeal.setCreatedDate(new Date());
		nxMpDeal.setModifiedDate(new Date());
		nxMpDeal.setActiveYN(com.att.sales.nexxus.common.CommonConstants.ACTIVE_Y);
		nxMpDeal.setQuoteType(request.getQuoteType());
		if(null != oldNxMpDeal && null != oldNxMpDeal.getPriceScenarioId()) {
			nxMpDeal.setPriceScenarioId(oldNxMpDeal.getPriceScenarioId());
			nxMpDeal.setAction(StringConstants.DEAL_ACTION_PD_CLONE);
			nxMpDeal.setSolutionId(oldNxMpDeal.getSolutionId());
			nxMpDeal.setOfferId(oldNxMpDeal.getOfferId());
		} else if(null != oldNxMpDeal) {
			nxMpDeal.setAction(StringConstants.MP_CREATE_ADDITIONAL_REQUEST);
			nxMpDeal.setSolutionId(oldNxMpDeal.getSolutionId());
			nxMpDeal.setOfferId(oldNxMpDeal.getOfferId());
		} else {
			nxMpDeal.setSolutionId(solnData.getNxSolutionId());
		}
		nxMpDeal.setNxMpStatusInd(StringConstants.CONSTANT_Y);
		request.setOptyId(nxMpDeal.getOptyId());
		request.setOffer(nxMpDeal.getOffer());
		request.setPricingManager(nxMpDeal.getPricingManager());
		request.setDealDescription(nxMpDeal.getDealDescription());
		request.setSvId(nxMpDeal.getSvId());
		nxMpDealRepository.save(nxMpDeal);
		updateDeal(oldNxMpDeal, nxMpDeal);
		logger.info("Service Log :: [Nexxus Info] :: createEntryNxMpDeal Destroyed");
		return nxMpDeal;
	}
	
	public void updateDeal(NxMpDeal nxMpDeal, NxMpDeal newNxMPDeal) {
		if(null!= nxMpDeal) {
			//save to nx_mp_price_details
			List<NxMpPriceDetails> nxMpPriceDetailList = nxMpPriceDetailsRepository.findByNxTxnId(nxMpDeal.getNxTxnId());
			if(null != nxMpPriceDetailList) {
				List<NxMpPriceDetails>  copyMpPriceDetailList = new ArrayList<NxMpPriceDetails>();
				for(NxMpPriceDetails nxMpPriceDetails : nxMpPriceDetailList) {
					NxMpPriceDetails newPriceDetails = new NxMpPriceDetails();
					BeanUtils.copyProperties(nxMpPriceDetails, newPriceDetails, "nxPriceDetailsId", "nxTxnId");
					newPriceDetails.setNxTxnId(newNxMPDeal.getNxTxnId());
					copyMpPriceDetailList.add(newPriceDetails);
				}
				if(!copyMpPriceDetailList.isEmpty()) {
					nxMpPriceDetailsRepository.saveAll(copyMpPriceDetailList);
				}
			}
		}
	}
	
}
