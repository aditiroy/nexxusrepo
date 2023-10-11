package com.att.sales.nexxus.inr;

import java.util.LinkedHashMap;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.att.sales.nexxus.dao.repository.InrXmlToJsonRuleDao;
import com.att.sales.nexxus.dao.repository.InrXmlToJsonRuleDao.InrXmlToJsonRuleDaoResult;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.dao.repository.NxUdfMappingDao;
import com.att.sales.nexxus.dao.repository.SalesMsDao;
import com.att.sales.nexxus.dao.repository.SalesMsProdcompUdfAttrValRepository;
import com.att.sales.nexxus.service.NexxusAIService;
import com.att.sales.nexxus.service.NxMyPriceRepositoryServce;
import com.att.sales.nexxus.util.UnmockableWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * A factory for creating Inr objects.
 */
@Component
public class InrFactory {

	/** The log. */
	private static Logger log = LoggerFactory.getLogger(InrFactory.class);

	/** The p 8 d local path. */
	@Value("${p8.local.destPath}")
	private String p8dLocalPath;

	/** The template path. */
	@Value("${nx.inrPreview.template.path}")
	private String templatePath;
	
	@Value("${nx.inr.address.edit.template.path}")
	private String inrAddressEditTemplatePath;

	/** The mapper. */
	@Autowired
	private ObjectMapper mapper;

	/** The inr xml to json rule dao. */
	@Autowired
	private InrXmlToJsonRuleDao inrXmlToJsonRuleDao;

	/** The nx lookup data repository. */
	@Autowired
	private NxLookupDataRepository nxLookupDataRepository;

	/** The unmockable wrapper. */
	@Autowired
	private UnmockableWrapper unmockableWrapper;

	/** The nexxus AI service. */
	@Autowired
	private NexxusAIService nexxusAIService;

	/** The em. */
	@PersistenceContext
	private EntityManager em;

	@Autowired
	private NxUdfMappingDao nxUdfMappingDao;

	@Autowired
	private SalesMsProdcompUdfAttrValRepository salesMsProdcompUdfAttrValRepository;
	
	@Autowired
	private SalesMsDao salesMsDao;
	
	@Autowired
	private NxMyPriceRepositoryServce nxMyPriceRepositoryServce;

	/**
	 * Gets the inr json to intermediate json.
	 *
	 * @param rawJson
	 *            the raw json
	 * @return the inr json to intermediate json
	 */
	public InrJsonToIntermediateJson getInrJsonToIntermediateJson(JsonNode rawJson) {
		return new InrJsonToIntermediateJson(mapper, inrXmlToJsonRuleDao, nxMyPriceRepositoryServce, rawJson);
	}

	/*
	 * no longer in use
	public InrXmlToIntermediateJson getInrXmlToIntermediateJson(String xmlFileName) {
		return new InrXmlToIntermediateJson(mapper, inrXmlToJsonRuleDao, nxLookupDataRepository, p8dLocalPath,
				xmlFileName);
	}
	*/

	/**
	 * Gets the output json generator.
	 *
	 * @param intermediateJson
	 *            the intermediate json
	 * @return the output json generator
	 */
	public OutputJsonGenerator getOutputJsonGenerator(JsonNode intermediateJson, String flowType) {
		return new OutputJsonGenerator(mapper, em, intermediateJson, nexxusAIService, nxMyPriceRepositoryServce, flowType);
	}
	
//	public InrPreviewGenerator getInrPreviewGenerator(List<JsonNode> nodes, int hash) {
//		return new InrPreviewGenerator(nodes, templatePath, p8dLocalPath, unmockableWrapper, hash);
//	}
	
	public InrPreviewGeneratorV1 getInrPreviewGeneratorV1(ArrayNode cdirDataArray, int hash) {
		return new InrPreviewGeneratorV1(cdirDataArray, templatePath, p8dLocalPath, unmockableWrapper, hash, mapper);
	}
	
	public InrPreviewGeneratorV1 getInrAddressEditGenerator(ArrayNode excelData, int hash) {
		return new InrPreviewGeneratorV1(excelData, inrAddressEditTemplatePath, p8dLocalPath, unmockableWrapper, hash, mapper);
	}

	public AseDppRequestToSnsdRequest getAseDppRequestToSnsdRequest(JsonNode dppRequest) {
		return new AseDppRequestToSnsdRequest(mapper, inrXmlToJsonRuleDao, nxMyPriceRepositoryServce, dppRequest, salesMsDao);
	}
	
	public AseDppRequestToSnsdSoldCancelRequest getAseDppRequestToSnsdSoldCancelRequest(JsonNode dppRequest) {
		return new AseDppRequestToSnsdSoldCancelRequest(mapper, inrXmlToJsonRuleDao, nxMyPriceRepositoryServce, dppRequest,salesMsDao,salesMsProdcompUdfAttrValRepository);
	}

	public AseDppRequestUdfTranslation getAseDppRequestUdfTranslation(JsonNode dppRequest) {
		return new AseDppRequestUdfTranslation(mapper, inrXmlToJsonRuleDao, dppRequest, nxUdfMappingDao,
				nxLookupDataRepository, salesMsProdcompUdfAttrValRepository);
	}
	
	public InrInventoryJsonFlatten getInrInventoryJsonFlatten(JsonNode inventoryJson, String rootTagValue) {
		return new InrInventoryJsonFlatten(mapper, inventoryJson, rootTagValue);
	}
	
	public InrInventoryJsonToIntermediateJson getInrInventoryJsonToIntermediateJson(JsonNode inventoryJson, InrXmlToJsonRuleDaoResult inrXmlToJsonRuleDaoResult) {
		return new InrInventoryJsonToIntermediateJson(mapper, inrXmlToJsonRuleDao, nxMyPriceRepositoryServce, inventoryJson, inrXmlToJsonRuleDaoResult);
	}
	
	public CopyOutputToIntermediateJson getCopyOutputToIntermediateJson(JsonNode outputJson, JsonNode intermediateJson) {
		return new CopyOutputToIntermediateJson(outputJson, intermediateJson);
	}
	
	public InrIntermediateJsonUpdate getInrIntermediateJsonUpdate(JsonNode outputJson, LinkedHashMap<String, Object> criteriaMap, LinkedHashMap<String, Object> dataMap, String action,
			String product, Set<String> cktIdforAugmentation, String flowType) {
		return new InrIntermediateJsonUpdate(outputJson, criteriaMap, dataMap, action, product, cktIdforAugmentation, flowType);
	}
}
