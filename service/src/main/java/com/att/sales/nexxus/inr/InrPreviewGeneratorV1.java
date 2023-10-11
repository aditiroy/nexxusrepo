package com.att.sales.nexxus.inr;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import com.att.sales.nexxus.util.UnmockableWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class InrPreviewGeneratorV1 {
	private int hash;
	private ArrayNode cdirDataArray;
	private String templatePath;
	private String p8dLocalPath;
	private PreviewWorkbookV1 wb;
	private UnmockableWrapper unmockableWrapper;
	private ObjectMapper mapper;
	
	private InrPreviewGeneratorV1(String templatePath, String p8dLocalPath, UnmockableWrapper unmockableWrapper,
			int hash, ObjectMapper mapper) {
		super();
		this.templatePath = templatePath;
		this.p8dLocalPath = p8dLocalPath;
		this.unmockableWrapper = unmockableWrapper;
		this.hash = hash;
		this.mapper = mapper;
		wb = new PreviewWorkbookV1(templatePath, p8dLocalPath, unmockableWrapper, hash);
	}
	
	public InrPreviewGeneratorV1(ArrayNode cdirDataArray, String templatePath, String p8dLocalPath,
			UnmockableWrapper unmockableWrapper, int hash, ObjectMapper mapper) {
		this(templatePath, p8dLocalPath, unmockableWrapper, hash, mapper);
		this.cdirDataArray = cdirDataArray;
	}
	
	public File generate() throws IOException {
		wb.init(cdirDataArray);
		for (JsonNode node : cdirDataArray) {
			for (JsonNode n : node.path("mainSheet")) {
				wb.processNode(n);
			}
			for (JsonNode n : node.path("falloutSheet")) {
				wb.processNode(n);
			}
			JsonNode treeView = node.path("treeView");
			processTreeView(treeView, wb);
			
		}
		return wb.write();
	}
	
	protected void processTreeView(JsonNode treeView, PreviewWorkbookV1 wb) {
		String rootTag = treeView.path("rootTag").asText();
		String excelWritePath = treeView.path("excelWritePath").asText();
		JsonPath rootPath = JsonPath.getRootPath();
		rootPath = rootPath.resolveContainerNode(rootTag);
		Map<JsonPath, Map<String, JsonNode>> rowMap = new HashMap<>();
		Map<String, JsonNode> sibling = new HashMap<>();
		sibling.put("rootTag", treeView.path("rootTag"));
		rowMap.put(JsonPath.getRootPath(), sibling);
		AtomicInteger counter = new AtomicInteger();
		
		traverseTreeView(rootPath, treeView.path("data"), excelWritePath, rowMap, counter, wb);
	}
	
	protected void traverseTreeView(JsonPath path, JsonNode node, String excelWritePath,
			Map<JsonPath, Map<String, JsonNode>> rowMap, AtomicInteger counter, PreviewWorkbookV1 wb) {
		if (node.getNodeType() == JsonNodeType.ARRAY) {
			for (JsonNode element : node) {
				traverseTreeView(path, element, excelWritePath, rowMap, counter, wb);
			}
		} else if (node.getNodeType() == JsonNodeType.OBJECT) {
			Map<String, JsonNode> sibling = new HashMap<>();
			rowMap.put(path, sibling);
			for (Iterator<Entry<String, JsonNode>> i = node.fields(); i.hasNext();) {
				Entry<String, JsonNode> entry = i.next();
				JsonNode childNode = entry.getValue();
				if (childNode.isContainerNode()) {
					continue;
				}
				String childName = entry.getKey();
				sibling.put(path.resolveField(childName), childNode);
			}
			for (Iterator<Entry<String, JsonNode>> i = node.fields(); i.hasNext();) {
				Entry<String, JsonNode> entry = i.next();
				JsonNode childNode = entry.getValue();
				if (!childNode.isContainerNode()) {
					continue;
				}
				String childName = entry.getKey();
				traverseTreeView(path.resolveContainerNode(childName), childNode, excelWritePath, rowMap, counter, wb);
			}
			if (excelWritePath.equals(path.toString())) {
				ObjectNode row = mapper.createObjectNode();
				rowMap.values().forEach(m -> m.forEach((k,v) -> row.set(k, v)));
				if (!InrJsonServiceImpl.OLD_CIRCUITS_NO_BILLING.equals(row.path(excelWritePath + "/nexxusFalloutReason").asText())
						&& !InrJsonServiceImpl.NOT_EPLSWAN_BITRATE.equals(row.path(excelWritePath + "/nexxusFalloutReason").asText())) {
					row.put("sequence", counter.incrementAndGet());
					wb.processNode(row);
				}
			}
			rowMap.remove(path);
		}
		
	}
}
