package com.att.sales.nexxus.inr;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.constant.InrConstants;
import com.att.sales.nexxus.dao.model.InrXmlToJsonRule;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.repository.InrXmlToJsonRuleDao;
import com.att.sales.nexxus.service.NxMyPriceRepositoryServce;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The Class InrIntermediateJsonGenerator.
 */
public abstract class InrIntermediateJsonGenerator {

	/** The Constant SEPARATOR. */
	public static final String SEPARATOR = "\\s*,\\s*";
	public static final String TRIM = "TRIM";
	public static final String REPLACE = "REPLACE";
	public static final String FALLOUTMATCHINGID = "FALLOUTMATCHINGID";
	public static final String NXSITEMATCHINGID = "NXSITEMATCHINGID";

	/** The mapper. */
	protected ObjectMapper mapper;

	/** The inr xml to json rule dao. */
	protected InrXmlToJsonRuleDao inrXmlToJsonRuleDao;

	/** The node map. */
	protected Map<String, JsonNode> nodeMap = new HashMap<>();

	/** The inr xml to json rule map. */
	protected Map<String, InrXmlToJsonRule> inrXmlToJsonRuleMap;
	
	protected NxMyPriceRepositoryServce nxMyPriceRepositoryServce;
	
	/**
	 * Instantiates a new inr intermediate json generator.
	 *
	 * @param mapper
	 *            the mapper
	 * @param inrXmlToJsonRuleDao
	 *            the inr xml to json rule dao
	 * @param nxLookupDataRepository
	 *            the nx lookup data repository
	 */
	protected InrIntermediateJsonGenerator(ObjectMapper mapper, InrXmlToJsonRuleDao inrXmlToJsonRuleDao,
			NxMyPriceRepositoryServce nxMyPriceRepositoryServce) {
		this.mapper = mapper;
		this.inrXmlToJsonRuleDao = inrXmlToJsonRuleDao;
		this.nxMyPriceRepositoryServce = nxMyPriceRepositoryServce;
		nodeMap.put(InrConstants.ROOT_JSON_MAP_KEY, mapper.createObjectNode());
	}

	/**
	 * Inits the.
	 *
	 * @throws SalesBusinessException
	 *             the sales business exception
	 */
	protected void init() throws SalesBusinessException {
		initializeRuleMap();
		if (inrXmlToJsonRuleMap == null || inrXmlToJsonRuleMap.isEmpty()) {
			throw new SalesBusinessException(InrConstants.EMPTY_XML_JSON_RULE_EXCEPTION);
		}
	}

	/**
	 * Generate.
	 *
	 * @return the json node
	 * @throws SalesBusinessException
	 *             the sales business exception
	 */
	public abstract JsonNode generate() throws SalesBusinessException;

	/**
	 * Initialize rule map.
	 *
	 * @throws SalesBusinessException
	 *             the sales business exception
	 */
	protected abstract void initializeRuleMap() throws SalesBusinessException;

	/**
	 * check if a json node is valid only valid json node will be included in
	 * intermediate json validation by size and by required fields.
	 *
	 * @param inrXmlToJsonRule
	 *            the inr xml to json rule
	 * @param jsonNode
	 *            the json node
	 * @return true, if is node valid
	 */
	protected boolean isNodeValid(InrXmlToJsonRule inrXmlToJsonRule, JsonNode jsonNode) {
		if (inrXmlToJsonRule.getMinSize() != null && jsonNode.size() < inrXmlToJsonRule.getMinSize()) {
			return false;
		}
		if (StringUtils.isNotEmpty(inrXmlToJsonRule.getRequiredFields())) {
			String[] requiredFields = inrXmlToJsonRule.getRequiredFields().trim().split(SEPARATOR);
			for (String field : requiredFields) {
				if (jsonNode.path(field).isMissingNode()) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Convert data.
	 *
	 * @param inrXmlToJsonRule
	 *            the inr xml to json rule
	 * @param data
	 *            the data
	 * @return the string
	 */
	protected String convertData(InrXmlToJsonRule inrXmlToJsonRule, String data) {
		String res = convertData(inrXmlToJsonRule.getLookupDatasetName(), data);
		if (res == null || inrXmlToJsonRule.getOperations() == null) {
			return res;
		}
		if (inrXmlToJsonRule.getOperations().contains(TRIM)) {
			res = res.trim();
		}
		if (inrXmlToJsonRule.getOperations().contains(REPLACE)) {
			// replace non-word character to empty string
			res = res.replaceAll("\\W", "");
		}
		return res;
	}
	
	protected String convertData(String lookupDataSet, String data) {
		if (lookupDataSet == null) {
			return data;
		}
		NxLookupData nxLookupData = nxMyPriceRepositoryServce.getLookupDataByItemId(lookupDataSet).get(data);
		if (nxLookupData != null) {
			return nxLookupData.getDescription();
		}
		return data;
	}
}
