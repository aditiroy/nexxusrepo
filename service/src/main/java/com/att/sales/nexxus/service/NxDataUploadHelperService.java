package com.att.sales.nexxus.service;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TimeZone;
import java.util.stream.Stream;

import javax.transaction.Transactional;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.admin.model.DataUploadRequest;
import com.att.sales.nexxus.admin.model.ExcelPojoMapper;
import com.att.sales.nexxus.admin.model.ExcelReader;
import com.att.sales.nexxus.admin.model.ExcelValueConfig;
import com.att.sales.nexxus.common.MessageConstants;
import com.att.sales.nexxus.constant.DataUploadConstants;
import com.att.sales.nexxus.dao.model.LittleProductDataEntity;
import com.att.sales.nexxus.dao.model.LookupDataMapping;
import com.att.sales.nexxus.dao.model.NxAdminAuditData;
import com.att.sales.nexxus.dao.model.NxLineItemLookUpDataModel;
import com.att.sales.nexxus.dao.model.NxLineItemLookUpStagingModel;
import com.att.sales.nexxus.dao.repository.DataUploadLookupRepository;
import com.att.sales.nexxus.dao.repository.LittleProductRepo;
import com.att.sales.nexxus.dao.repository.NxAdminAuditDataRepository;
import com.att.sales.nexxus.dao.repository.NxLineItemLookUpStagingRepository;
import com.github.pjfanning.xlsx.StreamingReader;

/**
 * The Class NxDataUploadHelperService.
 */
/**
 * @author vt393d
 *
 */
@Service
public class NxDataUploadHelperService {
	
	/** The log. */
	private static Logger log = LoggerFactory.getLogger(NxDataUploadHelperService.class);
	
	/** The data upload lookup repo. */
	@Autowired
	private DataUploadLookupRepository dataUploadLookupRepo;
	
	/** The excel reader. */
	@Autowired
	private ExcelReader excelReader;
	
	/** The nx line item look up staging repo. */
	@Autowired
	private NxLineItemLookUpStagingRepository nxLineItemLookUpStagingRepo;
	
	/** The little product repo. */
	@Autowired
	private LittleProductRepo littleProductRepo;
	
	/** The nx admin audit data repository. */
	@Autowired
	private NxAdminAuditDataRepository nxAdminAuditDataRepository;
		
	/** The mail service. */
	@Autowired
	private MailService mailService;

	/**
	 * Upload data file.
	 *
	 * @param request the request
	 */
	@Transactional(rollbackOn = Exception.class)
	public void uploadDataFile(DataUploadRequest request) {
		log.info("Inside uploadDataFile method  {}","");
		try {
			Long littleId=StringUtils.isNotEmpty(request.getId())?Long.valueOf(request.getId()):0l;
			LittleProductDataEntity littleProductData=this.getLittleProductDataById(littleId);
			Map<String,Object> inputmap=new HashMap<>();
			
			inputmap.put(DataUploadConstants.TOP_PROD_ID, littleProductData.getTopProductData().getTopProductId());
			inputmap.put(DataUploadConstants.LITTLE_PROD_ID, littleProductData.getLittleProductId());
			inputmap.put(DataUploadConstants.USER_ID, request.getUserId());
			 
			if(StringUtils.isNotEmpty(request.getActivity()) && request.getActivity().equalsIgnoreCase(DataUploadConstants.ACTIVE)) {
				List<NxLineItemLookUpDataModel> savedEntities=nxLineItemLookUpStagingRepo.activateLineItemData(inputmap);
				if(CollectionUtils.isNotEmpty(savedEntities)) {
					//create idRangeString using first starting and ending record id
					String idRangeString=savedEntities.get(0).getNxItemId()+"##"+savedEntities.get(savedEntities.size() - 1).getNxItemId();
					inputmap.put(DataUploadConstants.ID_RANGE, idRangeString);
					this.saveDataInAdminAuditTable(inputmap,DataUploadConstants.AUDIT_DISCRIPTION.ACTIVATE.getValue());
					mailService.dataUploadMailNotification(inputmap, request.getUserId());
				}
				
			}else {
				this.deleteStaggingData(inputmap);
				List<NxLineItemLookUpStagingModel> inputData=
						readExcelFile(request.getInputStream(),littleProductData,NxLineItemLookUpStagingModel.class,inputmap);
				this.uploadDataInStaggingTable(inputmap, inputData);
			}
		} catch (Exception e) {
			log.error("Exception in file", e);
		}
		
	}
	
	
	/**
	 * Upload data in stagging table.
	 *
	 * @param inputmap the inputmap
	 * @param inputData the input data
	 */
	protected void uploadDataInStaggingTable(Map<String,Object> inputmap,List<NxLineItemLookUpStagingModel> inputData) {
		List<NxLineItemLookUpStagingModel> savedEntities=nxLineItemLookUpStagingRepo.bulkSave(inputData);
		if(CollectionUtils.isNotEmpty(savedEntities)) {
			//create idRangeString using first starting and ending record id
			String idRangeString=savedEntities.get(0).getNxItemId()+"##"+savedEntities.get(savedEntities.size()- 1).getNxItemId();
			inputmap.put(DataUploadConstants.ID_RANGE, idRangeString);
			this.saveDataInAdminAuditTable(inputmap,DataUploadConstants.AUDIT_DISCRIPTION.UPLOAD.getValue());
		}	
					
		
	}
	
	/**
	 * Delete stagging data.
	 *
	 * @param inputmap the inputmap
	 */
	protected void deleteStaggingData(Map<String,Object> inputmap) {
		Long littleProdId=null!=inputmap.get(DataUploadConstants.LITTLE_PROD_ID)?
				Long.valueOf(inputmap.get(DataUploadConstants.LITTLE_PROD_ID).toString()):null;
		Long topProdId=null!=inputmap.get(DataUploadConstants.TOP_PROD_ID)?
				Long.valueOf(inputmap.get(DataUploadConstants.TOP_PROD_ID).toString()):null;
		List<Long> deleteDataIds=nxLineItemLookUpStagingRepo.getIdByLittleAndTopProdId(littleProdId, topProdId);
		//List<NxLineItemLookUpStagingModel> deleteEntities=nxLineItemLookUpStagingRepo.getStaggingDataByLittleAndTopProdId(littleProdId, topProdId)
		if(CollectionUtils.isNotEmpty(deleteDataIds)) {
			nxLineItemLookUpStagingRepo.deleteRecordByIds(littleProdId, topProdId);
			//nxLineItemLookUpStagingRepo.bulkDelete(deleteEntities)
			//create idRangeString using first starting and ending record id
			String idRangeString=deleteDataIds.get(0)+"##"+deleteDataIds.get(deleteDataIds.size()- 1);
			inputmap.put(DataUploadConstants.ID_RANGE, idRangeString);
			this.saveDataInAdminAuditTable(inputmap,DataUploadConstants.AUDIT_DISCRIPTION.DELETE_STAGGING_DATA.getValue());		
		}
	}
	
	/**
	 * Gets the little product data by id.
	 *
	 * @param id the id
	 * @return the little product data by id
	 */
	protected LittleProductDataEntity  getLittleProductDataById(Long id) {
		return littleProductRepo.findByLittleId(id);
	}
	
	
	/**
	 * Read excel file.
	 *
	 * @param <T> the generic type
	 * @param inputFile the input file
	 * @param data the data
	 * @param type the type
	 * @param inputmap the inputmap
	 * @return the list
	 * @throws SalesBusinessException the sales business exception
	 */
	public  <T> List<T> readExcelFile(InputStream inputFile,LittleProductDataEntity data,Class<T> type,
			Map<String,Object> inputmap) throws SalesBusinessException {
		log.info("Inside readExcelFile method  {}","");
    	Workbook workbook=null;
    	try{
    		 workbook = StreamingReader.builder()
        	        .rowCacheSize(100)    
        	        .bufferSize(4096)     
        	        .open(inputFile); 	 
    		 Map<String, List<ExcelValueConfig[]>> excelRowValuesMap=new HashMap<>();
    		 Map<String,List<LookupDataMapping>> mappinglookupData=this.getLookupMappingData(data.getLittleId());
    		 Sheet sheet=workbook.getSheetAt(0);
    		 if(null!=sheet) {
    			 excelRowValuesMap=excelReader.getInputExcelRowValues(sheet,mappinglookupData,inputmap);
    			 if(excelRowValuesMap.containsKey(DataUploadConstants.FILE_DATA)) {
    				 return ExcelPojoMapper.getPojos(excelRowValuesMap.get(DataUploadConstants.FILE_DATA),
            				 type);
    			 }
    			
    		 }
    		 return new ArrayList<>();
    		
    	} catch (Exception e) {
			log.error("Exception in file", e);
			throw new SalesBusinessException(MessageConstants.PROCESS_ERROR_CODE);
		}finally {
    		try {
    			if(null!=workbook)
				workbook.close();
			} catch (IOException e) {
			}
    	}
    	
    	
    }

	
	/**
	 * Gets the lookup mapping data.
	 *
	 * @param littelId the littel id
	 * @return the lookup mapping data
	 */
	protected Map<String,List<LookupDataMapping>> getLookupMappingData(Long littelId){
		log.info("Inside getLookupMappingData method  {}","");
		List<LookupDataMapping> mappingDataLst=dataUploadLookupRepo.findByLittleId(littelId);
		Map<String,List<LookupDataMapping>> resultMap=new HashMap<>();
		Optional.ofNullable(mappingDataLst).map(List::stream).orElse(Stream.empty()).filter(Objects::nonNull).forEach( data -> {
			if(!resultMap.containsKey(data.getFlowType())) {
				resultMap.put(data.getFlowType(), new ArrayList<LookupDataMapping>());
			}
			resultMap.get(data.getFlowType()).add(data);
		});
		return resultMap;
	}
	
	
	/**
	 * Save data in admin audit table.
	 *
	 * @param inputmap the inputmap
	 * @param discription the discription
	 */
	protected  void saveDataInAdminAuditTable(Map<String,Object> inputmap,String discription){
		NxAdminAuditData adminAudit=new NxAdminAuditData();
		adminAudit.setLittleProductId(null!=inputmap.get(DataUploadConstants.LITTLE_PROD_ID)?
				Long.valueOf(inputmap.get(DataUploadConstants.LITTLE_PROD_ID).toString()):null);
		adminAudit.setTopProductid(null!=inputmap.get(DataUploadConstants.TOP_PROD_ID)?
				Long.valueOf(inputmap.get(DataUploadConstants.TOP_PROD_ID).toString()):null);
		adminAudit.setUserId(null!=inputmap.get(DataUploadConstants.USER_ID)?
				String.valueOf(inputmap.get(DataUploadConstants.USER_ID)):null);
		adminAudit.setDiscription(discription);
		adminAudit.setNxItemIds(inputmap.containsKey(DataUploadConstants.ID_RANGE)?
				String.valueOf(inputmap.get(DataUploadConstants.ID_RANGE)):null);
	    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date date=new Date();
		dateFormat.format(date);
		adminAudit.setCreatedDate(date);
		nxAdminAuditDataRepository.saveAndFlush(adminAudit);
	}
	
	
}
