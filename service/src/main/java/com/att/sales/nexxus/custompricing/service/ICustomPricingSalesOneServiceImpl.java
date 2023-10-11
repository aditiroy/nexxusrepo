package com.att.sales.nexxus.custompricing.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.framework.service.BaseServiceImpl;
import com.att.sales.nexxus.custompricing.model.CustomPricingRequest;
import com.att.sales.nexxus.custompricing.model.CustomerDetail;
import com.att.sales.nexxus.custompricing.model.DealSummary;
import com.att.sales.nexxus.custompricing.model.GetCustomPricingResponse;
import com.att.sales.nexxus.custompricing.model.ProductLevelSummary;
import com.att.sales.nexxus.dao.model.NxMpDeal;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.repository.NxMpDealRepository;
import com.att.sales.nexxus.dao.repository.NxSolutionDetailsRepository;
import com.att.sales.nexxus.myprice.transaction.model.GetTransactionLineItem;
import com.att.sales.nexxus.myprice.transaction.model.GetTransactionLineResponse;
import com.att.sales.nexxus.myprice.transaction.model.GetTransactionResponse;
import com.att.sales.nexxus.myprice.transaction.service.GetTransactionLineServiceImpl;
import com.att.sales.nexxus.myprice.transaction.service.GetTransactionServiceImpl;
import com.att.sales.nexxus.rome.model.GetOptyRequest;
import com.att.sales.nexxus.rome.model.GetOptyResponse;
import com.att.sales.nexxus.rome.service.GetOptyInfoServiceImpl;
import com.att.sales.nexxus.util.LogUtils;

@Service("ICustomPricingSalesOneServiceImpl")
public class ICustomPricingSalesOneServiceImpl extends BaseServiceImpl implements ICustomPricingSalesOneService {

	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(ICustomPricingSalesOneServiceImpl.class);

	@Autowired
	private NxMpDealRepository nxMpDealRepository;

	@Autowired
	private NxSolutionDetailsRepository repository;
	
	@Autowired
	private GetTransactionLineServiceImpl getTransactionLineServiceImpl;

	@Autowired
	private GetTransactionServiceImpl getTransactionServiceImpl;
	
	@Autowired
	private GetOptyInfoServiceImpl getOptyInfoServiceImpl;
	
	
	@Override
	public ServiceResponse getCustomPricingSalesOne(CustomPricingRequest request) {
		
		
		GetCustomPricingResponse getCPSOresponse = new GetCustomPricingResponse();

		List<NxMpDeal> nxMpDeals;
		nxMpDeals = nxMpDealRepository.findBydealIDVersnIdRevId(request.getSolution().getDealId(),request.getSolution().getVersionNumber(),request.getSolution().getRevisionNumber());
		
	
		GetOptyResponse getOptyDetails = null;
		GetTransactionResponse getTxnResp = null;
		GetTransactionLineResponse getTxnLineResp = null;
		
		if(CollectionUtils.isNotEmpty(nxMpDeals)) {
			
			Long solutionId = nxMpDeals.get(0).getSolutionId();
			NxSolutionDetail solution = repository.findByNxSolutionId(solutionId);
			
				GetOptyRequest getOptyRequest = new GetOptyRequest();
				
				if(request.getUserId()!=null ) {
					//getOptyRequest.setHrId(request.getUserId());

					getOptyRequest.setAttuid(request.getUserId());
					
					logger.info("ATTUID ID {} "  ,request.getUserId());
				}
				else {
					getOptyRequest.setAttuid(solution.getCreatedUser());
				}
						
				getOptyRequest.setOptyId(solution.getOptyId());
				getOptyRequest.setAction("myPriceFlow");
				
			String transactionId = nxMpDeals.get(0).getTransactionId();
			logger.info("Querying transaction Id to retrieve data from myPrice {}" ,transactionId);
			
			try {
				
				getOptyDetails = (GetOptyResponse) getOptyInfoServiceImpl.performGetOptyInfo(getOptyRequest);
				logger.info("Opty Response " +getOptyDetails.toString());
				
			} catch (SalesBusinessException e) {
				// TODO Auto-generated catch block
				logger.error("Error while performing Opty call in getCustomPricingSalesOne() " +e.getMessage());
			}
			
			try {
				
				 getTxnResp = getTransactionServiceImpl.getTransactionSalesOne(transactionId);
				 logger.info("Printing get transaction response " +getTxnResp);
				
				
			} catch (SalesBusinessException e) {
				// TODO Auto-generated catch block
				logger.error("Error while performing getTransaction API call in getCustomPricingSalesOne() " +e.getMessage());
			}
			try {
				getTxnLineResp = getTransactionLineServiceImpl.getTransactionLineSalesOne(transactionId);
								
			} catch (SalesBusinessException e) {
				// TODO Auto-generated catch block
				logger.error("Error while performing getTransactionLine API call in getCustomPricingSalesOne() " +e.getMessage());
			}
			
			
		}		
		
		/*Creation of customer details block*/
			List<CustomerDetail> customerDetailList = new ArrayList<CustomerDetail>();
			
								
			customerDetailList.add(createCustomerDetailBLock(getOptyDetails, getTxnResp, getTxnLineResp));
			
			getCPSOresponse.setCustomerDetail(customerDetailList);
		
			
		/*Data for Product LevelSummary Deal Sugetmmary details block*/
		
		List<DealSummary> dealSummaryList = new ArrayList<DealSummary>();
		List<ProductLevelSummary> productLevelSummaryList = new ArrayList<ProductLevelSummary>();
		
		List<GetTransactionLineItem> getTransactionLineItemList = getTxnLineResp.getItems();
		HashMap<String, List<Float>> hashMap = new HashMap<String, List<Float>>();
		Float dealPriceMRC = 0f;
		Float dealPriceNRC = 0f;
			
		getTransactionLineItemList.forEach((getTransactionLineItem) -> {
			
			//if(null != getTransactionLineItem.getBomId() && (getTransactionLineItem.getBomId().equalsIgnoreCase("BOM_ASE") || getTransactionLineItem.getBomId().equalsIgnoreCase("BOM_ADE"))) {
			
			if(null != getTransactionLineItem.getIsProductRow() && (getTransactionLineItem.getIsProductRow().equalsIgnoreCase("true"))){
				
			String getExtendedPriceMRC = "0.00";
			String getExtendedPriceNRC = "0.00";
			
			if(getTransactionLineItem.getExtendedPriceMRC() != null) {
				getExtendedPriceMRC = getTransactionLineItem.getExtendedPriceMRC().replace(",", "");
			}
			if(getTransactionLineItem.getExtendedPriceNRC() != null) {
				getExtendedPriceNRC = getTransactionLineItem.getExtendedPriceNRC().replace(",", "");
			}
			
			Float extPriceMRC = Float.valueOf(getExtendedPriceMRC);
			Float extPriceNRC = Float.valueOf(getExtendedPriceNRC);
					
			
			List<Float> existingValue = (List<Float>) hashMap.get(getTransactionLineItem.getRlProduct());
			
			if(existingValue == null) {
				existingValue = new ArrayList<Float>();
				existingValue.add(0, 0f);
				existingValue.add(1, 0f);
			}
				
			Float newPriceMrc = extPriceMRC + existingValue.get(0);
			Float newPriceNrc = extPriceNRC + existingValue.get(1);
			
			existingValue.set(0, newPriceMrc);
			existingValue.set(1, newPriceNrc);
						
			hashMap.put(getTransactionLineItem.getRlProduct(), existingValue);
		}	
		});
		
		
		for (Map.Entry<String, List<Float>> entry : hashMap.entrySet()) {
			System.out.println(entry.getKey() + ":" + entry.getValue());
			
			List<Float> priceValueList = entry.getValue();
			dealPriceMRC = dealPriceMRC + priceValueList.get(0);
			dealPriceNRC = dealPriceNRC + priceValueList.get(1);
			ProductLevelSummary productLevelSummary = new ProductLevelSummary();	
			
			//String datasetName = "SALESONEPRODUCT";
			//productLevelSummary.setProduct(getDataFromNxLookUp(entry.getKey(), datasetName));	
			productLevelSummary.setProduct(entry.getKey());
			
			productLevelSummary.setTotalMrc(priceValueList.get(0).toString());
			productLevelSummary.setTotalNrc(priceValueList.get(1).toString());
			productLevelSummary.setCurrency("USD");
			productLevelSummaryList.add(productLevelSummary);
			getCPSOresponse.setProductLevelSummary(productLevelSummaryList);
		}
		
		DealSummary dealSummary = new DealSummary();
		dealSummary.setDescription("Deal Total");
		dealSummary.setDealId(request.getSolution().getDealId());
		dealSummary.setVersionNumber(request.getSolution().getVersionNumber());
		dealSummary.setRevisionNumber(request.getSolution().getRevisionNumber());
		dealSummary.setTotalMrc(dealPriceMRC.toString());
		dealSummary.setTotalNrc(dealPriceNRC.toString());
		dealSummary.setCurrency("USD");
		dealSummaryList.add(dealSummary);
		getCPSOresponse.setDealSummary(dealSummaryList);
		
		setSuccessResponse(getCPSOresponse);
		
		return getCPSOresponse;

	}

	/* ***Method for creating Customer Detail block***** */
	private CustomerDetail createCustomerDetailBLock(GetOptyResponse getOptyDetails, GetTransactionResponse getTxnResp, GetTransactionLineResponse getTxnLineResp) {
		
		CustomerDetail customerDetail = new CustomerDetail();
		
		createCustomerAddressDetail(getOptyDetails, getTxnResp, customerDetail);		
		
		if(getTxnResp.getCustomerFirstName()!=null) {			
			customerDetail.setFirstName(getTxnResp.getCustomerFirstName());}
		
		if(getTxnResp.getCustomerLastName()!=null) {		
		customerDetail.setLastName(getTxnResp.getCustomerLastName());}
		
		if(getTxnResp.getCustomerCompanyName()!=null ) {
			customerDetail.setCompanyName(getTxnResp.getCustomerCompanyName());				}
			else {
				customerDetail.setCompanyName(getOptyDetails.getCustomerName());}

		if(getTxnResp.getCustomerPhone() !=null) {			
			customerDetail.setTelephone(getTxnResp.getCustomerPhone().toString());}
			else {
			customerDetail.setTelephone(getOptyDetails.getMainPhone());}
		
		return customerDetail;
			
	}
	
	private void createCustomerAddressDetail(GetOptyResponse getOptyDetails, GetTransactionResponse getTxnResp, CustomerDetail customerDetail) {
		
		if(getTxnResp.getCustomerAddres()!=null) {  
			customerDetail.setAddress(getTxnResp.getCustomerAddres());	
		}
		
		else if(getOptyDetails.getAddress1()!=null && getOptyDetails.getCity() != null && getOptyDetails.getState() !=null && getOptyDetails.getCountry() !=null && getOptyDetails.getPostalCode()!=null) {
				customerDetail.setAddr1(getOptyDetails.getAddress1());
				customerDetail.setAddr2(getOptyDetails.getAddress2());			
				customerDetail.setCity(getOptyDetails.getCity());
				customerDetail.setState(getOptyDetails.getState());
				customerDetail.setCountry(getOptyDetails.getCountry());
				customerDetail.setPostalCode(getOptyDetails.getPostalCode());
			}
		
		else if(getTxnResp.getCustomerCity()!=null && getTxnResp.getCustomerState() != null && getTxnResp.getCustomerCountry() !=null && getTxnResp.getCustomerPostalCode() !=null) {
				customerDetail.setAddr1(getOptyDetails.getAddress1());
				customerDetail.setAddr2(getOptyDetails.getAddress2());			
				customerDetail.setCity(getTxnResp.getCustomerCity());
				if(null != getTxnResp.getCustomerState() && null != getTxnResp.getCustomerState().getValue()) {
					customerDetail.setState(getTxnResp.getCustomerState().getDisplayValue());
				}
				if(null != getTxnResp.getCustomerCountry() && null != getTxnResp.getCustomerCountry().getValue()) {
					customerDetail.setCountry(getTxnResp.getCustomerCountry().getValue());
				}
				customerDetail.setPostalCode(getTxnResp.getCustomerPostalCode());
			}
		
		else {
				customerDetail.setAddr1(getOptyDetails.getAddress1());
				customerDetail.setAddr2(getOptyDetails.getAddress1());			
				
				if(getTxnResp.getCustomerCity() !=null) {
					customerDetail.setCity(getTxnResp.getCustomerCity());}
					else {
						customerDetail.setCity(getOptyDetails.getCity());}
				
				if(getTxnResp.getCustomerState() !=null) {	
						customerDetail.setState(getTxnResp.getCustomerState().toString());}
					else {
						customerDetail.setState(getOptyDetails.getState());}
				
				if(getTxnResp.getCustomerCountry() !=null) {			
					customerDetail.setCountry(getTxnResp.getCustomerCountry().toString());}
					else {
						customerDetail.setCountry(getOptyDetails.getCountry());}
				
				if(getTxnResp.getCustomerPostalCode() !=null) {			
					customerDetail.setPostalCode(getTxnResp.getCustomerPostalCode().toString());}
					else {
						customerDetail.setPostalCode(getOptyDetails.getPostalCode());}
			}
	}
	
	
/*	protected String getDataFromNxLookUp(String input, String looupDataSet) {
 		if(StringUtils.isNotEmpty(looupDataSet)) {
 			NxLookupData nxLookup=nxLookupDataRepository.findTopByDatasetNameAndItemId(looupDataSet, input);
 			if(null!=nxLookup) {
 				input=nxLookup.getDescription();
 			}
 		}
 		return input;
 	}*/
	
}
