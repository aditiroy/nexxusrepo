package com.att.sales.nexxus.helper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.att.sales.nexxus.constant.FmoConstants;
import com.att.sales.nexxus.dao.model.FmoProdLookupData;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.repository.FmoProdLookupDataRepo;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.hazelcast.core.HazelcastInstance;

/**
 * The Class FmoLookUpDataHelper.
 *
 * @author vt393d
 */
@Component
@CacheConfig(cacheNames = "nexxus-cache")
public class FmoLookUpDataHelper{
	
	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(FmoLookUpDataHelper.class);
	
	/** The fmo prod lookup data repo. */
	@Autowired
	private FmoProdLookupDataRepo fmoProdLookupDataRepo;
	
	/** The hazelcast instance. */
	@Autowired
	private HazelcastInstance  hazelcastInstance;
	
	@Autowired
	private NxLookupDataRepository nxLookupDataRepository;
	
	/**
	 * This method is to clear all the elements from catch.
	 */
	@CacheEvict(allEntries = true)
	 public void clearCache(){
		//clear the cache
	}
	
	
	
	/**
	 * Creates the price type data map.
	 *
	 * @return the concurrent map
	 */
	/*@Cacheable(value="priceTypeData")
	public ConcurrentMap<String,String> createPriceTypeDataMap(){
		logger.info("Inside createPriceTypeDataMap method: {}","");
		ConcurrentMap<String,String> resultMap= new ConcurrentHashMap<>();
		List<FmoProdLookupData> fmoProdLookupDataLst=fmoProdLookupDataRepo.
				findByRateTypeAndIms2code(FmoConstants.RATE_TYPE, FmoConstants.IMS2_CODE);
		Optional.ofNullable(fmoProdLookupDataLst).map(List::stream).orElse(Stream.empty()).
		filter(Objects::nonNull).forEach( data -> {
			String dataString = (data.getIms2Code().concat(data.getRateType())).toUpperCase();
			String priceType=FmoConstants.NOT_AVAILABLE;
			if(FmoConstants.PRICE_TYPE_MAPPING.containsKey(dataString) ) {
				priceType=FmoConstants.PRICE_TYPE_MAPPING.get(dataString);
			}
			resultMap.put(data.getProductRateId(),priceType);
		});
		return resultMap;
	}*/
	
	@Cacheable(value="priceTypeData")
	public ConcurrentMap<String,String> createPriceTypeDataMap(){
		logger.info("Inside createPriceTypeDataMap method: {}","");
		ConcurrentMap<String,String> resultMap= new ConcurrentHashMap<>();
		Map<String,String> priceTypeDataMap=getDataMapForPriceType();
		List<FmoProdLookupData> fmoProdLookupDataLst=fmoProdLookupDataRepo.
				findDataRateType(FmoConstants.RATE_TYPE);
		Optional.ofNullable(fmoProdLookupDataLst).map(List::stream).orElse(Stream.empty()).
		filter(Objects::nonNull).forEach( data -> {
			String dataString = (data.getIms2Code().concat(data.getRateType())).toUpperCase();
			String priceType=FmoConstants.NOT_AVAILABLE;
			if(priceTypeDataMap.containsKey(dataString) ) {
				priceType=priceTypeDataMap.get(dataString);
			}
			resultMap.put(data.getProductRateId(),priceType);
		});
		return resultMap;
	}
	
	protected Map<String,String> getDataMapForPriceType(){
		Map<String,String> result=new HashMap<String,String>();
		List<NxLookupData> nxLookupLst=nxLookupDataRepository.findByDatasetName(FmoConstants.PRICE_TYPE_DATASET);
		 Optional.ofNullable(nxLookupLst).map(List::stream).orElse(Stream.empty()).filter(Objects::nonNull).
		    forEach( data -> {
		    	result.put(data.getItemId(), data.getDescription());
		    	
		 });
		return result;
	}
	
	
	
	/**
	 * Gets the fmo look data from cache.
	 *
	 * @return the fmo look data from cache
	 */
	@SuppressWarnings("unchecked")
	public ConcurrentMap<String,String> getFmoLookDataFromCache(){
		logger.info("Inside getFmoLookDataFromCache method: {}","");
		if(MapUtils.isNotEmpty(hazelcastInstance.getMap(FmoConstants.PRICE_TYPE_DATA))) {
			return hazelcastInstance.getMap(FmoConstants.PRICE_TYPE_DATA).entrySet()!=null
					&& hazelcastInstance.getMap(FmoConstants.PRICE_TYPE_DATA).entrySet().size()>0?
							(ConcurrentMap<String, String>)hazelcastInstance.getMap(FmoConstants.PRICE_TYPE_DATA).
							entrySet().iterator().next().getValue():null;
		}
		return null;
	}

	
	

}
