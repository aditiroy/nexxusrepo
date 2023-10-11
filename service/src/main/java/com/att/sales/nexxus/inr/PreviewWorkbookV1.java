package com.att.sales.nexxus.inr;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.att.sales.nexxus.util.UnmockableWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class PreviewWorkbookV1 {
	private static Logger log = LoggerFactory.getLogger(PreviewWorkbookV1.class);
	private String templatePath;
	private String p8dLocalPath;
	private Workbook wb;
	private List<PreviewSheetV1> sheets = new LinkedList<>();
	private UnmockableWrapper unmockableWrapper;
	private int hash;

	public PreviewWorkbookV1() {
		
	}
	
	public PreviewWorkbookV1(String templatePath, String p8dLocalPath, UnmockableWrapper unmockableWrapper, int hash) {
		super();
		this.templatePath = templatePath;
		this.p8dLocalPath = p8dLocalPath;
		this.unmockableWrapper = unmockableWrapper;
		this.hash = hash;
	}

	public void init(ArrayNode cdirDataArray) throws IOException {
		try (FileInputStream fis = unmockableWrapper.newFileInputStream(templatePath)) {
			wb = newXSSFWorkbook(fis);
		}

		// init sheets
		for (Iterator<Sheet> iterator = wb.sheetIterator(); iterator.hasNext();) {
			Sheet sheet = iterator.next();
			if(!"Instructions".equalsIgnoreCase(sheet.getSheetName())) {
				PreviewSheetV1 previewSheet = newPreviewSheetV1(sheet);
				previewSheet.init();
				sheets.add(previewSheet);
			}
		}

		// remove empty sheets
		Set<String> rootTags = new HashSet<>();
		for (JsonNode data : cdirDataArray) {
			JsonNode n = data.path("rootTag");
			for (JsonNode root : n) {
				rootTags.add(root.asText());
			}
		}
		List<Integer> sheetIndexToBeDeleted = new ArrayList<>();
		int i = 0;
		for (Iterator<PreviewSheetV1> iterator = sheets.iterator(); iterator.hasNext();) {
			PreviewSheetV1 previewSheet = iterator.next();
			if (!rootTags.contains(previewSheet.getRootTag())) {
				iterator.remove();
				sheetIndexToBeDeleted.add(i);
			}
			i++;
		}

		for (i = sheetIndexToBeDeleted.size() - 1; i >= 0; i--) {
			wb.removeSheetAt(sheetIndexToBeDeleted.get(i));
			// poi remove sheet bug workaround starts
			wb.createSheet();
			wb.setSheetHidden(wb.getNumberOfSheets() - 1, true);
			// poi remove sheet bug workaround ends
		}
		wb.setActiveSheet(0);
		wb.getSheetAt(0).setActiveCell(CellAddress.A1);
		for (JsonNode data : cdirDataArray) {
			for (JsonNode n : data.path("header")) {
				sheets.forEach(s -> s.populateHeader(n));
			}
		}
		useSXSSF();
	}
	
	protected Workbook newXSSFWorkbook(FileInputStream fis) throws IOException {
		return new XSSFWorkbook(fis);
	}
	
	protected PreviewSheetV1 newPreviewSheetV1(Sheet sheet) {
		return new PreviewSheetV1(sheet);
	}

	public void processNode(JsonNode node) {
		sheets.forEach(s -> s.processNode(node));
	}

	public File write() throws IOException {
		String outputFilePath = Paths.get(p8dLocalPath).resolve(FilenameUtils.getName(getFileName())).toString(); // NOSONAR
		try (OutputStream fileOut = unmockableWrapper.newFileOutputStream(outputFilePath)) {
			wb.write(fileOut);
		}
		wb.close();
		return new File(outputFilePath); // NOSONAR
	}

	protected void useSXSSF() {
		wb = newSXSSFWorkbook((XSSFWorkbook) wb, 100);
		sheets.forEach(s -> {
			s.setSheet(wb.getSheet(s.getSheet().getSheetName()));
		});
	}
	
	protected Workbook newSXSSFWorkbook(Workbook wb, int n) {
		return new SXSSFWorkbook((XSSFWorkbook) wb, n);
	}

	protected String getFileName() {
		return "previewInr_" + hash + "_" + System.currentTimeMillis() + templatePath.substring(templatePath.lastIndexOf("."));
	}
}
