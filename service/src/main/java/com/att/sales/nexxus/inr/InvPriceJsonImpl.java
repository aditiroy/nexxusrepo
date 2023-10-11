package com.att.sales.nexxus.inr;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.assertj.core.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.service.BaseServiceImpl;
import com.att.sales.nexxus.constant.InrConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.dao.model.NxDesignAudit;
import com.att.sales.nexxus.dao.model.NxDwPriceDetails;
import com.att.sales.nexxus.dao.model.NxDwToJsonRules;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.model.NxRequestDetails;
import com.att.sales.nexxus.dao.repository.NxDesignAuditRepository;
import com.att.sales.nexxus.dao.repository.NxDwInventoryDao;
import com.att.sales.nexxus.dao.repository.NxDwPriceDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxDwToJsonRulesRepository;
import com.att.sales.nexxus.dao.repository.NxDwUbCallInventoryDataDao;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.dao.repository.NxOneNetInventoryDataDao;
import com.att.sales.nexxus.dao.repository.NxRequestDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxVtnsLDCallInventoryDataDao;
import com.att.sales.nexxus.dao.repository.UsrpDao;
import com.att.sales.nexxus.service.NxMyPriceRepositoryServce;
import com.att.sales.nexxus.util.JacksonUtil;
import com.att.sales.nexxus.util.LogUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class InvPriceJsonImpl extends BaseServiceImpl {
	private static Logger logger = LoggerFactory.getLogger(InvPriceJsonImpl.class);
	private static int chunkSize = 1000;
	private static String RULE_NAME = "INV_PRICE_JSON";
	private static String Y = "Y";
	private static final List<String> MODULE_CARD_PBIS = Arrays.asList("00070436", "00079022", "00079023");
	/** The Constant BILLED_MINS_PBI_DESCS. */
	private static final List<String> BILLED_MINS_PBI_DESCS = Arrays.asList("VOAVPN INT L OFFNET",
			"VOAVPN INTERNATIONAL OFF-NET", "VDNASB INTERNATIONAL OFF-NET", "VOIP INTERNATIONAL OFF-NET",
			"VOAVPN US OFF-NET LD", "VDNASB US OFF-NET LD", "VDNASB US ON-NET LD", "VOAVPN US OFFNET LD",
			"IPTF INTERSTATE USAGE", "IPTF INTRASTATE USAGE", "IPTF INTRALATA USAGE", "IPTF CANADA USAGE",
			"VOAVPN IPTF INTERSTATE USAGE", "VOAVPN IPTF INTRASTATE USAGE", "VOAVPN IPTF INTRALATA USAGE",
			"VOAVPN IPTF CANADA USAGE", "VOAVPN IPTF MOW USAGE");
	
	/** The Constant BILLED_MINS_PBI. */
	private static final List<String> BILLED_MINS_PBI = Arrays.asList("00070467", "00092978", "00079044", "00082410",
			"00093908");
	
	/** The Constant BILLED_MINS_PBI_DESC. */
	private static final String BILLED_MINS_PBI_DESC = "VoIP US Off-Net LD";

	@Autowired
	private NxDesignAuditRepository nxDesignAuditRepository;
	@Autowired
	private NxDwInventoryDao nxDwInventoryDao;
	@Autowired
	private NxRequestDetailsRepository nxRequestDetailsRepository;
	@Autowired
	private NxDwToJsonRulesRepository nxDwToJsonRulesRepository;
	@Autowired
	private NxDwPriceDetailsRepository nxDwPriceDetailsRepository;
	@Autowired
	private ObjectMapper mapper;
	@Autowired
	private NxLookupDataRepository nxLookupDataRepository;
	@Autowired
	private NxMyPriceRepositoryServce nxMyPriceRepositoryServce;
	@Autowired
	private UsrpDao usrpDao;
	@Autowired
	private NxDwUbCallInventoryDataDao nxDwUbCallInventoryDataDao;
	@Autowired
	private NxOneNetInventoryDataDao nxOneNetInventoryDataDao;
	@Autowired
	private NxVtnsLDCallInventoryDataDao nxVtnsLDCallInventoryDataDao;
	
	public InvPriceJsonResponse invPriceJson(InvPriceJsonRequest request) throws SalesBusinessException {
		Long startTime = System.currentTimeMillis();
		logger.info("enter inventory price json creation for nx request id {}", request.getNxReqId());
		InvPriceJsonResponse response = new InvPriceJsonResponse();
		try {
			NxRequestDetails nxRequestDetails = nxRequestDetailsRepository.findByNxReqId(request.getNxReqId());
			String priceJson = createJsonNodeString1(nxRequestDetails);
			NxDwPriceDetails nxDwPriceDetails = new NxDwPriceDetails();
			nxDwPriceDetails.setCreatedDate(new Date());
			nxDwPriceDetails.setNxReqId(request.getNxReqId());
			nxDwPriceDetails.setPriceJson(priceJson);
			NxDwPriceDetails savedentity = nxDwPriceDetailsRepository.save(nxDwPriceDetails);
			Long savedEntityPricedtailsId = savedentity.getId();
			response.setNxDwPriceId(savedEntityPricedtailsId);
		} catch (Exception e) {
			logger.info("Exception:", e);
			throw new SalesBusinessException();
		}
		LogUtils.logExecutionDurationMs(logger, String.format("Total inventory price json creation for nx request id %d", request.getNxReqId()), startTime);
		return response;
	}

	/*
	protected String createJsonNodeString(NxRequestDetails nxRequestDetails)
			throws JsonMappingException, JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		Args args = initializeArgs(nxRequestDetails.getProduct());
		JsonNode node = mapper.readTree("{}");
		args.nodeMap.put("root", node);
		String searchCriteria = nxRequestDetails.getManageBillingPriceJson();
		long lastId = 0;
		long totalRecords = nxDwInventoryDao.getTotalCountBySearchCriteria(searchCriteria,
				nxRequestDetails.getProduct());
		long recordProcess = 0;
		boolean hasMore = false;
		do {
			Map<String, Object> datrecordsMap = null;
			datrecordsMap = nxDwInventoryDao.getNxInventoryBySearchCriteriaChunkandId(searchCriteria, chunkSize, lastId,
					nxRequestDetails.getProduct());
			lastId = (long) datrecordsMap.get("lastId");
			List<Map<String, Object>> datrecords = (List<Map<String, Object>>) datrecordsMap.get("data");
			for (Map<String, Object> row : datrecords) {
				for (NxDwToJsonRules jsonBuildRule : args.jsonRuleList) {
					if (!jsonBuildRule.getFieldType().equals("list")
							&& !jsonBuildRule.getFieldType().equals("object")) {
						ObjectNode parentNode = getParentNode(jsonBuildRule, row, args);
						putValueToNode(jsonBuildRule.getFieldType(),
								convertData(jsonBuildRule, row.get(jsonBuildRule.getDwKey()), row, args), parentNode,
								jsonBuildRule.getFieldName(), jsonBuildRule, args);
					}
				}
			}

			recordProcess = recordProcess + datrecords.size();
			if (recordProcess < totalRecords) {
				hasMore = true;
			} else {
				hasMore = false;
			}
		} while (hasMore);
		return args.nodeMap.get("root").toString();

	}
	*/
	
	protected String createJsonNodeString1(NxRequestDetails nxRequestDetails)
			throws JsonMappingException, JsonProcessingException, ParseException {
		long loadingRulesAndQueryDwInventoryStartTime = System.currentTimeMillis();
		Args args = initializeArgs(nxRequestDetails.getProduct());
		JsonNode node = mapper.readTree("{}");
		args.nodeMap.put("root", node);
		String searchCriteria = nxRequestDetails.getManageBillingPriceJson();
		Queue<Map<String, Object>> rows = null;
		if (MyPriceConstants.DW_INVENTORY_PRODUCTS.contains(nxRequestDetails.getProduct())) {
			rows = nxDwInventoryDao.getNxInventoryBySearchCriteriaWithSize(searchCriteria, chunkSize, args.product);
			if(nxRequestDetails.getProduct()!=null && ("AVPN".equalsIgnoreCase(nxRequestDetails.getProduct() ) || ("MIS/PNT".equalsIgnoreCase(nxRequestDetails.getProduct()))))
			{
				rows=getNxInventoryBySearchCriteriaWithSizewithCktId(rows,searchCriteria, chunkSize, args.product,nxRequestDetails.getNxReqId());
			}
		}else if (MyPriceConstants.DW_UB_CALL_PRODUCTS.contains(nxRequestDetails.getProduct())) {
			rows = nxDwUbCallInventoryDataDao.getNxInventoryBySearchCriteriaWithSize(searchCriteria, chunkSize, args.product);
		} else if (MyPriceConstants.INR_BETA_ONENET_PRODUCTS.contains(nxRequestDetails.getProduct())) {
			rows = nxOneNetInventoryDataDao.getNxInventoryBySearchCriteriaWithSize(searchCriteria, chunkSize, args.product);
		}else if(MyPriceConstants.DW_VTNS_LD.contains(nxRequestDetails.getProduct())) {						
			rows = nxVtnsLDCallInventoryDataDao.getNxInventoryBySearchCriteriaWithSize(searchCriteria, chunkSize, args.product);
		}

		LogUtils.logExecutionDurationMs(logger,
				String.format("Total time to load all rules and query nx_dw_inventory for nx request id %d", nxRequestDetails.getNxReqId()),
				loadingRulesAndQueryDwInventoryStartTime);
		long priceJsonGenerationStartTime = System.currentTimeMillis();
		
		while (!rows.isEmpty()) {
			Map<String, Object> row = rows.remove();
			if(MyPriceConstants.EPLSWAN.equals(args.product) && !isEplswanCkt(row)){
				continue;
			}

			for (NxDwToJsonRules jsonBuildRule : args.jsonRuleList) {
				if (!jsonBuildRule.getFieldType().equals("list")
						&& !jsonBuildRule.getFieldType().equals("object")) {
					ObjectNode parentNode = getParentNode(jsonBuildRule, row, args);
					putValueToNode(jsonBuildRule.getFieldType(),
							convertData(jsonBuildRule, row.get(jsonBuildRule.getDwKey()), row, args, nxRequestDetails), parentNode,
							jsonBuildRule.getFieldName(), jsonBuildRule, args);
				}
			}
		}
		LogUtils.logExecutionDurationMs(logger,
				String.format("generate price json for nx request id %d", nxRequestDetails.getNxReqId()),
				priceJsonGenerationStartTime);
		return args.nodeMap.get("root").toString();
	}

	private Queue<Map<String, Object>> getNxInventoryBySearchCriteriaWithSizewithCktId(Queue<Map<String, Object>> rows,
			String searchCriteria, int chunkSize2, String product, Long nxReqId) {
		long loadingMismatchCircuitRulesAndQueryDwInventoryStartTime = System.currentTimeMillis();
		Queue<Map<String, Object>> finalRows = new LinkedList<Map<String, Object>>(rows);//pass the user search criteria driven price data to a list
		NxDesignAudit nxDesignAudit = nxDesignAuditRepository.findByNxRefIdAndTransaction(nxReqId, MyPriceConstants.USRP_CIRCUITID_LIST);
		List<String> usrpCktList = new ArrayList<>();
		List<String> firstPriceJsonCktList = new ArrayList<>();
		String usrpCktData = "";
		if (nxDesignAudit != null) {
			usrpCktData = nxDesignAudit.getData();
		}	
		while (!rows.isEmpty()) {//pick all the ckt numbers from first pricejson data
			Map<String, Object> inrMap = new HashMap<>();;
			inrMap = rows.poll();
			if(null != inrMap.get("CIRCUIT_NUMBER"))
			firstPriceJsonCktList.add(inrMap.get("CIRCUIT_NUMBER").toString());
        }
	List<String> mismatchCktIds = new ArrayList<>();
		if(usrpCktData != null) {
			usrpCktData = usrpCktData.startsWith(",")?usrpCktData.substring(1):usrpCktData;
			usrpCktList = Arrays.asList(usrpCktData.split(","));			
			mismatchCktIds = usrpCktList.stream()
                .filter(usrpCktId->!firstPriceJsonCktList.contains(usrpCktId))
                .collect(Collectors.toList());  //identify the mismatch ckts ids b/z usrp and pricejson
		}
		if(mismatchCktIds == null || mismatchCktIds.isEmpty() || mismatchCktIds.size() == 0)
		{
			logger.info("there are no mismatch ckts so returning with first json data");
			return finalRows;
		}
		logger.info("size of pricejson, usrp and mismatch are"+firstPriceJsonCktList.size()+","+usrpCktList.size()+","+mismatchCktIds.size());
		JsonNode searchCriteriaNode = JacksonUtil.toJsonNode(searchCriteria);		
		List<List<String>> cktListForChunkSize = ListUtils.partition(mismatchCktIds, chunkSize);
		for(List<String> misMatchCktslist : cktListForChunkSize)
		{
		String mismatchCircuits =(String) misMatchCktslist.stream().map(Object::toString).collect(Collectors.joining(","));			
		String billMonth = searchCriteriaNode.get("billMonth").asText();
		String cktIdSearchCriteria = "{\"searchCriteria\":\"CIRCUITID\",\"circuitid\":\":cktIds\",\"billMonth\":\":billmonth\"}";// new search criteria for ckt ID search for only mismatch ckts
		cktIdSearchCriteria = cktIdSearchCriteria.replace(":cktIds", mismatchCircuits);
		cktIdSearchCriteria = cktIdSearchCriteria.replace(":billmonth", billMonth);
		Queue<Map<String, Object>> rowsforcktIdcriteria=nxDwInventoryDao.getNxInventoryBySearchCriteriaWithSize(cktIdSearchCriteria, chunkSize, product);
		//accessmcn redundant ckt list identification
		try {
			Queue<Map<String, Object>> AccessMcnRows = new LinkedList<Map<String, Object>>(rowsforcktIdcriteria);
			Queue<Map<String, Object>> AccessMcnRowsFinal = new LinkedList<Map<String, Object>>();
			while (!rowsforcktIdcriteria.isEmpty()) {
				Map<String, Object> accessMcnRow = rowsforcktIdcriteria.remove();
				accessMcnRow.put("IS_SKIP_ACCESS_MCN", "Y");
				AccessMcnRowsFinal.add(accessMcnRow);				
			}
			finalRows.addAll(AccessMcnRowsFinal); 			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
		}
		LogUtils.logExecutionDurationMs(logger,
				String.format("Time to load  rules and query nx_dw_inventory when mismatch circuit is there for nx request id %d", nxReqId),
				loadingMismatchCircuitRulesAndQueryDwInventoryStartTime);
		return finalRows;
	}

	protected ObjectNode getParentNode(NxDwToJsonRules jsonBuildRule, Map<String, Object> dwRow, Args args) throws ParseException {
		if (args.jsonBuildRuleMap.get(jsonBuildRule.getFieldParent()) == null) {
			return (ObjectNode) args.nodeMap.get(jsonBuildRule.getFieldParent());
		}
		NxDwToJsonRules parentRule = args.jsonBuildRuleMap.get(jsonBuildRule.getFieldParent());
		String identifierKey = getIdentifierKey(parentRule, args, dwRow);
		if (args.nodeMap.containsKey(identifierKey)) {
			return (ObjectNode) args.nodeMap.get(identifierKey);
		}
		ObjectNode grandParentNode = getParentNode(parentRule, dwRow, args);
		ObjectNode parentNode = createParentNode(parentRule, dwRow, args);
		if (parentRule.getFieldType().equals("list")) {
			grandParentNode.withArray(parentRule.getFieldName()).add(parentNode);
		} else {
			grandParentNode.set(parentRule.getFieldName(), parentNode);
		}
		args.nodeMap.put(identifierKey, parentNode);
		return parentNode;
	}

	protected ObjectNode createParentNode(NxDwToJsonRules jsonBuildRule, Map<String, Object> dwRow, Args args) throws ParseException {
		ObjectNode res = mapper.createObjectNode();

		List<String> keys = new ArrayList<>();
		List<String> dwKeys = new ArrayList<>();
		List<String> types = new ArrayList<>();

		if (jsonBuildRule.getIdentifierKey().contains("|")) {
			if (jsonBuildRule.getFieldName().equalsIgnoreCase("design")) {
				if (dwRow.get("TYPE_OF_CHARGE").equals("A")) {
					keys.add("circuitId");
					dwKeys.add("CIRCUIT_NUMBER");
					types.add("String");
				}
				if (dwRow.get("TYPE_OF_CHARGE").equals("P")) {
					keys.add("portNumber");
					dwKeys.add("PORT_NUMBER");
					types.add("String");
				}
			}
		} else {
			keys = Arrays.asList(jsonBuildRule.getIdentifierKey().split(","));
			types = Arrays.asList(jsonBuildRule.getIdentifierType().split(","));
			dwKeys = Arrays.asList(jsonBuildRule.getDwKey().split(","));

		}
		 if(jsonBuildRule.getFieldName().equalsIgnoreCase("accountDetails")
					&& null != dwRow && dwRow.containsKey("IS_SKIP_ACCESS_MCN") 
					) 
				{
				res.put("isSkipAccessMCN", "Y");
					}

		for (int i = 0; i < keys.size(); i++) {
			
			putValueToNode(types.get(i), convertData(jsonBuildRule, dwRow.get(dwKeys.get(i)), dwRow, args, null), res,
					keys.get(i), jsonBuildRule, args);
		}
		return res;
	}

	protected void putValueToNode(String type, Object value, ObjectNode parentNode, String tagName,
			NxDwToJsonRules jsonBuildRule, Args args) {
		if ("nexxusDummyId".equals(tagName)) {
			return;
		}
		if (value == null) {
			return;
		}
		if (type.equals("String")) {
			parentNode.put(tagName, String.valueOf(value).trim());
		} else if (type.equals("int")) {
			parentNode.put(tagName, Integer.parseInt(String.valueOf(value).trim()));
		} else if (type.equals("long")) {
			parentNode.put(tagName, Long.parseLong(String.valueOf(value).trim()));
		} else if (type.equals("double")) {
			parentNode.put(tagName, Double.parseDouble(String.valueOf(value).trim()));
		}
		
		
		 if("pbi".equals(jsonBuildRule.getFieldName())) {
			if(value!=null) {
				String pbiValue=removeLeadingZeros(value.toString()).trim();
				StringBuilder datasetName= new StringBuilder();
				datasetName.append(jsonBuildRule.getOffer());
				datasetName.append("_");
				datasetName.append("PBI_NRC");
				List<String>  res= nxLookupDataRepository.fetchDescriptionByDataSetName(datasetName.toString());
				if( CollectionUtils.isNotEmpty(res) && res.get(0)!=null) {
					Set<String> nrcPbiSet=Arrays.stream(res.get(0).split(",")).collect(Collectors.toSet());
					if(nrcPbiSet.contains(pbiValue)) {
						parentNode.put(InrConstants.NEXXUS_FALLOUT, "Y");
						parentNode.put(InrConstants.NEXXUS_FALLOUT_REASON, InrConstants.NRC_CHARGES_ARE_NOT_NEEDED);
					 }
				}
			}
		 }
	
		 if("TransportServiceDesc".equals(jsonBuildRule.getFieldName())) {
				if(value!=null) {
					String transportServiceCode=null;
					if("AVPN".equals(value)) {
						transportServiceCode="4";
					}else if ("ADI".equals(value)) {
						transportServiceCode="0";
					}else if ("PNT".equals(value)) {
						transportServiceCode="2";
					}
					if(transportServiceCode!=null){
						parentNode.put("TransportServiceCode", transportServiceCode);
					}

				}
		 }
		 
	
	}

	protected Object convertData(NxDwToJsonRules jsonBuildRule, Object data, Map<String, Object> dwRow,
			Args args, NxRequestDetails nxRequestDetails) throws ParseException {
		if ("CUSTOM".equals(jsonBuildRule.getType())) {
			if ("priceType".equals(jsonBuildRule.getFieldName())) {
				if ("P".equals(String.valueOf(dwRow.get("TYPE_OF_CHARGE")))) {
					return "PORTBEID";
				} else if ("A".equals(String.valueOf(dwRow.get("TYPE_OF_CHARGE")))) {
					return "ACCESSBEID";
				}
				return null;
			}
			
			if (MyPriceConstants.AVTS.equalsIgnoreCase(args.product)) {
				if ("discount".equals(jsonBuildRule.getFieldName())) {
					Object localListPrice = dwRow.get("LOCALLISTPRICE");
					Object actualPrice = dwRow.get("ACTUALPRICE");
					if (localListPrice != null && actualPrice != null) {
						return Double.parseDouble(String.valueOf(localListPrice)) - Double.parseDouble(String.valueOf(actualPrice));
					}
					return null;
				}
			}
			if (MyPriceConstants.ANIRA.equalsIgnoreCase(args.product)) {
				if ("discount".equals(jsonBuildRule.getFieldName())) {
					Object localListPrice = dwRow.get("LOCALLISTPRICE");
					Object actualPrice = dwRow.get("ACTUALPRICE");
					if (localListPrice != null && actualPrice != null) {
						return Double.parseDouble(String.valueOf(localListPrice)) - Double.parseDouble(String.valueOf(actualPrice));
					}
					return null;
				}
			}
			
			if (MyPriceConstants.BVoIP_NON_USAGE.equalsIgnoreCase(args.product)) {
				if ("discount".equals(jsonBuildRule.getFieldName())) {
					Object discountAmount = dwRow.get("DISCOUNT_AMOUNT");
					Object preDiscountAmount = dwRow.get("PRE_DISCOUNT_AMT");
					if (discountAmount != null && preDiscountAmount != null) {
						double discount = 0;
						double roundedDiscount=0;
						try {
							double da = Double.parseDouble(String.valueOf(discountAmount));
							double pda = Double.parseDouble(String.valueOf(preDiscountAmount));
							if (pda != 0) {
								discount= da / pda * 100;
								roundedDiscount =(double)Math.round(discount*100)/100;
							}
						} catch (NumberFormatException e) {
							logger.info("Exception", e);
						}

						return roundedDiscount;
					}
					return null;
				}
						
			if ("currentMRC".equals(jsonBuildRule.getFieldName())) {
				Object postDiscountAmount = dwRow.get("POST_DISCOUNT_AMT");
				Object bitCt = dwRow.get("BIT_CT");
				if (postDiscountAmount != null && bitCt != null) {
					double curretMrc = 0;
					try {
						double pda = Double.parseDouble(String.valueOf(postDiscountAmount));
						double bct = Double.parseDouble(String.valueOf(bitCt));
						if (bct != 0) {
							 curretMrc = pda / bct;
						}
					} catch (NumberFormatException e) {
						logger.info("Exception", e);
					}
					return curretMrc;
				}
				return null;
			}
			
			if ("concurrentCallType".equals(jsonBuildRule.getFieldName())) {
				String pbi = String.valueOf(dwRow.get("PBI"));
				String genericQuantity = String.valueOf(dwRow.get("BIT_CT"));
				String concurrentCallType = null;
				if (MODULE_CARD_PBIS.contains(pbi) && genericQuantity != null) {
					Double genericQty = Double.parseDouble(genericQuantity);
					if (genericQty <= 12) {
						concurrentCallType = "VoMIS12";
					} else if (genericQty <= 24) {
						concurrentCallType = "VoMIS24";
					} else if (genericQty <= 48) {
						concurrentCallType = "VoMIS48";
					} else {
						concurrentCallType = "T3";
					}
				}
				return concurrentCallType;
			}
			
			if ("TransportServiceDesc".equals(jsonBuildRule.getFieldName())) {
				String portNumber=String.valueOf(dwRow.get("PORT_NUMBER"));
				String circuitId = String.valueOf(dwRow.get("WORK_WITH_CKT_NB"));
				String productType=null;
				if(stringHasValue(portNumber) && stringHasValue(circuitId)) {
					String avpncircuitId=getCircuitIdwitdots(circuitId,"AVPN");
					String avpnclsSerial=getClsSerial(avpncircuitId);
					String adigcircuitId=getCircuitIdwitdots(circuitId,"ADIG");
					String adigclsSerial=getClsSerial(adigcircuitId);
					productType=usrpDao.getProductTypeBasedCircuitAndPortNumber(avpncircuitId, avpnclsSerial,adigcircuitId,adigclsSerial,portNumber);
				}else if (stringHasValue(circuitId)){
					String avpncircuitId=getCircuitIdwitdots(circuitId,"AVPN");
					String avpnclsSerial=getClsSerial(avpncircuitId);
					String adigcircuitId=getCircuitIdwitdots(circuitId,"ADIG");
					String adigclsSerial=getClsSerial(adigcircuitId);
					productType=usrpDao.getProductTypeBasedCircuit(avpncircuitId, avpnclsSerial,adigcircuitId,adigclsSerial);
				}else if (stringHasValue(portNumber)){
					productType=usrpDao.getProductTypeBasedPortNumber(portNumber);
				}
				if(productType==null) {
					String pbiCodeval = String.valueOf(dwRow.get("PBI"));
					if(stringHasValue(pbiCodeval)) {
						NxLookupData nxLookupData = nxMyPriceRepositoryServce
								.getLookupDataByItemId("INR_BETA_BVOIP_TRANS_DESC").get(pbiCodeval);
						if (nxLookupData != null) {
							return nxLookupData.getDescription();
						}
					}
				}
				return productType;
			}

		 }
		 //VTNS Usage Custom Logic
			if(MyPriceConstants.VTNS_LD.equalsIgnoreCase(args.product)) {
				
				if ("minutesCount".equals(jsonBuildRule.getFieldName())) {
					String totalMinutesCount = String.valueOf(dwRow.get("MINUTES_COUNT"));
					if (stringHasValue(totalMinutesCount)) {
						double totalMinutesAnnual = 0;
						try {
							double tqm = Double.parseDouble(String.valueOf(totalMinutesCount));
							totalMinutesAnnual = tqm * monthlyFactor(nxRequestDetails);

						} catch (NumberFormatException e) {
							logger.info("Exception", e);
						}

						return totalMinutesAnnual;
					}
					return null;
				}
				
				if ("callsCount".equals(jsonBuildRule.getFieldName())) {
					String totalCallCount = String.valueOf(dwRow.get("CALL_COUNT"));
					if (stringHasValue(totalCallCount)) {
						double totalCallsAnnual = 0;
						try {
							double tqm = Double.parseDouble(String.valueOf(totalCallCount));
							totalCallsAnnual = tqm * monthlyFactor(nxRequestDetails);

						} catch (NumberFormatException e) {
							logger.info("Exception", e);
						}

						return totalCallsAnnual;
					}
					return null;
				}
				
			}
			
			// BVoIP Usage Custom logic
			if (MyPriceConstants.BVoIP.equalsIgnoreCase(args.product)) {
				
				if ("jurisdictionMyVal".equals(jsonBuildRule.getFieldName())) {
					String jurisdictionDwVal = String
                            .valueOf(convertData(args.jsonBuildRuleMap.get("jurisdictionDwVal"), dwRow.get("JUR_CODE"), dwRow, args, nxRequestDetails));
					String jurisdictionMyVal = null;
					/*if (jurisdictionDwVal.equalsIgnoreCase("Intrastate InterLATA")
							|| jurisdictionDwVal.equalsIgnoreCase("Intrastate IntraLATA")) {
					jurisdictionMyVal = "Intrastate";
						
						return jurisdictionMyVal;
					} else if (jurisdictionDwVal.equalsIgnoreCase("Interstate InterLATA")
							|| jurisdictionDwVal.equalsIgnoreCase("Interstate IntraLATA")) {
						jurisdictionMyVal = "Interstate";
						return jurisdictionMyVal;
					} else {
						jurisdictionMyVal = "N/A";
						return jurisdictionMyVal;
					}*/
					NxLookupData nxLookupData = nxMyPriceRepositoryServce
							.getLookupDataByItemId("inrbeta_bvoip_myJurisdictionVal").get(jurisdictionDwVal);
					if (nxLookupData != null) {
						return nxLookupData.getDescription();
					} else {
						jurisdictionMyVal = "N/A";
						return jurisdictionMyVal;
					}
				}
				String pbiCode = String.valueOf(dwRow.get("PBI"));
				if ("cdtRegion".equals(jsonBuildRule.getFieldName())) {
					String cdtRegion=null;
					String jurisdictionMyVal = String
							.valueOf(convertData(args.jsonBuildRuleMap.get("jurisdictionMyVal"), null, dwRow, args, nxRequestDetails));
					if (!"Intrastate".equals(jurisdictionMyVal)
							|| !"Interstate".equals(jurisdictionMyVal)) {
						double myBilledMinutes = 0;
						double discount = 0;
						try {
							String genericQuantity = String
									.valueOf(convertData(args.jsonBuildRuleMap.get("genericQuantity"), null, dwRow, args, nxRequestDetails));
							if(stringHasValue(genericQuantity)) {
								myBilledMinutes = Double.parseDouble(String.valueOf(genericQuantity));
							}
							String grossDiscount = String.valueOf(dwRow.get("TOTAL_DISCOUNT_AMT"));
							if(stringHasValue(grossDiscount)) {
								discount=Double.parseDouble(String.valueOf(grossDiscount));
							}
						} catch (NumberFormatException e) {
							logger.info("Exception", e);
						}
						if (myBilledMinutes > 0 && discount <= 0 && !"00070467".equals(pbiCode)) {
							cdtRegion= "US";
						}
						
					}
					String originatingStateCountryName = String.valueOf(dwRow.get("ORIG_ST_CTRY_NAME"));
					if (("00079044".equals(pbiCode) || "00092978".equals(pbiCode) || "00093908".equals(pbiCode))
							&& stringHasValue(originatingStateCountryName)) {
						NxLookupData nxLookupData = nxMyPriceRepositoryServce
								.getLookupDataByItemId("INR_BVOIP_CDT_REGION").get(originatingStateCountryName);
						if (nxLookupData != null) {
							cdtRegion= nxLookupData.getDescription();
						}
					}
					return cdtRegion;
				}
				if ("totalQuantityAnnual".equals(jsonBuildRule.getFieldName())
						|| ("genericQuantity".equals(jsonBuildRule.getFieldName()))) {
					String totalQtyMontly = String.valueOf(dwRow.get("TOTAL_MONTHLY_SEC"));
					if (stringHasValue(totalQtyMontly)) {
						double totalQtyAnnual = 0;
						BigDecimal bd = null;
						try {
							double tqm = Double.parseDouble(String.valueOf(totalQtyMontly));
							totalQtyAnnual = tqm * monthlyFactor(nxRequestDetails);
							bd = roundOff(totalQtyAnnual, 4);
						} catch (NumberFormatException e) {
							logger.info("Exception", e);
						}

						return bd;
					}
					return null;
				}
				if ("discount".equals(jsonBuildRule.getFieldName())) {
					String grossDiscount = String.valueOf(dwRow.get("TOTAL_DISCOUNT_AMT"));
					String grossCharge = String.valueOf(dwRow.get("TOTAL_PRE_DISCOUNT_AMT"));
					double discount = 0;
					BigDecimal bd = null;
					try {
						double gd = Double.parseDouble(grossDiscount);
						double gc = Double.parseDouble(grossCharge);
						if (gc != 0) {
							discount = gd / gc * 100;
							bd = roundOff(discount, 4);
						}
					} catch (NumberFormatException e) {
						logger.info("Exception", e);
					}
					return bd;
				}
				if ("billedMinutesQty".equals(jsonBuildRule.getFieldName())) {
					String totalQtyAnnual = String
							.valueOf(convertData(args.jsonBuildRuleMap.get("totalQuantityAnnual"), null, dwRow, args, nxRequestDetails));
					String callDirection = String
							.valueOf(convertData(args.jsonBuildRuleMap.get("callDirection"), dwRow.get("DIR_IND"), dwRow, args, nxRequestDetails));
					String billingElementCode = String.valueOf(dwRow.get("BILL_ELE_CODE")); 
					String pBIDescription = String.valueOf(dwRow.get("PBI_DSC"));
					if (stringHasValue(totalQtyAnnual)) {
						if (("Outbound".equalsIgnoreCase(callDirection) && "USAGE".equalsIgnoreCase(billingElementCode))
								&& ((isFreeMinsPbiDesc(pBIDescription))
										|| (BILLED_MINS_PBI_DESC.equalsIgnoreCase(pBIDescription)
												&& BILLED_MINS_PBI.contains(pbiCode)))) {
							return totalQtyAnnual;
						}
					}
				}
				if ("freeMinutesQty".equals(jsonBuildRule.getFieldName())) {
					String totalQtyAnnual = String
							.valueOf(convertData(args.jsonBuildRuleMap.get("totalQuantityAnnual"), null, dwRow, args, nxRequestDetails));
					String callDirection = String
							.valueOf(convertData(args.jsonBuildRuleMap.get("callDirection"), dwRow.get("DIR_IND"), dwRow, args, nxRequestDetails));
					String billingElementCode = String.valueOf(dwRow.get("BILL_ELE_CODE")); 
					String pBIDescription = String.valueOf(dwRow.get("PBI_DSC"));
					if (!totalQtyAnnual.isEmpty()) {
						if (!(("Outbound".equalsIgnoreCase(callDirection) && "USAGE".equalsIgnoreCase(billingElementCode))
								&& ((isFreeMinsPbiDesc(pBIDescription))
										|| (BILLED_MINS_PBI_DESC.equalsIgnoreCase(pBIDescription)
												&& BILLED_MINS_PBI.contains(pbiCode))))) {
							return totalQtyAnnual;
						} 
					}
				}
				if ("currentMRC".equals(jsonBuildRule.getFieldName())) {
					String unitRate = String.valueOf(dwRow.get("UNIT_RATE_AMT"));
					if (stringHasValue(unitRate)) {
						return  unitRate;    
					}
				}
				if ("inrQty".equals(jsonBuildRule.getFieldName())) {
					String genericQuantity = String.valueOf(
							convertData(args.jsonBuildRuleMap.get("genericQuantity"), null, dwRow, args, nxRequestDetails));
					if (stringHasValue(genericQuantity)) {
						return genericQuantity;
					}
				}
				if ("GrossChargeAnnual".equals(jsonBuildRule.getFieldName())) {
					String grossCharge = String.valueOf(dwRow.get("TOTAL_PRE_DISCOUNT_AMT"));
					if (stringHasValue(grossCharge)) {
						double grossChargeAnnual = 0;
						BigDecimal bd = null;
						try {
							double gc = Double.parseDouble(String.valueOf(grossCharge));
							grossChargeAnnual = gc * monthlyFactor(nxRequestDetails);
							bd = roundOff(grossChargeAnnual, 4);

						} catch (NumberFormatException e) {
							logger.info("Exception", e);
						}

						return bd;
					}
					return null;
				}
				if ("TotalQuantityAnnualMessages".equals(jsonBuildRule.getFieldName())) {
					String totalUsgMsgCnt = String.valueOf(dwRow.get("TOTAL_USG_MSG_CNT"));
					if (stringHasValue(totalUsgMsgCnt)) {
						double totalQuantityAnnualMessages = 0;
						BigDecimal bd = null;
						try {
							double tumc = Double.parseDouble(String.valueOf(totalUsgMsgCnt));
							totalQuantityAnnualMessages = tumc * monthlyFactor(nxRequestDetails);
							bd = roundOff(totalQuantityAnnualMessages, 4);
						} catch (NumberFormatException e) {
							logger.info("Exception", e);
						}

						return bd;
					}
					return null;
				}
				if ("TransportServiceDesc".equals(jsonBuildRule.getFieldName())) {
					String portNumber=String.valueOf(dwRow.get("PORT_NUM"));
					String productType=null;
					if (stringHasValue(portNumber)){
						productType=usrpDao.getProductTypeBasedPortNumber(portNumber);
					}
					if(productType==null) {
						String pbiCodeval = String.valueOf(dwRow.get("PBI"));
						if(stringHasValue(pbiCodeval)) {
							NxLookupData nxLookupData = nxMyPriceRepositoryServce
									.getLookupDataByItemId("INR_BETA_BVOIP_TRANS_DESC").get(pbiCodeval);
							if (nxLookupData != null) {
								return nxLookupData.getDescription();
							}
						}
					}
					return productType;
				}
				if("pBIDescription".equals(jsonBuildRule.getFieldName())) {
					String pbiDesc = null;
					String pd = String.valueOf(dwRow.get("PBI_DSC"));
					if(pd.contains("'")) {
						pbiDesc = pd.replace("'", " ");
						return pbiDesc;
					}
					else {
						return pd;
					}
					
				}
			}
//ABN custom logic
			if (MyPriceConstants.ABN_LD_VOICE.equalsIgnoreCase(args.product)) {
				
				if ("originatingStateCountryNameVal".equals(jsonBuildRule.getFieldName()) || 
						"terminatingStateCountryNameVal".equals(jsonBuildRule.getFieldName())) {
					String itemId = null;
					if("originatingStateCountryNameVal".equals(jsonBuildRule.getFieldName())) {
						itemId = String.valueOf(dwRow.get("ORIG_ST_CTRY_NAME"));
					}
					else if("terminatingStateCountryNameVal".equals(jsonBuildRule.getFieldName())) {
						itemId = String.valueOf(dwRow.get("TERM_ST_CTRY_NAME"));
					}
					String jurisdictionDwVal = String
                            .valueOf(convertData(args.jsonBuildRuleMap.get("jurisdiction"), dwRow.get("JUR_CODE"), dwRow, args, nxRequestDetails));	
					NxLookupData nxLookupData = null;
					if("International".equals(jurisdictionDwVal)) {
					 nxLookupData = nxMyPriceRepositoryServce.getLookupDataByItemId("ABN_INTL_COUNTRY").get(itemId);
					}
					else {
						 nxLookupData = nxMyPriceRepositoryServce.getLookupDataByItemId("ABN_US_COUNTRY").get(itemId);
					}
					if (nxLookupData != null) {
						return nxLookupData.getDescription(); 
					}
					else 
						return null;
				}
				if("pbiDescription".equals(jsonBuildRule.getFieldName())) {
					String pbiDesc = null;
					String pd = String.valueOf(dwRow.get("PBI_DSC"));
					if(pd.contains("'")) {
						pbiDesc = pd.replace("'", " ");
						return pbiDesc;
					}
					else {
						return pd;
					}
					
				}
				
				if ("totalQuantityAnnual".equals(jsonBuildRule.getFieldName())
						|| ("genericQuantity".equals(jsonBuildRule.getFieldName()))) {
					String totalQtyMontly = String.valueOf(dwRow.get("TOTAL_MONTHLY_SEC"));
					if (stringHasValue(totalQtyMontly)) {
						double totalQtyAnnual = 0;
						BigDecimal bd = null;
						try {
							double tqm = Double.parseDouble(String.valueOf(totalQtyMontly));
							totalQtyAnnual = tqm * monthlyFactor(nxRequestDetails);
							bd = roundOff(totalQtyAnnual, 4);

						} catch (NumberFormatException e) {
							logger.info("Exception", e);
						}

						return bd;
					}
					return null;
				}
				if ("grossChargeAnnual".equals(jsonBuildRule.getFieldName())) {
					String grossCharge = String.valueOf(dwRow.get("TOTAL_PRE_DISCOUNT_AMT"));
					if (stringHasValue(grossCharge)) {
						double grossChargeAnnual = 0;
						BigDecimal bd = null;
						try {
							double gc = Double.parseDouble(String.valueOf(grossCharge));
							grossChargeAnnual = gc * monthlyFactor(nxRequestDetails);
							bd = roundOff(grossChargeAnnual, 4);
						} catch (NumberFormatException e) {
							logger.info("Exception", e);
						}

						return bd;
					}
					return null;
				}
				if ("discountannual".equals(jsonBuildRule.getFieldName())) {
					String totalDiscountAmt = String.valueOf(dwRow.get("TOTAL_DISCOUNT_AMT"));
					if (stringHasValue(totalDiscountAmt)) {
						double postDiscountAmount = 0;
						BigDecimal bd = null;
						try {
							double tdm = Double.parseDouble(String.valueOf(totalDiscountAmt));
							postDiscountAmount = tdm * monthlyFactor(nxRequestDetails);
							bd = roundOff(postDiscountAmount, 4);
						} catch (NumberFormatException e) {
							logger.info("Exception", e);
						}

						return bd;
					}
					return null;
				}
				if ("totalQuantityAnnualMessages".equals(jsonBuildRule.getFieldName())) {
					String totalUsgMsgCnt = String.valueOf(dwRow.get("TOTAL_USG_MSG_CNT"));
					if (stringHasValue(totalUsgMsgCnt)) {
						double totalQuantityAnnualMessages = 0;
						BigDecimal bd = null;
						try {
							double tumc = Double.parseDouble(String.valueOf(totalUsgMsgCnt));
							totalQuantityAnnualMessages = tumc * monthlyFactor(nxRequestDetails);
							bd = roundOff(totalQuantityAnnualMessages, 4);

						} catch (NumberFormatException e) {
							logger.info("Exception", e);
						}

						return bd;
					}
					return null;
				}
				
			}
			return null;
		}
		if (jsonBuildRule.getDefaultValue() != null) {
			return jsonBuildRule.getDefaultValue();
		}
		if (!stringHasValue(String.valueOf(data))) {
			return null;
		}
		if (jsonBuildRule.getLookupDatasetName() == null) {
			return data;
		}
		if (jsonBuildRule.getLookupDatasetName() != null) {
			NxLookupData nxLookupData = nxMyPriceRepositoryServce
					.getLookupDataByItemIdIgnoreKeyCase(jsonBuildRule.getLookupDatasetName()).get(String.valueOf(data));
			if (nxLookupData != null) {
				return nxLookupData.getDescription();
			} 
		}
		return data;
	}
	
	protected BigDecimal roundOff(double value, int places) {
		try {
			if(java.util.Optional.ofNullable(value).isPresent()) {
				BigDecimal bd = new BigDecimal(value);
				bd = bd.setScale(places, BigDecimal.ROUND_HALF_UP);
				return bd;
			}
		} catch (Exception e) {
			logger.error("Error : While rounding off value : ",e);
		}
		return null;
	}

	protected String getIdentifierKey(NxDwToJsonRules jsonBuildRule, Args args, Map<String, Object> dwRow) {
		StringBuilder sb = new StringBuilder();
		getIdentifierKeyHelper(sb, jsonBuildRule, args, dwRow);
		return sb.toString();
	}

	protected void getIdentifierKeyHelper(StringBuilder sb, NxDwToJsonRules jsonBuildRule, Args args,
			Map<String, Object> dwRow) {
		if (jsonBuildRule == null) {
			return;
		}
		getIdentifierKeyHelper(sb, args.jsonBuildRuleMap.get(jsonBuildRule.getFieldParent()), args, dwRow);
		List<String> identifierKeys = new ArrayList<>();
		List<String> dwKeys = new ArrayList<>();

		if (jsonBuildRule.getIdentifierKey().contains("|")) {
			if (jsonBuildRule.getFieldName().equalsIgnoreCase("design")) {
				if (dwRow.get("TYPE_OF_CHARGE").equals("A")) {
					identifierKeys.add("circuitId");
					dwKeys.add("CIRCUIT_NUMBER");
				}
				if (dwRow.get("TYPE_OF_CHARGE").equals("P")) {
					identifierKeys.add("portNumber");
					dwKeys.add("PORT_NUMBER");

				}
			}
		} else {
			identifierKeys = Arrays.asList(jsonBuildRule.getIdentifierKey().split(","));
			if (jsonBuildRule.getDwKey() != null) {
				dwKeys = Arrays.asList(jsonBuildRule.getDwKey().split(","));
			}
		}
		for (int i = 0; i < identifierKeys.size(); i++) {
			String value = String.valueOf(dwRow.get(dwKeys.get(i)));
			if (sb.length() > 0) {
				sb.append("_");
			}
			sb.append(value);

		}

	}

	protected static class Args {
		Map<String, NxDwToJsonRules> jsonBuildRuleMap;
		String product;
		List<NxDwToJsonRules> jsonRuleList;
		Map<String, JsonNode> nodeMap = new HashMap<>();

	}

	protected Args initializeArgs(String product) {
		if ("MIS/PNT".equals(product)) {
			product = "ADI";
		} else if ("GMIS".equals(product)) {
			product = "ADIG";
		}
		Args args = new Args();
		List<NxDwToJsonRules> rulesList = nxDwToJsonRulesRepository.findByOfferAndRuleNameAndActive(product, RULE_NAME,
				Y);
		Map<String, NxDwToJsonRules> jsonBuildRuleMap = rulesList.stream()
				.collect(Collectors.toMap(NxDwToJsonRules::getFieldName, Function.identity()));

		args.jsonBuildRuleMap = jsonBuildRuleMap;
		args.product = product;
		args.jsonRuleList = rulesList;
		return args;
	}

	protected String removeLeadingZeros(String in) {
		if (in == null) {
			return null;
		}
		return in.replaceAll("^0+(?!$)", "");
	}

	protected boolean stringHasValue(String in) {
		if (in == null) {
			return false;
		}
		if (in.isEmpty()) {
			return false;
		}
		if ("null".equals(in)) {
			return false;
		}
		return true;
	}
	/**
	 * Checks if is free mins pbi desc.
	 *
	 * @param pbiDesc the pbi desc
	 * @return true, if is free mins pbi desc
	 */
	protected boolean isFreeMinsPbiDesc(String pbiDesc) {
		for (String s : BILLED_MINS_PBI_DESCS) {
			if (s.equalsIgnoreCase(pbiDesc)) {
				return true;
			}
		}
		return false;
	}
	public int monthlyFactor(NxRequestDetails nxRequestDetails) throws ParseException {
		String billMonth = null;
		String beginBillMonth = null;
		String manageBillingPriceJson=nxRequestDetails.getManageBillingPriceJson().toString();
		JsonNode manageBillingPriceNode = JacksonUtil.toJsonNode(manageBillingPriceJson);
		if (manageBillingPriceNode.has("billMonth")
				&& !Strings.isNullOrEmpty(manageBillingPriceNode.get("billMonth").asText())) {
			billMonth=manageBillingPriceNode.get("billMonth").asText();
		}
		if (manageBillingPriceNode.has("beginBillMonth")
				&& !Strings.isNullOrEmpty(manageBillingPriceNode.get("beginBillMonth").asText())) {
			beginBillMonth=manageBillingPriceNode.get("beginBillMonth").asText();
		}
		Date date1 = new SimpleDateFormat("yyyyMM").parse(billMonth);
		Date date2 = new SimpleDateFormat("yyyyMM").parse(beginBillMonth);
		int difInMonths = date1.getMonth() - date2.getMonth();
		int diffInYears = date1.getYear() - date2.getYear();
		int monthsBetween = diffInYears * 12 + (difInMonths+1);
		double monthlyFactor =(double)12/monthsBetween;
		int monthlyFactorResult = (int) Math.round(monthlyFactor);
		return monthlyFactorResult;
	}

	protected int findNumberOfPlaceholder(String sql) {
		int res = 0;
		for (int i = 0; i < sql.length(); i++) {
			if (sql.charAt(i) == '?') {
				res++;
			}
		}
		return res;
	}
	
	protected String getClsSerial(String circuitId) {
		int start = circuitId.indexOf(".", 1);
		int end = circuitId.indexOf(".", start + 1);
		if (start > -1 && end > -1) {
			return circuitId.substring(start + 1, end);
		}
		return "";
	}
	
	protected String getCircuitIdwitdots(String circuitId,String product) {
			String res = circuitId.replaceAll("\\s", "").replaceAll("\\.", "");
			if (res.endsWith("ATI")) {
				if (res.length() == 13) {
					res = String.format(".%s.%s..%s.", res.substring(0, 4), res.substring(4, 10),
							res.substring(10, 13));
				} else if (res.length() == 16) {
					res = String.format(".%s.%s.%s.%s.", res.substring(0, 4), res.substring(4, 10),
							res.substring(10, 13), res.subSequence(13, 16));
				} else if (res.length() == 12) {
					res = String.format(".%s.%s..%s.", res.substring(0, 4), res.substring(4, 9), res.substring(9, 12));
				} 
				if (!MyPriceConstants.AVPN.equalsIgnoreCase(product)) {
					res = res.substring(1, res.length() - 1);
				}
			}
		
		return res;
	}
	
	protected boolean isEplswanCkt(Map<String, Object> row) {
		String cleancircuitNumber = String.valueOf(row.get("CIRCUIT_NUMBER"));
		String eplswanCktIdentification=cleancircuitNumber.substring(0, 4);
		Map<String, NxLookupData> eplswanSpeedckt = nxMyPriceRepositoryServce.
				getLookupDataByCriteria("USRP_EPLSWAN_DISPLAYSPEED");
		if (eplswanSpeedckt.keySet().contains(eplswanCktIdentification)) {
			return true;
		}
		return false;
		
	}

}
