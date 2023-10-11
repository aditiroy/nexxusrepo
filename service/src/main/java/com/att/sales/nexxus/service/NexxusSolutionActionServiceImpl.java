package com.att.sales.nexxus.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.framework.service.BaseServiceImpl;
import com.att.sales.nexxus.common.StringConstants;
import com.att.sales.nexxus.constant.AuditTrailConstants;
import com.att.sales.nexxus.constant.FmoConstants;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.model.NxLookupDataModel;
import com.att.sales.nexxus.dao.model.solution.NxUiAudit;
import com.att.sales.nexxus.dao.repository.NxFeatureRepository;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.dao.repository.NxSolutionDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxUiAuditRepository;
import com.att.sales.nexxus.dao.repository.NxUserFeatureMappingRepository;
import com.att.sales.nexxus.model.AuditDetails;
import com.att.sales.nexxus.model.NexxusSolActionRequest;
import com.att.sales.nexxus.model.NexxusSolActionResponse;
import com.att.sales.nexxus.util.AuditUtil;

/**
 * @author sx623g
 *
 */
@Service
@Transactional
public class NexxusSolutionActionServiceImpl extends BaseServiceImpl implements NexxusSolutionActionService {
	
	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(NexxusSolutionActionServiceImpl.class);
	@Autowired
	private NxSolutionDetailsRepository nxSolutionDetailsRepository;
	
	@Autowired
	private NxUiAuditRepository nxUiAuditRepository;
	
	@Autowired
	private AuditUtil auditUtil;
	
	@Autowired
	private NxLookupDataRepository nxLookupDataRepository;
	
	@Autowired
	private NxMyPriceRepositoryServce nxMyPriceRepositoryServce;
	
	@Autowired
	private NxUserFeatureMappingRepository nxUserFeatureMappingRepository;
	
	@Autowired
	private NxFeatureRepository nxFeatureRepository;
	
	@Override
	public ServiceResponse nexxusSolutionAction(NexxusSolActionRequest request) {
		Long currentTime = System.currentTimeMillis();
        Long startTime = System.currentTimeMillis() - currentTime;
		NexxusSolActionResponse response = new NexxusSolActionResponse();
		if(Optional.ofNullable(request.getSolutionId()).isPresent() && Optional.ofNullable(request.getAction()).isPresent()) {
			
			if(request.getAction().equalsIgnoreCase(StringConstants.ARCHIVESOLUTION)) {
				try {
					nxSolutionDetailsRepository.updateArchivedSolutionId("Y", request.getSolutionId());
					Long endTime = System.currentTimeMillis() - currentTime;
		            Long executionTime = endTime-startTime;
					//for capturing audit trail	
					auditUtil.addActionToNxUiAudit(request.getSolutionId(),AuditTrailConstants.ARCHIVE_SOLUTION,request.getActionPerformedBy(),AuditTrailConstants.SUCCESS,null,null,executionTime,null);
				}catch(Exception e) {
					Long endTime = System.currentTimeMillis() - currentTime;
		            Long executionTime = endTime-startTime;
					auditUtil.addActionToNxUiAudit(request.getSolutionId(),AuditTrailConstants.ARCHIVE_SOLUTION,request.getActionPerformedBy(),AuditTrailConstants.FAIL,null,null,executionTime,null);
				}
			} 
			else if(request.getAction().equalsIgnoreCase(StringConstants.UNARCHIVESOLUTION)){
				try {
					nxSolutionDetailsRepository.updateArchivedSolutionId("N", request.getSolutionId());
					Long endTime = System.currentTimeMillis() - currentTime;
		            Long executionTime = endTime-startTime;
					//for capturing audit trail	
					auditUtil.addActionToNxUiAudit(request.getSolutionId(),AuditTrailConstants.UNARCHIVE_SOLUTION,request.getActionPerformedBy(),AuditTrailConstants.SUCCESS,null,null,executionTime,null);
				}catch(Exception e) {
					Long endTime = System.currentTimeMillis() - currentTime;
		            Long executionTime = endTime-startTime;
					auditUtil.addActionToNxUiAudit(request.getSolutionId(),AuditTrailConstants.UNARCHIVE_SOLUTION,request.getActionPerformedBy(),AuditTrailConstants.FAIL,null,null,executionTime,null);
				}
			}
			else if(request.getAction().equalsIgnoreCase(StringConstants.GETAUDITTRAIL)){
				List<NxUiAudit> nxUiAuditList= nxUiAuditRepository.findByNxSolutionIdandActionType(request.getSolutionId(), AuditTrailConstants.ACTION_TYPE);
				if(CollectionUtils.isNotEmpty(nxUiAuditList)) {
				
					List<AuditDetails> auditDetailsList=new ArrayList<>();
					for(NxUiAudit nxUiAudit:nxUiAuditList) {
						String message=String.format(StringConstants.AUDIT_MESSAGE,nxUiAudit.getMessage(),nxUiAudit.getAttId());
						AuditDetails audit= new AuditDetails(message,nxUiAudit.getStatus(), nxUiAudit.getCreatedDate().getTime());
						auditDetailsList.add(audit);
					}
					response.setAudit(auditDetailsList);
				}
			}
			
			//add audit object in NexxusSolActionResponse
			response.setNxSolutionId(request.getSolutionId());
		}
		else if (Optional.ofNullable(request.getAction()).isPresent()
				&& request.getAction().equalsIgnoreCase(StringConstants.GET_PRODUCT_LIST)
				&& Optional.ofNullable(request.getActionPerformedBy()).isPresent()) {
			Map<String,NxLookupData> lookupDataMap = nxMyPriceRepositoryServce.getLookupDataByItemId(FmoConstants.UI_PRODUCT_LIST);
			Map<String,NxLookupDataModel> lookupDataList = new HashMap<>();
			Map<String,NxLookupData> resultMap=nxMyPriceRepositoryServce.getLookupDataByItemId(FmoConstants.USRP_PRODUCT_HRID_COND);
			List<String> featureMapping = nxUserFeatureMappingRepository.findByNxUserUserAttIdAndEnabled(request.getActionPerformedBy().toLowerCase());
			NxLookupData ciProduct=resultMap.containsKey("CI")?resultMap.get("CI"):null;
			NxLookupData gaProduct=resultMap.containsKey("GA")?resultMap.get("GA"):null;
			List<String> gaProductList = new ArrayList<>();
			List<String> ciProductList = new ArrayList<>();

			if (gaProduct != null) {
				gaProductList = Arrays.stream(gaProduct.getDescription().split(",")).collect(Collectors.toList());
			}

			if (ciProduct != null) {
				ciProductList = Arrays.stream(ciProduct.getDescription().split(",")).collect(Collectors.toList());
			}
			NxLookupData lookupData;
			NxLookupDataModel nxLookupDataModel;
			for(String keyVal : lookupDataMap.keySet()) {
				if (CollectionUtils.isNotEmpty(gaProductList) && gaProductList.contains(keyVal)) {
					lookupData=lookupDataMap.get(keyVal);
					nxLookupDataModel=new NxLookupDataModel();
					nxLookupDataModel.setActive(lookupData.getActive());
					nxLookupDataModel.setCriteria(lookupData.getCriteria());
					nxLookupDataModel.setDatasetName(lookupData.getDatasetName());
					nxLookupDataModel.setDescription(lookupData.getDescription());
					nxLookupDataModel.setItemId(lookupData.getItemId());
					lookupDataList.put(keyVal, nxLookupDataModel);
				}
				if(CollectionUtils.isNotEmpty(ciProductList) && ciProductList.contains(keyVal)) {
					if(featureMapping.contains(keyVal)) {
						lookupData=lookupDataMap.get(keyVal);
						nxLookupDataModel=new NxLookupDataModel();
						nxLookupDataModel.setActive(lookupData.getActive());
						nxLookupDataModel.setCriteria(lookupData.getCriteria());
						nxLookupDataModel.setDatasetName(lookupData.getDatasetName());
						nxLookupDataModel.setDescription(lookupData.getDescription());
						nxLookupDataModel.setItemId(lookupData.getItemId());
						lookupDataList.put(keyVal, nxLookupDataModel);
					}else {
						lookupData=lookupDataMap.get(keyVal);
						nxLookupDataModel=new NxLookupDataModel();
						nxLookupDataModel.setActive("N");
						nxLookupDataModel.setCriteria(lookupData.getCriteria());
						nxLookupDataModel.setDatasetName(lookupData.getDatasetName());
						nxLookupDataModel.setDescription(lookupData.getDescription());
						nxLookupDataModel.setItemId(lookupData.getItemId());
						lookupDataList.put(keyVal, nxLookupDataModel);
					}
				}
			}
			response.setNxLookupDataList(lookupDataList);
		}
		else if(Optional.ofNullable(request.getAction()).isPresent() && request.getAction().equalsIgnoreCase(StringConstants.GET_PRODUCT_SEARCH_LIST)) {
			Map<String,NxLookupData> lookupDataMap = nxMyPriceRepositoryServce.getLookupDataByItemId(FmoConstants.UI_PRODUCT_PRODUCT_SEARCH);
			Map<String,NxLookupDataModel> respLookupDataMap = new HashMap<String,NxLookupDataModel>(); 
			for(String keyVal : lookupDataMap.keySet()) {
				NxLookupData lookupData=lookupDataMap.get(keyVal);
				NxLookupDataModel nxLookupDataModel = new NxLookupDataModel();
				nxLookupDataModel.setActive(lookupData.getActive());
				nxLookupDataModel.setCriteria(lookupData.getCriteria());
				nxLookupDataModel.setDatasetName(lookupData.getDatasetName());
				nxLookupDataModel.setDescription(lookupData.getDescription());
				nxLookupDataModel.setItemId(lookupData.getItemId());
				if(nxLookupDataModel.getActive().equals("Y")) {
					respLookupDataMap.put(keyVal, nxLookupDataModel);
				}
			}
			response.setNxLookupDataList(respLookupDataMap);
		}
		else if(Optional.ofNullable(request.getAction()).isPresent() && request.getAction().equalsIgnoreCase(StringConstants.GET_BEGIN_BILL_MONTH_USRP)) {
			List<String> dataSetName = new ArrayList<String>();
			dataSetName.add(FmoConstants.UI_BEGIN_BILL_MONTH_USRP);
			List<NxLookupData> nxLookUpDatas = nxLookupDataRepository.fetchByDatasetNameAndActive(dataSetName, StringConstants.Y);
			Map<String,NxLookupDataModel> respLookupDataMap = new LinkedHashMap<String,NxLookupDataModel>(); 
			for(NxLookupData lookupData : nxLookUpDatas) {
				NxLookupDataModel nxLookupDataModel = new NxLookupDataModel();
				nxLookupDataModel.setActive(lookupData.getActive());
				nxLookupDataModel.setCriteria(lookupData.getCriteria());
				nxLookupDataModel.setDatasetName(lookupData.getDatasetName());
				nxLookupDataModel.setDescription(lookupData.getDescription());
				nxLookupDataModel.setItemId(lookupData.getItemId());
				if(nxLookupDataModel.getActive().equals("Y")) {
					respLookupDataMap.put(lookupData.getItemId(), nxLookupDataModel);
				}
			}
			response.setNxLookupDataList(respLookupDataMap);
		}else if(Optional.ofNullable(request.getAction()).isPresent() && request.getAction().equalsIgnoreCase(StringConstants.GET_BILL_MONTH_USRP)) {
			List<String> dataSetName = new ArrayList<String>();
			dataSetName.add(FmoConstants.UI_BILL_MONTH_USRP);
			List<NxLookupData> nxLookUpDatas = nxLookupDataRepository.fetchByDatasetNameAndActive(dataSetName, StringConstants.Y);
			Map<String,NxLookupDataModel> respLookupDataMap = new LinkedHashMap<String,NxLookupDataModel>(); 
			for(NxLookupData lookupData : nxLookUpDatas) {
				NxLookupDataModel nxLookupDataModel = new NxLookupDataModel();
				nxLookupDataModel.setActive(lookupData.getActive());
				nxLookupDataModel.setCriteria(lookupData.getCriteria());
				nxLookupDataModel.setDatasetName(lookupData.getDatasetName());
				nxLookupDataModel.setDescription(lookupData.getDescription());
				nxLookupDataModel.setItemId(lookupData.getItemId());
				if(nxLookupDataModel.getActive().equals("Y")) {
					respLookupDataMap.put(lookupData.getItemId(), nxLookupDataModel);
				}
			}
			response.setNxLookupDataList(respLookupDataMap);
		}
		setSuccessResponse(response);
		return response;
	}
	
}
