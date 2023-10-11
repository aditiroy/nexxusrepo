package com.att.sales.nexxus.myprice.transaction.service;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyMap;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doThrow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.dao.model.NxMpDesignDocument;
import com.att.sales.nexxus.dao.repository.NxMpDesignDocumentRepository;
import com.oracle.xmlns.cpqcloud.configuration.wireline.solution.solution.CommonStatusType;
import com.oracle.xmlns.cpqcloud.configuration.wireline.solution.solution.ConfigureResponse;
import com.oracle.xmlns.cpqcloud.configuration.wireline.solution.solution.ObjectFactory;

@ExtendWith(MockitoExtension.class)

public class ConfigDesignHelperServiceTest {
	
	@InjectMocks
	@Spy
	private ConfigDesignHelperService configDesignHelperService;
	
	@Mock
	private NxMpDesignDocumentRepository nxMpDesignDocumentRepo;
	
	private Map<String, Object> methodParam;
	
	@BeforeEach
	public void init() {
		methodParam = new HashMap<String, Object>();
		methodParam.put(MyPriceConstants.NX_TRANSACTION_ID, 1010l);
		methodParam.put(MyPriceConstants.MP_TRANSACTION_ID, "101010");
		methodParam.put(MyPriceConstants.NX_DESIGN_ID, 2000l);
		methodParam.put(MyPriceConstants.CONFIG_DESIGN_RESPONSE, getConfigureResponse());
	}
	
	@Test
	public void testProcessConfigDesignResponse() {
		Map<String,Map<String,Object>> dataMapByProductId = new HashMap<String,Map<String,Object>>();
		dataMapByProductId.put("2", new HashMap<String,Object>());
		methodParam.put(MyPriceConstants.CONFIG_RESPONSE_PROCESSING_DATA, dataMapByProductId);
		methodParam.put(MyPriceConstants.FLOW_TYPE, "INR");
		Mockito.when(nxMpDesignDocumentRepo.checkDesignForUpdate(anyString(), anyString(), anyLong())).thenReturn(null);
		configDesignHelperService.processConfigDesignResponse(methodParam);
		
		methodParam.put(MyPriceConstants.FLOW_TYPE, "IGLOO");
		Mockito.when(nxMpDesignDocumentRepo.findByNxTxnIdAndNxDesignId(anyLong(), anyLong())).thenReturn(null);
		configDesignHelperService.processConfigDesignResponse(methodParam);
		
		methodParam.put(MyPriceConstants.FLOW_TYPE, "IGLOO");
		List<NxMpDesignDocument> designDocuments = new ArrayList<NxMpDesignDocument>();
		designDocuments.add(new NxMpDesignDocument());
		Mockito.when(nxMpDesignDocumentRepo.findByNxTxnIdAndNxDesignId(anyLong(), anyLong())).thenReturn(designDocuments);
		Mockito.when(nxMpDesignDocumentRepo.updateDesignBySolIdAndProductId(anyLong(), anyString(), any(), anyString(), anyString(), anyLong())).thenReturn(1);
		configDesignHelperService.processConfigDesignResponse(methodParam);
	}
	
	@Test
	public void testProcessConfigDesignResponseExc() throws SalesBusinessException {
		doThrow(new SalesBusinessException()).when(configDesignHelperService).processConfigResponse(any(), anyMap());
		configDesignHelperService.processConfigDesignResponse(methodParam);
	}
	
	@Test
	public void testIsProductLineIdMatchForConfigDesign() {
		Map<String,Object> methodParam = new HashMap<String,Object>();
		methodParam.put(MyPriceConstants.MP_PRODUCT_LINE_ID, "3");
		configDesignHelperService.isProductLineIdMatchForConfigDesign(methodParam, "3");
	}
	
	private ConfigureResponse getConfigureResponse() {
		ConfigureResponse response = new ConfigureResponse();
		ObjectFactory objectFactory = new ObjectFactory();
		response.setTransaction(
				objectFactory.createConfigureResponseTransaction(objectFactory.createTransactionType()));
		response.getTransaction().getValue().setDataXml(objectFactory.createAnyType());
		response.getTransaction().getValue().getDataXml().getAny().add(createTransactionElement());
		CommonStatusType commonstatus = new CommonStatusType();
		commonstatus.setSuccess(new JAXBElement<String>(new QName("success"), String.class, "true"));
		response.setStatus(commonstatus);
		return response;
	}
	
	private Element createTransactionElement() {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		Document document = db.newDocument();

		Element transaction = document.createElement("bmt:transaction");
		Element subDocuments = document.createElement("bmt:sub_documents");

		Element transactionLine1 = document.createElement("bmt:transactionLine");
		Element nxSiteId = document.createElement("bmt:lii_nxSiteId_ql");
		Element adeSiteReln = document.createElement("bmt:wl_int_ade_site_reln");
		Element lineBomId = document.createElement("bmt:_line_bom_id");
		Element lineBomParentId = document.createElement("bmt:_line_bom_parent_id");
		Element documentNumber = document.createElement("bmt:_document_number");
		Element parentDocNumber = document.createElement("bmt:_parent_doc_number");
		Element parentLineItem = document.createElement("bmt:_parent_line_item");
		Element uniqueId = document.createElement("bmt:wi_uniqueID_ql");
		Element partNumber = document.createElement("bmt:_line_bom_part_number");
		
		lineBomId.appendChild(document.createTextNode("BOM_Solution"));
		lineBomParentId.appendChild(document.createTextNode("BOM_Solution"));
		documentNumber.appendChild(document.createTextNode("3"));
		parentLineItem.appendChild(document.createTextNode("37903795"));
		parentDocNumber.appendChild(document.createTextNode("2"));
		nxSiteId.appendChild(document.createTextNode("10"));
		adeSiteReln.appendChild(document.createTextNode("111"));
		uniqueId.appendChild(document.createTextNode("ADE UNIQUEID"));
		partNumber.appendChild(document.createTextNode("3"));

		transactionLine1.appendChild(lineBomId);
		transactionLine1.appendChild(lineBomParentId);
		transactionLine1.appendChild(documentNumber);
		transactionLine1.appendChild(parentLineItem);
		transactionLine1.appendChild(parentDocNumber);
		transactionLine1.appendChild(nxSiteId);
		transactionLine1.appendChild(adeSiteReln);
		transactionLine1.appendChild(uniqueId);
		transactionLine1.appendChild(partNumber);
		
		Element transactionLine2 = document.createElement("bmt:transactionLine");
		Element nxSiteId2 = document.createElement("bmt:lii_nxSiteId_ql");
		Element adeSiteReln2 = document.createElement("bmt:wl_int_ade_site_reln");
		Element lineBomId2 = document.createElement("bmt:_line_bom_id");
		Element lineBomParentId2 = document.createElement("bmt:_line_bom_parent_id");
		Element documentNumber2 = document.createElement("bmt:_document_number");
		Element parentDocNumber2 = document.createElement("bmt:_parent_doc_number");
		Element parentLineItem2 = document.createElement("bmt:_parent_line_item");
		Element uniqueId2 = document.createElement("bmt:wi_uniqueID_ql");
		Element partNumber2 = document.createElement("bmt:_line_bom_part_number");
		
		lineBomId2.appendChild(document.createTextNode("Solution"));
		lineBomParentId2.appendChild(document.createTextNode("Solution"));
		documentNumber2.appendChild(document.createTextNode("4"));
		parentLineItem2.appendChild(document.createTextNode("37903795"));
		parentDocNumber2.appendChild(document.createTextNode("3"));
		nxSiteId2.appendChild(document.createTextNode("10"));
		adeSiteReln2.appendChild(document.createTextNode("111"));
		uniqueId2.appendChild(document.createTextNode("ADE UNIQUEID"));
		partNumber2.appendChild(document.createTextNode("3"));

		transactionLine2.appendChild(lineBomId2);
		transactionLine2.appendChild(lineBomParentId2);
		transactionLine2.appendChild(documentNumber2);
		transactionLine2.appendChild(parentLineItem2);
		transactionLine2.appendChild(parentDocNumber2);
		transactionLine2.appendChild(nxSiteId2);
		transactionLine2.appendChild(adeSiteReln2);
		transactionLine2.appendChild(uniqueId2);
		transactionLine2.appendChild(partNumber2);
		
		transactionLine1.setAttribute("bmt:bs_id", "101010");
		subDocuments.appendChild(transactionLine1);
		//subDocuments.appendChild(transactionLine2);
		transaction.appendChild(subDocuments);
		document.appendChild(transaction);
		return transaction;
	}
	

}
