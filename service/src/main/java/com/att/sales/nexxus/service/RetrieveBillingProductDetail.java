package com.att.sales.nexxus.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.att.aft.dme2.internal.apache.commons.lang3.StringUtils;
import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.framework.service.BaseServiceImpl;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.dao.model.NxDesignAudit;
import com.att.sales.nexxus.dao.model.NxRequestDetails;
import com.att.sales.nexxus.dao.repository.NxDesignAuditRepository;
import com.att.sales.nexxus.dao.repository.NxDwInventoryDao;
import com.att.sales.nexxus.dao.repository.NxRequestDetailsRepository;
import com.att.sales.nexxus.dao.repository.UsrpDao;
import com.att.sales.nexxus.inr.InvPriceJsonRequest;

@Service
public class RetrieveBillingProductDetail extends BaseServiceImpl {
	private static Logger logger = LoggerFactory.getLogger(RetrieveBillingProductDetail.class);
	private static String RETRIEVE_BILLING_PRODUCT_DETAIL = "RetrieveBillingProductDetail";

	@Autowired
	private NxDwInventoryDao nxDwInventoryDao;
	
	@Autowired
	private NxDesignAuditRepository nxDesignAuditRepository;

	@Autowired
	private UsrpDao usrpDao;

	@Autowired
	private NxRequestDetailsRepository nxRequestDetailsRepository;

	public ServiceResponse getProductdetails(InvPriceJsonRequest request) throws SalesBusinessException {
		logger.info("enter getProductdetails method for nxrequest id {}", request.getNxReqId());
		ServiceResponse response = new ServiceResponse();
		try {
			Long currentTime = System.currentTimeMillis();
			Long startTime = System.currentTimeMillis() - currentTime;

			NxRequestDetails nxRequestDetails = nxRequestDetailsRepository.findByNxReqId(request.getNxReqId());

			if ("AVPN".equalsIgnoreCase(nxRequestDetails.getProduct())) {
				getAVPNDetail(nxRequestDetails);
			}
			if ("MIS/PNT".equalsIgnoreCase(nxRequestDetails.getProduct())) {
				getADIDetail(nxRequestDetails);
			}
			printTotalDuration(currentTime, startTime, RETRIEVE_BILLING_PRODUCT_DETAIL,request.getNxReqId());
		} catch (Exception e) {
			logger.info("Exception:", e);
			throw new SalesBusinessException();
		}
		return response;
	}

	protected void getAVPNDetail(NxRequestDetails nxRequestDetails) {
		String searchCriteria = nxRequestDetails.getManageBillingPriceJson();
		List<Object[]> avpnPortNumberDetails = nxDwInventoryDao
				.findPortNumberDetailsBySearchCriteriaAndProduct(searchCriteria, nxRequestDetails.getProduct());
		if (CollectionUtils.isNotEmpty(avpnPortNumberDetails)) {
			List<String> avpnPortNumber = new ArrayList<>();
			Map<String, Map<String, Object>> portNumberLookup = new HashMap<>();
			avpnPortNumberDetails.forEach(object -> {
				avpnPortNumber.add(String.valueOf(object[0]));
				Map<String, Object> row = new HashMap<>();
				row.put("PARENT_ACCT_ID", object[1]);
				row.put("ACCT_ID", object[2]);
				row.put("L3", object[3]);
				portNumberLookup.put(String.valueOf(object[0]), row);
			});

			List<Map<String, Object>> r = usrpDao.queryAvpnCircuitByPortNumber(avpnPortNumber);
			Set<String> usrpAvpnPortAccessTrimCircuitSet = new HashSet<>();
			Map<String, Map<String, Object>> usrpcircuitLookup = new HashMap<>();
			if (CollectionUtils.isNotEmpty(r)) {
				r.forEach(row -> {
					if (stringHasValue(String.valueOf(row.get("circuitid")))) {
						String circuitId = trimSpaceAndDot(String.valueOf(row.get("circuitid")));
						usrpAvpnPortAccessTrimCircuitSet.add(circuitId);
						String portNumber=String.valueOf(row.get("icore_site_id"));
						Map<String,Object> portNumberMap=portNumberLookup.get(portNumber); 
						if(portNumberMap!=null) {
							row.put("PARENT_ACCT_ID", portNumberMap.get("PARENT_ACCT_ID"));
							row.put("ACCT_ID", portNumberMap.get("ACCT_ID"));
							row.put("L3", portNumberMap.get("L3"));
						}
						usrpcircuitLookup.put(circuitId, row);
					}
					if (stringHasValue(String.valueOf(row.get("ww_circuitid")))) {
						String accessCircuitId = trimSpaceAndDot(String.valueOf(row.get("ww_circuitid")));
						usrpAvpnPortAccessTrimCircuitSet.add(accessCircuitId);
					}
				});
			}
			List<String> usrpAvpnTrimCircuit =  usrpAvpnPortAccessTrimCircuitSet.stream().collect(Collectors.toList());
			//27102022- avpnadi pricejson issue with mcn search criteria
			//starts here
			String usrpAvpnTrimCircuits =usrpAvpnTrimCircuit.stream().map(Object::toString).collect(Collectors.joining(","));
			NxDesignAudit nxDesignAudit = nxDesignAuditRepository.findByNxRefIdAndTransaction(nxRequestDetails.getNxReqId(), MyPriceConstants.USRP_CIRCUITID_LIST);
			if (nxDesignAudit != null) {
				nxDesignAudit.setData(usrpAvpnTrimCircuits);
				nxDesignAudit.setModifedDate(new Date());
			} else {
				nxDesignAudit = new NxDesignAudit();
				nxDesignAudit.setData(usrpAvpnTrimCircuits);
				nxDesignAudit.setTransaction(MyPriceConstants.USRP_CIRCUITID_LIST);
				nxDesignAudit.setNxRefId(nxRequestDetails.getNxReqId());
				nxDesignAudit.setCreatedDate(new Date());
			}
			nxDesignAuditRepository.saveAndFlush(nxDesignAudit);
			//ends here
			
			if (CollectionUtils.isNotEmpty(usrpAvpnTrimCircuit)) {
				List<Object[]> result = nxDwInventoryDao.findCircuitToAvpnAccessBySearchCriteriaAndProduct(
						searchCriteria, nxRequestDetails.getProduct(), usrpAvpnTrimCircuit);
				if (CollectionUtils.isNotEmpty(result)) {
					List<Long> nxdwId = result.stream().map(objects -> Long.parseLong(objects[2].toString()))
							.collect(Collectors.toList());
					if (CollectionUtils.isNotEmpty(nxdwId)) {
						nxDwInventoryDao.updateBasedOnId(nxdwId, "AVPN", "A");
					}
				}
				// update the dcs file circuit acctnumber,subacctnumber , l3 with that of ub
				// file
				
				//nxDwInventoryDao.updateDcsFileDetailBasedOncircuitId(usrpcircuitLookup,searchCriteria, nxRequestDetails.getProduct());
			}
		}
	}

	protected void getADIDetail(NxRequestDetails nxRequestDetails) {
		String searchCriteria = nxRequestDetails.getManageBillingPriceJson();
		List<String> adiCircuitToAccess = nxDwInventoryDao.findADIPortCircuitToAccess(searchCriteria);
			
		if (CollectionUtils.isNotEmpty(adiCircuitToAccess)) {
			List<Object[]> result=nxDwInventoryDao.findCircuitToAdiAccessBySearchCriteriaAndProduct(searchCriteria,nxRequestDetails.getProduct(),adiCircuitToAccess);
					 if(CollectionUtils.isNotEmpty(result)) {
							List<Long> nxdwId= result.stream().map(objects ->Long.parseLong(objects[2].toString())).collect(Collectors.toList());
							if(CollectionUtils.isNotEmpty(nxdwId)) {
								nxDwInventoryDao.updateBasedOnId(nxdwId,"ADI","A");
							}
						 }
		}
		//02112022- avpnadi pricejson issue with mcn search criteria
		//starts here
		String usrpAvpnTrimCircuits =adiCircuitToAccess.stream().map(Object::toString).collect(Collectors.joining(","));
		NxDesignAudit nxDesignAudit = nxDesignAuditRepository.findByNxRefIdAndTransaction(nxRequestDetails.getNxReqId(), MyPriceConstants.USRP_CIRCUITID_LIST);
		if (nxDesignAudit != null) {
			nxDesignAudit.setData(usrpAvpnTrimCircuits);
			nxDesignAudit.setModifedDate(new Date());
		} else {
			nxDesignAudit = new NxDesignAudit();
			nxDesignAudit.setData(usrpAvpnTrimCircuits);
			nxDesignAudit.setTransaction(MyPriceConstants.USRP_CIRCUITID_LIST);
			nxDesignAudit.setNxRefId(nxRequestDetails.getNxReqId());
			nxDesignAudit.setCreatedDate(new Date());
		}
		nxDesignAuditRepository.saveAndFlush(nxDesignAudit);
		//ends here

		
	}

	protected String trimSpaceAndDot(String in) {
		if (in == null) {
			return null;
		}
		return in.replaceAll("\\s", "").replaceAll("\\.", "");
	}
	
	protected void printTotalDuration(Long currentTime, Long startTime, String operation, Long nxRequestId) {
		Long endTime = System.currentTimeMillis() - currentTime;
		String totalDuration = new StringBuilder().append(operation + " for request id :: ").append(nxRequestId)
				.append(" took :: ").append((endTime - startTime)).append(" ").append(MyPriceConstants.MILLISEC)
				.toString();
		logger.info(totalDuration);
	}
	protected boolean stringHasValue(String in) {
		if (in == null) {
			return false;
		}
		if (in.isEmpty()) {
			return false;
		}
		if ("null".equals(in)) {
			return false;
		}
		return true;
	}

}
