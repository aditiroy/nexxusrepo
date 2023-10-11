package com.att.sales.nexxus.myprice.transaction.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.service.BaseServiceImpl;
import com.att.sales.nexxus.common.CommonConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.myprice.transaction.model.UpdateTxnSiteUploadAddrsValidationSerQualResp;
import com.att.sales.nexxus.myprice.transaction.model.UpdateTxnSiteUploadAddrsValidationSerQualRespLocation;
import com.att.sales.nexxus.myprice.transaction.model.UpdateTxnSiteUploadDocuments;
import com.att.sales.nexxus.myprice.transaction.model.UpdateTxnSiteUploadFieldedAddress;
import com.att.sales.nexxus.myprice.transaction.model.UpdateTxnSiteUploadGISLocationAttributes;
import com.att.sales.nexxus.myprice.transaction.model.UpdateTxnSiteUploadIntegrationSiteDict;
import com.att.sales.nexxus.myprice.transaction.model.UpdateTxnSiteUploadLocationProperties;
import com.att.sales.nexxus.myprice.transaction.model.UpdateTxnSiteUploadLocations;
import com.att.sales.nexxus.myprice.transaction.model.UpdateTxnSiteUploadRequest;
import com.att.sales.nexxus.myprice.transaction.model.UpdateTxnSiteUploadResponse;
import com.att.sales.nexxus.myprice.transaction.model.UpdateTxnSiteUploadSAGLocationAttributes;
import com.att.sales.nexxus.myprice.transaction.model.UpdateTxnSiteUploadSAGproperties;
import com.att.sales.nexxus.myprice.transaction.model.updateTxnSiteUploadPrimaryNpaNxx;
import com.att.sales.nexxus.util.HttpRestClient;
import com.att.sales.nexxus.util.RestClientUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
/**
 * 
 * @author Laxman Honawad
 *
 */
@Service("updateTxnSiteUploadServiceImpl")
public class UpdateTxnSiteUploadServiceImpl extends BaseServiceImpl {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(UpdateTxnSiteUploadServiceImpl.class);

	/** The env. */
	@Autowired
	private Environment env;

	@Autowired
	private RestClientUtil restClient;
	
	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private HttpRestClient httpRestClient;

	@Value("${myprice.proxy.enabled}")
	private String isProxyEnabled;
	
	public UpdateTxnSiteUploadResponse updateTransactionSiteUpload(String myPriceTxnId, String siteJsonString, Map<String, Object> requestParams)
			throws SalesBusinessException {
		logger.info("Entering updateTransactionSiteUpload() method");
		UpdateTxnSiteUploadResponse response = null;
		try {
			ObjectNode request = mapper.createObjectNode();
			request.with("documents").put("integrationSiteDict1", translateSiteJsonRemoveDuplicatedNxSiteId(siteJsonString));
			String uri = env.getProperty("myprice.updateTransactionSiteUploadRequest");
			uri = uri.replace("{transactionId}", myPriceTxnId);
			String requestString = request.toString();
			Map<String, String> requestHeaders = new HashMap<String, String>();
			requestHeaders.put(StringConstants.REQUEST_AUTHORIZATION, "Basic "+env.getProperty(com.att.sales.nexxus.common.CommonConstants.MYPRICE_AUTHORIZATION));
			String proxy = null;
			if(StringConstants.CONSTANT_Y.equalsIgnoreCase(isProxyEnabled)) {
				proxy = env.getProperty(CommonConstants.CDP_HTTP_PROXY);
			}
			String transResponse = httpRestClient.callHttpRestClient(uri, HttpMethod.POST, null, requestString, 
					requestHeaders, proxy);

			if (null != transResponse) {
				response = (UpdateTxnSiteUploadResponse) restClient.processResult(transResponse,
						UpdateTxnSiteUploadResponse.class);
				setSuccessResponse(response);
			}
		} catch (SalesBusinessException e) {
			logger.error("exception occured in Myprice updateTransactionSiteUpload call");
			e.getMessage();
		}
		logger.info("Existing updateTransactionSiteUpload() method");
		return response;

	}
	
/*	public String translateSiteJson(String siteJsonString) throws SalesBusinessException {
		if(siteJsonString != null) {
			JsonNode r;
			try {
				r = mapper.readTree(siteJsonString);
			} catch (IOException e) {
				throw new SalesBusinessException();
			}
			ObjectNode root = (ObjectNode) r;
			for (JsonNode location : root.path("locations")) {
				((ObjectNode) location).set("Name", location.get("locName"));
				((ObjectNode) location).set("AddressValidationServiceQualificationResponse", location.get("avsqResponse"));
				((ObjectNode) location).remove("avsqResponse");
			}
			root.set("Locations", root.get("locations"));
			root.remove("locations");
			return root.toString();
		}
		return null;
	}*/
	
	public String translateSiteJsonRemoveDuplicatedNxSiteId(String siteJsonString) throws SalesBusinessException {
		if(siteJsonString != null) {
			JsonNode r;
			try {
				r = mapper.readTree(siteJsonString);
			} catch (IOException e) {
				throw new SalesBusinessException();
			}
			ObjectNode root = mapper.createObjectNode();
			root.set("status", r.get("status"));
			Set<String> nxSiteIdSet = new HashSet<>();
			for (JsonNode location : r.path("locations")) {
				if (nxSiteIdSet.add(location.path("nxSiteId").asText())) {
					((ObjectNode) location).set("Name", location.get("locName"));
					((ObjectNode) location).set("AddressValidationServiceQualificationResponse",
							location.get("avsqResponse"));
					((ObjectNode) location).remove("avsqResponse");
					root.withArray("Locations").add(location);
				}
			}
			return root.toString();
		}
		return null;
	}
	
	/*	public static void main(String[] args) {
		String jsonString = "{\"status\":\"COMPLETED\",\"locations\":[{\"id\":29598,\"globalLocationId\":\"000008K2KG\",\"nxSiteId\":4642,\"locName\":\"loc1\",\"street\":\"100 CONGRESS AVE\",\"city\":\"AUSTIN\",\"state\":\"TX\",\"zip\":\"78701\",\"validationStatus\":\"VALID\",\"avsqResponse\":{\"Location\":{\"GISLocationAttributes\":[{\"globalLocationId\":\"000008K2KG\",\"FieldedAddress\":{\"city\":\"AUSTIN\",\"state\":\"TX\",\"postalCode\":\"78701\",\"singleLineStandardizedAddress\":\"100 CONGRESS AVE,AUSTIN,TX,78701-4072\",\"country\":\"USA\",\"postalCodePlus4\":\"4072\"},\"LocationProperties\":{\"matchStatus\":\"M\",\"buildingClli\":\"AUSTTXDK\",\"regionFranchiseStatus\":\"Y\",\"addressMatchCode\":\"S80\",\"swcCLLI\":\"AUSTTXGR\",\"localProviderName\":\"SOUTHWESTERN BELL\",\"lataCode\":\"558\",\"primaryNpaNxx\":{\"npa\":\"512\",\"nxx\":\"232\"}}}],\"SAGLocationAttributes\":[{\"SAGProperties\":{\"region\":\"SW\"}}]}}}]}";
		UpdateTxnSiteUploadServiceImpl impl = new UpdateTxnSiteUploadServiceImpl();
		UpdateTxnSiteUploadRequest request = impl.getRequest(jsonString);
		String requestString = JacksonUtil.toString(request);
		System.out.println("Siteupload Request body : "+requestString);
	}*/

	public UpdateTxnSiteUploadRequest getRequest(String siteJsonString)throws JSONException, SalesBusinessException, JsonProcessingException{
		logger.info("preparing siteupload request");
		JSONObject jsonObject = new JSONObject(siteJsonString);
		JSONArray locationsJsonArray = jsonObject.getJSONArray("locations");
		UpdateTxnSiteUploadRequest request = null;
		if (null != locationsJsonArray && locationsJsonArray.length() >= 1) {
			request = new UpdateTxnSiteUploadRequest();
			UpdateTxnSiteUploadDocuments documents = new UpdateTxnSiteUploadDocuments();
			UpdateTxnSiteUploadIntegrationSiteDict integrationSiteDict = new UpdateTxnSiteUploadIntegrationSiteDict();
			List<UpdateTxnSiteUploadLocations> locations = new ArrayList<>();
			for (int index = 0; index < locationsJsonArray.length(); index++) {
				JSONObject locationsJsonObject = (JSONObject) locationsJsonArray.get(index);
				if (null != locationsJsonObject) {
					UpdateTxnSiteUploadLocations siteUploadLocation = new UpdateTxnSiteUploadLocations();
					if (!locationsJsonObject.isNull("id")) {
						Long id = locationsJsonObject.getLong("id");
						siteUploadLocation.setId(id);
					}
					if (!locationsJsonObject.isNull("nxSiteId")) {
						Long nxSiteId = locationsJsonObject.getLong("nxSiteId");
						siteUploadLocation.setNxSiteId(nxSiteId);
					}
					if (!locationsJsonObject.isNull("siteInfoSource")) {
						String siteInfoSource = locationsJsonObject.getString("siteInfoSource");
						siteUploadLocation.setSiteInfoSource(siteInfoSource);
					}
					if (!locationsJsonObject.isNull("locName")) {
						String name = locationsJsonObject.getString("locName");
						siteUploadLocation.setName(name);
					}
					if (!locationsJsonObject.isNull("validationStatus")) {
						String validationStatus = locationsJsonObject.getString("validationStatus");
						siteUploadLocation.setValidationStatus(validationStatus);
					}
					JSONObject addressValidationServiceQual = locationsJsonObject
							.optJSONObject("avsqResponse");
					if (Optional.ofNullable(addressValidationServiceQual).isPresent()) {
						UpdateTxnSiteUploadAddrsValidationSerQualResp addressValidationSerQualResponse = new UpdateTxnSiteUploadAddrsValidationSerQualResp();
						JSONObject locationJsonObject = addressValidationServiceQual.optJSONObject("Location");
						if (Optional.ofNullable(locationJsonObject).isPresent()) {
							UpdateTxnSiteUploadAddrsValidationSerQualRespLocation location = new UpdateTxnSiteUploadAddrsValidationSerQualRespLocation();
							JSONArray gisLocationAttributes = locationJsonObject.optJSONArray("GISLocationAttributes");
							if (null != gisLocationAttributes && gisLocationAttributes.length() >= 1) {
								List<UpdateTxnSiteUploadGISLocationAttributes> gisLocationAttributeList = new ArrayList<>();
								for (int jIndex = 0; jIndex < gisLocationAttributes.length(); jIndex++) {
									JSONObject locationAttributeJsonObject = (JSONObject) gisLocationAttributes
											.get(jIndex);
									if (Optional.ofNullable(locationAttributeJsonObject).isPresent()) {
										UpdateTxnSiteUploadGISLocationAttributes gisLocationAttribute = new UpdateTxnSiteUploadGISLocationAttributes();
										JSONObject fieldedAddressJsonObject = locationAttributeJsonObject
												.optJSONObject("FieldedAddress");
										if (Optional.ofNullable(fieldedAddressJsonObject).isPresent()) {
											UpdateTxnSiteUploadFieldedAddress fieldedAddress = new UpdateTxnSiteUploadFieldedAddress();
											if (!fieldedAddressJsonObject.isNull("singleLineStandardizedAddress")) {
												String singleLineStandardizedAddress = fieldedAddressJsonObject
														.getString("singleLineStandardizedAddress");
												fieldedAddress.setSingleLineStandardizedAddress(
														singleLineStandardizedAddress);
											}
											if (!fieldedAddressJsonObject.isNull("country")) {
												String country = fieldedAddressJsonObject.getString("country");
												fieldedAddress.setCountry(country);
											}
											if (!fieldedAddressJsonObject.isNull("city")) {
												String city = fieldedAddressJsonObject.getString("city");
												fieldedAddress.setCity(city);
											}
											if (!fieldedAddressJsonObject.isNull("postalCode")) {
												String postalCode = fieldedAddressJsonObject.getString("postalCode");
												fieldedAddress.setPostalCode(postalCode);
											}
											if (!fieldedAddressJsonObject.isNull("postalCodePlus4")) {
												String postalCodePlus4 = fieldedAddressJsonObject
														.getString("postalCodePlus4");
												fieldedAddress.setPostalCodePlus(postalCodePlus4);
											}
											if (!fieldedAddressJsonObject.isNull("state")) {
												String state = fieldedAddressJsonObject.getString("state");
												fieldedAddress.setState(state);
											}
											if (!fieldedAddressJsonObject.isNull("unitValue")) {
												String unitValue = fieldedAddressJsonObject.getString("unitValue");
												fieldedAddress.setUnitValue(unitValue);
											}
											if (!fieldedAddressJsonObject.isNull("structureValue")) {
												String structureValue = fieldedAddressJsonObject.getString("structureValue");
												fieldedAddress.setStructureValue(structureValue);
											}
											if (!fieldedAddressJsonObject.isNull("levelValue")) {
												String levelValue = fieldedAddressJsonObject.getString("levelValue");
												fieldedAddress.setLevelValue(levelValue);
											}
											gisLocationAttribute.setFieldedAddress(fieldedAddress);
										}
										JSONObject locationPropertiesJsonObject = locationAttributeJsonObject
												.optJSONObject("LocationProperties");
										logger.info("preparing LocationProperties request");
										if (Optional.ofNullable(locationPropertiesJsonObject).isPresent()) {
											UpdateTxnSiteUploadLocationProperties locationProperties = new UpdateTxnSiteUploadLocationProperties();
											if (!locationPropertiesJsonObject.isNull("matchStatus")) {
												String matchStatus = locationPropertiesJsonObject
														.getString("matchStatus");
												locationProperties.setMatchStatus(matchStatus);
											}
											if (!locationPropertiesJsonObject.isNull("buildingClli")) {
												String buildingClli = locationPropertiesJsonObject
														.getString("buildingClli");
												locationProperties.setBuildingClli(buildingClli);
											}
											if (!locationPropertiesJsonObject.isNull("regionFranchiseStatus")) {
												String regionFranchiseStatus = locationPropertiesJsonObject
														.getString("regionFranchiseStatus");
												locationProperties.setRegionFranchiseStatus(regionFranchiseStatus);
											}
											if (!locationPropertiesJsonObject.isNull("addressMatchCode")) {
												String addressMatchCode = locationPropertiesJsonObject
														.getString("addressMatchCode");
												locationProperties.setAddressMatchCode(addressMatchCode);
											}
											if (!locationPropertiesJsonObject.isNull("swcCLLI")) {
												String swcCLLI = locationPropertiesJsonObject.getString("swcCLLI");
												locationProperties.setSwcClli(swcCLLI);
											}
											if(!locationPropertiesJsonObject.isNull("localProviderName")) {
												String localProviderName = locationPropertiesJsonObject.getString("localProviderName");
												locationProperties.setLocalProviderName(localProviderName);
											}
											if(!locationPropertiesJsonObject.isNull("lataCode")) {
												String lataCode = locationPropertiesJsonObject.getString("lataCode");
												locationProperties.setLataCode(lataCode);
											}
											JSONObject primaryNpaNxxJsonObject = locationPropertiesJsonObject.optJSONObject("primaryNpaNxx");
											if (Optional.ofNullable(primaryNpaNxxJsonObject).isPresent()) {
												updateTxnSiteUploadPrimaryNpaNxx primaryNpaxxDetails = new updateTxnSiteUploadPrimaryNpaNxx();
												if(!primaryNpaNxxJsonObject.isNull("npa")) {
													String primaryNpa = primaryNpaNxxJsonObject.getString("npa");
													primaryNpaxxDetails.setNpa(primaryNpa);
												}
												if(!primaryNpaNxxJsonObject.isNull("nxx")) {
													String primaryNxx =  primaryNpaNxxJsonObject.getString("nxx");
													primaryNpaxxDetails.setNxx(primaryNxx);
												}
												locationProperties.setPrimaryNpaNxx(primaryNpaxxDetails);
											}
											gisLocationAttribute.setLocationProperties(locationProperties);
										}
										if (!locationAttributeJsonObject.isNull("globalLocationId")) {
											String globalLocationId = locationAttributeJsonObject
													.getString("globalLocationId");
											gisLocationAttribute.setGlobalLocationId(globalLocationId);
										}
										gisLocationAttributeList.add(gisLocationAttribute);
									}
								}
								location.setGisLocationAttributes(gisLocationAttributeList);
							}
								
							JSONArray sagLocationProperties = locationJsonObject.has("SAGLocationAttributes") ? locationJsonObject.getJSONArray("SAGLocationAttributes") : null;
							logger.info("preparing LocationProperties request");
							if(null != sagLocationProperties && sagLocationProperties.length() >= 1)
							{
									List<UpdateTxnSiteUploadSAGLocationAttributes> sagPropertiesList = new ArrayList<>();
									for(int jIndexsag = 0; jIndexsag < sagLocationProperties.length(); jIndexsag++ )
									{
										JSONObject sagPropertiesJsonObject = (JSONObject) sagLocationProperties.get(jIndexsag);
										if(Optional.ofNullable(sagPropertiesJsonObject).isPresent()) {
											UpdateTxnSiteUploadSAGLocationAttributes sagLocationAttributes = new UpdateTxnSiteUploadSAGLocationAttributes();
											JSONObject sagPropertiesJSON = sagPropertiesJsonObject.optJSONObject("SAGProperties");
											UpdateTxnSiteUploadSAGproperties sagPropertyDetails = new UpdateTxnSiteUploadSAGproperties();
											if(sagPropertiesJSON != null && !sagPropertiesJSON.isNull("region")) {
												String region = sagPropertiesJSON.getString("region");
												sagPropertyDetails.setRegion(region);
											}
											sagLocationAttributes.setSagProperties(sagPropertyDetails);
											
											sagPropertiesList.add(sagLocationAttributes);
										}
										location.setSagLocationAttributes(sagPropertiesList);
									}
									
								}
									addressValidationSerQualResponse.setLocation(location);
								}
							
						siteUploadLocation.setAddressValidationSerQualResponse(addressValidationSerQualResponse);
					}
					locations.add(siteUploadLocation);
				}
			}
			integrationSiteDict.setLocations(locations);
			documents.setIntegrationSiteDict(getIntegrationSiteDictJson(integrationSiteDict));
			request.setDocuments(documents);
		}
		return request;
	}
	
	public String getIntegrationSiteDictJson(UpdateTxnSiteUploadIntegrationSiteDict integrationSiteDict) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(integrationSiteDict);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
