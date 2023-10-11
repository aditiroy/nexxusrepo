package com.att.sales.nexxus.inr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.model.NxOutputFileModel;
import com.att.sales.nexxus.dao.model.NxRequestDetails;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.dao.repository.NxOutputFileRepository;
import com.att.sales.nexxus.dao.repository.NxRequestDetailsRepository;
import com.att.sales.nexxus.util.NexxusJsonUtility;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author ar896d
 *
 */
@Component
public class CdirEdit {

	@Autowired
	private NexxusJsonUtility nexxusJsonUtility;

	@Autowired
	private NxLookupDataRepository nxLookupDataRepository;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private NxOutputFileRepository nxOutputFileRepository;
	
	@Autowired
	private NxRequestDetailsRepository nxRequestDetailsRepository;

	public static final String CDIR_DATA_EDIT = "CDIR_DATA_EDIT";
	public static final String DATA_UPDATE = "Data Update";
	public static final String NX_SITE_ID_FIELD = "NX_Site_ID";
	public static final String ADDRESS_UPDATE="Address Update";
	public static final String SITE_lOCATOR="siteLocator";
	public static final String MAINSHEET="mainSheet";
	public static final String NX_SITE_ID_PATH="nxSiteIdPath";
	public static final String NX_SITE_ID_Z="nxSiteIdZ";
	public static final String NX_SITE_ID="nxSiteId";
	public static final String DOMESTIC_PL_IOC="DOMESTIC PL IOC";
	public static final String MIS_PNT="MIS/PNT";
	public static final String POPCLLI="POPCLLI";
	public static final String CLLIZEND="CLLIZEND";
	public static final String NPA_NXX="NPA-NXX";
	public static final String NPA="NPA";
	public static final String NXX="NXX";
	private static Logger logger = LoggerFactory.getLogger(CdirEdit.class);
	

	/**
	 * To update the CDIR inr data  
	 * 
	 * @param dataMap
	 * 
	 * datamap is map of excel row
	 * key:INR edit excel column name
	 * value: excel value
	 * 
	 * @param requestId
	 */
	public void updateCdirData(Map<String, LinkedHashMap<String, Object>> dataMap, Long requestId) {
		logger.info("Request data map for cdir update " + dataMap);
		logger.info("request Id " + requestId);
		NxRequestDetails nxRequestDetails =nxRequestDetailsRepository.findByNxReqId(requestId);
		String product= nxRequestDetails.getProduct();
		List<NxOutputFileModel> nxOutputFileModels = nxRequestDetails.getNxOutputFiles();
		List<NxLookupData> nxLookupDetail = nxLookupDataRepository
				.findByDatasetNameAndItemIdAndDescription(CDIR_DATA_EDIT, product, DATA_UPDATE);
		NxLookupData ruleObj = CollectionUtils.isNotEmpty(nxLookupDetail) ? nxLookupDetail.get(0) : null;
		NxOutputFileModel nxOutput = CollectionUtils.isNotEmpty(nxOutputFileModels) ? nxOutputFileModels.get(0) : null;
		String cdirdata = nxOutput != null ? nxOutput.getCdirData() : null;
		if (nxOutput != null && ruleObj != null && cdirdata != null) {
			try {
				LinkedHashMap<String, Object> criteriaMap = (LinkedHashMap<String, Object>) nexxusJsonUtility
						.convertStringJsonToMap(ruleObj.getCriteria());
				JsonNode nodes = mapper.readTree(cdirdata);
				for (Map.Entry<String, LinkedHashMap<String, Object>> x : dataMap.entrySet()) {
					int nxSiteMatchingIdnodeValue = Integer.parseInt(x.getKey());
					if (nodes.has(MAINSHEET)) {
						JsonNode mainsheetNodes = nodes.get("mainSheet");
						for (JsonNode mainSheetNode : mainsheetNodes) {
							if (mainSheetNode.has(InrIntermediateJsonGenerator.NXSITEMATCHINGID) && 
									nxSiteMatchingIdnodeValue==mainSheetNode.path(InrIntermediateJsonGenerator.NXSITEMATCHINGID).asInt()) {
								updateNode(mainSheetNode, x.getValue(), criteriaMap);
							}						
						}
					}
				}
				String newCdirData = nodes.toString();
				nxOutput.setCdirData(newCdirData);
				nxOutputFileRepository.save(nxOutput);
			} catch (IOException e) {
				logger.info("Exception in cdir data update " + e);
			}
		}
	}

	protected void updateNode(JsonNode node,  LinkedHashMap<String, Object> rowMap,
			LinkedHashMap<String, Object> criteriaMap) {
			for (Map.Entry<String, Object> x : rowMap.entrySet()) {
				String field = x.getKey().trim();
				if (x.getValue() instanceof String) {
					String newVal = (String) x.getValue();
					if (StringUtils.isNotBlank(newVal)) {
						for (Map.Entry<String, Object> c : criteriaMap.entrySet()) {
							if (node.has(c.getKey().trim())) {
								if (c.getValue() instanceof Map<?, ?>) {
									Map<String, String> criteriaMapData = (Map<String, String>) c.getValue();
										String rootPath = criteriaMapData.get("rootPath").trim();
										if (criteriaMapData.containsKey(field)) {
											String subPath = criteriaMapData.get(field).trim();
											String path = rootPath + subPath;
											ObjectNode nodeObject = (ObjectNode) node;
											nodeObject.put(path, newVal);
										}
								}
							}
						}
					}
				}
			}
	}
	
	/**
	 * @param datamap
	 * 
	 * datamap is list of map of excel row
	 * key:Inr address excel column name
	 * value : excel value
	 * 
	 * Long is request Id
	 * 
	 * 
	 */
	public void updateCdirAddressData(Map<Long, List<LinkedHashMap<String, Object>>> datamap) {
		logger.info("Request data map for cdir address update " + datamap);
		for (Map.Entry<Long, List<LinkedHashMap<String, Object>>> x : datamap.entrySet()) {
			Long requestId = x.getKey();
			NxRequestDetails nxRequestDetails =nxRequestDetailsRepository.findByNxReqId(requestId);
			String product= nxRequestDetails.getProduct();
			List<NxOutputFileModel> nxOutputFileModels = nxRequestDetails.getNxOutputFiles();
			List<NxLookupData> nxLookupDetail = nxLookupDataRepository
					.findByDatasetNameAndItemIdAndDescription(CDIR_DATA_EDIT, product, ADDRESS_UPDATE);
			NxLookupData ruleObj = CollectionUtils.isNotEmpty(nxLookupDetail) ? nxLookupDetail.get(0) : null;
			NxOutputFileModel nxOutput = CollectionUtils.isNotEmpty(nxOutputFileModels) ? nxOutputFileModels.get(0) : null;
			String cdirdata = nxOutput != null ? nxOutput.getCdirData() : null;
			if (nxOutput != null && ruleObj != null && cdirdata != null) {
				try {
					LinkedHashMap<String, Object> criteriaMap = (LinkedHashMap<String, Object>) nexxusJsonUtility
						.convertStringJsonToMap(ruleObj.getCriteria());
					JsonNode node = mapper.readTree(cdirdata);
					LinkedHashMap<String, String>  nodeLocatorMap=criteriaMap.get(SITE_lOCATOR)!=null?
							(LinkedHashMap<String, String>)criteriaMap.get(SITE_lOCATOR):null;
							String jsonPath = nodeLocatorMap.get(NX_SITE_ID_PATH);
							List<String> nxsiteIdpathList = new ArrayList<>();
							if (jsonPath.contains(MyPriceConstants.OR_CONDITION_SEPERATOR)) {
								 nxsiteIdpathList = new ArrayList<String>(Arrays.asList(nodeLocatorMap.get(NX_SITE_ID_PATH).split("\\|\\|")));
							}else {
								nxsiteIdpathList= new ArrayList<String>(Arrays.asList(jsonPath));
							}
						for (LinkedHashMap<String, Object> row : x.getValue()) {
							String rowNxSiteId = row.containsKey(NX_SITE_ID_FIELD) ? (String) row.get(NX_SITE_ID_FIELD)
									: null;
							if (rowNxSiteId != null) {
								if (node.has(MAINSHEET)) {
									JsonNode mainsheetNodes = node.get("mainSheet");
									for (JsonNode mainSheetNode : mainsheetNodes) {
										for (String field : nxsiteIdpathList) {
											String nxSiteIdfield=field.trim();
											if (mainSheetNode.has(nxSiteIdfield) && Integer
													.parseInt(rowNxSiteId) == mainSheetNode.path(nxSiteIdfield).asInt()) {
												if(DOMESTIC_PL_IOC.equalsIgnoreCase(product)) {
													updateInputdata(row,nxSiteIdfield);
												}
												if(MIS_PNT.equalsIgnoreCase(product)) {
													updateMisData(row);
												}
												updateNode(mainSheetNode, row, criteriaMap);
											}
										}
									}
								}
							}
						}
						String newCdirData = node.toString();
						nxOutput.setCdirData(newCdirData);
						nxOutputFileRepository.saveAndFlush(nxOutput);
				} catch (IOException e) {
					logger.info("Exception in cdir address update " + e);
				}
			}
		}
		
	}
	
	protected void updateInputdata(LinkedHashMap<String, Object> row,String nxSiteIdField) {
		if(NX_SITE_ID_Z.equalsIgnoreCase(nxSiteIdField) && row.containsKey(POPCLLI)) {
			row.put(CLLIZEND,row.get(POPCLLI));
			row.remove(POPCLLI);
		}
	}
	
	protected void updateMisData(LinkedHashMap<String, Object> row) {
		if(row.containsKey(NPA_NXX)) {
			String npanxx=(String)row.get(NPA_NXX);
			if(npanxx.length()>=3) {
				String npa=npanxx.substring(0,3);
				String nxx=npanxx.substring(3,npanxx.length());
				row.put(NPA, npa);
				row.put(NXX, nxx);
			}
		}
	}
}