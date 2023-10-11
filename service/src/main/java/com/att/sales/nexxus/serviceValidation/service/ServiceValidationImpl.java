package com.att.sales.nexxus.serviceValidation.service;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.framework.service.BaseServiceImpl;
import com.att.sales.nexxus.constant.InrConstants;
import com.att.sales.nexxus.dao.model.NxMpDeal;
import com.att.sales.nexxus.dao.model.NxMpSiteDictionary;
import com.att.sales.nexxus.dao.repository.NxMpDealRepository;
import com.att.sales.nexxus.dao.repository.NxMpSiteDictionaryRepository;
import com.att.sales.nexxus.myprice.transaction.service.InputValidator;
import com.att.sales.nexxus.serviceValidation.model.ServiceValidationRequest;
import com.att.sales.nexxus.serviceValidation.model.ServiceValidationResponse;
import com.att.sales.nexxus.serviceValidation.model.SiteDetails;
import com.att.sales.nexxus.util.JacksonUtil;
import com.att.sales.nexxus.util.ThreadMetaDataUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service("ServiceValidationImpl")
public class ServiceValidationImpl extends BaseServiceImpl implements ServiceValidation {

	private static Logger logger = LoggerFactory.getLogger(ServiceValidationImpl.class);
	
	@Autowired
	private AVSQUtil avsqUtil;

	@Autowired
	private NxMpDealRepository nxMpDealRepository;

	@Autowired
	private NxMpSiteDictionaryRepository nxMpSiteDictionaryRepository;
	
	@Autowired
	private ObjectMapper mapper;

	@Override
	public ServiceResponse validateService(ServiceValidationRequest request) throws SalesBusinessException {
		logger.info("Entering validateService() method");
		logger.info("request {}", JacksonUtil.toString(request));

		InputValidator inputValidator = new InputValidator();
		ServiceValidationResponse response = new ServiceValidationResponse();
		response = inputValidator.validateRequest(request);

		if (response == null) {
			response = new ServiceValidationResponse();
			Map<String, Object> paramMap = new HashMap<String, Object>();
			Map<String, Object> requestMetaDataMap = new HashMap<>();
			if (ServiceMetaData.getRequestMetaData() != null) {
				ServiceMetaData.getRequestMetaData().forEach((key, value) -> requestMetaDataMap.put(key, value));
				requestMetaDataMap.put(ServiceMetaData.XCORRELATIONID, requestMetaDataMap.get(ServiceMetaData.XCONVERSATIONID));
				requestMetaDataMap.remove(ServiceMetaData.XCONVERSATIONID);
				paramMap.put(InrConstants.REQUEST_META_DATA_KEY, requestMetaDataMap);
			}
			CompletableFuture.runAsync(() -> {
				try {
					ThreadMetaDataUtil.initThreadMetaData(requestMetaDataMap);
					NxMpDeal nxMpDeal = nxMpDealRepository.findByTransactionId(String.valueOf(request.getTransactionId()));
					
					if (null != nxMpDeal) {
						String modelName = request.getSiteDetails().stream().flatMap(site -> site.getConfigurationDetails().stream()).findFirst().get().getModelName();
						nxMpDeal.setOfferId(modelName);
						nxMpDeal.setModifiedDate(new Date());
						nxMpDealRepository.save(nxMpDeal);
					}
					updateRequestLocations(request, Optional.ofNullable(nxMpDeal).map(NxMpDeal::getNxTxnId).orElse(null));
					avsqUtil.callAVSQ(request, Optional.ofNullable(nxMpDeal).map(NxMpDeal::getNxTxnId).orElse(null), paramMap);
				} catch (Exception e) {
					logger.info("Exception", e);
					throw e;
				} finally {
					ThreadMetaDataUtil.destroyThreadMetaData();
				}
			});
			setSuccessResponse(response);
			logger.info("Exiting validateService() method");
		}

		return response;
	}
	
	protected void updateRequestLocations(ServiceValidationRequest request, Long nxTxnId) {
		if (nxTxnId == null) {
			return;
		}
		NxMpSiteDictionary siteDictionary = nxMpSiteDictionaryRepository.findByNxTxnId(nxTxnId);
		boolean siteJsonUpdated = false;
		boolean siteAddressUpdated = false;
		try {
			String siteJson = siteDictionary.getSiteJson();
			String siteAddress = siteDictionary.getSiteAddress();
			JsonNode siteJsonNode = mapper.createObjectNode();
			JsonNode siteAddressNode = mapper.createObjectNode();
			if (siteJson != null) {
				siteJsonNode = mapper.readTree(siteJson);
			}
			if (siteAddress != null) {
				siteAddressNode = mapper.readTree(siteAddress);
				JsonNode node = siteAddressNode.path("siteAddress");
				if (node.isTextual()) {
					String nodeString = StringEscapeUtils.unescapeJava(node.asText());
					JsonNode nodeArray = mapper.readTree(nodeString);
					((ObjectNode) siteAddressNode).set("siteAddress", nodeArray);
					siteAddressUpdated = true;
				}
			}
			Map<String, JsonNode> siteJsonNodeMap = new HashMap<>();
			for (JsonNode n : siteJsonNode.path("locations")) {
				if (!n.path("nxSiteId").isMissingNode() && !n.path("nxSiteId").isNull()) {
					siteJsonNodeMap.put(n.path("nxSiteId").asText(), n);
				}
			}
			Map<String, JsonNode> siteAddressNodeMap = new HashMap<>();
			for (JsonNode n : siteAddressNode.path("siteAddress")) {
				if (!n.path("nxSiteId").isMissingNode() && !n.path("nxSiteId").isNull()) {
					siteAddressNodeMap.put(n.path("nxSiteId").asText(), n);
				}
			}
			for (SiteDetails sd : request.getSiteDetails()) {
				if (!"ipne".equalsIgnoreCase(sd.getSiteInfoSource()) && null != sd.getSiteInfoSource()) {
					if (siteAddressNodeMap.containsKey(sd.getNxSiteId())) {
						updateSiteDetailsFromSiteAddress(sd, siteAddressNodeMap.get(sd.getNxSiteId()));
					} else {
						addSiteDetailsToSiteAddress(sd, siteAddressNode);
						siteAddressUpdated = true;
					}
				} else {
					if (siteJsonNodeMap.containsKey(sd.getNxSiteId())) {
						updateSiteDetailsFromSiteJson(sd, siteJsonNodeMap.get(sd.getNxSiteId()));
					} else {
						addSiteDetailsToSiteJson(sd, siteJsonNode);
						siteJsonUpdated = true;
					}
				}
			}
			if (siteJsonUpdated) {
				siteDictionary.setSiteJson(siteJsonNode.toString());
			}
			if (siteAddressUpdated) {
				siteDictionary.setSiteAddress(siteAddressNode.toString());
			}
			if (siteJsonUpdated || siteAddressUpdated) {
				nxMpSiteDictionaryRepository.save(siteDictionary);
			}
		} catch (IOException e) {
			logger.info("Exception in updateRequestLocations() method for the myPriceTransId: {}", request.getTransactionId());
			logger.error("Exception", e);
		}
	}
	
	protected void addSiteDetailsToSiteJson(SiteDetails siteDetails, JsonNode siteJsonNode) {
		ObjectNode newNode = mapper.createObjectNode();
		newNode.put("nxSiteId", siteDetails.getNxSiteId());
		newNode.put("street", siteDetails.getAddressLine());
		newNode.put("locName", siteDetails.getName());
		newNode.put("city", siteDetails.getCity());
		newNode.put("state", siteDetails.getState());
		newNode.put("zip", siteDetails.getPostalCode());
		newNode.put("country", siteDetails.getCountry());
		newNode.put("globalLocationId", siteDetails.getGlobalLocationId());
		((ObjectNode) siteJsonNode).withArray("locations").add(newNode);
	}

	protected void updateSiteDetailsFromSiteJson(SiteDetails siteDetails, JsonNode jsonNode) {
		siteDetails.setAddressLine(JacksonUtil.nodeAtPointerAsText(jsonNode, "/street"));
		siteDetails.setName(JacksonUtil.nodeAtPointerAsText(jsonNode, "/locName"));
		siteDetails.setCity(JacksonUtil.nodeAtPointerAsText(jsonNode, "/city"));
		siteDetails.setState(JacksonUtil.nodeAtPointerAsText(jsonNode, "/state"));
		siteDetails.setPostalCode(JacksonUtil.nodeAtPointerAsText(jsonNode, "/zip"));
		siteDetails.setCountry(JacksonUtil.nodeAtPointerAsText(jsonNode, "/country"));
		siteDetails.setGlobalLocationId(JacksonUtil.nodeAtPointerAsText(jsonNode, "/globalLocationId"));
	}

	protected void addSiteDetailsToSiteAddress(SiteDetails siteDetails, JsonNode siteAddressNode) {
		ObjectNode newNode = mapper.createObjectNode();
		newNode.put("nxSiteId", siteDetails.getNxSiteId());
		newNode.put("addressLine", siteDetails.getAddressLine());
		newNode.put("name", siteDetails.getName());
		newNode.put("city", siteDetails.getCity());
		newNode.put("state", siteDetails.getState());
		newNode.put("postalCode", siteDetails.getPostalCode());
		newNode.put("country", siteDetails.getCountry());
		newNode.put("globalLocationId", siteDetails.getGlobalLocationId());
		if (siteAddressNode.path("siteAddress").isArray()) {
			((ObjectNode) siteAddressNode).withArray("siteAddress").add(newNode);
		} else if (siteAddressNode.path("siteAddress").isTextual()) {
			String arrayText = siteAddressNode.path("siteAddress").asText();
			try {
				JsonNode arrayNode = mapper.readTree(arrayText);
				((ArrayNode) arrayNode).add(newNode);
				((ObjectNode) siteAddressNode).put("siteAddress", arrayNode.toString());
			} catch (IOException e) {
				logger.info("Exception", e);
			}
		}
	}

	protected void updateSiteDetailsFromSiteAddress(SiteDetails siteDetails, JsonNode jsonNode) {
		siteDetails.setAddressLine(JacksonUtil.nodeAtPointerAsText(jsonNode, "/addressLine"));
		siteDetails.setName(JacksonUtil.nodeAtPointerAsText(jsonNode, "/name"));
		siteDetails.setCity(JacksonUtil.nodeAtPointerAsText(jsonNode, "/city"));
		siteDetails.setState(JacksonUtil.nodeAtPointerAsText(jsonNode, "/state"));
		siteDetails.setPostalCode(JacksonUtil.nodeAtPointerAsText(jsonNode, "/postalCode"));
		siteDetails.setCountry(JacksonUtil.nodeAtPointerAsText(jsonNode, "/country"));
		siteDetails.setGlobalLocationId(JacksonUtil.nodeAtPointerAsText(jsonNode, "/globalLocationId"));
	}

	/*
	private LocationsWrapper getSiteDictionaryLocations(ServiceValidationRequest request, Long nxTxnId) {
		LocationsWrapper locationsWrapper = new LocationsWrapper();
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		NxMpSiteDictionary siteDictionary = nxMpSiteDictionaryRepository.findByNxTxnId(nxTxnId);
		try {
			locationsWrapper = objectMapper.readValue(siteDictionary.getSiteJson(), LocationsWrapper.class);
		} catch (IOException e) {
			logger.info("Exiting validateService() method while deserializing site json for the myPriceTransId: "
					+ request.getTransactionId());
		}
		return locationsWrapper;
	}

	private void updateLocations(Map<Long, Locations> locationMap, SiteDetails siteDetail) {
		if (locationMap.get(siteDetail.getNxSiteId()) != null) {
			siteDetail.setAddressLine(locationMap.get(siteDetail.getNxSiteId()).getStreet());
			siteDetail.setName(locationMap.get(siteDetail.getNxSiteId()).getLocName());
			siteDetail.setCity(locationMap.get(siteDetail.getNxSiteId()).getCity());
			siteDetail.setState(locationMap.get(siteDetail.getNxSiteId()).getState());
			siteDetail.setPostalCode(locationMap.get(siteDetail.getNxSiteId()).getZip());
			siteDetail.setCountry(locationMap.get(siteDetail.getNxSiteId()).getCountry());
		}
	}
	*/
}
