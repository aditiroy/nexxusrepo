package com.att.sales.nexxus.inr;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import com.att.sales.nexxus.util.UnmockableWrapper;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * The Class InrPreviewGenerator.
 */
public class InrPreviewGenerator {
//	
//	/** The hash. */
//	private int hash;
//	
//	/** The nodes. */
//	private List<JsonNode> nodes = new LinkedList<>();
//	
//	/** The template path. */
//	private String templatePath;
//	
//	/** The p 8 d local path. */
//	private String p8dLocalPath;
//	
//	/** The wb. */
//	private PreviewWorkbook wb;
//	
//	/** The unmockable wrapper. */
//	private UnmockableWrapper unmockableWrapper;
//
//	/**
//	 * Instantiates a new inr preview generator.
//	 *
//	 * @param templatePath the template path
//	 * @param p8dLocalPath the p 8 d local path
//	 * @param unmockableWrapper the unmockable wrapper
//	 * @param hash the hash
//	 */
//	private InrPreviewGenerator(String templatePath, String p8dLocalPath, UnmockableWrapper unmockableWrapper,
//			int hash) {
//		super();
//		this.templatePath = templatePath;
//		this.p8dLocalPath = p8dLocalPath;
//		this.unmockableWrapper = unmockableWrapper;
//		this.hash = hash;
//		wb = new PreviewWorkbook(templatePath, p8dLocalPath, unmockableWrapper, hash);
//	}
//
//	/**
//	 * Instantiates a new inr preview generator.
//	 *
//	 * @param nodes the nodes
//	 * @param templatePath the template path
//	 * @param p8dLocalPath the p 8 d local path
//	 * @param unmockableWrapper the unmockable wrapper
//	 * @param hash the hash
//	 */
//	public InrPreviewGenerator(List<JsonNode> nodes, String templatePath, String p8dLocalPath,
//			UnmockableWrapper unmockableWrapper, int hash) {
//		this(templatePath, p8dLocalPath, unmockableWrapper, hash);
//		this.nodes.addAll(nodes);
//	}
//
//	/**
//	 * Instantiates a new inr preview generator.
//	 *
//	 * @param node the node
//	 * @param templatePath the template path
//	 * @param p8dLocalPath the p 8 d local path
//	 * @param unmockableWrapper the unmockable wrapper
//	 * @param hash the hash
//	 */
//	public InrPreviewGenerator(JsonNode node, String templatePath, String p8dLocalPath,
//			UnmockableWrapper unmockableWrapper, int hash) {
//		this(templatePath, p8dLocalPath, unmockableWrapper, hash);
//		this.nodes.add(node);
//	}
//
//	/**
//	 * Generate.
//	 *
//	 * @return the file
//	 * @throws IOException Signals that an I/O exception has occurred.
//	 */
//	public File generate() throws IOException {
//		wb.init();
//		convertJsonToRowData();
//		return wb.write();
//	}
//
//	/**
//	 * Convert json to row data.
//	 */
//	protected void convertJsonToRowData() {
//		for (JsonNode node : nodes) {
//			JsonPath rootPath = JsonPath.getRootPath();
//			convertJsonToRowDataHelper(node, rootPath);
//		}
//	}
//
//	/**
//	 * Convert json to row data helper.
//	 *
//	 * @param node the node
//	 * @param path the path
//	 */
//	protected void convertJsonToRowDataHelper(JsonNode node, JsonPath path) {
//		switch (node.getNodeType()) {
//		case ARRAY:
//			processArrayNode(node, path);
//			break;
//		case OBJECT:
//			processObjectNode(node, path);
//			break;
//		default:
//			processNonContainerNode(node, path);
//			break;
//		}
//	}
//
//	/**
//	 * Process array node.
//	 *
//	 * @param node the node
//	 * @param path the path
//	 */
//	protected void processArrayNode(JsonNode node, JsonPath path) {
//		for (JsonNode arrayElement : node) {
//			this.convertJsonToRowDataHelper(arrayElement, path);
//		}
//	}
//
//	/**
//	 * Process object node.
//	 *
//	 * @param node the node
//	 * @param path the path
//	 */
//	protected void processObjectNode(JsonNode node, JsonPath path) {
//		wb.startContainerNode(path);
//		for (Iterator<Entry<String, JsonNode>> i = node.fields(); i.hasNext();) {
//			Entry<String, JsonNode> entry = i.next();
//			JsonNode childNode = entry.getValue();
//			if (childNode.isContainerNode()) {
//				continue;
//			}
//			String childName = entry.getKey();
//			this.convertJsonToRowDataHelper(childNode, path.resolveContainerNode(childName));
//		}
//		for (Iterator<Entry<String, JsonNode>> i = node.fields(); i.hasNext();) {
//			Entry<String, JsonNode> entry = i.next();
//			JsonNode childNode = entry.getValue();
//			if (!childNode.isContainerNode()) {
//				continue;
//			}
//			String childName = entry.getKey();
//			this.convertJsonToRowDataHelper(childNode, path.resolveContainerNode(childName));
//		}
//		wb.endContainerNode(path);
//	}
//
//	/**
//	 * Process non container node.
//	 *
//	 * @param node the node
//	 * @param path the path
//	 */
//	protected void processNonContainerNode(JsonNode node, JsonPath path) {
//		if (!node.isNull() && !node.asText().isEmpty()) {
//			wb.valueNode(node.asText(), path);
//		}
//	}
}
