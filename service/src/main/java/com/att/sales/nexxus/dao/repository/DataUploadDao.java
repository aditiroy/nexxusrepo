package com.att.sales.nexxus.dao.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;

import org.springframework.stereotype.Repository;

import com.att.sales.framework.model.Message;
import com.att.sales.framework.model.Status;
import com.att.sales.framework.model.constants.HttpErrorCodes;
import com.att.sales.nexxus.constant.CommonConstants;
import com.att.sales.nexxus.constant.DataUploadConstants;

/**
 * The Class DataUploadDao.
 *
 * @author vt393d
 */
@Repository(value="dataUploadDao")
public class DataUploadDao {
	
	/** The em. */
	@PersistenceContext
	private EntityManager em;
	
	/**
	 * Active line item data.
	 *
	 * @param inputmap the inputmap
	 */
	public void activeLineItemData(Map<String,Object> inputmap) {
		
		Long littleProdId=null!=inputmap.get(DataUploadConstants.LITTLE_PROD_ID)?
				Long.valueOf(inputmap.get(DataUploadConstants.LITTLE_PROD_ID).toString()):null;
		Long topProdId=null!=inputmap.get(DataUploadConstants.TOP_PROD_ID)?
				Long.valueOf(inputmap.get(DataUploadConstants.TOP_PROD_ID).toString()):null;
				
		StoredProcedureQuery storedProcedure = em.createStoredProcedureQuery(CommonConstants.PROC_ACTIVE_LINE_ITEM_DATA);
		storedProcedure.registerStoredProcedureParameter("top_product_id", Long.class, ParameterMode.IN)
				.registerStoredProcedureParameter("little_product_id", Long.class, ParameterMode.IN)
				.registerStoredProcedureParameter("p_out_status", String.class, ParameterMode.OUT)
				.registerStoredProcedureParameter("p_out_unmatch_data", String.class, ParameterMode.OUT);

		storedProcedure.setParameter("top_product_id", topProdId);
		storedProcedure.setParameter("little_product_id", littleProdId);

		storedProcedure.execute();
		Object resultStatus = storedProcedure.getOutputParameterValue("p_out_status");
		Object unMatchdata = storedProcedure.getOutputParameterValue("p_out_unmatch_data");

		String strResultStatus = resultStatus != null ? resultStatus.toString() : "";
		String strResultDesc = unMatchdata != null ? unMatchdata.toString() : null;
		inputmap.put(DataUploadConstants.STATUS, strResultStatus);
		inputmap.put(DataUploadConstants.DISCRIPTION, strResultDesc);

		Status s = new Status();
		List<Message> msgList = new ArrayList<>();
		Message msg = new Message();
		msg.setDescription(strResultDesc);
		msg.setDetailedDescription(strResultDesc);
		msgList.add(msg);

		if ("SUCCESS".equalsIgnoreCase(strResultStatus)) {
			msg.setCode("M00000");
			s.setCode(HttpErrorCodes.STATUS_OK.toString());			
		} else {
			msg.setCode("M00003");
			s.setCode("500");
		}

		s.setMessages(msgList);
		
	}
	

}
