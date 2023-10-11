package com.att.sales.nexxus.service;



import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;

import com.att.sales.nexxus.constant.FmoConstants;
import com.att.sales.nexxus.dao.model.FmoJsonRulesModel;
import com.att.sales.nexxus.reteriveicb.model.PricePlanDetails;
import com.att.sales.nexxus.reteriveicb.model.Site;
import com.att.sales.nexxus.util.NexxusJsonUtility;

/**
 * The Class FmoExecutorService.
 *
 * @author vt393d
 */
public class FmoExecutorService implements Callable<Object>{

	
	/** The item map. */
	private Map<String,List<FmoJsonRulesModel>> itemMap;
	
	/** The nexus json utility. */
	private NexxusJsonUtility nexusJsonUtility;
	
	/** The site. */
	private Site site;
	
	/** The offer id. */
	private Long offerId;
	
	/** The fmo processing service. */
	private FmoProcessingService fmoProcessingService;
	
	/** The price plan details. */
	private List<PricePlanDetails> pricePlanDetails;
	
	/** The method input map. */
	private Map<String,Object> methodInputMap;
	
	
	
	/**
	 * Gets the item map.
	 *
	 * @return the item map
	 */
	public Map<String, List<FmoJsonRulesModel>> getItemMap() {
		return itemMap;
	}
	
	/**
	 * Sets the item map.
	 *
	 * @param itemMap the item map
	 */
	public void setItemMap(Map<String, List<FmoJsonRulesModel>> itemMap) {
		this.itemMap = itemMap;
	}
	
	/**
	 * Gets the nexus json utility.
	 *
	 * @return the nexus json utility
	 */
	public NexxusJsonUtility getNexusJsonUtility() {
		return nexusJsonUtility;
	}
	
	/**
	 * Sets the nexus json utility.
	 *
	 * @param nexusJsonUtility the new nexus json utility
	 */
	public void setNexusJsonUtility(NexxusJsonUtility nexusJsonUtility) {
		this.nexusJsonUtility = nexusJsonUtility;
	}
	
	/**
	 * Gets the offer id.
	 *
	 * @return the offer id
	 */
	public Long getOfferId() {
		return offerId;
	}
	
	/**
	 * Sets the offer id.
	 *
	 * @param offerId the new offer id
	 */
	public void setOfferId(Long offerId) {
		this.offerId = offerId;
	}
	
	/**
	 * Gets the fmo processing service.
	 *
	 * @return the fmo processing service
	 */
	public FmoProcessingService getFmoProcessingService() {
		return fmoProcessingService;
	}
	
	/**
	 * Sets the fmo processing service.
	 *
	 * @param fmoProcessingService the new fmo processing service
	 */
	public void setFmoProcessingService(FmoProcessingService fmoProcessingService) {
		this.fmoProcessingService = fmoProcessingService;
	}
	
	/**
	 * Gets the price plan details.
	 *
	 * @return the price plan details
	 */
	public List<PricePlanDetails> getPricePlanDetails() {
		return pricePlanDetails;
	}
	
	/**
	 * Sets the price plan details.
	 *
	 * @param pricePlanDetails the new price plan details
	 */
	public void setPricePlanDetails(List<PricePlanDetails> pricePlanDetails) {
		this.pricePlanDetails = pricePlanDetails;
	}
	
	/**
	 * Gets the method input map.
	 *
	 * @return the method input map
	 */
	public Map<String, Object> getMethodInputMap() {
		return methodInputMap;
	}
	
	/**
	 * Sets the method input map.
	 *
	 * @param methodInputMap the method input map
	 */
	public void setMethodInputMap(Map<String, Object> methodInputMap) {
		this.methodInputMap = methodInputMap;
	}
	
	
	/**
	 * Instantiates a new fmo executor service.
	 *
	 * @param site the site
	 */
	public FmoExecutorService(Site site) {
		this.site=site;
	}
	
	/* (non-Javadoc)
	 * @see edu.emory.mathcs.backport.java.util.concurrent.Callable#call()
	 */
	@Override
	public Object call() throws Exception {
		return this.getSiteLevelData(site, itemMap, pricePlanDetails,
				methodInputMap, offerId);
	}
	
	
	
	/**
	 * Gets the site level data.
	 *
	 * @param site the site
	 * @param itemMap the item map
	 * @param pricePlanDetails the price plan details
	 * @param methodInputMap the method input map
	 * @param offerId the offer id
	 * @return the site level data
	 */
	@SuppressWarnings("unchecked")
	protected JSONObject getSiteLevelData(Site site,Map<String,List<FmoJsonRulesModel>> itemMap,
			List<PricePlanDetails> pricePlanDetails,Map<String,Object> methodInputMap,Long offerId) {
		List<FmoJsonRulesModel> siteFields=itemMap.get(FmoConstants.SITE_TAG);
		JSONObject jsonObject = getFmoProcessingService().createNewJSONObject();
		Optional.ofNullable(siteFields).map(List::stream).orElse(Stream.empty()).
		filter(item-> item!=null && StringUtils.isNotEmpty(item.getUdfQuery())).forEach( item -> 
			jsonObject.put(item.getFieldNameJson(), getFmoProcessingService().getData(getNexusJsonUtility().getValue(site, 
					item.getUdfQuery()), offerId, item.getUdfId(), item.getComponentId(),item.getDataSetname(),item.getDefaultValue()))
		);
		jsonObject.put(FmoConstants.REF_OFFER_ID, getFmoProcessingService().getData(offerId,
				null, null, null,FmoConstants.NOT_AVAILABLE,null));
		String countryCd=getFmoProcessingService().getCountryCode(jsonObject);
		methodInputMap.put(FmoConstants.COUNTRY_CD, getFmoProcessingService().getData(countryCd,
				null, null, null,FmoConstants.NOT_AVAILABLE,null));
		PricePlanDetails pricePlanDetailsObj=getFmoProcessingService().getPricePlanDetails(pricePlanDetails,methodInputMap);
		jsonObject.put(FmoConstants.DESIGN_ATR, getFmoProcessingService().createDesign(site.getDesignSiteOfferPort(),
				site.getPriceDetails(),itemMap,pricePlanDetailsObj,site.getSiteId(),offerId));
		return jsonObject;
	}

}
