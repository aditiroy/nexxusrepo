package com.att.sales.nexxus.reteriveicb.model;

/*
 * @Author: Akash Arya
 * 
 * 
 */
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;


/**
 * The Class UDFBaseData.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class UDFBaseData {
	
	/** The udf attribute id. */
	private List<Long> udfAttributeId;

    /** The udf id. */
    private int udfId;
    
    /** The read only. */
    private String readOnly;
    
    /** The udf attribute text. */
    private List<String> udfAttributeText;
    
    /** The default udf attribute id. */
    private List<String> defaultUdfAttributeId;
    
    /** The lov attribute id list. */
    private List<String> lovAttributeIdList;
    
    /** The udf value. */
    private String udfValue;

    /**
     * Sets the udf attribute id.
     *
     * @param udfAttributeId the new udf attribute id
     */
    public void setUdfAttributeId(List<Long> udfAttributeId){
        this.udfAttributeId = udfAttributeId;
    }
    
    /**
     * Gets the udf attribute id.
     *
     * @return the udf attribute id
     */
    public List<Long> getUdfAttributeId(){
        return this.udfAttributeId;
    }
    
    /**
     * Sets the udf id.
     *
     * @param udfId the new udf id
     */
    public void setUdfId(int udfId){
        this.udfId = udfId;
    }
    
    /**
     * Gets the udf id.
     *
     * @return the udf id
     */
    public int getUdfId(){
        return this.udfId;
    }
	
	/**
	 * Gets the udf attribute text.
	 *
	 * @return the udf attribute text
	 */
	public List<String> getUdfAttributeText() {
		return udfAttributeText;
	}
	
	/**
	 * Sets the udf attribute text.
	 *
	 * @param udfAttributeText the new udf attribute text
	 */
	public void setUdfAttributeText(List<String> udfAttributeText) {
		this.udfAttributeText = udfAttributeText;
	}
	
	/**
	 * Gets the default udf attribute id.
	 *
	 * @return the default udf attribute id
	 */
	public List<String> getDefaultUdfAttributeId() {
		return defaultUdfAttributeId;
	}
	
	/**
	 * Sets the default udf attribute id.
	 *
	 * @param defaultUdfAttributeId the new default udf attribute id
	 */
	public void setDefaultUdfAttributeId(List<String> defaultUdfAttributeId) {
		this.defaultUdfAttributeId = defaultUdfAttributeId;
	}
	
	/**
	 * Gets the read only.
	 *
	 * @return the read only
	 */
	public String getReadOnly() {
		return readOnly;
	}
	
	/**
	 * Sets the read only.
	 *
	 * @param readOnly the new read only
	 */
	public void setReadOnly(String readOnly) {
		this.readOnly = readOnly;
	}
	
	/**
	 * Gets the lov attribute id list.
	 *
	 * @return the lov attribute id list
	 */
	public List<String> getLovAttributeIdList() {
		return lovAttributeIdList;
	}
	
	/**
	 * Sets the lov attribute id list.
	 *
	 * @param lovAttributeIdList the new lov attribute id list
	 */
	public void setLovAttributeIdList(List<String> lovAttributeIdList) {
		this.lovAttributeIdList = lovAttributeIdList;
	}
	
	/**
	 * Gets the udf value.
	 *
	 * @return the udf value
	 */
	public String getUdfValue() {
		return udfValue;
	}
	
	/**
	 * Sets the udf value.
	 *
	 * @param udfValue the new udf value
	 */
	public void setUdfValue(String udfValue) {
		this.udfValue = udfValue;
	}
	
}
