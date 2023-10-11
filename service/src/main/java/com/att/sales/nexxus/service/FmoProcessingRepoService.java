package com.att.sales.nexxus.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.att.sales.nexxus.dao.model.FmoOfferJsonRulesMapping;
import com.att.sales.nexxus.dao.model.NxKeyFieldPathModel;
import com.att.sales.nexxus.dao.model.NxLineItemLookUpDataModel;
import com.att.sales.nexxus.dao.model.NxLineItemLookUpFieldModel;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.model.NxOutputFileModel;
import com.att.sales.nexxus.dao.model.NxRequestDetails;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.repository.NxLineItemProcessingDao;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.dao.repository.NxOutputFileRepository;
import com.att.sales.nexxus.dao.repository.NxRequestDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxSolutionDetailsRepository;

/**
 * The Class FmoProcessingRepoService.
 *
 * @author vt393d
 */
@Component
public class FmoProcessingRepoService {
	
	
	
	/** The line item processing dao. */
	@Autowired
	private NxLineItemProcessingDao lineItemProcessingDao;
	
	/** The nexus output file repository. */
	@Autowired
	private NxOutputFileRepository nexusOutputFileRepository;
	
	/** The nx request details repository. */
	@Autowired
	private NxRequestDetailsRepository nxRequestDetailsRepository;
	
	/** The nx solution details repository. */
	@Autowired
	private NxSolutionDetailsRepository nxSolutionDetailsRepository;
	
	@Autowired
	private NxLookupDataRepository nxLookupDataRepository;
	
	@Autowired
	private NxMyPriceRepositoryServce nxMyPriceRepositoryServce;
	
	/**
	 * Save nx output file.
	 *
	 * @param data the data
	 */
	public void saveNxOutputFile(NxOutputFileModel data) {
		nexusOutputFileRepository.save(data);
	}
	
	/**
	 * Save nx request details.
	 *
	 * @param data the data
	 */
	public void saveNxRequestDetails(NxRequestDetails data) {
		nxRequestDetailsRepository.save(data);
	}
	
	/**
	 * Gets the fmo rules from tbl.
	 *
	 * @param allOfferIds the all offer ids
	 * @return the fmo rules from tbl
	 */
	public List<FmoOfferJsonRulesMapping> getFmoRulesFromTbl(Set<Long> allOfferIds) {
		return lineItemProcessingDao.getFmoRules(allOfferIds);
	}

	/**
	 * Gets the data from look up tbl.
	 *
	 * @param inputData the input data
	 * @param offerId the offer id
	 * @param udfId the udf id
	 * @param componentId the component id
	 * @return the data from look up tbl
	 */
	public Object getDataFromSalesLookUpTbl(Object inputData,Long offerId,Long udfId,
			Long componentId) {
		List<Object> result= lineItemProcessingDao.getDataFromSalesLookUpTbl(inputData,
				offerId, udfId, componentId);
		if(CollectionUtils.isNotEmpty(result)) {
			return result.get(0);
		}
		return null;
	}
	
	
	/**
	 * Gets the data from ims 2 look up tbl.
	 *
	 * @param inputData the input data
	 * @param offerId the offer id
	 * @param udfId the udf id
	 * @param componentId the component id
	 * @return the data from ims 2 look up tbl
	 */
	public Object getDataFromIms2LookUpTbl(Object inputData,Long offerId,Long udfId,
			Long componentId) {
		List<Object> result= lineItemProcessingDao.getDataFromIms2LookUpTbl(inputData,
				offerId, udfId, componentId);
		if(CollectionUtils.isNotEmpty(result)) {
			return result.get(0);
		}
		return null;
	}
	
	/**
	 * Gets the nexxus line item look up items.
	 *
	 * @param offerId the offer id
	 * @param inputType the input type
	 * @return the nexxus line item look up items
	 */
	public List<NxLineItemLookUpFieldModel> getNxLineItemFieldDataByOfferId(Long offerId,
			String inputType) {
		return lineItemProcessingDao.
	    		getNxLineItemFieldDataByOfferId(offerId,inputType);
	}
	
	/**
	 * Gets the nx line item field data by offer name.
	 *
	 * @param offerName the offer name
	 * @param inputType the input type
	 * @return the nx line item field data by offer name
	 */
	public List<NxLineItemLookUpFieldModel> getNxLineItemFieldDataByOfferName(String offerName,
			String inputType) {
		return lineItemProcessingDao.
	    		getNxLineItemFieldDataByOfferName(offerName, inputType);
	}
	
	/**
	 * Load nexxus key path data.
	 *
	 * @return the list
	 */
	public List<NxKeyFieldPathModel> loadNexxusKeyPathData() {
		return lineItemProcessingDao.loadNexxusKeyPathData();
	}
	
	
	
	/**
	 * Gets the line item data.
	 *
	 * @param condition the condition
	 * @param resultColumn1 the result column 1
	 * @param resultColumn2 the result column 2
	 * @param flowType the flow type
	 * @return the line item data
	 */
	public List<NxLineItemLookUpDataModel> getLineItemData(String condition,String resultColumn1,
			String resultColumn2,String flowType) {
		if(StringUtils.isNotEmpty(condition) && StringUtils.isNotEmpty(flowType)) {
			return lineItemProcessingDao.getLineItemData(condition,resultColumn1,resultColumn2,flowType);
		}
		return new ArrayList<>();
		
	}
	
	/**
	 * Gets the list item data.
	 *
	 * @param condition the condition
	 * @param flowType the flow type
	 * @return the list item data
	 */
	public List<NxLineItemLookUpDataModel> getLineItemData(String condition,String flowType )  {
		return lineItemProcessingDao.getLineItemData(condition,flowType);
	}
	
	/**
	 * Gets the nx outpu file model.
	 *
	 * @param requestId the request id
	 * @return the nx outpu file model
	 */
	public NxOutputFileModel getNxOutputFileModel(Long requestId) {
		List<NxOutputFileModel> nxOutputFileModels=nexusOutputFileRepository.findByNxReqId(requestId);
		if(CollectionUtils.isNotEmpty(nxOutputFileModels)) {
			return nxOutputFileModels.get(0)!=null?nxOutputFileModels.get(0):null;
		}
		return null;
	}
	
	
	/**
	 * Save solution details.
	 *
	 * @param nxSolutionDetail the nx solution detail
	 */
	public void saveSolutionDetails(NxSolutionDetail nxSolutionDetail) {
		nxSolutionDetailsRepository.save(nxSolutionDetail);
	}
	
	
	/**
	 * Gets the lookup data by id.
	 *
	 * @param datasetName the dataset name
	 * @param itemId the item id
	 * @return the lookup data by id
	 */
	public NxLookupData getLookupDataById(String datasetName,String itemId) {
		List<NxLookupData> resultLst=lineItemProcessingDao.getNxLookupDataById(datasetName, itemId);
		if(CollectionUtils.isNotEmpty(resultLst)) {
			return resultLst.get(0);
		}
		return null;
	}
	
	/*protected String getDataFromNxLookUp(String input, String looupDataSet) {
		if(StringUtils.isNotEmpty(looupDataSet)) {
			NxLookupData nxLookup=nxLookupDataRepository.findTopByDatasetNameAndItemId(looupDataSet, input);
			if(null!=nxLookup) {
				input=nxLookup.getDescription();
			}
		}
		return input;
	}*/
	
	protected String getDataFromNxLookUp(String input, String looupDataSet) {
		if(StringUtils.isNotEmpty(looupDataSet) && StringUtils.isNotEmpty(input)) {
			Map<String,NxLookupData> resultMap=nxMyPriceRepositoryServce.getLookupDataByItemId(looupDataSet);
			if(null!=resultMap && resultMap.containsKey(input) && null!= resultMap.get(input) ) {
				NxLookupData data=resultMap.get(input);
				return data.getDescription();
			}
		}
		return input;
	}
	
	protected List<NxLookupData> getDataFromNxLooUpByDatasetName(String looupDataSet) {
		if(StringUtils.isNotEmpty(looupDataSet)) {
			return nxLookupDataRepository.findByDatasetName(looupDataSet);
		}
		return null;
	}
}
