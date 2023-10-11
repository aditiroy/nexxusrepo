package com.att.sales.nexxus.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.att.sales.nexxus.constant.FmoConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.constant.OutputFileConstants;
import com.att.sales.nexxus.dao.model.NxKeyFieldPathModel;
import com.att.sales.nexxus.dao.model.NxLineItemLookUpDataModel;
import com.att.sales.nexxus.dao.model.NxLineItemLookUpFieldModel;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.myprice.transaction.dao.service.NxMpRepositoryService;
import com.att.sales.nexxus.myprice.transaction.service.ConfigAndUpdateProcessingFmoService;
import com.att.sales.nexxus.output.entity.NxAvpnIntAccessOutputBean;
import com.att.sales.nexxus.output.entity.NxAvpnOutputBean;
import com.att.sales.nexxus.output.entity.NxBaseOutputBean;
import com.att.sales.nexxus.output.entity.NxBvoipOutputBean;
import com.att.sales.nexxus.output.entity.NxDsAccessBean;
import com.att.sales.nexxus.output.entity.NxEthernetAccessOutputBean;
import com.att.sales.nexxus.output.entity.NxMisBean;
import com.att.sales.nexxus.output.entity.NxOutputBean;
import com.att.sales.nexxus.util.JacksonUtil;
import com.att.sales.nexxus.util.NexxusJsonUtility;
import com.att.sales.nexxus.util.OutputBeanUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * The Class LineItemProcessingService.
 *
 * @author vt393d
 */
@Component
public class LineItemProcessingService {
	
	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(LineItemProcessingService.class);
	
	/** The repository service. */
	@Autowired
	private FmoProcessingRepoService repositoryService;
	
	/** The nexus json utility. */
	@Autowired
	private NexxusJsonUtility nexusJsonUtility;
	
	@Autowired
	private FmoMpOutputJsonHelper fmoMpOutputJsonHelper;
	
	@Autowired
	private NxMpRepositoryService nxMpRepositoryService;
	
	@Autowired
	private ConfigAndUpdateProcessingFmoService configAndUpdateProcessingFmoService;
	
	
	@Value("${myPrice.fmo.flow:N}")
	private String mpFmoFlow;
	
	
	
	public String getMpFmoFlow() {
		return mpFmoFlow;
	}
	
	

	
	
	
	/**
	 * Gets the key path data map.
	 *
	 * @return the key path data map
	 */
	protected Map<String,String> getKeyPathDataMap(){
		List<NxKeyFieldPathModel> resultLst=repositoryService.loadNexxusKeyPathData();
		Map<String,String> resultMap=new HashMap<>();
		for(NxKeyFieldPathModel obj:resultLst) {
			resultMap.put(obj.getKeyFieldName(), obj.getJsonPath());
		}
		return resultMap;
	}
	
	
	/**
	 * Gets the line item data.
	 *
	 * @param jsonObject the json object
	 * @return LineItem
	 */
	@SuppressWarnings("unchecked")
	public NxOutputBean getLineItemData(JSONObject jsonObject,Map<String, Object> paramMap) {
		logger.info("Inside getLineItemData method: {}","");
		if(null!=jsonObject) {
			NxOutputBean nxOutputBean=new NxOutputBean();
			Map<String,String> keyPathMap=this.getKeyPathDataMap();
			JSONObject solutionObj=(JSONObject)jsonObject.get(FmoConstants.SOLUTION_ATR);
			if(null!=solutionObj) {
				paramMap.put(FmoConstants.FALLOUT_MAP, new HashMap<String, Set<String>>());
				paramMap.put(FmoConstants.IS_COMPLETE_FALLOUT, true);
				Map<String, List<String>> accessTypeDataMap = configAndUpdateProcessingFmoService
						.getDataMapFromLookup(MyPriceConstants.FMO_ACCESS_PRODUCT_NAME_DATASET);
				paramMap.put(MyPriceConstants.FMO_ACCESS_PRODUCT_NAME_DATA_MAP, accessTypeDataMap);
				JSONArray objectLst=(JSONArray)solutionObj.get(FmoConstants.OFFER_ATR);
				Optional.ofNullable(objectLst).ifPresent(i -> i.forEach(offerItem -> {
				    JSONObject offerObj = (JSONObject) offerItem;
				    Long offerId=offerObj.get(FmoConstants.OFFER_ID)!=null?
				    		Long.valueOf(offerObj.get(FmoConstants.OFFER_ID).toString()):0l;
				   // this.setLookupDataForFallout(offerId, paramMap);
				    List<NxLineItemLookUpFieldModel> resultLst= repositoryService.
				    		getNxLineItemFieldDataByOfferId(offerId,FmoConstants.FMO);
				    JSONArray siteLst=(JSONArray)offerObj.get(FmoConstants.SITE_ATR);
				    Optional.ofNullable(siteLst).ifPresent(s -> s.forEach(siteItem -> {
				    	JSONObject siteObj = (JSONObject) siteItem;
				    	siteObj.put(FmoConstants.IS_LINE_ITEM_PICKED, "N");				    	
				    	Map<String,ArrayList<String>> portwiseKeyMap=new HashMap<String, ArrayList<String>>();
					    Optional.ofNullable(resultLst).map(List::stream).orElse(Stream.empty()).filter(Objects::nonNull).
					    forEach( data -> {
					    	List<NxLineItemLookUpDataModel> lineItemData=null;
					    	if(data.getOfferName().equals(FmoConstants.AVPN)) {
					    		lineItemData=outputFileGenerationAvpnFlow(nxOutputBean,keyPathMap,siteObj,data,paramMap);
					    	}else if(data.getOfferName().equals(FmoConstants.ADI)) {
					    		lineItemData=outputFileGenerationMisFlow(nxOutputBean,keyPathMap,siteObj,data,paramMap);
					    	}else if(data.getOfferName().equals(FmoConstants.BVOIP)) {
					    		lineItemData=outputFileGenerationBvoipFlow(nxOutputBean, keyPathMap, siteObj, data,paramMap);
					    	}
					    	this.setFallout(paramMap, lineItemData,siteObj);
					    	Map<String,Object> inputMap=new HashMap<String,Object>();
					    	fmoMpOutputJsonHelper.processMPoutputJson(lineItemData, siteObj, data, inputMap);
					    	this.generateDesignLevelNxKey(lineItemData,portwiseKeyMap, siteObj);
					    });
					    this.setNxKey(offerId,siteObj, portwiseKeyMap,paramMap);
				    }));
				}));
			}
			return getNxOutputBean(nxOutputBean);
		}
		return null;
		
	}
	
	@SuppressWarnings("unchecked")
	protected void setFallout(Map<String, Object> paramMap,List<NxLineItemLookUpDataModel> lineItemData,JSONObject siteObj) {
		if(CollectionUtils.isNotEmpty(lineItemData)) {
			paramMap.put(FmoConstants.IS_COMPLETE_FALLOUT, false);
			siteObj.put(FmoConstants.IS_LINE_ITEM_PICKED, "Y");
		}
	}
	

	/**
	 * Gets the nx output bean.
	 *
	 * @param nxOutputBean the nx output bean
	 * @return the nx output bean
	 */
	protected NxOutputBean getNxOutputBean(NxOutputBean nxOutputBean) {
		if(CollectionUtils.isNotEmpty(nxOutputBean.getNxAdiMisBean())
				|| CollectionUtils.isNotEmpty(nxOutputBean.getNxAvpnIntlOutputBean())
				|| CollectionUtils.isNotEmpty(nxOutputBean.getNxAvpnOutput())
				|| CollectionUtils.isNotEmpty(nxOutputBean.getNxDs3AccessBean())
				|| CollectionUtils.isNotEmpty(nxOutputBean.getNxEthernetAccOutputBean())
				|| CollectionUtils.isNotEmpty(nxOutputBean.getNxMisDS1AccessBean()) 
				|| CollectionUtils.isNotEmpty(nxOutputBean.getNxBvoipOutputBean())) {
			return nxOutputBean;
		}
		return null;
	}

	
	/**
	 * Output file generation avpn flow.
	 *
	 * @param nxOutputBean the nx output bean
	 * @param keyPathMap the key path map
	 * @param siteObj the site obj
	 * @param data the data
	 */
	protected List<NxLineItemLookUpDataModel> outputFileGenerationAvpnFlow(NxOutputBean nxOutputBean, Map<String, String> keyPathMap,
			JSONObject siteObj, NxLineItemLookUpFieldModel data,Map<String, Object> paramMap) {
		if(("Y").equals(getMpFmoFlow())) {
			return this.fetchLineItemIdOnBeIdForMp(data,keyPathMap,nxOutputBean,siteObj,paramMap);
		}else {
			String country=null!=siteObj.get(FmoConstants.COUNTRY_CD)?
					String.valueOf(siteObj.get(FmoConstants.COUNTRY_CD)):"";
			if(country.equals(FmoConstants.US_COUNTRY_CD))	{
				if(data.getKeyFieldName().contains(FmoConstants.BEID_ID_COUNTRY_CRITERIA) 
						|| data.getKeyFieldName().contains(FmoConstants.ACCESS_RC_RATEID_CRITERIA) ) {
					return this.fetchLineItemIdOnBeId(data,keyPathMap,nxOutputBean,siteObj,paramMap);
				}else if(data.getKeyFieldName().contains(FmoConstants.STATE_COUNTRY_CRITERIA)){
					return this.fetchLineItemOnStateAndCountry(data,keyPathMap,nxOutputBean,siteObj,paramMap);
				}else if(data.getKeyFieldName().contains(FmoConstants.ETHERNET_ACCESS_CRITERIA) 
						|| data.getKeyFieldName().contains(FmoConstants.DS_ACCESS_CRITERIA)) {
					return this.fetchLineItemOnDesignLevel(data, keyPathMap, nxOutputBean, siteObj,paramMap);
				}
			}else {
				if(data.getKeyFieldName().contains(FmoConstants.RATE_ID_COUNTRY_CRITERIA)){
					return this.fetchLineItemIdByRateIdOnSiteLevel(data,keyPathMap,nxOutputBean,siteObj,paramMap);
				}else if(data.getKeyFieldName().contains(FmoConstants.INTL_ACCESS_CRITERIA) 
						|| data.getKeyFieldName().contains(FmoConstants.DS_ACCESS_CRITERIA)) {
					return this.fetchLineItemOnDesignLevel(data, keyPathMap, nxOutputBean, siteObj,paramMap);
				}else if(data.getKeyFieldName().contains(FmoConstants.COUNTRY_CRITERIA)) {
					return this.fetchLineItemOnStateAndCountry(data,keyPathMap,nxOutputBean,siteObj,paramMap);
				}
			} 
		}
		return null;
	}
	
	
	
	/**
	 * Output file generation MIS flow.
	 *
	 * @param nxOutputBean the nx output bean
	 * @param keyPathMap the key path map
	 * @param siteObj the site obj
	 * @param data the data
	 */
	protected List<NxLineItemLookUpDataModel> outputFileGenerationMisFlow(NxOutputBean nxOutputBean,Map<String, String> keyPathMap,
			JSONObject siteObj, NxLineItemLookUpFieldModel data,Map<String, Object> paramMap) {
		if(data.getKeyFieldName().contains(FmoConstants.ETHERNET_ACCESS_CRITERIA) 
				|| data.getKeyFieldName().contains(FmoConstants.DS_ACCESS_CRITERIA)) {
			return this.fetchLineItemOnDesignLevel(data, keyPathMap, nxOutputBean, siteObj,paramMap);
		}else {
			return this.fetchLineItemIdByRateIdDesignLevel(data,keyPathMap,nxOutputBean,siteObj,paramMap);
		}
	}
	
	
	/**
	 * Output file generation bvoip flow.
	 *
	 * @param nxOutputBean the nx output bean
	 * @param keyPathMap the key path map
	 * @param siteObj the site obj
	 * @param data the data
	 */
	protected List<NxLineItemLookUpDataModel> outputFileGenerationBvoipFlow(NxOutputBean nxOutputBean,Map<String, String> keyPathMap,
			JSONObject siteObj, NxLineItemLookUpFieldModel data,Map<String, Object> paramMap) {
		String country=null!=siteObj.get(FmoConstants.COUNTRY_CD)?
				String.valueOf(siteObj.get(FmoConstants.COUNTRY_CD)):"";
		if(country.equals(FmoConstants.US_COUNTRY_CD))	{
			if(data.getKeyFieldName().contains(FmoConstants.RCRATEID_CON_CALLTYPE_CRITERIA) ||
					data.getKeyFieldName().contains(FmoConstants.RCRATEID_HAS_VOICEDNA_CRITERIA) ){
				return this.fetchLineItemIdByRateIdDesignLevel(data, keyPathMap, nxOutputBean, siteObj,paramMap);
			}else {
				return this.fetchLineItemIdByRateIdOnSiteLevel(data,keyPathMap,nxOutputBean,siteObj,paramMap);
			}
		}else {
			return this.fetchLineItemIdByRateIdOnSiteLevel(data,keyPathMap,nxOutputBean,siteObj,paramMap);
		}
		
	}
	
	
	/**
	 * Fetch line item id on be id.
	 *
	 * @param data the data
	 * @param keyPath the key path
	 * @param nxOutputBean the nx output bean
	 * @param siteObj the site obj
	 */
	@SuppressWarnings("unchecked")
	protected List<NxLineItemLookUpDataModel> fetchLineItemIdOnBeId(NxLineItemLookUpFieldModel data,Map<String,String> keyPath,
			NxOutputBean nxOutputBean,JSONObject siteObj,Map<String, Object> paramMap) {
		Set<NxKeyFieldPathModel> keyFieldMapping=data.getKeyFieldMapping();
		
		//Map which hold the mapping for MRC and NRC source
		Map<String,Map<String,String>>  nrcMrcSrcMap=(Map<String,Map<String,String>>) 
				nexusJsonUtility.convertStringJsonToMap(data.getMrcNrcSourceMap());
		String siteId=null!=siteObj.get(FmoConstants.SITE_ID)?
				String.valueOf(siteObj.get(FmoConstants.SITE_ID)):null;
		//constructing query only using MRC beId
		String queryClause=constructQuery(siteObj,keyFieldMapping,data.getKeyFieldCondition());
		if(StringUtils.isNotEmpty(queryClause)) {
			JSONArray designLst=(JSONArray)siteObj.get(FmoConstants.DESIGN_ATR);
			
			//if MRC_NRC source map contain source as table then get the column name and pass to query
			String mrcBeidSrc=getColumnNameFromMap(nrcMrcSrcMap, FmoConstants.MRC_BEID_SOURCE);
			String nrcBeidSrc=getColumnNameFromMap(nrcMrcSrcMap, FmoConstants.NRC_BEID_SOURCE);
			List<NxLineItemLookUpDataModel> resultLst=repositoryService.getLineItemData
					(queryClause,mrcBeidSrc,nrcBeidSrc,FmoConstants.FMO);
			//Set<String> allMrcBeids=this.getDataForFalloutProcessing(siteObj,keyFieldMapping,FmoConstants.BEID);
			String offerName=data.getOfferName();
			String falloutKey=offerName+"_"+siteId;
			setDataForFalloutProcessingSiteLevel(queryClause,falloutKey,paramMap);
			//iterate the line item data and separate out the MRC and NRc priceDetails block for output generation
			Optional.ofNullable(resultLst).map(List::stream).orElse(Stream.empty()).filter(Objects::nonNull).
			forEach( lineItem -> {
				Map<String, LinkedHashMap<String, String>> priceDetailsMap = getMrcNrcDataMapByBeid(keyPath,nrcMrcSrcMap,
						designLst, lineItem);
				
				LinkedHashMap<String, String> mrcPriceDetailsBlock=priceDetailsMap.get(FmoConstants.MRC)!=null?
						priceDetailsMap.get(FmoConstants.MRC):new LinkedHashMap<>();
				LinkedHashMap<String, String> nrcPriceDetailsBlock=priceDetailsMap.get(FmoConstants.NRC)!=null?
						priceDetailsMap.get(FmoConstants.NRC):new LinkedHashMap<>();
						
				//MRC NRC priceDetails block send to output generation process		
				this.createNexusOutput(keyPath,nxOutputBean,siteObj,designLst,lineItem,mrcPriceDetailsBlock,
						nrcPriceDetailsBlock);
				//in this method separate out fallout BEID from all MRC BEID
				//Fallout BEID means which has no Line Item data
				//this.collectFalloutBeid(allMrcBeids,mrcPriceDetailsBlock);
				processSiteLevelFallout(falloutKey,paramMap);
			});
			//this.setFalloutDataMap(FmoConstants.BEID, allMrcBeids);
			return resultLst;
		}
		
		return null;
	}
	
	/**
	 * In this method separate out FallOut BEID from all MRC BEID
		FallOut BEID means which has no Line Item data
		
	 * Collect FallOut BEID from all MRC BEID.
	 *
	 * @param allMrcBeids the all MRC BEID
	 * @param mrcPriceDtlsBlock the MRC price details block
	 * 
	 */
	protected void collectFalloutBeid(Set<String> allMrcBeids,
			LinkedHashMap<String,String> mrcPriceDtlsBlock) {
		String lineItemMatchBeid=mrcPriceDtlsBlock.get(FmoConstants.BEID);
		if(CollectionUtils.isNotEmpty(allMrcBeids) && StringUtils.isNotEmpty(lineItemMatchBeid)) {
			allMrcBeids.removeIf(s -> s.equals(lineItemMatchBeid)); 
		}
		
	}
	
	
	
	
	/**
	 * Gets the all MRC BEID.
	 *
	 * @param siteObj the site obj
	 * @param keyFieldMapping the key field mapping
	 * @param key the key
	 * @return the all MRC BEID
	 */
	/*protected Set<String> getDataForFalloutProcessing(JSONObject siteObj,Set<NxKeyFieldPathModel> keyFieldMapping,String key){
		//collect all mrc beid for processing fallout
		Set<String> data=new HashSet<>();
		if(CollectionUtils.isNotEmpty(keyFieldMapping)) {
			for(NxKeyFieldPathModel x:keyFieldMapping) {
				if(null!= siteObj && StringUtils.isNotEmpty(x.getKeyFieldName()) && 
						x.getKeyFieldName().equals(FmoConstants.RC_RATE_ID)) {
	    			List<Object> dataList=nexusJsonUtility.getValueLst(siteObj,x.getJsonPath());
	    			data.addAll( Optional.ofNullable(dataList).map(List::stream).orElse(Stream.empty()).filter(Objects::nonNull)
	    					   .map(object -> Objects.toString(object, null))
	    					   .collect(Collectors.toSet()));
	    		}
			}
		}
		if(falloutDataMap.containsKey(key)) {
			data.addAll(falloutDataMap.get(key));
		}
		
		return data;
	}*/

	/**
	 * Gets the mrc nrc data map by beid.
	 *
	 * @param keyPath the key path
	 * @param nrcMrcSrcMap the nrc mrc src map
	 * @param designLst the design lst
	 * @param lineItem the line item
	 * @return the mrc nrc data map by beid
	 */
	protected Map<String, LinkedHashMap<String, String>> getMrcNrcDataMapByBeid(Map<String, String> keyPath,
			Map<String, Map<String, String>> nrcMrcSrcMap, JSONArray designLst, NxLineItemLookUpDataModel lineItem) {
		//collect the MRC and NRC beId
		List<String> beIdLst=this.getBeIdList(lineItem,nrcMrcSrcMap,designLst,keyPath);
		//get priceDetails from intermediate JSON design block on basis of BeId
		
		List<Object> priceDetails =nexusJsonUtility.getValueLst(designLst,
				nexusJsonUtility.getJsonPath(keyPath.get(FmoConstants.PRICE_DETAILS_BY_MULTIPLE_BEIDS),beIdLst));
		
		//According to MRC and NRC BeId get the MRC and NRC price details block from intermediate JSON
		return this.getMrcNrcPriceDetails(priceDetails);
	}
	
	
	/**
	 * Gets the be id list.
	 *
	 * @param lookupdata the lookupdata
	 * @param nrcMrcSrcMap the nrc mrc src map
	 * @param designLst the design lst
	 * @param keyPath the key path
	 * @return This method used to collected MRC and NRC BEID
	 */
	protected List<String> getBeIdList(NxLineItemLookUpDataModel lookupdata,Map<String,Map<String,String>> nrcMrcSrcMap,
			JSONArray designLst,Map<String,String> keyPath){
		List<String> beIdLst=new ArrayList<>();
		
		//collecting MRC related BEID in list
		//if its not find in table then get it from intermediate JSON using JSON path(nx_key_field_path)
		if(StringUtils.isNotEmpty(lookupdata.getIdetityField1()) && 
				!lookupdata.getIdetityField1().equals(FmoConstants.NOT_AVAILABLE)) {
			beIdLst.add(lookupdata.getIdetityField1());
		}else if(null!=nrcMrcSrcMap.get(FmoConstants.MRC_BEID_SOURCE) 
				&& null!=nrcMrcSrcMap.get(FmoConstants.MRC_BEID_SOURCE).get(FmoConstants.LOOKUP)){
			beIdLst.add(String.valueOf(nexusJsonUtility.getValue(designLst, 
					keyPath.get(nrcMrcSrcMap.get(FmoConstants.MRC_BEID_SOURCE).get(FmoConstants.LOOKUP)))));
		}
		
		//collecting NRC related BEID in list
		//if its not find in table then get it from intermediate JSON using JSON path(nx_key_field_path) 
		if(StringUtils.isNotEmpty(lookupdata.getIdetityField2()) && 
				!lookupdata.getIdetityField2().equals(FmoConstants.NOT_AVAILABLE)) {
			beIdLst.add(lookupdata.getIdetityField2());
		}else if(null!=nrcMrcSrcMap.get(FmoConstants.NRC_BEID_SOURCE) 
				&& null!=nrcMrcSrcMap.get(FmoConstants.NRC_BEID_SOURCE).get(FmoConstants.LOOKUP)){
			beIdLst.add(String.valueOf(nexusJsonUtility.getValue(designLst,
					keyPath.get(nrcMrcSrcMap.get(FmoConstants.NRC_BEID_SOURCE).get(FmoConstants.LOOKUP)))));
		}
		return beIdLst;
	}
	
	
	/**
	 * Fetch line item id by rate id on site level.
	 *
	 * @param data the data
	 * @param keyPath the key path
	 * @param nxOutputBean the nx output bean
	 * @param siteObj the site obj
	 */
	@SuppressWarnings("unchecked")
	protected List<NxLineItemLookUpDataModel> fetchLineItemIdByRateIdOnSiteLevel(NxLineItemLookUpFieldModel data,Map<String,String> keyPath,
			NxOutputBean nxOutputBean,JSONObject siteObj,Map<String, Object> paramMap) {
		Set<NxKeyFieldPathModel> keyFieldMapping=data.getKeyFieldMapping();
		//Map which hold the mapping for MRC and NRC source
		Map<String,Map<String,String>>  rateIdSrcMap=(Map<String,Map<String,String>>) 
				nexusJsonUtility.convertStringJsonToMap(data.getMrcNrcSourceMap());
		String siteId=null!=siteObj.get(FmoConstants.SITE_ID)?
				String.valueOf(siteObj.get(FmoConstants.SITE_ID)):null;
		//constructing query only using MRC beId
		String queryClause=constructQuery(siteObj,keyFieldMapping,data.getKeyFieldCondition());
		if(StringUtils.isNotEmpty(queryClause)) {
			JSONArray designLst=(JSONArray)siteObj.get(FmoConstants.DESIGN_ATR);
			String mrcRateIdSrc=getColumnNameFromMap(rateIdSrcMap,FmoConstants.MRC_RATEID_SOURCE);
			String nrcRateIdSrc=getColumnNameFromMap(rateIdSrcMap, FmoConstants.NRC_RATEID_SOURCE);
			//all MRC product rate id
			//<String> allRcRateId=getDataForFalloutProcessing(siteObj, keyFieldMapping,FmoConstants.RC_RATE_ID);
			String offerName=data.getOfferName();
			String falloutKey=offerName+"_"+siteId;
			setDataForFalloutProcessingSiteLevel(queryClause,falloutKey,paramMap);
			List<NxLineItemLookUpDataModel> resultLst=repositoryService.getLineItemData
					(queryClause,mrcRateIdSrc,nrcRateIdSrc,FmoConstants.FMO);
			Optional.ofNullable(resultLst).map(List::stream).orElse(Stream.empty()).filter(Objects::nonNull).
			forEach( lineItem -> {
				Map<String, LinkedHashMap<String, String>> priceDetailsMap = getMrcNrcDataMapByRateId(keyPath,rateIdSrcMap,
						designLst, lineItem);
				LinkedHashMap<String, String> mrcPriceDetailsBlock=priceDetailsMap.get(FmoConstants.MRC)!=null?
						priceDetailsMap.get(FmoConstants.MRC):new LinkedHashMap<>();
				LinkedHashMap<String, String> nrcPriceDetailsBlock=priceDetailsMap.get(FmoConstants.NRC)!=null?
						priceDetailsMap.get(FmoConstants.NRC):new LinkedHashMap<>();
				//MRC NRC priceDetails block send to output generation process		
				this.createNexusOutput(keyPath,nxOutputBean,siteObj,designLst,lineItem,mrcPriceDetailsBlock,
						nrcPriceDetailsBlock);
				//In this method separate out fallout product rate id from all product rate id
				//Fallout product rate id means which has no Line Item data
				//this.collectFalloutProdRateId(allRcRateId,mrcPriceDetailsBlock,data.getOfferName());
				processSiteLevelFallout(falloutKey,paramMap);
			});
			//allRcRateId.removeAll(succeedRcRateId);
			//this.setFalloutDataMap(FmoConstants.RC_RATE_ID, allRcRateId);
			return resultLst;
		}
		return null;
	}
		
	
	
	/**
	 * Fetch line item id on rate id.
	 *
	 * @param data the data
	 * @param keyPath the key path
	 * @param nxOutputBean the nx output bean
	 * @param siteObj the site obj
	 */
	@SuppressWarnings("unchecked")
	protected List<NxLineItemLookUpDataModel> fetchLineItemIdByRateIdDesignLevel(NxLineItemLookUpFieldModel data,Map<String,String> keyPath,
			NxOutputBean nxOutputBean,JSONObject siteObj,Map<String, Object> paramMap) {
		Set<NxKeyFieldPathModel> keyFieldMapping=data.getKeyFieldMapping();
		//Map which hold the mapping for MRC and NRC source
		Map<String,Map<String,String>>  rateIdSrcMap=(Map<String,Map<String,String>>) 
				nexusJsonUtility.convertStringJsonToMap(data.getMrcNrcSourceMap());
		String siteId=null!=siteObj.get(FmoConstants.SITE_ID)?
				String.valueOf(siteObj.get(FmoConstants.SITE_ID)):null;
		JSONArray designLst=(JSONArray)siteObj.get(FmoConstants.DESIGN_ATR);
		if(null!=designLst) {
			for(Object designs:designLst) {
				JSONObject designObj = (JSONObject) designs;
			    designObj.put(FmoConstants.COUNTRY_CD, siteObj.get(FmoConstants.COUNTRY_CD));
				String queryClause=constructQuery(designObj,keyFieldMapping,data.getKeyFieldCondition());
				if(StringUtils.isNotEmpty(queryClause)) {
					String mrcRateIdSrc=getColumnNameFromMap(rateIdSrcMap,FmoConstants.MRC_RATEID_SOURCE);
					String nrcRateIdSrc=getColumnNameFromMap(rateIdSrcMap, FmoConstants.NRC_RATEID_SOURCE);
					//all MRC product rate id
					//Set<String> allRcRateId=getDataForFalloutProcessing(siteObj, keyFieldMapping,FmoConstants.RC_RATE_ID);
					String offerName=data.getOfferName();
					String falloutKey=offerName+"_"+siteId;
					setDataForFalloutProcessingSiteLevel(queryClause,falloutKey,paramMap);
					List<NxLineItemLookUpDataModel> resultLst=repositoryService.getLineItemData
							(queryClause,mrcRateIdSrc,nrcRateIdSrc,FmoConstants.FMO);
					Optional.ofNullable(resultLst).map(List::stream).orElse(Stream.empty()).filter(Objects::nonNull).
					forEach( lineItem -> {
						Map<String, LinkedHashMap<String, String>> priceDetailsMap = getMrcNrcDataMapByRateId(keyPath,rateIdSrcMap,
								designLst, lineItem);
						LinkedHashMap<String, String> mrcPriceDetailsBlock=priceDetailsMap.get(FmoConstants.MRC)!=null?
								priceDetailsMap.get(FmoConstants.MRC):new LinkedHashMap<>();
						LinkedHashMap<String, String> nrcPriceDetailsBlock=priceDetailsMap.get(FmoConstants.NRC)!=null?
								priceDetailsMap.get(FmoConstants.NRC):new LinkedHashMap<>();
						JSONArray tempDesignLst=new JSONArray();
						tempDesignLst.add(designObj);				
						//MRC NRC priceDetails block send to output generation process		
						this.createNexusOutput(keyPath,nxOutputBean,siteObj,tempDesignLst,lineItem,mrcPriceDetailsBlock,
								nrcPriceDetailsBlock);
						//In this method separate out fallout product rate id from all product rate id
						//Fallout product rate id means which has no Line Item data
						//this.collectFalloutProdRateId(allRcRateId,mrcPriceDetailsBlock,data.getOfferName());
						
						processSiteLevelFallout(falloutKey,paramMap);
					});
					//allRcRateId.removeAll(succeedRcRateId);
					//this.setFalloutDataMap(FmoConstants.RC_RATE_ID, allRcRateId);
					return resultLst;
				}
			 
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	protected void setDataForFalloutProcessingSiteLevel(String queryCriteria,String falloutKey,Map<String, Object> paramMap){
		if(!paramMap.containsKey(falloutKey) || !(boolean)paramMap.get(falloutKey)) {
			Map<String, Set<String>> falloutMap=(Map<String, Set<String>>) paramMap.get(FmoConstants.FALLOUT_MAP);
			if(!falloutMap.containsKey(falloutKey)) {
				falloutMap.put(falloutKey, new HashSet<String>());
			}
			((HashSet<String>)falloutMap.get(falloutKey)).add(queryCriteria);
		}
		
	}
	@SuppressWarnings("unchecked")
	protected void processSiteLevelFallout(String falloutKey,Map<String, Object> paramMap) {
		Map<String, Set<String>> falloutMap=(Map<String, Set<String>>) paramMap.get(FmoConstants.FALLOUT_MAP);
		falloutMap.remove(falloutKey);
		paramMap.put(falloutKey,true);
	}
	
	/**
	 * Gets the rate ids list.
	 *
	 * @param lookupdata the lookupdata
	 * @param nrcMrcSrcMap the nrc mrc src map
	 * @param designLst the design lst
	 * @param keyPath the key path
	 * @return the rate ids list
	 */
	protected List<String> getRateIdsList(NxLineItemLookUpDataModel lookupdata,Map<String,Map<String,String>> nrcMrcSrcMap,
			JSONArray designLst,Map<String,String> keyPath){
		Set<String> data=new HashSet<>();
		//collecting MRC related RateId in list
		//if its not find in table then get it from intermediate JSON using JSON path(nx_key_field_path)
		if(StringUtils.isNotEmpty(lookupdata.getIdetityField1()) && 
				!lookupdata.getIdetityField1().equals(FmoConstants.NOT_AVAILABLE)) {
			data.add(lookupdata.getIdetityField1());
		}else if(null!=nrcMrcSrcMap.get(FmoConstants.MRC_RATEID_SOURCE) 
				&& null!=nrcMrcSrcMap.get(FmoConstants.MRC_RATEID_SOURCE).get(FmoConstants.LOOKUP)){
			data.add(String.valueOf(nexusJsonUtility.getValue(designLst, 
					keyPath.get(nrcMrcSrcMap.get(FmoConstants.MRC_RATEID_SOURCE).get(FmoConstants.LOOKUP)))));
		}
		
		//collecting NRC related RateId in list
		//if its not find in table then get it from intermediate JSON using JSON path(nx_key_field_path) 
		if(StringUtils.isNotEmpty(lookupdata.getIdetityField2()) && 
				!lookupdata.getIdetityField2().equals(FmoConstants.NOT_AVAILABLE)) {
			data.add(lookupdata.getIdetityField2());
		}else if(null!=nrcMrcSrcMap.get(FmoConstants.NRC_RATEID_SOURCE) 
				&& null!=nrcMrcSrcMap.get(FmoConstants.NRC_RATEID_SOURCE).get(FmoConstants.LOOKUP)){
			data.add(String.valueOf(nexusJsonUtility.getValue(designLst,
					keyPath.get(nrcMrcSrcMap.get(FmoConstants.NRC_RATEID_SOURCE).get(FmoConstants.LOOKUP)))));
		}
		return new ArrayList<>(data);
	}
	
	/**
	 * Collect fallout prod rate id.
	 *
	 * @param allRcRateId the all rc rate id
	 * @param mrcPriceDtlsBlock the mrc price dtls block
	 * @param offerName the offer name
	 */
	/*protected void collectFalloutProdRateId(Set<String> allRcRateId,
			LinkedHashMap<String,String> mrcPriceDtlsBlock,String offerName) {
		
		if(offerName.equals(FmoConstants.ADI)) {
			String lineItemMatchProdRateId=null!=mrcPriceDtlsBlock.get(FmoConstants.PRODUCT_RATE_ID)?
					String.valueOf(mrcPriceDtlsBlock.get(FmoConstants.PRODUCT_RATE_ID)):null;
			succeedRcRateId.add(lineItemMatchProdRateId);
		}else {
			String lineItemMatchProdRateId=null!=mrcPriceDtlsBlock.get(FmoConstants.PRODUCT_RATE_ID)?
					String.valueOf(mrcPriceDtlsBlock.get(FmoConstants.PRODUCT_RATE_ID)):null;
			succeedRcRateId.add(lineItemMatchProdRateId);
		}
	}*/
	
	
	
	
	/**
	 * Gets the mrc nrc data map by rate id.
	 *
	 * @param keyPath the key path
	 * @param nrcMrcSrcMap the nrc mrc src map
	 * @param designLst the design lst
	 * @param lineItem the line item
	 * @return the mrc nrc data map by rate id
	 */
	protected Map<String, LinkedHashMap<String, String>> getMrcNrcDataMapByRateId(Map<String, String> keyPath,
			Map<String, Map<String, String>> nrcMrcSrcMap, JSONArray designLst, NxLineItemLookUpDataModel lineItem) {
		List<String> rateId=this.getRateIdsList(lineItem, nrcMrcSrcMap, designLst, keyPath);
		List<Object> priceDetails =nexusJsonUtility.getValueLst(designLst,
				nexusJsonUtility.getJsonPath(keyPath.get(FmoConstants.PRICE_DETAILS_BY_MULTIPLE_RATE_IDS),
						rateId));
		return this.getMrcNrcPriceDetails(priceDetails);
	}
	
	
	/**
	 * Gets the mrc nrc price details.
	 *
	 * @param priceDetails the price details
	 * @return the mrc nrc price details
	 */
	@SuppressWarnings("unchecked")
	protected Map<String,LinkedHashMap<String, String>> getMrcNrcPriceDetails(List<Object> priceDetails){
		Map<String,LinkedHashMap<String, String>> resultMap=new HashMap<>();
		//Separate the MRC and NRC priceDetails block and put in map
		Optional.ofNullable(priceDetails).map(List::stream).orElse(Stream.empty()).filter(Objects::nonNull).
		forEach( pricedata -> {
			LinkedHashMap<String, String> data=(LinkedHashMap<String, String>) pricedata;
			if(StringUtils.isNotEmpty(data.get(FmoConstants.FREQUENCY)) &&
					data.get(FmoConstants.FREQUENCY).equals(FmoConstants.MRC) ) {
				resultMap.put(FmoConstants.MRC, data);
			}
			if(StringUtils.isNotEmpty(data.get(FmoConstants.FREQUENCY)) &&
					data.get(FmoConstants.FREQUENCY).equals(FmoConstants.NRC) ) {
				resultMap.put(FmoConstants.NRC, data);
			}
		});
		return resultMap;
		
	}
	
	/**
	 * Get column name from map.
	 *
	 * @param inputDataMap the input data map
	 * @param key the key
	 * @return the string
	 */
	protected String getColumnNameFromMap(Map<String,Map<String,String>>  inputDataMap,String key) {
		//get only column name from source map
		if(MapUtils.isNotEmpty(inputDataMap) && inputDataMap.get(key)!=null && null!=inputDataMap.get(key).get(FmoConstants.COLUMN)) {
			return inputDataMap.get(key).get(FmoConstants.COLUMN);
		}
		return null;
	}
	
	
	/**
	 * Fetch line item on state and country.
	 *
	 * @param data the data
	 * @param keyPath the key path
	 * @param nxOutputBean the nx output bean
	 * @param siteObj the site obj
	 */
	protected List<NxLineItemLookUpDataModel> fetchLineItemOnStateAndCountry(NxLineItemLookUpFieldModel data,
			Map<String,String> keyPath,NxOutputBean nxOutputBean,JSONObject siteObj,Map<String, Object> paramMap) {
		Set<NxKeyFieldPathModel> keyFieldMapping=data.getKeyFieldMapping();
		String siteId=null!=siteObj.get(FmoConstants.SITE_ID)?
				String.valueOf(siteObj.get(FmoConstants.SITE_ID)):null;
		String queryClause=constructQuery(siteObj,keyFieldMapping,data.getKeyFieldCondition());
		if(StringUtils.isNotEmpty(queryClause)) {
			String offerName=data.getOfferName();
			String falloutKey=offerName+"_"+siteId;
			setDataForFalloutProcessingSiteLevel(queryClause,falloutKey,paramMap);
			List<NxLineItemLookUpDataModel> result=
					repositoryService.getLineItemData(queryClause,FmoConstants.FMO);
			if(CollectionUtils.isNotEmpty(result) && null!=result.get(0)) {
				NxLineItemLookUpDataModel lineItem=result.get(0);
				processSiteLevelFallout(falloutKey,paramMap);
				this.createNexusOutput(keyPath, nxOutputBean, siteObj, null,lineItem,
						new LinkedHashMap<>(),new LinkedHashMap<>());
			}
			return result;
		}
		
		return null;
	}
	
	/**
	 * Fetch line item on design level.
	 *
	 * @param data the data
	 * @param keyPath the key path
	 * @param nxOutputBean the nx output bean
	 * @param siteObj the site obj
	 */
	@SuppressWarnings("unchecked")
	protected List<NxLineItemLookUpDataModel> fetchLineItemOnDesignLevel(NxLineItemLookUpFieldModel data,
			Map<String,String> keyPath,NxOutputBean nxOutputBean,JSONObject siteObj,Map<String, Object> paramMap) {
		Set<NxKeyFieldPathModel> keyFieldMapping=data.getKeyFieldMapping();
		JSONArray designLst=(JSONArray)siteObj.get(FmoConstants.DESIGN_ATR);
		String siteId=null!=siteObj.get(FmoConstants.SITE_ID)?
				String.valueOf(siteObj.get(FmoConstants.SITE_ID)):null;
		if(null!=designLst) {
			for(Object x:designLst) {
		    	JSONObject designObj = (JSONObject) x;
		    	String countryCd=null!=siteObj.get(FmoConstants.COUNTRY_CD)?
						String.valueOf(siteObj.get(FmoConstants.COUNTRY_CD)):null;
		    	designObj.put(FmoConstants.COUNTRY_CD,countryCd );
		    	designObj.put(FmoConstants.CURRENCY_CD, this.getCurrencyByCountryCd(countryCd));
		    	String queryClause=constructQuery(designObj,keyFieldMapping,data.getKeyFieldCondition());
				if(StringUtils.isNotEmpty(queryClause)) {
					String offerName=data.getOfferName();
					String falloutKey=offerName+"_"+siteId;
					setDataForFalloutProcessingSiteLevel(queryClause,falloutKey,paramMap);
					List<NxLineItemLookUpDataModel> result=
							repositoryService.getLineItemData(queryClause,FmoConstants.FMO);
					if(CollectionUtils.isNotEmpty(result) && null!=result.get(0)) {
						NxLineItemLookUpDataModel lineItem=result.get(0);
						JSONArray tempDesignLst=new JSONArray();
						tempDesignLst.add(designObj);
						processSiteLevelFallout(falloutKey,paramMap);
						this.createNexusOutput(keyPath,nxOutputBean,siteObj,tempDesignLst,lineItem,
								new LinkedHashMap<>(),new LinkedHashMap<>());
					}
					return result;
				}
				
			}
		}
		return null;
		
	}
	
	
	
	/**
	 * Gets the currency by country cd.
	 *
	 * @param countryCd the country cd
	 * @return the currency by country cd
	 */
	protected String getCurrencyByCountryCd(String countryCd) {
		NxLookupData nxLookupData=repositoryService.getLookupDataById(FmoConstants.COUNTRY_CURRENCY_MAPPING,
				countryCd);
		if(null!=nxLookupData) {
			return nxLookupData.getDescription();
		}
		return null;
	}

	/**
	 * Creates the nexus output.
	 *
	 * @param keyPath the key path
	 * @param nxOutputBean the nx output bean
	 * @param siteObj the site obj
	 * @param designLst the design lst
	 * @param lineItem the line item
	 * @param mrcPriceDetailsBlock the mrc price details block
	 * @param nrcPriceDetailsBlock the nrc price details block
	 */
	protected void createNexusOutput(Map<String, String> keyPath, NxOutputBean nxOutputBean,
			JSONObject siteObj, JSONArray designLst, NxLineItemLookUpDataModel lineItem,
			Map<String, String> mrcPriceDetailsBlock,Map<String, String> nrcPriceDetailsBlock) {
		if(null!=lineItem.getNexusOutputMapping() && StringUtils.isNotEmpty(lineItem.getNexusOutputMapping().getTabName())) {
			if(lineItem.getNexusOutputMapping().getTabName().equals(OutputFileConstants.AVPN_TAB)) {
				NxAvpnOutputBean data=this.createAvpnOutput(siteObj,mrcPriceDetailsBlock,nrcPriceDetailsBlock,designLst,keyPath);
				this.collectNxOutputData(nxOutputBean.getNxAvpnOutput(),data,lineItem);
			}else if(lineItem.getNexusOutputMapping().getTabName().equals(OutputFileConstants.AVPN_INTERNATIONAL_TAB)){
				NxAvpnIntAccessOutputBean data=this.createAvpnIntlOutput(siteObj,designLst,keyPath);
				this.collectNxOutputData(nxOutputBean.getNxAvpnIntlOutputBean(),data,lineItem);
			}else if(lineItem.getNexusOutputMapping().getTabName().equals(OutputFileConstants.MIS_DS1_ACCESS_TAB)) {
				NxDsAccessBean data=this.createMISDS1AccessOutputBean(siteObj,designLst,keyPath,lineItem.getOfferId());
				this.collectNxOutputData(nxOutputBean.getNxMisDS1AccessBean(),data,lineItem);
			}else if(lineItem.getNexusOutputMapping().getTabName().equals(OutputFileConstants.AVPN_DS0DS1_ACCESS_TAB)){
				NxDsAccessBean data=this.createAvpnDS0DS1AccessOutputBean(siteObj,designLst,keyPath,lineItem.getOfferId());
				this.collectNxOutputData(nxOutputBean.getNxAvpnDS0DS1AccessBean(),data,lineItem);
			}else if(lineItem.getNexusOutputMapping().getTabName().equals(OutputFileConstants.DS3_ACCESS_TAB)) {
				NxDsAccessBean data=this.createDS3AccessOutputBean(siteObj,designLst,keyPath,lineItem.getOfferId());
				this.collectNxOutputData(nxOutputBean.getNxDs3AccessBean(),data,lineItem);
			}else if(lineItem.getNexusOutputMapping().getTabName().equals(OutputFileConstants.ETHERNET_ACCESS_TAB)) {
				NxEthernetAccessOutputBean	data=this.createEthernetAccessOutput(siteObj,designLst,keyPath,lineItem.getOfferId());
				this.collectNxOutputData(nxOutputBean.getNxEthernetAccOutputBean(),data,lineItem);
			}else if(lineItem.getNexusOutputMapping().getTabName().equals(OutputFileConstants.MIS_TAB)) {
				NxMisBean data=this.createMisOutput(siteObj,mrcPriceDetailsBlock,nrcPriceDetailsBlock);
				this.collectNxOutputData(nxOutputBean.getNxAdiMisBean(),data,lineItem);
			}else if(lineItem.getNexusOutputMapping().getTabName().equals(OutputFileConstants.BVOIP_TAB)) {
				NxBvoipOutputBean data=this.createBvoipOutput(siteObj,designLst,mrcPriceDetailsBlock,nrcPriceDetailsBlock,keyPath);
				this.collectNxOutputData(nxOutputBean.getNxBvoipOutputBean(),data,lineItem);
			}	
		}
	}
	
	
	
	/**
	 * Collect nx output data.
	 *
	 * @param <T> the generic type
	 * @param inputLst the input lst
	 * @param data the data
	 * @param lineItem the line item
	 */
	protected <T extends NxBaseOutputBean> void collectNxOutputData(List<T> inputLst,T data,NxLineItemLookUpDataModel lineItem) {
		data.setType(FmoConstants.FMO);
		data.setLineItemId(lineItem.getLineItemId()!=null?
				String.valueOf(lineItem.getLineItemId()):null);
		data.setSecondaryKey(lineItem.getSecondaryKey()!=null?
				lineItem.getSecondaryKey():null);
		data.setLittleProdId(lineItem.getLittleProdId()!=null?
				String.valueOf(lineItem.getLittleProdId()):null);
		data.setTopProdId(lineItem.getTopProdId()!=null?
				String.valueOf(lineItem.getTopProdId()):null);
		inputLst.add(data);
	}
	
	/**
	 * Construct query.
	 *
	 * @param inputObj the input obj
	 * @param keyFieldMapping the key field mapping
	 * @param filterCriteriaMapping the filter criteria mapping
	 * @return the string
	 */
	protected String constructQuery(JSONObject inputObj,Set<NxKeyFieldPathModel> keyFieldMapping,
			String filterCriteriaMapping) {
		StringBuilder finalQuery=new StringBuilder();
		if(CollectionUtils.isNotEmpty(keyFieldMapping)) {
			for(NxKeyFieldPathModel x:keyFieldMapping) {
				String inputValue=this.getInputValue(inputObj,x);
				if(StringUtils.isNotEmpty(inputValue)) {
					if(finalQuery.length()==0) {
						finalQuery.append(x.getFieldName()).append(" ").append(inputValue);
					}else {
						finalQuery.append(" and ")
						.append(x.getFieldName()).append(" ").append(inputValue);
					}
				}else {
					return null;
				}
			}
		}
		
		return finalQuery.toString();
	}
	
	/**
	 * Gets the input value.
	 *
	 * @param inputObj the input obj
	 * @param input the input
	 * @return the input value
	 */
	protected String getInputValue(Object inputObj,NxKeyFieldPathModel input) {
		if(StringUtils.isNotEmpty(input.getJsonPath())) {
			String resultdata=getDataUsingJsonPath(inputObj, input.getJsonPath(), input);
			if(StringUtils.isNotEmpty(resultdata) && !("null".equals(resultdata) ||"'null'".equals(resultdata))) {
				return " in (" + resultdata + ")";
			}else {
				return null;
			}
			
		}else{
			return input.getDefaultValue();
		}
	}
	
	protected String getDataUsingJsonPath(Object inputObj,String jsonPath,NxKeyFieldPathModel input) {
		String resultdata=nexusJsonUtility.convertListToCsvWithQuote(
				nexusJsonUtility.getValueLst(inputObj,input.getJsonPath()));
		if(StringUtils.isEmpty(resultdata) && StringUtils.isNotEmpty(input.getDefaultValue())) {
			return nexusJsonUtility.convertListToCsvWithQuote(Arrays.asList(input.getDefaultValue()));
		}
		return resultdata;
	}
	
	
	
	/**
	 * Filter criteria.
	 *
	 * @param filterCriteriaMapping the filter criteria mapping
	 * @return the string
	 */
	@SuppressWarnings("unchecked")
	protected String filterCriteria(String filterCriteriaMapping) {
		StringBuilder fliterCriteria=new StringBuilder();
		Map<String,List<String>>  resultMap=(Map<String, List<String>>) 
				nexusJsonUtility.convertStringJsonToMap(filterCriteriaMapping);
		resultMap.entrySet().stream().forEach(x-> {
			String inputValue=nexusJsonUtility.convertListToCsvWithQuote(x.getValue());
			fliterCriteria.append(" and ")
			.append(x.getKey()).append(" in ").append("(" + inputValue + ")");
		});
		return fliterCriteria.toString();
	}
	
	
	
	
	/**
	 * Creates the avpn output.
	 *
	 * @param siteObj the site obj
	 * @param mrcPriceObj the mrc price obj
	 * @param nrcPriceObj the nrc price obj
	 * @param designLst the design lst
	 * @param keyPath the key path
	 * @return the nx avpn output bean
	 */
	protected NxAvpnOutputBean createAvpnOutput(JSONObject siteObj,
			Map<String, String> mrcPriceObj,Map<String, String> nrcPriceObj,JSONArray designLst,
			Map<String,String> keyPath) {
		NxAvpnOutputBean bean=new NxAvpnOutputBean();
		bean.setProductType(FmoConstants.AVPN);
		bean.setSiteId(null!=siteObj.get(FmoConstants.SITE_ID)?
				String.valueOf(siteObj.get(FmoConstants.SITE_ID)):null);
		bean.setSiteName(null!=siteObj.get(FmoConstants.SITE_NAME)?
				String.valueOf(siteObj.get(FmoConstants.SITE_NAME)):null);
		bean.setState(null!=siteObj.get(FmoConstants.STATE)?
				String.valueOf(siteObj.get(FmoConstants.STATE)):null);
		bean.setCity(null!=siteObj.get(FmoConstants.CITY)?
				String.valueOf(siteObj.get(FmoConstants.CITY)):null);
		bean.setCountry(null!=siteObj.get(FmoConstants.COUNTRY_CD)?
				String.valueOf(siteObj.get(FmoConstants.COUNTRY_CD)):null);
		if(null!=designLst) {
			Object portSpeed=nexusJsonUtility.getValue(designLst,
					nexusJsonUtility.getJsonPath(keyPath.get(FmoConstants.PORT_SPEED),
							mrcPriceObj.get(FmoConstants.REF_PORT_ID)));
			
			bean.setPortSpeed(null!=portSpeed?String.valueOf(portSpeed):null);
			
			Object portProtocol=nexusJsonUtility.getValue(designLst,
					nexusJsonUtility.getJsonPath(keyPath.get(FmoConstants.PROTOCOL_BY_PORTID), 
							mrcPriceObj.get(FmoConstants.REF_PORT_ID)));
			bean.setPortProtocol(null!=portProtocol?String.valueOf(portProtocol):null);
		}
		bean.setCurrency(null!=mrcPriceObj.get(FmoConstants.LOCAL_CURRENCY)?
				String.valueOf(mrcPriceObj.get(FmoConstants.LOCAL_CURRENCY)):null);
		bean.setFmoMrcQuantity(null!=mrcPriceObj.get(FmoConstants.QUANTITY)?
				String.valueOf(mrcPriceObj.get(FmoConstants.QUANTITY)):null);
		bean.setMrcDiscount(null!=mrcPriceObj.get(FmoConstants.ICB_DESIRED_DISCOUNT)?
				String.valueOf(mrcPriceObj.get(FmoConstants.ICB_DESIRED_DISCOUNT)):null);
		bean.setNrcDiscount(null!=nrcPriceObj.get(FmoConstants.ICB_DESIRED_DISCOUNT)?
				String.valueOf(nrcPriceObj.get(FmoConstants.ICB_DESIRED_DISCOUNT)):null);
		bean.setTerm(null!=mrcPriceObj.get(FmoConstants.TERM)?
				String.valueOf(mrcPriceObj.get(FmoConstants.TERM)):null);
		return bean;
	}
	
	
	
	
	/**
	 * Creates the avpn intl output.
	 *
	 * @param siteObj the site obj
	 * @param designLst the design lst
	 * @param keyPath the key path
	 * @return the nx avpn int access output bean
	 */
	protected NxAvpnIntAccessOutputBean createAvpnIntlOutput(JSONObject siteObj,
			JSONArray designLst,Map<String,String> keyPath) {
		NxAvpnIntAccessOutputBean bean=new NxAvpnIntAccessOutputBean();
		bean.setProductType(FmoConstants.ACCESS);
		bean.setSiteId(null!=siteObj.get(FmoConstants.SITE_ID)?
				String.valueOf(siteObj.get(FmoConstants.SITE_ID)):null);
		bean.setCountry(null!=siteObj.get(FmoConstants.COUNTRY_CD)?
				String.valueOf(siteObj.get(FmoConstants.COUNTRY_CD)):null);
		bean.setSiteAliase(null!=siteObj.get(FmoConstants.SITE_NAME)?
				String.valueOf(siteObj.get(FmoConstants.SITE_NAME)):null);
		bean.setCity(null!=siteObj.get(FmoConstants.CITY)?
				String.valueOf(siteObj.get(FmoConstants.CITY)):null);
		bean.setPostalCd(null!=siteObj.get(FmoConstants.POSTAL_CD)?
				String.valueOf(siteObj.get(FmoConstants.POSTAL_CD)):null);
		bean.setAddress(null!=siteObj.get(FmoConstants.ADDRESS)?
				String.valueOf(siteObj.get(FmoConstants.ADDRESS)):null);
		//not required
		bean.setExistingTelcoCircuitId("");
		bean.setExistingTelcoProvider("");
		bean.setAccessPrimaryOrBackup("Primary");
		
		if(null!=designLst) {
			Object accessPopCill=nexusJsonUtility.getValue(designLst,keyPath.get(FmoConstants.POP_CILLI));
			bean.setAccessPopCilli(null!=accessPopCill?String.valueOf(accessPopCill):null);
			Object iglooId=nexusJsonUtility.getValue(designLst,keyPath.get(FmoConstants.DESIGN_LEVEL_DQID));
			bean.setIglooId(null!=iglooId?String.valueOf(iglooId):null);
			Object speed=nexusJsonUtility.getValue(designLst,keyPath.get(FmoConstants.DESIGN_LEVEL_SPEED));
			bean.setAccessBandwidth(null!=speed?String.valueOf(speed).concat(" Kbps"):null);
			Object accessTailTechnology=nexusJsonUtility.getValue(designLst,keyPath.get(FmoConstants.DESIGN_LEVEL_ACEES_TAIL_TECH));
			bean.setAccessTailTechnology(null!=accessTailTechnology?String.valueOf(accessTailTechnology):
				FmoConstants.DEFAULT_ACCESS_TAIL_TECH);
			Object term=nexusJsonUtility.getValue(designLst,keyPath.get(FmoConstants.DESIGN_LEVEL_TERM));
			bean.setTerm(null!=term?String.valueOf(term):null);
			Object currency=nexusJsonUtility.getValue(designLst,keyPath.get(FmoConstants.DESIGN_LEVEL_CURRENCY));
			bean.setCurrency(null!=currency?String.valueOf(currency):null);
			Object accessSpeedUdfAttrIdObj=nexusJsonUtility.getValue(designLst,keyPath.get(FmoConstants.ACCESS_SPEED_UDF_ATTR_ID));
			String accessSpeedUdfAttrId=null!=accessSpeedUdfAttrIdObj?String.valueOf(accessSpeedUdfAttrIdObj):null;
			bean.setFmoMrcQuantity(this.getFmoQuntityByAccessSpeed(accessSpeedUdfAttrId));
		}
		
		
		return bean;
	}
	
	/**
	 * Gets the fmo quntity by access speed.
	 *
	 * @param accessSpeed the access speed
	 * @return the fmo quntity by access speed
	 */
	protected String getFmoQuntityByAccessSpeed(String accessSpeed) {
		NxLookupData nxLookupData=repositoryService.getLookupDataById(FmoConstants.DEFAULT_FMO_QUANTITY,
				accessSpeed);
		if(null!=nxLookupData) {
			return nxLookupData.getDescription();
		}
		return "1";
	}
	

	/**
	 * Creates the MISDS 1 access output bean.
	 *
	 * @param siteObj the site obj
	 * @param designLst the design lst
	 * @param keyPath the key path
	 * @param offerId the offer id
	 * @return the nx ds access bean
	 */
	protected NxDsAccessBean createMISDS1AccessOutputBean(JSONObject siteObj,
			JSONArray designLst,Map<String,String> keyPath,Long offerId) {
		NxDsAccessBean bean=new NxDsAccessBean();
		bean.setProductType(FmoConstants.ACCESS);
		bean.setRelatedProduct(FmoConstants.OFFER_ID_MAPPING.get(offerId));
		if(null!=designLst) {
			Object npaNxx=nexusJsonUtility.getValue(designLst,keyPath.get(FmoConstants.DESIGN_LEVEL_NPANXX));
			bean.setNpaNxx(null!=npaNxx?String.valueOf(npaNxx):null);
			Object accessSpeed=nexusJsonUtility.getValue(designLst,keyPath.get(FmoConstants.DESIGN_LEVEL_ACCESS_SPEED));
			bean.setSpeed(null!=accessSpeed?String.valueOf(accessSpeed):null);
			Object mileage=nexusJsonUtility.getValue(designLst,keyPath.get(FmoConstants.DESIGN_LEVEL_MILEAGE));
			bean.setMileage(null!=mileage?String.valueOf(mileage):null);
			Object term=nexusJsonUtility.getValue(designLst,keyPath.get(FmoConstants.DESIGN_LEVEL_TERM));
			bean.setTerm(null!=term?String.valueOf(term):null);
			Object accessSpeedUdfAttrIdObj=nexusJsonUtility.getValue(designLst,keyPath.get(FmoConstants.ACCESS_SPEED_UDF_ATTR_ID));
			String accessSpeedUdfAttrId=null!=accessSpeedUdfAttrIdObj?String.valueOf(accessSpeedUdfAttrIdObj):null;
			bean.setFmoMrcQuantity(this.getFmoQuntityByAccessSpeed(accessSpeedUdfAttrId));
		}
		
		bean.setCustSrvgWireCtrCLLICd(null!=siteObj.get(FmoConstants.SWC_CLLI)?
				String.valueOf(siteObj.get(FmoConstants.SWC_CLLI)):null);
		return bean;
	}
	
	
	
	/**
	 * Creates the avpn DS 0 DS 1 access output bean.
	 *
	 * @param siteObj the site obj
	 * @param designLst the design lst
	 * @param keyPath the key path
	 * @param offerId the offer id
	 * @return the nx ds access bean
	 */
	protected NxDsAccessBean createAvpnDS0DS1AccessOutputBean(JSONObject siteObj,
			JSONArray designLst,Map<String,String> keyPath,Long offerId) {
		NxDsAccessBean bean=new NxDsAccessBean();
		bean.setProductType(FmoConstants.ACCESS);
		bean.setRelatedProduct(FmoConstants.OFFER_ID_MAPPING.get(offerId));
		if(null!=designLst) {
			Object npaNxx=nexusJsonUtility.getValue(designLst,keyPath.get(FmoConstants.DESIGN_LEVEL_NPANXX));
			bean.setNpaNxx(null!=npaNxx?String.valueOf(npaNxx):null);
			Object accessSpeed=nexusJsonUtility.getValue(designLst,keyPath.get(FmoConstants.DESIGN_LEVEL_ACCESS_SPEED));
			bean.setSpeed(null!=accessSpeed?String.valueOf(accessSpeed):null);
			Object mileage=nexusJsonUtility.getValue(designLst,keyPath.get(FmoConstants.DESIGN_LEVEL_MILEAGE));
			bean.setMileage(null!=mileage?String.valueOf(mileage):null);
			bean.setMileage(null!=mileage?String.valueOf(mileage):null);
			Object term=nexusJsonUtility.getValue(designLst,keyPath.get(FmoConstants.DESIGN_LEVEL_TERM));
			bean.setTerm(null!=term?String.valueOf(term):null);
			Object accessSpeedUdfAttrIdObj=nexusJsonUtility.getValue(designLst,keyPath.get(FmoConstants.ACCESS_SPEED_UDF_ATTR_ID));
			String accessSpeedUdfAttrId=null!=accessSpeedUdfAttrIdObj?String.valueOf(accessSpeedUdfAttrIdObj):null;
			bean.setFmoMrcQuantity(this.getFmoQuntityByAccessSpeed(accessSpeedUdfAttrId));
		}
		bean.setCustSrvgWireCtrCLLICd(null!=siteObj.get(FmoConstants.SWC_CLLI)?
				String.valueOf(siteObj.get(FmoConstants.SWC_CLLI)):null);
		return bean;
	}
	
	
	
	
	/**
	 * Creates the DS 3 access output bean.
	 *
	 * @param siteObj the site obj
	 * @param designLst the design lst
	 * @param keyPath the key path
	 * @param offerId the offer id
	 * @return the nx ds access bean
	 */
	protected NxDsAccessBean createDS3AccessOutputBean(JSONObject siteObj,
			JSONArray designLst,Map<String,String> keyPath,Long offerId) {
		NxDsAccessBean bean=new NxDsAccessBean();
		bean.setProductType(FmoConstants.ACCESS);
		bean.setRelatedProduct(FmoConstants.OFFER_ID_MAPPING.get(offerId));
		if(null!=designLst) {
			Object npaNxx=nexusJsonUtility.getValue(designLst,keyPath.get(FmoConstants.DESIGN_LEVEL_NPANXX));
			bean.setNpaNxx(null!=npaNxx?String.valueOf(npaNxx):null);
			Object accessSpeed=nexusJsonUtility.getValue(designLst,keyPath.get(FmoConstants.DESIGN_LEVEL_ACCESS_SPEED));
			bean.setSpeed(null!=accessSpeed?String.valueOf(accessSpeed):null);
			Object mileage=nexusJsonUtility.getValue(designLst,keyPath.get(FmoConstants.DESIGN_LEVEL_MILEAGE));
			bean.setMileage(null!=mileage?String.valueOf(mileage):null);
			Object term=nexusJsonUtility.getValue(designLst,keyPath.get(FmoConstants.DESIGN_LEVEL_TERM));
			bean.setTerm(null!=term?String.valueOf(term):null);
			Object accessSpeedUdfAttrIdObj=nexusJsonUtility.getValue(designLst,keyPath.get(FmoConstants.ACCESS_SPEED_UDF_ATTR_ID));
			String accessSpeedUdfAttrId=null!=accessSpeedUdfAttrIdObj?String.valueOf(accessSpeedUdfAttrIdObj):null;
			bean.setFmoMrcQuantity(this.getFmoQuntityByAccessSpeed(accessSpeedUdfAttrId));
			Object currency=nexusJsonUtility.getValue(designLst,keyPath.get(FmoConstants.DESIGN_LEVEL_CURRENCY));
			bean.setCurrency(null!=currency?String.valueOf(currency):null);
		}
		bean.setCustSrvgWireCtrCLLICd(null!=siteObj.get(FmoConstants.SWC_CLLI)?
				String.valueOf(siteObj.get(FmoConstants.SWC_CLLI)):null);
		bean.setAddress(null!=siteObj.get(FmoConstants.ADDRESS)?
				String.valueOf(siteObj.get(FmoConstants.ADDRESS)):null);
		bean.setCity(null!=siteObj.get(FmoConstants.CITY)?
				String.valueOf(siteObj.get(FmoConstants.CITY)):null);
		bean.setState(null!=siteObj.get(FmoConstants.STATE)?
				String.valueOf(siteObj.get(FmoConstants.STATE)):null);
		return bean;
	}
	
	
	
	
	
	/**
	 * Creates the ethernet access output.
	 *
	 * @param siteObj the site obj
	 * @param designLst the design lst
	 * @param keyPath the key path
	 * @param offerId the offer id
	 * @return the nx ethernet access output bean
	 */
	protected NxEthernetAccessOutputBean createEthernetAccessOutput(JSONObject siteObj,
			JSONArray designLst,Map<String,String> keyPath,Long offerId) {
		NxEthernetAccessOutputBean bean=new NxEthernetAccessOutputBean();
		bean.setProductType(FmoConstants.ETHERNET_ACCESS);
		bean.setSiteName(null!=siteObj.get(FmoConstants.SITE_NAME)?
				String.valueOf(siteObj.get(FmoConstants.SITE_NAME)):null);
		if(null!=designLst) {
			Object accessArchitecture=nexusJsonUtility.getValue(designLst,keyPath.get(FmoConstants.DESIGN_ACCESS_ARCHITECTURE));
			bean.setAccessArchitecture(null!=accessArchitecture?String.valueOf(accessArchitecture):null);
			Object accessSpeed=nexusJsonUtility.getValue(designLst,keyPath.get(FmoConstants.ACCESS_SPEED));
			bean.setSpeed(null!=accessSpeed?String.valueOf(accessSpeed):null);
			Object term=nexusJsonUtility.getValue(designLst,keyPath.get(FmoConstants.DESIGN_LEVEL_TERM));
			bean.setTerm(null!=term?String.valueOf(term):null);
			Object interfaceType=nexusJsonUtility.getValue(designLst,keyPath.get(FmoConstants.INTERFACE_TYPE));
			bean.setInterfaceType(null!=interfaceType?String.valueOf(interfaceType):null);
			Object ethernetPopCill=nexusJsonUtility.getValue(designLst,keyPath.get(FmoConstants.POP_CILLI));
			bean.setEthernetPopCilli(null!=ethernetPopCill?String.valueOf(ethernetPopCill):null);
			Object alternateProvider=nexusJsonUtility.getValue(designLst,keyPath.get(FmoConstants.SUPPLIER_NAME));
			bean.setAlternateProvider(null!=alternateProvider?String.valueOf(alternateProvider):null);
		}
		bean.setIlecSWCCilli(null!=siteObj.get(FmoConstants.SWC_CLLI)?
				String.valueOf(siteObj.get(FmoConstants.SWC_CLLI)):null);
		bean.setAccociatedService(FmoConstants.OFFER_ID_MAPPING.get(offerId));
		bean.setPremisesCode(null!=siteObj.get(FmoConstants.CUSTOMER_LOC_CLLI)?
				String.valueOf(siteObj.get(FmoConstants.CUSTOMER_LOC_CLLI)):null);
		bean.setFmoMrcQuantity("1");
	
		return bean;
	}
	
	
	
	/**
	 * Creates the adi mis output.
	 *
	 * @param siteObj the site obj
	 * @param mrcPriceObj the mrc price obj
	 * @param nrcPriceObj the nrc price obj
	 * @return the nx adi mis bean
	 */
	protected NxMisBean createMisOutput(JSONObject siteObj,
			Map<String,String> mrcPriceObj,	Map<String,String> nrcPriceObj) {
		NxMisBean bean=new NxMisBean();
		bean.setProductType(FmoConstants.MIS);
		bean.setState(null!=siteObj.get(FmoConstants.STATE)?
				String.valueOf(siteObj.get(FmoConstants.STATE)):null);
		bean.setFmoMrcQuantity(null!=mrcPriceObj.get(FmoConstants.QUANTITY)?
				String.valueOf(mrcPriceObj.get(FmoConstants.QUANTITY)):null);
		bean.setLocation(OutputBeanUtil.getLocation(bean.getState()));
		bean.setCurrentMrc(null!=mrcPriceObj.get(FmoConstants.LOCAL_LST_PRICE)?
				String.valueOf(mrcPriceObj.get(FmoConstants.LOCAL_LST_PRICE)):null);
		bean.setMrcDiscount(null!=mrcPriceObj.get(FmoConstants.ICB_DESIRED_DISCOUNT)?
				String.valueOf(mrcPriceObj.get(FmoConstants.ICB_DESIRED_DISCOUNT)):null);
		bean.setNrcDiscount(null!=nrcPriceObj.get(FmoConstants.ICB_DESIRED_DISCOUNT)?
				String.valueOf(nrcPriceObj.get(FmoConstants.ICB_DESIRED_DISCOUNT)):null);
		bean.setTerm(null!=mrcPriceObj.get(FmoConstants.TERM)?
				String.valueOf(mrcPriceObj.get(FmoConstants.TERM)):null);
		
		return bean;
	}
	
	/**
	 * Creates the bvoip output.
	 *
	 * @param siteObj the site obj
	 * @param designLst the design lst
	 * @param mrcPriceObj the mrc price obj
	 * @param nrcPriceObj the nrc price obj
	 * @param keyPath the key path
	 * @return the nx bvoip output bean
	 */
	protected NxBvoipOutputBean createBvoipOutput(JSONObject siteObj,JSONArray designLst,
			Map<String,String> mrcPriceObj,	Map<String,String> nrcPriceObj,Map<String,String> keyPath) {
		NxBvoipOutputBean bean=new NxBvoipOutputBean();
		bean.setProductType(FmoConstants.BVOIP);
		bean.setCountry(null!=siteObj.get(FmoConstants.COUNTRY_CD)?
				String.valueOf(siteObj.get(FmoConstants.COUNTRY_CD)):null);
		if(null!=designLst) {
			Object concurrentCallType=nexusJsonUtility.getValue(designLst,keyPath.get(FmoConstants.DESIGN_LEVEL_CALL_TYPE));
			bean.setConcurrentCallType(null!=concurrentCallType?String.valueOf(concurrentCallType):null);
		}
		if(MapUtils.isNotEmpty(mrcPriceObj)) {
			bean.setQuantity(null!=mrcPriceObj.get(FmoConstants.QUANTITY)?
				String.valueOf(mrcPriceObj.get(FmoConstants.QUANTITY)):null);
			bean.setCurrentMrc(null!=mrcPriceObj.get(FmoConstants.LOCAL_LST_PRICE)?
					String.valueOf(mrcPriceObj.get(FmoConstants.LOCAL_LST_PRICE)):null);
			bean.setMrcDiscount(null!=mrcPriceObj.get(FmoConstants.ICB_DESIRED_DISCOUNT)?
					String.valueOf(mrcPriceObj.get(FmoConstants.ICB_DESIRED_DISCOUNT)):null);
			bean.setTerm(null!=mrcPriceObj.get(FmoConstants.TERM)?
					String.valueOf(mrcPriceObj.get(FmoConstants.TERM)):null);
		}else if(MapUtils.isNotEmpty(nrcPriceObj)) {
			bean.setQuantity(null!=nrcPriceObj.get(FmoConstants.QUANTITY)?
					String.valueOf(nrcPriceObj.get(FmoConstants.QUANTITY)):null);
			bean.setNrcDiscount(null!=nrcPriceObj.get(FmoConstants.ICB_DESIRED_DISCOUNT)?
					String.valueOf(nrcPriceObj.get(FmoConstants.ICB_DESIRED_DISCOUNT)):null);
			bean.setTerm(null!=nrcPriceObj.get(FmoConstants.TERM)?
					String.valueOf(nrcPriceObj.get(FmoConstants.TERM)):null);
		}
		return bean;
	}
	
	/*@SuppressWarnings("unchecked")
	protected List<NxLineItemLookUpDataModel> fetchLineItemIdOnBeIdForMpOld(NxLineItemLookUpFieldModel data,Map<String,String> keyPath,
			NxOutputBean nxOutputBean,JSONObject siteObj,Map<String, Object> paramMap) {
		Set<NxKeyFieldPathModel> keyFieldMapping=data.getKeyFieldMapping();
		
		//Map which hold the mapping for MRC and NRC source
		Map<String,Map<String,String>>  nrcMrcSrcMap=(Map<String,Map<String,String>>) 
				nexusJsonUtility.convertStringJsonToMap(data.getMrcNrcSourceMap());
		Set<String> beidFromLookup=paramMap.containsKey(FmoConstants.AVPN_LOOKUP_BEID_FOR_FALLOUT)?
				(HashSet<String>)paramMap.get(FmoConstants.AVPN_LOOKUP_BEID_FOR_FALLOUT):null;
		//constructing query only using MRC beId
		String queryClause=constructQuery(siteObj,keyFieldMapping,data.getKeyFieldCondition());
		if(StringUtils.isNotEmpty(queryClause)) {
			JSONArray designLst=(JSONArray)siteObj.get(FmoConstants.DESIGN_ATR);
			
			//if MRC_NRC source map contain source as table then get the column name and pass to query
			String mrcBeidSrc=getColumnNameFromMap(nrcMrcSrcMap, FmoConstants.MRC_BEID_SOURCE);
			String nrcBeidSrc=getColumnNameFromMap(nrcMrcSrcMap, FmoConstants.NRC_BEID_SOURCE);
			List<NxLineItemLookUpDataModel> resultLst=repositoryService.getLineItemData
					(queryClause,mrcBeidSrc,nrcBeidSrc,FmoConstants.MYPRICE_FMO);
			Set<String> allMrcNrcBeids=this.getDataForFalloutProcessingForMp(siteObj,keyFieldMapping,FmoConstants.BEID);
			//iterate the line item data and separate out the MRC and NRc priceDetails block for output generation
			Optional.ofNullable(resultLst).map(List::stream).orElse(Stream.empty()).filter(Objects::nonNull).
			forEach( lineItem -> {
				Map<String, LinkedHashMap<String, String>> priceDetailsMap = getMrcNrcDataMapByBeid(keyPath,nrcMrcSrcMap,
						designLst, lineItem);
				LinkedHashMap<String, String> mrcPriceDetailsBlock=priceDetailsMap.get(FmoConstants.MRC)!=null?
						priceDetailsMap.get(FmoConstants.MRC):new LinkedHashMap<>();
				LinkedHashMap<String, String> nrcPriceDetailsBlock=priceDetailsMap.get(FmoConstants.NRC)!=null?
						priceDetailsMap.get(FmoConstants.NRC):new LinkedHashMap<>();
						
				//MRC NRC priceDetails block send to output generation process		
				//this.createNexusOutput(keyPath,nxOutputBean,siteObj,designLst,lineItem,mrcPriceDetailsBlock,
						//nrcPriceDetailsBlock);
				
				this.collectFalloutBeidForMp(allMrcNrcBeids,mrcPriceDetailsBlock,nrcPriceDetailsBlock);
			});
			this.processFalloutForAvpn(allMrcNrcBeids, beidFromLookup);
			this.setFalloutDataMap(FmoConstants.BEID, allMrcNrcBeids);
			return resultLst;
		}
		
		return null;
	}
	
	protected Set<String> getDataForFalloutProcessingForMp(JSONObject siteObj,Set<NxKeyFieldPathModel> keyFieldMapping,
			String key){
		//collect all mrc nrc beid for processing fallout
		Set<String> data=new HashSet<>();
		if(CollectionUtils.isNotEmpty(keyFieldMapping)) {
			for(NxKeyFieldPathModel x:keyFieldMapping) {
				if(null!= siteObj && StringUtils.isNotEmpty(x.getKeyFieldName()) && 
						(x.getKeyFieldName().equals(FmoConstants.RC_RATE_ID) || x.getKeyFieldName().equals(FmoConstants.NRC_RATE_ID))) {
	    			List<Object> dataList=nexusJsonUtility.getValueLst(siteObj,x.getJsonPath());
	    			data.addAll( Optional.ofNullable(dataList).map(List::stream).orElse(Stream.empty()).filter(Objects::nonNull)
	    					   .map(object -> Objects.toString(object, null))
	    					   .collect(Collectors.toSet()));
	    		}
			}
		}
		if(falloutDataMap.containsKey(key)) {
			data.addAll(falloutDataMap.get(key));
		}
		
		return data;
	}*/
	
	@SuppressWarnings("unchecked")
	protected List<NxLineItemLookUpDataModel> fetchLineItemIdOnBeIdForMp(NxLineItemLookUpFieldModel data,Map<String,String> keyPath,
			NxOutputBean nxOutputBean,JSONObject siteObj,Map<String, Object> paramMap) {
		Set<NxKeyFieldPathModel> keyFieldMapping=data.getKeyFieldMapping();
		
		//Map which hold the mapping for MRC and NRC source
		Map<String,Map<String,String>>  nrcMrcSrcMap=(Map<String,Map<String,String>>) 
				nexusJsonUtility.convertStringJsonToMap(data.getMrcNrcSourceMap());
		String siteId=null!=siteObj.get(FmoConstants.SITE_ID)?
						String.valueOf(siteObj.get(FmoConstants.SITE_ID)):null;			
		//constructing query only using MRC beId
		String queryClause=constructQuery(siteObj,keyFieldMapping,data.getKeyFieldCondition());
		if(StringUtils.isNotEmpty(queryClause)) {
			String offerName=data.getOfferName();
			String falloutKey=offerName+"_"+siteId;
			setDataForFalloutProcessingSiteLevel(queryClause,falloutKey,paramMap);
			//if MRC_NRC source map contain source as table then get the column name and pass to query
			String mrcBeidSrc=getColumnNameFromMap(nrcMrcSrcMap, FmoConstants.MRC_BEID_SOURCE);
			String nrcBeidSrc=getColumnNameFromMap(nrcMrcSrcMap, FmoConstants.NRC_BEID_SOURCE);
			List<NxLineItemLookUpDataModel> resultLst=repositoryService.getLineItemData
					(queryClause,mrcBeidSrc,nrcBeidSrc,FmoConstants.FMO);
			//iterate the line item data and separate out the MRC and NRc priceDetails block for output generation
			Optional.ofNullable(resultLst).map(List::stream).orElse(Stream.empty()).filter(Objects::nonNull).
			forEach( lineItem -> {
				processSiteLevelFallout(falloutKey,paramMap);
			});
			return resultLst;
		}
		
		return null;
	}
	
	
	/*protected void collectFalloutBeidForMp(Set<String> allMrcNrcBeids,
			LinkedHashMap<String,String> mrcPriceDtlsBlock,LinkedHashMap<String, String> nrcPriceDetailsBlock,Map<String, Object> paramMap) {
		String lineItemMatchMrcBeid=mrcPriceDtlsBlock.get(FmoConstants.BEID);
		String lineItemMatchNrcBeid=nrcPriceDetailsBlock.get(FmoConstants.BEID);
		if(StringUtils.isNotEmpty(lineItemMatchMrcBeid)) { 
			succeedBeId.add(lineItemMatchMrcBeid);
		}
		if(StringUtils.isNotEmpty(lineItemMatchNrcBeid)) {
			succeedBeId.add(lineItemMatchNrcBeid);
		}
		
	}
	
	protected void processFalloutForAvpn(Set<String> falloutBeid,Set<String> beidFromLookup) {
		if(CollectionUtils.isNotEmpty(beidFromLookup)) {
			succeedBeId.addAll(beidFromLookup);
		}
		if(CollectionUtils.isNotEmpty(falloutBeid)) {
			falloutBeid.removeAll(succeedBeId);
		}
	}
	
	protected void setLookupDataForFallout(Long offerId,Map<String, Object> paramMap){
		String offerName = nxMpRepositoryService.getOfferNameByOfferId(offerId.intValue());
		Set<String> data=new HashSet<String>();
		if(offerName.equalsIgnoreCase(FmoConstants.AVPN)) {
			List<NxLookupData> looupdata=repositoryService.getDataFromNxLooUpByDatasetName(FmoConstants.AVPN_LOOKUP_BEID_DATASET);
			data=Optional.ofNullable(looupdata).map(List::stream).orElse(Stream.empty()).
					filter(Objects::nonNull).map(rd->rd.getItemId()).collect(Collectors.toSet());
			paramMap.put(FmoConstants.AVPN_LOOKUP_BEID_FOR_FALLOUT, data);
		}
	}*/
	

	protected void generateDesignLevelNxKey(List<NxLineItemLookUpDataModel> lineItemLst,Map<String,ArrayList<String>> portwiseMap,
			JSONObject siteObj) {
		Optional.ofNullable(lineItemLst).map(List::stream).orElse(Stream.empty()).filter(Objects::nonNull).
		forEach( lineItem -> {
			Long nxItemId=lineItem.getNxItemId();
			List<String> portIdList=getPortIdByNxItemId(nxItemId, siteObj);
			if(CollectionUtils.isNotEmpty(portIdList)) {
				for(String portId:portIdList) {
					if(!portwiseMap.containsKey(portId)) {
						portwiseMap.put(portId, new ArrayList<String>());
					}
					portwiseMap.get(portId).add(nxItemId.toString());
				}
			}
		});
	}
	
	protected List<String> getPortIdByNxItemId(Long nxItemId,JSONObject siteObj) {
		ArrayList<String> data=new ArrayList<String>();
		//getting portId/component id using nxItemId  from priceDetails block
		List<Object> result=nexusJsonUtility.getValueLst(siteObj,"$..[?('"+nxItemId+"' in @['priceDetails'][*]['nxItemId'])].portId");
		if(CollectionUtils.isNotEmpty(result)) {
			for(Object o:result) {
				data.add(o.toString());
			}
		}
		return data;
	}
	
	
	@SuppressWarnings("unchecked")
	protected void setNxKey(Long offerId,JSONObject siteObj, Map<String, ArrayList<String>> portwiseKeyMap,Map<String, Object> paramMap) {
		String country=null!=siteObj.get(FmoConstants.COUNTRY_CD)?
				String.valueOf(siteObj.get(FmoConstants.COUNTRY_CD)):"";
		String offerName = null;
		if (null!=offerId) {
			offerName = nxMpRepositoryService.getOfferNameByOfferId(offerId.intValue());
		}
		offerName = configAndUpdateProcessingFmoService.getBvoipOfferName(offerName);
		if(MyPriceConstants.BVoIP.equals(offerName)) {
			siteObj.put("nxKeyId", country);
		}else {
			for (Map.Entry<String,ArrayList<String>> x : portwiseKeyMap.entrySet()) {
				String portId=x.getKey();
				List<String> designLevelKey=x.getValue();
				
				Collections.sort(designLevelKey);
				if(StringUtils.isNotEmpty(country)) {
					designLevelKey.add(country);
				}
				String accessType=this.getAccessType(offerName, portId, siteObj, paramMap);
				if(StringUtils.isNotEmpty(accessType)) {
					designLevelKey.add(this.getAccessType(offerName,portId,siteObj,paramMap));
				}
				String key= String.join("$", designLevelKey);
				String setPath="$..design.[?(@.portId=="+portId+")].nxKeyId";
				fmoMpOutputJsonHelper.insertOrUpdateData(siteObj, setPath, key, "field", "nxKeyId");
			}
		}
		
	}
	
	protected String getAccessType(String offerName ,String portId,JSONObject siteObj,Map<String, Object> paramMap) {
		JsonNode siteElement=JacksonUtil.jsonNodeFromObj(siteObj);
		Object designByPortId=nexusJsonUtility.getValue(siteObj, "$..design.[?(@.portId=="+portId+")]");
		if(null!=designByPortId) {
			JsonNode designElement=JacksonUtil.jsonNodeFromObj(designByPortId);
			ObjectNode newSite = (ObjectNode) siteElement.deepCopy();
			newSite.remove("design");
			newSite.withArray("design").add(designElement);
			return configAndUpdateProcessingFmoService.getAccessType(newSite,offerName,paramMap);
		}
		return null;
	}
	
	
	
}
