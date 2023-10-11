package com.att.sales.nexxus.inr;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.constant.InrConstants;
import com.att.sales.nexxus.dao.model.InrXmlToJsonRule;
import com.att.sales.nexxus.dao.repository.InrXmlToJsonRuleDao;
import com.att.sales.nexxus.service.NxMyPriceRepositoryServce;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * The Class InrXmlToIntermediateJson.
 * This class is no longer in use. Refer to InrInventoryJsonToIntermediateJson
 */
public class InrXmlToIntermediateJson extends InrIntermediateJsonGenerator {
	
	/** The log. */
	private static Logger log = LoggerFactory.getLogger(InrXmlToIntermediateJson.class);
	
	/** The xml event reader. */
	private XMLEventReader xmlEventReader;
	
	/** The p 8 d local path. */
	private String p8dLocalPath;
	
	/** The xml file name. */
	private String xmlFileName;

	/**
	 * Instantiates a new inr xml to intermediate json.
	 *
	 * @param mapper the mapper
	 * @param inrXmlToJsonRuleDao the inr xml to json rule dao
	 * @param nxLookupDataRepository the nx lookup data repository
	 * @param p8dLocalPath the p 8 d local path
	 * @param xmlFileName the xml file name
	 */
	protected InrXmlToIntermediateJson(ObjectMapper mapper, InrXmlToJsonRuleDao inrXmlToJsonRuleDao,
			NxMyPriceRepositoryServce nxMyPriceRepositoryServce, String p8dLocalPath, String xmlFileName) {
		super(mapper, inrXmlToJsonRuleDao, nxMyPriceRepositoryServce);
		this.p8dLocalPath = p8dLocalPath;
		this.xmlFileName = xmlFileName;
	}

	/* (non-Javadoc)
	 * @see com.att.sales.nexxus.inr.InrIntermediateJsonGenerator#init()
	 */
	@Override
	protected void init() throws SalesBusinessException {
		Path xmlPath = Paths.get(p8dLocalPath).resolve(FilenameUtils.getName(xmlFileName)); //NOSONAR
		log.info("creating InrXmlToIntermediateJson generator for xml file {}", xmlPath);
		XMLInputFactory xmlInputFactory = getXMLInputFactory();
		try {
			xmlEventReader = xmlInputFactory.createXMLEventReader(filesNewInputStream(xmlPath));
		} catch (XMLStreamException e) {
			log.error("Error occured in InrXmlToIntermediateJson costructor", e);
			throw new SalesBusinessException(InrConstants.XML_PARSE_EXCEPTION);
		} catch (IOException e) {
			log.error("Error occured in InrXmlToIntermediateJson costructor", e);
			throw new SalesBusinessException(InrConstants.XML_FILE_NOT_FOUND_EXCEPTION);
		}
		initializeRuleMap();
		if (inrXmlToJsonRuleMap == null || inrXmlToJsonRuleMap.isEmpty()) {
			throw new SalesBusinessException(InrConstants.EMPTY_XML_JSON_RULE_EXCEPTION);
		}
	}

	/**
	 * Gets the XML input factory.
	 *
	 * @return the XML input factory
	 */
	protected XMLInputFactory getXMLInputFactory() {
		return XMLInputFactory.newInstance();
	}

	/**
	 * Files new input stream.
	 *
	 * @param path the path
	 * @return the input stream
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected InputStream filesNewInputStream(Path path) throws IOException {
		return new InrFilterInputStream(Files.newInputStream(path));
	}

	/* (non-Javadoc)
	 * @see com.att.sales.nexxus.inr.InrIntermediateJsonGenerator#generate()
	 */
	@Override
	public JsonNode generate() throws SalesBusinessException {
		init();
		try {
			JsonPath rootPath = JsonPath.getRootPath();
			generateHelper(rootPath);
		} catch (XMLStreamException e) {
			log.error("Error occured in generate", e);
			throw new SalesBusinessException(InrConstants.XML_PARSE_EXCEPTION);
		}
		JsonNode res = nodeMap.get(InrConstants.ROOT_JSON_MAP_KEY);
		((ObjectNode) res).put(InrConstants.FLOW_TYPE, InrConstants.INR);
		return res;
	}

	/**
	 * Generate helper.
	 *
	 * @param path the path
	 * @return the json path
	 * @throws XMLStreamException the XML stream exception
	 */
	protected JsonPath generateHelper(JsonPath path) throws XMLStreamException {
		JsonPath currentPath = path;
		boolean isAfterStartTag = true;
		while (xmlEventReader.hasNext()) {
			XMLEvent event = xmlEventReader.peek();
			// event is not consumed when end tag is not equal to start tag in
			// INR_XML_TO_JSON_RULES
			// consume the event in all other cases
			switch (event.getEventType()) {
			case XMLEvent.START_ELEMENT:
				StartElement startElement = event.asStartElement();
				currentPath = currentPath.resolveContainerNode(startElement.getName().getLocalPart());
				xmlEventReader.nextEvent();
				isAfterStartTag = true;
				// recursive call point
				currentPath = processXmlStartTag(currentPath);
				break;
			case XMLEvent.CHARACTERS:
				xmlEventReader.nextEvent();
				processXmlCharacter(currentPath, event, isAfterStartTag);
				break;
			case XMLEvent.END_ELEMENT:
				isAfterStartTag = false;
				// recursive call return point
				EndTagResult endTagResult = processXmlEndTag(path, currentPath);
				currentPath = endTagResult.getNewPath();
				if (endTagResult.isReturn()) {
					return currentPath;
				}
				break;
			default:
				xmlEventReader.nextEvent();
				break;
			}
		}
		return currentPath;
	}

	/**
	 * For the start tag processing, if start tag will be converted to a json object
	 * or json array, recursive call starts.
	 *
	 * @param currentPath the current path
	 * @return the json path
	 * @throws XMLStreamException the XML stream exception
	 */
	protected JsonPath processXmlStartTag(JsonPath currentPath) throws XMLStreamException {
		JsonPath newPath = currentPath;
		if (inrXmlToJsonRuleMap.containsKey(currentPath.getPath())) {
			InrXmlToJsonRule inrXmlToJsonRule = inrXmlToJsonRuleMap.get(currentPath.getPath());
			if ("Y".equals(inrXmlToJsonRule.getFieldNullYn())) {
				String jsonKey = inrXmlToJsonRule.getFieldName();
				String jsonParent = inrXmlToJsonRule.getFieldParent();
				((ObjectNode) nodeMap.get(jsonParent)).set(jsonKey, null);
			}
			if (inrXmlToJsonRule.getJsonType().contains(InrConstants.JSON_TYPE_OBJECT)) {
				// create ObjectNode for holding the json object
				ObjectNode newNode = mapper.createObjectNode();
				nodeMap.put(inrXmlToJsonRule.getObjectName(), newNode);
				// recursive call made to populate the ObjectNode
				newPath = generateHelper(currentPath);
				// if the ObjectNode is valid, link it to its parent json node
				if (isNodeValid(inrXmlToJsonRule, newNode)) {
					String jsonKey = inrXmlToJsonRule.getObjectName();
					((ObjectNode) nodeMap.get(inrXmlToJsonRule.getObjectParent())).set(jsonKey, newNode);
				}
			} else if (inrXmlToJsonRule.getJsonType().contains(InrConstants.JSON_TYPE_ARRAY)) {
				// create ObjectNode for holding the json array element
				ObjectNode newNode = mapper.createObjectNode();
				nodeMap.put(inrXmlToJsonRule.getArrayElementName(), newNode);
				// recursive call made to populate the array element ObjectNode
				newPath = generateHelper(currentPath);
				// if the array element ObjectNode is valid, add it to its json array node
				if (isNodeValid(inrXmlToJsonRule, newNode)) {
					((ObjectNode) nodeMap.get(inrXmlToJsonRule.getArrayParent()))
							.withArray(inrXmlToJsonRule.getArrayName()).add(newNode);
				}
			}
		}
		return newPath;
	}

	/**
	 * Process xml character.
	 *
	 * @param currentPath the current path
	 * @param event the event
	 * @param isAfterStartTag the is after start tag
	 */
	protected void processXmlCharacter(JsonPath currentPath, XMLEvent event, boolean isAfterStartTag) {
		Characters characters = event.asCharacters();
		String data = characters.getData();
		if (data != null && !data.isEmpty() && inrXmlToJsonRuleMap.containsKey(currentPath.getPath())
				&& isAfterStartTag) {
			InrXmlToJsonRule inrXmlToJsonRule = inrXmlToJsonRuleMap.get(currentPath.getPath());
			String jsonKey = inrXmlToJsonRule.getFieldName();
			String jsonParent = inrXmlToJsonRule.getFieldParent();
			if (inrXmlToJsonRule.getJsonType().contains(InrConstants.JSON_TYPE_FIELD_TAG)) {
				((ObjectNode) nodeMap.get(jsonParent)).put(inrXmlToJsonRule.getFieldNameForTag(),
						currentPath.getFieldName());
			}
			if (inrXmlToJsonRule.getJsonType().contains(InrConstants.JSON_TYPE_FIELD_STR)) {
				((ObjectNode) nodeMap.get(jsonParent)).put(jsonKey, convertData(inrXmlToJsonRule, data));
			} else if (inrXmlToJsonRule.getJsonType().contains(InrConstants.JSON_TYPE_FIELD_DOUBLE)) {
				((ObjectNode) nodeMap.get(jsonParent)).put(jsonKey, Double.valueOf(data));
			} else if (inrXmlToJsonRule.getJsonType().contains(InrConstants.JSON_TYPE_FIELD_INT)) {
				((ObjectNode) nodeMap.get(jsonParent)).put(jsonKey, Integer.valueOf(data));
			} else if (inrXmlToJsonRule.getJsonType().contains(InrConstants.JSON_TYPE_FIELD_LONG)) {
				((ObjectNode) nodeMap.get(jsonParent)).put(jsonKey, Long.valueOf(data));
			}
		}
	}

	/**
	 * For the end tag processing, if return true, recursive call returns.
	 *
	 * @param path the path
	 * @param endPath the end path
	 * @return the end tag result
	 * @throws XMLStreamException the XML stream exception
	 */
	protected EndTagResult processXmlEndTag(JsonPath path, JsonPath endPath) throws XMLStreamException {
		if (inrXmlToJsonRuleMap.containsKey(path.getPath())
				&& endPath.getPath().equals(inrXmlToJsonRuleMap.get(path.getPath()).getXmlEndTag())) {
			if (inrXmlToJsonRuleMap.get(path.getPath()).getXmlStartTag()
					.equals(inrXmlToJsonRuleMap.get(path.getPath()).getXmlEndTag())) {
				xmlEventReader.nextEvent();
				return new EndTagResult(true, endPath.parent());
			} else {
				// end tag is not equal to star tag
				// the only case event is not consumed
				// return the recursive call in this star tag
				// the event will be later consumed in caller
				return new EndTagResult(true, endPath);
			}
		}
		xmlEventReader.nextEvent();
		return new EndTagResult(false, endPath.parent());
	}

	/**
	 * find the first start tag in xml which is the root xml tag using the root xml
	 * tag to find inrXmlToJsonRuleMap from DB.
	 *
	 * @throws SalesBusinessException the sales business exception
	 */
	@Override
	protected void initializeRuleMap() throws SalesBusinessException {
		try {
			while (xmlEventReader.hasNext()) {
				XMLEvent event = xmlEventReader.peek();
				if (event.getEventType() == XMLEvent.START_ELEMENT) {
					String rootTag = event.asStartElement().getName().getLocalPart();
					log.info("xml root element is: {}", rootTag);
					inrXmlToJsonRuleMap = inrXmlToJsonRuleDao.getInrXmlToJsonRuleMap(rootTag);
					break;
				}
				xmlEventReader.nextEvent();
			}
		} catch (XMLStreamException e) {
			log.error("Error occured in initializeRuleMap", e);
			throw new SalesBusinessException(InrConstants.XML_PARSE_EXCEPTION);
		}
	}

	/**
	 * The Class EndTagResult.
	 */
	public static class EndTagResult {
		
		/** The is return. */
		private boolean isReturn;
		
		/** The new path. */
		private JsonPath newPath;

		/**
		 * Instantiates a new end tag result.
		 *
		 * @param isReturn the is return
		 * @param newPath the new path
		 */
		public EndTagResult(boolean isReturn, JsonPath newPath) {
			super();
			this.isReturn = isReturn;
			this.newPath = newPath;
		}

		/**
		 * Checks if is return.
		 *
		 * @return true, if is return
		 */
		public boolean isReturn() {
			return isReturn;
		}

		/**
		 * Gets the new path.
		 *
		 * @return the new path
		 */
		public JsonPath getNewPath() {
			return newPath;
		}
	}
}
