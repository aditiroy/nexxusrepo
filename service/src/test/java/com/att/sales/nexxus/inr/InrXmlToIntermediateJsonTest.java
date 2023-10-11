package com.att.sales.nexxus.inr;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.constant.InrConstants;
import com.att.sales.nexxus.dao.model.InrXmlToJsonRule;
import com.att.sales.nexxus.dao.repository.InrXmlToJsonRuleDao;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.inr.InrXmlToIntermediateJson.EndTagResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
@ExtendWith(MockitoExtension.class)
public class InrXmlToIntermediateJsonTest {
	@Mock
	private XMLEventReader xmlEventReader;
	@Mock
	private ObjectMapper mapper;
	@Mock
	private InrXmlToJsonRuleDao inrXmlToJsonRuleDao;
	@Mock
	private Map<String, JsonNode> nodeMap;
	@Mock
	private Map<String, InrXmlToJsonRule> inrXmlToJsonRuleMap;
	@Mock
	private NxLookupDataRepository nxLookupDataRepository;
	@Spy
	@InjectMocks
	private InrXmlToIntermediateJson inrXmlToIntermediateJson;
	@Mock
	private XMLInputFactory xmlInputFactory;
	@Mock
	private InputStream inputStream;
	@Mock
	private ObjectNode objectNode;
	@Mock
	private ArrayNode arrayNode;
	@Mock
	private XMLEvent xMLEvent;
	@Mock
	private QName qName;
	@Mock
	private StartElement startElement;
	@Mock
	private EndElement endElement;
	@Mock
	private Characters characters;

	@BeforeEach
	public void initStringField() {
		ReflectionTestUtils.setField(inrXmlToIntermediateJson, "p8dLocalPath", "p8dLocalPath");
		ReflectionTestUtils.setField(inrXmlToIntermediateJson, "xmlFileName", "xmlFileName");
	}

	@Test
	public void initTest() throws SalesBusinessException, IOException {
		doReturn(xmlInputFactory).when(inrXmlToIntermediateJson).getXMLInputFactory();
		doNothing().when(inrXmlToIntermediateJson).initializeRuleMap();
		doReturn(inputStream).when(inrXmlToIntermediateJson).filesNewInputStream(any());
		ReflectionTestUtils.setField(inrXmlToIntermediateJson, "inrXmlToJsonRuleMap", inrXmlToJsonRuleMap);
		inrXmlToIntermediateJson.init();
	}

	@Test
	public void initTestException() throws IOException, SalesBusinessException {
		doReturn(xmlInputFactory).when(inrXmlToIntermediateJson).getXMLInputFactory();
		doThrow(IOException.class).when(inrXmlToIntermediateJson).filesNewInputStream(any());
		inrXmlToIntermediateJson.init();
	}

	@Test
	public void initTestException1() throws IOException, XMLStreamException, SalesBusinessException {
		doReturn(xmlInputFactory).when(inrXmlToIntermediateJson).getXMLInputFactory();
		doReturn(inputStream).when(inrXmlToIntermediateJson).filesNewInputStream(any());
		doThrow(XMLStreamException.class).when(xmlInputFactory).createXMLEventReader(inputStream);
		inrXmlToIntermediateJson.init();
	}

	@Test
	public void initTestException2() throws IOException, XMLStreamException, SalesBusinessException {
		doReturn(xmlInputFactory).when(inrXmlToIntermediateJson).getXMLInputFactory();
		doReturn(inputStream).when(inrXmlToIntermediateJson).filesNewInputStream(any());
		when(xmlInputFactory.createXMLEventReader(inputStream)).thenReturn(xmlEventReader);
		doNothing().when(inrXmlToIntermediateJson).initializeRuleMap();
		inrXmlToIntermediateJson.init();
	}

	@Test
	public void getXMLInputFactoryTest() {
		assertNotNull(inrXmlToIntermediateJson.getXMLInputFactory());
	}

	@Test
	public void filesNewInputStreamTest() throws IOException {
		InputStream filesNewInputStream = inrXmlToIntermediateJson
				.filesNewInputStream(Paths.get("src/main/resources/message_resources.xml"));
		filesNewInputStream.close();
	}

	@Test
	public void generateTest() throws SalesBusinessException, XMLStreamException {
		ReflectionTestUtils.setField(inrXmlToIntermediateJson, "nodeMap", nodeMap);
		doNothing().when(inrXmlToIntermediateJson).init();
		doReturn(null).when(inrXmlToIntermediateJson).generateHelper(any());
		when(nodeMap.get(any())).thenReturn(objectNode);
		assertSame(objectNode, inrXmlToIntermediateJson.generate());
	}

	@Test
	public void generateTestException() throws SalesBusinessException, XMLStreamException {
		doNothing().when(inrXmlToIntermediateJson).init();
		doThrow(XMLStreamException.class).when(inrXmlToIntermediateJson).generateHelper(any());
		inrXmlToIntermediateJson.generate();
	}

	@Test
	public void generateHelperTest() throws XMLStreamException {
		ReflectionTestUtils.setField(inrXmlToIntermediateJson, "xmlEventReader", xmlEventReader);
		when(xmlEventReader.peek()).thenReturn(xMLEvent);
		JsonPath path = JsonPath.getRootPath();

		// case 1
		when(xmlEventReader.hasNext()).thenReturn(true, false);
		when(xMLEvent.getEventType()).thenReturn(XMLEvent.START_ELEMENT);
		when(xMLEvent.asStartElement()).thenReturn(startElement);
		when(startElement.getName()).thenReturn(qName);
		doReturn(path).when(inrXmlToIntermediateJson).processXmlStartTag(any());
		inrXmlToIntermediateJson.generateHelper(path);

		// case 2
		when(xmlEventReader.hasNext()).thenReturn(true, false);
		when(xMLEvent.getEventType()).thenReturn(XMLEvent.CHARACTERS);
		doNothing().when(inrXmlToIntermediateJson).processXmlCharacter(any(), any(), anyBoolean());
		inrXmlToIntermediateJson.generateHelper(path);

		// case 3
		when(xmlEventReader.hasNext()).thenReturn(true, false);
		when(xMLEvent.getEventType()).thenReturn(XMLEvent.END_ELEMENT);
		when(xMLEvent.asEndElement()).thenReturn(endElement);
		when(endElement.getName()).thenReturn(qName);
		EndTagResult endTagResult = new EndTagResult(false, path);
		doReturn(endTagResult).when(inrXmlToIntermediateJson).processXmlEndTag(any(), any());
		inrXmlToIntermediateJson.generateHelper(null);

		// case 4
		when(xmlEventReader.hasNext()).thenReturn(true, false);
		when(xMLEvent.getEventType()).thenReturn(XMLEvent.END_ELEMENT);
		when(xMLEvent.asEndElement()).thenReturn(endElement);
		when(endElement.getName()).thenReturn(qName);
		endTagResult = new EndTagResult(true, path);
		doReturn(endTagResult).when(inrXmlToIntermediateJson).processXmlEndTag(any(), any());
		inrXmlToIntermediateJson.generateHelper(null);

		// case 5
		when(xmlEventReader.hasNext()).thenReturn(true, false);
		when(xMLEvent.getEventType()).thenReturn(XMLEvent.END_DOCUMENT);
		inrXmlToIntermediateJson.generateHelper(null);
	}

	@Test
	public void processXmlStartTagTest() throws XMLStreamException {
		ReflectionTestUtils.setField(inrXmlToIntermediateJson, "inrXmlToJsonRuleMap", inrXmlToJsonRuleMap);
		ReflectionTestUtils.setField(inrXmlToIntermediateJson, "nodeMap", nodeMap);
		JsonPath path = JsonPath.getRootPath();
		when(inrXmlToJsonRuleMap.containsKey(any())).thenReturn(true);
		InrXmlToJsonRule inrXmlToJsonRule = new InrXmlToJsonRule();
		inrXmlToJsonRule.setFieldNullYn("Y");
		when(inrXmlToJsonRuleMap.get(any())).thenReturn(inrXmlToJsonRule);
		doReturn(path).when(inrXmlToIntermediateJson).generateHelper(any());
		doReturn(true).when(inrXmlToIntermediateJson).isNodeValid(any(), any());
		when(nodeMap.get(any())).thenReturn(objectNode);
		
		
		// case 1
		inrXmlToJsonRule.setJsonType(InrConstants.JSON_TYPE_OBJECT);
		inrXmlToIntermediateJson.processXmlStartTag(path);

		// case 2
		inrXmlToJsonRule.setJsonType(InrConstants.JSON_TYPE_ARRAY);
		when(objectNode.withArray(any())).thenReturn(arrayNode);
		inrXmlToIntermediateJson.processXmlStartTag(path);
	}

	@Test
	public void processXmlCharacterTest() {
		ReflectionTestUtils.setField(inrXmlToIntermediateJson, "inrXmlToJsonRuleMap", inrXmlToJsonRuleMap);
		ReflectionTestUtils.setField(inrXmlToIntermediateJson, "nodeMap", nodeMap);
		JsonPath path = JsonPath.getRootPath();
		when(xMLEvent.asCharacters()).thenReturn(characters);
		when(characters.getData()).thenReturn("data");
		when(inrXmlToJsonRuleMap.containsKey(any())).thenReturn(true);
		InrXmlToJsonRule inrXmlToJsonRule = new InrXmlToJsonRule();
		when(inrXmlToJsonRuleMap.get(any())).thenReturn(inrXmlToJsonRule);
		inrXmlToJsonRule.setFieldName("fieldName");
		inrXmlToJsonRule.setFieldParent("fieldParent");
		when(nodeMap.get(any())).thenReturn(objectNode);

		// case 1
		inrXmlToJsonRule.setJsonType(InrConstants.JSON_TYPE_FIELD_TAG + ", " + InrConstants.JSON_TYPE_FIELD_STR);
		inrXmlToJsonRule.setFieldNameForTag("fieldNameForTag");
		inrXmlToIntermediateJson.processXmlCharacter(path, xMLEvent, true);

		// case 2
		when(characters.getData()).thenReturn("1.0");
		inrXmlToJsonRule.setJsonType(InrConstants.JSON_TYPE_FIELD_DOUBLE);
		inrXmlToIntermediateJson.processXmlCharacter(path, xMLEvent, true);

		// case 3
		when(characters.getData()).thenReturn("1");
		inrXmlToJsonRule.setJsonType(InrConstants.JSON_TYPE_FIELD_INT);
		inrXmlToIntermediateJson.processXmlCharacter(path, xMLEvent, true);

		// case 4
		when(characters.getData()).thenReturn("1");
		inrXmlToJsonRule.setJsonType(InrConstants.JSON_TYPE_FIELD_LONG);
		inrXmlToIntermediateJson.processXmlCharacter(path, xMLEvent, true);
	}

	@Test
	public void processXmlEndTagTest() throws XMLStreamException {
		JsonPath tag = JsonPath.getRootPath().resolveContainerNode("tag");
		JsonPath endTag = JsonPath.getRootPath().resolveContainerNode("endTag");
		ReflectionTestUtils.setField(inrXmlToIntermediateJson, "inrXmlToJsonRuleMap", inrXmlToJsonRuleMap);
		ReflectionTestUtils.setField(inrXmlToIntermediateJson, "xmlEventReader", xmlEventReader);
		InrXmlToJsonRule inrXmlToJsonRule = new InrXmlToJsonRule();
		when(inrXmlToJsonRuleMap.get(any())).thenReturn(inrXmlToJsonRule);

		// case 1
		when(inrXmlToJsonRuleMap.containsKey(any())).thenReturn(true);
		inrXmlToJsonRule.setXmlEndTag("/endTag");
		inrXmlToJsonRule.setXmlStartTag("/endTag");
		assertTrue(inrXmlToIntermediateJson.processXmlEndTag(tag, endTag).isReturn());

		// case 2
		inrXmlToJsonRule.setXmlStartTag("/tag");
		assertTrue(inrXmlToIntermediateJson.processXmlEndTag(tag, endTag).isReturn());
		
		// case 3
		when(inrXmlToJsonRuleMap.containsKey(any())).thenReturn(false);
		assertFalse(inrXmlToIntermediateJson.processXmlEndTag(tag, endTag).isReturn());
	}

	@Test
	public void initializeRuleMapTest() throws XMLStreamException, SalesBusinessException {
		ReflectionTestUtils.setField(inrXmlToIntermediateJson, "xmlEventReader", xmlEventReader);
		when(xmlEventReader.hasNext()).thenReturn(true);
		when(xmlEventReader.peek()).thenReturn(xMLEvent);
		when(xMLEvent.getEventType()).thenReturn(XMLEvent.START_DOCUMENT, XMLEvent.START_ELEMENT);
		when(xMLEvent.asStartElement()).thenReturn(startElement);
		when(startElement.getName()).thenReturn(qName);
		inrXmlToIntermediateJson.initializeRuleMap();
	}

	@Test
	public void initializeRuleMapTestException() throws XMLStreamException, SalesBusinessException {
		ReflectionTestUtils.setField(inrXmlToIntermediateJson, "xmlEventReader", xmlEventReader);
		when(xmlEventReader.hasNext()).thenReturn(true);
		doThrow(XMLStreamException.class).when(xmlEventReader).peek();
		inrXmlToIntermediateJson.initializeRuleMap();
	}
}
