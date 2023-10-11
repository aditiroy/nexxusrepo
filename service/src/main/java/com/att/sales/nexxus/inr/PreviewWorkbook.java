package com.att.sales.nexxus.inr;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.att.sales.nexxus.util.UnmockableWrapper;

/**
 * The Class PreviewWorkbook.
 */
public class PreviewWorkbook {
//	
//	/** The log. */
//	private static Logger log = LoggerFactory.getLogger(PreviewWorkbook.class);
//	
//	/** The template path. */
//	private String templatePath;
//	
//	/** The p 8 d local path. */
//	private String p8dLocalPath;
//	
//	/** The wb. */
//	private Workbook wb;
//	
//	/** The sheets. */
//	private List<PreviewSheet> sheets = new LinkedList<>();
//	
//	/** The unmockable wrapper. */
//	private UnmockableWrapper unmockableWrapper;
//	
//	/** The hash. */
//	private int hash;
//	
//	/**
//	 * Instantiates a new preview workbook.
//	 *
//	 * @param templatePath the template path
//	 * @param p8dLocalPath the p 8 d local path
//	 * @param unmockableWrapper the unmockable wrapper
//	 * @param hash the hash
//	 */
//	public PreviewWorkbook(String templatePath, String p8dLocalPath, UnmockableWrapper unmockableWrapper, int hash) {
//		super();
//		this.templatePath = templatePath;
//		this.p8dLocalPath = p8dLocalPath;
//		this.unmockableWrapper = unmockableWrapper;
//		this.hash = hash;
//	}
//
//	/**
//	 * Inits the.
//	 *
//	 * @throws IOException Signals that an I/O exception has occurred.
//	 */
//	public void init() throws IOException {
//		try (FileInputStream fis = unmockableWrapper.newFileInputStream(templatePath)) {
//			wb = new XSSFWorkbook(fis);
//		}
//		for (Iterator<Sheet> iterator = wb.sheetIterator(); iterator.hasNext();) {
//			Sheet sheet = iterator.next();
//			PreviewSheet previewSheet = new PreviewSheet(sheet);
//			previewSheet.init();
//			sheets.add(previewSheet);
//		}
//	}
//	
//	/**
//	 * Start container node.
//	 *
//	 * @param path the path
//	 */
//	public void startContainerNode(JsonPath path) {
//		sheets.forEach(s -> s.startContainerNode(path));
//	}
//	
//	/**
//	 * Value node.
//	 *
//	 * @param data the data
//	 * @param path the path
//	 */
//	public void valueNode(String data, JsonPath path) {
//		sheets.forEach(s -> s.valueNode(data, path));
//	}
//	
//	/**
//	 * End container node.
//	 *
//	 * @param path the path
//	 */
//	public void endContainerNode(JsonPath path) {
//		sheets.forEach(s -> s.endContainerNode(path));
//	}
//
//	/**
//	 * Write.
//	 *
//	 * @return the file
//	 * @throws IOException Signals that an I/O exception has occurred.
//	 */
//	public File write() throws IOException {
//		List<Integer> sheetIndexToBeDeleted = new ArrayList<>();
//		int i = 0;
//		for (Iterator<PreviewSheet> iterator = sheets.iterator();iterator.hasNext();) {
//			PreviewSheet previewSheet = iterator.next();
//			if (previewSheet.isEmpty()) {
//				iterator.remove();
//				sheetIndexToBeDeleted.add(i);
//			}
//			i++;
//		}
//		
//		for (i = sheetIndexToBeDeleted.size() - 1; i >= 0; i--) {
//			wb.removeSheetAt(sheetIndexToBeDeleted.get(i));
//			//poi remove sheet bug workaround starts
//			wb.createSheet();
//			wb.setSheetHidden(wb.getNumberOfSheets() - 1, true);
//			//poi remove sheet bug workaround ends
//		}
//		
//		wb.setActiveSheet(0);
//		wb.getSheetAt(0).setActiveCell(CellAddress.A1);
//		useSXSSF();
//		sheets.forEach(PreviewSheet::write);
//		String outputFilePath = Paths.get(p8dLocalPath).resolve(FilenameUtils.getName(getFileName())).toString(); //NOSONAR
//		try (OutputStream fileOut = unmockableWrapper.newFileOutputStream(outputFilePath)) {
//			wb.write(fileOut);
//		}
//		wb.close();
//		return new File(outputFilePath); //NOSONAR
//	}
//	
//	/**
//	 * Use SXSSF.
//	 */
//	protected void useSXSSF() {
//		wb = new SXSSFWorkbook((XSSFWorkbook) wb, 100);
//		sheets.forEach(s -> s.setSheet(wb.getSheet(s.getSheet().getSheetName())));
//	}
//	
//	/**
//	 * Gets the file name.
//	 *
//	 * @return the file name
//	 */
//	protected String getFileName() {
//		return "previewInr_" + hash + "_" + System.currentTimeMillis() + ".xlsx";
//	}
}
