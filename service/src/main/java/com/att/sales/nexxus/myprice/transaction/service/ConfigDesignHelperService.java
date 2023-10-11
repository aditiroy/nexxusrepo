package com.att.sales.nexxus.myprice.transaction.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.constant.FmoConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.dao.model.NxMpDesignDocument;
import com.att.sales.nexxus.dao.repository.NxMpDesignDocumentRepository;
import com.att.sales.nexxus.myprice.transaction.model.ConfigRespProcessingBean;
import com.oracle.xmlns.cpqcloud.configuration.wireline.solution.solution.ConfigureResponse;

@Component
public class ConfigDesignHelperService {
	
	private static final Logger log = LoggerFactory.getLogger(ConfigDesignHelperService.class);
	
	@Autowired
	private NxMpDesignDocumentRepository nxMpDesignDocumentRepo;
	
	
	public Map<String, Object> processConfigDesignResponse(Map<String, Object> methodParam){
		Map<String, Object> responseMap = new HashMap<String, Object>();
		if(methodParam.containsKey(MyPriceConstants.CONFIG_DESIGN_RESPONSE) &&
				null!=methodParam.get(MyPriceConstants.CONFIG_DESIGN_RESPONSE)) {
			ConfigureResponse configureResponse=(ConfigureResponse)methodParam.get(MyPriceConstants.CONFIG_DESIGN_RESPONSE);
			try {
				this.processConfigResponse(configureResponse, methodParam);
			} catch (SalesBusinessException e) {
				responseMap.put(MyPriceConstants.RESPONSE_STATUS, false);
				log.info("Exception while processing config design response : "+e);
				return responseMap;
			}
		
		}
		return responseMap;
	}
	
	
	@SuppressWarnings("unchecked")
	protected void processConfigResponse(ConfigureResponse configureResponse,Map<String,Object> methodParam) 
			throws SalesBusinessException {
		if(null!=configureResponse 
				&& null!=configureResponse.getTransaction() 
				&& null!=configureResponse.getTransaction().getValue() 
				&& null!=configureResponse.getTransaction().getValue().getDataXml() 
				&& null!=configureResponse.getTransaction().getValue().getDataXml().getAny()) {
			Map<String,Map<String,Object>> dataMapByProductId=(Map<String, Map<String,Object>>) methodParam.get(MyPriceConstants.CONFIG_RESPONSE_PROCESSING_DATA);
			for (Element element : configureResponse.getTransaction().getValue().getDataXml().getAny()) {
				if(null!=element && null!=element.getElementsByTagName("bmt:transactionLine")) {
					NodeList transactionLineList = element.getElementsByTagName("bmt:transactionLine");
					for (int transLineCount = 0; transLineCount < transactionLineList.getLength(); transLineCount++) {
						Node transactionLineNode = transactionLineList.item(transLineCount);
						ConfigRespProcessingBean obj=this.createResponseBean(transactionLineNode,methodParam);
						this.persistResponse(obj,methodParam,dataMapByProductId);
					}	
				}
			}
		}
		
	}
	
	
	/**
	 * Creates the response bean.
	 *
	 * @param transactionLineNode the transaction line node
	 * @param methodParam the method param
	 * @return the config resp processing bean
	 */
	
	protected ConfigRespProcessingBean createResponseBean(Node transactionLineNode,Map<String,Object> methodParam) {
		Long nxTxnId=methodParam.get(MyPriceConstants.NX_TRANSACTION_ID)!=null?
				(Long)methodParam.get(MyPriceConstants.NX_TRANSACTION_ID):null;
		String mpRequestTxnId=methodParam.get(MyPriceConstants.MP_TRANSACTION_ID)!=null?
				(String)methodParam.get(MyPriceConstants.MP_TRANSACTION_ID):null;				
		ConfigRespProcessingBean result=null;
		if(null!=transactionLineNode) {
			Node mpTxnIdNode=transactionLineNode.getAttributes().getNamedItem("bmt:bs_id");
			String mpRespTxnId=null!=mpTxnIdNode?mpTxnIdNode.getTextContent():null;
			if(StringUtils.isNotEmpty(mpRespTxnId) && mpRespTxnId.equals(mpRequestTxnId)) {
				result=new ConfigRespProcessingBean();
				result.setMpTransactionId(mpRespTxnId);
				result.setNxTransactionId(nxTxnId);
				NodeList transLineChildList = transactionLineNode.getChildNodes();
				if(null!=transLineChildList) {
					for (int transLineChildCount = 0; transLineChildCount < transLineChildList
							.getLength(); transLineChildCount++) {
						Node transLineChild = transLineChildList.item(transLineChildCount);
						if(null!=transLineChild) {
							if (transLineChild.getNodeName().equalsIgnoreCase("bmt:lii_nxSiteId_ql")) {
								result.setNxSiteId(transLineChild.getTextContent());
							}
							if (transLineChild.getNodeName().equalsIgnoreCase("bmt:wl_int_ade_site_reln")) {
								result.setAdeSiteRelation(transLineChild.getTextContent());
							}
							if (transLineChild.getNodeName().equalsIgnoreCase("bmt:_line_bom_id")) {
								result.setLineBomId(transLineChild.getTextContent());
							}
							if (transLineChild.getNodeName().equalsIgnoreCase("bmt:_line_bom_parent_id")) {
								result.setLineBomParentId(transLineChild.getTextContent());
							}
							if (transLineChild.getNodeName().equalsIgnoreCase("bmt:_document_number")) {
								result.setDocumentNumber(transLineChild.getTextContent());
							}
							if (transLineChild.getNodeName().equalsIgnoreCase("bmt:_parent_doc_number")) {
								result.setParentDocNumber(transLineChild.getTextContent());
							}
							if (transLineChild.getNodeName().equalsIgnoreCase("bmt:_parent_line_item")) {
								result.setParentLineItem(transLineChild.getTextContent());
							}
							if (transLineChild.getNodeName().equalsIgnoreCase("bmt:wi_uniqueID_ql")) {
								result.setUsocCode(transLineChild.getTextContent());
							}
							if (transLineChild.getNodeName().equalsIgnoreCase("bmt:_line_bom_part_number")) {
								result.setLineBomPartNumber(transLineChild.getTextContent());
							}
						}
						
					}
				}
				
			}
		}
		return result;
	}
	

	
	/**
	 * Persist response.
	 *
	 * @param input the input
	 * @param methodParam the method param
	 * @throws SalesBusinessException the sales business exception
	 */
	
	protected void persistResponse(ConfigRespProcessingBean input,Map<String,Object> methodParam,Map<String,Map<String,Object>> dataMapByProductId) 
			throws SalesBusinessException {
		
		if((StringUtils.isNotEmpty(input.getParentLineItem()) && input.getParentLineItem().equalsIgnoreCase("Solution")) 
						|| (StringUtils.isNotEmpty(input.getLineBomParentId()) && input.getLineBomParentId().equalsIgnoreCase("BOM_Solution"))) {
			Map<String,String> productIdSolutionIdMap=new HashMap<String, String>();
			String mpProductId=input.getDocumentNumber();
			String mpSolutionId=input.getParentDocNumber();
			productIdSolutionIdMap.put(mpProductId, mpSolutionId);
			methodParam.put(MyPriceConstants.PRODUCT_ID_SOLUTION_ID_MAP,productIdSolutionIdMap);
		}
		if(!(input.getLineBomPartNumber().equalsIgnoreCase("Solution") ||
				input.getParentLineItem().equalsIgnoreCase("Solution"))){
			//This flag is use to check Design transactionLine block from configDesign response is belonging to same product id whatever we r getting in request
			//This is use to find child design block for particular product
		
			String mpProductLineId=	input.getParentDocNumber();	
			if(null!=dataMapByProductId && dataMapByProductId.containsKey(mpProductLineId)) {
				methodParam.putAll(dataMapByProductId.get(mpProductLineId));
				this.processDesignBlock(input,methodParam);
			}
			
			
		}
	}
	
	
	public Boolean isProductLineIdMatchForConfigDesign(Map<String,Object> methodParam,String respParentId) {
		String reqProductLineId=methodParam.get(MyPriceConstants.MP_PRODUCT_LINE_ID)!=null?
				(String)methodParam.get(MyPriceConstants.MP_PRODUCT_LINE_ID):"";
		if(StringUtils.isNotEmpty(respParentId) && 
				reqProductLineId.equals(respParentId)) {
			return true;		
		}	
		return false;
	}
	
	
	@SuppressWarnings("unchecked")
	protected void processDesignBlock(ConfigRespProcessingBean input,Map<String,Object> methodParam) throws SalesBusinessException {
		try {
			Map<String,String> productIdSolutionIdMap=(Map<String, String>) methodParam.get(MyPriceConstants.PRODUCT_ID_SOLUTION_ID_MAP);
			String flowType=methodParam.get(MyPriceConstants.FLOW_TYPE)!=null?
							(String)methodParam.get(MyPriceConstants.FLOW_TYPE):null;		
			String mpProductLineId=	input.getParentDocNumber();	
			String mpSolutionId=productIdSolutionIdMap.get(mpProductLineId);
			Long nxTxnId=methodParam.get(MyPriceConstants.NX_TRANSACTION_ID)!=null?
					(Long)methodParam.get(MyPriceConstants.NX_TRANSACTION_ID):null;
			this.processResponseUscoCode(input,methodParam);
			if(StringUtils.isNotEmpty(flowType)) {
				if(MyPriceConstants.SOURCE_FMO.equals(flowType) 
					|| MyPriceConstants.SOURCE_INR.equals(flowType)) {

					Long nxDesignId=methodParam.get(MyPriceConstants.NX_DESIGN_ID)!=null?
							(Long)methodParam.get(MyPriceConstants.NX_DESIGN_ID):null;
					
					if(StringUtils.isNotEmpty(mpSolutionId)) {
						//PORT_ID applicable for FMO
						String portId=methodParam.get(FmoConstants.PORT_ID)!=null?
								(String)methodParam.get(FmoConstants.PORT_ID):"";
						List<NxMpDesignDocument> checkDesignForUpdate=nxMpDesignDocumentRepo.checkDesignForUpdate(mpSolutionId,
								input.getParentDocNumber(), input.getNxTransactionId());
						if(CollectionUtils.isEmpty(checkDesignForUpdate)) {
							createNewNxMpDesignDocument(input, nxDesignId, mpSolutionId,portId);
						}else {
							nxMpDesignDocumentRepo.updateDesignBySolIdAndProductIdFmo(Long.valueOf(input.getDocumentNumber()),
									input.getUsocCode(),portId,new Date(),mpSolutionId,
									input.getParentDocNumber(),input.getNxTransactionId());
						}
					}	
				
				}else if(MyPriceConstants.SOURCE_IGLOO.equals(flowType) ) {
					Long nxDesignId=methodParam.get(MyPriceConstants.NX_ACCESS_PRICE_ID)!=null?
							(Long)methodParam.get(MyPriceConstants.NX_ACCESS_PRICE_ID):null;
					List<NxMpDesignDocument>  designDocuments = nxMpDesignDocumentRepo.findByNxTxnIdAndNxDesignId(nxTxnId,nxDesignId);
					if(CollectionUtils.isEmpty(designDocuments)) {
						createNewNxMpDesignDocument(input, nxDesignId, mpSolutionId,null);
					}else {
						nxMpDesignDocumentRepo.updateDesignBySolIdAndProductId(Long.valueOf(input.getDocumentNumber()),
								input.getUsocCode(),new Date(),mpSolutionId,
								input.getParentDocNumber(),input.getNxTransactionId());
						methodParam.put(mpSolutionId, true);
					}
				}
			}
				
			
		}catch(Exception e) {
			log.error("Exception during processDesign", e);
			throw new SalesBusinessException(e.getMessage());
		}
		
	}
	
	
	protected void createNewNxMpDesignDocument(ConfigRespProcessingBean input, Long nxDesignId, 
			String mpSolutionId,String mpPartNumber) {
		NxMpDesignDocument entity=new NxMpDesignDocument();
		entity.setNxDesignId(nxDesignId);
		entity.setMpSolutionId(mpSolutionId);
		entity.setMpProductLineId(input.getParentDocNumber());
		entity.setMpDocumentNumber(Long.valueOf(input.getDocumentNumber()));
		entity.setNxTxnId(input.getNxTransactionId());
		entity.setUsocId(input.getUsocCode());
		entity.setMpPartNumber(mpPartNumber);
		entity.setActiveYN(com.att.sales.nexxus.common.CommonConstants.ACTIVE_Y);
		entity.setCreatedDate(new Date());
		nxMpDesignDocumentRepo.save(entity);
	}
	
	@SuppressWarnings("unchecked")
	protected void processResponseUscoCode(ConfigRespProcessingBean input,Map<String,Object> methodParam) {
		if(StringUtils.isNotEmpty(input.getUsocCode())) {
			if(methodParam.containsKey(MyPriceConstants.UNIQUEID_BEID_MAP)) {
				Map<String,String> uniqueIdBeidMap=(Map<String, String>) methodParam.get(MyPriceConstants.UNIQUEID_BEID_MAP);
				input.setUsocCode(uniqueIdBeidMap.get(input.getUsocCode()));
			}else {
				input.setUsocCode(null);
			}
		}
	}
	
}
