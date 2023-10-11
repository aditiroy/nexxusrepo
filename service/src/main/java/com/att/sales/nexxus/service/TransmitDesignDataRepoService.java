package com.att.sales.nexxus.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.att.sales.nexxus.constant.CommonConstants;
import com.att.sales.nexxus.dao.model.NxDesign;
import com.att.sales.nexxus.dao.model.NxDesignAudit;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.model.NxUdfMapping;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.repository.NxDesignAuditRepository;
import com.att.sales.nexxus.dao.repository.NxDesignRepository;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.dao.repository.NxSolutionDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxUdfMappingDao;
import com.att.sales.nexxus.dao.repository.SalesMsProdcompRepository;

/**
 * The Class TransmitDesignDataRepoService.
 */
@Component
public class TransmitDesignDataRepoService {

	@Autowired
	private NxSolutionDetailsRepository nxSolutionDetailsRepo;
	
	@Autowired
	private NxDesignAuditRepository nxDesignAuditRepo;
	
	@Autowired
	private NxUdfMappingDao nxUdfMappingDao;
	
	@Autowired
	private NxLookupDataRepository nxLookupDataRepo;
	
	@Autowired
	private SalesMsProdcompRepository salesMsProdcompRepo;
	
	@Autowired
	private NxDesignRepository nxDesignRepository;
	
	/**
	 * Save solution details.
	 *
	 * @param entity the entity
	 */
	public void saveSolutionDetails(NxSolutionDetail entity) {
		nxSolutionDetailsRepo.saveAndFlush(entity);
	}
	
	/**
	 * Find by external key.
	 *
	 * @param externalKey the external key
	 * @return the list
	 */
	public List<NxSolutionDetail> findByExternalKey(Long externalKey){
		return nxSolutionDetailsRepo.findByExternalKey(externalKey);
	}
	
	/**
	 * Gets the udf data map.
	 *
	 * @param ruleSet the rule set
	 * @param offerId the offer id
	 * @return the udf data map
	 */
	public Map<Long,Map<String,NxUdfMapping>> getUdfDataMap(String ruleSet, Long offerId){
		Map<Long,Map<String,NxUdfMapping>> result=new HashMap<>();
		List<NxUdfMapping> resultList=nxUdfMappingDao.getNxUdfDataByOfferIdAndRule(ruleSet, offerId);
		Optional.ofNullable(resultList).map(List::stream).orElse(Stream.empty()).filter(Objects::nonNull).
		forEach( data -> {
			if(!result.containsKey(data.getComponentId())) {
				result.put(data.getComponentId(), new HashMap<String,NxUdfMapping>());
			}
			result.get(data.getComponentId()).put(data.getUdfAbbr(), data);
		});
		return result;
	}
	
	/**
	 * Save audit data.
	 *
	 * @param entity the entity
	 */
	public void saveAuditData(NxDesignAudit entity) {
		nxDesignAuditRepo.save(entity);
	}
	
	/**
	 * Find top by dataset name and description.
	 *
	 * @param datasetName the dataset name
	 * @param description the description
	 * @return the nx lookup data
	 */
	public NxLookupData findTopByDatasetNameAndDescription(String datasetName, String description) {
		return nxLookupDataRepo.findTopByDatasetNameAndDescription(datasetName, description);
	}
	
	/**
	 * Gets the udf attr id from sales tbl.
	 *
	 * @param offerId the offer id
	 * @param componentId the component id
	 * @param udfId the udf id
	 * @param udfAttributeValue the udf attribute value
	 * @return the udf attr id from sales tbl
	 */
	public Long getUdfAttrIdFromSalesTbl(Long offerId,Long componentId,
			Long udfId,String udfAttributeValue){
		List<Long>result=salesMsProdcompRepo.getUdfAttrIdFromSalesTbl(offerId, componentId, udfId, udfAttributeValue);
		if(CollectionUtils.isNotEmpty(result)) {
			return result.get(0);
		}
		return null;
	}
	
	/**
	 * Gets the ped status map.
	 *
	 * @return the ped status map
	 */
	protected Map<String,NxLookupData> getPedStatusMap(){
		List<NxLookupData> lookUpDataList=nxLookupDataRepo.findByDatasetName(CommonConstants.STATUS_DATASET_NAME);
		return Optional.ofNullable(lookUpDataList).map(List::stream).orElse(Stream.empty()).filter(Objects::nonNull)
				.collect(Collectors.toMap(NxLookupData::getItemId, Function.identity()));
	}
	
	public void saveNxDesings(List<NxDesign> nxDesigns) {
		nxDesignRepository.saveAll(nxDesigns);
	}
	
}
