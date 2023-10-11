package com.att.sales.nexxus.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import com.att.aft.dme2.internal.apache.commons.lang.WordUtils;
import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.Message;
import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.framework.model.Status;
import com.att.sales.framework.model.constants.HttpErrorCodes;
import com.att.sales.framework.service.BaseServiceImpl;
import com.att.sales.framework.util.MessageResourcesUtil;
import com.att.sales.nexxus.common.CommonConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.dao.model.NxSsdfSpeedMapping;
import com.att.sales.nexxus.reteriveicb.model.AseThirdPartyDetail;
import com.att.sales.nexxus.reteriveicb.model.CIRSpeed;
import com.att.sales.nexxus.reteriveicb.model.CIRSpeedPrice;
import com.att.sales.nexxus.reteriveicb.model.ContractInvComponent;
import com.att.sales.nexxus.reteriveicb.model.ContractInvResponse;
import com.att.sales.nexxus.reteriveicb.model.ContractInvResponseBean;
import com.att.sales.nexxus.reteriveicb.model.ContractInventoryRequestBean;
import com.att.sales.nexxus.reteriveicb.model.ContractRates;
import com.att.sales.nexxus.reteriveicb.model.ContractRatesPrice;
import com.att.sales.nexxus.reteriveicb.model.CosPremium;
import com.att.sales.nexxus.reteriveicb.model.SDNCharges;
import com.att.sales.nexxus.reteriveicb.model.SDNEthernetContractResponse;
import com.att.sales.nexxus.reteriveicb.model.SDNEthernetContractResponseBean;
import com.att.sales.nexxus.reteriveicb.model.SDNMRC;
import com.att.sales.nexxus.reteriveicb.model.SDNMRCPrice;
import com.att.sales.nexxus.util.HttpRestClient;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import groovy.util.Eval;

/**
 * 
 * The class contractInventoryServiceImpl
 * 
 * @author VijayBasantpure
 *
 */
@Service
public class ContractInventoryServiceImpl extends BaseServiceImpl implements ContractInventoryService {
	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(ContractInventoryServiceImpl.class);

	/** The env. */
	@Autowired
	private Environment env;

	/** The mapper. */
	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private NxMyPriceRepositoryServce nxMyPriceRepositoryServce;

	@Autowired
	private HttpRestClient httpRestClient;
	
	@Value("${azure.proxy.enabled}")
	private String isProxyEnabled;

	@Override
	public ServiceResponse getContractInventory(ContractInventoryRequestBean request)
			throws  JsonParseException, JsonMappingException, IOException {
		ContractInvResponseBean contractInvResponseBean = new ContractInvResponseBean();
		logger.info("inside getContractInventory method");
		if(!StringUtils.isEmpty(request.getContractInventoryRequest().getContractNumber())) {
			Map<String, String> requestHeaders = new HashMap<>();
			requestHeaders.put("Content-Type", "application/json");
			
			String uri=env.getProperty("contractInventory.lgw.uri");
			uri=uri.concat("/"+request.getContractInventoryRequest().getContractNumber());
			Map<String, String> headers  = new HashMap<String, String>();
			headers.put(StringConstants.REQUEST_AUTHORIZATION, "Basic "+env.getProperty("contractInventory.authorization"));

			Long currentTime = System.currentTimeMillis();
			Long startTime = System.currentTimeMillis() - currentTime;
			logger.info("calling ssdf api start time :: {}", startTime);
			String response=null;
			try {
				String proxy = null;
				if(StringConstants.CONSTANT_Y.equals(isProxyEnabled)) {
					proxy = env.getProperty(CommonConstants.AZURE_HTTP_PROXY);
				}
				response = httpRestClient.callHttpRestClient(uri, HttpMethod.GET, null, null, headers, proxy);
			} catch (SalesBusinessException e) {
				return (ContractInvResponseBean) setErrorResponse(new ContractInvResponseBean(), "M00023");
			}
			logger.info("ssdf response ::::: {}", org.apache.commons.lang3.StringUtils.normalizeSpace(response));
			Long endTime = System.currentTimeMillis() - currentTime;
			long executionTime=endTime-startTime;
			logger.info("ssdf api execution time :: {}", executionTime);

			Long nCurrentTime=System.currentTimeMillis();
			Long nStartTime=System.currentTimeMillis() - nCurrentTime;
			logger.info("nexxus api start time :: {}", nStartTime);
			ContractInvResponse contractInvResponse = new ContractInvResponse();
			// checking for response is empty or not
			if (StringUtils.isNotEmpty(response)) {
				Map<String, String> ssdfComponents = nxMyPriceRepositoryServce
						.getDescDataFromLookup(MyPriceConstants.SSDF_COMPONENT);
				// reading the response as SDNEthernetContractResponseBean obj from dme response
				SDNEthernetContractResponseBean ssdfResponse = mapper.readValue(response,
						SDNEthernetContractResponseBean.class);
				if(ssdfResponse != null) {
					SDNEthernetContractResponse ssdf = ssdfResponse.getSdnEthernetContractResponse();
					if(ssdf != null) {
						contractInvResponse.setContractId(ssdf.getContractID());
						contractInvResponse.setContractTerm(ssdf.getContractTerm());
						contractInvResponse.setContractIcb(ssdf.getContractICB());
						contractInvResponse.setNetpricePercentage(ssdf.getNetPricePercentage());
						contractInvResponse.setPricePlanId(ssdf.getPricePlanId());
						contractInvResponse.setRatePlanId(ssdf.getRatePlanId());
						SDNCharges sdnCharges = ssdf.getSdnCharges();
						if(sdnCharges != null) {
							List<CosPremium> cosPremiums = sdnCharges.getCosPremium();
							List<ContractInvComponent> components = new ArrayList<>();
							if (CollectionUtils.isNotEmpty(cosPremiums)) {
								ContractInvComponent component = getCosPremiumComponent(cosPremiums, ssdfComponents);
								components.add(component);
							}
							List<SDNMRC> sdnMrcs = sdnCharges.getSdnMRC();
							if (CollectionUtils.isNotEmpty(sdnMrcs)) {
								ContractInvComponent portComponent = getPortComponent(sdnMrcs, ssdfComponents, ssdf.getMarketSegment());
								components.add(portComponent);
								ContractInvComponent portFeatureComponent = getPortFeatureComponent(sdnMrcs, ssdfComponents);
								components.add(portFeatureComponent);
							}
							List<AseThirdPartyDetail> aseThirdPartyDetails = sdnCharges.getAseThirdPartyDetail();
							if (CollectionUtils.isNotEmpty(aseThirdPartyDetails)) {
								List<ContractInvComponent> aseThirdPartyDetailComponent = get3PAComponent(aseThirdPartyDetails,
										ssdfComponents);
								components.addAll(aseThirdPartyDetailComponent);
							}
							contractInvResponse.setComponent(components);
						}
					}
				}
			} else {
				logger.info("DME2 Response is empty");
			}
			contractInvResponseBean.setContractInvResponse(contractInvResponse);
			Long nEndTime = System.currentTimeMillis() - nCurrentTime;
			logger.info("nexxus api end time :: {}", nEndTime);
			long nExecutionTime = nEndTime - nStartTime;
			logger.info("nexxus api execution time :: {}", nExecutionTime);
			logger.info("Exiting getContractInventory() method");
			setSuccessResponse(contractInvResponseBean);
		}else {
			contractInvResponseBean.setResponseStatus("ContractNumber is mandatory");
			return null;
		}

		
		return contractInvResponseBean;
	}

	private List<ContractInvComponent> get3PAComponent(List<AseThirdPartyDetail> aseThirdPartyDetails,
			Map<String, String> ssdfComponents) {
		List<ContractInvComponent> component = new ArrayList<ContractInvComponent>();

		List<ContractRates> cosContractRates = new ArrayList<>();
		ContractInvComponent cosComponent = new ContractInvComponent();
		List<ContractRates> accessContractRates = new ArrayList<>();
		ContractInvComponent accessComponent = new ContractInvComponent();
		List<ContractRates> mileageContractRates = new ArrayList<>();
		ContractInvComponent mileageComponent = new ContractInvComponent();
		List<String> mileageSpeeds = new ArrayList<>();
		List<String> connectionSpeeds = new ArrayList<>();
		Map<String, List<NxSsdfSpeedMapping>> mileageMpSpeeds = nxMyPriceRepositoryServce.getSsdfMileageData("Mileage", "ASEoD3PA");
		Map<String, List<NxSsdfSpeedMapping>> connectionMpSpeeds = nxMyPriceRepositoryServce.getSsdfConnectionData("Connection", "ASEoD3PA");
		for (AseThirdPartyDetail aseThirdPartyDetail : aseThirdPartyDetails) {
			if (aseThirdPartyDetail.getFieldName().equalsIgnoreCase("Class of Service Premium")) {
				String componentType = ssdfComponents.get(aseThirdPartyDetail.getFieldName());
				cosComponent.setComponentCodeType(componentType);
				get3paRates(aseThirdPartyDetail, cosContractRates, aseThirdPartyDetail.getCosPremium(),
						componentType);

			} else if (aseThirdPartyDetail.getFieldName().equalsIgnoreCase("Mileage")) {
				String componentType = ssdfComponents.get(aseThirdPartyDetail.getFieldName());
				mileageComponent.setComponentCodeType(componentType);
				String chargeDescription = aseThirdPartyDetail.getConnectionType() + " " + aseThirdPartyDetail.getPriceMileageGroup() + " " + aseThirdPartyDetail.getMileageType();
				getMileageAndConnection3paRates(aseThirdPartyDetail, mileageContractRates, mileageSpeeds, chargeDescription, mileageMpSpeeds);
				
			} else if (aseThirdPartyDetail.getFieldName().equalsIgnoreCase("Connection")) {

				String componentType = ssdfComponents.get(aseThirdPartyDetail.getFieldName());
				accessComponent.setComponentCodeType(componentType);
				String chargeDescription = aseThirdPartyDetail.getConnectionType() + " " + aseThirdPartyDetail.getPriceMileageGroup();
				getMileageAndConnection3paRates(aseThirdPartyDetail, accessContractRates, connectionSpeeds, chargeDescription, connectionMpSpeeds);
			}
		}
		mileageComponent.setContractRates(mileageContractRates);
		component.add(mileageComponent);
		cosComponent.setContractRates(cosContractRates);
		component.add(cosComponent);
		accessComponent.setContractRates(accessContractRates);
		component.add(accessComponent);
		return component;
	}
	
	private void getMileageAndConnection3paRates(AseThirdPartyDetail aseThirdPartyDetail, List<ContractRates> contractRates,
			List<String> distinctSpeeds, String description, Map<String, List<NxSsdfSpeedMapping>> mpSpeeds) {
		List<NxSsdfSpeedMapping> mappings = null;
		if(mpSpeeds.containsKey(description)) {
			mappings = mpSpeeds.get(description);
		}
		List<CIRSpeed> cirSpeeds = aseThirdPartyDetail.getCirSpeed();
		for (CIRSpeed cirSpeed : cirSpeeds) {
			boolean isSpeedRange = false;
			if(mappings != null) {
				for (NxSsdfSpeedMapping mapping : mappings) {
					if (StringUtils.isNotEmpty(mapping.getFormula())) {
						String str = mapping.getFormula().replace("speed", cirSpeed.getCir());
						Object res = Eval.me(str);
						if (res.toString().equalsIgnoreCase("true")) {
							String desc = description + " " + mapping.getSpeedRange();
							if (!distinctSpeeds.contains(desc)) {
								ContractRates contractRate = new ContractRates();
								contractRate.setChargeDescription(description);
								contractRate.setSpeed(mapping.getSpeedRange());
								CIRSpeedPrice cirPrice = cirSpeed.getPrice();
								ContractRatesPrice price = new ContractRatesPrice();
								price.setRateId(cirPrice.getRateId());
								price.setUsocId(null);
								price.setRateDescription(null);
								price.setRateType(cirPrice.getCirTypeOfRate());
								price.setRateCurrency(cirPrice.getCirCurrency());
								price.setRate(cirPrice.getCirListRate());
								price.setPricingTier(null);
								contractRate.setPrice(price);
								contractRates.add(contractRate);
								distinctSpeeds.add(desc);
							}
							isSpeedRange = true;
							break;
						}
					}

				}
			}else {
				isSpeedRange = false;
			}
			
			if(!isSpeedRange) {
				String speed = cirSpeed.getCir() + " " + WordUtils.capitalizeFully(cirSpeed.getCirUnits());
				String desc = description + " " + speed;
				if (!distinctSpeeds.contains(desc)) {
					ContractRates contractRate = new ContractRates();
					contractRate.setChargeDescription(description);
					contractRate.setSpeed(speed);
					CIRSpeedPrice cirPrice = cirSpeed.getPrice();
					ContractRatesPrice price = new ContractRatesPrice();
					price.setRateId(cirPrice.getRateId());
					price.setUsocId(null);
					price.setRateDescription(null);
					price.setRateType(cirPrice.getCirTypeOfRate());
					price.setRateCurrency(cirPrice.getCirCurrency());
					price.setRate(cirPrice.getCirListRate());
					price.setPricingTier(null);
					contractRate.setPrice(price);
					contractRates.add(contractRate);
					distinctSpeeds.add(desc);
				}
			}
		}   
		
	}

	private void get3paRates(AseThirdPartyDetail aseThirdPartyDetail, List<ContractRates> contractRates,
			String description, String componentCodeType) {
		List<CIRSpeed> cirSpeeds = aseThirdPartyDetail.getCirSpeed();
		for (CIRSpeed cirSpeed : cirSpeeds) {
			ContractRates contractRate = new ContractRates();
			contractRate.setChargeDescription(description);
			contractRate.setSpeed(cirSpeed.getCir() + " " + WordUtils.capitalizeFully(cirSpeed.getCirUnits()));
			CIRSpeedPrice cirPrice = cirSpeed.getPrice();
			ContractRatesPrice price = new ContractRatesPrice();
			price.setRateId(cirPrice.getRateId());
			price.setUsocId(null);
			price.setRateDescription(null);
			price.setRateType(cirPrice.getCirTypeOfRate());
			price.setRateCurrency(cirPrice.getCirCurrency());
			price.setRate(cirPrice.getCirListRate());
			price.setPricingTier(null);
			contractRate.setPrice(price);
			contractRates.add(contractRate);
		}
	}

	private ContractInvComponent getPortComponent(List<SDNMRC> sdnMrcs, Map<String, String> ssdfComponents,
			String marketSegment) {
		ContractInvComponent component = new ContractInvComponent();
		component.setComponentCodeType("Port");
		List<ContractRates> contractRates = new ArrayList<>();
		Set<String> sdnComponent = new TreeSet<String>();
		List<SDNMRC> portCon = sdnMrcs.stream().filter(n -> "Port Connection".equalsIgnoreCase(n.getPrice().getRateDescription())).collect(Collectors.toList());
		for (SDNMRC sdnMrc : portCon) {
			SDNMRCPrice sdnPrice = sdnMrc.getPrice();
			String portType = null;
			String uniquePort = null;
			//if (MyPriceConstants.WHOLESALE_MARKETSEGMENT.equalsIgnoreCase(marketSegment)) {
				if (sdnPrice.getRateCategory() != null) {
					portType = sdnPrice.getRateCategory();
				} else {
					portType = MyPriceConstants.PORT_TYPE_STANDARD;
				}
				uniquePort = sdnMrc.getMrcCharge() + "#" + portType;
			/*} else {
				uniquePort = sdnMrc.getMrcCharge();
			}*/
			if (sdnPrice.getRateDescription().equalsIgnoreCase("Port Connection") 
					&& !sdnComponent.contains(uniquePort)) {
				sdnComponent.add(uniquePort);
				//component.setComponentCodeType(ssdfComponents.get(sdnPrice.getRateDescription()));
				ContractRates contractRate = new ContractRates();
				ContractRatesPrice price = new ContractRatesPrice();
				String mrcCharge = sdnMrc.getMrcCharge();
				contractRate.setChargeDescription(mrcCharge);
				int index = mrcCharge.indexOf(" ");
				contractRate.setSpeed(mrcCharge.substring(0, index));
				price.setRateId(sdnPrice.getRateId());
				price.setUsocId(null);
				price.setRateDescription(sdnPrice.getRateDescription());
				price.setRateType(sdnPrice.getRateType());
				price.setRateCurrency(sdnPrice.getRateCurrency());
				price.setRate(sdnPrice.getRate());
				price.setPortType(portType);
				contractRate.setPrice(price);
				contractRates.add(contractRate);
				component.setContractRates(contractRates);
			}
		}
		sdnComponent = null;

		Set<String> sdnComponentPPCoS = new TreeSet<String>();
		List<SDNMRC> portConPPCoS = sdnMrcs.stream().filter(n -> "Port Connection - PPCOS".equalsIgnoreCase(n.getPrice().getRateDescription())).collect(Collectors.toList());
		for (SDNMRC sdnMrc : portConPPCoS) {
			SDNMRCPrice sdnPrice = sdnMrc.getPrice();
			String portType = null;
			String uniquePort = null;
			//if (MyPriceConstants.WHOLESALE_MARKETSEGMENT.equalsIgnoreCase(marketSegment)) {
				if (sdnPrice.getRateCategory() != null) {
					portType = sdnPrice.getRateCategory();
				} else {
					portType = MyPriceConstants.PORT_TYPE_STANDARD;
				}
				uniquePort = sdnMrc.getMrcCharge() + "#" + portType;
			/*} else {
				uniquePort = sdnMrc.getMrcCharge();
			}*/

			if (sdnPrice.getRateDescription().equalsIgnoreCase("Port Connection - PPCOS") 
					&& !sdnComponentPPCoS.contains(uniquePort)) {
				sdnComponentPPCoS.add(uniquePort);
				//component.setComponentCodeType(ssdfComponents.get(sdnPrice.getRateDescription()));
				ContractRates contractRate = new ContractRates();
				ContractRatesPrice price = new ContractRatesPrice();
				String mrcCharge = sdnMrc.getMrcCharge();
				contractRate.setChargeDescription(mrcCharge);
				int index = mrcCharge.indexOf(" ");
				contractRate.setSpeed(mrcCharge.substring(0, index));
				price.setRateId(sdnPrice.getRateId());
				price.setUsocId(null);
				price.setRateDescription(sdnPrice.getRateDescription());
				price.setRateType(sdnPrice.getRateType());
				price.setRateCurrency(sdnPrice.getRateCurrency());
				price.setRate(sdnPrice.getRate());
				price.setPortType(portType);
				contractRate.setPrice(price);
				contractRates.add(contractRate);
				component.setContractRates(contractRates);
			}
		}
		sdnComponentPPCoS = null;
		return component;

	}
	
	private ContractInvComponent getPortFeatureComponent(List<SDNMRC> sdnMrcs, Map<String, String> ssdfComponents) {
		ContractInvComponent component = new ContractInvComponent();
		Set<String> sdnComponent = new TreeSet<String>();
		List<ContractRates> contractRates = new ArrayList<>();
		List<SDNMRC> portFeature = sdnMrcs.stream().filter(n -> "Port Feature".equalsIgnoreCase(n.getPrice().getRateDescription()) || "Diverse Access (Per Port)".equalsIgnoreCase(n.getPrice().getRateDescription())).collect(Collectors.toList());
		for (SDNMRC sdnMrc : portFeature) {
			SDNMRCPrice sdnPrice = sdnMrc.getPrice();
			if (sdnPrice.getRateDescription().equalsIgnoreCase("Port Feature")
					&& !sdnComponent.contains(sdnMrc.getMrcCharge())) {
				sdnComponent.add(sdnMrc.getMrcCharge());
				component.setComponentCodeType(ssdfComponents.get(sdnPrice.getRateDescription()));
				ContractRates contractRate = new ContractRates();
				ContractRatesPrice price = new ContractRatesPrice();
				contractRate.setChargeDescription(sdnMrc.getMrcCharge());
				contractRate.setSpeed(null);
				price.setRateId(sdnPrice.getRateId());
				price.setUsocId(null);
				price.setRateDescription(sdnPrice.getRateDescription());
				price.setRateType(sdnPrice.getRateType());
				price.setRateCurrency(sdnPrice.getRateCurrency());
				price.setRate(sdnPrice.getRate());
				price.setPortType(sdnPrice.getRateCategory());
				contractRate.setPrice(price);
				contractRates.add(contractRate);
				component.setContractRates(contractRates);
			}else if (sdnPrice.getRateDescription().equalsIgnoreCase("Diverse Access (Per Port)")
					&& !sdnComponent.contains(sdnMrc.getMrcCharge())) {
				sdnComponent.add(sdnMrc.getMrcCharge());
				component.setComponentCodeType(ssdfComponents.get(sdnPrice.getRateDescription()));
				ContractRates contractRate = new ContractRates();
				ContractRatesPrice price = new ContractRatesPrice();
				contractRate.setChargeDescription(sdnMrc.getMrcCharge());
				contractRate.setSpeed(null);
				price.setRateId(sdnPrice.getRateId());
				price.setUsocId(null);
				price.setRateDescription(sdnPrice.getRateDescription());
				price.setRateType(sdnPrice.getRateType());
				price.setRateCurrency(sdnPrice.getRateCurrency());
				price.setRate(sdnPrice.getRate());
				price.setPortType(sdnPrice.getRateCategory());
				contractRate.setPrice(price);
				contractRates.add(contractRate);
				component.setContractRates(contractRates);
			}
		}
		sdnComponent = null;
		
		return component;

	}

	private ContractInvComponent getCosPremiumComponent(List<CosPremium> cosPremiums,
			Map<String, String> ssdfComponents) {

		ContractInvComponent component = new ContractInvComponent();
		component.setComponentCodeType(ssdfComponents.get("CosPremium"));
		List<ContractRates> contractRates = new ArrayList<>();
		for (CosPremium cosPremium : cosPremiums) {
			String premium = cosPremium.getCosPremium();
			List<CIRSpeed> cirSpeeds = cosPremium.getCirSpeed();
			for (CIRSpeed cirSpeed : cirSpeeds) {
				ContractRates contractRate = new ContractRates();
				contractRate.setChargeDescription(premium);
				contractRate.setSpeed(cirSpeed.getCir() + " " + WordUtils.capitalizeFully(cirSpeed.getCirUnits()));
				CIRSpeedPrice cirPrice = cirSpeed.getPrice();
				ContractRatesPrice price = new ContractRatesPrice();
				price.setRateId(cirPrice.getRateId());
				price.setUsocId(null);
				price.setRateDescription(cirPrice.getRateDescription());
				price.setRateType(cirPrice.getCirTypeOfRate());
				price.setRateCurrency(cirPrice.getCirCurrency());
				price.setRate(cirPrice.getCirListRate());
				price.setPricingTier(cirPrice.getRateCategory());
				contractRate.setPrice(price);
				contractRates.add(contractRate);
			}
			component.setContractRates(contractRates);
		}

		return component;
	}
	/**
	 * Sets the error response.
	 *
	 * @param response the response
	 * @param errorCode the error code
	 * @return the service response
	 */
	public ServiceResponse setErrorResponse(ServiceResponse response, String errorCode) {
		Status status = new Status();
		List<Message> messageList = new ArrayList<>();
		Message msg = MessageResourcesUtil.getMessageMapping().get(errorCode);
		messageList.add(msg);
		status.setCode(HttpErrorCodes.SERVER_ERROR.toString());
		status.setMessages(messageList);
		response.setStatus(status);
		return response;
	}

}
