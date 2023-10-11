package com.att.sales.nexxus.myprice.transaction.service;

import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.common.CommonConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.constant.TDDConstants;
import com.att.sales.nexxus.dao.model.NxMpDeal;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.repository.NxMpDealRepository;

@Component
public class AutomationFlowHelperService {
	
	
	private static final Logger log = LoggerFactory.getLogger(AutomationFlowHelperService.class);
	@Autowired
	private UpdateTransactionPriceScore updateTransactionPriceScore;
	
	@Autowired
	private UpdateTransactionQualifyService updateTransactionQualifyService;
	
	@Autowired
	private UpdateTransactionSubmitToApproval updateTransactionSubmitToApproval;
	
	/*@Autowired
	private UpdateTransactionAssignAccesptService updateTransactionAssignAccesptService;
	
	@Autowired
	private UpdateTransactionApproveRateLetterServiceImpl updateTransactionApproveRateLetterService;
	
	@Autowired
	private UpdateTransactionPreviewWirelineServiceImpl updateTransactionPreviewWirelineService;*/
	
	@Autowired
	private UpdateTransactionRepriceServiceImpl updateTransactionRepriceService;
	
	@Autowired
	private UpdateTransactionPrintDocServiceImpl updateTransactionPrintDocService;
	
	@Autowired
	private GenerateRateLetter generateRateLetter;
	
	@Autowired
	private NxMpDealRepository nxMpDealRepository;
	
	@Autowired
	private GetTransactionLineServiceImpl getTransactionLineService;
	
	
	
	public void process(LinkedHashMap<String, Object> requestMap) {
		  
	  NxSolutionDetail solutionData=(NxSolutionDetail)
	  requestMap.get(TDDConstants.SOLUTION_DATA); 
	  if(null!=solutionData) {
        log.info("Inside automation flow {}", solutionData.getNxSolutionId());
		  List<NxMpDeal>  nxMpDeals=nxMpDealRepository.findBySolutionIdAndActiveYN(solutionData.getNxSolutionId(), CommonConstants.ACTIVE_Y); 
		  if(CollectionUtils.isNotEmpty(nxMpDeals)) { 
			  for(NxMpDeal  nxMpDeal:nxMpDeals) { 
				  if(StringUtils.isEmpty(nxMpDeal.getDealStatus()) ||
						  !nxMpDeal.getDealStatus().equalsIgnoreCase(MyPriceConstants. DEAL_STATUS_APPROVED)) { 
					  String transactionId=nxMpDeal.getTransactionId();
					  if(StringUtils.isNotEmpty(transactionId)) {
						  requestMap.put(StringConstants.MY_PRICE_TRANS_ID, transactionId);
							  try {
									int retry = 0;
									boolean priceScoreSuccess = false;
									do {
										retry++;
										try {
											updateTransactionQualifyService.updateTransactionQualifyService(requestMap);
										} catch (SalesBusinessException e) {
											process(20000);
											log.info("Inside automation retry call for qualify {}",
													solutionData.getNxSolutionId());
											continue;
										}
										process(180000);
										try {
											updateTransactionRepriceService.updateTransactionRepriceService(requestMap);
										} catch (SalesBusinessException e) {
											process(20000);
											log.info("Inside automation retry call for reprice {}",
													solutionData.getNxSolutionId());
											updateTransactionRepriceService.updateTransactionRepriceService(requestMap);
										}
										process(300000);
										try {
											updateTransactionPriceScore.updateTransactionPriceScore(requestMap);
											priceScoreSuccess = true;
											break;
										} catch (SalesBusinessException e) {
											log.info("priceScore call get Failed");
											process(1200000);
											log.info("Inside automation retry call for failed {}",
													solutionData.getNxSolutionId());
											continue;
										}

									} while (retry < 3);
									if (!priceScoreSuccess) {
										throw new SalesBusinessException();
									}
								  /*
								  try {
									  updateTransactionQualifyService.updateTransactionQualifyService(requestMap);
								  }catch(SalesBusinessException e) {
									  process(20000);
						              log.info("Inside automation recursive call for qualify {}", solutionData.getNxSolutionId());
									  this.process(requestMap);											  
								  }
								  try {
									  updateTransactionRepriceService.updateTransactionRepriceService(requestMap);
								  }catch(SalesBusinessException e) {
									  process(20000);
						              log.info("Inside automation recursive call for reprice {}", solutionData.getNxSolutionId());
						              updateTransactionRepriceService.updateTransactionRepriceService(requestMap);										  
								  }
								  process(300000);
								  try {
									  updateTransactionPriceScore.updateTransactionPriceScore(requestMap);
								  }catch(SalesBusinessException e) {
									  log.info("priceScore call get Failed");
									  if(!requestMap.containsKey(StringConstants.PRICE_SCORE_STATUS)) {
										  process(1200000);
							              log.info("Inside automation recursive call for failed {}", solutionData.getNxSolutionId());
										  requestMap.put(StringConstants.PRICE_SCORE_STATUS,true);
										  this.process(requestMap);											  
									  }
									  throw e;
								  }
								  */
								  boolean submitToApproval = updateTransactionSubmitToApproval.updateTransactionSubmitToApproval(requestMap);
								  if(!submitToApproval) {
									  process(20000);
									  submitToApproval = updateTransactionSubmitToApproval.updateTransactionSubmitToApproval(requestMap);
								  }
								  
								  if(submitToApproval) {
									  try {
										  updateTransactionPrintDocService.updateTransactionPrintDocService(requestMap);
									  }catch(SalesBusinessException e) {
										  process(10000);
										  log.info("Inside automation recursive call for failed {}", solutionData.getNxSolutionId());
										  updateTransactionPrintDocService.updateTransactionPrintDocService(requestMap);
									  }
									  generateRateLetter.generateRateLetter(requestMap);
								  } else {
									  getTransactionLineService.publishDmaapForAutomationFlow(nxMpDeal, MyPriceConstants.AUTOMATION_DMAAP_REJECTED);
									  break;
								  }
							  } catch  (Exception e) { 
								  getTransactionLineService.publishDmaapForAutomationFlow(nxMpDeal, MyPriceConstants.AUTOMATION_DMAAP_FAILED);
								  log.error("Exception during calling automation API: {}", e);
								  break;
							  }
						  } 
					 }	  
			  } 
		  }
	  }
}
	
	public static void process(int time) {
		try {
			log.info("Before QualifyService Thread sleep for 5 min: {}");
			Thread.sleep(time);	
		}catch(Exception e){	
			log.error("After QualifyService Thread sleep for 5 min: {}", e);
		}
		  
	  }

}
