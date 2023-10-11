package com.att.sales.nexxus.util.xmlMerge;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
import org.apache.poi.util.ReplacingInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.constant.InrConstants;
/**
 * The Class XmlMergeUtil.
 *
 *  @author(ar896d) 
 * 
 * The XmlMergeUtility for merging of files
 * 
 *
 */

@Component
public class XmlMergeUtil {

	/** The Constant log. */
	private static final Logger log = LoggerFactory.getLogger(XmlMergeUtil.class);

	public void process(List<Path> pathList, List<String> expressionList, String outputFilepath) throws SalesBusinessException  {
		if (pathList != null && expressionList != null && outputFilepath != null) {
			 Document doc = merge(expressionList, pathList);
			 print(doc, outputFilepath);
			}
	}

	protected Document merge(List<String> expressionList, List<Path> path) throws SalesBusinessException {
		try {
			Document base;
			XPathFactory xPathFactory = XPathFactory.newInstance();
			XPath xpath = xPathFactory.newXPath();
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			docBuilderFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
			docBuilderFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
			docBuilderFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
			docBuilderFactory.setIgnoringElementContentWhitespace(true);
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			InputStream inputStream = Files.newInputStream(path.get(0));
	        base = docBuilder.parse(stringToInputStream(inputStreamToString(inputStream).replaceAll("&", "&amp;")));
//			base = docBuilder.parse(new ReplacingInputStream(inputStream, "&".getBytes(), "&amp;".getBytes()));
	        	for (int i = 1; i < path.size(); i++) {
	        		for (String expression : expressionList) {
	    				String parentRoot[] = expression.split("/");
	    				String parentObject = parentRoot[parentRoot.length - 1];
	    			XMLReader r = new XMLReader();
					r.addHandler(parentObject, new NodeHandler() {
						@Override
						public void process(StructuredNode node) {
							try {
								XPathExpression compiledExpression = xpath.compile(expression);
								Node results = (Node) compiledExpression.evaluate(base, XPathConstants.NODE);
								if (results == null) {
									  int lastIndex= expression.lastIndexOf('/'); 
									  String bodyPath = expression.substring(0,lastIndex); 
									  XPathExpression bodyExpression =xpath.compile(bodyPath); 
									  Node xmlBody = (Node) bodyExpression.evaluate(base,XPathConstants.NODE);
									  Element element = base.createElement(parentObject);
									  xmlBody.appendChild(element);
									  results = (Node) compiledExpression.evaluate(base, XPathConstants.NODE);
									}
								Node nextResults = node.queryNode("/" + parentObject);
								while (nextResults.hasChildNodes()) {
									Node kid = nextResults.getFirstChild();
									nextResults.removeChild(kid);
									kid = base.importNode(kid, true);
									results.appendChild(kid);
								}

							} catch (XPathExpressionException e) {
								e.printStackTrace();
							}
						}
					});

					InputStream inputStream1 = Files.newInputStream(path.get(i));
			        r.parse(stringToInputStream(inputStreamToString(inputStream1).replaceAll("&", "&amp;")));
				}
					 Files.deleteIfExists(path.get(i)); 
			}
				 Files.deleteIfExists(path.get(0)); 
			return base;
		} catch (IOException e) {
			log.error("General exception occur in merging the xml file ::"+e);
			log.error("General exception occur in merging the xml file :: "+e.getCause());
			throw new SalesBusinessException(InrConstants.XML_FILE_NOT_FOUND_EXCEPTION);

		}catch ( ParserConfigurationException | SAXException e) {
			log.error("General exception occur in merging the xml file ::"+e);
			log.error("General exception occur in merging the xml file :: "+e.getCause());
			throw new SalesBusinessException(InrConstants.XML_PARSE_EXCEPTION);
		}
	}

	protected void print(Document doc, String filePath) throws SalesBusinessException {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			transformerFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
			transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
			transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			DOMSource source = new DOMSource(doc);
			StreamResult file = new StreamResult(new File(filePath));
			transformer.transform(source, file);
			log.info("Finished writing the merge file :: {} ", filePath);
		} catch (TransformerException e) {
			log.error("Exception in saving the xmlmerge file ", e.getCause());
			throw new SalesBusinessException(InrConstants.XML_PARSE_EXCEPTION);

		}

	}
	
	 protected String inputStreamToString(InputStream inputStream) throws IOException {
	        StringWriter writer = new StringWriter();
	        IOUtils.copy(inputStream, writer, StandardCharsets.UTF_8);
	        return writer.toString();
	    }
	 
	 protected InputStream stringToInputStream(String s) {
		 return new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
	 }

}
