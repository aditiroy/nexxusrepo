package com.att.sales.nexxus.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.model.NxMpConfigJsonMapping;
import com.att.sales.nexxus.dao.model.NxMpConfigMapping;
import com.att.sales.nexxus.dao.model.NxSsdfSpeedMapping;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.dao.repository.NxMpConfigJsonMappingRepository;
import com.att.sales.nexxus.dao.repository.NxMpConfigMappingRepository;
import com.att.sales.nexxus.dao.repository.NxSsdfSpeedMappingRepository;

@Component
@CacheConfig(cacheNames = "myPrice-cache")
public class NxMyPriceRepositoryServce {
	
	private static final Logger log = LoggerFactory.getLogger(NxMyPriceRepositoryServce.class);
	
	@Value("${cache.enabled:false}")
	private boolean cacheEnabled;
	
	public boolean cacheEnabled() {
		return cacheEnabled;
	}
	@CacheEvict(value={ "getLookupDataByItemId", "findByOfferAndProductTypeAndRuleName" ,"findByOfferAndRuleName",
			"findByOfferAndSubOfferAndProductTypeAndRuleName","findByOfferAndProductTypeAndRuleNameForJson","findByOfferAndRuleNameForJson","datasetName"},allEntries = true)
    public void clearCache(){
		log.info("*********************Cache Clear************************");
	}
	
	@Autowired
	private NxLookupDataRepository nxLookupDataRepository;
	
	@Autowired
	private NxMpConfigMappingRepository nxMpConfigMappingRepository;
	
	@Autowired
	private NxMpConfigJsonMappingRepository nxMpConfigJsonMappingRepository;
	
	
	@Cacheable(value="getLookupDataByItemId",key = "#datasetName",condition = "#root.target.cacheEnabled()")
	public Map<String,NxLookupData> getLookupDataByItemId(String datasetName){
		List<NxLookupData> lookupData=nxLookupDataRepository.findByDatasetName(datasetName);
		Map<String,NxLookupData> resultMap= new HashMap<>();
		if(CollectionUtils.isNotEmpty(lookupData)) {
			for(NxLookupData data:lookupData) {
				resultMap.put(data.getItemId(),data);
			}
		}
		return resultMap;
	}
	
	@Cacheable(value="findByOfferAndProductTypeAndRuleName",key = "{#offerName,#productType,#ruleType}",condition = "#root.target.cacheEnabled()")
	public List<NxMpConfigMapping>  findByOfferAndProductTypeAndRuleName(String offerName,String productType,String ruleType) {
		return nxMpConfigMappingRepository.findByOfferAndProductTypeAndRuleName(offerName, productType,ruleType);
	}
	
	@Cacheable(value="findByOfferAndRuleName",key = "{#offerName,#ruleType}",condition = "#root.target.cacheEnabled()")
	public List<NxMpConfigMapping> findByOfferAndRuleName(String offerName,String ruleType) {
		return nxMpConfigMappingRepository.findByOfferAndRuleName(offerName,ruleType);
	}
	
	@Cacheable(value="findByOfferAndSubOfferAndProductTypeAndRuleName",key = "{#offerName,#subOfferName,#productType,#ruleType}",condition = "#root.target.cacheEnabled()")
	public List<NxMpConfigJsonMapping>  findByOfferAndSubOfferAndProductTypeAndRuleName(String offerName,String subOfferName,String productType,String ruleType) {
		return nxMpConfigJsonMappingRepository.findByOfferAndSubOfferAndProductTypeAndRuleName(offerName,subOfferName, productType,ruleType);
	}
	
	@Cacheable(value="findByOfferAndProductTypeAndRuleNameForJson",key = "{#offerName,#productType,#ruleType}",condition = "#root.target.cacheEnabled()")
	public List<NxMpConfigJsonMapping>  findByOfferAndProductTypeAndRuleNameForJson(String offerName,String productType,String ruleType) {
		return nxMpConfigJsonMappingRepository.findByOfferAndProductTypeAndRuleName(offerName, productType,ruleType);
	}
	
	@Cacheable(value="findByOfferAndRuleNameForJson",key = "{#offerName,#ruleType}",condition = "#root.target.cacheEnabled()")
	public List<NxMpConfigJsonMapping> findByOfferAndRuleNameForJson(String offerName,String ruleType) {
		return nxMpConfigJsonMappingRepository.findByOfferAndRuleName(offerName,ruleType);
	}
	
	
	@Cacheable(value="getDataFromLookup",key = "{#datasetName}", condition = "#root.target.cacheEnabled()")
	public LinkedHashMap<String, String> getDataFromLookup(String datasetName){
		LinkedHashMap<String,String> result=new LinkedHashMap<String,String>();
		List<NxLookupData> nxLookupLst=nxLookupDataRepository.findByDatasetName(datasetName);
		 Optional.ofNullable(nxLookupLst).map(List::stream).orElse(Stream.empty()).filter(Objects::nonNull).
		    forEach( data -> {
		    	if(StringUtils.isNotEmpty(data.getCriteria())) {
		    		result.put(data.getItemId(), data.getCriteria());
		    	}
		 });
		return result;
	}
	
	@Cacheable(value="getDescDataFromLookup",key = "{#datasetName}", condition = "#root.target.cacheEnabled()")
	public Map<String, String> getDescDataFromLookup(String datasetName){
		LinkedHashMap<String,String> result=new LinkedHashMap<String,String>();
		List<NxLookupData> nxLookupLst=nxLookupDataRepository.findByDatasetName(datasetName);
		 Optional.ofNullable(nxLookupLst).map(List::stream).orElse(Stream.empty()).filter(Objects::nonNull).
		    forEach( data -> {
		    	if(StringUtils.isNotEmpty(data.getDescription())) {
		    		result.put(data.getItemId(), data.getDescription());
		    	}
		 });
		return result;
	}
	
	@Cacheable(value="getItemDescFromLookup",key = "{#datasetName,#active}", condition = "#root.target.cacheEnabled()")
	public List<NxLookupData> getItemDescFromLookup(String datasetName, String active){
		return nxLookupDataRepository.findByDatasetNameAndActive(datasetName, active);
	}
	
	@Autowired
	private NxSsdfSpeedMappingRepository nxSsdfSpeedMappingDao;
	
	
	@Cacheable(value="getSsdfMileageData",key = "{#component,#offer}", condition = "#root.target.cacheEnabled()")
	public LinkedHashMap<String, List<NxSsdfSpeedMapping>> getSsdfMileageData(String component, String offer){
		LinkedHashMap<String, List<NxSsdfSpeedMapping>> speedMap = new LinkedHashMap<>();
		List<NxSsdfSpeedMapping> nxSsdfSpeedMappings = nxSsdfSpeedMappingDao.getSpeedData(component, offer);
		for(NxSsdfSpeedMapping nxSsdfSpeedMapping : nxSsdfSpeedMappings) {
			String key = nxSsdfSpeedMapping.getConnectionType() + " " + nxSsdfSpeedMapping.getPriceGroup() + " " + nxSsdfSpeedMapping.getPriceType();
			if(speedMap.containsKey(key)) {
				speedMap.get(key).add(nxSsdfSpeedMapping);
			}else {
				List<NxSsdfSpeedMapping> list =  new ArrayList<NxSsdfSpeedMapping>();
				list.add(nxSsdfSpeedMapping);
				speedMap.put(key, list);
			}
		}
		return speedMap;
	}
	
	@Cacheable(value="getSsdfConnectionData",key = "{#component,#offer}", condition = "#root.target.cacheEnabled()")
	public Map<String, List<NxSsdfSpeedMapping>> getSsdfConnectionData(String component, String offer){
		Map<String, List<NxSsdfSpeedMapping>> speedMap = new HashMap<>();
		List<NxSsdfSpeedMapping> nxSsdfSpeedMappings = nxSsdfSpeedMappingDao.getSpeedData(component, offer);
		for(NxSsdfSpeedMapping nxSsdfSpeedMapping : nxSsdfSpeedMappings) {
			String key = nxSsdfSpeedMapping.getConnectionType() + " " + nxSsdfSpeedMapping.getPriceGroup();
			if(speedMap.containsKey(key)) {
				speedMap.get(key).add(nxSsdfSpeedMapping);
			}else {
				List<NxSsdfSpeedMapping> list =  new ArrayList<NxSsdfSpeedMapping>();
				list.add(nxSsdfSpeedMapping);
				speedMap.put(key, list);
			}
		}
		return speedMap;
	}
	
	
	@Cacheable(value="getLookupDataByItemIdIgnoreKeyCase",key = "#datasetName",condition = "#root.target.cacheEnabled()")
	public Map<String,NxLookupData> getLookupDataByItemIdIgnoreKeyCase(String datasetName){
		List<NxLookupData> lookupData=nxLookupDataRepository.findByDatasetName(datasetName);
		Map<String,NxLookupData> resultMap= new TreeMap<String, NxLookupData>(String.CASE_INSENSITIVE_ORDER);
		if(CollectionUtils.isNotEmpty(lookupData)) {
			for(NxLookupData data:lookupData) {
				resultMap.put(data.getItemId(),data);
			}
		}
		return resultMap;
	}
	
	@Cacheable(value="getLookupDataByCriteria", key= "#datasetName",condition = "#root.target.cacheEnabled()")
	public Map<String,NxLookupData> getLookupDataByCriteria(String datasetName){
		List<NxLookupData> lookupData=nxLookupDataRepository.findByDatasetName(datasetName);
		Map<String,NxLookupData> resultMap= new HashMap<>();
		if(CollectionUtils.isNotEmpty(lookupData)) {
			for(NxLookupData data:lookupData) {
				resultMap.put(data.getCriteria(),data);
			}
		}
		return resultMap;
	}


}
