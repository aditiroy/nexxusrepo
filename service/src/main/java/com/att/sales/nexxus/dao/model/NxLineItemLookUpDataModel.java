package com.att.sales.nexxus.dao.model;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


/**
 * The Class NxLineItemLookUpDataModel.
 *
 * @author vt393d
 * 
 * This model class of NX_LINE_ITEM_LOOKUP_DATA table is referring line item data in 
 */
@SqlResultSetMappings({
	@SqlResultSetMapping(
		    name = "getAllLineItemData",
		    classes = {
		        @ConstructorResult(targetClass = NxLineItemLookUpDataModel.class,
		            columns = {
		            	@ColumnResult(name = "nxItemId", type = Long.class),
		                @ColumnResult(name = "lineItemId", type = Long.class),
		                @ColumnResult(name = "secondaryKey", type = String.class),
		                @ColumnResult(name = "offerId", type = Long.class),
		                @ColumnResult(name = "productId", type = Long.class),
		                @ColumnResult(name = "productCd", type = String.class),
		                @ColumnResult(name = "priceType", type = String.class),
		                @ColumnResult(name = "field1Value", type = String.class),
		                @ColumnResult(name = "field2Value", type = String.class),
		                @ColumnResult(name = "field3Value", type = String.class),
		                @ColumnResult(name = "field4Value", type = String.class),
		                @ColumnResult(name = "field5Value", type = String.class),
		                @ColumnResult(name = "field6Value", type = String.class),
		                @ColumnResult(name = "field7Value", type = String.class),
		                @ColumnResult(name = "field8Value", type = String.class),
		                @ColumnResult(name = "field9Value", type = String.class),
		                @ColumnResult(name = "field10Value", type = String.class),
		                @ColumnResult(name = "field11Value", type = String.class),
		                @ColumnResult(name = "field12Value", type = String.class),
		                @ColumnResult(name = "field13Value", type = String.class),
		                @ColumnResult(name = "field14Value", type = String.class),
		                @ColumnResult(name = "field15Value", type = String.class),
		                @ColumnResult(name = "field16Value", type = String.class),
		                @ColumnResult(name = "field17Value", type = String.class),
		                @ColumnResult(name = "field18Value", type = String.class),
		                @ColumnResult(name = "field19Value", type = String.class),
		                @ColumnResult(name = "field20Value", type = String.class),
		                @ColumnResult(name = "active", type = String.class),
		                @ColumnResult(name = "littleProdId", type = Long.class),
		                @ColumnResult(name = "topProdId", type = Long.class),
		                @ColumnResult(name = "flowType", type = String.class),
		                @ColumnResult(name = "idetityField1", type = String.class),
		                @ColumnResult(name = "idetityField2", type = String.class),
		                @ColumnResult(name = "tabName", type = String.class),
		            }
		        )
		    }
	),
	@SqlResultSetMapping(
		    name = "getLineItem",
		    classes = {
		        @ConstructorResult(targetClass = NxLineItemLookUpDataModel.class,
		            columns = {
		                @ColumnResult(name = "lineItemId", type = Long.class),
		                @ColumnResult(name = "secondaryKey", type = String.class),
		                @ColumnResult(name = "littleProdId", type = Long.class),
		                @ColumnResult(name = "topProdId", type = Long.class),
		                @ColumnResult(name = "offerId", type = Long.class),
		                @ColumnResult(name = "field19Value", type = String.class),
		                @ColumnResult(name = "field20Value", type = String.class),
		                @ColumnResult(name = "idetityField1", type = String.class),
		                @ColumnResult(name = "idetityField2", type = String.class),
		                @ColumnResult(name = "tabName", type = String.class),
		            }
		        )
		    }
	)
})

@Entity
@Table(name="NX_LINE_ITEM_LOOKUP_DATA")
public class NxLineItemLookUpDataModel implements Comparable<NxLineItemLookUpDataModel>{
	private static final List<String> WILD_CARDS = Arrays.asList("*", "N/A");
	
	/**
	 * Instantiates a new nexus line item look up data model.
	 */
	public NxLineItemLookUpDataModel() {
		
	}
	
	
	public NxLineItemLookUpDataModel(Long nxItemId, Long lineItemId, String secondaryKey, Long offerId, Long productId,
			String productCd, String priceType, String field1Value, String field2Value, String field3Value,
			String field4Value, String field5Value, String field6Value, String field7Value, String field8Value,
			String field9Value, String field10Value, String field11Value, String field12Value, String field13Value,
			String field14Value, String field15Value, String field16Value, String field17Value, String field18Value,
			String field19Value, String field20Value, String active, Long littleProdId, Long topProdId, String flowType,
			String idetityField1, String idetityField2,String tabName) {
		this.nxItemId = nxItemId;
		this.lineItemId = lineItemId;
		this.secondaryKey = secondaryKey;
		this.offerId = offerId;
		this.productId = productId;
		this.productCd = productCd;
		this.priceType = priceType;
		this.field1Value = field1Value;
		this.field2Value = field2Value;
		this.field3Value = field3Value;
		this.field4Value = field4Value;
		this.field5Value = field5Value;
		this.field6Value = field6Value;
		this.field7Value = field7Value;
		this.field8Value = field8Value;
		this.field9Value = field9Value;
		this.field10Value = field10Value;
		this.field11Value = field11Value;
		this.field12Value = field12Value;
		this.field13Value = field13Value;
		this.field14Value = field14Value;
		this.field15Value = field15Value;
		this.field16Value = field16Value;
		this.field17Value = field17Value;
		this.field18Value = field18Value;
		this.field19Value = field19Value;
		this.field20Value = field20Value;
		this.active = active;
		this.littleProdId = littleProdId;
		this.topProdId = topProdId;
		this.flowType = flowType;
		this.idetityField1 = idetityField1;
		this.idetityField2 = idetityField2;
		this.nexusOutputMapping=new NxOutputProductMappingModel(topProdId, tabName);
	}




	/**
	 * Instantiates a new nx line item look up data model.
	 *
	 * @param lineItemId the line item id
	 * @param secondaryKey the secondary key
	 * @param littleProdId the little prod id
	 * @param topProdId the top prod id
	 * @param offerId the offer id
	 * @param idetityField1 the idetity field 1
	 * @param idetityField2 the idetity field 2
	 * @param tabName the tab name
	 */
	public NxLineItemLookUpDataModel(Long lineItemId, String secondaryKey, Long littleProdId,
			Long topProdId,Long offerId,String field19Value,String field20Value,String idetityField1, String idetityField2,String tabName) {
		this.lineItemId = lineItemId;
		this.secondaryKey = secondaryKey;
		this.littleProdId = littleProdId;
		this.topProdId = topProdId;
		this.offerId=offerId;
		this.field19Value=field19Value;
		this.field20Value=field20Value;
		this.idetityField1 = idetityField1;
		this.idetityField2 = idetityField2;
		this.nexusOutputMapping=new NxOutputProductMappingModel(topProdId, tabName);
	}



	/** The nx item id. */
	@Id
	@Column(name="NX_ITEM_ID")
	@SequenceGenerator(name="sequence_nx_line_item_lookup_data",sequenceName="SEQ_NX_ITEM_ID", allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="sequence_nx_line_item_lookup_data")
	private Long nxItemId;
	
	/** The line item id. */
	@Column(name="LINE_ITEM_ID")
	private Long lineItemId;
	
	/** The secondary key. */
	@Column(name="SECONDARY_KEY")
	private String secondaryKey;
	
	/** The offer id. */
	@Column(name="OFFER_ID")
	private Long offerId; 
	
	/** The product id. */
	@Column(name="PRODUCT_ID")
	private Long productId;
	
	/** The product cd. */
	@Column(name="PRODUCT_CODE")
	private String productCd;
	
	/** The price type. */
	@Column(name="PRICE_TYPE")
	private String priceType;
	
	/** The field 1 value. */
	@Column(name="FIELD1_VALUE")
	private String field1Value;
	
	/** The field 2 value. */
	@Column(name="FIELD2_VALUE")
	private String field2Value;
	
	/** The field 3 value. */
	@Column(name="FIELD3_VALUE")
	private String field3Value;
	
	/** The field 4 value. */
	@Column(name="FIELD4_VALUE")
	private String field4Value;
	
	/** The field 5 value. */
	@Column(name="FIELD5_VALUE")
	private String field5Value;
	
	/** The field 6 value. */
	@Column(name="FIELD6_VALUE")
	private String field6Value;
	
	/** The field 7 value. */
	@Column(name="FIELD7_VALUE")
	private String field7Value;
	
	/** The field 8 value. */
	@Column(name="FIELD8_VALUE")
	private String field8Value;
	
	/** The field 9 value. */
	@Column(name="FIELD9_VALUE")
	private String field9Value;
	
	/** The field 10 value. */
	@Column(name="FIELD10_VALUE")
	private String field10Value;
	
	/** The field 11 value. */
	@Column(name="FIELD11_VALUE")
	private String field11Value;
	
	/** The field 12 value. */
	@Column(name="FIELD12_VALUE")
	private String field12Value;
	
	/** The field 13 value. */
	@Column(name="FIELD13_VALUE")
	private String field13Value;
	
	/** The field 14 value. */
	@Column(name="FIELD14_VALUE")
	private String field14Value;
	
	/** The field 15 value. */
	@Column(name="FIELD15_VALUE")
	private String field15Value;
	
	/** The field 16 value. */
	@Column(name="FIELD16_VALUE")
	private String field16Value;
	
	@Column(name="FIELD17_VALUE")
	private String field17Value;
	
	@Column(name="FIELD18_VALUE")
	private String field18Value;
	
	@Column(name="FIELD19_VALUE")
	private String field19Value;
	
	@Column(name="FIELD20_VALUE")
	private String field20Value;
	
	/** The active. */
	@Column(name="ACTIVE_YN")
	private String active;
	
	/** The little prod id. */
	@Column(name="LITTLE_PRODUCT_ID")
	private Long littleProdId;
	
	/** The top prod id. */
	@Column(name="TOP_PRODUCT_ID")
	private Long topProdId;
	
	/** The flow type. */
	@Column(name="FLOW_TYPE")
	private String flowType;
	
	@Column(name="FIELD_16_20_VALUE")
	private String field1620Value;
	
	@Column(name="MP_ACTIVE_YN")
	private String mpActiveYn;
	
	/** The idetity field 1. */
	@Transient
	private String idetityField1;
	
	/** The idetity field 2. */
	@Transient
	private String idetityField2;
	
	/** The nexus output mapping. */
	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "TOP_PRODUCT_ID",referencedColumnName="TOP_PROD_ID",insertable=false, updatable=false)
	@NotFound ( action = NotFoundAction.IGNORE )
	private NxOutputProductMappingModel nexusOutputMapping;
	

	/**
	 * Gets the nx item id.
	 *
	 * @return the nx item id
	 */
	public Long getNxItemId() {
		return nxItemId;
	}

	/**
	 * Sets the nx item id.
	 *
	 * @param nxItemId the new nx item id
	 */
	public void setNxItemId(Long nxItemId) {
		this.nxItemId = nxItemId;
	}

	/**
	 * Gets the line item id.
	 *
	 * @return the line item id
	 */
	public Long getLineItemId() {
		return lineItemId;
	}

	/**
	 * Sets the line item id.
	 *
	 * @param lineItemId the new line item id
	 */
	public void setLineItemId(Long lineItemId) {
		this.lineItemId = lineItemId;
	}
	
	/**
	 * Gets the secondary key.
	 *
	 * @return the secondary key
	 */
	public String getSecondaryKey() {
		return secondaryKey;
	}

	/**
	 * Sets the secondary key.
	 *
	 * @param secondaryKey the new secondary key
	 */
	public void setSecondaryKey(String secondaryKey) {
		this.secondaryKey = secondaryKey;
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
	 * Gets the product id.
	 *
	 * @return the product id
	 */
	public Long getProductId() {
		return productId;
	}

	/**
	 * Sets the product id.
	 *
	 * @param productId the new product id
	 */
	public void setProductId(Long productId) {
		this.productId = productId;
	}

	/**
	 * Gets the product cd.
	 *
	 * @return the product cd
	 */
	public String getProductCd() {
		return productCd;
	}

	/**
	 * Sets the product cd.
	 *
	 * @param productCd the new product cd
	 */
	public void setProductCd(String productCd) {
		this.productCd = productCd;
	}

	/**
	 * Gets the price type.
	 *
	 * @return the price type
	 */
	public String getPriceType() {
		return priceType;
	}

	/**
	 * Sets the price type.
	 *
	 * @param priceType the new price type
	 */
	public void setPriceType(String priceType) {
		this.priceType = priceType;
	}

	/**
	 * Gets the field 1 value.
	 *
	 * @return the field 1 value
	 */
	public String getField1Value() {
		return field1Value;
	}

	/**
	 * Sets the field 1 value.
	 *
	 * @param field1Value the new field 1 value
	 */
	public void setField1Value(String field1Value) {
		this.field1Value = field1Value;
	}

	/**
	 * Gets the field 2 value.
	 *
	 * @return the field 2 value
	 */
	public String getField2Value() {
		return field2Value;
	}

	/**
	 * Sets the field 2 value.
	 *
	 * @param field2Value the new field 2 value
	 */
	public void setField2Value(String field2Value) {
		this.field2Value = field2Value;
	}

	/**
	 * Gets the field 3 value.
	 *
	 * @return the field 3 value
	 */
	public String getField3Value() {
		return field3Value;
	}

	/**
	 * Sets the field 3 value.
	 *
	 * @param field3Value the new field 3 value
	 */
	public void setField3Value(String field3Value) {
		this.field3Value = field3Value;
	}

	/**
	 * Gets the field 4 value.
	 *
	 * @return the field 4 value
	 */
	public String getField4Value() {
		return field4Value;
	}

	/**
	 * Sets the field 4 value.
	 *
	 * @param field4Value the new field 4 value
	 */
	public void setField4Value(String field4Value) {
		this.field4Value = field4Value;
	}

	/**
	 * Gets the field 5 value.
	 *
	 * @return the field 5 value
	 */
	public String getField5Value() {
		return field5Value;
	}

	/**
	 * Sets the field 5 value.
	 *
	 * @param field5Value the new field 5 value
	 */
	public void setField5Value(String field5Value) {
		this.field5Value = field5Value;
	}

	/**
	 * Gets the field 6 value.
	 *
	 * @return the field 6 value
	 */
	public String getField6Value() {
		return field6Value;
	}

	/**
	 * Sets the field 6 value.
	 *
	 * @param field6Value the new field 6 value
	 */
	public void setField6Value(String field6Value) {
		this.field6Value = field6Value;
	}

	/**
	 * Gets the field 7 value.
	 *
	 * @return the field 7 value
	 */
	public String getField7Value() {
		return field7Value;
	}

	/**
	 * Sets the field 7 value.
	 *
	 * @param field7Value the new field 7 value
	 */
	public void setField7Value(String field7Value) {
		this.field7Value = field7Value;
	}

	/**
	 * Gets the field 9 value.
	 *
	 * @return the field 9 value
	 */
	public String getField9Value() {
		return field9Value;
	}
	
	/**
	 * Gets the field 8 value.
	 *
	 * @return the field 8 value
	 */
	public String getField8Value() {
		return field8Value;
	}
	
	/**
	 * Sets the field 8 value.
	 *
	 * @param field8Value the new field 8 value
	 */
	public void setField8Value(String field8Value) {
		this.field8Value = field8Value;
	}
	
	/**
	 * Sets the field 9 value.
	 *
	 * @param field9Value the new field 9 value
	 */
	public void setField9Value(String field9Value) {
		this.field9Value = field9Value;
	}

	/**
	 * Gets the field 10 value.
	 *
	 * @return the field 10 value
	 */
	public String getField10Value() {
		return field10Value;
	}

	/**
	 * Sets the field 10 value.
	 *
	 * @param field10Value the new field 10 value
	 */
	public void setField10Value(String field10Value) {
		this.field10Value = field10Value;
	}

	/**
	 * Gets the field 11 value.
	 *
	 * @return the field 11 value
	 */
	public String getField11Value() {
		return field11Value;
	}

	/**
	 * Sets the field 11 value.
	 *
	 * @param field11Value the new field 11 value
	 */
	public void setField11Value(String field11Value) {
		this.field11Value = field11Value;
	}

	/**
	 * Gets the field 12 value.
	 *
	 * @return the field 12 value
	 */
	public String getField12Value() {
		return field12Value;
	}

	/**
	 * Sets the field 12 value.
	 *
	 * @param field12Value the new field 12 value
	 */
	public void setField12Value(String field12Value) {
		this.field12Value = field12Value;
	}

	/**
	 * Gets the field 13 value.
	 *
	 * @return the field 13 value
	 */
	public String getField13Value() {
		return field13Value;
	}

	/**
	 * Sets the field 13 value.
	 *
	 * @param field13Value the new field 13 value
	 */
	public void setField13Value(String field13Value) {
		this.field13Value = field13Value;
	}

	/**
	 * Gets the field 14 value.
	 *
	 * @return the field 14 value
	 */
	public String getField14Value() {
		return field14Value;
	}

	/**
	 * Sets the field 14 value.
	 *
	 * @param field14Value the new field 14 value
	 */
	public void setField14Value(String field14Value) {
		this.field14Value = field14Value;
	}

	/**
	 * Gets the field 15 value.
	 *
	 * @return the field 15 value
	 */
	public String getField15Value() {
		return field15Value;
	}

	/**
	 * Sets the field 15 value.
	 *
	 * @param field15Value the new field 15 value
	 */
	public void setField15Value(String field15Value) {
		this.field15Value = field15Value;
	}

	/**
	 * Gets the field 16 value.
	 *
	 * @return the field 16 value
	 */
	public String getField16Value() {
		return field16Value;
	}

	/**
	 * Sets the field 16 value.
	 *
	 * @param field16Value the new field 16 value
	 */
	public void setField16Value(String field16Value) {
		this.field16Value = field16Value;
	}

	/**
	 * Gets the active.
	 *
	 * @return the active
	 */
	public String getActive() {
		return active;
	}

	/**
	 * Sets the active.
	 *
	 * @param active the new active
	 */
	public void setActive(String active) {
		this.active = active;
	}

	
	/**
	 * Gets the little prod id.
	 *
	 * @return the little prod id
	 */
	public Long getLittleProdId() {
		return littleProdId;
	}

	/**
	 * Sets the little prod id.
	 *
	 * @param littleProdId the new little prod id
	 */
	public void setLittleProdId(Long littleProdId) {
		this.littleProdId = littleProdId;
	}

	/**
	 * Gets the top prod id.
	 *
	 * @return the top prod id
	 */
	public Long getTopProdId() {
		return topProdId;
	}

	/**
	 * Sets the top prod id.
	 *
	 * @param topProdId the new top prod id
	 */
	public void setTopProdId(Long topProdId) {
		this.topProdId = topProdId;
	}
	
	/**
	 * Gets the idetity field 1.
	 *
	 * @return the idetity field 1
	 */
	public String getIdetityField1() {
		return idetityField1;
	}

	/**
	 * Sets the idetity field 1.
	 *
	 * @param idetityField1 the new idetity field 1
	 */
	public void setIdetityField1(String idetityField1) {
		this.idetityField1 = idetityField1;
	}

	/**
	 * Gets the idetity field 2.
	 *
	 * @return the idetity field 2
	 */
	public String getIdetityField2() {
		return idetityField2;
	}

	/**
	 * Sets the idetity field 2.
	 *
	 * @param idetityField2 the new idetity field 2
	 */
	public void setIdetityField2(String idetityField2) {
		this.idetityField2 = idetityField2;
	}

	/**
	 * Gets the nexus output mapping.
	 *
	 * @return the nexus output mapping
	 */
	public NxOutputProductMappingModel getNexusOutputMapping() {
		return nexusOutputMapping;
	}

	/**
	 * Sets the nexus output mapping.
	 *
	 * @param nexusOutputMapping the new nexus output mapping
	 */
	public void setNexusOutputMapping(NxOutputProductMappingModel nexusOutputMapping) {
		this.nexusOutputMapping = nexusOutputMapping;
	}


	/**
	 * Gets the flow type.
	 *
	 * @return the flow type
	 */
	public String getFlowType() {
		return flowType;
	}

	/**
	 * Sets the flow type.
	 *
	 * @param flowType the new flow type
	 */
	public void setFlowType(String flowType) {
		this.flowType = flowType;
	}




	public String getField17Value() {
		return field17Value;
	}




	public void setField17Value(String field17Value) {
		this.field17Value = field17Value;
	}




	public String getField18Value() {
		return field18Value;
	}




	public void setField18Value(String field18Value) {
		this.field18Value = field18Value;
	}




	public String getField19Value() {
		return field19Value;
	}




	public void setField19Value(String field19Value) {
		this.field19Value = field19Value;
	}




	public String getField20Value() {
		return field20Value;
	}




	public void setField20Value(String field20Value) {
		this.field20Value = field20Value;
	}


	public String getField1620Value() {
		return field1620Value;
	}


	public void setField1620Value(String field1620Value) {
		this.field1620Value = field1620Value;
	}


	public String getMpActiveYn() {
		return mpActiveYn;
	}


	public void setMpActiveYn(String mpActiveYn) {
		this.mpActiveYn = mpActiveYn;
	}


	@Override
	public int compareTo(NxLineItemLookUpDataModel another) {
		return countWildCards(this) - countWildCards(another);
	}
	
	private static int countWildCards(NxLineItemLookUpDataModel model) {
		int res = 0;
		if (WILD_CARDS.contains(model.getField1Value())) {
			res++;
		}
		if (WILD_CARDS.contains(model.getField2Value())) {
			res++;
		}
		if (WILD_CARDS.contains(model.getField3Value())) {
			res++;
		}
		if (WILD_CARDS.contains(model.getField4Value())) {
			res++;
		}
		if (WILD_CARDS.contains(model.getField5Value())) {
			res++;
		}
		if (WILD_CARDS.contains(model.getField6Value())) {
			res++;
		}
		if (WILD_CARDS.contains(model.getField7Value())) {
			res++;
		}
		if (WILD_CARDS.contains(model.getField8Value())) {
			res++;
		}
		if (WILD_CARDS.contains(model.getField9Value())) {
			res++;
		}
		if (WILD_CARDS.contains(model.getField10Value())) {
			res++;
		}
		if (WILD_CARDS.contains(model.getField11Value())) {
			res++;
		}
		if (WILD_CARDS.contains(model.getField12Value())) {
			res++;
		}
		if (WILD_CARDS.contains(model.getField13Value())) {
			res++;
		}
		if (WILD_CARDS.contains(model.getField14Value())) {
			res++;
		}
		if (WILD_CARDS.contains(model.getField15Value())) {
			res++;
		}
		if (WILD_CARDS.contains(model.getField16Value())) {
			res++;
		}
		if (WILD_CARDS.contains(model.getField17Value())) {
			res++;
		}
		if (WILD_CARDS.contains(model.getField18Value())) {
			res++;
		}
		if (WILD_CARDS.contains(model.getField19Value())) {
			res++;
		}
		if (WILD_CARDS.contains(model.getField20Value())) {
			res++;
		}
		if (null == model.getField19Value()) {
			res++;
		}
		if (null == model.getField20Value()) {
			res++;
		}
		return res;
	}
	
	
}
