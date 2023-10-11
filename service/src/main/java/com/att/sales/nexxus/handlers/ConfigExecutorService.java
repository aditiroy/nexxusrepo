package com.att.sales.nexxus.handlers;

import java.util.Map;
import java.util.concurrent.Callable;

import javax.xml.bind.JAXBElement;

import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;

import com.att.sales.nexxus.dao.model.NxMpConfigMapping;
import com.att.sales.nexxus.myprice.transaction.service.ConfigAndUpdatePricingUtil;
import com.att.sales.nexxus.myprice.transaction.service.ConfigAndUpdatePricingUtilFmo;
import com.att.sales.nexxus.myprice.transaction.service.ConfigAndUpdatePricingUtilIgloo;
import com.att.sales.nexxus.myprice.transaction.service.ConfigAndUpdatePricingUtilInr;
import com.oracle.xmlns.cpqcloud.configuration.wireline.solution.solution.ModelNamePf;
import com.oracle.xmlns.cpqcloud.configuration.wireline.solution.solution.ObjectFactory;

public class ConfigExecutorService<T> implements Callable<Object>{
	
	
	private NxMpConfigMapping data;
	private T t;
	private Map<String, Object> requestMap;
	private JSONObject inputData;
	private String designData;
	
	ConfigExecutorService(NxMpConfigMapping data){
		this.data=data;
	}
	public void setT(T t) {
		this.t = t;
	}
	public void setRequestMap(Map<String, Object> requestMap) {
		this.requestMap = requestMap;
	}
	public void setInputDesignDetails(JSONObject inputDesignDetails) {
		this.inputData = inputDesignDetails;
	}
	public void setDesignData(String designData) {
		this.designData = designData;
	}
	@Override
	public Object call() throws Exception {
		return this.process(requestMap, inputData, data);
	}
	
	
	protected ModelNamePf process(Map<String, Object> requestMap,
			JSONObject inputDesignDetails,NxMpConfigMapping data) {
		String attrData=null;
		if(t instanceof ConfigureDesignWSHandler) {
			ConfigureDesignWSHandler configDesign=(ConfigureDesignWSHandler) t;
			attrData=configDesign.getData(data,inputDesignDetails,requestMap);
		}else if(t instanceof ConfigAndUpdatePricingUtil) {
			ConfigAndUpdatePricingUtil configSolAndProduct=(ConfigAndUpdatePricingUtil) t;
			attrData=configSolAndProduct.getData(data,inputDesignDetails);
		}else if(t instanceof ConfigAndUpdatePricingUtilInr) {
			ConfigAndUpdatePricingUtilInr configInr=(ConfigAndUpdatePricingUtilInr) t;
			attrData=configInr.getData(data,designData,requestMap);
		}else if(t instanceof ConfigAndUpdatePricingUtilIgloo) {
			ConfigAndUpdatePricingUtilIgloo configIgloo=(ConfigAndUpdatePricingUtilIgloo) t;
			attrData=configIgloo.getData(data,designData,requestMap);
		}else if(t instanceof ConfigAndUpdatePricingUtilFmo) {
			ConfigAndUpdatePricingUtilFmo configFmo=(ConfigAndUpdatePricingUtilFmo) t;
			attrData=configFmo.getData(data,designData,requestMap);
		}
		if(StringUtils.isNotEmpty(attrData)) {
			JAXBElement<String> value = new ObjectFactory().
					createModelNamePfValue(attrData);
			ModelNamePf obj = new ModelNamePf();
			obj.setVariableName(data.getVariableName());
			obj.setValue(value);
			return obj;
		}
		return null;
	}

}
