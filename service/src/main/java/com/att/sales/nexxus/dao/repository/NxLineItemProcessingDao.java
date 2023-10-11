package com.att.sales.nexxus.dao.repository;

import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.att.sales.nexxus.constant.FmoConstants;
import com.att.sales.nexxus.dao.model.FmoOfferJsonRulesMapping;
import com.att.sales.nexxus.dao.model.NxKeyFieldPathModel;
import com.att.sales.nexxus.dao.model.NxLineItemLookUpDataModel;
import com.att.sales.nexxus.dao.model.NxLineItemLookUpFieldModel;
import com.att.sales.nexxus.dao.model.NxLookupData;

/**
 * The Class NxLineItemProcessingDao.
 *
 * @author vt393d
 * 
 * This class used to access data layer
 */
@Repository(value="nxLineItemProcessingDao")
public class NxLineItemProcessingDao {

	

	/** The em. */
	@PersistenceContext
	private EntityManager em;
	
	/** The Constant FLOW_TYPE. */
	private  static final String FLOW_TYPE="flowType";
	
	
	
	/**
	 * Gets the fmo rules.
	 *
	 * @param offerId the offer id
	 * @return the fmo rules
	 */
	@SuppressWarnings("unchecked")
	public List<FmoOfferJsonRulesMapping> getFmoRules(Set<Long> offerId){
		List<FmoOfferJsonRulesMapping> resultLst=null;
		String hqlQuery="select fo FROM FmoOfferJsonRulesMapping  fo JOIN fo.fmoRules f"
				+ " WHERE fo.jsonRuleId=f.id and fo.offerId in (:offerId) and fo.active='Y'";
		Query query=em.createQuery(hqlQuery);
		query.setParameter(FmoConstants.OFFER_ID, offerId);
		resultLst = query.getResultList();
		return resultLst;
	}
	
	
	/**
	 * Gets the Nexxus line item look up items.
	 *
	 * @param offerId the offer id
	 * @param inputType the input type
	 * @return This method is used to fetch lookItem data for filtering line item
	 */
	@SuppressWarnings("unchecked")
	public List<NxLineItemLookUpFieldModel> getNxLineItemFieldDataByOfferId(Long offerId,String inputType){
		 List<NxLineItemLookUpFieldModel> resultLst=null;
		 String hqlQuery="from NxLineItemLookUpFieldModel where offerId=:offerId and "
		 		+ " inputType=:inputType and active='Y'";
		Query query=em.createQuery(hqlQuery);
		query.setParameter(FmoConstants.OFFER_ID, offerId);
		query.setParameter("inputType", inputType);
		resultLst = query.getResultList();
		return resultLst;
	}
	
	/**
	 * Gets the nx line item field data by offer name.
	 *
	 * @param offerName the offer name
	 * @param inputType the input type
	 * @return the nx line item field data by offer name
	 */
	@SuppressWarnings("unchecked")
	public List<NxLineItemLookUpFieldModel> getNxLineItemFieldDataByOfferName(String offerName,String inputType){
		 List<NxLineItemLookUpFieldModel> resultLst=null;
		 String hqlQuery="from NxLineItemLookUpFieldModel where offerName=:offerName and "
		 		+ " inputType=:inputType and active='Y'";
		Query query=em.createQuery(hqlQuery);
		query.setParameter("offerName", offerName);
		query.setParameter("inputType", inputType);
		resultLst = query.getResultList();
		return resultLst;
	}
	
	
	
	/**
	 * Gets the list item data.
	 *
	 * @param condition the condition
	 * @param flowType the flow type
	 * @return the list item data
	 */
	@SuppressWarnings("unchecked")
	public List<NxLineItemLookUpDataModel> getLineItemData(String condition,String flowType ) {
		List<NxLineItemLookUpDataModel> resultLst=null;
		String queryString= new StringBuilder().append("select * from NX_LINE_ITEM_LOOKUP_DATA where flow_type=:flowType and ACTIVE_YN=:activeFlag and "
				+ "").append(condition).toString();
		Query query=em.createNativeQuery(queryString,NxLineItemLookUpDataModel.class); //NOSONAR
		query.setParameter(FLOW_TYPE, flowType);
		query.setParameter("activeFlag", FmoConstants.ACTIVE_FLAG);
		resultLst = query.getResultList();
		return resultLst;
		
	}
	
	
	/*@SuppressWarnings("unchecked")
	public List<NxLineItemLookUpDataModel> getLineItemData(String condition,String resultCol1,
			String resultCol2,String flowType) {
		List<NxLineItemLookUpDataModel> lineItemLst=null;
		String queryString= new StringBuilder().append("select "
				+ "	nx.LINE_ITEM_ID as lineItemId , nx.SECONDARY_KEY as secondaryKey,"
				+ "	nx.LITTLE_PRODUCT_ID as littleProdId,nx.TOP_PRODUCT_ID as topProdId,"
				+ " nx.offer_id as offerId,nx.FIELD19_VALUE as field19Value,nx.FIELD20_VALUE as field20Value, "
				+ " "+resultCol1+" as idetityField1, "+resultCol2+" as idetityField2,"
				+ " nxpm.TAB_NAME as tabName from NX_LINE_ITEM_LOOKUP_DATA nx"
				+ " left outer join NX_OUTPUT_PRODUCT_MAPPING nxpm  on nx.top_product_id = nxpm.top_prod_id"
				+ " where flow_type=:flowType and ACTIVE_YN=:activeFlag and ").append(condition).append(" ORDER BY nx.NX_ITEM_ID").toString();
		Query query=em.createNativeQuery(queryString,"getLineItem"); //NOSONAR
		query.setParameter(FLOW_TYPE, flowType);
		query.setParameter("activeFlag", FmoConstants.ACTIVE_FLAG);
		lineItemLst = query.getResultList();
		return lineItemLst;
		
	}*/
	/**
	 * Gets the line item data by be id.
	 *
	 * @param condition the condition
	 * @param resultCol1 the result col 1
	 * @param resultCol2 the result col 2
	 * @param flowType the flow type
	 * @return the line item data by be id
	 */
	@SuppressWarnings("unchecked")
	public List<NxLineItemLookUpDataModel> getLineItemData(String condition,String resultCol1,
			String resultCol2,String flowType) {
		List<NxLineItemLookUpDataModel> lineItemLst=null;
		String queryString= new StringBuilder().append("select nx.NX_ITEM_ID as nxItemId,"
				+ "	nx.LINE_ITEM_ID as lineItemId,nx.SECONDARY_KEY as secondaryKey,"
				+ " nx.OFFER_ID as offerId,nx.PRODUCT_ID as productId,nx.PRODUCT_CODE as productCd,"
				+ " nx.PRICE_TYPE as priceType,nx.FIELD1_VALUE as field1Value,"
				+ " nx.FIELD2_VALUE as field2Value,nx.FIELD3_VALUE as field3Value,"
				+ " nx.FIELD4_VALUE as field4Value,nx.FIELD5_VALUE as field5Value,"
				+ " nx.FIELD6_VALUE as field6Value,nx.FIELD7_VALUE as field7Value,"
				+ " nx.FIELD8_VALUE as field8Value,nx.FIELD9_VALUE as field9Value,"
				+ " nx.FIELD10_VALUE as field10Value,nx.FIELD11_VALUE as field11Value,"
				+ " nx.FIELD12_VALUE as field12Value,nx.FIELD13_VALUE as field13Value,"
				+ " nx.FIELD14_VALUE as field14Value,nx.FIELD15_VALUE as field15Value,"
				+ " nx.FIELD16_VALUE as field16Value,nx.FIELD17_VALUE as field17Value,"
				+ " nx.FIELD18_VALUE as field18Value,nx.FIELD19_VALUE as field19Value,"
				+ " nx.FIELD20_VALUE as field20Value,nx.ACTIVE_YN as active, "
				+ " nx.LITTLE_PRODUCT_ID as littleProdId,nx.TOP_PRODUCT_ID as topProdId,nx.FLOW_TYPE as flowType, "
				+ " "+resultCol1+" as idetityField1, "+resultCol2+" as idetityField2,"
				+ " nxpm.TAB_NAME as tabName from NX_LINE_ITEM_LOOKUP_DATA nx"
				+ " left outer join NX_OUTPUT_PRODUCT_MAPPING nxpm  on nx.top_product_id = nxpm.top_prod_id"
				+ " where flow_type=:flowType and ACTIVE_YN=:activeFlag and ").append(condition).append(" ORDER BY nx.NX_ITEM_ID").toString();
		Query query=em.createNativeQuery(queryString,"getAllLineItemData"); //NOSONAR
		query.setParameter(FLOW_TYPE, flowType);
		query.setParameter("activeFlag", FmoConstants.ACTIVE_FLAG);
		lineItemLst = query.getResultList();
		return lineItemLst;
		
	}
	
	/**
	 * Load Nexxus key path data.
	 *
	 * @return List<NxKeyFieldPathModel>
	 */
	@SuppressWarnings("unchecked")
	public List<NxKeyFieldPathModel> loadNexxusKeyPathData(){
		List<NxKeyFieldPathModel> resultLst=null;
		String hqlQuery="from NxKeyFieldPathModel where type='OTHER' ";
		Query query=em.createQuery(hqlQuery);
		resultLst = query.getResultList();
		return resultLst;
	}
	
	
	/**
	 * Gets the data from sales look up tbl.
	 *
	 * @param itemId the item id
	 * @param offerId the offer id
	 * @param udfId the udf id
	 * @param componentId the component id
	 * @return the data from sales look up tbl
	 */
	@SuppressWarnings("unchecked")
	public List<Object> getDataFromSalesLookUpTbl(Object itemId,Long offerId,Long udfId,Long componentId) {
		String queryString= "select COALESCE(ATTRVAL_ORDER_HAND_OFF, ATTR_display_value) as displayValue from SALES_MS_PRODCOMP_UDF_ATTR_VAL where "
				+ "offer_id=:offerId and component_id=:compId and udf_id=:udfId and udf_attribute_id=:itemId";
		Query query=em.createNativeQuery(queryString);
		query.setParameter("offerId", offerId);
		query.setParameter("itemId", itemId);
		query.setParameter("udfId", udfId);
		query.setParameter("compId", componentId);
		return query.getResultList();
	}
	
	
	/**
	 * Gets the data from ims 2 look up tbl.
	 *
	 * @param itemId the item id
	 * @param offerId the offer id
	 * @param udfId the udf id
	 * @param componentId the component id
	 * @return the data from ims 2 look up tbl
	 */
	@SuppressWarnings("unchecked")
	public List<Object> getDataFromIms2LookUpTbl(Object itemId,Long offerId,Long udfId,Long componentId) {
		String queryString= "select COALESCE(IMS2_DISPLAY_VALUE, ATTR_display_value) as displayValue from nx_udf_ims2_data_mapping where "
				+ "offer_id=:offerId and component_id=:compId and udf_id=:udfId and udf_attribute_id=:itemId";
		Query query=em.createNativeQuery(queryString);
		query.setParameter("offerId", offerId);
		query.setParameter("itemId", itemId);
		query.setParameter("udfId", udfId);
		query.setParameter("compId", componentId);
		return query.getResultList();
	}
	
	
	 
	
	
	/**
	 * Gets the nx lookup data by id.
	 *
	 * @param datasetName the dataset name
	 * @param itemId the item id
	 * @return the nx lookup data by id
	 */
	@SuppressWarnings("unchecked")
	public List<NxLookupData> getNxLookupDataById(String datasetName,String itemId) {
		List<NxLookupData> resultLst=null;
		String hqlQuery="from NxLookupData where datasetName=:datasetName and itemId=:itemId ";
		Query query=em.createQuery(hqlQuery);
		query.setParameter("datasetName", datasetName);
		query.setParameter("itemId", itemId);
		resultLst = query.getResultList();
		return resultLst;
	}
	
	
	
}
